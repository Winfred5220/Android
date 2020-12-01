package com.example.administrator.yanfoxconn.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.zxing.encoding.EncodingUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 展示活動二維碼,界面
 * Created by song on 2018/7/9.
 */

public class EventShowActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_event_name)
    TextView tvEventName;//活動名稱
    @BindView(R.id.iv_code)
    ImageView ivCode;//二維碼

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_show);

        ButterKnife.bind(this);

        tvTitle.setText("活動二維碼");
        tvEventName.setText(getIntent().getStringExtra("eventName"));
        btnBack.setOnClickListener(this);

        //size给固定的值，避免使用iamgeview.getWidth,导致二维码模糊
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.a16);
        Bitmap bitmap = EncodingUtils.createQRCode(getIntent().getStringExtra("dimId").toString().trim(), 800, 800, null);
        ivCode.setImageBitmap(bitmap);
    }


    @Override
    public void onClick(View view) {
        finish();
    }
}
