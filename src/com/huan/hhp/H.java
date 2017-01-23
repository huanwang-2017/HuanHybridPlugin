package com.huan.hhp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.huan.hhp.app.App_config;
import com.huan.hhp.app.PluginInfo;
import com.huan.hhp.app.Resource;
import com.huan.hhp.common.DynamicActivity;
import com.huan.hhp.common.SilentlyJsChannel;
import com.huan.hhp.net.HttpTask;
import com.huan.hhp.utils.*;
import com.huan.hhp.widget.ViewMapping;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tjy on 2016/11/18 0018.
 * 和js交互的一个工具类
 */
public class H {
    private Context context;
    private Handler mHandler;
    private SilentlyJsChannel mSilentlyJsChannel;
    private App_config app_config;

    public App_config getApp_config() {
        return app_config;
    }

    public void setApp_config(App_config app_config) {
        this.app_config = app_config;
    }

    private boolean died;

    public H (Context context) {
        this.context = context;
        mHandler = new Handler(context.getMainLooper());
    }

    public H setmSilentlyJsChannel(SilentlyJsChannel mSilentlyJsChannel) {
        this.mSilentlyJsChannel = mSilentlyJsChannel;
        return this;
    }



    public interface Callback {
        void callback(Object obj);
    }

    /**
     * 异步执行
     * @param async method is run
     * @param args run方法参数
     * @param main method is run
     *<pre>
     * js代码：
     * H.asyncTask({
     *     run:function(){
     *         // 异步执行代码
     *              return obj;
     *     },
     *
     *     {},
     *
     *     run:function(){
     *         // UI线程执行代码，如果没有传递null或{}
     *     }
     * })
     *             </pre>
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void asyncTask(final ScriptableObject async, final Object[] args, final ScriptableObject main){
        AsyncTask<Void, Void, Object> asyncTask = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                Object result = ScriptableObject.callMethod(async, "run", args);
                return result;
            }

            @Override
            protected void onPostExecute(Object o) {
                if(!died) {
                    ScriptableObject.callMethod(main, "run", new Object[]{o});
                }
            }
        };
        asyncTask.execute();
    }

    /**
     * 异步执行
     * @param async method is run
     *
     *              <pre>
     * js代码：
     * H.asyncTask({
     *     run:function(a, b, c){
     *              // 异步执行带代码
     *              return obj;
     *     }
     * }, "参数1", "参数2", 3)
     *              <pre/>
     */
    public void asyncTask(final ScriptableObject async, final Object ... args){
        new Thread(){
            @Override
            public void run() {
                ScriptableObject.callMethod(async, "run", args);
            }
        }.start();
    }

    /**
     *
     * @param async method is run
     * @param main method is run
     *
     *             <pre>
     * js代码：
     * H.asyncTask({
     *     run:function(){
     *             // 异步执行代码
     *              return obj;
     *     }
     * },
     * {
     *    run:function(obj){}
     * })
     *             <pre/>
     */
    public void asyncTask(ScriptableObject async, ScriptableObject main){
        this.asyncTask(async, new Object[]{}, main);
    }

    /**
     * 异步请求
     * @param data
     * @param listener
     *
     * <pre>
     * js代码：
     * H.ajax(
     *      // 请求
     *  {
     *     method:'get',
     *     url:'http://www.baidu.com',
     *     body:null 或 function(){
     *
     *     }
     *     或 字符串 或 不填，
     *     contentType:"application/xml或 application/json"
     *
     *
     * },
     *
     *  {
     *      // 响应
     *      success:function（data）{
     *
     *      }，
     *      error:function(data){
     *
     *      }
     *  }
     *
     * )
     *<pre/>
     */
    public void ajax(ScriptableObject data, final ScriptableObject listener){
        HttpTask<String, String> httpTask = new HttpTask<String, String>() {
            @Override
            protected String onRequest(String requestBody) {
                return requestBody;
            }

            @Override
            protected String onResponse(String retnString) {
                return retnString;
            }

            @Override
            protected void onPost2Ui(int code, String arg) {
                if(!died) {
                    if (code == 200) {
                        callMethod(listener, "success", new Object[]{arg});
                    } else {
                        callMethod(listener, "error", new Object[]{arg});
                    }
                }
            }
        };
        Object method = callField(data, "method");
        Object contentType = callField(data, "contentType");
        Object b = ScriptableObject.getProperty(data, "body");
        Object body;
        if(b instanceof String){
            body = callField(data, "body");
        }
        else {
            body = callMethod(data, "body", new Object[]{});
        }
        Object url = callField(data, "url");
        if(method != null){
            httpTask.setRequestMethod(method.toString().toUpperCase());
        }
        if(contentType != null) {
            httpTask.setContentType(contentType.toString());
        }
        if(body != null) {
            httpTask.setRequestBody(contentType.toString());
        }
        if(url != null) {
            httpTask.setAddress(url.toString());
        }
        httpTask.setEnable();
        httpTask.start();
    }

