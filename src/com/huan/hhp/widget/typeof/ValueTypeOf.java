package com.huan.hhp.widget.typeof;

import android.content.Context;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
public class ValueTypeOf extends TypeOf {
    public ValueTypeOf(String name, Class<?> type) {
        super(name, type);
    }

    public ValueTypeOf(int mode, String name, Class<?> type) {
        super(mode, name, type);
    }

    public ValueTypeOf(int mode, String name) {
        super(mode, name);
    }

    @Override
    public Object convert(Context context, String value, Class<?> type) {
        value = androidUnit(context, value);
        if(String.class == type || CharSequence.class == type){
            return value;
        }
        if(Integer.class == type || int.class == type)
            return Integer.parseInt(value);
        if(Long.class == type || long.class == type)
            return Long.parseLong(value);
        if(Float.class == type || float.class == type)
            return Float.parseFloat(value);
        if(Short.class == type || short.class == type)
            return Short.parseShort(value);
        if(Double.class == type || double.class == type)
            return Double.parseDouble(value);
        if(Boolean.class == type || boolean.class == type)
            return Boolean.parseBoolean(value);
        return value;
    }
}
