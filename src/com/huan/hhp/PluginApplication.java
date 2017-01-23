package com.huan.hhp;

import android.content.Context;
import android.util.Log;
import com.huan.hhp.app.PluginInfo;
import com.huan.hhp.app.ResourceManager;
import com.huan.hhp.utils.ReflexUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tjy on 2016/10/30.
 */
public class PluginApplication {

    /** 获取插件中的服务管理器 **/
    public static final String HW_SERVICE_MANAGER = "com.huan.hhp.common.hw-service-manager";

    private HwServiceManager hwServiceManager; {
        hwServiceManager = new HwServiceManager(this);
    }

    /**
     * 获取系统服务
     * @param name
     * @return
     */
    public Object getSystemService(String name) {
        if(HW_SERVICE_MANAGER.equals(name)){
            return hwServiceManager;
        }
        return null;
    }

    private PluginInfo pluginInfo;
    private ResourceManager resourceManager;
    private Context mContext; // 上下文对象

    public PluginApplication(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setPluginInfo(PluginInfo pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    private List<Object> queue = new ArrayList<Object>(){
        @Override
        public boolean add(Object object) {
            Log.i(PluginApplication.class.getSimpleName(), "add:" + object);
            if(size() == 0){
                onCreate();
            }
            return super.add(object);
        }

        @Override
        public boolean remove(Object object) {
            boolean flag = super.remove(object);
            if(size() == 0){
                onDestroy();
            }
            return flag;
        }

        @Override
        public void clear() {
            for(Object obj : queue){
                ReflexUtil.execute(obj, "onDestroy");
            }
            onDestroy();
            super.clear();
        }
    };

    public void onCreate(){
        Log.i(PluginApplication.class.getSimpleName(), "onCreate");
        resourceManager = new ResourceManager();
        resourceManager.manage(pluginInfo).resource(pluginInfo.getFile());
    }

    public void onDestroy(){
        Log.i(PluginApplication.class.getSimpleName(), "onDestroy " + resourceManager);
        if(resourceManager != null) {
            resourceManager.release();
            resourceManager = null;
        }
        if(hwServiceManager != null) {
            hwServiceManager.destroy(true);
        }
        pluginInfo = null;
        mContext = null;
    }

    void add(Object obj) {
        queue.add(obj);
    }

    void remove(Object obj){
        queue.remove(obj);
    }

    public void clear(){
        if(queue.size() > 0) {
            Log.i(PluginApplication.class.getSimpleName(), "clear");
            queue.clear();
        }
    }

    public List<Object> getQueue() {
        return new ArrayList<Object>(queue);
    }
}
