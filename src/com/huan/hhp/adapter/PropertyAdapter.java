package com.huan.hhp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.huan.hhp.app.PluginInfo;
import com.huan.hhp.app.Resource;
import com.huan.hhp.utils.ReflexUtil;
import com.huan.hhp.widget.ViewMapping;
import com.huan.hhp.xmlParser.HLayoutInflater;
import com.huan.sdk.huanpay4.util.Log;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName: PropertyAdapter
 * @Description: 属性适配器
 * @author tjy
 */
public class PropertyAdapter<V> extends CommonAdapter<V> {
    private HLayoutInflater layoutInflater; // 工具类
    private InputStream layout;
    private PluginInfo pluginInfo;

    private int currPosition;
    private HashMap<ViewMapping, List<HLayoutInflater.KeyValue>> cache = new HashMap<ViewMapping, List<HLayoutInflater.KeyValue>>();

    public PropertyAdapter(Context context, String layout, PluginInfo pluginInfo) {
        this(context, new ByteArrayInputStream(layout.getBytes()), pluginInfo);
    }

    public PropertyAdapter(Context context, InputStream layout, PluginInfo pluginInfo) {
        super(context);
        this.layout = layout;
        this.pluginInfo = pluginInfo;
        layoutInflater = new HLayoutInflater(context){
            @Override
            public void intercept(ViewMapping viewMapping, KeyValue keyValue) {
                V v = getItem(currPosition);
                if(Resource.isEgg(keyValue.getValue())){
//                    String field = Resource.getYolk(keyValue.getValue());
//                    String getMethod = ""+ ReflexUtil.execute(v, "get"+field.substring(0, 1).toUpperCase()+field.substring(1));
//                    keyValue.setValue(getMethod);

                    keyValue.setValue(getString(v, keyValue.getValue()));
                }
                List<KeyValue> list;
                if(!cache.containsKey(viewMapping)){
                    list = new ArrayList<KeyValue>();
                }
                else{
                    list = cache.get(viewMapping);
                }
                if(!list.contains(keyValue)) {
                    list.add(keyValue);
                }
                cache.put(viewMapping, list);
            }
        };
    }

    /**
     * 比如：hello world${text} name=${name}
     * @param obj
     * @param value
     * @return
     */
    public static String getString(Object obj, String value){
        String[] arr = value.split("\\$");
        String begin = "";
        for(String string : arr){
            if(Resource.isEgg(string)){
                String field = Resource.getYolk(string);
                String getMethodResult = ""+ ReflexUtil.execute(obj, "get"+field.substring(0, 1).toUpperCase()+field.substring(1));
                begin += getMethodResult;
                if(string.indexOf("}") < string.length()-1) {
                    begin += string.substring(string.indexOf("}") + 1);
                }
            }
            else {
                begin += string;
            }
        }
//        Log.i(PropertyAdapter.class.getSimpleName(), "getString:"+begin + "    "+value);
        return begin;
    }

    String layoutXml;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        currPosition = position;
        if(convertView == null){
            if(layoutXml == null) {
                try {
                    convertView = layoutInflater.inflater(layout, pluginInfo);
                    layoutXml = layoutInflater.getXML();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            else{
                InputStream inputStream = new ByteArrayInputStream(layoutXml.getBytes());
                try {
                    convertView = layoutInflater.inflater(inputStream, pluginInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            V v = getItem(currPosition);
            ViewMapping viewMapping = (ViewMapping) convertView.getTag();
            if(cache.containsKey(viewMapping)){
                List<HLayoutInflater.KeyValue> list = cache.get(viewMapping);
                for(HLayoutInflater.KeyValue keyValue : list){
                    layoutInflater.intercept(viewMapping, keyValue);
                }
            }
        }
        convertView.setLayoutParams(getLayoutParamsFromParent(parent.getClass()));
        return convertView;
    }

    ViewGroup.LayoutParams getLayoutParamsFromParent(Class clz){
        try {
            Class lpClz = Class.forName(clz.getName()+"$LayoutParams");
            try {
                ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) lpClz.getConstructor(int.class, int.class).newInstance(-1, -1);
                return lp;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            if(clz.getSuperclass() != null){
                return getLayoutParamsFromParent(clz.getSuperclass());
            }
        }
        return null;
    }
}
