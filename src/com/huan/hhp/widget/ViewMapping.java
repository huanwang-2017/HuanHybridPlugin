package com.huan.hhp.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.huan.hhp.common.HHP;
import com.huan.hhp.common.SilentlyJsChannel;
import com.huan.hhp.exception.TypeMismatchException;
import com.huan.hhp.utils.Constants;
import com.huan.hhp.utils.IDUtil;
import com.huan.hhp.utils.ImageOptions;
import com.huan.hhp.utils.ReflexUtil;
import com.huan.hhp.widget.typeof.DimenTypeOf;
import com.huan.hhp.widget.typeof.TypeOf;
import com.huan.hhp.widget.typeof.ValueTypeOf;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import org.mozilla.javascript.ScriptableObject;

/**
 * Created by Administrator on 2016/10/15.
 */
public class ViewMapping extends CoreMapping {
    protected String TAG;

    {
        TAG = this.getClass().getSimpleName();
        /* View */
        mapping("layout_width", new DimenTypeOf("this.layout_width", String.class));
        mapping("layout_height", new DimenTypeOf("this.layout_height", String.class));
        mapping("width", new DimenTypeOf("this.layout_width", String.class));
        mapping("height", new DimenTypeOf("this.layout_height", String.class));
        mapping("minWidth", new DimenTypeOf("this.setMinimumWidth", int.class));
        mapping("minHeiht", new DimenTypeOf("this.setMinimumHeight", int.class));
        mapping("maxWidth", new DimenTypeOf("this.setMaxWidth", int.class));
        mapping("maxHeiht", new DimenTypeOf("this.setMaxHeight", int.class));
        mapping("id", new TypeOf("this.setId", String.class));
        mapping("x", new DimenTypeOf("setX", float.class));
        mapping("y", new DimenTypeOf("setY", float.class));
        mapping("background", new TypeOf("this.setBackground", String.class));
        mapping("getFocus", new ValueTypeOf("this.getFocus", boolean.class));
        mapping("selected", new ValueTypeOf("setSelected", boolean.class));
        mapping("focusable", new ValueTypeOf("setFocusable", boolean.class));
        mapping("onClick", new TypeOf("this.onClick", String.class));
        mapping("margin", new TypeOf("this.setMargins", String.class));
        mapping("padding", new TypeOf("this.setPaddings", String.class));
        mapping("gravity", new ValueTypeOf("this.gravity", boolean.class));

        /* RelativeLayout */
        mapping("layout_toRightOf", new TypeOf("this.layout_toRightOf", String.class));
        mapping("layout_toLeftOf", new TypeOf("this.layout_toLeftOf", String.class));
        mapping("layout_below", new TypeOf("this.layout_below", String.class));
        mapping("layout_above", new TypeOf("this.layout_above", String.class));
        mapping("layout_alignLeft", new TypeOf("this.layout_alignLeft", String.class));
        mapping("layout_alignTop", new TypeOf("this.layout_alignTop", String.class));
        mapping("layout_alignRight", new TypeOf("this.layout_alignRight", String.class));
        mapping("layout_alignBottom", new TypeOf("this.layout_alignBottom", String.class));
        mapping("layout_alignParentLeft", new ValueTypeOf("this.layout_alignParentLeft", boolean.class));
        mapping("layout_alignParentTop", new ValueTypeOf("this.layout_alignParentTop", boolean.class));
        mapping("layout_alignParentRight", new ValueTypeOf("this.layout_alignParentRight", boolean.class));
        mapping("layout_alignParentBottom", new ValueTypeOf("this.layout_alignParentBottom", boolean.class));
        mapping("layout_centerInParent", new ValueTypeOf("this.layout_centerInParent", boolean.class));
        mapping("layout_centerHorizontal", new ValueTypeOf("this.layout_centerHorizontal", boolean.class));
        mapping("layout_centerVertical", new ValueTypeOf("this.layout_centerVertical", boolean.class));

        /* LinearLayout */
        mapping("layout_weight", new TypeOf("this.layout_weight", float.class));

        /* GET */
        mapping("get.x", new TypeOf(TypeOf.MODE_GET, "getX"));
        mapping("get.y", new TypeOf(TypeOf.MODE_GET, "getY"));
        mapping("get.width", new TypeOf(TypeOf.MODE_GET, "getWidth"));
        mapping("get.height", new TypeOf(TypeOf.MODE_GET, "getHeight"));
        mapping("get.hasFocus", new TypeOf(TypeOf.MODE_GET, "hasFocus"));
        mapping("get.parent", new TypeOf(TypeOf.MODE_GET, "this.getParent"));
    }


