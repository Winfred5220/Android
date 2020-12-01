package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.MobileMessage;
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

public class MobilecontrolMessageActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private String id;//工號
    private String url;//地址
    private String result;//网络获取结果
    private List<MobileMessage> mobileMessage;//物品信息

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_document_no)
    TextView tvDocumentNo;//單號
    @BindView(R.id.tv_area)
    TextView tvArea;//使用廠區及區域
    @BindView(R.id.tv_userno)
    TextView tvUserno;//工號
    @BindView(R.id.tv_username)
    TextView tvUsername;//姓名
    @BindView(R.id.tv_userdepm)
    TextView tvUserdemp;//部門
    @BindView(R.id.tv_userps)
    TextView tvUserps;//資位
    @BindView(R.id.tv_userlc)
    TextView tvUserlc;//職位
    @BindView(R.id.tv_equipment)
    TextView tvEquipment;//設備名稱
    @BindView(R.id.tv_ardit_date)
    TextView tvArditDate;//生效日期

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobilecontrol_message);
        ButterKnife.bind(this);

        id = getIntent().getStringExtra("id");

        tvTitle.setText("移動上網設備管控");
        btnBack.setOnClickListener(this);

        getMessage(id);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
        }
    }

    private void getMessage(String id) {
        showDialog();

        url = Constants.HTTP_MOBILE_CONTROL_SERVLET + "?id=" + id;

        Log.e("-----------", "url-----" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();

                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        mobileMessage = new ArrayList<MobileMessage>();
                        for (JsonElement type : array) {
                            MobileMessage humi = gson.fromJson(type, MobileMessage.class);
                            mobileMessage.add(humi);
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
                    ToastUtils.showLong(MobilecontrolMessageActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                    break;
//                case MESSAGE_SHOW://顯示提醒
//                    setText();
//                    tvShow.setVisibility(View.VISIBLE);
//                    tvShow.setText(msg.obj.toString());
            }
            super.handleMessage(msg);
        }
    };

    private void setText() {
        tvDocumentNo.setText(mobileMessage.get(0).getDOCUMENT_NO());
        tvArea.setText(mobileMessage.get(0).getAREA());
        tvUserno.setText(mobileMessage.get(0).getUSERNO());
        tvUsername.setText(mobileMessage.get(0).getUSERNAME());
        tvUserdemp.setText(mobileMessage.get(0).getUSERDEPM());
        tvUserps.setText(mobileMessage.get(0).getUSERPS());
        tvUserlc.setText(mobileMessage.get(0).getUSERLC());
        tvEquipment.setText(mobileMessage.get(0).getEQUIPMENT());
        tvArditDate.setText(mobileMessage.get(0).getARDIT_DATE());
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
