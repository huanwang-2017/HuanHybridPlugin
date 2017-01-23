package com.huan.hhp.utils;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

/**
 * Created by Administrator on 2016/4/28.
 * 缩放工具
 * 主要用途：主要提供了View的缩放和倒影的缩放。
 *
 * 需要改善，最好使用 装饰者模式
 */
public class ScaleUtil {
    private static ScaleUtil instance;
    public static ScaleUtil get(float ratio, int duration){
        if(instance == null)
            instance=new ScaleUtil();
        instance.setRatio(ratio);
        instance.setDuration(duration);
        return instance;
    }

    /** 动画回调接口，移动完成后会调用。 **/
    public interface Callback {
        void begin();
        void end();
    }

    /** 监听 **/
    private static class SListener implements Animation.AnimationListener {
        private Callback mCallback;
        private final Callback DEFAULT_CALLBACK = new Callback(){
            @Override
            public void begin() {

            }

            @Override
            public void end() {

            }
        };
        SListener setCallback(Callback callback){
            this.mCallback = callback!=null ? callback:DEFAULT_CALLBACK;
            return this;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            mCallback.begin();
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mCallback.end();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private int duration; // 一次执行需要的时间
    private float ratio;  // 缩放比例
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private static final SListener ANIMATION_LISTENER = new SListener();

    /**
     * 设置动画时长
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 设置比例
     * @param ratio
     */
    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    /**
     * 设置动画拦截器
     * @param interpolator
     */
    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    /**
     * 放大
     * @param view
     * @param callback
     */
    public void scale(View view, Callback callback){
        ScaleAnimation animation = new ScaleAnimation(1.0f, ratio, 1.0f, ratio, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setInterpolator(mInterpolator);
        animation.setDuration(duration);
        animation.setAnimationListener(ANIMATION_LISTENER.setCallback(callback));
        view.startAnimation(animation);
    }

    /**
     * 缩小
     * @param view
     * @param callback
     */
    public void shrink(View view, Callback callback){
        view.clearAnimation();
        ScaleAnimation animation = new ScaleAnimation(ratio, 1.0f, ratio, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(false);
        animation.setInterpolator(mInterpolator);
        animation.setDuration(duration);
        animation.setAnimationListener(ANIMATION_LISTENER.setCallback(callback));
        view.startAnimation(animation);
    }

    ////////////////

    /**
     * 放大倒影
     * @param view
     * @param callback
     */
    public void scaleReflect(View view, Callback callback){
        view.clearAnimation();
        ScaleAnimation animation = new ScaleAnimation(1.0f, ratio, 1.0f, ratio, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, -0.5f);
        animation.setFillAfter(true);
        animation.setInterpolator(mInterpolator);
        animation.setDuration(duration);
        animation.setAnimationListener(ANIMATION_LISTENER.setCallback(callback));
        view.startAnimation(animation);
    }

    /**
     * 缩小倒影
     * @param view
     * @param callback
     */
    public void shrinkReflect(View view, Callback callback){
        view.clearAnimation();
        ScaleAnimation animation = new ScaleAnimation(ratio, 1.0f, ratio, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, -0.5f);
        animation.setFillAfter(false);
        animation.setInterpolator(mInterpolator);
        animation.setDuration(duration);
        animation.setAnimationListener(ANIMATION_LISTENER.setCallback(callback));
        view.startAnimation(animation);
    }
}
