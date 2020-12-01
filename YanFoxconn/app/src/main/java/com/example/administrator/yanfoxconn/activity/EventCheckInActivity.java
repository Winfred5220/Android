package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 活動簽到 界面
 * Created by song on 2018/6/26.
 */

public class EventCheckInActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_DIALOG = 0;
    private final static int SCANNIN_EVENT = 3;

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_check)
    Button btnCheck;//跳轉掃描

    private String url;//請求地址
    private String qrResult;//二維碼內含結果
    private String result;//二維碼返回結果
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_check);

        ButterKnife.bind(this);

        tvTitle.setText("員工掃描");
        btnBack.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_check:
                Intent toQrActivity = new Intent(EventCheckInActivity.this, QrCodeActivity.class);
                toQrActivity.putExtra("title","二維碼掃描");
                toQrActivity.putExtra("num","3");
                startActivityForResult(toQrActivity, SCANNIN_EVENT);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_EVENT:
                if (resultCode == 3) {

                    String name = null;
                    qrResult = data.getExtras().get("result").toString();
//                    String spStr[] = qrResult.split(",");
                    BDLocation location = FoxContext.getInstance().getLocation();
//                    String type = spStr[1].substring(0, 2);
//                    Log.e("----", type);

                    try {
                        name = URLEncoder.encode(URLEncoder.encode(FoxContext.getInstance().getName(), "UTF-8"), "UTF-8");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
//                    if (location == null || location.getLongitude() == 0.0 || location.getLatitude() == 0.0) {
//                        ToastUtils.showLong(this, "位置信息獲取失敗請稍後或移動后重試!");
//                    } else
                        if (!FoxContext.getInstance().getType().equals("X0")) {
                        Log.e("--------------",FoxContext.getInstance().getType());
                        ToastUtils.showLong(this,FoxContext.getInstance().getType()+ "event權限不符合,請確認!");
                    } else {

                        if (name.equals("") || FoxContext.getInstance().getLoginId().equals("")) {
                            ToastUtils.showLong(EventCheckInActivity.this, "登錄驗證失效,請重新登陸!!");
                        } else {
                            showDialog();
                            url = Constants.HTTP_BARCODE_SCAN_SERVLET + "?dim_id=" + qrResult + "&createor=" + name +

                                    "&creator_id=" + FoxContext.getInstance().getLoginId();
                            Log.e("---------", "==fff===" + url);
                            new Thread() {
                                @Override
                                public void run() {
                                    result = HttpUtils.queryStringForPost(url);
                                    Log.e("---------", "==fff===" + result);
                                    dismissDialog();

                                    if (result != null) {
                                        FoxContext.getInstance().setLocation(null);
                                        Message message = new Message();
                                        message.what = MESSAGE_DIALOG;
                                        mHandler.sendMessage(message);
                                    }
                                }
                            }.start();
                        }
                    }}
                    break;
                }
    }


    private void aboutAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("二維碼掃描信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DIALOG://掃描后彈出
                    aboutAlert(result.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
