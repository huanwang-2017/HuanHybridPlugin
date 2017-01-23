package com.huan.hhp.widget;

import android.content.Context;
import android.widget.LinearLayout;
import com.huan.hhp.widget.typeof.TypeOf;

/**
 * Created by Administrator on 2016/10/16.
 */
public class LinearLayoutMapping extends ViewGroupMapping {
    {
        mapping("orientation", new TypeOf("this.setOrientation", String.class));
        mapping("weightSum", new TypeOf("setWeightSum", int.class));
    }

    public LinearLayoutMapping(Context context, String name) {
        super(context, name);
    }

    public void setOrientation(String orientation){
        int orientationValue = LinearLayout.HORIZONTAL;
        if("vertical".equals(orientation)){
            orientationValue = LinearLayout.VERTICAL;
        }
        ((LinearLayout)mView).setOrientation(orientationValue);
    }
}
