package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.FileUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 跨區無紙化 主界面
 * 常用表單 輸入賬號界面
 * 健康追蹤  輸入身份證或工號
 * Created by song on 2018/8/22.
 */

public class CrossScanMainActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final int REQUEST_CODE_ACCURATE_BASIC = 107;
    private static final int REQUEST_CODE_LICENSE_PLATE = 122;

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_scan)
    Button btnScan;//掃描進入
    @BindView(R.id.btn_write)
    Button btnWrite;//手動輸入
    @BindView(R.id.btn_ocr)
    Button btnOCR;//文字識別
    @BindView(R.id.rg_check)
    RadioGroup rgCheck;//安保部-健康追蹤 顯示
    @BindView(R.id.rtb_out)
    RadioButton rtbOut;//廠商
    @BindView(R.id.rtb_worker)
    RadioButton rtbWorker;//員工

    private boolean hasGotToken = false;

    private AlertDialog.Builder alertDialog;private String flag="";//判斷是協管跨區無紙化,還是安全部三個常用表單,health是健康追蹤
    private String check="2";//健康追蹤下是 廠商 還是 員工


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_mian);
        ButterKnife.bind(this);
        alertDialog = new AlertDialog.Builder(this);
        btnBack.setOnClickListener(this);
        btnScan.setOnClickListener(this);
        btnWrite.setOnClickListener(this);
        btnOCR.setOnClickListener(this);

        rgCheck.setOnCheckedChangeListener(this::onCheckedChanged);
        rtbWorker.setOnClickListener(this::onClick);
        rtbOut.setOnClickListener(this::onClick);
        rtbWorker.setChecked(true);
//        Bundle bundle = this.getIntent().getExtras();
        flag = getIntent().getStringExtra("flag");
        if (flag.equals("health")) {
            btnScan.setVisibility(View.GONE);
            rgCheck.setVisibility(View.VISIBLE);
        }
        tvTitle.setText("輸入方式");
        // 请选择您的初始化方式
        // initAccessToken();
        initAccessTokenWithAkSk();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_scan://掃描進入
                Intent intent = new Intent(CrossScanMainActivity.this, QrCodeActivity.class);
                intent.putExtra("title", "二維碼掃描");
                intent.putExtra("num", flag);
                startActivity(intent);
            break;
            case R.id.btn_write://手動錄入
                Intent intent1 = new Intent(CrossScanMainActivity.this, CarWriteIdActivity.class);
                intent1.putExtra("from", flag + "Scan");
                intent1.putExtra("check", check);
                Log.e("----------", "check===" + check);
                startActivity(intent1);
                break;
            case R.id.btn_ocr://OCR識別
                if (!checkTokenStatus()) {
                    return;
                }

                Intent intent2 = new Intent(CrossScanMainActivity.this, CameraActivity.class);
                intent2.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent2.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                if(flag.equals("car")){
                    //車牌識別
                    startActivityForResult(intent2, REQUEST_CODE_LICENSE_PLATE);
                }else {
                    //文字識別
                    startActivityForResult(intent2, REQUEST_CODE_ACCURATE_BASIC);
                }
                break;
        }
    }

    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

    /**
     * 以license文件方式初始化
     */
    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("licence方式获取token失败", error.getMessage());
            }
        }, getApplicationContext());
    }

    /**
     * 用明文ak，sk初始化
     */
    private void initAccessTokenWithAkSk() {
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, getApplicationContext(),  "d6PEEyy6O8WHXGHnmA4vApQo", "3h2BO8M1Kv4IGhXuRg4ylshPt6mNBeWw");
    }


    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initAccessToken();
        } else {
            Toast.makeText(getApplicationContext(), "需要android.permission.READ_PHONE_STATE", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode!=0) {
            Intent intent = new Intent(CrossScanMainActivity.this, CarWriteIdActivity.class);
            if (flag.equals("cross")) {
                intent.putExtra("from", "crossOCR");
            }else if(flag.equals("car")){
                intent.putExtra("from", "carOCR");
            }  else if (flag.equals("health")) {

                intent.putExtra("from", "healthOCR");
                intent.putExtra("check", check);
            } else {
                intent.putExtra("from", flag + "OCR");
            }
            startActivity(intent);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        OCR.getInstance(this).release();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rtb_out:
                check = "1";
                break;
            case R.id.rtb_worker:
                check = "2";
                break;
        }
    }
}
