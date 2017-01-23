package com.huan.hhp.widget.typeof;

import android.content.Context;
import android.util.TypedValue;
import com.huan.hhp.exception.TypeMismatchException;
import com.huan.hhp.widget.CoreMapping;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
public class TypeOf {
    public static final int MODE_SET = 0;   // 赋值
    public static final int MODE_GET = 1;   // 取值

    private int mode = MODE_SET;
    public String name;
    public Class<?> type;

    public TypeOf(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public TypeOf(int mode, String name, Class<?> type) {
        this.mode = mode;
        this.name = name;
        this.type = type;
    }

    public TypeOf(int mode, String name) {
        this.mode = mode;
        this.name = name;
    }

    public void set(CoreMapping mp, Object obj, String value) throws TypeMismatchException {
        mp.set(obj, name, value, this);
    }

    @Override
    public String toString() {
        return name+"-->"+type;
    }

    public Object convert(Context context, String value, Class<?> type){
        /*if(value.contains("@dimen/")){
            String valueName = value.substring("@dimen/".length());
            value = androidUnit(context, ((BaseActivity)context).getReSourceUtil().getDimens(valueName));
        }
        else if(value.contains("@string/")){
            String valueName = value.substring("@string/".length());
            value = ((BaseActivity)context).getReSourceUtil().getValue(valueName);
        }*/
        return value;
    }

    public static String androidUnit(Context context, String value){
        if(value.contains("dip")){
            return convertUnit(context, value.substring(0, value.indexOf("dip")), TypedValue.COMPLEX_UNIT_DIP)+"";
        }
        if(value.contains("dp")){
            return convertUnit(context, value.substring(0, value.indexOf("dp")), TypedValue.COMPLEX_UNIT_DIP)+"";
        }
        if(value.contains("px")){
            return convertUnit(context, value.substring(0, value.indexOf("px")), TypedValue.COMPLEX_UNIT_PX)+"";
        }
        if(value.contains("sp")){
            return convertUnit(context, value.substring(0, value.indexOf("sp")), TypedValue.COMPLEX_UNIT_SP)+"";
        }
        return value;
    }

    public static int convertUnit(Context context, String value, int unit){
        return (int) TypedValue.applyDimension(unit, Integer.parseInt(value), context.getResources().getDisplayMetrics());
    }
}
