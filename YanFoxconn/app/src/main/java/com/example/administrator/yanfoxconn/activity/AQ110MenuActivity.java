package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.utils.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 110接處警 菜單
 * Created by wang on 2021/03/29.
 */

public class AQ110MenuActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_scan)
    Button btnOCR;//
    @BindView(R.id.btn_write)
    Button btnJQTB;//警情通報
    @BindView(R.id.btn_ocr)
    Button btnCJFK;//處警反饋
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_mian);
        ButterKnife.bind(this);

        btnJQTB.setText("警情通報");
        btnCJFK.setText("處警反饋");
        btnOCR.setVisibility(View.GONE);
        btnJQTB.setBackgroundColor(getResources().getColor(R.color.color_009adb));
        btnJQTB.setTextColor(getResources().getColor(R.color.color_eeeeee));
        btnCJFK.setBackgroundColor(getResources().getColor(R.color.color_009adb));
        btnCJFK.setTextColor(getResources().getColor(R.color.color_eeeeee));
        tvTitle.setText("110接處警");

        btnBack.setOnClickListener(this);
        btnJQTB.setOnClickListener(this);
        btnCJFK.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_write://警情通報
                Intent intent5 = new Intent(AQ110MenuActivity.this,CommonFormsJqtbActivity.class);
                startActivity(intent5);
                break;
            case R.id.btn_ocr://處警反饋
                Intent intent6 = new Intent(AQ110MenuActivity.this, CommonFormsCjfkListActivity.class);
                startActivity(intent6);
                break;
            case R.id.btn_title_left://返回
                finish();
                break;
        }
    }



}
