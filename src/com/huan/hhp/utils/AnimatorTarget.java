package com.huan.hhp.utils;

import android.view.View;
import android.view.ViewGroup;

/**
 * 属性动画工具类
 * 主要用途：和ObjectAnimator 配合使用。
 */
public class AnimatorTarget {

	public AnimatorTarget() {

	}

	public AnimatorTarget(AnimatorTarget target) {
		this.x = target.x;
		this.y = target.y;
		this.width = target.width;
		this.height = target.height;
		this.alpha = target.alpha;
	}

	protected float x;
	protected float y;
	protected float alpha;
	protected float width;
	protected float height;
	protected View target;

	protected boolean canceled;

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
		ViewGroup.LayoutParams lp = target.getLayoutParams();
		lp.width = (int)this.width;
		target.setLayoutParams(lp);
		set();
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
		ViewGroup.LayoutParams lp = target.getLayoutParams();
		lp.height = (int)this.height;
		target.setLayoutParams(lp);
		set();
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
		set();
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
		set();
	}

	public View getTarget() {
		return target;
	}

	public void setTarget(View target) {
		if (target == null || this.target == target)
			return;
		this.target = target;
		width = target.getLayoutParams().width;
		height = target.getLayoutParams().height;
		x = target.getX();
		y = target.getY();
	}

	public void replaceTarget(View target){
		target.setX(this.target.getX());
		target.setY(this.target.getY());
		this.target = target;
		set();
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
		target.setAlpha(alpha);
	}

	public void set() {
		if(canceled)
			return;
		int x = (int)this.x;
		int y = (int)this.y;
		int w = (int)this.width;
		int h = (int)this.height;
		target.layout(x, y, x+w, y+h);
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

}
