package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 共用類
 * 碼頭 手動輸入銷單號
 * 常用表單
 * 跨區 無紙化作業 手動輸入工號
 * 健康追蹤 手動輸入工號
 * Created by song on 2018/5/17.
 */

public class CarWriteIdActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_up)
    Button btnUp;//提交
    @BindView(R.id.et_write_id)
    EditText etWriteId;//銷單號 或 工號 或 車牌號

    private String from;//來自

    private AlertDialog.Builder alertDialog;
    private List<OCRMessage> messageList;
    private String check = "";//健康追蹤 廠商 或 員工

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_carid);

        ButterKnife.bind(this);

        alertDialog = new AlertDialog.Builder(this);
        from = getIntent().getStringExtra("from");

        if (from.equals("healthScan") || from.equals("healthOCR")) {
            check = getIntent().getStringExtra("check");
        }
        if (from.equals("crossScan") || from.equals("inoutScan") || from.equals("vehicleScan") || from.equals("wrongScan") || from.equals("healthScan")) {

            if (check.equals("")) {
                tvTitle.setText("手動輸入工號");
            } else if (check.equals("out")) {
                tvTitle.setText("手動輸入身份證號");
            } else {

                tvTitle.setText("手動輸入工號");
            }
        } else if (from.equals("crossOCR") || from.equals("wrongOCR") || from.equals("inoutOCR") || from.equals("vehicleOCR") || from.equals("healthOCR")) {
            tvTitle.setText("請輸入");
            // 识别成功回调，通用文字识别（高精度版）
            RecognizeService.recAccurateBasic(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            Gson gson = new Gson();
                            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                            JsonArray array = jsonObject.get("words_result").getAsJsonArray();
                            messageList = new ArrayList<OCRMessage>();
                            if (array.size() == 0) {
                                ToastUtils.showLong(CarWriteIdActivity.this, "拍照無效,請手動輸入!");
                            } else {
                                for (JsonElement type : array) {
                                    OCRMessage humi = gson.fromJson(type, OCRMessage.class);
                                    messageList.add(humi);
                                }

                                infoPopText(messageList.get(messageList.size() - 1).getWords());

                            }

                        }
                    });
        } else if (from.equals("carOCR")) {
            // 识别成功回调，车牌识别
            RecognizeService.recLicensePlate(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            if (result.equals("0")) {
                                ToastUtils.showLong(CarWriteIdActivity.this, "識別失敗,請重試!");
                            } else {
                                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                                JsonObject wordsResult = jsonObject.get("words_result").getAsJsonObject();
                                String cardId = wordsResult.get("number").getAsString();
                                infoPopText(cardId);
                            }
                        }
                    });
        } else if (from.equals("IG")){

            tvTitle.setText("手動輸入工號");
        }else if (from.equals("leave")){

            tvTitle.setText("手動輸入工號");
        }else{
            tvTitle.setText("手動輸入銷單號");
        }


//        tvTitle.setText("手動輸入");
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
                if (etWriteId.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "輸入結果不能為空!");
                } else if (from.equals("carList")) {
                    Intent resultIntent = new Intent(CarWriteIdActivity.this, CarScanActivity.class);
                    resultIntent.putExtra("result", etWriteId.getText().toString());
                    startActivity(resultIntent);
                    finish();
                } else if (from.equals("crossScan") || from.equals("crossOCR")) {
                    Intent resultIntent = new Intent(CarWriteIdActivity.this, CrossScanActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("result", etWriteId.getText().toString());
                    resultIntent.putExtras(bundle);
                    startActivity(resultIntent);
                    finish();
                } else if (from.equals("carScan") || from.equals("carOCR")) {
                    Intent resultIntent = new Intent(CarWriteIdActivity.this, CrossCarActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("result", etWriteId.getText().toString());
                    resultIntent.putExtras(bundle);
                    startActivity(resultIntent);
                    finish();
                } else if (from.equals("wrongScan") || from.equals("wrongOCR")) {

                    Intent resultIntent = new Intent(CarWriteIdActivity.this, EmpWrongActivity.class);
                    resultIntent.putExtra("result", etWriteId.getText().toString());
                    startActivity(resultIntent);
                    finish();
                } else if (from.equals("inoutScan") || from.equals("inoutOCR")) {

                    Intent resultIntent = new Intent(CarWriteIdActivity.this, EmpTurnoverActivity.class);
                    resultIntent.putExtra("result", etWriteId.getText().toString());
                    startActivity(resultIntent);
                    finish();
                } else if (from.equals("vehicleScan") || from.equals("vehicleOCR")) {

                    Intent resultIntent = new Intent(CarWriteIdActivity.this, TwoWheelVehicleActivity.class);
                    resultIntent.putExtra("result", etWriteId.getText().toString());
                    startActivity(resultIntent);
                    finish();
                } else if (from.equals("healthScan") || from.equals("healthOCR")) {
                    if (check.equals("")) {

                        ToastUtils.showShort(this, "請返回選擇廠商或員工");
                    } else {
                        Intent resultIntent = new Intent(CarWriteIdActivity.this, GCCheckIDActivity.class);
                        resultIntent.putExtra("result", etWriteId.getText().toString());
                        resultIntent.putExtra("check", check);
                        startActivity(resultIntent);
                        finish();
                    }
                }else if (from.equals("IG")){
                    Intent resultIntent = new Intent(CarWriteIdActivity.this, IGMainActivity.class);
//                    resultIntent.putExtra("id", etWriteId.getText().toString());
                    resultIntent.putExtra("id", etWriteId.getText().toString());
                    resultIntent.putExtra("from", "add");
                    startActivity(resultIntent);
                    finish();
                }else if (from.equals("leave")){
                    Intent resultIntent = new Intent(CarWriteIdActivity.this, IGMainActivity.class);
//                    resultIntent.putExtra("id", etWriteId.getText().toString());
                    resultIntent.putExtra("id", "S1008368");
                    resultIntent.putExtra("from", "leave");
                    startActivity(resultIntent);
                    finish();
                }
                break;
        }
    }


//    private void alertText(final String title, final String message) {
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                alertDialog.setTitle(title)
//                        .setMessage(message)
//                        .setPositiveButton("确定", null)
//                        .show();
//            }
//        });
//    }

    private void infoPopText(final String result) {
//        alertText("", result);
        etWriteId.setText(result);
        Log.e("-------------", "ocr===" + result);
    }
}