    protected View mView; // 对应的View

    protected Handler mHandler;
    private IDUtil idUtil;

    private boolean died; // kill标记
    private String mPkg;

    public String getPackage() {
        return mPkg;
    }

    public void setPackage(String pkg) {
        this.mPkg = pkg;
    }

    public ViewMapping(Context context, String name) {
        super(context, name);
        mHandler = ((HHP)mContext).getHandler();
        idUtil = ((HHP)mContext).getIdUtil();
    }

    @Override
    protected Object getObjTag() {
        return mView;
    }

    public void setView(View view) {
        this.mView = view;
        mView.setTag(this);
    }

    public View getView() {
        return mView;
    }

    /**
     * 设置margin
     * @param margin
     */
    public void setMargins(String margin){
        try {
            String[] margins = margin.split(",");
            ReflexUtil.execute(mView.getLayoutParams(), "setMargins", new Class[]{int.class, int.class, int.class, int.class},
                    new Object[]{
                            Integer.parseInt(margins[0].trim()),
                            Integer.parseInt(margins[1].trim()),
                            Integer.parseInt(margins[2].trim()),
                            Integer.parseInt(margins[3].trim())
                    });
            mView.setLayoutParams(mView.getLayoutParams());
        } catch (Exception e){

        }
    }

    /**
     * 设置padding
     * @param padding
     */
    public void setPaddings(String padding){
        try {
            String[] paddings = padding.split(",");
            mView.setPadding(
                    Integer.parseInt(paddings[0].trim()),
                    Integer.parseInt(paddings[1].trim()),
                    Integer.parseInt(paddings[2].trim()),
                    Integer.parseInt(paddings[3].trim())
            );
        } catch (Exception e){

        }
    }

    private String id;
    /**
     * 从配置中制定View的id
     * @param id
     */
    public void setId(String id){
        this.id = id;
        int idValue = idUtil.convertString2Int(id);
        mView.setId(idValue);
    }

    public String getId() {
        return id;
    }

    @JavascriptInterface
    public ViewMapping findViewById(int id){
        View childView = mView.findViewById(id);
        if(childView != null && childView.getTag() instanceof ViewMapping) {
            return (ViewMapping) childView.getTag();
        }
        return null;
    }

    @JavascriptInterface
    public ViewMapping findViewById(String id){
        return this.findViewById(idUtil.getId(id));
    }

    @JavascriptInterface
    public ViewMapping getParent(){
        if(mView.getParent() != null){
            return (ViewMapping) ((ViewGroup)mView.getParent()).getTag();
        }
        return null;
    }

