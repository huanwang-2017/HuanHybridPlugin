package com.huan.hhp.widget;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.huan.hhp.FileManager;
import com.huan.hhp.app.*;
import com.huan.hhp.widget.typeof.TypeOf;

/**
 * Created by tjy on 2016/11/5.
 */
public class IncludeMapping extends RelativeLayoutMapping {
    {
        forTag(HwMappings.PKG + ".view.IncludeView");
        mapping("layout", new TypeOf("this.setLayout", String.class));
    }

    public IncludeMapping(Context context, String name) {
        super(context, name);
    }

    //    private boolean fromHttp = false;
    public void setLayout(String layout){
        App_config app_config = App_configManager.getApp_config(getPackage());
        PluginInfo pluginInfo = app_config.getPluginManager().getCurrentPlugInfo();
        ActivityInfo activityInfo = new ActivityInfo(pluginInfo.getCurrentActivity());
        Resource resource = app_config.convert(layout);
        if(!layout.substring(0, 1).equals("/")) {
            resource.setValue(activityInfo.getLayout().getValue() + "/../" + layout);
        }
        Log.i(TAG, "rsource = " + resource.getValue());
        activityInfo.setLayout(resource);

        new FileManager(mContext).loadFile(pluginInfo, activityInfo, new FileManager.Callback() {
            @Override
            public void onSuccess(View view) {
                ((ViewGroup)mView).removeAllViews();
                ((ViewGroup)mView).addView(view);
                ViewMapping viewMapping = (ViewMapping) view.getTag();
                viewMapping.onCreate();
            }
        });
    }
}
