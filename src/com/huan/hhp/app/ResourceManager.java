package com.huan.hhp.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.huan.hhp.utils.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tjy on 2016/11/25 0025.
 */
public class ResourceManager {

    public static String TAG = ResourceManager.class.getSimpleName();
    private static HashMap<String, Resource> gcQueue = new HashMap<String, Resource>();
    private static HashMap<String, Timer> gcTimerQueue = new HashMap<String, Timer>();
    private Timer resourceOverdueSelector;

    private Resource mResource;
    private PluginInfo pluginInfo;
    private App_config app_config;

    public synchronized ResourceManager resource(Resource mResource) {
        this.mResource = mResource;
        final String key = mResource.getPackage()+"."+mResource.getName();
        if(gcTimerQueue.containsKey(key)){
            Timer timer = gcTimerQueue.remove(key);
            timer.cancel();
            Log.i(TAG, mResource.getName()+"被唤醒!");
        }
        return this;
    }

    public synchronized ResourceManager manage(PluginInfo pluginInfo){
        Log.i(TAG, "manage "+pluginInfo);
        if(pluginInfo != null) {
            this.pluginInfo = pluginInfo;
            app_config = App_configManager.getApp_config(pluginInfo.getPackage());
        }
        return this;
    }

    /**
     * 不同的app-config中同名resource做区分
     * @param key
     * @return
     */
    String onlymeKey(String key){
        return mResource.getPackage()+"/"+key;
    }

    /**
     * 缓存内容
     * @param content
     */
    public synchronized void set(String content){
        if(!mResource.isPersistence())
            return;

        if(mResource.getValue().contains(".zip") || mResource.getValue().contains(".apk")){
            FileUtil.saveVer(app_config.getPluginManager().getContext(), onlymeKey(mResource.getName()), mResource.getVer());
        }

        else if(mResource.getValue().contains(".xml")){
            Log.i(TAG, "app_config="+app_config+", manag="+app_config.getPluginManager()+" nReource="+mResource);
            File file = new File(app_config.getPluginManager().getPluginDirFile(), "xml-list/"+mResource.getName()+".xml");
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            try {
                FileUtil.saveString2File(file, content);
                FileUtil.saveVer(app_config.getPluginManager().getContext(), onlymeKey(mResource.getName()), mResource.getVer());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(mResource.getValue().contains(".js")){
            File file = new File(app_config.getPluginManager().getPluginDirFile(), "js/"+mResource.getName()+".js");
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            try {
                FileUtil.saveString2File(file, content);
                FileUtil.saveVer(app_config.getPluginManager().getContext(), onlymeKey(mResource.getName()), mResource.getVer());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            SharedPreferences sp = app_config.getPluginManager().getContext().getSharedPreferences("com.huan.hhp-resource", Context.MODE_PRIVATE);
            sp.edit().putString(onlymeKey(mResource.getName()), mResource.getValue()).commit();
        }
    }

    /**
     * 删除缓存
     */
    public void del(){
        App_config app_config = App_configManager.getApp_config(mResource.getPackage());
        if(mResource.getValue().contains(".zip") || mResource.getValue().contains(".apk")){
            Log.i(TAG, "del zip");
            FileUtil.deleteAndParnetNoChildren(pluginInfo.getWorkspace());
        }
        else if(mResource.getValue().contains(".xml")){
            Log.i(TAG, "del xml");
            File file = new File(app_config.getPluginManager().getPluginDirFile(), "xml-list/"+mResource.getName()+".xml");
            FileUtil.deleteAndParnetNoChildren(file);
        }
        else if(mResource.getValue().contains(".js")){
            Log.i(TAG, "del js");
            File file = new File(app_config.getPluginManager().getPluginDirFile(), "js/"+mResource.getName()+".js");
            FileUtil.deleteAndParnetNoChildren(file);
        }
        else{
            Log.i(TAG, "del string");
            SharedPreferences sp = app_config.getPluginManager().getContext().getSharedPreferences("com.huan.hhp-resource", Context.MODE_PRIVATE);
            sp.edit().remove(onlymeKey(mResource.getName())).commit();
        }
    }

    /**
     * 获取缓存内容
     * @return
     */
    public <T> T get(){
        if(!mResource.isPersistence())
            return null;

        if(!FileUtil.getVer(app_config.getPluginManager().getContext(), onlymeKey(mResource.getName())).equals(mResource.getVer()))
            return null;

        if(mResource.getValue().contains(".zip") || mResource.getValue().contains(".apk")){
            return (T) pluginInfo.getWorkspace();
        }
        else if(mResource.getValue().contains(".xml")){
            File file = new File(app_config.getPluginManager().getPluginDirFile(), "xml-list/"+mResource.getName()+".xml");
            try {
                return (T) new FileInputStream(file);
            } catch (FileNotFoundException e) {
            }
        }
        else if(mResource.getValue().contains(".js")){
            File file = new File(app_config.getPluginManager().getPluginDirFile(), "js/"+mResource.getName()+".js");
            try {
                return (T) new FileInputStream(file);
            } catch (FileNotFoundException e) {
            }
        }
        else{
            SharedPreferences sp = app_config.getPluginManager().getContext().getSharedPreferences("com.huan.hhp-resource", Context.MODE_PRIVATE);
            return (T) sp.getString(onlymeKey(mResource.getName()), null);
        }
        return null;
    }

    /**
     * 销毁
     */
    public void release(){
        if(mResource == null)
            return;
        if (mResource.isPersistence() && mResource.getKeepMilliseconds() != -1) {
            final String key = mResource.getPackage()+"."+mResource.getName();
            mResource.setEffectOfTime(System.currentTimeMillis());
            gcQueue.put(key, mResource);
            Log.i(TAG, mResource.getName()+"准备在"+mResource.getKeepMilliseconds()+"毫秒后删除缓存!");
            resourceOverdueSelector = new Timer("RESOURCE_OVERDUE_SELECTOR", false);
            resourceOverdueSelector.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(gcQueue.containsKey(key)) {
                        Resource resource = gcQueue.get(key);
                        if(System.currentTimeMillis()-resource.getEffectOfTime() >= resource.getKeepMilliseconds()){
                            if(resource == mResource){
                                del();
                                Log.i(TAG, "资源 " + mResource.getName() + " 已过期,被删除!");
                                gcQueue.remove(key);
                                gcTimerQueue.remove(key);
                            }
                        }
                        resourceOverdueSelector.cancel();
                        resourceOverdueSelector = null;
                        Log.i(TAG, "停止计时 " + gcQueue.size()+", "+gcTimerQueue.size());
                    }
                }
            }, 5000, 5000);
            gcTimerQueue.put(key, resourceOverdueSelector);
        }
    }
}
