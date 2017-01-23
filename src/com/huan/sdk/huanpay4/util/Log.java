package com.huan.sdk.huanpay4.util;


import com.huan.sdk.huanpay4.Constants;

public class Log {

	private static final boolean debug = Constants.isTest;
	private static final String TAG = "HuanPay4 ==> ";
	
	public static void e(String tag, String msg){
		if(!debug) return;
		android.util.Log.e(TAG + tag, msg);
	}
	
	public static void e1(String msg){
		if(!debug) return;
		android.util.Log.e(TAG , msg);
	}
	
	public static void i(String tag, String msg){
		if(!debug) return;
		android.util.Log.i(TAG + tag, msg);
	}
	
	public static void v(String tag, String msg){
		if(!debug) return;
		android.util.Log.v(TAG + tag, msg);
	}
	
	public static void print(Object obj){
		if(!debug) return;
		
		if(obj == null) android.util.Log.i(TAG, "null");
		else android.util.Log.i(TAG, obj.toString());
	}
	
	
}
