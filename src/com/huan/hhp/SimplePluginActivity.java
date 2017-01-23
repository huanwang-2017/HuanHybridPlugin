package com.huan.hhp;

import android.content.Intent;
import com.huan.hhp.app.ActivityInfo;
import com.huan.hhp.app.PluginInfo;
import com.huan.hhp.common.HHP;

/**
 * Created by tjy on 2016/10/31.
 */
public class SimplePluginActivity extends PluginActivity{
    public SimplePluginActivity(HHP openMeFrom, PluginInfo pluginInfo, ActivityInfo activityInfo) {
        super(openMeFrom, pluginInfo, activityInfo);
    }

    @Override
    public void onCreate(Intent intent) {
        super.onCreate(intent);
        setContentView(getActivityInfo().getName());
    }

    @Override
    public void onUiAfter() {
    }
}
