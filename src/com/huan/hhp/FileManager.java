package com.huan.hhp;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import com.huan.hhp.app.ActivityInfo;
import com.huan.hhp.app.PluginInfo;
import com.huan.hhp.app.ResourceManager;
import com.huan.hhp.utils.HttpUrlUtil;
import com.huan.hhp.xmlParser.HLayoutInflater;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by tjy on 2016/11/5.
 */
public class FileManager {

    final String TAG = FileManager.class.getSimpleName();

    public interface Callback {
        void onSuccess(View view);
    }

    HLayoutInflater l2p;
    private ResourceManager mResourceManager;


    public FileManager(Context context) {
        l2p = new HLayoutInflater(context);
        mResourceManager = new ResourceManager();
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void loadFile(final PluginInfo pluginInfo, final ActivityInfo activityInfo, final Callback callback){
        Log.i(TAG, "load file:"+this);
        if(activityInfo.getLayout().getValue().contains("local:")){

            // 校正地址
//            String realPath = getRealPath(activityInfo.getLayout().getValue());
//            activityInfo.getLayout().setValue(realPath.replace("local:", ""));

            try {
                File f = new File(pluginInfo.getWorkspace(), activityInfo.getLayout().getValue().replace("local:", ""));
                Log.i(TAG, "从workspace加载xml " + f.getAbsolutePath() + "      " + f.exists());
                InputStream inputStream = new FileInputStream(f);
                try {
                    View view = l2p.inflater(inputStream, pluginInfo);
                    callback.onSuccess(view);
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        InputStream inputStream = mResourceManager.manage(pluginInfo).resource(activityInfo.getLayout()).get();
        if(inputStream != null){
            Log.i(TAG, "从本地加载xml");
            try {
                View view = l2p.inflater(inputStream, pluginInfo);
                callback.onSuccess(view);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "从网络加载xml "+activityInfo.getLayout().getValue());
        new AsyncTask<Void, Void, View>(){
            @Override
            protected View doInBackground(Void... params) {
                try {
                    URL url = new URL(HttpUrlUtil.getHttpUrl(pluginInfo, activityInfo.getLayout().getValue()));
                    View view = l2p.inflater(url, pluginInfo);
                    mResourceManager.manage(pluginInfo).resource(activityInfo.getLayout()).set(l2p.getXML());
                    return view;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(View view) {
                if(view != null){
                    callback.onSuccess(view);
                }
            }
        }.execute();
    }

    public void release(){
        mResourceManager.release();
        mResourceManager = null;
    }

    /**
     * android File 对 ../ 解析bug导致无法找到文件，所以针对../ 必须主动处理。
     * @param absPath
     * @return
     */
    public static String getRealPath(String absPath){
        String s = "/../";
        int i = absPath.indexOf(s);
        if(i != -1) {
            String v1 = absPath.substring(0, i);
            String v2 = absPath.substring(i + s.length());
            System.out.println("v1="+v1);
            System.out.println("v2="+v2);
            return getRealPath(v1.substring(0, v1.lastIndexOf("/"))+ "/"+ v2);
        }
        return absPath;
    }
}
