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
 * 工業安全 鋰電池防火共用
 * Created by wang on 2021/01/20.
 */

public class GCMainActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_scan)
    Button btnViolation;//日常消殺點檢  鋰電池充電區
    @BindView(R.id.btn_write)
    Button btnTurnover;//體症異常追蹤 鋰電池儲存區
    @BindView(R.id.btn_ocr)
    Button btnVehicle;//處置後消殺點檢  鋰電池使用區
    @BindView(R.id.btn_self)
    Button btnSelf;//處置後消殺點檢  充電樁點檢
    @BindView(R.id.btn_search)
    Button btnSearch;//已有記錄查詢 上報無法點檢異常

    String type="";//類別

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_mian);
        ButterKnife.bind(this);


        type=getIntent().getStringExtra("type");
        if(type!=null&&type.equals("FX")){
            btnSelf.setVisibility(View.VISIBLE);
            btnSearch.setVisibility(View.VISIBLE);
            tvTitle.setText("鋰電池防火");
            btnViolation.setText("充電區點檢");
            btnTurnover.setText("儲存區點檢");
            btnVehicle.setText("使用區點檢");
            btnSelf.setText("充電樁點檢");
            btnSearch.setText("無法點檢");
        }else{
            tvTitle.setText("異常處置區");
            btnViolation.setText("日常消殺點檢");
            btnTurnover.setText("體症異常追蹤");
            btnVehicle.setText("處置後消殺點檢");
        }

        btnViolation.setBackgroundColor(getResources().getColor(R.color.color_009adb));
        btnViolation.setTextColor(getResources().getColor(R.color.color_eeeeee));
        btnTurnover.setBackgroundColor(getResources().getColor(R.color.color_009adb));
        btnTurnover.setTextColor(getResources().getColor(R.color.color_eeeeee));
        btnVehicle.setBackgroundColor(getResources().getColor(R.color.color_009adb));
        btnVehicle.setTextColor(getResources().getColor(R.color.color_eeeeee));
        btnSearch.setBackgroundColor(getResources().getColor(R.color.color_f5a306));
        btnSearch.setTextColor(getResources().getColor(R.color.color_eeeeee));
        btnSelf.setBackgroundColor(getResources().getColor(R.color.color_009adb));
        btnSelf.setTextColor(getResources().getColor(R.color.color_eeeeee));

        btnBack.setOnClickListener(this);
        btnViolation.setOnClickListener(this);
        btnTurnover.setOnClickListener(this);
        btnVehicle.setOnClickListener(this);
        btnSelf.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if(type!=null&&type.equals("FX")){
            switch (view.getId()){
                case R.id.btn_scan://充電區點檢
                    FoxContext.getInstance().setType("FX");
                    Intent intent = new Intent(GCMainActivity.this,QrCodeActivity.class);
                    intent.putExtra("title", "掃描二維碼");
                    intent.putExtra("num", "cz");
                    startActivity(intent);
                    break;
                case R.id.btn_write://儲存區點檢
                    FoxContext.getInstance().setType("HC");
                    Intent intent2 = new Intent(GCMainActivity.this,QrCodeActivity.class);
                    intent2.putExtra("title", "掃描二維碼");
                    intent2.putExtra("num", "cz");
                    startActivity(intent2);

                    break;
                case R.id.btn_ocr://使用區點檢
                    FoxContext.getInstance().setType("HD");
                    Intent intent3 = new Intent(GCMainActivity.this,QrCodeActivity.class);
                    intent3.putExtra("title", "掃描二維碼");
                    intent3.putExtra("num", "cz");
                    startActivity(intent3);
                    break;
                case R.id.btn_self://無法點檢
                    FoxContext.getInstance().setType("HK");
                    Intent intent5 = new Intent(GCMainActivity.this,QrCodeActivity.class);
                    intent5.putExtra("title", "掃描二維碼");
                    intent5.putExtra("num", "cz");
                    startActivity(intent5);
                    break;
                case R.id.btn_search://充電樁點檢
                    Intent intent4 = new Intent(GCMainActivity.this,AbnormalCantCheckActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.btn_title_left:
                    finish();
                    break;

            }
        }else{
            switch (view.getId()){
                case R.id.btn_scan://日常消殺點檢
                    FoxContext.getInstance().setType("HA");
                    Intent intent = new Intent(GCMainActivity.this,QrCodeActivity.class);
                    intent.putExtra("title", "掃描二維碼");
                    intent.putExtra("num", "cz");
                    startActivity(intent);
                    break;
                case R.id.btn_write://體症異常追蹤
                    Intent intent2 = new Intent(GCMainActivity.this,CrossScanMainActivity.class);
                    intent2.putExtra("flag","health");
                    startActivity(intent2);
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



}
