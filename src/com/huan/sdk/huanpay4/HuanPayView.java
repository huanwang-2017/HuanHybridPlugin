package com.huan.sdk.huanpay4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.*;
import android.widget.Toast;
import com.huan.sdk.huanpay4.been.PayInfo;
import com.huan.sdk.huanpay4.util.PayUtil;

public class HuanPayView extends WebView {
    public static final String TAG = HuanPayView.class.getSimpleName();
    public String orderNO = "0";
    public int state = 0;
    private WebView mWebView;
    private Activity activity;
    private Handler handler;

    public HuanPayView(Context context) {
        super(context);
    }

    public HuanPayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HuanPayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void load(final Activity activity, PayInfo mPayInfo) {
        this.activity = activity;
        this.mWebView = this;
        handler = new Handler();
        WebSettings webSettings = mWebView.getSettings();
        // 告诉WebView先不要自动加载图片，等页面finish后再发起图片加载
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
        // Build.BRAND
        // webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 默认不使用缓存！
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置 缓存模式
        // webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        // 开启 DOM storage API 功能
        // webSettings.setDomStorageEnabled(true);

        // 设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings
                .setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);

        final String USER_AGENT_STRING = webSettings.getUserAgentString()
                + " Rong/2.0";
        webSettings.setUserAgentString(USER_AGENT_STRING);
        webSettings.setSupportZoom(false);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setLoadWithOverviewMode(true);

        // 点击链接继续在当前browser中响应
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              String url) {
                WebResourceResponse response = null;
                response = super.shouldInterceptRequest(view, url);
                return response;
            }

            /**
             * 加载https时候，需要加入 下面代码
             */
            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                // 接受所有证书
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // super.onPageFinished(view, url);
                if (!mWebView.getSettings().getLoadsImagesAutomatically()) {
                    mWebView.getSettings().setLoadsImagesAutomatically(true);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                Log.d(TAG, "加载出错");
                Toast.makeText(activity, "网络出错，请稍后再试！", Toast.LENGTH_SHORT).show();
                activity.finish();
            }

        });

        // 设置setWebChromeClient对象
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                /*
                 * if (mProgressBar.getVisibility() == View.GONE) {
                 * mProgressBar.setVisibility(View.VISIBLE); }
                 * mProgressBar.setProgress(newProgress); if (newProgress ==
                 * 100) { //mProgressBar.setVisibility(View.GONE); }
                 */
            }
        });
        mWebView.addJavascriptInterface(getHtmlObject(), "jsAndroidObj");
        mWebView.setFocusable(true);
        mWebView.requestFocus();
        mWebView.setBackgroundColor(0); // 设置背景色
//        mWebView.getBackground().setAlpha(230); // 设置填充透明度 范围：0-255
        String params = PayUtil.getInstance().getInitParams(getContext(), mPayInfo);
        Log.i(TAG, "URL：" + Constants.URL + "params:" + params);
//        mWebView.loadUrl("http://hejiahuan.huan.tv/index.html");
//        mWebView.loadUrl("http://103.235.237.119:8080/html/index.html");
        mWebView.loadUrl(Constants.URL + params);
    }

    /**
     * js和android交互方法
     *
     * @return
     */
    private Object getHtmlObject() {
        Object insertObj = new Object() {
            @JavascriptInterface
            public void closePay() {
                new OrderTask().execute();
            }

            @JavascriptInterface
            public void banFocus() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.setFocusable(false);
                    }
                });
            }

            @JavascriptInterface
            public void hasFocus() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.setFocusable(true);
                    }
                });
            }

            @JavascriptInterface
            public void setOrderNo(String orderNo) {
                orderNO = orderNo;
            }
        };
        return insertObj;
    }

    class OrderTask extends AsyncTask<String, Void, Boolean> {
        private String message;

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                message = PayService.searchOrderResult(orderNO);
                return true;
            } catch (Exception e) {
                message = e.getMessage();
                Log.e(TAG, e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                if (message.equals("orderCompleted")) {
                    state = 1;
                } else {
                    state = 0;
                }
                Intent intent = new Intent();
                intent.putExtra("state", state);
                //设置返回数据
                activity.setResult(-1, intent);
                activity.finish();
            } else {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        }
    }

}
