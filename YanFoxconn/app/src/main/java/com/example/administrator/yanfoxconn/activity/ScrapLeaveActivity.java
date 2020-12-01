package com.example.administrator.yanfoxconn.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.ScrapMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.DateTimePickDialogUtil;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * 廢料出廠詳情頁面
 * Created by wangqian on 2019/5/31.
 */
public class ScrapLeaveActivity extends BaseActivity implements View.OnClickListener{

    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_NETMISTAKE = 4;//網絡錯誤

    private String[] gateData = {"請選擇","A區西大門","E03南大門","E05南大門","E07南大門","E09南大門","C區西大門","D區北大門","G區北大門"};
    private String gateSp ="";//稽核門崗
    private String initStartDateTime; // 初始化开始时间
    private SimpleDateFormat formatter;
    private Date curDate = null;//當前時間

    private String code;//過磅單號
    private String url;//地址
    private String result;//网络获取结果
    private List<ScrapMessage> scrapMessage;//單號信息

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_no)
    TextView tvNo;//放行單號
    @BindView(R.id.tv_name)
    TextView tvName;//物品名稱
    @BindView(R.id.tv_area)
    TextView tvArea;//區域
    @BindView(R.id.tv_receive)
    TextView tvReceive;//收購廠商
    @BindView(R.id.tv_in_date)
    TextView tvInDate;//檻車時間
    @BindView(R.id.tv_out_date)
    TextView tvOutDate;//出廠時間
    @BindView(R.id.sp_gate)
    Spinner spGate;//稽核門崗
    @BindView(R.id.et_license)
    EditText etLicense;//車牌號
    @BindView(R.id.et_null_weight)
    EditText etNullWeight;//空車重量
    @BindView(R.id.et_weight)
    EditText etWeight;//毛重
    @BindView(R.id.et_person)
    EditText etPerson;//小組負責人

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrap_leave);
        ButterKnife.bind(this);

        tvTitle.setText("廢料出廠");
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        tvInDate.setOnClickListener(this);
        tvOutDate.setOnClickListener(this);
        //稽核課隊下拉列表選擇
        spGate.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, gateData));
        spGate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gateSp = gateData[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        initStartDateTime = formatterUse.format(curDate);
        tvInDate.setText(formatter.format(curDate));
        tvOutDate.setText(formatter.format(curDate));
        code=getIntent().getStringExtra("code");
        getMessage(code);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right://提交
                upMessage();
                break;
            case R.id.tv_in_date:
                DateTimePickDialogUtil dateTimePicKDialog1 = new DateTimePickDialogUtil(ScrapLeaveActivity.this, initStartDateTime);
                dateTimePicKDialog1.dateTimePicKDialog(tvInDate, "", "");
                break;
            case R.id.tv_out_date:
                DateTimePickDialogUtil dateTimePicKDialog2 = new DateTimePickDialogUtil(ScrapLeaveActivity.this, initStartDateTime);
                dateTimePicKDialog2.dateTimePicKDialog(tvOutDate, "", "");
                break;
        }
    }

    private void getMessage(String code) {
        showDialog();

        url = Constants.HTTP_SCRAP_LEAVE_SERVLET + "?code=" + code;

        Log.e("-----------", "url-----" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        scrapMessage = new ArrayList<ScrapMessage>();
                        for (JsonElement type : array) {
                            ScrapMessage humi = gson.fromJson(type, ScrapMessage.class);
                            scrapMessage.add(humi);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                        dismissDialog();

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

    private void upMessage(){

        final String url = Constants.HTTP_SCRAP_LEAVE_UPDATE_SERVLET; //此處寫上自己的URL
        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡
        final Map<String, File> fileMap = new HashMap<String, File>(); //檔全部添加到Map裡

        if (tvInDate.getText().toString().equals("")){
            ToastUtils.showShort(this, "請選擇檻車時間");
        } else if (tvOutDate.getText().toString().equals("")){
            ToastUtils.showShort(this, "請選擇出廠時間");
        } else if (gateSp.equals("請選擇")){
            ToastUtils.showShort(this, "請選擇放行門崗");
        } else if (etLicense.getText().toString().equals("")){
            ToastUtils.showShort(this, "車牌號不能為空");
        } else if (etNullWeight.getText().toString().equals("")){
            ToastUtils.showShort(this, "空車重量不能為空");
        } else if (etWeight.getText().toString().equals("")){
            ToastUtils.showShort(this, "毛重不能為空");
        } else if (etPerson.getText().toString().equals("")){
            ToastUtils.showShort(this, "小組負責人不能為空");
        } else {

            paramMap.put("fangdan", tvNo.getText().toString());
            paramMap.put("goodName", tvName.getText().toString());
            paramMap.put("area", tvArea.getText().toString());
            paramMap.put("manufacturer", tvReceive.getText().toString());
            paramMap.put("inDate", tvInDate.getText().toString());
            paramMap.put("outDate", tvOutDate.getText().toString());
            paramMap.put("gate", gateSp);
            paramMap.put("license", etLicense.getText().toString());
            paramMap.put("null_weight",etNullWeight.getText().toString());
            paramMap.put("weight", etWeight.getText().toString());
            paramMap.put("person", etPerson.getText().toString());
            paramMap.put("person_jw", FoxContext.getInstance().getName());
            paramMap.put("code", code);
            Log.e("-----------", "paramMap-----" + paramMap.toString());

            //開啟一個新執行緒，向伺服器上傳資料
            new Thread() {
                public void run() {
                    //把网络访问的代码放在这里
                    try {
                        showDialog();
                        Log.e("---------", "==fff===" + url);
                        HttpURLConnection b = HttpConnectionUtil.doPostPicture(url, paramMap, fileMap);
                        if (b != null) {
                            dismissDialog();
                            Log.e("---------", "==fff===" + b);
                            if (b.getResponseCode() == 200) {
                                Message message = new Message();
                                message.what = MESSAGE_TOAST;
                                message.obj = b.getResponseMessage();
                                mHandler.sendMessage(message);
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);

                            } else {
                                Message message = new Message();
                                message.what = MESSAGE_TOAST;
                                message.obj = b.getResponseMessage();
                                mHandler.sendMessage(message);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
          }
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
                    ToastUtils.showLong(ScrapLeaveActivity.this,R.string.net_mistake);
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
        tvNo.setText(scrapMessage.get(0).getEHD01());
        tvName.setText(scrapMessage.get(0).getEHD03());
        tvReceive.setText(scrapMessage.get(0).getEHD04());
        tvArea.setText(scrapMessage.get(0).getEHD05());
        etNullWeight.setText(scrapMessage.get(0).getEHD08());
        etLicense.setText(scrapMessage.get(0).getEHD14());
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
