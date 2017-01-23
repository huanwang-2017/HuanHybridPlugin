package com.huan.hhp.widget;

import android.content.Context;
import android.util.Log;
import android.view.View;
import com.huan.hhp.FileManager;
import com.huan.hhp.app.*;
import com.huan.hhp.net.HttpTask;
import com.huan.hhp.utils.FileUtil;
import com.huan.hhp.utils.HttpUrlUtil;
import com.huan.hhp.widget.typeof.TypeOf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2016/11/9.
 */
public class ScriptMapping extends ViewMapping {
    {
        forTag(HwMappings.PKG + ".view.Script");
        mapping("src", new TypeOf("this.loadJs", String.class));
    }

    private ResourceManager mResourceManager;
    private Resource resource;
    private boolean created;
    private String lazyReqestJsUrl; // 延迟执行的请求地址

    public ScriptMapping(Context context, String name) {
        super(context, name);
        mResourceManager = new ResourceManager();
    }

    @Override
    public void setView(View view) {
        super.setView(view);
        mView.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        Log.i("ScriptMapping", "xmlValue="+getXmlValue());
        if(getSilentlyJsChannel() != null && getXmlValue() != null) {
            getSilentlyJsChannel().loadJs(getXmlValue());
            getSilentlyJsChannel().exFunction("onReady");
        }
        created = true;
        if(this.lazyReqestJsUrl != null){
            loadJs(this.lazyReqestJsUrl);
            this.lazyReqestJsUrl = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        created = false;
    }

    @Override
    public void setXmlValue(String xmlValue) {
        if(xmlValue != null) {
            super.setXmlValue(xmlValue);
        }
    }

    /**
     * 加载网络js
     * @param jsUrl
     */
    public void loadJs(String jsUrl){
        if(!created){
            this.lazyReqestJsUrl = jsUrl;
            return;
        }
        App_config app_config = App_configManager.getApp_config(getPackage());
        final PluginInfo pluginInfo = app_config.getPluginManager().getCurrentPlugInfo();
        Log.i(TAG, "loadJs:"+jsUrl);
        if(Resource.isEgg(jsUrl)){
            resource = app_config.getResource(Resource.getYolk(jsUrl));
            if(resource != null){
                FileInputStream inputStream = mResourceManager.manage(pluginInfo).resource(resource).get();
                if(inputStream != null) {
                    String arg = FileUtil.getFileContent(inputStream);
                    Log.i(TAG, "从本地加载了js");
                    this.setXmlValue(arg);
                    return;
                }
                jsUrl = HttpUrlUtil.getHttpUrl(pluginInfo, resource.getValue());
            }
        }
        else{
            if(jsUrl.substring(0, 1).equals("/")){
                jsUrl = HttpUrlUtil.getHttpUrl(pluginInfo, jsUrl);
            }
            else {
                ActivityInfo activityInfo = pluginInfo.getCurrentActivity();
                jsUrl = activityInfo.getLayout().getValue() + "/../" + jsUrl;
                jsUrl = FileManager.getRealPath(jsUrl);
            }
        }

        if(jsUrl.contains("local:")){
            Log.i(TAG, "从workspace加载js");
            try {
                FileInputStream fileInputStream = new FileInputStream(new File(pluginInfo.getWorkspace(), jsUrl.replace("local:", "")));
                String arg = FileUtil.getFileContent(fileInputStream);
                ScriptMapping.this.setXmlValue(arg);
                return;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        jsUrl = HttpUrlUtil.getHttpUrl(pluginInfo, jsUrl);
        Log.i(TAG, "从网络加载js" + jsUrl);
        HttpTask<Void, String> httpTask = new HttpTask<Void, String>() {
            @Override
            protected String onRequest(Void requestBody) {
                return null;
            }

            @Override
            protected String onResponse(String retnString) {
                Log.i(TAG, "js:"+resource);
                if(resource != null && retnString != null){
                    mResourceManager.manage(pluginInfo).resource(resource).set(retnString);
                }
                return retnString;
            }

            @Override
            protected void onPost2Ui(int code, String arg) {
                Log.i(TAG, "code="+code + " "+getAddress() + " "+arg);
                if(!isDied() && code == 200) {
                    if(getSilentlyJsChannel() != null && arg != null) {
                        getSilentlyJsChannel().loadJs(arg);
                        getSilentlyJsChannel().exFunction("onReady");
                        Log.i(TAG, "ex onReady!");
                    }
                }
            }
        };
        httpTask.setAddress(jsUrl);
        httpTask.setEnable();
        httpTask.start();
    }
}
