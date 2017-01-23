package com.huan.hhp.app;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import com.huan.hhp.utils.Constants;
import com.huan.hhp.utils.FileUtil;
import com.huan.hhp.xmlParser.HXmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by tjy on 2016/11/18 0018.
 */
public class App_config {
    
    public static final String TAG = App_config.class.getSimpleName();

    App_config(String pkg){
        this.mPkg = pkg;
    }

    void clear(){
        resources.clear();
        plugins.clear();
        allActivity.clear();
        activityMapping2Plugin.clear();
    }

    /**
     * 停止所有插件application
     */
    void kill(){
        Log.i(TAG, "kill");
        Collection<PluginInfo> pluginInfos = plugins.values();
        int i = 0;
        PluginInfo pluginInfo;
        for(; i < pluginInfos.size(); i++){
            pluginInfo = pluginInfos.iterator().next();
            if(pluginInfo.getApplication() != null) {
                pluginInfo.getApplication().clear();
            }
        }
        plugins.clear();
        resources.clear();
        allActivity.clear();
        activityMapping2Plugin.clear();
        pullSuccessed = false;
        mainPlugin = null;
        pluginManager = null;
        hXmlPullParser = null;
        System.gc();
    }

    HashMap<String, Resource> resources = new HashMap<String, Resource>(); // 资源
    HashMap<String, PluginInfo> plugins = new HashMap<String, PluginInfo>(); // 插件
    HashMap<String, ActivityInfo> allActivity = new HashMap<String, ActivityInfo>();
    HashMap<String, PluginInfo> activityMapping2Plugin = new HashMap<String, PluginInfo>();

    private Context context;
    private boolean cacheable; // 是否缓存
    private String ver = ""; // 版本

    private HXmlPullParser hXmlPullParser;

    private boolean pullSuccessed;
    PluginInfo mainPlugin;

    private String mPkg = "";
    private String custom; // 自定义


