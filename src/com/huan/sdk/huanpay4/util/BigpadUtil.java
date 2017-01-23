package com.huan.sdk.huanpay4.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: warriorr
 * Mail: warriorr@163.com
 * Date: 2016/10/17 0017
 * Time: 16:14
 * To change this template use File | Settings | File Templates
 */
public class BigpadUtil {
    public static String AUTH = "com.tcl.big.provider";
    public static String AUTH_PREFIX = "content://" + AUTH;
    // Device related contents
    public Uri URI_DM = Uri.parse(AUTH_PREFIX + "/devicemodel");
    public Uri URI_DNUM = Uri.parse(AUTH_PREFIX + "/devicenum");
    public Uri URI_DTK = Uri.parse(AUTH_PREFIX + "/devicetoken");
    public Uri URI_CT = Uri.parse(AUTH_PREFIX + "/clienttype");
    public Uri URI_DID = Uri.parse(AUTH_PREFIX + "/deviceid");
    // User system related contents
    public Uri URI_UNAME = Uri.parse(AUTH_PREFIX + "/username");
    public Uri URI_UID = Uri.parse(AUTH_PREFIX + "/userid");
    public Uri URI_UTK = Uri.parse(AUTH_PREFIX + "/usertoken");
    public Uri URI_APPID = Uri.parse(AUTH_PREFIX + "/appid");
    public Uri URI_APPKEY = Uri.parse(AUTH_PREFIX + "/appkey");

    public String getBigpadInfo(Context context, Uri contentUri) {
        String content = null;
        if (context == null || contentUri == null) {
            return content;
        }
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                content = cursor.getString(0);
            }
            cursor.close();
        }
        return content;
    }
}
