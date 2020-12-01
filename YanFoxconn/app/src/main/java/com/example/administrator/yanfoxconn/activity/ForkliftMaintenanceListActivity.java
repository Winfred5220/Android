package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.ForkliftMaintenanceAdapter;
import com.example.administrator.yanfoxconn.bean.ForkliftMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForkliftMaintenanceListActivity extends BaseActivity {

    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_NETMISTAKE = 4;//網絡錯誤
    private List<ForkliftMessage> mForkliftMessages;
    private ForkliftMaintenanceAdapter mForkliftMaintenanceAdapter;
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.lv_maintenance)
    MyListView lvMaintenance;//維修列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forklift_maintenance_list);
        ButterKnife.bind(this);

        tvTitle.setText("維修列表");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        lvMaintenance.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ForkliftMaintenanceListActivity.this,ForkliftMaintenanceActivity.class);
                intent.putExtra("danhao",mForkliftMessages.get(i).getDanhao());
                startActivity(intent);
            }
        });
    }

    private void getMessage(){
        showDialog();
        final String url = Constants.HTTP_FORKLIFT_MAINTENANCE_SERVLET +"?danhao=";
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                Log.e("---------", "==fff===" + url);
                String result = HttpUtils.queryStringForPost(url);
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        mForkliftMessages = new ArrayList<ForkliftMessage>();
                        for (JsonElement type : array) {
                            ForkliftMessage humi = gson.fromJson(type, ForkliftMessage.class);
                            mForkliftMessages.add(humi);
                        }
                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    } else{
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                    finish();
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
                    ToastUtils.showLong(ForkliftMaintenanceListActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
                    dismissDialog();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setText() {
        if(mForkliftMessages != null){
            mForkliftMaintenanceAdapter = new ForkliftMaintenanceAdapter(ForkliftMaintenanceListActivity.this, mForkliftMessages);
            lvMaintenance.setAdapter(mForkliftMaintenanceAdapter);
        }else {
            ToastUtils.showShort(ForkliftMaintenanceListActivity.this, "沒有數據!");
        }
    }

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
    @Override
    protected void onStart() {
        super.onStart();
        Log.e("---------", "onStart");
        getMessage();
    }
}
