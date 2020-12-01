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

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DutySectionChiefActivity extends BaseActivity implements View.OnClickListener , TimeDatePickerDialog.TimePickerDialogInterface{
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private String url;//地址
    private String result;//网络获取结果
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.btn_before)
    Button btnBefore;//前一天
    @BindView(R.id.tv_date)
    TextView tvDate;//日期
    @BindView(R.id.btn_after)
    Button btnAfter;//後一天
    @BindView(R.id.tv_chief)
    TextView tvChief;//科長
    @BindView(R.id.btn_sure)
    Button btnSure;//確定

    private String nowDateTime;//获取当前时间
    private String changeDate;//改變的時間,同時用於設置tvDate
    private Calendar noChangeTime = Calendar.getInstance();
    private Calendar changeDateTime = Calendar.getInstance();
    private Date selectTime = null;//所選時間
    private Date nowTime = null;//所選時間
    private TimeDatePickerDialog timeDatePickerDialog;
    private SimpleDateFormat formatter;
    private SimpleDateFormat formattery = new SimpleDateFormat("yyyy");
    private SimpleDateFormat formatterm = new SimpleDateFormat("MM");
    private SimpleDateFormat formatterd = new SimpleDateFormat("dd");
    private int mYear, mMonth, mDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duty_section_chief);
        ButterKnife.bind(this);
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        noChangeTime = Calendar.getInstance();
        nowTime = new Date(System.currentTimeMillis());
        nowDateTime = formatter.format(noChangeTime.getTime());
        mYear = Integer.parseInt(formattery.format(noChangeTime.getTime()));
        mMonth = Integer.parseInt(formatterm.format(noChangeTime.getTime()));
        mDay = Integer.parseInt(formatterd.format(noChangeTime.getTime()));
        tvDate.setText(nowDateTime);
        tvTitle.setText("值班課長");
        tvChief.setText(FoxContext.getInstance().getName());
        btnBack.setOnClickListener(this);
        btnBefore.setOnClickListener(this);
        btnAfter.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        btnSure.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_sure:
                try {
                    selectTime = formatter.parse(tvDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (selectTime.getTime()-nowTime.getTime()>0){
                    ToastUtils.showShort(this, "請检查時間是否超過當前時間");
                    return;
                }

                upMessage(tvDate.getText().toString(),tvChief.getText().toString());
                break;
            case R.id.btn_before:
                SimpleDateFormat sdfBefore = new SimpleDateFormat("yyyy-MM-dd");
                changeDate = sdfBefore.format(getBeforeDay(changeDateTime).getTime());
                mYear = Integer.parseInt(formattery.format(changeDateTime.getTime()));
                mMonth = Integer.parseInt(formatterm.format(changeDateTime.getTime()));
                mDay = Integer.parseInt(formatterd.format(changeDateTime.getTime()));
                tvDate.setText(changeDate);
                break;
            case R.id.btn_after:
                SimpleDateFormat sdfAfter = new SimpleDateFormat("yyyy-MM-dd");
                changeDate = sdfAfter.format(getAfterDay(changeDateTime).getTime());
                mYear = Integer.parseInt(formattery.format(changeDateTime.getTime()));
                mMonth = Integer.parseInt(formatterm.format(changeDateTime.getTime()));
                mDay = Integer.parseInt(formatterd.format(changeDateTime.getTime()));
                tvDate.setText(changeDate);
                break;
            case R.id.tv_date:
                timeDatePickerDialog = new TimeDatePickerDialog(DutySectionChiefActivity.this,mYear, mMonth-1, mDay);
                timeDatePickerDialog.showDatePickerDialog();
                break;
        }
    }

    //提交数据
    private void upMessage(String duty_date, String name){
        try {
            String name1 =  URLEncoder.encode(URLEncoder.encode(name.toString(), "UTF-8"), "UTF-8");
            url = Constants.HTTP_DUTY_CHIEF_UPDATE_SERVLET + "?duty_date=" + duty_date + "&name=" + name1;
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
                    if (errCode.equals("200")||errCode.equals("300")) {
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
                    Intent intent=new Intent(DutySectionChiefActivity.this,DutyChiefMainActivity.class);
                    intent.putExtra("duty_date",tvDate.getText().toString());
                    intent.putExtra("name",tvChief.getText().toString());
                    startActivity(intent);
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(DutySectionChiefActivity.this,R.string.net_mistake);
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

    /**
     * 获取当前时间的前一天时间
     * @param cl
     * @return
     */
    private Calendar getBeforeDay(Calendar cl) {
        //使用roll方法进行向前回滚
        //cl.roll(Calendar.DATE, -1);
        //使用set方法直接进行设置
        if (cl.equals(noChangeTime)) {

            btnAfter.setClickable(false);
        } else {
            int day = cl.get(Calendar.DATE);
            cl.set(Calendar.DATE, day - 1);

            btnAfter.setClickable(true);
        }
        return cl;
    }

    /**
     * 获取当前时间的后一天时间
     *
     * @param cl
     * @return
     */
    private Calendar getAfterDay(Calendar cl) {
        //使用roll方法进行回滚到后一天的时间
        //cl.roll(Calendar.DATE, 1);
        //使用set方法直接设置时间值
        int day = cl.get(Calendar.DATE);
        cl.set(Calendar.DATE, day + 1);
//        if (cl.compareTo(noChangeTime) > 0) {
//            ToastUtils.showShort(HubSelectInputActivity.this, "未到日期不可查詢");
//            btnAfter.setClickable(false);
//
//            day = cl.get(Calendar.DATE);
//            cl.set(Calendar.DATE, day - 1);
//        } else if (cl.compareTo(noChangeTime) < 0) {
//            btnAfter.setClickable(true);
//        }
        return cl;
    }

    //时间选择器----------确定
    @Override
    public void positiveListener() {

        try {
            changeDateTime.setTime(formatter.parse(timeDatePickerDialog.getTimeDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mYear = timeDatePickerDialog.getYear();
        mDay = timeDatePickerDialog.getDay();
        mMonth = timeDatePickerDialog.getMonth();
        tvDate.setText(timeDatePickerDialog.getDate());
    }

    //时间选择器-------取消
    @Override
    public void negativeListener() {
        if (timeDatePickerDialog.getTimeDate().equals("")) {
            ToastUtils.showShort(this, "請選擇時間");
        }
    }



}
