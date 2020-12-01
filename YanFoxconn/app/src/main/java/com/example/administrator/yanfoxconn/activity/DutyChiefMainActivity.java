package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class DutyChiefMainActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private String url;//地址
    private String result;//网络获取结果
    private String flag1,flag2;
    private String duty_date;//日期
    private String name;//科長
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_up)
    Button btnUp;//提交
    @BindView(R.id.btn_see)
    Button btnSee;//查看
    @BindView(R.id.et_captain)
    EditText etCaptain;//隊長值班情況
    @BindView(R.id.et_weighty)
    EditText etWeighty;//重大異常記錄

    @BindView(R.id.tv_show1)
    TextView tvShow1;//隊長值班情況
    @BindView(R.id.tv_show2)
    TextView tvShow2;//重大異常記錄

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duty_chief_main);
        ButterKnife.bind(this);
        tvTitle.setText("異常情況記錄");
        btnBack.setOnClickListener(this);
        duty_date=getIntent().getStringExtra("duty_date");
        btnUp.setOnClickListener(this);
        name=getIntent().getStringExtra("name");
        btnSee.setOnClickListener(this);
        etCaptain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
             if(!etCaptain.getText().toString().equals("")&&etCaptain.getText().toString()!=null){
                 btnUp.setText("提 交");
                 btnUp.setBackgroundColor(getResources().getColor(R.color.color_009adb));
                 btnUp.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         upMessage(etCaptain.getText().toString(),etWeighty.getText().toString(),duty_date,name);
                     }
                 });
             }else{
                 btnUp.setText("稽核問題點錄入");
                 btnUp.setBackgroundColor(getResources().getColor(R.color.color_e84e4e));
                 btnUp.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         Intent intent=new Intent(DutyChiefMainActivity.this,DutyChiefProblemActivity.class);
                         intent.putExtra("duty_date",duty_date);
                         intent.putExtra("name",name);
                         startActivity(intent);
                     }
                 });
             }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etWeighty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!etWeighty.getText().toString().equals("")&&etCaptain.getText().toString()!=null){
                    btnUp.setText("提 交");
                    btnUp.setBackgroundColor(getResources().getColor(R.color.color_009adb));
                    btnUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            upMessage(etCaptain.getText().toString(),etWeighty.getText().toString(),duty_date,name);
                        }
                    });
                }else{
                    btnUp.setText("稽核問題點錄入");
                    btnUp.setBackgroundColor(getResources().getColor(R.color.color_e84e4e));
                    btnUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(DutyChiefMainActivity.this,DutyChiefProblemActivity.class);
                            intent.putExtra("duty_date",duty_date);
                            intent.putExtra("name",name);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_up:
                Intent intent=new Intent(DutyChiefMainActivity.this,DutyChiefProblemActivity.class);
                intent.putExtra("duty_date",duty_date);
                intent.putExtra("name",name);
                startActivity(intent);
                break;
            case R.id.btn_see:
                Intent intent1=new Intent(DutyChiefMainActivity.this,DutyChiefProblemSeeActivity.class);
                intent1.putExtra("duty_date",duty_date);
                intent1.putExtra("name",name);
                startActivity(intent1);
                break;
        }
    }
    //獲取数据
    private void getMessage(String duty_date, String name){
        try {
            String name1 =  URLEncoder.encode(URLEncoder.encode(name.toString(), "UTF-8"), "UTF-8");
            url = Constants.HTTP_DUTY_CHIEF_VIEW_SERVLET + "?duty_date=" + duty_date + "&name=" + name1;
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
                     flag1 = jsonObject.get("flag1").getAsString();
                     flag2 = jsonObject.get("flag2").getAsString();

                    Message message = new Message();
                    message.what = MESSAGE_SHOW;
                    mHandler.sendMessage(message);
                }else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                }
            } }.start();
    }
    //提交数据
    private void upMessage(String captain, String weighty,String duty_date, String name){
        try {
            String name1 =  URLEncoder.encode(URLEncoder.encode(name.toString(), "UTF-8"), "UTF-8");
            String captain1 =  URLEncoder.encode(URLEncoder.encode(captain.toString(), "UTF-8"), "UTF-8");
            String weighty1 =  URLEncoder.encode(URLEncoder.encode(weighty.toString(), "UTF-8"), "UTF-8");
            url = Constants.HTTP_DUTY_CHIEF_MAIN_UPDATE_SERVLET + "?captain=" + captain1 + "&weighty=" + weighty1+ "&duty_date=" + duty_date+ "&name=" + name1;
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
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(DutyChiefMainActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_UP);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                case MESSAGE_SET_TEXT://text賦值
//                    setText();
                    break;
                case MESSAGE_SHOW://Toast彈出
                    if(flag1.equals("N")){
                        tvShow1.setVisibility(View.GONE);

                    }else{
                        tvShow1.setText(flag1);
                        tvShow1.setVisibility(View.VISIBLE);
                        etCaptain.setText("");
                    }
                    if(flag2.equals("N")){
                        tvShow2.setVisibility(View.GONE);
                    }else{
                        tvShow2.setText(flag2);
                        tvShow2.setVisibility(View.VISIBLE);
                        etWeighty.setText("");
                    }
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
                            getMessage(duty_date,name);
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
                            getMessage(duty_date,name);
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMessage(duty_date,name);
    }


}
