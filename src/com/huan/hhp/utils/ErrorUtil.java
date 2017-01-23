package com.huan.hhp.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Administrator on 2016/6/13.
 */
public class ErrorUtil {
    public static String e(Throwable t) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            String errorString = "\r\n" + sw.toString() + "\r\n";
            return errorString;
        } catch (Throwable t1) {
            return "bad getErrorInfoFromException";
        }
    }
}
