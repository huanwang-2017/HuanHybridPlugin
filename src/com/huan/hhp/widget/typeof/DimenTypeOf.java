package com.huan.hhp.widget.typeof;

import android.content.Context;
import com.huan.hhp.common.HHP;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
public class DimenTypeOf extends ValueTypeOf {
    public DimenTypeOf(String name, Class<?> type) {
        super(name, type);
    }

    public DimenTypeOf(int mode, String name, Class<?> type) {
        super(mode, name, type);
    }

    public DimenTypeOf(int mode, String name) {
        super(mode, name);
    }

    @Override
    public Object convert(Context context, String value, Class<?> type) {
        if(value.contains("@dimen/")){
            String valueName = value.substring("@dimen/".length());
            value = ((HHP)context).getReSourceUtil().getDimens(valueName);
        }
        return super.convert(context, value, type);
    }
}
