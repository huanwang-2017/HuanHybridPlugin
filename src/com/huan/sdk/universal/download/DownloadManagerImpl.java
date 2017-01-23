package com.huan.sdk.universal.download;

import java.util.*;

/**
 * Created by Administrator on 2016/10/12.
 */
public class DownloadManagerImpl extends Observable implements DownloadManager {
    private DownloadTask downloadTask;
    private int notifyObserversRate = 1000; // 上报UI的心跳时间
    private Timer mTimer;

    private final List<DownloadEntity> mQueue = new ArrayList<DownloadEntity>(0) {
        @Override
        public boolean add(DownloadEntity entity) {
            if (!this.contains(entity)) {
                return super.add(entity);
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return super.remove(o);
        }
    };

    /* 下载下一个任务 */ void doNext() {
        if(mQueue.size() > 0) {
            DownloadEntity entity0 = mQueue.get(0);
            if (entity0.getState().getState() == DownloadState.STATE_PAUSE) {
                resume(entity0);
            } else {
                download(entity0);
            }
        }
    }

    @Override
    public void download(DownloadEntity entity) {
        entity.getState().setState(DownloadState.STATE_DOWNLOAD);
        mQueue.add(entity);
        DownloadManagerImpl.this.downloadTask.setObservable(DownloadManagerImpl.this);
        notifyObservers(entity);
        if (!downloadTask.isRunning()) {
            downloadTask.setDownloadEntity(entity);
            new Thread(downloadTask).start();
            verifyReady();
        }
    }

    @Override
    public void resume(DownloadEntity entity) {
        entity.getState().setState(DownloadState.STATE_RESUME);
        mQueue.add(entity);
        DownloadManagerImpl.this.downloadTask.setObservable(DownloadManagerImpl.this);
        notifyObservers(entity);
        if (!downloadTask.isRunning()) {
            downloadTask.setDownloadEntity(entity);
            new Thread(downloadTask).start();
            verifyReady();
        }
    }

    @Override
    public void pause(DownloadEntity entity) {
        entity.getState().setState(DownloadState.STATE_PAUSE);
        mQueue.remove(entity);
        downloadTask.setRunning(false);
        notifyObservers(entity);
    }

    @Override
    public void stop(DownloadEntity entity) {
        entity.getState().setState(DownloadState.STATE_STOP);
        mQueue.remove(entity);
        downloadTask.setRunning(false);
        notifyObservers(entity);
    }

    void verifyReady() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                notifyObservers(DownloadManagerImpl.this.downloadTask.getDownloadEntity());
            }
        }, 0, notifyObserversRate);
    }

    public DownloadTask getDownloadTask() {
        return downloadTask;
    }

    public void setDownloadTask(DownloadTask downloadTask) {
        this.downloadTask = downloadTask;
    }

    public int getNotifyObserversRate() {
        return notifyObserversRate;
    }

    public void setNotifyObserversRate(int notifyObserversRate) {
        this.notifyObserversRate = notifyObserversRate;
    }


    @Override
    public void notifyObservers(Object arg) {
        switch (((DownloadEntity) arg).getState().getState()) {
            case DownloadState.STATE_SUCCESS:
            case DownloadState.STATE_FAIL:
            case DownloadState.STATE_STOP:
            case DownloadState.STATE_PAUSE:
                mTimer.cancel();
                mQueue.remove(downloadTask.getDownloadEntity()); // 删除上一个下载任务
                downloadTask.setObservable(null);
                doNext(); // 执行下一个下载任务
        }
        setChanged();
        super.notifyObservers(arg);
    }
}
