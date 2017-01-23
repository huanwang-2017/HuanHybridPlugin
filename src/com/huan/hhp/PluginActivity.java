//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.huan.hhp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.huan.hhp.FileManager.Callback;
import com.huan.hhp.app.ActivityInfo;
import com.huan.hhp.app.App_config;
import com.huan.hhp.app.PluginInfo;
import com.huan.hhp.common.HHP;
import com.huan.hhp.source.ReSourceUtil;
import com.huan.hhp.utils.IDUtil;

public abstract class PluginActivity {
    final String TAG = PluginActivity.class.getSimpleName();
    private PluginInfo pluginInfo;
    private ActivityInfo activityInfo;
    private HHP openMeFrom;
    private Intent intent;
    protected IDUtil idUtil;
    private ReSourceUtil reSourceUtil;
    private FileManager fileManager;
    private App_config app_config;

    public App_config getApp_config() {
        return app_config;
    }

    public void setApp_config(App_config app_config) {
        this.app_config = app_config;
    }

    public PluginActivity(HHP openMeFrom, PluginInfo pluginInfo, ActivityInfo activityInfo) {
        this.openMeFrom = openMeFrom;
        this.pluginInfo = pluginInfo;
        this.activityInfo = activityInfo;
        this.idUtil = openMeFrom.getIdUtil();
        this.reSourceUtil = openMeFrom.getReSourceUtil();
        reSourceUtil.setPluginInfo(pluginInfo);
        this.fileManager = new FileManager(openMeFrom);
    }

    protected HHP getActivity() {
        return this.openMeFrom;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public PluginInfo getPluginInfo() {
        return this.pluginInfo;
    }

    public ActivityInfo getActivityInfo() {
        return this.activityInfo;
    }

    public void setContentView(String uri) {
        Log.i(this.TAG, "activityName=" + uri);
        PluginInfo pluginInfo;
        ActivityInfo activityInfo;
//        if(uri.contains(".xml")) {
//            pluginInfo = getApp_config().getPluginManager().getCurrentPlugInfo();
//            activityInfo = new ActivityInfo();
//            Resource resource = new Resource();
//            resource.setValue(uri);
//            activityInfo.setLayout(resource);
//            pluginInfo.setCurrentActivity(activityInfo);
//            this.fileManager.loadFile(pluginInfo, activityInfo, new Callback() {
//                public void onSuccess(View view) {
//                    Log.i(PluginActivity.this.TAG, "onSuccess "+ view);
//                    PluginActivity.this.openMeFrom.setContentView(view);
//                    PluginActivity.this.onUiAfter();
//                    PluginActivity.this.openMeFrom.getSilentlyJsChannel().exFunction("onReady", new Object[0]);
//                }
//            });
//        } else {
            Log.i(TAG, "getApp_config()="+getApp_config());
            pluginInfo = getApp_config().getPluginInfoByActivityName(uri);
            activityInfo = pluginInfo.getApplicationInfo().getActivity(uri);
            pluginInfo.setCurrentActivity(activityInfo);
            Log.i(this.TAG, "activityInfo=" + activityInfo);
            if(activityInfo.getLayout().getValue() == null){
                Log.i(TAG, "没有laoyout配置");
                return;
            }
            this.fileManager.loadFile(pluginInfo, activityInfo, new Callback() {
                public void onSuccess(View view) {
                    Log.i(PluginActivity.this.TAG, "onSuccess");
                    PluginActivity.this.openMeFrom.setContentView(view);
                    PluginActivity.this.onUiAfter();
                    //PluginActivity.this.openMeFrom.getSilentlyJsChannel().exFunction("onReady", new Object[0]);
                }
            });
//        }

    }

    public void setContentView(View view) {
        this.openMeFrom.setContentView(view);
    }

    public abstract void onUiAfter();

    public void onCreate(Intent intent) {
        this.intent = intent;
        Log.i(this.TAG, "onCreate " + this.pluginInfo);
        this.getPluginInfo().getApplication().add(this);
    }

    public void onResume() {
        Log.i(this.TAG, "onResume");
    }

    public void onPause() {
        Log.i(this.TAG, "onPause");
    }

    public void onDestroy() {
        Log.i(this.TAG, "onDestroy");
        this.fileManager.release();
        this.getPluginInfo().getApplication().remove(this);
        this.reSourceUtil = null;
        this.fileManager = null;
        this.app_config = null;
    }

    protected View findViewById(String id) {
        return this.openMeFrom.findViewById(this.openMeFrom.getIdUtil().getId(id));
    }

    protected String getString(String name) {
        return this.reSourceUtil != null?this.reSourceUtil.getValue(name):null;
    }

    protected Bitmap getDrawable(String name) {
        return this.reSourceUtil != null?this.reSourceUtil.getDrawable(name):null;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return false;
    }

    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        this.openMeFrom.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开主程序的Activity
     * @param action
     *
     * <pre>
     * H.startActivity("main_layout") main_layout是配置文件中的name
     * H.startActivity("com.example.aaa.ui.MainActivity") // 跳转到系统Activity
     * H.startActivity("com_huan_action") // 跳转到action
     * <pre/>
     */
    public void startActivity(String action){
        getH().startActivity(action);
    }

    public void startActivity(Intent intent) {
        this.openMeFrom.startActivity(intent);
    }

    public void startService(Intent service) {
        this.openMeFrom.startService(service);
    }

    /**
     * 工具类
     * @return
     */
    public H getH(){
        return openMeFrom.getH();
    }
}
