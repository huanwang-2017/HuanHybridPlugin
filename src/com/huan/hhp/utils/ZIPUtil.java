package com.huan.hhp.utils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Administrator on 2016/10/24.
 */
public class ZIPUtil {
    /**
     * 解压缩功能.
     * 将zipFile文件解压到folderPath目录下.
     * @throws Exception
     *//*
    public static int upZipFile(File zipFile, String folderPath)throws ZipException,IOException {
        //public static void upZipFile() throws Exception{
        ZipFile zfile=new ZipFile(zipFile);
        Enumeration zList=zfile.entries();
        ZipEntry ze=null;
        byte[] buf=new byte[1024];
        int a = 0;
        while(zList.hasMoreElements()){
            if(a == 1)
                break;
            a = 1;
            ze=(ZipEntry)zList.nextElement();
            if(ze.isDirectory()){
                Log.d("upZipFile", "ze.getName() = "+ze.getName());
                String dirstr = folderPath + ze.getName();
                //dirstr.trim();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "str = "+dirstr);
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            Log.d("upZipFile", "ze.getName() = "+ze.getName());
            OutputStream os=new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is=new BufferedInputStream(zfile.getInputStream(ze));
            int readLen=0;
            while ((readLen=is.read(buf, 0, 1024))!=-1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        Log.d("upZipFile", "finishssssssssssssssssssss");
        return 0;
    }

    public static File getRealFileName(String baseDir, String absFileName){
        String[] dirs=absFileName.split("/");
        File ret=new File(baseDir);
        String substr = null;
        if(dirs.length>1){
            for (int i = 0; i < dirs.length-1;i++) {
                substr = dirs[i];
                try {
                    //substr.trim();
                    substr = new String(substr.getBytes("8859_1"), "GB2312");

                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ret=new File(ret, substr);

            }
            Log.d("upZipFile", "1ret = "+ret);
            if(!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length-1];
            try {
                //substr.trim();
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "substr = "+substr);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            ret=new File(ret, substr);
            Log.d("upZipFile", "2ret = "+ret);
            return ret;
        }
        return ret;
    }*/


    public static void upZipFile(File zipPath, File folderPath) throws IOException  {
        if (!zipPath.isFile()) {
            throw new FileNotFoundException(zipPath.getAbsolutePath()+" file not exist!");
        }
        if(!folderPath.exists()){
            folderPath.mkdirs();
        }
        ZipFile zipFile = new ZipFile(zipPath);
        Enumeration<? extends ZipEntry> files = zipFile.entries();
        ZipEntry entry = null;
        File outFile = null;
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        while (files.hasMoreElements()) {
            entry = files.nextElement();
            outFile = new File(folderPath, entry.getName());
            // 如果条目为目录，则跳向下一个
            if(entry.isDirectory()){
                outFile.mkdirs();
                continue;
            }
            // 创建目录
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            // 创建新文件
            outFile.createNewFile();
            // 如果不可写，则跳向下一个条目
            if (!outFile.canWrite()) {
                continue;
            }
            try {
                bin = new BufferedInputStream(zipFile.getInputStream(entry));
                bout = new BufferedOutputStream(new FileOutputStream(outFile));
                byte[] buffer = new byte[1024];
                int readCount = -1;
                while ((readCount = bin.read(buffer)) != -1) {
                    bout.write(buffer, 0, readCount);
                }
            } finally {
                try {
                    bin.close();
                    bout.flush();
                    bout.close();
                } catch (Exception e) {}
            }
        }
    }
}
