package com.huan.hhp;


import android.util.Log;

/**
 * Created by tjy on 2016/12/22 0022.
 * 服务是运行在后台的异步任务，每一个服务都拥有一个管理员（也就是说：谁启动了一个服务，那么这个服务就由谁关闭。）
 * 添加管理员的目的就可以更加合理的控制了服务的权限和生命周期。
 *
 * 比如：
 * HwService service = new HwService();
 * service.start("admin");
 * // 必须使用"admin"来停止服务
 * service.stop("admin");
 */
public class HwService implements Runnable {

    private HwServiceManager mServiceManager; // 服务管理器

    private Object administrator;
    private Object administratorMistress;
    private boolean running;

    public HwService(HwServiceManager manager) {
        this.mServiceManager = manager;
    }

    /**
     * 启动服务 <br/>
     * 一个管理员只能启动一个服务。
     * @param administrator
     */
    public final void start(Object administrator){
        if(!running && !mServiceManager.hwServices.containsKey(administrator)) {
            this.administratorMistress = administrator;
            new Thread(this).start();
        }
    }

    /**
     * 停止服务
     * @param administrator
     */
    public final void stop(Object administrator){
        if(administrator == this.administrator) {
            if (running) {
                onDestroy();
                mServiceManager.hwServices.remove(this.administrator);
                this.running = false;
                this.administrator = null;
                this.mServiceManager = null;
                Log.d(HwService.class.getSimpleName(), "服务停止！");
            }
        }
    }

    /**
     * 如果有特殊要求，关闭服务不是在插件关闭后关闭，那么请重写该方法。
     */
    public void stopSelf() {
        stop(this.administrator);
    }

    @Override
    public void run() {
        if(!mServiceManager.isAlive()){
            return;
        }
        // 小三上位
        if(this.administratorMistress != null){
            this.administrator = this.administratorMistress;
            this.administratorMistress = null;
        }
        else{
            throw new UnsupportedOperationException("没有设置启动服务的 administrator ！");
        }
        if(running || administrator == null) {
            Log.e(HwService.class.getSimpleName(), "服务必须调用 start 方法启动！");
            return;
        }
        running = true;
        onCreate();
        mServiceManager.hwServices.put(this.administrator, this);
        Log.d(HwService.class.getSimpleName(), "服务启动！");
        onRun();
    }

    /**
     * 启动
     */
    protected void onCreate() {

    }

    /**
     * 执行
     */
    protected void onRun() {

    }

    /**
     * 结束
     */
    protected void onDestroy() {

    }

    /**
     * 是否运行中
     * @return
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * 管理员
     * @return
     */
    public Object getAdministrator() {
        return administrator;
    }
}
