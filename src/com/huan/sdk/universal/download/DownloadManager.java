package com.huan.sdk.universal.download;

/**
 * Created by Administrator on 2016/10/12.
 */
public interface DownloadManager {
    void download(DownloadEntity entity);

    void resume(DownloadEntity entity);

    void pause(DownloadEntity entity);

    void stop(DownloadEntity entity);
}
