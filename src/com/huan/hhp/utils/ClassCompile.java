package com.huan.hhp.utils;

import android.content.Context;
import android.util.Log;
import dalvik.system.PathClassLoader;

import java.io.*;

/**
 * Created by Administrator on 2016/10/23.
 */
public class ClassCompile {
    private Context context;

    private String javaCode;
    private String packageName;
    private File workspace;
    private File localFile;

    public ClassCompile(Context context) {
        this.context = context;
        workspace = new File(context.getCacheDir(), "class_plug");
    }

    public void save(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(javaCode.getBytes("utf-8"))));
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(localFile));
            while (true) {
                String str = reader.readLine();
                if (str != null) {
                    printWriter.println(str);
                } else {
                    break;
                }
            }
            reader.close();
            printWriter.flush();
            printWriter.close();
        }catch (Exception e){
            String msg = ErrorUtil.e(e);
            Log.i("ClassCompile", "save fail msg=" + msg);
        }
    }

    public Class<?> compile(String javaCode, String packageName, String className) throws ClassNotFoundException {
        this.javaCode = javaCode;
        this.packageName = packageName;
        File pkg = new File(workspace, packageName.replace(".", "/"));
        if(!pkg.exists()) {
            pkg.mkdirs();
        }
        try {
            Runtime.getRuntime().exec("chmod 777 " + pkg);
        } catch (Exception e) {
            String msg = ErrorUtil.e(e);
            Log.i("ClassCompile", "chmod fail msg=" + msg);
        }
        localFile = new File(pkg, className);
        if(!localFile.exists()) {
            save();
        }
        try {
            Runtime.getRuntime().exec("adb shell");
            String str = "javac "+pkg.getAbsolutePath()+"/"+className;
            Log.i("CC", "str="+str);
            Runtime.getRuntime().exec(str);

        }catch (Exception e){
            e.printStackTrace();
        }
        PathClassLoader classLoader = new PathClassLoader(workspace.getAbsolutePath(), context.getClassLoader().getParent());
        return classLoader.loadClass(packageName+"."+className.replace(".java", ""));
    }


    /*public static void main(String[] args) throws Exception {
        Process p = Runtime.getRuntime().exec("cmd");//建议使用cmd
        OutputStream out = p.getOutputStream();
        out.write("c:\r\n".getBytes());// 切换到C盘
        out.write("cd Users\\Administrator\\Desktop\r\n".getBytes());// 切换目录到桌面
        out.write("javac Test.java\r\n".getBytes());// 执行你原来的那个命令
        out.write("java Test\r\n".getBytes());// 执行java Test，因为用的同一个cmd进程
        //所以cmd里边的目录没变，执行java和javac时就不用重复设定路径。
        // 这里还可以用start java Test这样就能将Test从另一个java虚拟机中启动，不过也就得不到InputStram了
        out.flush();
    }*/
}
