package com.huan.sdk.huanpay4.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import tv.huan.sdk.auth.HuanUserAuth;
import tv.huan.sdk.auth.UserAuth;

public class DeviceInfo {
	public static final String TAG = DeviceInfo.class.getSimpleName();

	/**
	 * 获取UA信息
	 */
	
	public String huanID = "";
	public String token = "";
	public String termUnitNo = "";
	public String termUnitParam = "";
	public String TM = "";
	public String didtoken = "";

	public String mac = "";//mac地址 (过滤掉"-"并将大写字母转成小写字母)
	
//	private static DeviceInfo mDeviceInfo ;

	
	private DeviceInfo(Context context){
		mac = getMac(context);
		UserAuth userAuth = HuanUserAuth.getdevicesinfo(context, null);
		if(userAuth == null) return;
		
		huanID = userAuth.getHuanId();
		token = userAuth.getHuanToken();
		termUnitNo = userAuth.getDeviceNum();
		termUnitParam = userAuth.getDevModel();
		System.out.println("DevModel========"+termUnitParam);
		TM = userAuth.getTM();
		didtoken = userAuth.getDidtoken();
		
/*		if(ConnectFactory.isTest){
			huanID = "TV581001123456780";
			termUnitNo = "123456780";
			termUnitParam = "MT36-E5390A-3D";
			token = "123456789";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
		}*/
		
		if(huanID == null || "".equals(huanID) || "0".equals(huanID))  huanID = "";
		if(token == null || "".equals(token) || "0".equals(token))  token = "";
		if(termUnitNo == null || "".equals(termUnitNo) || "0".equals(termUnitNo))  termUnitNo = "";
		if(termUnitParam == null || "".equals(termUnitParam) || "0".equals(termUnitParam))  termUnitParam = "";
		if(TM == null || "".equals(TM) || "0".equals(TM))  TM = "";
		if(didtoken == null || "".equals(didtoken) || "0".equals(didtoken))  didtoken = "";

	}
	
	public synchronized static DeviceInfo getInstance(Context context){
		return  new DeviceInfo(context);
	}
	
	private String getMac(Context context){
		String macString = "";
		String macAddress = null;
		WifiManager wifiMgr = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		StringBuffer mac = new StringBuffer();
		if (null != info) {
			macAddress = info.getMacAddress();
			if (macAddress == null) {
				mac.append("000000");
			} else {
				if (macAddress.contains("-")) {
					macString = macAddress.replace("-", "");
				} else if (macAddress.contains(":")) {
					macString = macAddress.replace(":", "");
				}

				for (int i = 0; i < macString.length(); i++) {
					char c = macString.charAt(i);
					mac.append(Character.toLowerCase(c));
				}
			}
		}
		Log.i(TAG,"MAC-->" + mac.toString());
		return mac.toString();
	}
	
}
