/**   
* @Title:   AppNetComThread.java 
* @Package: com.huan.apsclient.netcom   
* @Copyright: Copyright (c) 2011
* @Company: 广州欢网科技有限责任公司
* @author：   李森   
* @date:    2011-10-31 上午11:55:11
* @version: V1.0   
*/

package com.huan.hhp.net;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Http 工具
 * @param <A>	请求报文
 * @param <B> 封装类型（返回报文为String类型，B是返回内容的解析类）
 */
public abstract class HttpTask<A, B> extends Thread {
	static final String TAG = HttpTask.class.getSimpleName();

	public interface CustomResponse {
		void onCustom(HttpTask httpTask, InputStream inputStream);
	}

	/**
	 * 请求接口
	 * @param requestBody 请求报文封装对象
	 * @return
     */
	protected abstract String onRequest(A requestBody);

	/**
	 * 响应接口
	 * @param retnString 响应报文
	 * @return
     */
	protected abstract B onResponse(String retnString);

	/**
	 * 通知Ui线程
	 * @param code	 // 服务器返回code
	 * @param arg	// 服务器返回内容的封装对象
     */
	protected void onPost2Ui(int code, B arg){

	}

	private Handler m2UiHandler = new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg) {
			onPost2Ui(msg.what, (B)msg.obj);
		}
	};


	public static final String HTTP_POST = "POST";
	public static final String HTTP_GET = "GET";
	public static final String HTTP_PUT = "PUT";
	public static final String HTTP_DELETE = "DELETE";

	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";

	/**
	 * 网络延迟
	 */
	private static final int NET_TIMEOUT_TICK = 10*1000;

	/**
	 * 是否为可执行状态
	 * 如果设置为false将再也无法接收到服务器返回的数据。
	 */
	private boolean enable = true;

	private int retnCode = -1;	// 返回码
	private String mRetnString = null;	// 返回内容
	private Exception e;	// 异常信息

	private String address;
	private A requestBody; // 参数

	private String requestMethod = HTTP_POST;
	private String contentType = APPLICATION_JSON;

	private CustomResponse mCustomResponse;

	/**
	 * 设置 requestMethod
	 * @param requestMethod POST GET PUT DELETE 等
     */
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	/**
	 * 设置 Content-Type
	 * @param contentType
     */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * 设置运行状态
	 * @param enable
     */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * 设置运行状态为 运行
	 */
	public void setEnable(){
		this.enable = true;
	}

	/**
	 * 获取返回内容
	 * @return
     */
	public String getRetnString() {
		return mRetnString;
	}

	/**
	 * 获取异常信息
	 * @return
     */
	public Exception getException() {
		return e;
	}


	public String getAddress() {
		return address;
	}

	public A getRequestBody() {
		return requestBody;
	}

	public int getRetnCode() {
		return retnCode;
	}

	/**
	 * 设置请求地址
	 * @param address
     */
	public HttpTask setAddress(String address) {
		this.address = address;
		return this;
	}

	/**
	 * 设置请求数据
	 * @param requestBody
	 * @return
     */
	public HttpTask setRequestBody(A requestBody) {
		this.requestBody = requestBody;
		return this;
	}

	public void setCustomResponse(CustomResponse customResponse) {
		this.mCustomResponse = customResponse;
	}

	@Override
	public void run() {
		Log.i(TAG, "request start address is "+address);
		String requestContent = null;
		URL httpUrl;
		/** 获取请求内容(XML)*/
		requestContent = onRequest(requestBody);
		if(requestContent == null){
			requestContent = "";
		}
		/** 用于发送request*/
		HttpURLConnection httpURLConn = null;
		DataOutputStream dataOutput = null;
		
		/** 用于接受服务器响应*/
		InputStreamReader streamReader = null;
		BufferedReader reader = null ;
		String line = null;
		
		try {
			Log.i(TAG, "Send JSON Request = " + requestContent);
			byte[] data = requestContent.getBytes("UTF-8");

			// 设置连接参数，根据指定URL连接资源服务器
			httpUrl = new URL(this.address);
			
			httpURLConn = (HttpURLConnection) httpUrl.openConnection();
			httpURLConn.setDoOutput(true);
			httpURLConn.setDoInput(true);
			httpURLConn.setRequestMethod(requestMethod);
			httpURLConn.setConnectTimeout(NET_TIMEOUT_TICK);
			httpURLConn.setReadTimeout(NET_TIMEOUT_TICK);
			httpURLConn.setRequestProperty("Content-Type", contentType + "; charset=UTF-8");
			httpURLConn.setRequestProperty("Content-Length", String.valueOf(data.length));
			httpURLConn.connect();// 连接服务器
			Log.i(TAG, "connected");

			// 开始发送请求
			dataOutput = new DataOutputStream(httpURLConn.getOutputStream());
			dataOutput.write(data);// 发送请求
			dataOutput.flush();
			dataOutput.close();
			dataOutput = null;
			Log.i(TAG, "server back data ...");
			
			// 接受服务器的响应
			mRetnString = "";
			if ((retnCode=httpURLConn.getResponseCode()) != HttpURLConnection.HTTP_OK) {
				Log.e(TAG, "httpURLConn Response Error! HttpURL Code:" + httpURLConn.getResponseCode());
				throw new IOException(); 
			}
			InputStream inputStrean = httpURLConn.getInputStream();
			if (inputStrean == null) {
				Log.e(TAG, "Download InputStream Error!");
				throw new IOException();
			}
			if (mCustomResponse == null) {
				streamReader = new InputStreamReader(inputStrean);
				reader = new BufferedReader(streamReader);
				StringBuffer buffer = new StringBuffer();
				while (enable && (line = reader.readLine()) != null) {
					buffer.append(line).append("\n");
				}
				// 服务器响应信息
				mRetnString = buffer.toString();
			} else {
				mCustomResponse.onCustom(this, inputStrean);
			}

			Log.i(TAG, "over");
		}catch (IOException ioex) {
			/** 进行异常处理*/
			e = ioex;
		}catch (Exception ex) {
			/** 进行异常处理*/
			e = ex;
		}finally{
			/** 关闭请求连接及IO流*/
			if(httpURLConn != null) {
				httpURLConn.disconnect();
			}

			if(reader != null){
				try{
					reader.close();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}

			if(streamReader != null){
				try{
					streamReader.close();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}

			if(dataOutput != null){
				try{
					dataOutput.flush();
					dataOutput.close();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}

			// 发送Message通知：表示请求结束
			if(enable){
				m2UiHandler.sendMessage(m2UiHandler.obtainMessage(retnCode, onResponse(mRetnString)));
			}

			System.gc();

		}
	}
}
