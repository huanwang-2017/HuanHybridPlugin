package com.huan.hhp.utils;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016/10/20.
 */
public class StaticReflexUtil {
    public static <T> T get(Class<?> clz, String fieldName) {
        try {
            Field field = clz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(clz);
        } catch (NoSuchFieldException e) {
            if(clz.getSuperclass() != null)
                return get(clz.getSuperclass(), fieldName);
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}
