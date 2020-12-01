package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.utils.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZhiyinshuiMenuActivity extends BaseActivity implements View.OnClickListener {

    private String type,title,name;//類別,標題


    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTittle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.btn_xunjian)
    Button btnXunjian;//點檢
    @BindView(R.id.btn_weibao)
    Button btnWeibao;//維保
    @BindView(R.id.btn_view)
    Button btnView;//點檢進度
    @BindView(R.id.btn_weibao_view)
    Button btnWeibaoView;//維保查看
    @BindView(R.id.btn_exce)
    Button btnExce;//異常查看
    @BindView(R.id.ll_bg)
    LinearLayout llBg;//背景
    @BindView(R.id.tv_name)
    TextView tvName;//點檢名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhiyinshui_menu);
        ButterKnife.bind(this);

        btnBack.setOnClickListener(this);
        btnXunjian.setOnClickListener(this);
        btnWeibao.setOnClickListener(this);
        btnView.setOnClickListener(this);
        btnWeibaoView.setOnClickListener(this);
        btnExce.setOnClickListener(this);
        type = getIntent().getStringExtra("type");
        title = getIntent().getStringExtra("title");
        name = getIntent().getStringExtra("name");
        tvTittle.setText(title);
        if (!type.equals("DQ")){
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(name);
            llBg.setBackground(getResources().getDrawable(R.mipmap.bg_2));
        }


    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_title_right://提交

                break;
            case R.id.btn_xunjian://點檢
                Intent resultIntent = new Intent(ZhiyinshuiMenuActivity.this, QrCodeActivity.class);
                resultIntent.putExtra("title", "二維碼掃描");
                resultIntent.putExtra("num", "dq_check");
                resultIntent.putExtra("type", type);
                startActivity(resultIntent);
                break;
            case R.id.btn_weibao://維保
                Intent resultIntent4 = new Intent(ZhiyinshuiMenuActivity.this, QrCodeActivity.class);
                resultIntent4.putExtra("title", "二維碼掃描");
                resultIntent4.putExtra("num","dq_maintain");
                resultIntent4.putExtra("type", type);
                startActivity(resultIntent4);
                break;
            case R.id.btn_view://點檢進度
                Intent resultIntent3 = new Intent(ZhiyinshuiMenuActivity.this, ZhiyinshuiCheckProcessActivity.class);
                resultIntent3.putExtra("type", type);
                startActivity(resultIntent3);
                break;
            case R.id.btn_weibao_view://維保查詢
                Intent resultIntent5 = new Intent(ZhiyinshuiMenuActivity.this, ZhiyinshuiMaintainListActivity.class);
                resultIntent5.putExtra("type", type);
                startActivity(resultIntent5);
                break;
            case R.id.btn_exce://異常查看
                Intent resultIntent2 = new Intent(ZhiyinshuiMenuActivity.this, ZhiyinshuiExceDeviceListActivity.class);
                resultIntent2.putExtra("type", type);
                startActivity(resultIntent2);
                break;
        }
    }
}