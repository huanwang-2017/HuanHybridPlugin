package com.huan.hhp.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import com.huan.hhp.FileManager;
import com.huan.hhp.PluginApplication;
import com.huan.hhp.common.DynamicActivity;
import com.huan.hhp.utils.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tjy on 2016/10/24.
 */
public class PluginManager extends HWPkg {
    final String TAG = PluginManager.class.getSimpleName();

    private Context context;
    private File pluginDirFile;
    private PluginInfo currentPlugInfo;

    private final HashMap<String, PluginInfo> localPlugins = new HashMap<String, PluginInfo>();
    public PluginManager(Context context) {
        this.context = context;
    }

    /**
     * 必须调用
     * @see App_config#loadConfig
     */
    void init(){
        String dataPath = Environment.getDataDirectory() + File.separator + "data";
        pluginDirFile = new File(dataPath + File.separator + context.getPackageName()+ File.separator +Constants.PLUGIN_DIR+"/"+getPackage());
        if(!pluginDirFile.exists()){
            pluginDirFile.mkdirs();
        }
    }

    public Context getContext() {
        return context;
    }

    public PluginInfo getCurrentPlugInfo() {
        return currentPlugInfo;
    }

    public File getPluginDirFile() {
        return pluginDirFile;
    }

    /**
     * 运行插件
     * @param context
     * @param pluginInfo
     */
    public static void runPlugin(Context context, PluginInfo pluginInfo){
        if (pluginInfo != null) {
            Intent intent = new Intent(context, DynamicActivity.class);
            intent.putExtra("package", pluginInfo.getPackage());
            intent.putExtra(Constants.ACTIVITY_NAME, pluginInfo.getApplicationInfo().getMainActivity().getName());
            context.startActivity(intent);
        }
    }

    /**
     * 运行插件
     * @param context
     * @param pluginInfo
     * @param activityInfo
     */
    public static void runPlugin(Context context, PluginInfo pluginInfo, ActivityInfo activityInfo){
        if (pluginInfo != null) {
            Intent intent = new Intent(context, DynamicActivity.class);
            intent.putExtra("package", pluginInfo.getPackage());
            intent.putExtra(Constants.ACTIVITY_NAME, activityInfo.getName());
            context.startActivity(intent);
        }
    }

    /**
     * 运行插件
     * @param context
     * @param pluginName
     */
    public static void runPlugin(Context context, String pkg, String pluginName){
        PluginInfo pluginInfo = App_configManager.getApp_config(pkg).getPluginInfoByName(pluginName);
        if (pluginInfo != null) {
            Intent intent = new Intent(context, DynamicActivity.class);
            intent.putExtra("package", pkg);
            intent.putExtra(Constants.ACTIVITY_NAME, pluginInfo.getApplicationInfo().getMainActivity().getName());
            context.startActivity(intent);
        }
    }

    /**
     * 运行插件
     * @param context
     * @param pluginName
     * @param activityName
     */
    public static void runPlugin(Context context, String pkg, String pluginName, String activityName){
        PluginInfo pluginInfo = App_configManager.getApp_config(pkg).getPluginInfoByName(pluginName);
        ActivityInfo applicationInfo = pluginInfo.getApplicationInfo().getActivity(activityName);
        if (pluginInfo != null) {
            Intent intent = new Intent(context, DynamicActivity.class);
            intent.putExtra("package", pkg);
            intent.putExtra(Constants.ACTIVITY_NAME, applicationInfo.getName());
            context.startActivity(intent);
        }
    }


    /**
     * 加载目录下的插件
     * @param pluginInfoList
     */
    public void loadPlugins(Collection<PluginInfo> pluginInfoList){
        for(PluginInfo pluginInfo : pluginInfoList){
            initFile(pluginInfo, pluginInfo.getFile().getValue());
            loadPlugin(pluginInfo);
        }
        Log.i(TAG, "localPlugins="+localPlugins + getPackage());
        this.currentPlugInfo = App_configManager.getApp_config(getPackage()).getMainPlugin();
    }

