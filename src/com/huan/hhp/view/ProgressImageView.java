package com.huan.hhp.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by tjy on 2016/11/20 0020.
 */
public class ProgressImageView extends ViewGroup {
    private ImageView mImageView;
    public ProgressImageView(Context context) {
        super(context);
        mImageView = new ImageView(context);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addView(mImageView, layoutParams);
    }

    public ImageView getImageView() {
        return mImageView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LayoutParams lp = getLayoutParams();
        Log.i("HwImageView", "onMeasure..."+lp.width+" - "+lp.height);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
        int count = getChildCount();
        int i = 0;
        View child = null;
        for(; i < count; i++){
            child = getChildAt(i);
            child.getLayoutParams().width = lp.width;
            child.getLayoutParams().height = lp.height;
            super.measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i("HwImageView", "onLoad "+getChildCount());
        int count = getChildCount();
        int i = 0;
        View child = null;
        for(; i < count; i++){
            child = getChildAt(i);
            child.layout(0, 0, child.getLayoutParams().width, child.getLayoutParams().height);
        }
    }
}
