package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiCheckMsg;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiMsg;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.TimeDateUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZhiyinshuiMaintainActivity extends BaseActivity implements View.OnClickListener, TimeDatePickerDialog.TimePickerDialogInterface {
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息
    private final int MESSAGE_JUMP = 4;//跳轉維護界面
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_CHECK = 6;

    private Context mcontext;
    private String getQrMessage;//二維碼內容
    private List<ZhiyinshuiMsg> mMsgList;//基本信息
    private String nowDateTime;//获取当前时间
    private String changeDate;//改變的時間,同時用於設置tvDate
    private Calendar noChangeTime = Calendar.getInstance();
    private Calendar changeDateTime = Calendar.getInstance();
    private Date selectTime = null;//所選時間
    private Date nowTime = null;//所選時間
    private TimeDatePickerDialog timeDatePickerDialog1;
    private TimeDatePickerDialog timeDatePickerDialog2;
    private TimeDatePickerDialog timeDatePickerDialog3;
    private SimpleDateFormat formatter;
    private SimpleDateFormat formattery = new SimpleDateFormat("yyyy");
    private SimpleDateFormat formatterm = new SimpleDateFormat("MM");
    private SimpleDateFormat formatterd = new SimpleDateFormat("dd");
    private int mYear, mMonth, mDay,flag;
    private String dim_id;//二維碼內容
    private String type;//類別

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_name)
    TextView tvName;//名稱
    @BindView(R.id.tv_producter)
    TextView tvProducter;//品牌
    @BindView(R.id.tv_position)
    TextView tvPosition;//位置
    @BindView(R.id.tv_dep)
    TextView tvDep;//部門

    @BindView(R.id.tr_filter1)
    TableRow trFilter1;//濾芯1
    @BindView(R.id.tr_filter2)
    TableRow trFilter2;//濾芯2
    @BindView(R.id.tr_filter3)
    TableRow trFilter3;//濾芯3
    @BindView(R.id.tv_filter1)
    TextView tvFilter1;//濾芯1
    @BindView(R.id.tv_filter2)
    TextView tvFilter2;//濾芯2
    @BindView(R.id.tv_filter3)
    TextView tvFilter3;//濾芯3
    @BindView(R.id.tv_filter1_date)
    TextView tvFilter1Date;//更換天數1
    @BindView(R.id.tv_filter2_date)
    TextView tvFilter2Date;//更換天數2
    @BindView(R.id.tv_filter3_date)
    TextView tvFilter3Date;//更換天數3
    @BindView(R.id.tv_filter1_t)
    TextView tvFilter1T;//更換天數1
    @BindView(R.id.tv_filter2_t)
    TextView tvFilter2T;//更換天數2
    @BindView(R.id.tv_filter3_t)
    TextView tvFilter3T;//更換天數3
    @BindView(R.id.tv_update1_date)
    TextView tvUpdate1Date;//更換時間1
    @BindView(R.id.tv_update2_date)
    TextView tvUpdate2Date;//更換時間2
    @BindView(R.id.tv_update3_date)
    TextView tvUpdate3Date;//更換時間3
    @BindView(R.id.tr_filter1_date)
    TableRow trFilter1Date;//濾芯1更換時間1
    @BindView(R.id.tr_filter2_date)
    TableRow trFilter2Date;//濾芯2更換時間2
    @BindView(R.id.tr_filter3_date)
    TableRow trFilter3Date;//濾芯3更換時間3

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhiyinshui_maintain);
        ButterKnife.bind(this);
        tvTitle.setText("維保");
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        tvUpdate1Date.setOnClickListener(this);
        tvUpdate2Date.setOnClickListener(this);
        tvUpdate3Date.setOnClickListener(this);

        formatter = new SimpleDateFormat("yyyy-MM-dd");
        noChangeTime = Calendar.getInstance();
        nowTime = new Date(System.currentTimeMillis());
        nowDateTime = formatter.format(noChangeTime.getTime());
        mYear = Integer.parseInt(formattery.format(noChangeTime.getTime()));
        mMonth = Integer.parseInt(formatterm.format(noChangeTime.getTime()));
        mDay = Integer.parseInt(formatterd.format(noChangeTime.getTime()));

        getQrMessage = getIntent().getStringExtra("result");
        type = getIntent().getStringExtra("type");

        if(getQrMessage==null||getQrMessage.equals("")){
            dim_id = getIntent().getStringExtra("dim_id");
        }else {
            if(!getQrMessage.startsWith(type)){
                ToastUtils.showShort(this, "二維碼掃描有誤！");
                finish();
            }
            dim_id = getQrMessage;
        }

        getMessage();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_release:
                // upMessage(qrResult[0], FoxContext.getInstance().getLoginId());
                break;
            case R.id.btn_title_right:
                aboutAlert(getResources().getString(R.string.update_confirm),MESSAGE_UP);
                break;
            case R.id.tv_update1_date:
                flag=1;
                timeDatePickerDialog1 = new TimeDatePickerDialog(ZhiyinshuiMaintainActivity.this,mYear, mMonth-1, mDay);
                timeDatePickerDialog1.showDatePickerDialog();
                break;
            case R.id.tv_update2_date:
                flag=2;
                timeDatePickerDialog2 = new TimeDatePickerDialog(ZhiyinshuiMaintainActivity.this,mYear, mMonth-1, mDay);
                timeDatePickerDialog2.showDatePickerDialog();
                break;
            case R.id.tv_update3_date:
                flag=3;
                timeDatePickerDialog3 = new TimeDatePickerDialog(ZhiyinshuiMaintainActivity.this,mYear, mMonth-1, mDay);
                timeDatePickerDialog3.showDatePickerDialog();
                break;
        }
    }
    private void getMessage(){
        showDialog();
        final String url = Constants.HTTP_WATER_MAINTAIN_SERVLET +"?type=DQ&flag=S"+"&dim_id="+dim_id;

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);
                Log.e("---------", "==fff===" + url);
                dismissDialog();
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                            JsonArray array = jsonObject.get("result").getAsJsonArray();
                            mMsgList = new ArrayList<ZhiyinshuiMsg>();

                            for (JsonElement type : array) {
                                ZhiyinshuiMsg humi = gson.fromJson(type, ZhiyinshuiMsg.class);
                                mMsgList.add(humi);
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
                    ToastUtils.showLong(ZhiyinshuiMaintainActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
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
        tvName.setText(mMsgList.get(0).getDIM_NAME());
        tvProducter.setText(mMsgList.get(0).getDIM_PRODUCER());
        tvPosition.setText(mMsgList.get(0).getDIM_POSITION());
        tvDep.setText(mMsgList.get(0).getSc_dep());
        if (mMsgList.get(0).getDim_filter1()!=null&&!mMsgList.get(0).getDim_filter1().equals("")){
            trFilter1.setVisibility(View.VISIBLE);
            trFilter1Date.setVisibility(View.VISIBLE);
            tvFilter1.setText(mMsgList.get(0).getDim_filter1());
            int num = Integer.parseInt(mMsgList.get(0).getDim_filter1_num()) - TimeDateUtils.daysDeviation(mMsgList.get(0).getDIM_filter1_date1().substring(0,16),nowDateTime+" 00:00");
            if(num<=0){
                tvFilter1T.setText("已超期");
                tvFilter1Date.setText(Math.abs(num)+" ");
                tvFilter1Date.setTextColor(getResources().getColor(R.color.color_e84e4e));
                tvFilter1T.setTextColor(getResources().getColor(R.color.color_e84e4e));

            }else if (num<=7&&num>0){
                tvFilter1Date.setText(Math.abs(num)+" ");
                tvFilter1Date.setTextColor(getResources().getColor(R.color.color_e84e4e));
            }else {
                tvFilter1Date.setText(Math.abs(num)+" ");
            }

            Log.e("setText: ", "setText: "+TimeDateUtils.daysDeviation(mMsgList.get(0).getDIM_filter1_date1(),nowDateTime) );

        }
        if (mMsgList.get(0).getDim_filter2()!=null&&!mMsgList.get(0).getDim_filter2().equals("")){
            trFilter2.setVisibility(View.VISIBLE);
            trFilter2Date.setVisibility(View.VISIBLE);
            tvFilter2.setText(mMsgList.get(0).getDim_filter2());
            int num = Integer.parseInt(mMsgList.get(0).getDim_filter2_num()) - TimeDateUtils.daysDeviation(mMsgList.get(0).getDIM_filter1_date2().substring(0,16),nowDateTime+" 00:00");
            if(num<=0){
                tvFilter2T.setText("已超期");
                tvFilter2Date.setText(Math.abs(num)+" ");
                tvFilter2Date.setTextColor(getResources().getColor(R.color.color_e84e4e));
                tvFilter2T.setTextColor(getResources().getColor(R.color.color_e84e4e));

            }else if (num<=7&&num>0){
                tvFilter2Date.setText(Math.abs(num)+" ");
                tvFilter2Date.setTextColor(getResources().getColor(R.color.color_e84e4e));
            }else {
                tvFilter2Date.setText(Math.abs(num)+" ");
            }
        }
        if (mMsgList.get(0).getDim_filter3()!=null&&!mMsgList.get(0).getDim_filter3().equals("")){
            trFilter3.setVisibility(View.VISIBLE);
            trFilter3Date.setVisibility(View.VISIBLE);
            tvFilter3.setText(mMsgList.get(0).getDim_filter3());
            int num = Integer.parseInt(mMsgList.get(0).getDim_filter3_num()) - TimeDateUtils.daysDeviation(mMsgList.get(0).getDIM_filter1_date3().substring(0,16),nowDateTime+" 00:00");
            if(num<=0){
                tvFilter3T.setText("已超期");
                tvFilter3Date.setText(Math.abs(num)+" ");
                tvFilter3Date.setTextColor(getResources().getColor(R.color.color_e84e4e));
                tvFilter3T.setTextColor(getResources().getColor(R.color.color_e84e4e));

            }else if (num<=7&&num>0){
                tvFilter3Date.setText(Math.abs(num)+" ");
                tvFilter3Date.setTextColor(getResources().getColor(R.color.color_e84e4e));
            }else {
                tvFilter3Date.setText(Math.abs(num)+" ");
            }
        }

    }

    //提交前檢查
    private void check() {
        String dim_filter1_date1 = tvUpdate1Date.getText().toString();
        String dim_filter1_date2 = tvUpdate2Date.getText().toString();
        String dim_filter1_date3 = tvUpdate3Date.getText().toString();

        if ((dim_filter1_date1==null||dim_filter1_date1.equals(""))&&(dim_filter1_date2==null||dim_filter1_date2.equals(""))&&(dim_filter1_date3==null||dim_filter1_date3.equals(""))){
            ToastUtils.showShort(this, "至少維護一個日期");
            return;
        }

        if (dim_filter1_date1==null||dim_filter1_date1.equals("")){
            if(mMsgList.get(0).getDIM_filter1_date1()!=null&&!mMsgList.get(0).getDIM_filter1_date1().equals("")){
                dim_filter1_date1 = mMsgList.get(0).getDIM_filter1_date1().substring(0,10);
            }
        }
        if (dim_filter1_date2==null||dim_filter1_date2.equals("")){
            if(mMsgList.get(0).getDIM_filter1_date2()!=null&&!mMsgList.get(0).getDIM_filter1_date2().equals("")){
                dim_filter1_date2 = mMsgList.get(0).getDIM_filter1_date2().substring(0,10);
            }
        }
        if (dim_filter1_date3==null||dim_filter1_date3.equals("")){
            if(mMsgList.get(0).getDIM_filter1_date3()!=null&&!mMsgList.get(0).getDIM_filter1_date3().equals("")){
                dim_filter1_date3 = mMsgList.get(0).getDIM_filter1_date3().substring(0,10);
            }
        }



       String url = Constants.HTTP_WATER_MAINTAIN_OK_SERVLET+"?dim_id="+dim_id+"&dim_filter1_date1="+dim_filter1_date1
                +"&dim_filter1_date2="+dim_filter1_date2+"&dim_filter1_date3="+dim_filter1_date3; //此處寫上自己的URL

//        try {
//            url= URLEncoder.encode(URLEncoder.encode(url.toString(), "UTF-8"), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
            return;
        }

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
                    Log.e("---------", "==fff===" + url);
                    String result = HttpUtils.queryStringForPost(url);

                    if (result != null) {
                        dismissDialog();
                        Log.e("---------", "result==fff===" + result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String errCode = jsonObject.get("errCode").getAsString();
                        if (errCode.equals("200")) {
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
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
                        }else if(type==MESSAGE_UP){
                            check();
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
    //时间选择器----------确定
    @Override
    public void positiveListener() {
        if (flag==1){
            try {
                changeDateTime.setTime(formatter.parse(timeDatePickerDialog1.getTimeDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mYear = timeDatePickerDialog1.getYear();
            mDay = timeDatePickerDialog1.getDay();
            mMonth = timeDatePickerDialog1.getMonth();
            tvUpdate1Date.setText(timeDatePickerDialog1.getDate());
        }
        if (flag==2){
            try {
                changeDateTime.setTime(formatter.parse(timeDatePickerDialog2.getTimeDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mYear = timeDatePickerDialog2.getYear();
            mDay = timeDatePickerDialog2.getDay();
            mMonth = timeDatePickerDialog2.getMonth();
            tvUpdate2Date.setText(timeDatePickerDialog2.getDate());
        }
        if (flag==3){
            try {
                changeDateTime.setTime(formatter.parse(timeDatePickerDialog3.getTimeDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mYear = timeDatePickerDialog3.getYear();
            mDay = timeDatePickerDialog3.getDay();
            mMonth = timeDatePickerDialog3.getMonth();
            tvUpdate3Date.setText(timeDatePickerDialog3.getDate());
        }



    }

    //时间选择器-------取消
    @Override
    public void negativeListener() {
        if (flag==1){
        if (timeDatePickerDialog1.getTimeDate().equals("")) {
            ToastUtils.showShort(this, "請選擇時間");
        }}
        if (flag==2){
        if (timeDatePickerDialog2.getTimeDate().equals("")) {
            ToastUtils.showShort(this, "請選擇時間");
        }}
        if (flag==3){
        if (timeDatePickerDialog3.getTimeDate().equals("")) {
            ToastUtils.showShort(this, "請選擇時間");
        }}
    }
}