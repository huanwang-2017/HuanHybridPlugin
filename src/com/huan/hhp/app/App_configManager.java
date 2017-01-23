package com.huan.hhp.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import com.huan.hhp.PluginApplication;
import com.huan.hhp.SimplePluginActivity;
import com.huan.hhp.utils.ErrorUtil;
import com.huan.hhp.utils.FileUtil;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by tjy on 2016/12/5 0005.
 */
public class App_configManager {
    public static String TAG = App_configManager.class.getSimpleName();

    private static HashMap<String, App_config> logs = new HashMap<String, App_config>(0);

    /**
     * 因为java机制的限制，一个so文件被加载过一次就不能再一次加载了，所以classLaoder只能创建一次
     */
    private static HashMap<String, PluginClassLoader> classLoaderHashMap = new HashMap<String, PluginClassLoader>(0);

    public interface OnLoadConfigListener{
        void onSuccess(App_config app_config);
        void onError();
    }

    /**
     * 注册classLoader
     * @param pkg
     * @param classLoader
     */
    static void registerClassLoader(String pkg, PluginClassLoader classLoader){

        // # 为了解决重复加载问题，所以这里不能够保存原有的classLoader #
        // # 最后的解决办法是把不变的部分独立成单独的plugin，比如第三方jar或so的加载部分都单独出来。#

        classLoaderHashMap.put(pkg, classLoader);
    }

    /**
     * 获取已经存在的classLoader
     * @param pkg
     * @return
     */
    static PluginClassLoader getClassLoader(String pkg){
        return classLoaderHashMap.get(pkg);
    }

    /**
     * 清理原来的classLoader
     * @param pkg
     */
    static void unregisterClassLoader(String pkg){
        classLoaderHashMap.remove(pkg);
    }

    /**
     * 从配置uri创建App_Config
     *
     * @param context
     * @param uri
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void createApp_config(final Context context, final String pkg, final String uri, final OnLoadConfigListener listener) {
        final File localFile = App_config.getLocalApp_Config(context, pkg);
        Log.i(TAG, "request ..." + uri);
        new AsyncTask<Void, Void, App_config>(){
            @Override
            protected App_config doInBackground(Void... params) {
                try {
                    App_config app_config = new App_config(pkg);
                    app_config.loadConfig(context, uri);
                    Log.i(TAG, "xml："+app_config.getXml());
                    App_configManager.log(app_config);
                    if(app_config.isCacheable()) {
                        File xmlParentFile = localFile.getParentFile();
                        if(!xmlParentFile.exists()){
                            xmlParentFile.mkdirs();
                        }
                        FileUtil.saveString2File(localFile, app_config.getXml());
                        Log.i(TAG, "saved 2 "+localFile.getAbsolutePath());
                    }
                    else{
                        FileUtil.deleteAndParnetNoChildren(localFile);
                    }
                    return app_config;
                } catch (Exception e) {
                    Log.e(TAG, "无法解析："+uri+" [ 或因网络异常！] \n" + ErrorUtil.e(e));
                    return null;
                }
            }

            @Override
            protected void onPostExecute(App_config app_config) {
                if (listener != null) {
                    if(app_config != null) {
                        listener.onSuccess(app_config);
                    }
                    else{
                        // 如果本地有缓存，则加载本地缓存
                        Log.i(TAG, "尝试加载本地缓存...");
                        new AsyncTask<Void, Void, App_config>(){
                            @Override
                            protected App_config doInBackground(Void... params) {
                                if(localFile.exists()){
                                    App_config app_config = new App_config(pkg);
                                    try {
                                        app_config.loadConfig(context, "local:"+localFile.getAbsolutePath());
                                        App_configManager.log(app_config);
                                        Log.i(TAG, "local! ");
                                        return app_config;
                                    } catch (Exception e) {
                                    }
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(final App_config localApp_config) {
                                if(listener != null) {
                                    if (localApp_config != null) {
                                        listener.onSuccess(localApp_config);
                                    }
                                    else{
                                        listener.onError();
                                    }
                                }
                            }
                        }.execute();

                    }
                }
            }
        }.execute();
    }

    static void log(App_config app_config){
        if(logs.containsKey(app_config.getPackage())){
            App_config config_contains = logs.get(app_config.getPackage());
            if(app_config != config_contains){
                // 停止之前所有的操作
                config_contains.kill();
            }
        }
        logs.put(app_config.getPackage(), app_config);
        app_config.createPluginManager();
    }

    /**
     * 根据报名查找App_Config
     * @param pkg
     * @return
     */
    public static App_config getApp_config(String pkg){
        return logs.get(pkg);
    }

    /**
     * 删除App_Config
     * @param pkg
     */
    public static void delApp_config(String pkg){
        logs.remove(pkg);
    }

    private static App_config defApp_config;
    public static void init(Context context){
        defApp_config = new App_config(context.getPackageName()+"-plugin");
        logs.put(defApp_config.getPackage(), defApp_config);
        defApp_config.pluginManager = new PluginManager(context);
        defApp_config.pluginManager.setPackage(defApp_config.getPackage());
        defApp_config.pluginManager.init();

        defApp_config.resources.put("layout", new Resource("layout", null));

        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setId("hw-plugin");
        pluginInfo.setDes("默认");
        pluginInfo.setMain(true);
        pluginInfo.setPackage(defApp_config.getPackage());
        pluginInfo.setProject("http:");
        pluginInfo.setFile(new Resource("hw-plugin.layout", "{?}"));

        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.setName("hw-activity");
        activityInfo.setMain(true);
        activityInfo.setClassName(SimplePluginActivity.class.getName());
        activityInfo.setPackage(defApp_config.getPackage());
        activityInfo.setLayout(defApp_config.resources.get("layout"));

        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.setClassName(PluginApplication.class.getName());
        applicationInfo.setPackage(defApp_config.getPackage());
        applicationInfo.addActivity(activityInfo.getName(), activityInfo);
        applicationInfo.setMainActivity(activityInfo);
        defApp_config.allActivity.put(activityInfo.getName(), activityInfo);
        defApp_config.activityMapping2Plugin.put(activityInfo.getName(), pluginInfo);

        pluginInfo.setApplicationInfo(applicationInfo);
        pluginInfo.setCurrentActivity(activityInfo);

        defApp_config.plugins.put(pluginInfo.getName(), pluginInfo);
        defApp_config.mainPlugin = pluginInfo;

        defApp_config.pluginManager.loadPlugins(Arrays.asList(pluginInfo));
    }

    public static void gc(){
        defApp_config = null;
        logs = null;
    }

    public static App_config getDefApp_config(){
        return defApp_config;
    }
}
