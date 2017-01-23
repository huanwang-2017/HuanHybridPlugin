package com.huan.hhp.listener;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/11/20 0020.
 */
public class SimpleBitmapLoadingProgress extends BitmapLoadingProgress {
    private TextView mTextView;
    public SimpleBitmapLoadingProgress(Context context) {
        super(context);
        mTextView = new TextView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mTextView.setGravity(Gravity.CENTER);
        setContentView(mTextView);
    }

    @Override
    public void onProgressUpdate(String s, View view, int i, int i1) {
        mTextView.setText(i1+"/"+i);
    }
}
