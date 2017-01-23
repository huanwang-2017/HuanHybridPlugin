package com.huan.hhp.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.huan.hhp.H;
import com.huan.hhp.HRelease;
import com.huan.hhp.PluginActivity;
import com.huan.hhp.source.ReSourceUtil;
import com.huan.hhp.utils.IDUtil;
import com.huan.hhp.widget.ViewMapping;
import org.mozilla.javascript.Context;

/**
 * Created by tjy on 2016/12/6 0006.
 */
public class HHP extends Activity {

    public static final String TAG = DynamicActivity.class.getSimpleName();

    private Handler mHandler;
    private SilentlyJsChannel mSilentlyJsChannel;
    private View contentView;
    private IDUtil idUtil;
    private ReSourceUtil reSourceUtil; // 资源管理工具

    private H hUtil;

    public void onDestroy(){
        // 回收H工具
        if(hUtil != null){
            HRelease.releash(hUtil);
        }
        // 回收js
        if(mSilentlyJsChannel != null) {
            try {
                Context.exit();
            } catch (Exception e) {
            }
        }
        // 回收view
        if(contentView != null && contentView.getTag() instanceof ViewMapping) {
            ((ViewMapping) contentView.getTag()).loadDestroy();
        }
        // 回收plugin activity
        if(pluginActivity != null) {
            pluginActivity.onDestroy();
            pluginActivity = null;
        }
        super.onDestroy();
        System.gc();
    }

    /**
     *
     * @param contentView
     * @param doNothing 调用父类方法
     */
    protected void setContentView(View contentView, boolean doNothing){
        if(doNothing){
            super.setContentView(contentView);
        }
        else{
            this.setContentView(contentView);
        }
    }

    @Override
    public void setContentView(View contentView) {
        super.setContentView(this.contentView = contentView);
        if(hUtil != null){
            HRelease.releash(hUtil);
        }
        if(contentView.getTag() instanceof ViewMapping) {
            ViewMapping viewMapping = (ViewMapping) contentView.getTag();
            mSilentlyJsChannel = new SilentlyJsChannel(this);
            mSilentlyJsChannel.addJavaScriptInterface("context", viewMapping);
            mSilentlyJsChannel.addJavaScriptInterface("H", hUtil = new H(this).setmSilentlyJsChannel(mSilentlyJsChannel)); // 提供一个工具类
            if (pluginActivity != null) {
                hUtil.setApp_config(pluginActivity.getApp_config());
            }
            if (viewMapping != null) {
                viewMapping.loadCreated();
            }
            mSilentlyJsChannel.loadJs(mSilentlyJsChannel.baseJs);
        }
    }

    /*public void onUiAfter(){
        Log.i(TAG, "pluginActivity="+pluginActivity);
        if(pluginActivity != null) {
            pluginActivity.onUiAfter();
        }
    }*/

    public SilentlyJsChannel getSilentlyJsChannel() {
        return mSilentlyJsChannel;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public IDUtil getIdUtil() {
        return idUtil;
    }

    public ReSourceUtil getReSourceUtil() {
        return reSourceUtil;
    }

    public H getH() {
        return hUtil;
    }

    ///////////////// 网络接口 /////////////////////////
    private PluginActivity pluginActivity;
    public void setPluginActivity(PluginActivity pluginActivity, Intent intent) {
        this.pluginActivity = pluginActivity;
        this.pluginActivity.onCreate(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(pluginActivity != null) {
            pluginActivity.onResume();
        }
        if(mSilentlyJsChannel != null) {
            mSilentlyJsChannel.exFunction("onResume");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(pluginActivity != null) {
            pluginActivity.onPause();
        }
        if(mSilentlyJsChannel != null) {
            mSilentlyJsChannel.exFunction("onPause");
        }
    }


    //

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mSilentlyJsChannel != null) {
            mSilentlyJsChannel.exFunction("loadOnKeyDown", keyCode);
            Log.i(TAG, "keyIntercept=" + keyIntercept);
            if (keyIntercept) {
                return !(keyIntercept = false);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(mSilentlyJsChannel != null) {
            mSilentlyJsChannel.exFunction("loadOnKeyUp", keyCode);
            Log.i(TAG, "keyIntercept=" + keyIntercept);
            if (keyIntercept) {
                return !(keyIntercept = false);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 提供给js使用的事件返回接口
     * 详细参考
     * @see ViewMapping#onKeyResult
     */
    boolean keyIntercept;
    public void onKeyResult(boolean keyIntercept){
        this.keyIntercept = keyIntercept;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(pluginActivity != null) {
            if (event.getAction() == KeyEvent.ACTION_DOWN){
                if(pluginActivity.onKeyDown(event.getKeyCode(), event))
                    return true;
            }
            else if(event.getAction() == KeyEvent.ACTION_UP){
                if(pluginActivity.onKeyUp(event.getKeyCode(), event))
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(pluginActivity != null) {
            if (pluginActivity.dispatchTouchEvent(ev))
                return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(pluginActivity != null) {
            if (pluginActivity.onTouchEvent(event))
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if(pluginActivity != null) {
            if (pluginActivity.onKeyLongPress(keyCode, event))
                return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        if(pluginActivity != null) {
            if (pluginActivity.onKeyMultiple(keyCode, repeatCount, event))
                return true;
        }
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        if(pluginActivity != null) {
            if (pluginActivity.onKeyShortcut(keyCode, event))
                return true;
        }
        return super.onKeyShortcut(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(pluginActivity != null) {
            pluginActivity.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idUtil = new IDUtil();
        reSourceUtil = new ReSourceUtil(this);
        mHandler = new Handler(this.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 10086) {
                    contentView.setLayoutParams(contentView.getLayoutParams());
                }
            }
        };
    }
}
