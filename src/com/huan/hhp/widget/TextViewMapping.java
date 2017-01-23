package com.huan.hhp.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.webkit.JavascriptInterface;
import android.widget.TextView;
import com.huan.hhp.utils.StaticReflexUtil;
import com.huan.hhp.widget.typeof.DimenTypeOf;
import com.huan.hhp.widget.typeof.StringTypeOf;
import com.huan.hhp.widget.typeof.TypeOf;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/16.
 */
public class TextViewMapping extends ViewMapping {
    {
        mapping("text", new StringTypeOf("setText", CharSequence.class));
        mapping("textSize", new DimenTypeOf("setTextSize", float.class));
        mapping("textColor", new TypeOf("this.setTextColor", String.class));
        mapping("maxLength", new TypeOf("this.setMaxLength", int.class));
        mapping("maxEms", new TypeOf("setMaxEms", int.class));
        mapping("maxLines", new TypeOf("setMaxLines", int.class));
        mapping("singleLine", new TypeOf("setSingleLine", boolean.class));
        mapping("lines", new TypeOf("setLines", int.class));
        mapping("ellipsize", new TypeOf("this.setEllipsize", String.class));
        mapping("inputType", new TypeOf("this.setInputType", String.class));
        mapping("hint", new TypeOf("this.setHint", String.class));
        mapping("scrollHorizontally", new TypeOf("setHorizontallyScrolling", boolean.class));
        mapping("gravity", new TypeOf("this.gravity", String.class));
    }

    private HashMap<String, Integer> inputTypeMapping = new HashMap<String, Integer>();{
        inputTypeMapping.put("text", InputType.TYPE_CLASS_TEXT);
        inputTypeMapping.put("number", InputType.TYPE_CLASS_NUMBER);
        inputTypeMapping.put("phone", InputType.TYPE_CLASS_PHONE);
        inputTypeMapping.put("datetime", InputType.TYPE_CLASS_DATETIME);
        inputTypeMapping.put("date", InputType.TYPE_DATETIME_VARIATION_DATE);
        inputTypeMapping.put("time", InputType.TYPE_DATETIME_VARIATION_TIME);
        inputTypeMapping.put("textCapCharacters", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        inputTypeMapping.put("textCapWords", InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        inputTypeMapping.put("textCapSentences", InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        inputTypeMapping.put("textAutoCorrect", InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        inputTypeMapping.put("textAutoComplete", InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        inputTypeMapping.put("textMultiLine", InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        inputTypeMapping.put("textImeMultiLine", InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        inputTypeMapping.put("textNoSuggestions", InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        inputTypeMapping.put("textUri", InputType.TYPE_TEXT_VARIATION_URI);
        inputTypeMapping.put("textEmailAddress", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputTypeMapping.put("textEmailSubject", InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        inputTypeMapping.put("textShortMessage", InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        inputTypeMapping.put("textLongMessage", InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        inputTypeMapping.put("textPersonName", InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        inputTypeMapping.put("textPostalAddress", InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        inputTypeMapping.put("textPassword", InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputTypeMapping.put("textVisiblePassword", InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        inputTypeMapping.put("textWebTextView", InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
        inputTypeMapping.put("textFilter", InputType.TYPE_TEXT_VARIATION_FILTER);
        inputTypeMapping.put("textPhonetic", InputType.TYPE_TEXT_VARIATION_PHONETIC);
        inputTypeMapping.put("numberSigned", InputType.TYPE_NUMBER_FLAG_SIGNED);
        inputTypeMapping.put("numberDecimal", InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    public TextViewMapping(Context context, String name) {
        super(context, name);
    }

    /**
     * 设置内容
     * @param text
     */
    @JavascriptInterface
    public void setText(final String text){
        Log.i(TAG, "setText:"+text);
        ((TextView)mView).setText(text);
    }

    public void setMaxLength(int length){
        ((TextView)mView).setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }

    /**
     * 设置文本类型
     * @param inputType
     */
    public void setInputType(String inputType){
        if(inputTypeMapping.containsKey(inputType)){
            int value = inputTypeMapping.get(inputType);
            Log.i(TAG, "inputType="+value);
            ((TextView)mView).setInputType(value);
        }
    }

    public void setEllipsize(String ellipsize){
        TextUtils.TruncateAt value = StaticReflexUtil.get(TextUtils.TruncateAt.class, ellipsize.toUpperCase());
        if(value != null){
            Log.i(TAG, "setEllipsize:"+value);
            ((TextView) mView).setEllipsize(value);
        }
    }

    /**
     * 设置 hint
     * @param hint
     */
    public void setHint(String hint){
        ((TextView)mView).setHint(hint);
    }

    /**
     * 设置居中类型
     * @param gravity
     */
    public void gravity(String gravity){
        gravity = gravity.toUpperCase();
        int value = StaticReflexUtil.get(Gravity.class, gravity);
        ((TextView)mView).setGravity(value);
    }

    public void setTextColor(String color){
        ((TextView)mView).setTextColor(Color.parseColor(color));
    }
}
