package com.huan.hhp.widget;

import android.content.Context;
import com.huan.hhp.view.PaperView;

/**
 * Created by tjy on 2016/11/21 0021.
 * @see PaperView
 */
public class PaperViewMapping extends RelativeLayoutMapping {
    {
        forTag(PaperView.class.getName());
    }

    public PaperViewMapping(Context context, String name) {
        super(context, name);
    }
}
