package com.huan.hhp.common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.huan.hhp.PluginActivity;
import com.huan.hhp.SimplePluginActivity;
import com.huan.hhp.app.*;
import com.huan.hhp.utils.Constants;

/**
 * Created by tjy on 2016/10/20.
 */
public class DynamicActivity extends HHP {
    public void onDestroy(){
        app_config = null;
        super.onDestroy();
    }

    App_config app_config;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle("...");
        Intent intent = getIntent();
        Log.i(TAG, "extras="+intent.getExtras());
        Log.i(TAG, "package="+intent.getStringExtra("package"));
        app_config = App_configManager.getApp_config(intent.getStringExtra("package"));
        if (app_config == null) {
            app_config = App_configManager.getDefApp_config();
        }
        Log.i(TAG, "app_config="+app_config);
        final String activityName = intent.getStringExtra(Constants.ACTIVITY_NAME);
        Log.i(TAG, "activityName="+activityName);
        PluginInfo pluginInfo = app_config.getPluginInfoByActivityName(activityName);
        app_config.getPluginManager().loadPluginSync(pluginInfo, new PluginManager.AfterLoadPluginByUrlListener() {
            @Override
            public synchronized void onAfter(PluginInfo pluginInfo) {
                try {
                    ActivityInfo activityInfo = app_config.getActivityByName(activityName);
                    if(activityInfo != null && app_config.getPluginManager() != null) {
                        Class clz = app_config.getPluginManager().loadClass(pluginInfo, activityInfo.getClassName());
                        PluginActivity pluginActivity = (PluginActivity) clz.getConstructor(HHP.class, PluginInfo.class, ActivityInfo.class).newInstance(DynamicActivity.this, pluginInfo, activityInfo);
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

    class AnonymousPluginActivity extends SimplePluginActivity {
        public AnonymousPluginActivity(HHP openMeFrom, PluginInfo pluginInfo, ActivityInfo activityInfo) {
            super(openMeFrom, pluginInfo, activityInfo);
        }
    }
}
