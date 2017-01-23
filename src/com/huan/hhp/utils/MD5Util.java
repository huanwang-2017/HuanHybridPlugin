package com.huan.hhp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: warriorr
 * Mail: warriorr@163.com
 * Date: 13-9-9
 * Time: 下午2:56
 * To change this template use File | Settings | File Templates
 */
public class MD5Util {
    protected static char hexDigits[] = { '0', '1','2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f'};
    public static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            value = bufferToHex(md5.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }


    private static String bufferToHex(byte bytes[]){
        return bufferToHex(bytes, 0,bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n){
        StringBuffer stringbuffer =new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l< k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }


    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt& 0xf0) >> 4];
        char c1 = hexDigits[bt& 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
}
