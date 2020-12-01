package com.example.administrator.yanfoxconn.activity;

import android.os.Bundle;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.utils.BaseActivity;

import androidx.annotation.Nullable;


/**
 * 圖片縮放
 * Created by song on 2017/12/22.
 */

public class SpaceImageDetailActivity extends BaseActivity{
//    @InjectView(R.id.siv_space)
//    SmoothImageView sivSpace;

    private int mPosition,mLocationX,mLocationY,mWidth,mHeight;
    private String mDatas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_image_detail);
//        sivSpace

//        mDatas = getIntent().getStringExtra("images");
//        mPosition = getIntent().getIntExtra("position", 0);
//        mLocationX = getIntent().getIntExtra("locationX", 0);
//        mLocationY = getIntent().getIntExtra("locationY", 0);
//        mWidth = getIntent().getIntExtra("width", 0);
//        mHeight = getIntent().getIntExtra("height", 0);


    }

}