    /**
     * 点击事件
     * @param jsMethodName
     */
    public void onClick(String jsMethodName){
        if(jsMethodName.contains("(this)")){
            final String methodName = jsMethodName.substring(0, jsMethodName.indexOf("(this)"));
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "click:" + methodName);
                    getSilentlyJsChannel().exFunction(methodName, ViewMapping.this);
                }
            });
        }
        else {
            final String methodName = jsMethodName;
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "click:" + methodName);
                    getSilentlyJsChannel().exFunction(methodName);
                }
            });
        }
    }

    private ImageView bgTemp;
    private Runnable bgDrawingEngineer = new Runnable() {
        @Override
        public void run() {
            String uri = bgTemp.getTag().toString();
            ImageLoader.getInstance().displayImage(uri, bgTemp, ImageOptions.getImageOptions(0), new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(mView != null) {
                    mView.setBackgroundDrawable(new BitmapDrawable(loadedImage));
                }
                }
            });
        }
    };

    /**
     * 设置背景
     * @param uri
     * @see #bgTemp
     * @see #bgDrawingEngineer
     */
    @JavascriptInterface
    public void setBackground(String uri){
        Log.i(TAG, "background=" + uri);
        if(uri.substring(0, 1).equals("#")){
            // 背景色
            mView.setBackgroundColor(Color.parseColor(uri));
        }
        else {
            if(bgTemp == null){
                bgTemp = new ImageView(mContext);
            }
            bgTemp.setTag(uri);
            mHandler.post(bgDrawingEngineer);
        }
    }

    /**
     * 指定是否获取焦点
     * @param getFocus
     */
    public void getFocus(boolean getFocus){
        if(getFocus){
            mView.requestFocus();
        }
    }

    public SilentlyJsChannel getSilentlyJsChannel(){
        return ((HHP)mContext).getSilentlyJsChannel();
    }

    public Handler getHandler() {
        return mHandler;
    }

    /**
     * 打开新的Activity
     * @param activityName
     */
    @JavascriptInterface
    public void startActivity(String activityName){
        Intent intent = new Intent(mContext, HHP.class);
        intent.putExtra(Constants.ACTIVITY_NAME, activityName);
        mContext.startActivity(intent);
    }

    /**
     * @param script {layout_width, "200dip", layout_height, "200dip"}
     */
    @JavascriptInterface
    public void css(ScriptableObject script){
        Object[] ids = script.getAllIds();
        for(Object id : ids){
            String name = id.toString();
            Object obj = ScriptableObject.getProperty(script, name);
            try {
                set(name, obj.toString());
            } catch (TypeMismatchException e) {
                e.printStackTrace();
            }
        }

        /*try {
            Map<String, String> changes = gson.fromJson(string, new TypeToken<Map<String, String>>() {
            }.getType());
            for (String name : changes.keySet()) {
                set(name, changes.get(name));
            }
        }catch (Exception e){
            Log.e(TAG, "gson 解析失败!" + ErrorUtil.e(e));
        }*/
    }

    @Override
    @JavascriptInterface
    public Object attr(String param) throws TypeMismatchException {
        param = "get."+param;
        Log.i(TAG, "attr:"+param);
        return super.attr(param);
    }

    /**
     * 请求焦点
     * @return
     */
    public boolean requestFocus(){
        return mView.requestFocus();
    }

    /**
     * 绘制
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public void layout(int l, int t, int r, int b){
        mView.layout(l, t, r, b);
    }

    @JavascriptInterface
    public void UI(){
        mHandler.sendEmptyMessage(10086);
    }

    @JavascriptInterface
    public void onKeyResult(boolean keyIntercept){
        ((HHP)mContext).onKeyResult(keyIntercept);
    }

    /*********************************************************************************
     *
     * *******************************************************************************
     * View Attr映射
     */

    public void layout_width(String value){
        Log.i(TAG, "layout_width="+value);
        if("match_parent".equals(value) || "fill_parent".equals(value))
            mView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        else if("wrap_content".equals(value))
            mView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        else
            mView.getLayoutParams().width = Integer.parseInt(TypeOf.androidUnit(mContext, value));
    }

    public void layout_height(String value){
        Log.i(TAG, "layout_height="+value);
        if("match_parent".equals(value) || "fill_parent".equals(value))
            mView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        else if("wrap_content".equals(value))
            mView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        else
            mView.getLayoutParams().height = Integer.parseInt(TypeOf.androidUnit(mContext, value));
    }


    /*********************************************************************************
     *
     * *******************************************************************************
     * RelativeLayout Attr映射
     */

    public void layout_toRightOf(String value){
        addRule(RelativeLayout.RIGHT_OF, idUtil.convertString2Int(value));
    }
    public void layout_toLeftOf(String value){
        addRule(RelativeLayout.LEFT_OF, idUtil.convertString2Int(value));
    }
    public void layout_below(String value){
        addRule(RelativeLayout.BELOW, idUtil.convertString2Int(value));
    }
    public void layout_above(String value){
        addRule(RelativeLayout.ABOVE, idUtil.convertString2Int(value));
    }
    public void layout_alignLeft(String value){
        addRule(RelativeLayout.ALIGN_LEFT, idUtil.convertString2Int(value));
    }
    public void layout_alignTop(String value){
        addRule(RelativeLayout.ALIGN_TOP, idUtil.convertString2Int(value));
    }
    public void layout_alignRight(String value){
        addRule(RelativeLayout.ALIGN_RIGHT, idUtil.convertString2Int(value));
    }
    public void layout_alignBottom(String value){
        addRule(RelativeLayout.ALIGN_BOTTOM, idUtil.convertString2Int(value));
    }
    public void layout_alignParentLeft(boolean value){
        addRule(RelativeLayout.ALIGN_PARENT_LEFT, value);
    }
    public void layout_alignParentTop(boolean value){
        addRule(RelativeLayout.ALIGN_PARENT_TOP, value);
    }
    public void layout_alignParentRight(boolean value){
        addRule(RelativeLayout.ALIGN_PARENT_RIGHT, value);
    }
    public void layout_alignParentBottom(boolean value){
        addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, value);
    }
    public void layout_centerInParent(boolean value){
        addRule(RelativeLayout.CENTER_IN_PARENT, value);
    }
    public void layout_centerHorizontal(boolean value){
        addRule(RelativeLayout.CENTER_HORIZONTAL, value);
    }
    public void layout_centerVertical(boolean value){
        addRule(RelativeLayout.CENTER_VERTICAL, value);
    }
    public void addRule(int verb, int anchor){
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mView.getLayoutParams();
        lp.addRule(verb, anchor);
    }

    public void addRule(int verb, boolean flag){
        if(flag) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mView.getLayoutParams();
            lp.addRule(verb);
        }
    }

    /*********************************************************************************
     *
     * *******************************************************************************
     * LinearLayout Attr映射
     */

    public void layout_weight(float weight){
        ((LinearLayout.LayoutParams)mView.getLayoutParams()).weight = weight;
    }

    public void release(){
        Log.i(TAG, "release ...");
        loadDestroy();
    }

    protected void onCreate(){
        died = false;
        Log.i(TAG, "onCreate");
        if(mView instanceof ViewGroup){
            ViewGroup group = (ViewGroup) mView;
            int count = group.getChildCount();
            int i = 0;
            View child = null;
            for(; i < count; i++){
                child = group.getChildAt(i);
                if(child.getTag() instanceof ViewMapping) {
                    ViewMapping vMapping = (ViewMapping) child.getTag();
                    vMapping.onCreate();
                }
            }
        }
    }

    protected void onDestroy(){
        died = true;
        Log.i(TAG, "onDestroy");
        if(mView instanceof ViewGroup){
            ViewGroup group = (ViewGroup) mView;
            int count = group.getChildCount();
            int i = 0;
            View child;
            for(; i < count; i++){
                child = group.getChildAt(i);
                if(child.getTag() instanceof ViewMapping) {
                    ViewMapping vMapping = (ViewMapping) child.getTag();
                    vMapping.onDestroy();
                }
            }
        }
//        mView.setBackground(null);
        mView.setTag(null);
    }

    public final void loadCreated(){onCreate();}
    public final void loadDestroy(){onDestroy();}

    public boolean isDied() {
        return died;
    }
}
