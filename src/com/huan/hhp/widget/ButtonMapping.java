package com.huan.hhp.widget;

import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2016/10/15.
 */
public class ButtonMapping extends TextViewMapping {
    public ButtonMapping(Context context, String name) {
        super(context, name);
    }

    @Override
    public void setView(View view) {
        super.setView(view);
        view.setFocusable(true);
    }
}
