package com.huan.hhp.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/10/30.
 */
public class IDUtil {
    private int seq = 0x001;
    private HashMap<String, Integer> ids; {
        ids = new HashMap<String, Integer>();
        Log.i("IDUtil", "ids 被初始化");
    }

    /**
     * 把String类型转换成int
     * 如：@+id/button1
     * @param id
     * @return
     */
    public synchronized int convertString2Int(String id){
        if(isNumeric(id)){
            return Integer.parseInt(id);
        }
        else if(id.contains("@id")){
            String realId = id.substring("@id".length());
            return convertString2Int("@+id"+realId);
        }
        else if(!id.contains("@id") && !id.contains("@+id")){
            return convertString2Int("@+id/"+id);
        }
        if(ids.containsKey(id))
            return ids.get(id);
        int seq = this.seq++;
        ids.put(id, seq);
        return seq;
    }

    /**
     * 获取id
     * @param id
     * @return
     */
    public synchronized int getId(String id){
        String idString = ("@+id/"+id);
        int idValue = ids.get(idString);
        return idValue;
    }

    public void release(){
        ids.clear();
    }

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public HashMap<String, Integer> getIds() {
        return ids;
    }
}
