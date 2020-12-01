package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 廢料出廠输入頁面
 * Created by wangqian on 2019/5/31.
 */

public class ScrapInputActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回

    @BindView(R.id.et_code)
    EditText etCode;//過磅單號
    @BindView(R.id.btn_up)
    Button btnUp;//提交

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrap_input);
        ButterKnife.bind(this);

        tvTitle.setText("廢料出廠");

        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_up:
                if (etCode.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "輸入結果不能為空!");
                } else {
                    Intent resultIntent = new Intent(ScrapInputActivity.this, ScrapLeaveActivity.class);
                    resultIntent.putExtra("code", etCode.getText().toString());
                    startActivity(resultIntent);
                }
                break;
        }
    }


}
