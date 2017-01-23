package com.huan.hhp.xmlParser;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.huan.hhp.exception.TypeMismatchException;
import com.huan.hhp.app.PluginInfo;
import com.huan.hhp.widget.HwMappings;
import com.huan.hhp.widget.ViewMapping;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * Created by tjy on 2016/10/16.
 * 布局解析器
 */
public class HLayoutInflater {
    private Context mContext;
    private PluginInfo pluginInfo;
    private ViewMapping rootMapping;
    HXmlPullParser xmlPullParser;

    /**
     * 获取对象
     * @param context
     * @return
     */
    public static HLayoutInflater from(Context context){
        return new HLayoutInflater(context);
    }

    public HLayoutInflater(Context mContext) {
        this.mContext = mContext;
        xmlPullParser = new HXmlPullParser();
    }

    /*public Element load(InputStream inputStream) throws DocumentException {
        SAXReader reader = new SAXReader();
        document = reader.read(inputStream);
        return document.getRootElement();
    }

    public Element load(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        document = reader.read(url);
        return document.getRootElement();
    }


    ViewMapping bindView(Element element, ViewMapping parentMapping) throws TypeMismatchException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ViewMapping viewMapping = getViewMapping(element.getName());
        View tagView = getViewByTag(element.getName());
        viewMapping.setView(tagView);
        Log.i("L2P", element.getName()+" mapping:"+viewMapping+", view:"+tagView);
        if(parentMapping == null) {
            try {
                String tagLayoutClassName = tagView.getClass().getName()+"$LayoutParams";
                Class<?> clz = tagView.getClass().getClassLoader().loadClass(tagLayoutClassName);
                ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) clz.getConstructor(int.class, int.class).newInstance(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                tagView.setLayoutParams(lp);
            } catch (ClassNotFoundException e) {
                tagView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }else{
            View parentView = parentMapping.getView();
            String parentLayoutClassName = parentView.getClass().getName()+"$LayoutParams";
            try {
                Class<?> clz = parentView.getClass().getClassLoader().loadClass(parentLayoutClassName);
                ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) clz.getConstructor(int.class, int.class).newInstance(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tagView.setLayoutParams(lp);
            } catch (ClassNotFoundException e){
                tagView.setLayoutParams(new ViewGroup.LayoutParams(parentView.getLayoutParams()));
            }
//            tagView.setLayoutParams(new ViewGroup.LayoutParams(parentView.getLayoutParams()));
            *//*String paramsClassName = parentView.getClass().getName()+".LayoutParams";
            System.out.println("paramsClassName="+paramsClassName);
            Class<?> paramsClass = mContext.getClassLoader().loadClass(paramsClassName);
            Object params = paramsClass.getConstructor(int.class, int.class).newInstance(-1, -1);
            tagView.setLayoutParams((ViewGroup.LayoutParams)params);*//*
        }
        // 遍历子元素
        List<Element> childs = element.elements();
        if(childs.size() > 0){
            for(Element element1 : childs) {
                ((ViewGroup)tagView).addView(bindView(element1, viewMapping).getView());
            }
        }
        // 配置参数
        mapping(viewMapping, element);
        return viewMapping;
    }*/

    public View inflater(InputStream inputStream, PluginInfo pluginInfo) throws IOException, XmlPullParserException {
        this.pluginInfo = pluginInfo;
        release();
        parse(inputStream);
        return rootMapping.getView();
    }

    public View inflater(URL url, PluginInfo pluginInfo) throws IOException, XmlPullParserException {
        Log.i("HLayoutInflater", "url="+url);
        this.pluginInfo = pluginInfo;
//            Element element = load(url);
        release();
        parse(url.openStream());
        return rootMapping.getView();
    }




