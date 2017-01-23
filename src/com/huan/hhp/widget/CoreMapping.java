package com.huan.hhp.widget;

import android.content.Context;
import android.util.Log;
import com.huan.hhp.exception.TypeMismatchException;
import com.huan.hhp.utils.ReflexUtil;
import com.huan.hhp.widget.typeof.TypeOf;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/15.
 */
public abstract class CoreMapping extends HashMap<String, TypeOf> {
    final String TAG = CoreMapping.class.getSimpleName();

    /**
     * 被映射的对象
     * @return
     */
    protected abstract Object getObjTag();

    /**
     * 占位对象
     */
    protected class Placeholder {
        private String name;
        private Class[] argTypes;
        private Object[] args;

        public Placeholder(String name, Class ... argTypes) {
            this.name = name;
            this.argTypes = argTypes;
        }

        public Placeholder setArgs(Object ... args){
            this.args = args;
            return this;
        }
    }

    private final HashMap<String, Placeholder> plMap = new HashMap<String, Placeholder>(0);

    /**
     * 填充占位符
     * @param placeholder
     */
    public final void fillPlaceholder(Placeholder placeholder){
        plMap.put(placeholder.name, placeholder);
    }

    protected Context mContext;
    protected String name; // view在布局中的名称，如：Button，TextView ...

    /**
     *  页面是用的自定义对象，以前Android在View.setTag中保存，因为我们占用了View.setTag
     *  所以这块只能重新定义路径。请使用是注意不要随意对View调用setTag 而最好使用
     *  ((ViewMapping)view.getTag()).setTag(obj)
     */
    private Object tag;


    private String xmlValue;

