package com.huan.hhp;

import android.os.Bundle;
import android.util.Log;
import com.huan.hhp.app.*;
import com.huan.hhp.common.HHP;

/**
 * Created by tjy on 2016/12/6 0006.
 */
public class App_configActivity extends HHP {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 加载配置文件
     * @param pkg
     * @param uri
     */
    protected void setApp_config(String pkg, String uri){
        App_configManager.createApp_config(this, pkg, uri, new App_configManager.OnLoadConfigListener() {
            @Override
            public void onSuccess(App_config app_config) {
                loadPluginInfo(app_config);
            }

            @Override
            public void onError() {

            }
        });
    }

    /**
     * 加载Plugin
     * @param app_config
     * @param pluginInfo
     */
    protected void loadPluginInfo(final App_config app_config, PluginInfo pluginInfo){
        app_config.getPluginManager().loadPluginSync(pluginInfo, new PluginManager.AfterLoadPluginByUrlListener() {
            @Override
            public synchronized void onAfter(PluginInfo pluginInfo) {
                try {
                    ActivityInfo activityInfo = pluginInfo.getApplicationInfo().getMainActivity();
                    if(activityInfo != null && app_config.getPluginManager() != null) {
                        Log.i(TAG, "main activity is " + activityInfo.getClassName());
                        Class clz = app_config.getPluginManager().loadClass(pluginInfo, activityInfo.getClassName());
                        PluginActivity pluginActivity = (PluginActivity) clz.getConstructor(HHP.class, PluginInfo.class, ActivityInfo.class).newInstance(App_configActivity.this, pluginInfo, activityInfo);
                        pluginActivity.setApp_config(app_config);
                        setPluginActivity(pluginActivity, activityInfo.getIntent());
                    }
                    else{
                        pluginInfo.getApplication().onCreate();
                    }
                } catch (PluginManager.PluginNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void loadPluginInfo(App_config app_config){
        PluginInfo pluginInfo;
        for(String key : app_config.getPlugins().keySet()){
            pluginInfo = app_config.getPlugins().get(key);
            if(pluginInfo.isInitiative()){
                loadPluginInfo(app_config, pluginInfo);
            }
        }
        loadPluginInfo(app_config, app_config.getMainPlugin());
    }
}
