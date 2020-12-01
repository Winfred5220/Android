package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.OCRMessage;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.RecognizeService;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * 移動上網設備管制输入厂牌頁面
 * Created by wangqian on 2019/3/22.
 */

public class MobilecontrolInputActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_ACCURATE_BASIC = 107;

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回

    @BindView(R.id.btn_scan)
    Button btnScan;//提交
    @BindView(R.id.et_code)
    EditText etCode;//工號
    @BindView(R.id.btn_scan_brand)
    ImageButton ibScan;//拍攝
    @BindView(R.id.btn_up)
    Button btnUp;//提交

    private boolean hasGotToken = false;

    private AlertDialog.Builder alertDialog;
    private List<OCRMessage> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobilecontrol_input);

        ButterKnife.bind(this);
        alertDialog = new AlertDialog.Builder(this);

        tvTitle.setText("移動上網設備管制");

        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        ibScan.setOnClickListener(this);
        btnScan.setOnClickListener(this);

        initAccessTokenWithAkSk();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_up:
                if (etCode.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "工號輸入結果不能為空!");

                } else {
                    Intent resultIntent = new Intent(MobilecontrolInputActivity.this, MobilecontrolMessageActivity.class);
                    resultIntent.putExtra("id", etCode.getText().toString().toUpperCase());
                    startActivity(resultIntent);
                    Log.e("----------", "etCode===" + etCode.getText().toString());
                }
                break;
            case R.id.btn_scan_brand://OCR識別
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(MobilecontrolInputActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_ACCURATE_BASIC);

                break;
            case R.id.btn_scan:
                Intent intent2 = new Intent(MobilecontrolInputActivity.this, QrCodeActivity.class);
                intent2.putExtra("title", "二維碼掃描");
                intent2.putExtra("num", "mobile");
                startActivity(intent2);
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
        }, getApplicationContext(), "d6PEEyy6O8WHXGHnmA4vApQo", "3h2BO8M1Kv4IGhXuRg4ylshPt6mNBeWw");
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
        Log.e("-----------", "requestCode, resultCode, data===" + requestCode + "-" + resultCode);
        if (resultCode == -1) {
            RecognizeService.recAccurateBasic(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {

                            Gson gson = new Gson();
                            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                            JsonArray array = jsonObject.get("words_result").getAsJsonArray();
                            messageList = new ArrayList<OCRMessage>();
                            if (array.size() == 0) {
                                ToastUtils.showLong(MobilecontrolInputActivity.this, "拍照無效,請手動輸入!");
                            } else {
                                for (JsonElement type : array) {
                                    OCRMessage humi = gson.fromJson(type, OCRMessage.class);
                                    messageList.add(humi);
                                }

                                infoPopText(messageList.get(messageList.size() - 1).getWords());
                            }

                        }
                    });
        }
    }

    private void infoPopText(final String result) {
//        alertText("", result);
        etCode.setText(result);
        Log.e("-------------", "ocr===" + result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        OCR.getInstance(this).release();
    }
}
