package com.huan.hhp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.huan.hhp.app.PluginInfo;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * Created by Administrator on 2016/10/26.
 */
public class FileUtil {

    /**
     *
     * 复制文件
     *
     * @param source
     *            - 源文件
     *
     * @param target
     *            - 目标文件
     *
     */
    public static boolean copyFile(File source, File target) {
        FileInputStream fi = null;
        FileOutputStream fo = null;

        FileChannel in = null;
        FileChannel out = null;

        try {
            fi = new FileInputStream(source);
            fo = new FileOutputStream(target);
            in = fi.getChannel();// 得到对应的文件通道
            out = fo.getChannel();// 得到对应的文件通道
            in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
            return true;
        } catch (FileNotFoundException e) {
            System.err.println("找不到文件："+ErrorUtil.e(e));
            return false;
        } catch (IOException e) {
            System.err.println("文件拷贝失败："+ErrorUtil.e(e));
            return false;
        } finally{
            if (fi != null) {
                try {
                    fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fo != null) {
                try {
                    fo.flush();
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** 文件及所在目录加载权限 */
    public static void chmodPath(String permission, String path) {
        try {
            Runtime.getRuntime().exec("chmod -R " + permission +" " + path);
        } catch (Exception e) {
           System.out.println("chmodPath fault1 msg=" + e);
        }
    }

    /**
     * 保存文件
     * @param toFile
     * @param string
     * @throws Exception
     */
    public static void saveString2File(File toFile, String string) throws Exception{
        if(string == null) {
            System.err.println("内容为空,操作结束！");
            return;
        }
        if(toFile.exists()){
            toFile.delete();
            System.out.println("删除本地旧文件");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(string.getBytes("utf-8"))));
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(toFile));
        while(true){
            String str = reader.readLine();
            if(str != null){
                printWriter.println(str+"\n");
            }
            else{
                break;
            }
        }
        reader.close();
        printWriter.flush();
        printWriter.close();
    }

    public static String getFileContent(FileInputStream fis) {
        StringBuffer stringBuilder = new StringBuffer();
        try {
            byte[] b = new byte[1024];
            int len = -1;
            while((len=fis.read(b)) != -1){
                stringBuilder.append(new String(b, 0, len));
            }
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally{
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取版本
     * @param context
     * @param pluginId
     * @return
     */
    public static String getVer(Context context, String pluginId){
        SharedPreferences sp = context.getSharedPreferences("plugin-ver", Context.MODE_PRIVATE);
        String ver = sp.getString(pluginId, "");
        return ver;
    }

    /**
     * 保存版本
     * @param context
     * @param pluginInfo
     */
    public static void saveVer(Context context, PluginInfo pluginInfo){
        SharedPreferences sp = context.getSharedPreferences("plugin-ver", Context.MODE_PRIVATE);
        sp.edit().putString(pluginInfo.getId(), pluginInfo.getFile().getVer()).commit();
    }

    /**
     * 保存版本
     * @param context
     * @param ver
     */
    public static void saveVer(Context context, String key, String ver){
        SharedPreferences sp = context.getSharedPreferences("plugin-ver", Context.MODE_PRIVATE);
        sp.edit().putString(key, ver).commit();
    }

    /**
     * 比对版本是否相同
     * @param context
     * @param pluginInfo
     * @return
     */
    public static boolean eqVer(Context context, PluginInfo pluginInfo){
        return getVer(context, pluginInfo.getId()).equals(pluginInfo.getFile().getVer());
    }

    /**
     * 清理过期的ver
     * @param context
     * @param ids
     */
    public static void clearDiedVer(Context context, String ids){
        if (ids != null && !ids.trim().equals("")) {
            SharedPreferences sp = context.getSharedPreferences("plugin-ver", Context.MODE_PRIVATE);
            String[] arr = ids.split(",");
            SharedPreferences.Editor editor = sp.edit();
            for(String id : arr){
                editor.remove(id);
                System.out.println("clear ver by "+id);
            }
            editor.commit();
        }
    }


    ///////////////////////////////////



    public static void clearDir(File parent){
        if(parent != null && parent.exists()) {
            for (File file : parent.listFiles()) {
                file.delete();
            }
        }
    }

    public static boolean deleteFile(File file){
        if(file.exists()){
            boolean success = file.delete();
            Log.i("FileUtil", "delete file "+file);
            return success;
        }
        return false;
    }

    public static void clearExceptionOff(File dir, String fileName){
        if(dir.listFiles() != null) {
            for (File file : dir.listFiles()) {
                System.out.println("file name="+file.getName()+", "+fileName);
                if (file.getName().equals(fileName)) {
                    continue;
                }
                if(file.isDirectory()){
                    deleteDirectory(file.getAbsolutePath());
                }
                else{
                    file.delete();
                }
                Log.i("FileUtil", "删除文件："+file.getName());
            }
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        boolean flag = false;
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i]);
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }


    public static void deleteAndParnetNoChildren(File file){
        if(file.exists()){
            File parent = file.getParentFile();
            if(file.isDirectory()){
                deleteDirectory(file.getAbsolutePath());
            }
            else {
                file.delete();
            }
            if(parent != null && parent.listFiles().length == 0){
                deleteAndParnetNoChildren(file.getParentFile());
            }
        }
    }

}
