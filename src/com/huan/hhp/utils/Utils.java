package com.huan.hhp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Administrator on 2016/10/24.
 */
public class Utils {
    /**
     * 文件及所在目录加载权限
     */
    public static void chmodPath(String permission, String path) {
        try {
            Runtime.getRuntime().exec("chmod -R " + permission + " " + path);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ApkDecryptImpl", "chmodPath fault1 msg=" + e);
        }
    }

    /**
     * @Description:判断网络是否可用
     * @param:Context context
     * @return: boolean
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conManager != null) {
            NetworkInfo info = conManager.getActiveNetworkInfo();
            if (info != null) {
                if (NetworkInfo.State.CONNECTED == info.getState())
                    return true;
            }
        }
        return false;
    }
}
