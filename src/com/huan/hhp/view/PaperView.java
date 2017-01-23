package com.huan.hhp.view;

import android.content.Context;
import android.widget.RelativeLayout;

/**
 * Created by tjy on 2016/11/21 0021.
 * PaperView 就是一张白纸，对子元素不做任何约束，如何绘制取决于子元素的layout方法。
 *
 * 比如：child.layout(0, 0, 100, 200)
 */
public class PaperView extends RelativeLayout {
    public PaperView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 神马都不做
    }
}
