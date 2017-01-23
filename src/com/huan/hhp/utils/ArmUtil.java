package com.huan.hhp.utils;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by tjy on 2016/12/27 0027.
 */
public class ArmUtil {
    public static String getSystemProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> clazz= Class.forName("android.os.SystemProperties");
            Method get = clazz.getMethod("get", String.class, String.class);
            value = (String)(get.invoke(clazz, key, ""));
        } catch (Exception e) {
            Log.d("getSystemProperty", "key = " + key + ", error = " + e.getMessage());
        }
        Log.d("getSystemProperty",  key + " = " + value);
        return value;
    }
}