    private void parse(InputStream inputStream) throws IOException, XmlPullParserException {
        xmlPullParser.setOnXmlPullParserListener(new HXmlPullParser.OnXmlPullParserListener() {
            @Override
            public void onBegin(HXmlPullParser.Element element) {
                try {
                    ViewMapping viewMapping = getViewMapping(element.getName());
                    viewMapping.setPackage(pluginInfo.getPackage());
                    View tagView = getViewByTag(element.getName());
                    try {
                        String tagLayoutClassName = tagView.getClass().getName()+"$LayoutParams";
                        Class<?> clz = tagView.getClass().getClassLoader().loadClass(tagLayoutClassName);
                        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) clz.getConstructor(int.class, int.class).newInstance(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        tagView.setLayoutParams(lp);
                    } catch (Exception e) {
                        tagView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                    viewMapping.setView(tagView);
                    element.setObj(viewMapping);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onEnd(HXmlPullParser.Element parent, HXmlPullParser.Element element) {
                ViewMapping viewMapping = element.getObj();
                View tagView = viewMapping.getView();
                if(parent != null){
                    ViewMapping parentMapping = parent.getObj();
                    View parentView = parentMapping.getView();
                    String parentLayoutClassName = parentView.getClass().getName()+"$LayoutParams";
                    try {
                        Class<?> clz = parentView.getClass().getClassLoader().loadClass(parentLayoutClassName);
                        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) clz.getConstructor(int.class, int.class).newInstance(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        tagView.setLayoutParams(lp);
                    } catch (Exception e){
                        tagView.setLayoutParams(new ViewGroup.LayoutParams(parentView.getLayoutParams()));
                    }
                    ((ViewGroup) parentView).addView(tagView);
                }
                try {
                    mapping(viewMapping, element);
                } catch (TypeMismatchException e) {
                    e.getE().printStackTrace();
                }
                if(parent == null){
                    rootMapping = viewMapping;
                }
            }
        });
        xmlPullParser.load(inputStream);
    }


    View getViewByTag(String tag) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String widgetClassName;
        if(HwMappings.getSingleInstance().has1(tag)){
            widgetClassName = HwMappings.getSingleInstance().getWidgetRefresh(tag);
        }
        else {
            widgetClassName = "android.widget." + tag;
        }
        Class<?> widgetClz;
        if(pluginInfo == null){
            widgetClz = mContext.getClassLoader().loadClass(widgetClassName);
        }
        else {
            try {
                widgetClz = pluginInfo.getClassLoader().loadClass(widgetClassName);
            } catch (ClassNotFoundException e) {
                widgetClz = mContext.getClassLoader().loadClass(widgetClassName);
            }
        }
        return (View) widgetClz.getConstructor(Context.class).newInstance(mContext);
    }

    ViewMapping getViewMapping(String tag) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String widgetClassName;
        if(HwMappings.getSingleInstance().has(tag)){
            widgetClassName = HwMappings.getSingleInstance().getMappingRefresh(tag);
        }
        else{
            widgetClassName = HwMappings.PKG + ".widget."+tag+"Mapping";
        }
        Class<?> widgetClz;
        if(pluginInfo == null){
            widgetClz = mContext.getClassLoader().loadClass(widgetClassName);
        }else {
            try {
                widgetClz = pluginInfo.getClassLoader().loadClass(widgetClassName);
            } catch (ClassNotFoundException e) {
                widgetClz = mContext.getClassLoader().loadClass(widgetClassName);
            }
        }
        return (ViewMapping) widgetClz.getConstructor(Context.class, String.class).newInstance(mContext, tag);
    }

    void mapping(ViewMapping viewMapping, HXmlPullParser.Element element) throws TypeMismatchException {
        HXmlPullParser.Attribute[] attributes = element.getAttributes();
        int attrCount = attributes.length;
        int i = 0;
        HXmlPullParser.Attribute attribute;
        for(; i < attrCount; i++){
            attribute = attributes[i];
            String attrName;
            if(attribute.getName().contains("android:")) {
                attrName = attribute.getName().substring("android:".length());
            }else{
                attrName = attribute.getName();
            }
            String attrValue = attribute.getValue();
            Log.i("HLayoutInflater", "name=" + attrName + ", value=" + attrValue);
            mKeyValue.name = attrName;
            mKeyValue.value = attrValue;
            intercept(viewMapping, mKeyValue);
            viewMapping.set(mKeyValue.name, mKeyValue.value);
        }
        viewMapping.setXmlValue(element.getText());
    }

    public static class KeyValue {
        private String name;
        private String value;

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
    }

    private KeyValue mKeyValue
            = new KeyValue();

    /**
     * 拦截并修改内容
     * @param keyValue
     */
    public void intercept(ViewMapping viewMapping, KeyValue keyValue){

    }

    public String getXML(){
        String xml = xmlPullParser.asXml();
        return xml;
    }

    public ViewMapping getRootMapping() {
        return rootMapping;
    }

    public static class ParseViewException extends Exception {
        private Class exClz;
        private String exString;
        private Exception ex;

        public ParseViewException(Class exClz, String exString, Exception ex) {
            this.exClz = exClz;
            this.exString = exString;
            this.ex = ex;
            ex.printStackTrace();
        }

        public Class getExClz() {
            return exClz;
        }

        public String getExString() {
            return exString;
        }

        public Exception getEx() {
            return ex;
        }
    }

    void release(){
        if(rootMapping != null){
            rootMapping.release();
        }
    }
}
