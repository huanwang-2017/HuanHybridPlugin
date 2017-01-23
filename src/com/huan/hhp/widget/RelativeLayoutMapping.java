package com.huan.hhp.widget;

import android.content.Context;

/**
 * Created by Administrator on 2016/10/20.
 */
public class RelativeLayoutMapping extends ViewGroupMapping {
    /*{
        mapping("layout_toRightOf", new TypeOf("this.layout_toRightOf", int.class));
        mapping("layout_toLeftOf", new TypeOf("this.layout_toLeftOf", int.class));
        mapping("layout_below", new TypeOf("this.layout_below", int.class));
        mapping("layout_above", new TypeOf("this.layout_above", int.class));
        mapping("layout_alignLeft", new TypeOf("this.layout_alignLeft", int.class));
        mapping("layout_alignTop", new TypeOf("this.layout_alignTop", int.class));
        mapping("layout_alignRight", new TypeOf("this.layout_alignRight", int.class));
        mapping("layout_alignBottom", new TypeOf("this.layout_alignBottom", int.class));
        mapping("layout_alignParentLeft", new TypeOf("this.layout_alignParentLeft", int.class));
        mapping("layout_alignParentTop", new TypeOf("this.layout_alignParentTop", int.class));
        mapping("layout_alignParentRight", new TypeOf("this.layout_alignParentRight", int.class));
        mapping("layout_alignParentBottom", new TypeOf("this.layout_alignParentBottom", int.class));

        mapping("layout_centerInParent", new TypeOf("this.layout_centerInParent", int.class));
        mapping("layout_centerHorizontal", new TypeOf("this.layout_centerHorizontal", int.class));
        mapping("layout_centerVertical", new TypeOf("this.layout_centerVertical", int.class));
    }*/

    public RelativeLayoutMapping(Context context, String name) {
        super(context, name);
    }

/*public void layout_toRightOf(int value){
        addRule(RelativeLayout.RIGHT_OF, value);
    }
    public void layout_toLeftOf(int value){
        addRule(RelativeLayout.LEFT_OF, value);
    }
    public void layout_below(int value){
        addRule(RelativeLayout.BELOW, value);
    }
    public void layout_above(int value){
        addRule(RelativeLayout.ABOVE, value);
    }
    public void layout_alignLeft(int value){
        addRule(RelativeLayout.ALIGN_LEFT, value);
    }
    public void layout_alignTop(int value){
        addRule(RelativeLayout.ALIGN_TOP, value);
    }
    public void layout_alignRight(int value){
        addRule(RelativeLayout.ALIGN_RIGHT, value);
    }
    public void layout_alignBottom(int value){
        addRule(RelativeLayout.ALIGN_BOTTOM, value);
    }
    public void layout_alignParentLeft(int value){
        addRule(RelativeLayout.ALIGN_PARENT_LEFT, value);
    }
    public void layout_alignParentTop(int value){
        addRule(RelativeLayout.ALIGN_PARENT_TOP, value);
    }
    public void layout_alignParentRight(int value){
        addRule(RelativeLayout.ALIGN_PARENT_RIGHT, value);
    }
    public void layout_alignParentBottom(int value){
        addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, value);
    }
    public void layout_centerInParent(int value){
        addRule(RelativeLayout.CENTER_IN_PARENT, value);
    }
    public void layout_centerHorizontal(int value){
        addRule(RelativeLayout.CENTER_HORIZONTAL, value);
    }
    public void layout_centerVertical(int value){
        addRule(RelativeLayout.CENTER_VERTICAL, value);
    }
    public void addRule(int verb, int anchor){
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mView.getLayoutParams();
        lp.addRule(verb, anchor);
    }*/
}
