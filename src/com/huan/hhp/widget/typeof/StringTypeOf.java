package com.huan.hhp.widget.typeof;

import android.content.Context;
import com.huan.hhp.common.HHP;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
public class StringTypeOf extends TypeOf {
    public StringTypeOf(String name, Class<?> type) {
        super(name, type);
    }

    public StringTypeOf(int mode, String name, Class<?> type) {
        super(mode, name, type);
    }

    public StringTypeOf(int mode, String name) {
        super(mode, name);
    }

    @Override
    public Object convert(Context context, String value, Class<?> type) {
        if(value.contains("@string/")){
            String valueName = value.substring("@string/".length());
            value = ((HHP)context).getReSourceUtil().getValue(valueName);
        }
        return super.convert(context, value, type);
    }
}
