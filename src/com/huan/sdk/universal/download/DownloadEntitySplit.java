package com.huan.sdk.universal.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/10/12.
 */
public class DownloadEntitySplit implements Runnable {
    final String TAG = DownloadEntitySplit.class.getSimpleName();
    private int id;
    private DownloadEntity entity;
    private long start;
    private long end;
    private long cursor;

    private long length;
    private byte[] reader;  // 读写工具
    private boolean running;

    private long lastSaveTime; // 最后一次保存的是时间

    public DownloadEntitySplit() {
    }

    public DownloadEntitySplit init(int id, DownloadEntity entity, long start, long end, long cursor) {
        this.id = id;
        this.entity = entity;
        this.start = start;
        this.end = end;
        this.length = this.end - this.start;
        this.cursor = Math.min(cursor, this.length);
        reader = new byte[1024<<3];
        Log.i(TAG, "id=" + id + ", " + "[" + start + "," + end + "]");
        return this;
    }

    InputStream getDownLoadFileIo(String path, long progress, long size) throws IOException {
        InputStream is = null;
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Referer", url.toString());
        conn.setRequestProperty("Range", "bytes=" + progress + "-" + size + "");
        conn.setRequestProperty("Connection", "Keep-Alive");
        is = conn.getInputStream();
        return is;
    }

    @Override
    public void run() {
        RandomAccessFile itemSaveFile;
        InputStream inputStream;
        long startPos = start + cursor;
        try {
            inputStream = getDownLoadFileIo(entity.getHttpUrl(), startPos, end);
        } catch (IOException e) {
            ErrorUtil.e(e);
            entity.getState().setState(DownloadState.STATE_FAIL);
            return;
        }
        // 初始化本地文件
        try {
            itemSaveFile = new RandomAccessFile(entity.getDbInfo().getLocalFilePath(), "rw");
            itemSaveFile.seek(startPos);
        } catch (Exception e) {
            ErrorUtil.e(e);
            entity.getState().setState(DownloadState.STATE_FAIL);
            return;
        }
        running = true;
        while (running) {
            if(entity.getState().getState() == DownloadState.STATE_PAUSE || entity.getState().getState() == DownloadState.STATE_STOP){
                break;
            }
            try {
                int readLen = inputStream.read(reader, 0, reader.length);
                if (readLen != -1) {
                    cursor += readLen;
                    synchronized (entity) {
                        itemSaveFile.write(reader, 0, readLen);
                    }
                    if(System.currentTimeMillis() - lastSaveTime > 2000) {
                        entity.getDbInfo().setCursor(id, cursor);
                        lastSaveTime = System.currentTimeMillis();
                    }
                } else {
                    break;
                }
            } catch (IOException e) {
                ErrorUtil.e(e);
                entity.getState().setState(DownloadState.STATE_FAIL);
                break;
            }
            if(entity.getState().getState() == DownloadState.STATE_PAUSE || entity.getState().getState() == DownloadState.STATE_STOP){
                break;
            }
        }
        entity.getDbInfo().setCursor(id, cursor); // 完成后保存一下最终状态
        running = false;
        if (itemSaveFile != null) {
            try {
                itemSaveFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "关闭输出流");
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "关闭输入流");
        }
    }

    public long getLength() {
        return length;
    }

    public long getCursor() {
        return cursor;
    }

    public boolean isRunning() {
        return running;
    }
}