    static synchronized Object callMethod(ScriptableObject obj, String method, Object[] args){
        if(ScriptableObject.hasProperty(obj, method)) {
            return ScriptableObject.callMethod(obj, method, args);
        }
        return null;
    }

    static synchronized Object callField(ScriptableObject obj, String method){
        if(ScriptableObject.hasProperty(obj, method)) {
            return ScriptableObject.getProperty(obj, method);
        }
        return null;
    }

    class LoopRunnable implements Runnable {

        String function;
        Object[] args;

        int loop;
        int loopCount;
        boolean died;
        long timeout;

        public LoopRunnable(int loopCount){
            this.loopCount = loopCount;
        }

        @Override
        public void run() {

        }

        public void setLoop(){
            loop++;
            if(loopCount != -1) {
                died = loop >= loopCount;
            }
        }

        public boolean isDied() {
            return died;
        }
    }

    private HashMap<String, LoopRunnable> loopQueue = new HashMap<String, LoopRunnable>();

    /**
     * 循环
     * @param function 被执行的方法名称
     * @param timeout 心跳时间
     * @param loopCount 循环次数
     * @param args 必要参数
     *
     *<pre>
     * js代码：
     * H.loop("timer", 1000, -1);
     * H.loop("timer", 1000, -1, new Date().getTime())
     *             <pre/>
     */
    public void loop(String function, long timeout, int loopCount, Object ... args){
        LoopRunnable loopRunnable;
        if(loopQueue.containsKey(function)){
            loopRunnable = loopQueue.get(function);
        }else{
            loopRunnable = new LoopRunnable(loopCount) {
                @Override
                public void run() {
                    if(loopQueue.containsKey(this.function)){
                        mSilentlyJsChannel.exFunction(this.function, this.args);
                    }
                    this.setLoop();
                    if(this.isDied()){
                        loopQueue.remove(this.function);
                        mHandler.removeCallbacks(this);
                    }
                    else{
                        mHandler.postDelayed(this, this.timeout);
                    }
                }
            };
            loopRunnable.function = function;
            loopRunnable.args = args;
            loopRunnable.timeout = timeout;
        }
        mHandler.postDelayed(loopRunnable, timeout);
        loopQueue.put(function, loopRunnable);
    }

    public void stopLoop(String function){
        loopQueue.remove(function);
    }

    public void sleep(long time){
        SystemClock.sleep(time);
    }

    /**
     * 打开主程序的Activity
     * @param action
     * @throws ClassNotFoundException
     *
     * <pre>
     * H.startActivity("main_layout") main_layout是配置文件中的name
     * H.startActivity("com.example.aaa.ui.MainActivity") // 跳转到系统Activity
     * H.startActivity("com_huan_action") // 跳转到action
     * <pre/>
     */
    public void startActivity(String action) {
        Resource resource;
        /*if(action.contains(".xml")){
            Intent intent = new Intent(context, DynamicActivity.class);
            intent.putExtra(Constants.ACTIVITY_NAME, action);
            context.startActivity(intent);
        }
        else */
        if((resource=getApp_config().getResource(action)) != null){
            Intent intent = new Intent(context, DynamicActivity.class);
            intent.putExtra(Constants.ACTIVITY_NAME, action);
            intent.putExtra("package", resource.getPackage());
            context.startActivity(intent);
        }
        else if(getApp_config().getActivityByName(action) != null){
            resource = getApp_config().getActivityByName(action).getLayout();
            Intent intent = new Intent(context, DynamicActivity.class);
            intent.putExtra(Constants.ACTIVITY_NAME, action);
            intent.putExtra("package", resource.getPackage());
            context.startActivity(intent);
        }
        else {
            try {
                context.startActivity(new Intent(context, context.getClassLoader().loadClass(action)));
            } catch (ClassNotFoundException e) {
                context.startActivity(new Intent(action));
            }
        }
    }

