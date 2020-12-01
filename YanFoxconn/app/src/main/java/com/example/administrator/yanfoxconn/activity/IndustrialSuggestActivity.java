package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 工安巡檢添加優缺建議
 * Created by wang on 2019/8/8.
 */
public class IndustrialSuggestActivity extends BaseActivity {
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private String url;//地址
    private String result;//网络获取结果
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_up)
    Button btnUp;//提交
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.et_advantage)
    EditText etAdvan;//優點
    @BindView(R.id.et_shortcoming)
    EditText etShort;//缺點
    @BindView(R.id.et_suggest)
    EditText etSugge;//建議

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industrial_suggest);
        ButterKnife.bind(this);
        tvTitle.setText("添加優缺建議");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upMessage(etAdvan.getText().toString(),etShort.getText().toString(),etSugge.getText().toString());
            }
        });
    }

    //提交数据
    private void upMessage(String advantage, String shortcoming, String suggest){

        if((etAdvan.getText().toString().equals("")||etAdvan.getText().toString()==null)&&(etShort.getText().toString().equals("")||etShort.getText().toString()==null)&&(etSugge.getText().toString().equals("")||etSugge.getText().toString()==null)){
            ToastUtils.showLong(IndustrialSuggestActivity.this,"都為空不可提交");
            return;
        }


        try {
            String advantage1 =  URLEncoder.encode(URLEncoder.encode(advantage.toString(), "UTF-8"), "UTF-8");
            String shortcoming1 =  URLEncoder.encode(URLEncoder.encode(shortcoming.toString(), "UTF-8"), "UTF-8");
            String suggest1 =  URLEncoder.encode(URLEncoder.encode(suggest.toString(), "UTF-8"), "UTF-8");
            url = Constants.HTTP_INDUSSAFE_SUGGESTUPDATE_SERVLET + "?advantage=" + advantage1 + "&shortcoming=" + shortcoming1+ "&suggest=" + suggest1;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("-----------", "url-----" + url);
        showDialog();
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();

                if (result != null) {
                    Log.e("---------", "result==fff===" + result);

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else{
                        Message message = new Message();
                        message.what = MESSAGE_UP;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                }
            } }.start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(IndustrialSuggestActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                case MESSAGE_SET_TEXT://text賦值
//                    setText();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private void aboutAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (type==MESSAGE_TOAST){
                            finish();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void worningAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (type==MESSAGE_TOAST){
                            finish();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