    /**
     *   
     * @Description: TODO(拷贝libs文件夹下的.so文件到目标文件夹)
       * 创建人：tangjingyu（唐）
       * 邮箱：    469490037@qq.com 
       * 创建时间：2016/12/27 0027 11:34
     * @return     
     * @param   
     * @throws 
    */
    void copySo2LibsDir(File libsDir, File toLibsDir){
        Log.i(TAG, "copy "+libsDir+" to "+toLibsDir);
        if(!libsDir.exists())
            return;
        if(!toLibsDir.exists())
            toLibsDir.mkdirs();
        final String armName = ArmUtil.getSystemProperty("ro.product.cpu.abi", "armeabi");
        Log.i(TAG, "ready "+armName);

        File[] childrenFiles = libsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.equals(armName);
            }
        });

        for(File file : childrenFiles){
            File[] soList = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.contains(".so");
                }
            });
            for(File f : soList) {
                File newSoFile = new File(toLibsDir, f.getName());
                if (newSoFile.exists()) {
                    boolean eq = MD5Util.getMd5ByFile(newSoFile).equals(MD5Util.getMd5ByFile(f));
                    if (eq)
                        return;
                }
                FileUtil.copyFile(f, newSoFile);
                Log.i(TAG, "cp file " + f + " to " + newSoFile);
            }
        }
    }

    /**
     * 加载本地插件
     * @param pluginInfo
     */
    public boolean loadPlugin(PluginInfo pluginInfo){
        File dexFile = new File(pluginInfo.getWorkspace(), "classes.dex");
        if(!dexFile.exists()) {
            if(!"{?}".equals(pluginInfo.getFile().getValue())) {
                return false;
            }
            else{
                dexFile = pluginInfo.getWorkspace();
            }
        }
        String getClassLoaderTag = pluginInfo.getPackage()+"."+pluginInfo.getId();
        if(pluginInfo.getClassLoader() == null) {
            if(App_configManager.getClassLoader(getClassLoaderTag) != null){
                PluginClassLoader classLoader = App_configManager.getClassLoader(getClassLoaderTag);
                pluginInfo.setClassLoader(classLoader);
            }
            else {
                Log.i(TAG, pluginInfo.getId() + " 加载classLoader");
                File runtimeDirectory = pluginInfo.getRuntimeDirectory();
                File[] children = runtimeDirectory.listFiles();
                if (children != null) {
                    for (File child : children) {
                        if (child.isFile()) {
                            child.delete();
                        } else {
                            FileUtil.deleteDirectory(child.getAbsolutePath());
                        }
                    }
                }
                Log.i(TAG, "runtimeDirectory=" + runtimeDirectory);
                String dexOutputPath = runtimeDirectory.getAbsolutePath();
                Log.i(TAG, "dexOutputPath=" + dexOutputPath);
                File libFile = new File(runtimeDirectory, "lib/");
                File libs = new File(pluginInfo.getWorkspace(), "libs");
                copySo2LibsDir(libs, libFile);
                ClassLoader parentClassLoader;
                if(pluginInfo.getParent() != null){
                    parentClassLoader = pluginInfo.getParent().getClassLoader();
                    if(parentClassLoader == null)
                        return false;
                }
                else{
                    parentClassLoader = context.getClassLoader();
                }
                PluginClassLoader classLoader = new PluginClassLoader(pluginInfo.getPackage(), pluginInfo.getId(), dexFile.getAbsolutePath(), dexOutputPath, libFile.getAbsolutePath(), parentClassLoader);
                pluginInfo.setClassLoader(classLoader);
                App_configManager.registerClassLoader(getClassLoaderTag, classLoader);
                Log.i(TAG, "classLoader=" + classLoader);
            }
            // 加载application
            if(!localPlugins.containsKey(pluginInfo.getId())){
                try {
                    Class applicationClz = pluginInfo.getClassLoader().loadClass(pluginInfo.getApplicationInfo().getClassName());
                    PluginApplication pluginApplication = (PluginApplication) applicationClz.getConstructor(Context.class).newInstance(context);
                    pluginInfo.setApplication(pluginApplication);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        localPlugins.put(pluginInfo.getId(), currentPlugInfo = pluginInfo);
        return true;
    }

    /**
     * 根据plugId查找PluginInfo对象
     * @param pluginId
     * @return
     */
    public PluginInfo getPluginInfo(String pluginId){
        return localPlugins.get(pluginId);
    }

    /**
     * 移除Plugin
     * @param pluginId
     * @return
     */
    public PluginInfo removePluginInfo(String pluginId){
        return localPlugins.remove(pluginId);
    }

    public Collection<PluginInfo> getLocalPlugins() {
        return localPlugins.values();
    }

    /**
     * 加载插件中的类
     * @param pluginInfo
     * @param className
     * @return
     * @throws PluginNotFoundException
     * @throws ClassNotFoundException
     */
    public Class<?> loadClass(PluginInfo pluginInfo, String className) throws PluginNotFoundException, ClassNotFoundException {
        if(pluginInfo == null)
            throw new PluginNotFoundException("不存在的插件被引用！");
        if(pluginInfo.getClassLoader() != null) {
            try {
                Log.i(TAG, "load class:" + className);
                return pluginInfo.getClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                return getClass().getClassLoader().loadClass(className);
            }
        }
        else{
            return getClass().getClassLoader().loadClass(className);
        }
    }

    public static class PluginNotFoundException extends Exception {
        public PluginNotFoundException(String detailMessage) {
            super(detailMessage);
        }
    }

    /**
     * 初始化文件地址
     * @param pluginInfo
     * @param pluginUrl
     */
    void initFile(PluginInfo pluginInfo, String pluginUrl){
        String fileName;
        if(!"{?}".equals(pluginUrl)) {
            fileName = pluginUrl.substring(pluginUrl.lastIndexOf("/"));
        }
        else{
            fileName = "";
        }
        pluginInfo.setRuntimeDirectory(context.getDir(pluginInfo.getId()+"---runtime", Context.MODE_APPEND));
        File verDir = new File(pluginDirFile, MessageFormat.format(Constants.PLUGIN_VER_DIR, pluginInfo.getId()));
        pluginInfo.setVerDir(verDir);
        // src目录
        File verSrc = new File(verDir, MessageFormat.format(Constants.PLUGIN_VER_SRC, pluginInfo.getFile().getVer(), fileName));
        pluginInfo.setVerSrc(verSrc);
//        // layout目录
//        File verLayout = new File(pluginDirFile, "xml-list");
//        if (!verLayout.exists()) {
//            verLayout.mkdirs();
//        }
//        pluginInfo.setVerLayout(verLayout);
//        // js目录
//        File verJs = new File(pluginDirFile, "js/" + pluginInfo.getId());
//        if (!verJs.exists()) {
//            verJs.mkdirs();
//        }
//        pluginInfo.setVerJs(verJs);
        // 解压目录
        File workspace = new File(pluginDirFile, MessageFormat.format(Constants.WORKSPACE, pluginInfo.getId()));
        pluginInfo.setWorkspace(workspace);
    }

    private List<Pms> initiativeQueue = new ArrayList<Pms>();
    private List<Pms> waitQueue = new ArrayList<Pms>();
    /**
     * 从网络加载Plugin
     * @param pluginInfo
     * @param afterLoadPluginByUrlListener
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void loadPluginSync(final PluginInfo pluginInfo, AfterLoadPluginByUrlListener afterLoadPluginByUrlListener){
        Pms pms = new Pms();
        pms.pluginInfo = pluginInfo;
        pms.listener = afterLoadPluginByUrlListener;
        if(initiativeQueue.size() > 0){
            waitQueue.add(pms);
            return;
        }
        if(pluginInfo.isInitiative()){
            initiativeQueue.add(pms);
        }
        new AsyncTask<Pms, Void, Pms>(){
            @Override
            protected Pms doInBackground(Pms... pmses) {
                Pms pms = pmses[0];
                if("{?}".equals(pms.pluginInfo.getFile().getValue())){
                    Log.i(TAG, "加载一个js项目");
                    return pms;
                }
                ResourceManager rm = null;
                Resource fileResource = pms.pluginInfo.getFile();
                Log.i(TAG, "需要缓存："+fileResource.isPersistence());
                if(!fileResource.isPersistence()){
                    // 删除本地缓存
                    FileUtil.clearDir(pms.pluginInfo.getWorkspace());
                    Log.i(TAG, "清理删除本地缓存版本");
                }
                else{
                    rm = new ResourceManager();
                    File workspace = rm.manage(pms.pluginInfo).resource(fileResource).get();
                    if(workspace != null && workspace.exists()){
                        Log.i(TAG, "从本地加载插件...");
                        return pms;
                    }
                }
                Log.i(TAG, "本地没有缓存文件或版本不一致");
                String uriPath = pms.pluginInfo.getFile().getValue();
                uriPath = FileManager.getRealPath(uriPath);

                Log.d(TAG, "uri path is "+uriPath);
                if(uriPath.contains("assets:")) {
                    try {
                        InputStream ins = context.getAssets().open(uriPath.replace("assets:", ""));
                        pms.stream = ins;
                        loadPluginByInputSteram(pms);
                        return pms;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(uriPath.contains("sdcard:")) {
                    try {
                        InputStream ins = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath(), uriPath.replace("sdcard:", "")));
                        pms.stream = ins;
                        loadPluginByInputSteram(pms);
                        return pms;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        InputStream ins = new URL(uriPath).openStream();
                        pms.stream = ins;
                        loadPluginByInputSteram(pms);
                        if(fileResource.isPersistence()){
                            // 保存版本
                            if(rm != null){
                                rm.set(null);
                                rm.release();
                            }
                        }
                        return pms;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Pms pms) {
                if(pms != null){
                    boolean success = loadPlugin(pms.pluginInfo);
                    if(success) {
                        pms.listener.onAfter(pms.pluginInfo);
                    }
                    initiativeQueue.remove(pms);
                    if(initiativeQueue.size() == 0 && waitQueue.size() > 0){
                        Pms plgPms = waitQueue.remove(0);
                        PluginManager.this.loadPluginSync(plgPms.pluginInfo, plgPms.listener);
                    }
                }
            }
        }.execute(pms);
    }


    class Pms{
        PluginInfo pluginInfo;
        InputStream stream;
        AfterLoadPluginByUrlListener listener;
    }

    /**
     *
     * @param pms
     * @return
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private boolean loadPluginByInputSteram(Pms pms) {
        boolean smooth = false;
        FileOutputStream fileOutputStream = null;
        try {
            if(!pms.pluginInfo.getVerSrc().getParentFile().exists()){
                pms.pluginInfo.getVerSrc().getParentFile().mkdirs();
            }
            fileOutputStream = new FileOutputStream(pms.pluginInfo.getVerSrc());
            int len = -1;
            byte[] b = new byte[1024];
            while ((len = pms.stream.read(b)) != -1) {
                fileOutputStream.write(b, 0, len);
            }
            smooth = true;
        } catch (IOException e) {
            smooth = false;
            e.printStackTrace();
            return false;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            if(smooth) {
                return moveHose(pms.pluginInfo);
            }
            return smooth;
        }
    }

    boolean moveHose(PluginInfo pluginInfo){
        // 4.解压
        boolean unZipSuccess = false;
        try {
            Log.i(TAG, "开始解压打包文件...");
            ZIPUtil.upZipFile(pluginInfo.getVerSrc(), pluginInfo.getWorkspace());
            unZipSuccess = true;
            Log.i(TAG, "成功解压打包文件");
        } catch (IOException e) {
            Log.e(TAG, "解压文件失败：" + ErrorUtil.e(e));
        } finally {
            if (unZipSuccess) {
                Log.i(TAG, "拷贝成功，初始化成功！");
                pluginInfo.setClassLoader(null);
                String getClassLoaderTag = pluginInfo.getPackage()+"."+pluginInfo.getId();
                App_configManager.unregisterClassLoader(getClassLoaderTag);
                boolean ifSuccessed = loadPlugin(pluginInfo);
                if (ifSuccessed) {
                    // 删除源文件
                    FileUtil.deleteAndParnetNoChildren(pluginInfo.getVerSrc());
                }
                return ifSuccessed;
            }
        }
        return false;
    }

    public interface AfterLoadPluginByUrlListener{
        void onAfter(PluginInfo pluginInfo);
    }
}
