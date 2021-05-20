package com.example.administrator.yanfoxconn.activity;



import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.itheima.view.BridgeWebView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 跳轉網頁 碼頭 或 協管開單 或巡檢報表
 * Created by song on 2017/10/27.
 */

public class WebViewTestActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    private BridgeWebView webView;
    WebSettings mWebSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_webview);
        ButterKnife.bind(this);

        btnBack.setOnClickListener(this);
        tvTitle.setText("工作互聯");

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏

        webView = (BridgeWebView)findViewById(R.id.activity_test_webView);
        String url = "https://hhs.sdzjdxu.com/healthview/WorkPlatform/login.html";//带Echarts图表的网址
        mWebSettings = webView.getSettings();
        //localStorage  允许存储
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setAllowContentAccess(true);
        mWebSettings.setUseWideViewPort(true);//设定支持viewport
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setBuiltInZoomControls(true);
        //mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebSettings.setSupportZoom(true);//设定支持缩放
        //mWebview.setDefaultHandler(new DefaultHandler());
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //localStorage  允许存储
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setLoadsImagesAutomatically(true) ;//支持自动加载图片
        //关键setGeolocationEnabled
        mWebSettings.setGeolocationEnabled(true);//开启定位


        webView.loadUrl(url);//显示H5页面


        //设置不用系统浏览器打开,直接显示在当前Webview
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });

        //设置WebChromeClient类
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,android.webkit.GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

        });
    }
    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left:
                finish();
                break;
        }
    }
}
