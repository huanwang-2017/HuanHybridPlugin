package com.huan.sdk.universal.download;

/**
 * Created by Administrator on 2016/10/12.
 */
public class DownloadState {
    public static final int STATE_DEFAULT = -1;
    public static final int STATE_DOWNLOAD = 0; // 下载中
    public static final int STATE_PAUSE = 1; // 下载中
    public static final int STATE_RESUME = 2;
    public static final int STATE_STOP = 3;
    public static final int STATE_DOING = 4;
    public static final int STATE_SUCCESS = 5;
    public static final int STATE_FAIL = 6;
    public static final int STATE_READY = 200; // 准备就绪，开始下载


    private int state = STATE_DEFAULT;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
