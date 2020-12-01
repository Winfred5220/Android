package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.TimeDateUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Song
 * on 2020/8/14
 * Description：人資監餐 選餐界面
 */
public class DZFoodCheckActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.ll_dz_check)
    LinearLayout llCheck;//選擇某餐界面
    @BindView(R.id.btn_breakfast)
    Button btnBreakfast;//早餐
    @BindView(R.id.btn_lunch)
    Button btnLunch;//午餐
    @BindView(R.id.btn_dinner)
    Button btnDinner;//晚餐
    @BindView(R.id.btn_supper)
    Button btnSupper;//夜宵
    private Intent intent;
    private String type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dz_food);
        ButterKnife.bind(this);
        tvTitle.setText("請選擇餐別");
        btnBack.setText("返回");
        Log.e("-----", "DZFoodCheckActivity");
        llCheck.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnBreakfast.setOnClickListener(this);
        btnLunch.setOnClickListener(this);
        btnDinner.setOnClickListener(this);
        btnSupper.setOnClickListener(this);
        intent = new Intent(DZFoodCheckActivity.this, DZFoodMainActivity.class);
        type = getIntent().getStringExtra("type");
        FoxContext.getInstance().setType(type);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_breakfast://早餐
                if (TimeDateUtils.isContain(5, 30, 8, 0)) {
                    intent.putExtra("flag", "B");
                    startActivity(intent);
                } else {
                    ToastUtils.showLong(DZFoodCheckActivity.this, "監餐時間段05:30--08:00，請確認！");
                }

                break;
            case R.id.btn_lunch://午餐
                if (TimeDateUtils.isContain(10, 20, 12, 40)) {
                    intent.putExtra("flag", "L");
                    startActivity(intent);
                } else {
                    ToastUtils.showLong(DZFoodCheckActivity.this, "監餐時間段10:20--12:40，請確認！");
                }
                break;
            case R.id.btn_dinner://晚餐
                if (TimeDateUtils.isContain(16, 30, 20, 0)) {
                    intent.putExtra("flag", "D");
                    startActivity(intent);
                } else {
                    ToastUtils.showLong(DZFoodCheckActivity.this, "監餐時間段16:30--20:00，請確認！");
                }
                break;
            case R.id.btn_supper://宵夜
                if (TimeDateUtils.isContain(22, 30, 23, 50)) {
                    intent.putExtra("flag", "S");
                    startActivity(intent);
                } else {
                    ToastUtils.showLong(DZFoodCheckActivity.this, "監餐時間段22：30--23:50，請確認！");
                }
                break;
        }
    }


}
