package com.huan.hhp.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import com.huan.hhp.utils.ImageOptions;
import com.huan.hhp.utils.ReflexUtil;
import com.huan.hhp.utils.StaticReflexUtil;
import com.huan.hhp.widget.typeof.TypeOf;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by tjy on 2016/10/20.
 */
public class ImageViewMapping extends ViewMapping {
    {
        mapping("src", new TypeOf("this.src", String.class));
        mapping("scaleType", new TypeOf("this.setScaleType", String.class));
    }

    public ImageViewMapping(Context context, String name) {
        super(context, name);
        mRunnable = new MRunnable();
    }

    private class MRunnable implements Runnable {
        Bitmap bitmap;
        @Override
        public void run() {
            ((ImageView)mView).setImageBitmap(bitmap);
        }
    }

    private MRunnable mRunnable;

    /**
     * 设置前景
     * @param uri
     */
    @JavascriptInterface
    public void src(String uri){
        Log.i(TAG, "src=" + uri);
        if(uri.substring(0, 1).equals("#")){
            // 背景色
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(uri));
            ((ImageView)mView).setImageDrawable(colorDrawable);
        }
        else {
            ImageLoader.getInstance().displayImage(uri, ((ImageView)mView), ImageOptions.getImageOptions(0), new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
                    mRunnable.bitmap = loadedImage;
                    mView.post(mRunnable);
                }
            });
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
                ReflexUtil.execute(mView, "setScaleType", new Class[]{ImageView.ScaleType.class}, new Object[]{result});
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().cancelDisplayTask((ImageView) mView);
        mRunnable = null;
    }
}
