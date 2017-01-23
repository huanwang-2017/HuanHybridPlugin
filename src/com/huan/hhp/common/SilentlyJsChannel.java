package com.huan.hhp.common;

import android.content.Context;
import com.huan.hhp.js.JsChannel;

/**
 * Created by tjy on 2016/10/18.
 */
public class SilentlyJsChannel extends JsChannel {
    final String TAG = SilentlyJsChannel.class.getSimpleName();

    public static final String baseJs = "function loadOnKeyDown(keyCode){\n" +
            "   if(typeof onKeyDown == 'function'){" +
            "       var result = onKeyDown(keyCode);\n" +
            "       window.context.onKeyResult(result);\n" +
            "   }else{" +
            "       context.onKeyResult(false);" +
            "   }" +
            "}\n\n" +
            "function loadOnKeyUp(keyCode){\n" +
            "   if(typeof onKeyUp == 'function'){" +
            "       var result = onKeyUp(keyCode);\n" +
            "       window.context.onKeyResult(result);\n" +
            "   }else{" +
            "       context.onKeyResult(false);" +
            "   }" +
            "}";


    public SilentlyJsChannel(Context context) {
        loadJs(baseJs);
    }
}
