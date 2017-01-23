package com.huan.sdk.universal.download;

/**
 * Created by Administrator on 2016/10/12.
 */
public class Log {
    public static void i(String tag, String value) {
        System.out.println(tag + ", " + value);
    }

    public static void d(String tag, String value) {
        System.out.println(tag + ", " + value);
    }

    public static void e(String tag, String value) {
        System.err.println(tag + ", " + value);
    }
}
