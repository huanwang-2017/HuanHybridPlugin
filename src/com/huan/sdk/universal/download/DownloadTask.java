package com.huan.sdk.universal.download;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by Administrator on 2016/10/12.
 */
public class DownloadTask implements Runnable {
    final String TAG = DownloadTask.class.getSimpleName();
    private Observable observable;
    private DownloadEntity mEntity; // 下载对象
    private int splitCount = 1;
    private List<DownloadEntitySplit> splits = new ArrayList<DownloadEntitySplit>();
    private boolean running;

    synchronized boolean childRunning() {
        for(DownloadEntitySplit dSplit : splits){
            if(dSplit.isRunning()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        if(childRunning())
            return;
        running = true;
        try {
            long length = getLength();
            Log.i(TAG, "length=" + length);
            mEntity.setLength(length);
            mEntity.getState().setState(DownloadState.STATE_READY);
            observable.notifyObservers(mEntity); // 通知页面状态
            mEntity.getDbInfo().setThreadCount(this.getSplitCount(), true);
            setSplitCount(mEntity.getDbInfo().getThreadCount());
            mEntity.getState().setState(DownloadState.STATE_DOING);
            if(length > mEntity.getDbInfo().getLength()) {
                split();
                executeAll();
                while (running || childRunning()) {
                    updateState();
                    if (mEntity.getProgress() == mEntity.getLength()) {
                        mEntity.getState().setState(DownloadState.STATE_SUCCESS);
                        mEntity.getDbInfo().clear();
                        break;
                    }
                    switch (mEntity.getState().getState()) {
                        case DownloadState.STATE_FAIL:
                        case DownloadState.STATE_STOP:
                            break;
                    }
                }
            }
            else if(length == mEntity.getDbInfo().getLength()){
                mEntity.setProgress(length);
                mEntity.getState().setState(DownloadState.STATE_SUCCESS);
                mEntity.getDbInfo().clear();
            }
        } catch (IOException e) {
            ErrorUtil.e(e);
            mEntity.getState().setState(DownloadState.STATE_FAIL);
        } finally {
            Log.i(TAG, "finally state is "+mEntity.getState().getState());
//            try {
//                Log.i(TAG, "finally md5 is "+new MD5().getFileMD5String(new File(mEntity.getDbInfo().getLocalFilePath())));
//                Log.i(TAG, "state is "+mEntity.getState().getState());
//            } catch (IOException e) {
//                ErrorUtil.e(e);
//            }
            if (observable != null) {
                observable.notifyObservers(mEntity);
                this.observable = null;
            }
            running = false;
        }
    }

    /**
     * 更新状态
     */
    public synchronized void updateState() {
        long cursor = 0;
        for (DownloadEntitySplit dSplit : splits) {
            cursor += dSplit.getCursor();
        }
        mEntity.setProgress(cursor);
    }

    /**
     * 下载所有
     */
    public void executeAll() {
        for (DownloadEntitySplit dSplit : splits) {
            execute(dSplit);
        }
    }

    /**
     * 下载某一个片段
     *
     * @param dSplit
     */
    public void execute(DownloadEntitySplit dSplit) {
        new Thread(dSplit).start();
    }

    long getLength() throws IOException {
        URL url = new URL(mEntity.getHttpUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(20 * 1000);
        String length = connection.getHeaderField("Content-Length"); // 获取文件长度

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Log.e(TAG, "下载APK前，获取文件真实大小失败...");
            throw new IOException("content-length is null");
        }
        if (length == null)
            throw new IOException("content-length is null");

        long len = Long.valueOf(length);

        if (len == 0)
            throw new IOException("content-length is null");

        return len;
    }

    /*package 分段*/void split() {
        long length = mEntity.getLength();
        long avgSize = length / splitCount;
        long size = 0;
        int i = 0;
        for (; i < splitCount - 1; i++) {
            DownloadEntitySplit dSplit = splits.get(i);
            dSplit.init(i, mEntity, size, (size += avgSize), mEntity.getDbInfo().getCursor(i));
            size++;
        }
        DownloadEntitySplit dSplit = splits.get(i);
        dSplit.init(i, mEntity, size, length, mEntity.getDbInfo().getCursor(i));
    }

    /**
     * 装载下载对象
     *
     * @param entity
     */
    public void setDownloadEntity(DownloadEntity entity) {
        mEntity = entity;
    }

    public DownloadEntity getDownloadEntity() {
        return mEntity;
    }

    public Observable getObservable() {
        return observable;
    }

    public void setObservable(Observable observable) {
        this.observable = observable;
    }

    public int getSplitCount() {
        return splitCount;
    }

    /**
     * 设置多线程下载
     *
     * @param splitCount
     */
    public void setSplitCount(int splitCount) {
        this.splitCount = Math.max(splitCount, 1);
        if(splitCount < splits.size()){
            int count = splits.size();
            int i = splitCount;
            for(; i < count; i++){
                splits.remove(0);
            }
        }
        else {
            for (int i = splits.size(); i < splitCount; i++) {
                splits.add(new DownloadEntitySplit());
            }
        }
    }

    /**
     * 获取下载分段集合
     *
     * @return
     */
    public final List<DownloadEntitySplit> getSplits() {
        return new ArrayList<DownloadEntitySplit>(splits);
    }

    /**
     * 是否在运行中
     *
     * @return
     */
    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }
}
