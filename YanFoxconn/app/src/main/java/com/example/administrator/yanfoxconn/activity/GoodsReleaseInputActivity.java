package com.example.administrator.yanfoxconn.activity;

import android.content.DialogInterface;
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
import com.example.administrator.yanfoxconn.constant.FoxContext;
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
 * 物品放行
 * 手動輸入工號/车牌号
 * Created by S1007989 on 2018/12/20.
 */

public class GoodsReleaseInputActivity extends BaseActivity implements View.OnClickListener{

    private static final int REQUEST_CODE_ACCURATE_BASIC = 107;

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnBlue;//補錄

    @BindView(R.id.btn_up)
    Button btnUp;//普通物品提交
    @BindView(R.id.btn_up_z)
    Button btnUpZ;//智慧物品提交
    @BindView(R.id.et_input_num)
    EditText etWorkId;//车牌號 或 工號
    @BindView(R.id.btn_scan_brand)
    ImageButton ibScan;//扫描

    private boolean hasGotToken = false;

    private AlertDialog.Builder alertDialog;
    private List<OCRMessage> messageList;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_release_input);

        ButterKnife.bind(this);
        alertDialog = new AlertDialog.Builder(this);

        tvTitle.setText("物品放行");

        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        btnUpZ.setOnClickListener(this);
        ibScan.setOnClickListener(this);
        //張欽然賬號顯示補錄
        if (FoxContext.getInstance().getLoginId().equals("F2722195")||FoxContext.getInstance().getLoginId().equals("F3416918")){
            btnBlue.setOnClickListener(this);
            btnBlue.setText("補錄");
            btnBlue.setVisibility(View.VISIBLE);
        }

        initAccessTokenWithAkSk();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:
                inputAlert();
                break;
            case R.id.btn_up:
                if (etWorkId.getText().toString().equals("")){
                    ToastUtils.showShort(this,"工號或車牌號不能為空!");
                    return;
                } else {
                    Intent resultIntent = new Intent(GoodsReleaseInputActivity.this, GoodsGeneralActivity.class);
                    resultIntent.putExtra("code", etWorkId.getText().toString().toUpperCase());
                    startActivity(resultIntent);
//                    Log.e("----------", "ddd4dddddddd===" + etWorkId.getText().toString());
                   // finish();
                }
                break;
            case R.id.btn_up_z:
                if (etWorkId.getText().toString().equals("")){
                    ToastUtils.showShort(this,"工號或車牌號不能為空!");

                } else {
                    Intent resultIntent2 = new Intent(GoodsReleaseInputActivity.this, GoodsIntelligenceActivity.class);
                    resultIntent2.putExtra("code", etWorkId.getText().toString().toUpperCase());
                    startActivity(resultIntent2);
//                    Log.e("----------", "ddddddddddd===" + etWorkId.getText().toString());
                   // finish();
                }
                break;
            case R.id.btn_scan_brand://OCR識別
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(GoodsReleaseInputActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_ACCURATE_BASIC);
                break;

        }
    }

    private void inputAlert() {
        final EditText inputServer = new EditText(GoodsReleaseInputActivity.this);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(GoodsReleaseInputActivity.this);
        builder.setTitle("請输入放行單號").setView(inputServer)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        String text = inputServer.getText().toString();
                        if (text.replaceAll(" ","").equals("")) {
                            ToastUtils.showShort(GoodsReleaseInputActivity.this, "單號不能為空");
                            return;
                        }else{
                            Intent resultIntent = new Intent(GoodsReleaseInputActivity.this, GoodsGeneralActivity.class);
                            resultIntent.putExtra("id", text.toUpperCase());
                            startActivity(resultIntent);
                        }
                    }
                });
        builder.show();
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
                //alertText("licence方式获取token失败", error.getMessage());
                //Toast.makeText(getApplicationContext(), "licence方式获取token失败", Toast.LENGTH_LONG).show();
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
                //Toast.makeText(getApplicationContext(), "AK，SK方式获取token失败", Toast.LENGTH_LONG).show();
                //alertText("AK，SK方式获取token失败", error.getMessage());
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
        Log.e("-----------","requestCode, resultCode, data==="+requestCode+"-"+resultCode);
        if (resultCode!=0){
        RecognizeService.recAccurateBasic(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                new RecognizeService.ServiceListener() {
                    @Override
                    public void onResult(String result) {

                            Gson gson = new Gson();
                            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                            JsonArray array = jsonObject.get("words_result").getAsJsonArray();
                            messageList = new ArrayList<OCRMessage>();
                            if (array.size() == 0) {
                                ToastUtils.showLong(GoodsReleaseInputActivity.this, "拍照無效,請手動輸入!");
                            } else {
                                for (JsonElement type : array) {
                                    OCRMessage humi = gson.fromJson(type, OCRMessage.class);
                                    messageList.add(humi);
                                }

                                infoPopText(messageList.get(messageList.size() - 1).getWords());
                            }

                        }
                    });

    }}

    private void infoPopText(final String result) {
//        alertText("", result);
        etWorkId.setText(result);
        Log.e("------","ocr==="+result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        OCR.getInstance(this).release();
    }
}
