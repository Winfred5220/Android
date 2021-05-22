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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.AQ110Message;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.DateTimePickDialogUtil;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import taobe.tec.jcc.JChineseConvertor;

/**
 * 人資活動發佈
 * Created by S1007989 on 2021/5/18.
 */

public class ActReleaseActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTittle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.rb_person)
    RadioButton rbPerson;//個人
    @BindView(R.id.rb_team)
    RadioButton rbTeam;//團隊
    @BindView(R.id.et_act_name)
    EditText etActName;//活動名稱
    @BindView(R.id.et_act_rules)
    EditText etActRules;//活動規則
    @BindView(R.id.et_act_award)
    EditText etActAward;//獎品設置
    @BindView(R.id.tv_time_start)
    TextView tvStartTime;//活動開始時間
    @BindView(R.id.tv_end_sign)
    TextView tvEndSignDate;//報名截止時間
    @BindView(R.id.tr_team_num)
    TableRow trTeamNum;//隊伍上限數量TableRow顯示隱藏
    @BindView(R.id.et_num_team)
    EditText etNumTeam;//隊伍上限數量
    @BindView(R.id.et_num_person)
    EditText etNumPerson;//活動上限人數

    private String initStartDateTime; // 初始化开始时间
    private Date selectStartTime = null;//所選時間
    private Date selectSignTime = null;//所選時間
    private Date curDate = null;//當前時間
    private String curDates;//當前時間
    private SimpleDateFormat formatter;
    private List<String> areaList;
    private List<AQ110Message> keduiList;
    private ActReleaseActivity mContext = this;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_release);
        ButterKnife.bind(this);

        //getMessage();//根據工號獲得信息
        tvTittle.setText("活動發佈");
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvEndSignDate.setOnClickListener(this);
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        initStartDateTime = formatterUse.format(curDate);
        tvStartTime.setText(formatter.format(curDate));
        tvEndSignDate.setText(formatter.format(curDate));
        curDates = formatter.format(curDate);

        rbPerson.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    trTeamNum.setVisibility(View.GONE);
                }else{
                    trTeamNum.setVisibility(View.VISIBLE);
                }
            }
        });
        rbPerson.setChecked(true);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_title_right://提交
                check();
                break;
            case R.id.tv_time_start:
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        mContext, initStartDateTime);
                dateTimePicKDialog.dateTimePicKDialog(tvStartTime,"","");
                break;
            case R.id.tv_end_sign:
                DateTimePickDialogUtil dateTimePicKDialog2 = new DateTimePickDialogUtil(
                        mContext, initStartDateTime);
                dateTimePicKDialog2.dateTimePicKDialog(tvEndSignDate,"","");
                break;
        }
    }
    //獲取門崗
    private void getMessage(){
        showDialog();
        final String url = Constants.HTTP_COMMON_FORMS_SERVLET;

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        areaList = new ArrayList<String>();
                        for (int i=0;i<array.size();i++) {
                           JsonObject humi = array.get(i).getAsJsonObject();
                           String humis = humi.get("NAME").getAsString();
                           areaList.add(humis);
                        }
                        JsonObject jsonObject1 = new JsonParser().parse(result).getAsJsonObject();
                        JsonArray array1 = jsonObject1.get("data2").getAsJsonArray();
                        keduiList = new ArrayList<AQ110Message>();
                        for (JsonElement type1 : array1) {
                            AQ110Message humi1 = gson.fromJson(type1, AQ110Message.class);
                            keduiList.add(humi1);
                        }
                        Message message = new Message();
                        message.what = Constants.MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    } else{
                        Message message = new Message();
                        message.what = Constants.MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            } }.start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case Constants.MESSAGE_TOAST://Toast彈出
                    worningAlert(msg.obj.toString(),Constants.MESSAGE_TOAST);
