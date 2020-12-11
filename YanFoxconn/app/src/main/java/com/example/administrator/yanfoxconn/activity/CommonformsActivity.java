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
 * 安全部 常用表單
 * Created by song on 2018/11/27.
 */

public class CommonformsActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_scan)
    Button btnViolation;//員工違規
    @BindView(R.id.btn_write)
    Button btnTurnover;//員工進出
    @BindView(R.id.btn_ocr)
    Button btnVehicle;//二輪車
    @BindView(R.id.btn_self)
    Button btnSelf;//私家車


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_mian);

        ButterKnife.bind(this);
        btnViolation.setText(getString(R.string.emp_wrong));
        btnTurnover.setText(getString(R.string.emp_turnover));
        btnVehicle.setText(getString(R.string.two_wheel_vehicle));
        btnSelf.setText("私家車違規登記表");
        btnViolation.setBackgroundColor(getResources().getColor(R.color.color_009adb));
        btnViolation.setTextColor(getResources().getColor(R.color.color_eeeeee));
        btnTurnover.setBackgroundColor(getResources().getColor(R.color.color_009adb));
        btnTurnover.setTextColor(getResources().getColor(R.color.color_eeeeee));
        btnVehicle.setBackgroundColor(getResources().getColor(R.color.color_009adb));
        btnVehicle.setTextColor(getResources().getColor(R.color.color_eeeeee));
        btnSelf.setBackgroundColor(getResources().getColor(R.color.color_009adb));
        btnSelf.setTextColor(getResources().getColor(R.color.color_eeeeee));
        tvTitle.setText("常用表單");

        btnBack.setOnClickListener(this);
        btnViolation.setOnClickListener(this);
        btnTurnover.setOnClickListener(this);
        btnVehicle.setOnClickListener(this);
        btnSelf.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_scan://員工違規
                Intent intent = new Intent(CommonformsActivity.this,CrossScanMainActivity.class);
                intent.putExtra("flag","wrong");
                startActivity(intent);
                break;
            case R.id.btn_write://員工進出
//                Intent intent = new Intent(CommonformsActivity.this,EmpTurnoverActivity.class);
//                startActivity(intent);
                Intent intent2 = new Intent(CommonformsActivity.this,CrossScanMainActivity.class);
                intent2.putExtra("flag","inout");
                startActivity(intent2);

                break;
            case R.id.btn_ocr://二輪車違規
                Intent intent3 = new Intent(CommonformsActivity.this,CrossScanMainActivity.class);
                intent3.putExtra("flag","vehicle");
                startActivity(intent3);
                break;
            case R.id.btn_self://私家車違規
                Intent intent4 = new Intent(CommonformsActivity.this,CommonFormsPrivateCarActivity.class);
                startActivity(intent4);
                break;
            case R.id.btn_title_left:
                finish();
                break;

        }
    }



}
