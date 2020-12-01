package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.ForkliftMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForkliftMessageToCheckActivity extends BaseActivity {

    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private String result;//車牌號
    private List<ForkliftMessage> mForkliftMessage;//基本信息
    String[] rel = new String[2];

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_area)
    TextView tvArea;//區域
    @BindView(R.id.tv_bumen)
    TextView tvBumen;//部門
    @BindView(R.id.tv_chepai)
    TextView tvChepai;//車牌號
    @BindView(R.id.tv_bianhao)
    TextView tvBianhao;//車架號
    @BindView(R.id.tv_xinghao)
    TextView tvXinghao;//車型
    @BindView(R.id.tv_dunwei)
    TextView tvDunwei;//噸位
    @BindView(R.id.tv_sc_date)
    TextView tvScDate;//車輛生產日期
    @BindView(R.id.tv_voltage)
    TextView tvVoltage;//電壓
    @BindView(R.id.tv_capacity)
    TextView tvCapacity;//電瓶容量
    @BindView(R.id.tv_hy_enddate)
    TextView tvHyEnddate;//合約期限
    @BindView(R.id.tv_youxiao)
    TextView tvYouxiao;//有效期

    @BindView(R.id.btn_xunjian)
    Button btnXunjian;//巡檢

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forklift_message_check);
        ButterKnife.bind(this);
        result = getIntent().getStringExtra("result");
        rel = result.split(",");
        Log.e("------------","-----result------"+result);
        tvTitle.setText("本車基本資料");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnXunjian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!result.equals("")){
                Intent intent=new Intent(ForkliftMessageToCheckActivity.this,ForkliftCheckActivity.class);
                intent.putExtra("result",result);
                startActivity(intent);
                finish();
                }else{
                    ToastUtils.showShort(ForkliftMessageToCheckActivity.this,"請重新掃描");
                }
            }
        });
        getMessage();
    }

    private void getMessage(){

        showDialog();

        final String url = Constants.HTTP_FORKLIFT_MESSAGE_SERVLET +"?bianhao=" + rel[0] + "&type=" + rel[1];
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        mForkliftMessage = new ArrayList<ForkliftMessage>();

                        for (JsonElement type : array) {
                            ForkliftMessage humi = gson.fromJson(type, ForkliftMessage.class);
                            mForkliftMessage.add(humi);

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
                    ToastUtils.showLong(ForkliftMessageToCheckActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
                    dismissDialog();
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
        tvArea.setText(mForkliftMessage.get(0).getArea());
        tvBumen.setText(mForkliftMessage.get(0).getBumen());
        tvChepai.setText(mForkliftMessage.get(0).getChepai());
        tvBianhao.setText(mForkliftMessage.get(0).getBianhao());
        tvXinghao.setText(mForkliftMessage.get(0).getXinghao());
        tvDunwei.setText(mForkliftMessage.get(0).getDunwei());
        tvScDate.setText(mForkliftMessage.get(0).getSc_date().replaceAll("00:00:00.0",""));
        tvVoltage.setText(mForkliftMessage.get(0).getVoltage());
        tvCapacity.setText(mForkliftMessage.get(0).getCapacity());
        tvHyEnddate.setText(mForkliftMessage.get(0).getHy_enddate().replaceAll("00:00:00.0",""));
        tvYouxiao.setText(mForkliftMessage.get(0).getStart_date().replaceAll("00:00:00.0","")+"至 "+mForkliftMessage.get(0).getEnd_date().replaceAll("00:00:00.0",""));
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
}
