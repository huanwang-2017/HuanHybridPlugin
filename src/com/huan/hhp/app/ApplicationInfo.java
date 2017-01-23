package com.huan.hhp.app;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by tjy on 2016/10/30.
 */
public class ApplicationInfo extends HWPkg {
    ApplicationInfo() {
    }

    private String className;
    private HashMap<String, ActivityInfo> activityInfoHashMap = new HashMap<String, ActivityInfo>();
    private ActivityInfo mainActivity;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    public Collection<ActivityInfo> getActivityInfoHashMap() {
        return activityInfoHashMap.values();
    }

    public void addActivity(String name, ActivityInfo activityInfo) {
        this.activityInfoHashMap.put(name, activityInfo);
    }

    public ActivityInfo getActivity(String name){
        return activityInfoHashMap.get(name);
    }

    public ActivityInfo getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(ActivityInfo mainActivity) {
        this.mainActivity = mainActivity;
    }
}