    /**
     * 获取资源
     * @param name
     * @return
     */
    public Resource getResource(String name){
        return app_config.getResource(name);
    }

    /**
     * 转换为分辨率单位
     * @param value
     * @return
     */
    public float rso(float value){
        return ResolutionUtil.dip2px(context, value);
    }

    /**
     * 获取插件的工作空间
     * @param pluginName
     * @return
     */
    public File getPluginWorkspace(String pluginName){
        PluginInfo pluginInfo = getApp_config().getPluginInfoByName(pluginName);
        return pluginInfo.getWorkspace();
    }

    /**
     * 获取插件对象，信息更全面
     * @param pluginName
     * @return
     */
    public PluginInfo getPluginInfoByName(String pluginName){
        return getApp_config().getPluginInfoByName(pluginName);
    }

    /**
     * 运行插件
     * @param pluginInfo
     */
    public void runPlugin(PluginInfo pluginInfo){
        app_config.getPluginManager().runPlugin(context, pluginInfo);
    }

    /**************************************************
     * 消息相关
     */

    /**
     * 弹出框
     * @param text
     */
    private List<Dialog> alertDialogQueue = new ArrayList<Dialog>();
    public void alert(String text){
        if(!died) {
            Dialog alertDialog = new AlertDialog.Builder(context).
                    setMessage(text).
                    create();
            alertDialog.show();
            alertDialogQueue.add(alertDialog);
        }
    }

