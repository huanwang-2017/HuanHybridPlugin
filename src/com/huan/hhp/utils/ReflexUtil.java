package com.huan.hhp.utils;

import java.lang.reflect.Method;

/**
 * 反射执行类
 * 主要用途：通过反射调用对象的方法。
 */
public class ReflexUtil {
	public static Object execute(Object obj, String name, Class<?>[] types,
			Object[] values) {
		try {
			Class<?> clz = obj.getClass();
			Method detachMethod = getMethodByName(clz, name, types);
			return detachMethod.invoke(obj, values);
		} catch (Exception e) {
			return false;
		}
	}

	public static Object execute(Object obj, String name, Class<?> type,
			Object value) {
		try {
			Class<?> clz = obj.getClass();
			Method detachMethod = getMethodByName(clz, name, type);
			return detachMethod.invoke(obj, value);
		} catch (Exception e) {
			return false;
		}
	}

	public static Object execute(Object obj, String name) {
		try {
			Class<?> clz = obj.getClass();
			Method detachMethod = getMethodByName(clz, name);
			return detachMethod.invoke(obj);
		} catch (Exception e) {
			return false;
		}
	}

	public static Method getMethodByName(Class<?> clz, String name) {
		try {
			Method detachMethod = clz.getDeclaredMethod(name);
			return detachMethod;
		} catch (NoSuchMethodException e) {
			Class<?> superClass = clz.getSuperclass();
			if (superClass != null)
				return getMethodByName(superClass, name);
		}
		return null;
	}

	public static Method getMethodByName(Class<?> clz, String name,
			Class<?> type) {
		try {
			Method detachMethod = clz.getDeclaredMethod(name, type);
			return detachMethod;
		} catch (NoSuchMethodException e) {
			Class<?> superClass = clz.getSuperclass();
			if (superClass != null)
				return getMethodByName(superClass, name, type);
		}
		return null;
	}

	public static Method getMethodByName(Class<?> clz, String name,
			Class<?>... paramterTypes) {
		try {
			Method detachMethod = clz.getDeclaredMethod(name, paramterTypes);
			return detachMethod;
		} catch (NoSuchMethodException e) {
			Class<?> superClass = clz.getSuperclass();
			if (superClass != null)
				return getMethodByName(superClass, name, paramterTypes);
		}
		return null;
	}
}
