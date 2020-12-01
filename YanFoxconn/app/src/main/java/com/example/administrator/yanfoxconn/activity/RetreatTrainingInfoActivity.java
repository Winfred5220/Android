package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.RetreatMsg;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.TimeDateUtils;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * 人資退訓放行
 * Created by wang on 2020/04/17.
 */
public class RetreatTrainingInfoActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤

    private String url;//地址
    private String result;//网络获取结果
    private String[] qrResult;//二維碼內含結果
    private String getQrMessage;//二維碼內容
    private List<RetreatMsg> retreatMsg;//退訓信息
    private SimpleDateFormat formatters = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_code)
    TextView tvCode;//工號
    @BindView(R.id.tv_name)
    TextView tvName;//姓名
    @BindView(R.id.tv_sf_code)
    TextView tvSfCode;//身份證
    @BindView(R.id.tv_report_date)
    TextView tvReportDate;//報到日期
    @BindView(R.id.tv_rl_from)
    TextView tvRlFrom;//人力來源
    @BindView(R.id.tv_yg_type)
    TextView tvYgType;//人力性質
    @BindView(R.id.tv_tx_reason)
    TextView tvTxReason;//退訓原因
    @BindView(R.id.tv_tx_huanjie)
    TextView tvTxHuanjie;//退訓環節
    @BindView(R.id.tv_tx_type)
    TextView tvTxType;//退訓類別
    @BindView(R.id.tv_tx_date)
    TextView tvTxDate;//退訓日期
    @BindView(R.id.tv_stay_date)
    TextView tvStayDate;//在廠天數
    @BindView(R.id.tv_state)
    TextView tvState;//離職狀態
    @BindView(R.id.btn_release)
    Button btnRelease;//放行按鈕

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retreat_info);
        ButterKnife.bind(this);

        tvTitle.setText("退訓申請單");
        btnBack.setOnClickListener(this);
        btnRelease.setOnClickListener(this);
        getQrMessage = getIntent().getStringExtra("result");
        qrResult = getQrMessage.split(",");


        Date curDates = new Date(System.currentTimeMillis());// 获取当前时间
        String strs = formatters.format(curDates);
        int hoursOver = TimeDateUtils.hoursDeviation(qrResult[3],strs);//獲取時間相差多少小時
        if (hoursOver>12){//判斷單子有效期
            aboutAlert("此退訓單超過12小時",MESSAGE_TOAST);
        }else{
            getMessage(qrResult[0]);//獲取詳細信息
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_release:
                upMessage(qrResult[0], FoxContext.getInstance().getLoginId());
                break;
        }
    }

    private void getMessage(String id) {
        showDialog();

        url = Constants.HTTP_RZTX_SERVLET + "?id=" + id;

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
                        retreatMsg = new ArrayList<RetreatMsg>();
                        for (JsonElement type : array) {
                            RetreatMsg humi = gson.fromJson(type, RetreatMsg.class);
                            retreatMsg.add(humi);
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

    private void upMessage(String id,String code) {
        showDialog();

        url = Constants.HTTP_RZTX_UPDATE_SERVLET + "?id=" + id +"&code=" + code;

        Log.e("-----------", "url-----" + url);
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
                        Log.e("--fff---------", "result==" + result);

                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
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
                    ToastUtils.showLong(RetreatTrainingInfoActivity.this,R.string.net_mistake);
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
        //相差天數
        int stayDays = TimeDateUtils.daysDeviation(retreatMsg.get(0).getReport_date(),retreatMsg.get(0).getTx_date());

        tvCode.setText(retreatMsg.get(0).getCode());
        tvName.setText(retreatMsg.get(0).getName());
        tvSfCode.setText(retreatMsg.get(0).getSf_code());
        tvReportDate.setText(retreatMsg.get(0).getReport_date());
        tvRlFrom.setText(retreatMsg.get(0).getRl_from());
        tvYgType.setText(retreatMsg.get(0).getYg_type());
        tvTxReason.setText(retreatMsg.get(0).getTx_reason());
        tvTxHuanjie.setText(retreatMsg.get(0).getTx_huanjie());
        tvTxType.setText(retreatMsg.get(0).getTx_type());
        tvTxDate.setText(retreatMsg.get(0).getTx_date());
        tvStayDate.setText(stayDays+"");
        if(stayDays>=0&&stayDays<7){
            tvState.setText("新进未报到");//離職狀態
        }else{
            tvState.setText("離職");
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

}