    public CoreMapping(Context context, String name) {
        this.mContext = context;
        this.name = name;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    /**
     * 标签内容
     * @param xmlValue
     */
    public void setXmlValue(String xmlValue) {
        this.xmlValue = xmlValue;
    }

    /**
     * 获取标签内容
     * @return
     */
    public String getXmlValue() {
        return xmlValue;
    }

    protected void mapping(String from, TypeOf to){
        this.put(from, to);
    }

    protected void forTag(String tagClass){
        HwMappings.getSingleInstance().addWidgetRefresh(name, tagClass);
    }

    public void set(String name, String value) throws TypeMismatchException {
        if(this.containsKey(name)) {
            TypeOf typeOf = this.get(name);
            typeOf.set(this, getObjTag(), value);
        }
        else{
            this.set(getObjTag(), name, value, null);
        }
    }

    public void set(Object obj, String name, String value) throws TypeMismatchException {
        if(this.containsKey(name)) {
            TypeOf typeOf = this.get(name);
            typeOf.set(this, obj, value);
        }
        else{
            this.set(obj, name, value, null);
        }
    }

    public void set(Object obj, String name, String value, TypeOf typeOf) throws TypeMismatchException {
        Log.i(TAG, this.getClass().getSimpleName()+" set name="+name+", value="+value);
        if(name.contains(".")){
            int firstPoiIndex = name.indexOf(".");
            String firstName = name.substring(0, firstPoiIndex);
            String lastName = name.substring(firstPoiIndex + 1);
            if(firstName.equals("this")){
                set(this, lastName, value, typeOf);
            }
            else if(firstName.contains("${") && firstName.contains("}")){
                String methodName = firstName.substring(0, firstName.indexOf("("));
                String plName = name.substring(name.indexOf("${") + 2, name.indexOf("}"));
                Placeholder pl = plMap.get(plName);
                Log.i(TAG, "pl methodName="+methodName);
                Log.i(TAG, "pl name="+plName);
                Log.i(TAG, "pl ="+pl);
                try {
                    Method method = obj.getClass().getDeclaredMethod(methodName, pl.argTypes);
                    Object val = method.invoke(obj, pl.args);
                    set(val, lastName, value, typeOf);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            else {
                Field field = getDeclaredField(obj.getClass(), firstName);
                if(field != null) {
                    Log.i(TAG, "field");
                    field.setAccessible(true);
                    try {
                        set(field.get(obj), lastName, value, typeOf);
                    } catch (IllegalAccessException e) {
                        throw new TypeMismatchException("不能给"+field.getName()+"赋值："+value+" typeOf "+typeOf, e);
                    }
                }
                else{
                    Method method = ReflexUtil.getMethodByName(obj.getClass(), firstName);
                    if(method != null) {
                        Log.i(TAG, "method");
                        try {
                            Object rtnObj = method.invoke(obj);
                            set(rtnObj, lastName, value, typeOf);
                        } catch (IllegalAccessException e) {
                            throw new TypeMismatchException("不能给" + method.getName() + "赋值：" + value + " typeOf " + typeOf, e);
                        } catch (InvocationTargetException e) {
                            throw new TypeMismatchException("不能给" + method.getName() + "赋值：" + value + " typeOf " + typeOf, e);
                        }
                    }
                }
            }
        }
        else{
            if(name.contains("${") && name.contains("}")) {
                String methodName = name.substring(0, name.indexOf("("));
                String plName = name.substring(name.indexOf("${") + 2, name.indexOf("}"));
                Placeholder pl = plMap.get(plName);
                Log.i(TAG, "pl methodName=" + methodName);
                Log.i(TAG, "pl name=" + plName);
                Log.i(TAG, "pl =" + pl);
                try {
                    Method method = obj.getClass().getDeclaredMethod(methodName, pl.argTypes);
                    method.invoke(obj, pl.args);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return;
            }
            Field field = getDeclaredField(obj.getClass(), name);
            if(field != null) {
                field.setAccessible(true);
                if(typeOf != null) {
                    try {
                        field.set(obj, typeOf.convert(mContext, value, typeOf.type));
                    } catch (IllegalAccessException e) {
                        throw new TypeMismatchException("IllegalAccessException：[field] "+ field +"<"+value +", " +field +"typeOf " +typeOf, e);
                    }
                }
                else{
                    try {
                        field.set(obj, value);
                    } catch (IllegalAccessException e) {
                        throw new TypeMismatchException("IllegalAccessException：[field] "+ field +"<"+value +", " +field +"typeOf " +typeOf, e);
                    }
                }
            }
            else{
                Method method = getDeclaredMethod(obj.getClass(), name, typeOf!=null?typeOf.type:null);
                if(method != null) {
                    try {
                        if(typeOf != null) {
                            method.invoke(obj, typeOf.convert(mContext, value, method.getParameterTypes()[0]));
                        }
                        else{
                            method.invoke(obj, value);
                        }
                    } catch (IllegalAccessException e) {
                        throw new TypeMismatchException("IllegalAccessException：[method] "+ method +"<"+value +", " +method +"typeOf " +typeOf, e);
                    } catch (InvocationTargetException e) {
                        throw new TypeMismatchException("InvocationTargetException：[method]"+ method +"<"+value +", " +method +"typeOf " +typeOf, e);
                    }
                }
            }
        }
    }


    /**
     * 获取属性内容
     * @param name
     * @return
     * @throws TypeMismatchException
     */
    public Object attr(String name) throws TypeMismatchException {
        if(this.containsKey(name)) {
            TypeOf typeOf = this.get(name);
            return this.get(getObjTag(), typeOf.name);
        }
        else{
            return this.get(getObjTag(), name);
        }
    }

    public Object get(Object obj, String name) throws TypeMismatchException{
        if(name.contains(".")) {
            int firstPoiIndex = name.indexOf(".");
            String firstName = name.substring(0, firstPoiIndex);
            String lastName = name.substring(firstPoiIndex + 1);
            if (firstName.equals("this")) {
                return get(this, lastName);
            }
            else{
                Field field = getDeclaredField(obj.getClass(), firstName);
                if(field != null) {
                    field.setAccessible(true);
                    try {
                        return get(field.get(obj), lastName);
                    } catch (IllegalAccessException e) {
                        throw new TypeMismatchException("不能获取"+field.getName(), e);
                    }
                }
                else{
                    return get(ReflexUtil.execute(obj, name), lastName);
                }
            }
        }
        Field field = getDeclaredField(obj.getClass(), name);
        if(field != null) {
            field.setAccessible(true);
            try {
                return field.get(obj);
            } catch (IllegalAccessException e) {
                throw new TypeMismatchException("不能获取"+field.getName(), e);
            }
        }
        else{
            return ReflexUtil.execute(obj, name);
        }
    }




        /*TypeOf mappingTypeOf = this.get(name);
        String mappingName = mappingTypeOf.name;
        Class<?> mappingType = mappingTypeOf.type;
        if(mappingName.contains(".")){
            String[] splitName;
            if (this.containsKey(name)) {
                splitName = this.get(name).name.split("\\.");
            }else{
                splitName = new String[]{name};
            }
            for(int i = 0; i < splitName.length-1; i++) {
                String sName = splitName[i];
                System.out.println("sName="+sName);
                Field field = this.getDeclaredField(obj.getClass(), sName);
                if(field != null) {
                    field.setAccessible(true);
                    obj = field.get(obj);
                }else{
                    String setMethodName = "set"+sName.substring(0, 1).toUpperCase()+sName.substring(1);
                    System.out.println("setMethodName="+setMethodName);
                    Method setMethod = this.getDeclaredMethod(obj.getClass(), setMethodName, mappingType);
                    setMethod.invoke(obj, TypeOf.convert(value, mappingType));
                }
            }
            name = splitName[splitName.length-1];
        }
        System.out.println("obj="+obj);
        System.out.println("name="+name);
        Field field = this.getDeclaredField(obj.getClass(), name);
        if(field != null) {
            field.setAccessible(true);
            field.set(obj, TypeOf.convert(value, mappingType));
        }else{
            String setMethodName = "set"+name.substring(0, 1).toUpperCase()+name.substring(1);
            Method setMethod = this.getDeclaredMethod(obj.getClass(), setMethodName, mappingType);
            setMethod.invoke(obj, TypeOf.convert(value, mappingType));
        }*/



    Field getDeclaredField(Class<?> clz, String sName) {
        try {
            Field field = clz.getDeclaredField(sName);
            return field;
        } catch (NoSuchFieldException e) {
            if(clz.getSuperclass() != null) {
                if(clz.getSuperclass() != null) {
                    return getDeclaredField(clz.getSuperclass(), sName);
                }
                return null;
            }
            return null;
        }
    }

    /**
     * 根据参数类型获取方法
     * @param clz
     * @param name
     * @param paramterTypes
     * @return
     */
    Method getDeclaredMethod(Class<?> clz, String name, Class<?>... paramterTypes) {
        try {
            if(paramterTypes[0] != null) {
                Method detachMethod = clz.getDeclaredMethod(name, paramterTypes);
                return detachMethod;
            }
            else{
                return getDeclaredMethod(clz, name, 1);
            }
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clz.getSuperclass();
            if (superClass != null)
                return getDeclaredMethod(superClass, name, paramterTypes);
        }
        return null;
    }

    /**
     * 根据参数数量获取方法
     * @param clz
     * @param name
     * @param paramsSize
     * @return
     */
    Method getDeclaredMethod(Class<?> clz, String name, int paramsSize) {
        while(clz.getSuperclass() != null){
            Method[] methods = clz.getDeclaredMethods();
            for(Method method : methods){
                if(method.getName().equals(name)) {
                    if (method.getParameterTypes() == null && paramsSize == 0)
                        return method;
                    if (method.getParameterTypes().length == paramsSize) {
                        return method;
                    }
                }
            }
            clz = clz.getSuperclass();
        }
        Class<?> superClass = clz.getSuperclass();
        if (superClass != null)
            return getDeclaredMethod(superClass, name, paramsSize);
        return null;
    }

    public Context getContext() {
        return mContext;
    }

    //////
}
