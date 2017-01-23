package com.huan.hhp.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by xuehuiniaoyu on 2015/5/27.
 * Android 快速选择工具
 * 主要用途：通过注册注解实现findViewById 等功能。
 *
 * @GetViewById(viewId) private Button myButton;  给myButton对象赋值，其中viewId是配置文件中的 @+id
 *
 * @OnClick(R.id.myButton) [OnLongClick， OnFocusChange]
 * public void viewClick(View view){
 *
 * }
 *
 */
public class ViewSelector {
    public static String TAG = ViewSelector.class.getSimpleName();

    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetViewById {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface OnClick {
        String value() default "@null";
        String mapping() default "@null";
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface OnLongClick {
        String value() default "@null";
        String mapping() default "@null";
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface OnFocusChange {
        String value() default "@null";
        String mapping() default "@null";
    }

    private static volatile ViewSelector mInstance;
    static{
        mInstance = new ViewSelector();
    }
    /**
     * 注解隶属的父类，该类中包含一些执行注解，比如GetViewById, OnClick, OnLongClick ...
     * 这些注解可以通过java反射找到
     */
    private Object annotationsFrom;
    /**
     * 注解注释的元属性或元方法的父类对象或者是可以被执行注解元方法的对象
     *
     * @GetViewById(R.id.button1)
     * private Button button1;
     *
     * 这里target 指的是能够通过findViewById方法找到target对象的类
     *
     */
    private Object target;

    private IDUtil idUtil;
    public ViewSelector setIdUtil(IDUtil idUtil) {
        this.idUtil = idUtil;
        return this;
    }

    /* package 解释注解 */ void execute(){
        if(mInstance.annotationsFrom == null){
            throw new UnsupportedOperationException("annotationsFrom 没有被赋值　！");
        }
        if(mInstance.target == null){
            throw new UnsupportedOperationException("target 没有被赋值　！");
        }
        Class<?> contextClass = annotationsFrom.getClass();
        Field[] fields = contextClass.getDeclaredFields();
        // 检查属性中的注解
        for(Field field : fields){
            // 根据Id获取View对象
            Annotation annotations_GetViewById = field.getAnnotation(GetViewById.class);
            if(annotations_GetViewById != null){
                GetViewById getViewByIdAnnotation = (GetViewById) annotations_GetViewById;
                String viewId = getViewByIdAnnotation.value();
                View getView = getView(idUtil.getId(viewId));
                if(getView == null || getView.getClass() != field.getType()){
                    Log.e(TAG, "无法为：" + field.getType() + " 赋值：" + getView);
                }else {
                    field.setAccessible(true);
                    try {
                        field.set(annotationsFrom, getView);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // 检查方法中的注解
        Method[] methods = contextClass.getDeclaredMethods();
        for(final Method method : methods){
            // 点击事件
            Annotation annotation_OnClick = method.getAnnotation(OnClick.class);
            if(annotation_OnClick != null){
                OnClick onClickAnnotation = (OnClick) annotation_OnClick;
                String viewId = onClickAnnotation.value();
                View getView = null;
                if("@null".equals(onClickAnnotation.mapping())) {
                    getView = getView(idUtil.getId(viewId));
                }else{
                    getView = (View) getValueByField(onClickAnnotation.mapping());
                }
                if(getView != null) {
                    getView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            executeMethod(method, v);
                        }
                    });
                }
            }
            // 长按事件
            Annotation annotation_OnLongClick = method.getAnnotation(OnLongClick.class);
            if(annotation_OnLongClick != null){
                OnLongClick onLongClickAnnotation = (OnLongClick) annotation_OnLongClick;
                String viewId = onLongClickAnnotation.value();
                View getView = null;
                if("@null".equals(onLongClickAnnotation.mapping())) {
                    getView = getView(idUtil.getId(viewId));
                }else{
                    getView = (View) getValueByField(onLongClickAnnotation.mapping());
                }
                if(getView != null) {
                    getView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            boolean result = (Boolean) executeMethod(method, v);
                            return result;
                        }
                    });
                }
            }
            // 焦点事件
            Annotation annotation_OnFocusChange = method.getAnnotation(OnFocusChange.class);
            if(annotation_OnFocusChange != null){
                OnFocusChange onFocusAnnotation = (OnFocusChange) annotation_OnFocusChange;
                String viewId = onFocusAnnotation.value();
                View getView = null;
                if("@null".equals(onFocusAnnotation.mapping())) {
                    getView = getView(idUtil.getId(viewId));
                }else{
                    getView = (View) getValueByField(onFocusAnnotation.mapping());
                }
                if(getView != null) {
                    getView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            executeMethod(method, v, hasFocus);
                        }
                    });
                }
            }
        }
    }

    /** 根据Id查找View **/
    View getView(int viewId){
        if(target instanceof Activity){
            return ((Activity) target).findViewById(viewId);
        }
        else if(target instanceof View){
            return ((View) target).findViewById(viewId);
        }
        return null;
    }

    /** 获取元数据内容 **/
    Object getValueByField(String fieldName){
        Object obj = null;
        Class<?> contextClass = annotationsFrom.getClass();
        try {
            Field field = contextClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            obj = field.get(annotationsFrom);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "无法从 " + contextClass + " 获取 " + fieldName);
        } catch (IllegalAccessException e) {
            Log.e(TAG, fieldName + " get 错误");
        } finally{
            return obj;
        }
    }

    /** 执行方法 **/
    Object executeMethod(Method method, Object ... objects){
        Object result = null;
        method.setAccessible(true);
        try {
            result = method.invoke(annotationsFrom, objects);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "方法 "+method.getName()+ " 执行错误 "+e.getLocalizedMessage());
        } catch (InvocationTargetException e) {
            Log.e(TAG, "方法 "+method.getName()+ " 执行错误 "+e.getLocalizedMessage());
        } finally {
            return result;
        }
    }

    public static ViewSelector setAnnotationsFrom(Object annotationsFrom) {
        if(mInstance == null){
            mInstance = new ViewSelector();
        }
        mInstance.annotationsFrom = annotationsFrom;
        return mInstance;
    }

    public static ViewSelector select(Object target){
        mInstance.target = target;
        mInstance.execute();
        return mInstance;
    }

    public void release(){
        mInstance = null;
    }
}