    PluginManager pluginManager; // 插件管理工具


    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver==null?"":ver;
    }

    public boolean isPullSuccessed() {
        return pullSuccessed;
    }

    /**
     * 获取资源
     * @return
     */
    public Resource getResource(String name) {
        Log.i(TAG, "getResource:"+name);
        return resources.get(name);
    }

    /**
     * 根据类型转换
     * @param layout
     */
    public Resource convert(String layout){
        if(layout != null && !layout.equals("{?}")) {
            if (Resource.isEgg(layout)) {
                // 私有目录
                String name = Resource.getYolk(layout);
                Log.i(TAG, "ref-name "+name);
                Resource resource = resources.get(name);
                return resource;
            }
        }
        Resource resource = new Resource();
        resource.setValue(layout);
        return resource;
    }

    public void loadConfig(Context context, InputStream inputStream) throws IOException, XmlPullParserException {
        this.context = context;
        clear();
        pullSuccessed = false;
        hXmlPullParser = new HXmlPullParser();
        hXmlPullParser.setOnXmlPullParserListener(new HXmlPullParser.OnXmlPullParserListener() {
            PluginInfo pluginInfo;
            ActivityInfo activityInfo;
            @Override
            public void onBegin(HXmlPullParser.Element element) {
                if(element.getName().equals("app-config")){
                    Log.i(TAG, "package="+mPkg);
                    App_config.this.setCacheable(Boolean.valueOf(element.attributeValue("cacheable")));
                    App_config.this.setVer(element.attributeValue("ver"));
                }
                // 资源
                else if(element.getName().equals("resource")){
                    Resource resource = new Resource();
                    resource.setPackage(mPkg);
                    resource.setName(element.attributeValue("name"));
                    resource.setValue(element.attributeValue("value"));
                    resource.setVer(element.attributeValue("ver"));
                    String keepMilliseconds = element.attributeValue("keepMilliseconds");
                    if(keepMilliseconds != null) {
                        resource.setKeepMilliseconds(Long.valueOf(keepMilliseconds));
                    }
                    element.setObj(resource);
                    resources.put(resource.getName(), resource);
                }

                // plugin-list
                else if(element.getName().equals("plugin")){
                    pluginInfo = new PluginInfo();
                    pluginInfo.setPackage(mPkg);
                    pluginInfo.setId(element.attributeValue("id"));
                    pluginInfo.setName(convert(element.attributeValue("name")).getValue());
                    pluginInfo.setIcon(convert(element.attributeValue("icon")).getValue());
                    pluginInfo.setDes(convert(element.attributeValue("des")).getValue());
                    pluginInfo.setProject(convert(element.attributeValue("project")).getValue());
                    pluginInfo.setFile(convert(element.attributeValue("file")));
                    pluginInfo.setMain(Boolean.valueOf(element.attributeValue("isMain")));
                    pluginInfo.setInitiative(Boolean.valueOf(element.attributeValue("initiative")));
                    String parent = element.attributeValue("parent");
                    if(parent != null){
                        String parentPluginName = Resource.getYolk(parent);
                        PluginInfo parentPlugin = getPluginInfoByName(parentPluginName);
                        pluginInfo.setParent(parentPlugin);
                    }
                }

                // application
                else if (element.getName().equals("application")){
                    ApplicationInfo applicationInfo = new ApplicationInfo();
                    applicationInfo.setPackage(mPkg);
                    applicationInfo.setClassName(convert(element.attributeValue("class")).getValue());
                    pluginInfo.setApplicationInfo(applicationInfo);
                }
                // activity
                else if (element.getName().equals("activity")){
                    activityInfo = new ActivityInfo();
                    activityInfo.setPackage(mPkg);
                    activityInfo.setName(convert(element.attributeValue("name")).getValue());
                    activityInfo.setClassName(convert(element.attributeValue("class")).getValue());
                    activityInfo.setLayout(convert(element.attributeValue("layout")));
                    activityInfo.setMain(Boolean.valueOf(element.attributeValue("isMain")));
                    if (activityInfo.isMain()) {
                        pluginInfo.getApplicationInfo().setMainActivity(activityInfo);
                    }
                    pluginInfo.getApplicationInfo().addActivity(activityInfo.getName(), activityInfo);
                    allActivity.put(activityInfo.getName(), activityInfo);
                    activityMapping2Plugin.put(activityInfo.getName(), pluginInfo);
                }
                // intent
                else if(element.getName().equals("intent")){
                    Intent androidIntent = new Intent();
                    for(HXmlPullParser.Attribute attribute : element.getAttributes()){
                        androidIntent.putExtra(attribute.getName(), convert(attribute.getValue()).getValue());
                    }
                    activityInfo.setIntent(androidIntent);
                }
                // clear
                else if(element.getName().equals("clear")){
                    String ids = element.attributeValue("ids");
                    FileUtil.clearDiedVer(App_config.this.context, ids);
                }
                // custom
                else if(element.getName().equals("custom")){
                    String value = element.attributeValue("value");
                    App_config.this.custom = value;
                }
            }

            @Override
            public void onEnd(HXmlPullParser.Element parent, HXmlPullParser.Element element) {
                if(element.getName().equals("resource") && parent.getName().equals("persistence-resources")){
                    Resource resource = element.getObj();
                    resource.setPersistence(true);
                }
                else if(element.getName().equals("plugin") && parent.getName().equals("plugin-list")){
                    plugins.put(pluginInfo.getName(), pluginInfo);
                    if(pluginInfo.isMain()){
                        mainPlugin = pluginInfo;
                    }
                }
            }
        });

        if( inputStream != null ) {
            hXmlPullParser.load(inputStream);
        }

        Log.i(TAG, "mapping:"+activityMapping2Plugin.hashCode());
        pullSuccessed = true;
    }

    /**
     * 加载插件
     */
    void createPluginManager(){
        // 解析完成
        pluginManager = new PluginManager(context);
        pluginManager.setPackage(mPkg);
        pluginManager.init();
        pluginManager.loadPlugins(plugins.values());
    }

    /**
     * 加载配置文件
     * @param config
     */
    public void loadConfig(Context context, String config) throws IOException, XmlPullParserException {

        // 根据config判断加载方式
        InputStream inputStream;
        if(config.contains("http://") || config.contains("https://")) {
            URL url = new URL(config);
//            inputStream = url.openStream();
            HttpURLConnection httpURLConn = (HttpURLConnection) url.openConnection();
            httpURLConn.setRequestMethod("GET");
            httpURLConn.setConnectTimeout(6000);
            httpURLConn.connect();// 连接服务器
            Log.i(App_config.class.getSimpleName(), "connected");
            inputStream = httpURLConn.getInputStream();
        }
        else if(config.contains("assets:")) {
            inputStream = context.getAssets().open(config.substring("assets:".length()));
        }
        else if(config.contains("local:")) {
            inputStream = new FileInputStream(config.substring("local:".length()));
        }
        else if(config.contains("sdcard:")) {
            inputStream = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath(), config.substring("sdcard:".length())));
        }
        else {
            inputStream = null;
        }

        loadConfig(context, inputStream);
    }


    public HashMap<String, PluginInfo> getPlugins() {
        return plugins;
    }

    public PluginInfo getPluginInfoByActivityName(String activityName){
        Log.i(TAG, "mapping:"+activityMapping2Plugin.hashCode());
        return activityMapping2Plugin.get(activityName);
    }

    public ActivityInfo getActivityByName(String activityName){
        return allActivity.get(activityName);
    }

    public PluginInfo getPluginInfoByName(String pluginName){
        return plugins.get(pluginName);
    }

    public PluginInfo getMainPlugin(){
        return mainPlugin;
    }

    public String getXml(){
        if(hXmlPullParser == null)
            return null;
        return hXmlPullParser.asXml();
    }

    /**
     * 获取唯一域名
     * @return
     */
    public String getPackage() {
        return mPkg;
    }

    void setPackage(String pkg) {
        this.mPkg = pkg;
    }

    public String getCustom() {
        return custom;
    }

    /**
     * 插件管理工具
     * @return
     */
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * 本地缓存文件
     * @param context
     * @return
     */
    public static File getLocalApp_Config(Context context, String pkg){
        String dataPath = Environment.getDataDirectory() + File.separator + "data";
        File pluginDirFile = new File(dataPath + File.separator + context.getPackageName()+ File.separator +Constants.PLUGIN_DIR);
        File xmlFile = new File(pluginDirFile, pkg+"/"+"config/"+Constants.APPLICATION_CONTEXT_LOCAL_PATH);
        Log.i(TAG, "xmlFile="+xmlFile.getAbsolutePath());
        return xmlFile;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof App_config){
            App_config app_config = (App_config) o;
            return this.ver.equals(app_config.getVer());
        }
        return super.equals(o);
    }
}
