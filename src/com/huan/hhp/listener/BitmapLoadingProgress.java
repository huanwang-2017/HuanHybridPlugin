package com.huan.hhp.listener;

import android.content.Context;
import android.view.View;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

/**
 * Created by tjy on 2016/11/20 0020.
 * 图片下载的监听器
 */
public abstract class BitmapLoadingProgress implements ImageLoadingProgressListener {


    @Override
    public abstract void onProgressUpdate(String s, View view, int i, int i1) ;




    private Context mContext;
    private View contentView;
    public BitmapLoadingProgress(Context context) {
        mContext = context;
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public Context getContext() {
        return mContext;
    }
}
