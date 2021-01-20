package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 安全部 異常處置區
 * Created by wang on 2021/01/20.
 */

public class GCMainActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_scan)
    Button btnViolation;//日常消殺點檢
    @BindView(R.id.btn_write)
    Button btnTurnover;//體症異常追蹤
    @BindView(R.id.btn_ocr)
    Button btnVehicle;//處置後消殺點檢

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_mian);

        ButterKnife.bind(this);
        btnViolation.setText("日常消殺點檢");
        btnTurnover.setText("體症異常追蹤");
        btnVehicle.setText("處置後消殺點檢");
        btnViolation.setBackgroundColor(getResources().getColor(R.color.color_009adb));
        btnViolation.setTextColor(getResources().getColor(R.color.color_eeeeee));
        btnTurnover.setBackgroundColor(getResources().getColor(R.color.color_009adb));
        btnTurnover.setTextColor(getResources().getColor(R.color.color_eeeeee));
        btnVehicle.setBackgroundColor(getResources().getColor(R.color.color_009adb));
        btnVehicle.setTextColor(getResources().getColor(R.color.color_eeeeee));
        tvTitle.setText("異常處置區");

        btnBack.setOnClickListener(this);
        btnViolation.setOnClickListener(this);
        btnTurnover.setOnClickListener(this);
        btnVehicle.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_scan://日常消殺點檢
                FoxContext.getInstance().setType("HA");
                Intent intent = new Intent(GCMainActivity.this,QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                startActivity(intent);
                break;
            case R.id.btn_write://體症異常追蹤
//                Intent intent = new Intent(CommonformsActivity.this,EmpTurnoverActivity.class);
//                startActivity(intent);
                Intent intent2 = new Intent(GCMainActivity.this,CrossScanMainActivity.class);
                intent2.putExtra("flag","health");
                GCMainActivity.this.startActivity(intent2);

                break;
            case R.id.btn_ocr://處置後消殺點檢
                FoxContext.getInstance().setType("HB");
                Intent intent3 = new Intent(GCMainActivity.this,QrCodeActivity.class);
                intent3.putExtra("title", "掃描二維碼");
                intent3.putExtra("num", "cz");
                startActivity(intent3);
                break;
            case R.id.btn_title_left:
                finish();
                break;

        }
    }



}
