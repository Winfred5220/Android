package com.example.administrator.yanfoxconn.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.widget.SmoothImageView;

public class BigImageActivity extends Activity {

    private String mImageUrl = "";
    public static final String QR_IMAGE_URL = "imageUrl";
    public static final String PHOTO_SELECT_POSITION = "PHOTO_SELECT_POSITION";
    public static final String PHOTO_SELECT_X_TAG = "PHOTO_SELECT_X_TAG";
    public static final String PHOTO_SELECT_Y_TAG = "PHOTO_SELECT_Y_TAG";
    public static final String PHOTO_SELECT_W_TAG = "PHOTO_SELECT_W_TAG";
    public static final String PHOTO_SELECT_H_TAG = "PHOTO_SELECT_H_TAG";

    private int locationX;
    private int locationY;
    private int locationW;
    private int locationH;
    private int position;
    private SmoothImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle b = intent.getExtras();
            mImageUrl = intent.getStringExtra(BigImageActivity.QR_IMAGE_URL);

//            position = intent.getIntExtra(PHOTO_SELECT_POSITION, 0);
            locationX = intent.getIntExtra(PHOTO_SELECT_X_TAG, 0);
            locationY = intent.getIntExtra(PHOTO_SELECT_Y_TAG, 0);
            locationW = intent.getIntExtra(PHOTO_SELECT_W_TAG, 0);
            locationH = intent.getIntExtra(PHOTO_SELECT_H_TAG, 0);
        }
        mImageView = findViewById(R.id.imageView2);
        if (!TextUtils.isEmpty(mImageUrl)) {
            // AppUtil.showToast(mImageUrl);
            byte[] decode = Base64.decode(mImageUrl, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            mImageView.setImageBitmap(bitmap);
        }

        // 设置进入动画
        mImageView.setOriginalInfo(locationW, locationH, locationX, locationY);
        mImageView.transformIn();

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishCurrentPage();
            }
        });

        // 设置退出动画必须
        mImageView.setOnTransformListener(new SmoothImageView.TransformListener() {
            @Override
            public void onTransformComplete(int mode) {
                if (mode == 2) { // 退出的时候 调用关闭界面
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishCurrentPage();
    }

    /**
     * 关闭当前界面
     */
    private void finishCurrentPage() {
        // 平稳退出
        mImageView.transformOut();
    }
}