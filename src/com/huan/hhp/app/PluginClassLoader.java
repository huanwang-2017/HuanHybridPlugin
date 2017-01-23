package com.huan.hhp.app;

import android.annotation.TargetApi;
import android.os.Build;
import dalvik.system.DexClassLoader;

/**
 * Created by tjy on 2016/11/5.
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class PluginClassLoader extends DexClassLoader {
    private String pkg;
    private String pluginId;

    public PluginClassLoader(String pkg, String pluginId, String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
        this.pluginId = pluginId;
        this.pkg = pkg;
    }

    public String getPluginId() {
        return pluginId;
    }

    public String getPackage() {
        return pkg;
    }
}
