package com.huan.hhp.utils;

import com.huan.hhp.app.PluginInfo;

import java.io.File;

/**
 * Created by Administrator on 2016/10/21.
 */
public class HttpUrlUtil {
    public static String getHttpUrl(PluginInfo pluginInfo, String url){
        if(url.contains("local:")){
            return new File(pluginInfo.getWorkspace(), url.substring("local:".length())).getAbsolutePath();
        }
        if(url.contains("http://") || url.contains("https://"))
            return url;
        return pluginInfo.getProject() + url;
    }
}
