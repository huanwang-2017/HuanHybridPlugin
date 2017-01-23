package com.huan.hhp.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import com.huan.hhp.listener.BitmapLoadingProgress;
import com.huan.hhp.utils.ImageOptions;
import com.huan.hhp.utils.ReflexUtil;
import com.huan.hhp.utils.StaticReflexUtil;
import com.huan.hhp.view.ProgressImageView;
import com.huan.hhp.widget.typeof.TypeOf;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by Administrator on 2016/10/20.
 */
public class HwImageViewMapping extends ViewGroupMapping {
    {
        forTag(ProgressImageView.class.getName());
        mapping("src", new TypeOf("this.src", String.class));
        mapping("scaleType", new TypeOf("this.setScaleType", String.class));
        mapping("srcProgress", new TypeOf("this.setSrcProgressListener", String.class));
    }

    /**
     * 前景的进度监听，用户可以自定义。
     */
    private BitmapLoadingProgress srcBitmapLoadingProgress;

    public HwImageViewMapping(Context context, String name) {
        super(context, name);
    }

    private class MRunnable implements Runnable {
        protected String uri;

        @Override
        public void run() {

        }
    }
    private MRunnable imgDrawingEngineer = new MRunnable() {
        @Override
        public void run() {
            ImageView imageView = ((ProgressImageView)mView).getImageView();
            ImageLoader.getInstance().displayImage(this.uri, imageView, ImageOptions.getImageOptions(0), srcBitmapLoadingProgress==null?null:new SimpleImageLoadingListener(){
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    ProgressImageView m = (ProgressImageView)mView;
                    m.removeView(srcBitmapLoadingProgress.getContentView());
                    m.addView(srcBitmapLoadingProgress.getContentView());
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ((ViewGroup)mView).removeView(srcBitmapLoadingProgress.getContentView());
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ((ViewGroup)mView).removeView(srcBitmapLoadingProgress.getContentView());
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    ((ViewGroup)mView).removeView(srcBitmapLoadingProgress.getContentView());
                }
            }, srcBitmapLoadingProgress);
        }
    };

    /**
     * 设置前景
     * @param uri
     * @see MRunnable
     * @see #imgDrawingEngineer
     */
    @JavascriptInterface
    public void src(String uri){
        Log.i(TAG, "src=" + uri);
        if(uri.substring(0, 1).equals("#")){
            // 背景色
            final ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(uri));
            ImageView imageView = ((ProgressImageView)mView).getImageView();
            imageView.setImageDrawable(colorDrawable);
        }
        else {
            imgDrawingEngineer.uri = uri;
            mHandler.post(imgDrawingEngineer);
        }
    }

    @JavascriptInterface
    public void setScaleType(String scaleType){
        String split = "fit";
        if(scaleType.contains(split)){
            String temp = scaleType.substring(0, split.length())+"_"+scaleType.substring(split.length());
            temp = temp.toUpperCase();
            Log.i(TAG, "scaleType="+temp);
            Object result = StaticReflexUtil.get(ImageView.ScaleType.class, scaleType.toUpperCase());
            if(result != null){
                ImageView imageView = ((ProgressImageView)mView).getImageView();
                ReflexUtil.execute(imageView, "setScaleType", new Class[]{ImageView.ScaleType.class}, new Object[]{result});
            }
        }
    }

    /**
     * 设置下载进度监听
     * @param progressClass
     */
    public void setSrcProgressListener(String progressClass){
        try {
            srcBitmapLoadingProgress = (BitmapLoadingProgress) Class.forName(progressClass).getConstructor(Context.class).newInstance(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imgDrawingEngineer = null;
    }
}
