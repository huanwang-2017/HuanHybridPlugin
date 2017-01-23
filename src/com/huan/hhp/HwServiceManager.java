package com.huan.hhp;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tjy on 2016/12/22 0022.
 * 服务管理
 */
public class HwServiceManager {
    private PluginApplication application;
    private boolean hasBackground; // 是否还有后台运行的服务
    private boolean alive;

    /** 所有的服务 **/
    ConcurrentHashMap<Object, HwService> hwServices = new ConcurrentHashMap<Object, HwService>(0){
        @Override
        public HwService remove(Object key) {
            HwService hwService = super.remove(key);
            if(hasBackground && this.size() == 0){
                HwServiceManager.this.application = null;
                hasBackground = false;
            }
            return hwService;
        }
    };

    HwServiceManager(PluginApplication application) {
        this.application = application;
        alive = true;
    }


    /** 结束使用 **/ synchronized void destroy(boolean comp){
        Collection<HwService> allServices = hwServices.values();
        for(HwService hwService : allServices){
            if(!comp) {
                hwService.stopSelf();
            }
            else{
                hwService.stop(hwService.getAdministrator());
            }
        }
        if(hwServices.size() == 0){
            HwServiceManager.this.application = null;
        }
        else{
            hasBackground = true;
        }
        alive = false;
    }

    /**
     * 通过管理员获取服务
     * @param administrator
     * @return
     */
    public HwService getService(Object administrator){
        return hwServices.get(administrator);
    }

    /**
     * 得到Application对象
     * @return
     */
    public PluginApplication getApplication() {
        return application;
    }

    public boolean isAlive() {
        return alive;
    }
}