    /**
     * 弹出Toast
     * @param text
     */
    public void toast(String text){
        if(!died) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    /*************************************************
     * Log相关
     */

    public void logI(String tag, String log){
        Log.i(tag, log);
    }

    public void logD(String tag, String log){
        Log.d(tag, log);
    }

    public void logE(String tag, String log){
        Log.e(tag, log);
    }

    public void print(String string){
        System.out.print(string);
    }

    public void println(String string){
        System.out.println(string);
    }


    /*************************************************
     * 按键相关
     */

    private HashMap<String, Integer> keyMappings = new HashMap<String, Integer>();
    {
        keyMappings.put("left", KeyEvent.KEYCODE_DPAD_LEFT);
        keyMappings.put("up", KeyEvent.KEYCODE_DPAD_UP);
        keyMappings.put("right", KeyEvent.KEYCODE_DPAD_RIGHT);
        keyMappings.put("down", KeyEvent.KEYCODE_DPAD_DOWN);
        keyMappings.put("center", KeyEvent.KEYCODE_DPAD_CENTER);
        keyMappings.put("enter", KeyEvent.KEYCODE_ENTER);
        keyMappings.put("back", KeyEvent.KEYCODE_BACK);
        keyMappings.put("menu", KeyEvent.KEYCODE_MENU);
    }

    /**
     * 得到按键值
     * 你可以使用简化名称获取到常用的一些按键值，
     * 当然也可以使用全称获取到对应的按键值。
     * @param name
     * @return
     */
    public int getEventCode(String name){
        if(keyMappings.containsKey(name)){
            return keyMappings.get(name);
        }
        return StaticReflexUtil.get(KeyEvent.class, name);
    }


    /*************************************************
     * Activity 相关
     */
    public void setTitle(String title){
        ((Activity)context).setTitle(title);
    }


    /*************************************************
     * 动画缩放 相关
     */

    /**
     * 放大动画
     * @param mapping View映射
     * @param ratio 放大比例
     * @param duration 用时
     */
    public void scale(ViewMapping mapping, float ratio, int duration){
       scale(mapping.getView(), ratio, duration);
    }

    /**
     * 放大动画 结束后通知
     * @param mapping View映射
     * @param ratio 放大比例
     * @param duration 用时
     * @param callback 回调函数
     *
     * <pre>
     * js代码：
     * var view1 = context.findViewById("v1");
     * H.scale(view1, 1.1f, 200, {
     *
     *      begin:fcuntion(){
     *
     *      }
     *      ,
     *      end:function(){
     *
     *      }
     * })
     * </>
     */
    public void scale(ViewMapping mapping, float ratio, int duration, ScriptableObject callback){
        scale(mapping.getView(), ratio, duration, callback);
    }

    /**
     * 放大动画
     * @param view View
     * @param ratio 放大比例
     * @param duration 用时
     */
    public void scale(View view, float ratio, int duration){
        scale(view, ratio, duration, null);
    }

    /**
     * 放大动画 结束通知
     * @param view View
     * @param ratio 放大比例
     * @param duration 用时
     * @param callback 回调函数
     */
    public void scale(View view, float ratio, int duration, final ScriptableObject callback){
        ScaleUtil.get(ratio, duration).scale(view, callback==null?null:new ScaleUtil.Callback() {
                        @Override
            public void begin() {
                callMethod(callback, "begin", new Object[]{});
            }

            @Override
            public void end() {
                callMethod(callback, "end", new Object[]{});
            }
        });
    }

    /**
     * 缩小动画
     * @param mapping View映射
     * @param ratio 缩小比例
     * @param duration  用时
     */
    public void shrink(ViewMapping mapping, float ratio, int duration){
        shrink(mapping.getView(), ratio, duration);
    }

    /**
     * 缩小动画
     * @param mapping   View映射
     * @param ratio 缩小比例
     * @param duration  用时
     * @param callback  回调函数
     */
    public void shrink(ViewMapping mapping, float ratio, int duration, ScriptableObject callback){
        shrink(mapping.getView(), ratio, duration, callback);
    }

    /**
     * 缩小动画
     * @param view  View
     * @param ratio 缩小比例
     * @param duration  用时
     */
    public void shrink(View view, float ratio, int duration){
        shrink(view, ratio, duration, null);
    }

    /**
     * 缩小动画
     * @param view  View
     * @param ratio 缩小比例
     * @param duration  用时
     * @param callback  回调函数
     */
    public void shrink(View view, float ratio, int duration, final ScriptableObject callback){
        ScaleUtil.get(ratio, duration).shrink(view, callback==null?null:new ScaleUtil.Callback() {
            @Override
            public void begin() {
                callMethod(callback, "begin", new Object[]{});
            }

            @Override
            public void end() {
                callMethod(callback, "end", new Object[]{});
            }
        });
    }

    /**
     * 创建动画工具
     * @param mapping
     * @return
     */
    public AnimatorUtil createAnimUtil(ViewMapping mapping){
        return createAnimUtil(mapping.getView());
    }

    /**
     * 创建动画工具
     * @param view
     * @return
     *
     * <pre>
     * js代码：
     * var animUtil = H.createAnimUtil(mapp1);
     * animUtil.layout(1, 10, 100, 100)
     *
     * animUtil.animate(1, 10, 100, 100, {
     *     begin:function(){
     *
     *     }
     * })
     * <pre/>
     */
    public AnimatorUtil createAnimUtil(View view){
        return new AnimatorUtil(view, context);
    }

    /**
     * 获取文件的MD5值
     * @param path
     * @return
     */
    public String getFileMD5(String path){
        File file = new File(path);
        if(file.exists()){
            return MD5Util.getMd5ByFile(file);
        }
        return null;
    }

    /**
     * 销毁
     */
    void release(){
        loopQueue.clear();
        died = true;
        int i = 0;
        int count = alertDialogQueue.size();
        Dialog dialog = null;
        for(; i < count; i++){
            dialog = alertDialogQueue.get(i);
            dialog.dismiss();
        }
        alertDialogQueue.clear();
    }
}
