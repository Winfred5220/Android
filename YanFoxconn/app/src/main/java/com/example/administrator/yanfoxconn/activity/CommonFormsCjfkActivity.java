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
import android.widget.TableRow;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CommonFormsCjfkzzAdapter;
import com.example.administrator.yanfoxconn.adapter.EmpListAdapter;
import com.example.administrator.yanfoxconn.bean.AQ110Message;
import com.example.administrator.yanfoxconn.bean.EmpFile;
import com.example.administrator.yanfoxconn.bean.EmpMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.DateTimePickDialogUtil;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import taobe.tec.jcc.JChineseConvertor;

/**
 * 110接處警處警反饋
 * Created by song on 2018/12/6.
 */

public class CommonFormsCjfkActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_SET_PEOPLE = 5;//掃描成功賦值

    private String initStartDateTime; // 初始化开始时间
    private Date selectTime = null;//所選時間
    private Date curDate = null;//當前時間
    private String curDates;//當前時間
    private SimpleDateFormat formatter;
    private List<String> teamData = new ArrayList<>();
    private List<EmpMessage> empMessagesList;
    private List<AQ110Message> aqMessageList;
    private List<AQ110Message> zzMessageList;
    private String teamSp = "";//警情類別
    private AQ110Message gcHeads ;
    private String from;//追蹤、結案
    private String flag1,flag;//追蹤、結案
    private CommonFormsCjfkzzAdapter mzzAdapter;

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTittle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_dep)
    EditText etDep;
    @BindView(R.id.et_level)
    EditText etLevel;//資位
    @BindView(R.id.et_join_date)
    EditText etJoinDate;//入廠時間
    @BindView(R.id.sp_team)
    Spinner spTeam;//警情類別
    @BindView(R.id.tv_team)
    TextView tvTeam;//警情類別
    @BindView(R.id.et_describe)
    EditText etDescribe;//處警描述
    @BindView(R.id.et_redeem)
    EditText etRedeem;//挽回損失
    @BindView(R.id.lv_zz)
    MyListView lvZZ;//追蹤列表

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_110_cjfk);
        ButterKnife.bind(this);

        from = getIntent().getStringExtra("from");
        gcHeads = (AQ110Message) getIntent().getSerializableExtra("msg");
        if (from.equals("add")){
            tvTittle.setText("案件追蹤");
            flag1="Z";
        }else if (from.equals("end")){
            tvTittle.setText("結案");
            flag1="J";
        }
        getMessage(gcHeads.getID());//根據單號獲得信息

        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        initStartDateTime = formatterUse.format(curDate);
        curDates = formatter.format(curDate);

    }
    //獲取單號信息
    private void getMessage(String id){
        showDialog();
        final String url = Constants.HTTP_COMMON_FORMS_110CJFK_SERVLET+"?id="+id;

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
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        aqMessageList = new ArrayList<AQ110Message>();
                        for (JsonElement type : array) {
                            AQ110Message humi = gson.fromJson(type, AQ110Message.class);
                            aqMessageList.add(humi);
                        }

                        JsonArray array2 = jsonObject.get("result2").getAsJsonArray();
                        teamData.clear();
                        for (int i=0;i<array2.size();i++){
                            JsonObject temp1 = array2.get(i).getAsJsonObject();
                            String temp2 = temp1.get("NAME").getAsString();
                            teamData.add(temp2);
                        }

                        JsonArray array3 = jsonObject.get("result3").getAsJsonArray();
                        zzMessageList = new ArrayList<AQ110Message>();
                        for (JsonElement type : array3) {
                            AQ110Message humi = gson.fromJson(type, AQ110Message.class);
                            zzMessageList.add(humi);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else{
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            } }.start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

                    ToastUtils.showLong(CommonFormsCjfkActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                    break;
                case MESSAGE_SET_PEOPLE://人員信息賦值
                    setPeople();
            }
            super.handleMessage(msg);
        }
    };

    private void setText() {
        //下拉列表選擇
        spTeam.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, teamData));
        spTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                teamSp = teamData.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (aqMessageList.get(0).getCODE()!=null&&!aqMessageList.get(0).getCODE().equals("")){
            etCode.setText(aqMessageList.get(0).getCODE());
            etName.setText(aqMessageList.get(0).getNAME());
            etDep.setText(aqMessageList.get(0).getWJ_ADDRESS());
            etLevel.setText(aqMessageList.get(0).getJC_TYPE());
            etJoinDate.setText(aqMessageList.get(0).getINTO_DATE());
            spTeam.setVisibility(View.GONE);tvTeam.setText(aqMessageList.get(0).getZZ_TYPE());
            tvTeam.setVisibility(View.VISIBLE);
            etCode.setEnabled(false);
            etName.setEnabled(false);
            etDep.setEnabled(false);
            etLevel.setEnabled(false);
            etJoinDate.setEnabled(false);
            flag=flag1+"2";
        }else {
            flag=flag1+"1";
            etCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // 此处为得到焦点时的处理内容
                        etName.setEnabled(true);
                        etDep.setEnabled(true);
                        etLevel.setEnabled(true);
                        etJoinDate.setEnabled(true);
                    } else {
                        // 此处为失去焦点时的处理内容
                        getPeopleMessage(etCode.getText().toString().replaceAll(" ","").toUpperCase());
                    }
                }
            });
        }

        if (zzMessageList!=null&&zzMessageList.size()>0){
            mzzAdapter = new CommonFormsCjfkzzAdapter(CommonFormsCjfkActivity.this,zzMessageList);
            lvZZ.setAdapter(mzzAdapter);
        }

    }
    //獲取人員信息
    private void getPeopleMessage(String code){
        showDialog();
        final String url = Constants.HTTP_COMMON_FORMS_SERVLET+"?code="+code;

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
                        empMessagesList = new ArrayList<EmpMessage>();

                        for (JsonElement type : array) {
                            EmpMessage humi = gson.fromJson(type, EmpMessage.class);
                            empMessagesList.add(humi);

                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_PEOPLE;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else{
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            } }.start();
    }
    private void setPeople() {
        etCode.setText(empMessagesList.get(0).getWORKNO());
        etName.setText(empMessagesList.get(0).getCHINESENAME());
        etDep.setText(empMessagesList.get(0).getCZC03());
        etLevel.setText(empMessagesList.get(0).getEMPLOYEELEVEL()+empMessagesList.get(0).getMANAGER());
        etJoinDate.setText(empMessagesList.get(0).getJOINGROUPDATE());

        //etCode.setEnabled(false);
        etName.setEnabled(false);
        etDep.setEnabled(false);
        etLevel.setEnabled(false);
        etJoinDate.setEnabled(false);
    }
    private void check(){
        if(FoxContext.getInstance().getLoginId().equals("")){
            ToastUtils.showLong(CommonFormsCjfkActivity.this,"登錄超時,請退出重新登登錄!");
            return;
        }
        if(etCode.getText().toString().equals("")){
            ToastUtils.showShort(CommonFormsCjfkActivity.this,"請填寫工號!");
            return;
        }
        if(etName.getText().toString().equals("")){
            ToastUtils.showShort(CommonFormsCjfkActivity.this,"請填寫姓名!");
            return;
        }
        if(etDescribe.getText().toString().equals("")){
            ToastUtils.showShort(CommonFormsCjfkActivity.this,"請填寫處警描述!");
            return;
        }

        if(flag1.equals("J")&&etRedeem.getText().toString().equals("")){
            ToastUtils.showShort(CommonFormsCjfkActivity.this,"請填寫挽回損失!");
            return;
        }
        upMsessage();
    }
    //提交信息
    private void upMsessage(){
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        final String utl = Constants.HTTP_COMMON_FORMS_110CJFK_UP_SERVLET;
        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡

        paramMap.put("flag", flag);
        paramMap.put("id", gcHeads.getID());
        paramMap.put("code", etCode.getText().toString());
        paramMap.put("name1", etName.getText().toString());
        paramMap.put("wj_address", etDep.getText().toString());
        paramMap.put("jc_type", etLevel.getText().toString());
        paramMap.put("into_date", etJoinDate.getText().toString());
        paramMap.put("zz_type",teamSp);
        paramMap.put("b_desc", etDescribe.getText().toString());
        paramMap.put("b_redeem", etRedeem.getText().toString());
        paramMap.put("b_login_code", FoxContext.getInstance().getLoginId());
        paramMap.put("b_login_name", FoxContext.getInstance().getName());

        showDialog();
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                try {
                    String result = HttpConnectionUtil.doPostPictureLog(utl, paramMap, null);
                    dismissDialog();
                    Log.e("---------", "==result===" + result);
                    String tip="";
                    if (result.equals("success")) {
                        tip="提交成功！";
                        Message message = new Message();
                        message.what = MESSAGE_UP;
                        message.obj = tip;
                        mHandler.sendMessage(message);
                    } else{
                        tip="提交失敗！";
                        Message message = new Message();
                        message.what = MESSAGE_UP;
                        message.obj = tip;
                        mHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();}




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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_title_right://提交
                check();
                break;

        }
    }
}
