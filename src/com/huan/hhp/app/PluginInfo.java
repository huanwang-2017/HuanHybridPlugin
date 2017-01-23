package com.huan.hhp.app;

import android.util.Log;
import com.huan.hhp.PluginApplication;

import java.io.File;

/**
 * Created by tjy on 2016/10/24.
 */
public class PluginInfo extends HWPkg {
    PluginInfo(){}

    private String id;      // 唯一id
    private String name;    // 名称，向外部提供的一个引用
    private String icon;    // 显示的icon
    private String des;     // 简要描述
    private Resource file;    // 服务器文件地址
    private String project; // 项目地址
    private boolean isMain;
    private boolean initiative; // 自动启动
    private PluginClassLoader classLoader;
    private ApplicationInfo applicationInfo;
    private PluginApplication application;

    private PluginInfo parent; // 父插件

    private ActivityInfo currentActivity;

    private File runtimeDirectory;  // 运行时文件夹，当解析dex的时候会把dex文件以及对应的so文件拷贝到该文件夹中，用完删除。
    private File verDir;    // 版本路径
    private File workspace; // 解压目录
    private File verSrc;  // 源文件地址
    private File verLayout; // 布局备份文件地址
    private File verJs;    // js缓存目录

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public boolean isInitiative() {
        return initiative;
    }

    public void setInitiative(boolean initiative) {
        this.initiative = initiative;
    }

    public PluginInfo getParent() {
        return parent;
    }

    public void setParent(PluginInfo parent) {
        this.parent = parent;
    }

    public Resource getFile() {
        return file;
    }

    public void setFile(Resource file) {
        String path = file.getValue();
        if(path == null){
            file.setValue(path = "{?}");
        }
        if(!"{?}".equals(path)){
            if(file.getValue().indexOf(project) == -1) {
                path = project + file.getValue();
            }
        }
        else{
            name = "def-hwF";
        }
        file.setValue(path);
        this.file = file;
        Log.i("PluginInfo", "file path="+file.getValue());
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public PluginApplication getApplication() {
        return application;
    }

    public void setApplication(PluginApplication application) {
        this.application = application;
        this.application.setPluginInfo(this);
    }

    public PluginClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(PluginClassLoader classLoader) {
        this.classLoader = classLoader;
        for(ActivityInfo activityInfo : applicationInfo.getActivityInfoHashMap()){
            activityInfo.setClassLoader(classLoader);
        }
    }

    public File getRuntimeDirectory() {
        return runtimeDirectory;
    }

    public void setRuntimeDirectory(File runtimeDirectory) {
        this.runtimeDirectory = runtimeDirectory;
    }

    public void setVerDir(File verDir) {
        this.verDir = verDir;
    }

    public File getVerDir() {
        return verDir;
    }

    public File getVerSrc() {
        return verSrc;
    }

    public void setVerSrc(File verSrc) {
        this.verSrc = verSrc;
    }

    public File getVerLayout() {
        return verLayout;
    }

    public void setVerLayout(File verLayout) {
        this.verLayout = verLayout;
    }

    public File getWorkspace() {
        return workspace;
    }

    public void setWorkspace(File workspace) {
        this.workspace = workspace;
    }

    public File getVerJs() {
        return verJs;
    }

    public void setVerJs(File verJs) {
        this.verJs = verJs;
    }

    public synchronized ActivityInfo getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(ActivityInfo currentActivity) {
        this.currentActivity = currentActivity;
    }
}
