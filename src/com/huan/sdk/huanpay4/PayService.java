package com.huan.sdk.huanpay4;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import com.huan.sdk.huanpay4.util.XMLFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: warriorr
 * Mail: warriorr@163.com
 * Date: 2016/9/8
 * Time: 19:42
 * To change this template use File | Settings | File Templates
 */
public class PayService {
    public static final String TAG = PayService.class.getSimpleName();

    //订单查询
    public static String searchOrderResult(String orderNo) throws Exception {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("orderNo", orderNo));
        String result = sendHttpPost(Constants.API_URL + "/sdkPayAction_searchOrder.action", params);
        InputSource is = new InputSource(new StringReader(result));
        return XMLFactory.getInstance().parseInitPayRequest(is).getRespResult();
    }

    public static String sendHttpPost(String requestUrl, List<NameValuePair> nvps) throws Exception {
        Log.d(TAG, "SendHttpPost(),requestUrl  = " + requestUrl+nvps.toString());
        HttpPost httppost = new HttpPost(requestUrl);
        httppost.addHeader("Content-Type", "application/xml");
        httppost.setEntity( new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        HttpResponse response = getHttpClient().execute(httppost);
        String responseString = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        Log.d(TAG, "SendHttpPost(),response  = " + responseString);
        return responseString;
    }

    private static HttpClient getHttpClient() {
        HttpParams params = new BasicHttpParams();
/* 设置HttpParam是的基本参数，其实都是对应http请求的消息头。其中三个都很好理解，重点介绍一些setUserExpectContinue。 一般都设置为flase，设置为true通常是传递request消息很大（例携带大文件），而服务器可能需要认证，我们不希望传完这个大文件，才收到服务器的拒绝。HTTP是TCP流方式，当server收到请求的头字段是Except：100-continue， 不在等待整个请求，返回100 continue应答继续读取，或者给出拒绝请求（final Status code，如4xx）。 具体可以参考：http://www.w3.org/Protocols/rfc2616/rfc2616-sec8.html#sec8.2.3 */
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpProtocolParams.setUserAgent(params, "Mozilla/5.0 (Linux; U; Android 2.2.1; en-us; Nexus One Build/FRG83)" +
                " AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
/* 设置超时时间。超时的异常均属于IOException，此外ClientProtocolException也是与IOException*/
// 从ClientConnectionManager获取连接的时间，这是从连接池中获取连接的超时设置，只有在连接池所有连接都在使用的情况下才可能出现超时。超时会扔出ConnectionPoolTimeoutException。一个HttpClient对应管理器，有连接池，里面有多个连接（socket），这是我对其架构的猜测。
        ConnManagerParams.setTimeout(params, 5000);
// 这是连接到远端web server的超时设置，超时会扔出ConnectTimeoutException
        HttpConnectionParams.setConnectionTimeout(params, 5000);//连接超时
// 这是发送请求消息后，最多等待多长时间得到响应的设置，超时会扔出SocketTimeoutException
        HttpConnectionParams.setSoTimeout(params, 10000);//socket超时
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        return new DefaultHttpClient(params);
    }

}
