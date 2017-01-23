package com.huan.hhp.source;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import com.huan.hhp.app.PluginInfo;
import com.huan.hhp.utils.ErrorUtil;
import com.huan.hhp.xmlParser.ValuesXmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by tjy on 2016/10/28.
 */
public class ReSourceUtil {
    static final String TAG = ReSourceUtil.class.getSimpleName();
    private Context context;

    File drawableGetter;
    private ValuesXmlPullParser dimensGetter; // dimens获取工具
    private ValuesXmlPullParser valuesGetter; // string获取工具

    public ReSourceUtil(Context context) {
        this.context = context;
    }

    public void setPluginInfo(PluginInfo pluginInfo) {
        if(!"{?}".equals(pluginInfo.getFile().getValue())) {
            drawableGetter = getDMS_File(pluginInfo.getWorkspace().getAbsolutePath(), "drawable");
            File file = null;
            try {
                dimensGetter = new ValuesXmlPullParser(file=new File(getDMS_File(pluginInfo.getWorkspace().getAbsolutePath(), "values"), "/dimens.xml"));
            } catch (IOException e) {
                Log.i(TAG, "找不到文件"+file);
            } catch (XmlPullParserException e) {
                Log.i(TAG, "无法解析"+file);
            }
            try {
                valuesGetter = new ValuesXmlPullParser(new File(pluginInfo.getWorkspace(), "res/values/strings.xml"));
            } catch (IOException e) {
                Log.i(TAG, "找不到文件"+file);
            } catch (XmlPullParserException e) {
                Log.i(TAG, "无法解析"+file);
            }
        }
    }

    public String getValue(String name){
        return valuesGetter.getString(name);
    }

    public String getDimens(String name){
        return dimensGetter.getString(name);
    }

    /**
     * 获取图片
     * @param resName
     * @return
     */
    public Bitmap getDrawable(String resName){
        return getBitmap(resName);
    }

    /**
     *
     * @param rootFile 根目录
     * @param name values/drawable
     * @return
     */
    public File getDMS_File(String rootFile, String name){
        /*
            drawable-ldpi       120DPI
            drawable-mdpi        160DPI
            drawable-hdpi        240DPI
            drawable-xhdpi       320DPI
            drawalbe-xxhdpi     480DPI
            drawable-xxxhdpi    640DPI
         */
        File resFile = new File(rootFile, "res");
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = (int) (displayMetrics.widthPixels*displayMetrics.density);
        int height = (int) (displayMetrics.heightPixels*displayMetrics.density);
        String abD = name+"-"+width+"x"+height;
        Log.i(TAG, "dst file name is "+abD);
        File abF = new File(resFile, abD);
        if(abF.exists())
            return abF;
        abD = name+"-w"+width+"dp";
        Log.i(TAG, "name is "+abD);
        abF = new File(resFile, abD);
        if(abF.exists())
            return abF;

        if(displayMetrics.densityDpi >= 120)
            abD = name+"-ldpi";
        if(displayMetrics.densityDpi >= 160)
            abD = "-mdpi";
        if(displayMetrics.densityDpi >= 240)
            abD = name+"drawable-hdpi";
        if(displayMetrics.densityDpi >= 320)
            abD = name+"-xhdpi";
        if(displayMetrics.densityDpi >= 480)
            abD = name+"-xxhdpi";
        if(displayMetrics.densityDpi >= 640)
            abD = name+"-xxxhdpi";
        Log.i(TAG, "name is "+abD);
        abF = new File(resFile, abD);
        if(abF.exists())
            return abF;
        Log.i(TAG, "name is "+name);
        return new File(resFile, name); // 如：drawable/values
    }

    /**
     * 获取图片
     * @param name
     * @return
     */
    public Bitmap getBitmap(String name){
        if(drawableGetter != null) {
            File file = new File(drawableGetter, name);
            if (file.exists()) {
                try {
                    return BitmapFactory.decodeStream(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "获取bitmap失败：" + ErrorUtil.e(e));
                }
            }
        }
        return null;
    }
}
