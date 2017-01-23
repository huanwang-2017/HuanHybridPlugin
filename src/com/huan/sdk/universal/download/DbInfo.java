package com.huan.sdk.universal.download;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

/**
 * Created by Administrator on 2016/10/12.
 */
public class DbInfo {
    private String id;
    private int threadCount;
    private String localFilePath;
    private Context context;
    private SharedPreferences sp;

    /**
     *
     * @param context 上下文
     * @param id    id
     * @param localFilePath 文件地址
     */
    public DbInfo(Context context, String id, String localFilePath) {
        this.context = context;
        this.id = id;
        this.localFilePath = localFilePath;
        sp = context.getSharedPreferences("dbInfo.log."+id, Context.MODE_PRIVATE);
    }

    public synchronized void setCursor(int threadId, long cursor) {
        sp.edit().putLong(id+"."+threadId, cursor).commit();
    }

    public synchronized long getCursor(int threadId) {
        return sp.getLong(id+"."+threadId, 0);
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int setThreadCount(int threadCount, boolean dbPrevail) {
        if(dbPrevail){
            return this.threadCount = sp.getInt("threadCount", 1);
        }
        sp.edit().putInt("threadCount", threadCount).commit();
        return this.threadCount = threadCount;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public int getLength(){
        int length = 0;
        int i = 0;
        for(; i < threadCount; i++){
            length += getCursor(i);
        }
        return length;
    }

    /**
     * 清理下载进度
     * 下载完成后会自动调用该方法，如果有特殊情况需要及时清理下载进度也可以手动调用该方法。
     */
    public void clear(){
        sp.edit().clear().commit();
    }

    /**
     * 删除下载文件
     * 当安装完成后，手动调用该方法进行删除文件。或有特殊情况需要删除本地文件的时候调用。
     */
    public void delete(){
        File localFile = new File(localFilePath);
        if(localFile.exists()){
            localFile.delete();
            Log.i("DbInfo", "删除本地文件！");
        }
    }
}
