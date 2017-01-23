package com.huan.hhp.app;

/**
 * Created by tjy on 2016/11/18 0018.
 */
public class Resource extends HWPkg {
    private String name;
    private String value;
    private String ver = "";
    private boolean persistence;    // 持久化状态
    private long effectOfTime;  // 生效时间
    private long keepMilliseconds = -1; // 保留时常，只针对persistence==true

    public Resource(){}

    public Resource(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Resource(Resource src){
        this.name = src.name;
        this.value = src.value;
        this.ver = src.ver;
        this.persistence = src.persistence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        if(ver != null) {
            this.ver = ver;
        }
    }

    public boolean isPersistence() {
        return persistence;
    }

    public void setPersistence(boolean persistence) {
        this.persistence = persistence;
    }

    public long getEffectOfTime() {
        return effectOfTime;
    }

    public void setEffectOfTime(long effectOfTime) {
        this.effectOfTime = effectOfTime;
    }

    public long getKeepMilliseconds() {
        return keepMilliseconds;
    }

    public void setKeepMilliseconds(long keepMilliseconds) {
        this.keepMilliseconds = keepMilliseconds;
    }

    /**
     * 是否过期
     * @return
     */
    public boolean isOverdue(){
        if(keepMilliseconds == -1)
            return false;
        return System.currentTimeMillis() - effectOfTime >= keepMilliseconds;
    }

    /**
     * 提取内容
     * {name} = name
     * @param string
     * @return
     */
    public static String getYolk(String string){
        return string.substring(string.indexOf("{")+1, string.indexOf("}"));
    }

    /**
     * 是资源名称
     * @param string
     * @return
     */
    public static boolean isEgg(String string){
        return string.indexOf("{")!= -1 && string.indexOf("}")!= -1;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        if(o instanceof Resource) {
            Resource resource = (Resource) o;
            return resource.name.equals(name) && resource.ver.equals(ver);
        }
        return false;
    }
}
