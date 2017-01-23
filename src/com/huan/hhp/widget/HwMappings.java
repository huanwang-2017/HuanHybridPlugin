package com.huan.hhp.widget;

import com.huan.hhp.xmlParser.HLayoutInflater;

import java.util.HashMap;

/**
 * Created by tjy on 2016/10/23.
 *
 * 映射对应表，tag -> className
 * 1.table.put("LinearLayout", "com.widget.LinearLayout");
 * 2.table.put("MyLayout", "com.mypackage.MyLayout")
 *
 * @see HLayoutInflater#getViewByTag(String)
 */
public class HwMappings {

    public static final String PKG = "com.huan.hhp";

    private final HashMap<String, String> mappingRefresh = new HashMap<String, String>();
    private final HashMap<String, String> widgetRefresh = new HashMap<String, String>();

    /* 添加映射 */{
        addMappingReference("include", PKG + ".widget.IncludeMapping");
        addMappingReference("script", PKG + ".widget.ScriptMapping");
        addMappingReference("PaperView", PKG + ".widget.PaperViewMapping");
        addMappingReference("HwImageView", PKG + ".widget.HwImageViewMapping");
    }

    public boolean has(String tag){
        return mappingRefresh.containsKey(tag);
    }

    public boolean has1(String tag){
        return widgetRefresh.containsKey(tag);
    }

    public String getMappingRefresh(String tag){
        return mappingRefresh.get(tag);
    }

    /**
     * 添加引用
     * @param key
     * @param value
     */
    public void addMappingReference(String key, String value){
        mappingRefresh.put(key, value);
    }

    /**
     * 删除引用
     * @param key
     */
    public void removeMappingReference(String key){
        mappingRefresh.remove(key);
        widgetRefresh.remove(key);
    }

    /**
     * 添加Widget引用
     * @param key
     * @param value
     */
    void addWidgetRefresh(String key, String value){
        widgetRefresh.put(key, value);
    }

    /**
     * 删除Widget引用
     * @param key
     */
    void removeWidgetRefresh(String key){
        widgetRefresh.remove(key);
    }

    public String getWidgetRefresh(String tag){
        return widgetRefresh.get(tag);
    }


    private static HwMappings instance;

    private HwMappings(){}
    public static HwMappings getSingleInstance(){
        if(instance == null)
            return instance = new HwMappings();
        return instance;
    }
}
