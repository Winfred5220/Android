package com.example.administrator.yanfoxconn.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 跳轉網頁 碼頭 或 協管開單 或巡檢報表
 * Created by song on 2017/10/27.
 */

public class WebViewActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    WebView mWebview;
    WebSettings mWebSettings;
    TextView beginLoading,endLoading,loading,mtitle;
    private String role;//權限
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        ButterKnife.bind(this);
        mWebview = (WebView) findViewById(R.id.webView1);
        beginLoading = (TextView) findViewById(R.id.text_beginLoading);
        endLoading = (TextView) findViewById(R.id.text_endLoading);
        loading = (TextView) findViewById(R.id.text_Loading);
        mtitle = (TextView) findViewById(R.id.title);

        Bundle bundle = this.getIntent().getExtras();
        role = bundle.getString("role");

        if (role.equals("F0")) {
            tvTitle.setText("碼頭出貨信息表");
        }else if (role.equals("FX")) {
            tvTitle.setText("鋰電池防火報表");
        }else if (role.equals("FW")) {
            tvTitle.setText("營建設備報表");
        }else if (role.equals("LMNOP")) {
            tvTitle.setText("商鋪物業巡檢");
        }else{
            tvTitle.setText("跨區申請單");
        }
        btnBack.setOnClickListener(this);

        mWebSettings = mWebview.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setAllowContentAccess(true);
        mWebSettings.setAppCacheEnabled(false);
        mWebview.setVerticalScrollbarOverlay(true); //指定的垂直滚动条有叠加样式
        mWebSettings.setUseWideViewPort(true);//设定支持viewport

        mWebSettings.setLoadWithOverviewMode(true);

        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mWebSettings.setSupportZoom(true);//设定支持缩放

        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        if (role.equals("F0")) {//碼頭出貨信息表
            mWebview.loadUrl(Constants.HTTP_WEBVIEW +"forward/matou/mt.jsp");
        }else if (role.equals("FX")) {//鋰電池報表
            mWebview.loadUrl(Constants.HTTP_WEBVIEW+"baobiao/SafeCheck_last_week.jsp");
        }else if (role.equals("FW")) {//營建設備報表
            //mWebview.loadUrl(Constants.SafeServer+"baobiao/SafeCheck_datetime_yingjian.jsp");
            mWebview.loadUrl(Constants.HTTP_WEBVIEW+"baobiao/SafeCheck_datetime_yingjian.jsp");

        }else if (role.equals("LMNOP")){
            mWebview.loadUrl(Constants.HTTP_WEBVIEW+"forward/matou/mt_report.jsp?packing_no="+getIntent().getStringExtra("num"));

        }else{//跨區申請單
            mWebview.loadUrl("http://60.212.41.39/exportio/people/info_view.jsp?login_code="+ FoxContext.getInstance().getLoginId());
        }

        //设置不用系统浏览器打开,直接显示在当前Webview
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        //设置WebChromeClient类
        mWebview.setWebChromeClient(new WebChromeClient() {
            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                System.out.println("标题在这里");
                mtitle.setText(title);
            }
            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    String progress ="加載中..."+ newProgress + "%";
                    loading.setText(progress);
                    loading.setVisibility(View.VISIBLE);
                } else if (newProgress == 100) {
                    String progress ="加載完成"+  newProgress + "%";
                    loading.setText(progress);
                    loading.setVisibility(View.GONE);
                }
            }
        });


        //设置WebViewClient类
        mWebview.setWebViewClient(new WebViewClient() {
            //设置加载前的函数
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                beginLoading.setText("正在玩命加載...");

            }

            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
                endLoading.setText("加載完成");

            }
        });
    }

    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();

            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }
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
