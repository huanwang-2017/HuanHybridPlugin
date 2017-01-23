package com.huan.hhp.app;

import android.content.Intent;

/**
 * Created by tjy on 2016/10/26.
 */
public class ActivityInfo extends HWPkg {
    private String name;
    private String className;
    private Resource layout;
    private Intent intent;
    private boolean isMain;
    private ClassLoader classLoader;

    public ActivityInfo() {
    }

    public ActivityInfo(ActivityInfo src) {
        this.name = src.name;
        this.className = src.className;
        this.intent = src.intent;
        this.isMain = src.isMain;
        this.classLoader = src.classLoader;
        this.layout = src.layout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Resource getLayout() {
        return layout;
    }

    public void setLayout(Resource layout) {
        this.layout = layout;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