//                   ToastUtils.showLong(mContext, msg.obj.toString());
//                    finish();
                    break;
                case Constants.MESSAGE_SET_TEXT://text賦值
                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case Constants.MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),Constants.MESSAGE_TOAST);
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
        // tvID.setText(empMessagesList.get(0).getWORKNO());
        //tvName.setText(empMessagesList.get(0).getCHINESENAME());
        //tvPro.setText(empMessagesList.get(0).getBU_CODE());
        // tvDep.setText(empMessagesList.get(0).getCZC03());
        // tvLecel.setText(empMessagesList.get(0).getEMPLOYEELEVEL()+empMessagesList.get(0).getMANAGER());
        // tvJoinDate.setText(empMessagesList.get(0).getJOINGROUPDATE());

    }
    //提交前檢查
    private void check(){
        if(FoxContext.getInstance().getLoginId().equals("")){
            ToastUtils.showLong(mContext,"登錄超時,請退出重新登錄!");
            return;
        }
        if (rbPerson.isChecked()==false&&rbTeam.isChecked()==false){
            ToastUtils.showShort(mContext,"請選擇活動類別！");
            return;
        }

        if (etActName.getText().toString().replaceAll(" ","").equals("")){
            ToastUtils.showShort(mContext,"請輸入活動名稱！");
            return;
        }
        if (etActRules.getText().toString().replaceAll(" ","").equals("")){
            ToastUtils.showShort(mContext,"請輸入活動規則！");
            return;
        }
        if (etActAward.getText().toString().replaceAll(" ","").equals("")){
            ToastUtils.showShort(mContext,"請輸入獎品設置！");
            return;
        }
        try {
            selectStartTime = formatter.parse(tvStartTime.getText().toString());
            selectSignTime = formatter.parse(tvEndSignDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (curDate.getTime()-selectStartTime.getTime()>0){
            ToastUtils.showShort(mContext,"請检查活動開始時間！");
            return;
        }
        if (curDate.getTime()-selectSignTime.getTime()>0){
            ToastUtils.showShort(mContext,"請检查報名截止時間！");
            return;
        }
        if (rbTeam.isChecked()) {
            if (etNumTeam.getText().toString().replaceAll(" ", "").equals("")) {
                ToastUtils.showShort(mContext, "請輸入隊伍上限數量！");
                return;
            }
        }
        if (etNumPerson.getText().toString().replaceAll(" ","").equals("")){
            ToastUtils.showShort(mContext,"請輸入活動上限人數！");
            return;
        }

        upMsessage();
    }


    //提交信息
    private void upMsessage() {

        final String url = Constants.HTTP_ACTIVITY_JSON_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        HashMap<Integer, ArrayList<String>> imagePathsMap; //存放圖片地址的map
        HashMap<Integer, String> etMap;//存放editText值的map
        String act_type = "";//活動類別
        if (rbPerson.isChecked()){
            act_type = "個人賽";
        }else{
            act_type = "團體賽";
        }
        //JsonArray array = new JsonArray();
        object.addProperty("flag","actRelease");
        object.addProperty("act_type",act_type);
        object.addProperty("act_name",etActName.getText().toString());
        object.addProperty("act_rules",etActRules.getText().toString());
        object.addProperty("act_award",etActAward.getText().toString());
        object.addProperty("act_time_start",tvStartTime.getText().toString());
        object.addProperty("act_end_sign",tvEndSignDate.getText().toString());
        object.addProperty("act_num_team",etNumTeam.getText().toString());
        object.addProperty("act_num_person",etNumPerson.getText().toString());
        object.addProperty("create_code", FoxContext.getInstance().getLoginId());
        object.addProperty("create_name", FoxContext.getInstance().getName());
        //object.add("info", array);

        Log.e("----object----",  object.toString());

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
                    Log.e("-----url----",  url);
                    String result = HttpConnectionUtil.doPostJsonObject(url, object);
                    if (result != null) {
                        dismissDialog();
                        Log.e("----result-----",  result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String errCode = jsonObject.get("errCode").getAsString();
                        if (errCode.equals("200")) {
                            Message message = new Message();
                            message.what = Constants.MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);

                        } else{
                            Message message = new Message();
                            message.what = Constants.MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                        }
                    }else{
                        Message message = new Message();
                        message.what = Constants.MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    FileUtil.deletePhotos(mContext);
                }
            }
        }.start();
    }

    //简体转成繁体
    public String change(String changeText) {
        try {
            JChineseConvertor jChineseConvertor = JChineseConvertor.getInstance();
            changeText = jChineseConvertor.s2t(changeText);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeText;
    }

    //繁体转成简体
    public String change1(String changeText) {
        try {
            JChineseConvertor jChineseConvertor = JChineseConvertor.getInstance();
            changeText = jChineseConvertor.t2s(changeText);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeText;
    }

    private void aboutAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("確認信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (type==Constants.MESSAGE_TOAST){
                            finish();
                        }else if(type==Constants.MESSAGE_UP){
                            check();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
//                        if (type==MESSAGE_TOAST){
//                            finish();
//                        }else if(type==MESSAGE_UP){
//                            check();
//                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void worningAlert(String msg, final int t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (t==Constants.MESSAGE_TOAST) {
                            finish();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



}
