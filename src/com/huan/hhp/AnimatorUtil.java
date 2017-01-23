package com.huan.hhp;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import com.huan.hhp.utils.AnimListener;
import com.huan.hhp.utils.AnimatorTarget;
import org.mozilla.javascript.ScriptableObject;

/**
 * 动画工具类
 * 主要用途：主要用在物体位移。
 */
public class AnimatorUtil implements Runnable{
//	private static ScrollerAnimatorUtil ins;
	private boolean alive = false;
	private Scroller scrollerXY;
	private Scroller scrollerWH;
	
	private AnimatorTarget mTarget;	// 动画代理
	private AnimatorListener listener;

	private Handler mHandler = new Handler();

	public AnimatorUtil(Context mContext) {
		Interpolator interpolator = new LinearInterpolator();
		scrollerXY = new Scroller(mContext, interpolator);
		scrollerWH = new Scroller(mContext, interpolator);
	}

	public AnimatorUtil(View targetView, Context mContext){
		this(mContext);
		mTarget = new AnimatorTarget();
		mTarget.setTarget(targetView);
	}

	public AnimatorUtil(Context mContext, Interpolator interpolator){
		scrollerXY = new Scroller(mContext, interpolator);
		scrollerWH = new Scroller(mContext, interpolator);
	}

	public AnimatorUtil(View targetView, Context mContext, Interpolator interpolator){
		mTarget = new AnimatorTarget();
		mTarget.setTarget(targetView);
		scrollerXY = new Scroller(mContext, interpolator);
		scrollerWH = new Scroller(mContext, interpolator);
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void run() {
		mHandler.removeCallbacks(this);
		if(!alive)
			return;
		if(scrollerXY.isFinished() && scrollerWH.isFinished()){
			mTarget.set();
			if(listener != null){
				listener.onAnimationEnd(null);
			}
			alive = false;
			return;
		}
		if(scrollerXY.computeScrollOffset()){
			int x = scrollerXY.getCurrX();
			int y = scrollerXY.getCurrY();
			mTarget.setX(x);
			mTarget.setY(y);
			mHandler.post(this);
		}
		if(scrollerWH.computeScrollOffset()){
			int w = scrollerWH.getCurrX();
			int h = scrollerWH.getCurrY();
			mTarget.setWidth(w);
			mTarget.setHeight(h);
			mHandler.post(this);
		}
	}

	public void animate(int x, int y, int w, int h, int duration){
		animate(x, y, w, h, duration, null);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void animate(int x, int y, int w, int h, int duration, final ScriptableObject callback){
		if(callback != null){
			this.setAnimationListener(new AnimListener(){
				@Override
				public void onAnimationStart(Animator animation) {
					H.callMethod(callback, "begin", new Object[]{});
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					H.callMethod(callback, "end", new Object[]{});
				}
			});
		}
		if(duration <= 0){
			layout(x, y, w, h);
			return;
		}

		if(listener != null){
			listener.onAnimationStart(null);
		}
		scrollTo(scrollerXY, x, y, duration);
		scrollTo(scrollerWH, w, h, duration);
//		if(ins != null){
//			ins.alive = false;
//		}
		this.alive = true;
//		ins = this;
		mHandler.post(this);
	}

	public void layoutNotDraw(int x, int y, int w, int h){
		scrollerXY.setFinalX(x);
		scrollerXY.setFinalY(y);
		scrollerXY.abortAnimation();
		scrollerWH.setFinalX(w);
		scrollerWH.setFinalY(h);
		scrollerWH.abortAnimation();
		scrollerXY.computeScrollOffset();
		scrollerWH.computeScrollOffset();
	}
	
	public void layout(int x, int y, int w, int h){
		layoutNotDraw(x, y, w, h);
		mTarget.setX(x);
		mTarget.setY(y);
		mTarget.setWidth(w);
		mTarget.setHeight(h);
		Log.i("animation", "layout:x="+x+", y="+y);
	}
	
	protected void scrollTo(Scroller scroller, int xto, int yto, int duration) {
		scroller.startScroll(scroller.getCurrX(), scroller.getCurrY(), 0, 0, duration);
		scroller.setFinalX(xto);
		scroller.setFinalY(yto);
	}
	
	public void setAnimationListener(AnimatorListener listener){
		this.listener = listener;
	}

	public AnimatorListener getListener() {
		return listener;
	}

	public AnimatorTarget getTarget() {
		return mTarget;
	}

	public void cancel(){
		alive = false;
		mHandler.removeCallbacks(this);
	}

	public void setTarget(AnimatorTarget target) {
		this.mTarget = target;
	}
}
