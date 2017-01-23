package com.huan.sdk.huanpay4.util;

import android.content.Context;
import com.huan.sdk.huanpay4.been.PayInfo;

import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: warriorr
 * Mail: warriorr@163.com
 * Date: 2016/7/21
 * Time: 14:25
 * To change this template use File | Settings | File Templates
 */
public class PayUtil {
    private static PayUtil payUtil = new PayUtil();

    public static PayUtil getInstance() {
        return payUtil;
    }


    public String getInitParams(Context context, PayInfo mPayInfo) {
        DeviceInfo mDeviceInfo = DeviceInfo.getInstance(context);
        if (mPayInfo.validateType.equals("")) {
            mPayInfo.termUnitNo = mDeviceInfo.termUnitNo;
            mPayInfo.termUnitParam = mDeviceInfo.termUnitParam;
            mPayInfo.accountID = mDeviceInfo.huanID;
            mPayInfo.validateParam = mDeviceInfo.huanID + "|"+ mDeviceInfo.token;
//            mPayInfo.termUnitNo = "109996221";
//            mPayInfo.termUnitParam = "TCL-CN-RT95-E5690A-3D";
//            mPayInfo.accountID = "180609710";
//            mPayInfo.validateParam = "180609710|c9a1296d6f6b4187b6a82fd1a13b5324";
        }
        StringBuffer sb = null;
        sb = new StringBuffer();
        if (mPayInfo.appSerialNo != null && mPayInfo.appSerialNo != "") sb.append("appSerialNo=" + mPayInfo.appSerialNo);
        if (mPayInfo.validateType != null && mPayInfo.validateType != "") sb.append("&validateType=" + mPayInfo.validateType);
        if (mPayInfo.huanID != null && mPayInfo.huanID != "") sb.append("&huanID=" + mPayInfo.huanID);
        if (mPayInfo.token != null && mPayInfo.token != "") sb.append("&token=" + mPayInfo.token);
        if (mPayInfo.accountID != null && mPayInfo.accountID != "") sb.append("&accountID=" + mPayInfo.accountID);
        if (mPayInfo.validateParam != null && mPayInfo.validateParam != "") sb.append("&validateParam=" + mPayInfo.validateParam);
        if (mPayInfo.termUnitNo != null && mPayInfo.termUnitNo != "") sb.append("&termUnitNo=" + mPayInfo.termUnitNo);
        if (mPayInfo.termUnitParam != null && mPayInfo.termUnitParam != "") sb.append("&termUnitParam=" + URLEncoder.encode(mPayInfo.termUnitParam));
        if (mPayInfo.appPayKey != null && mPayInfo.appPayKey != "") sb.append("&appPayKey=" + mPayInfo.appPayKey);
        if (mPayInfo.productName != null && mPayInfo.productName != "") sb.append("&productName=" + mPayInfo.productName);
        if (mPayInfo.productCount != null && mPayInfo.productCount != "") sb.append("&productCount=" + mPayInfo.productCount);
        if (mPayInfo.productDescribe != null && mPayInfo.productDescribe != "") sb.append("&productDescribe=" + mPayInfo.productDescribe);
        if (mPayInfo.productPrice != null && mPayInfo.productPrice != "") sb.append("&productPrice=" + mPayInfo.productPrice);
        if (mPayInfo.orderType != null && mPayInfo.orderType != "") sb.append("&orderType=" + mPayInfo.orderType);
        if (mPayInfo.paymentType != null && mPayInfo.paymentType != "") sb.append("&paymentType=" + mPayInfo.paymentType);
        if (mPayInfo.date != null && mPayInfo.date != "") sb.append("&date=" + mPayInfo.date);
        if (mPayInfo.productDetailURL != null && mPayInfo.productDetailURL != "") sb.append("&productDetailURL=" + mPayInfo.productDetailURL);
        if (mPayInfo.noticeUrl != null && mPayInfo.noticeUrl != "") sb.append("&noticeUrl=" + mPayInfo.noticeUrl);
        if (mPayInfo.extension != null && mPayInfo.extension != "") sb.append("&extension=" + mPayInfo.extension);
        if (mPayInfo.signType != null && mPayInfo.signType != "") sb.append("&signType=" + mPayInfo.signType);
        return sb.toString();

    }

}
