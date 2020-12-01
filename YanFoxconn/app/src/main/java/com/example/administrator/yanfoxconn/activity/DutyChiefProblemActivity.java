package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.EmpListAdapter;
import com.example.administrator.yanfoxconn.bean.EmpFile;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import taobe.tec.jcc.JChineseConvertor;

public class DutyChiefProblemActivity extends BaseActivity implements View.OnClickListener  {
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤

    private String[] teamData = {"請選擇","一大隊","二大隊","三大隊","機動巡邏隊","消防隊","綜合保障課"};
    private String url;//地址
    private String result;//网络获取结果
    private String duty_date;//日期
    private String name;//科長
    private String code;//工號
    private String initStartDateTime; // 初始化开始时间
    private SimpleDateFormat formatter;
    private Date selectTime = null;//所選時間
    private Date curDate = null;//當前時間
    private String teamSp = "";//稽核課隊
    final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡
    private List<String> teamList;
    private EmpListAdapter mAdapter;
    private List<EmpFile> empFileList;

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_up)
    Button btnUp;//提交

    @BindView(R.id.et_position)
    EditText etPosition;//稽核門崗
    @BindView(R.id.tv_time)
    TextView tvTime;//日期時間
    @BindView(R.id.lv_gate)
    MyListView lvGate;//稽核門崗列表
    @BindView(R.id.sp_team)
    Spinner spTeam;//稽核課隊
    @BindView(R.id.ll_list_gate)
    LinearLayout llLIstGate;//稽核課隊門崗列表
    @BindView(R.id.et_condition)
    EditText etCondition;//巡崗狀況
    @BindView(R.id.et_person)
    EditText etPerson;//責任人
    @BindView(R.id.et_captain)
    EditText etCaptain;//責任隊長

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duty_chief_problem);
        ButterKnife.bind(this);

        tvTitle.setText("稽核問題點錄入");
        btnBack.setOnClickListener(this);
        duty_date=getIntent().getStringExtra("duty_date");
        name=getIntent().getStringExtra("name");
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        code=FoxContext.getInstance().getLoginId();
        btnUp.setOnClickListener(this);
        tvTime.setOnClickListener(this);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        initStartDateTime = formatterUse.format(curDate);
        tvTime.setText(formatter.format(curDate));
        getMessage();

        //稽核課隊下拉列表選擇
        spTeam.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, teamData));
        spTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                teamSp = teamData[position];
                paramMap.put("team",teamSp);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        etPosition.setTag("0");//用於判斷是否選擇門崗0;為未選擇,其他為選擇
        //搜索关键字
        etPosition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                llLIstGate.setVisibility(View.VISIBLE);
                String a = etPosition.getText().toString();
                //调用适配器里面的搜索方法
                mAdapter.SearchCity(a);
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
                check();
                break;
            case R.id.tv_time:
                DateTimePickDialogUtil dateTimePicKDialog2 = new DateTimePickDialogUtil(DutyChiefProblemActivity.this, initStartDateTime);
                dateTimePicKDialog2.dateTimePicKDialog(tvTime, "", "");
                break;
        }
    }
    private void getMessage(){
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
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();
                        empFileList = new ArrayList<EmpFile>();
                        for (JsonElement type1 : array1) {
                            EmpFile humi1 = gson.fromJson(type1, EmpFile.class);
                            empFileList.add(humi1);
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
                }
            } }.start();
    }
    private void check(){
        if(etPosition.getText().toString().equals("")){
            ToastUtils.showLong(DutyChiefProblemActivity.this,"崗位位置不可為空");
            return;
        }
        if(etCondition.getText().toString().equals("")){
            ToastUtils.showLong(DutyChiefProblemActivity.this,"崗位狀況不可為空");
            return;
        }
        if(etPerson.getText().toString().equals("")){
            ToastUtils.showLong(DutyChiefProblemActivity.this,"責任人不可為空");
            return;
        }
        if(teamSp.equals("請選擇")){
            ToastUtils.showLong(DutyChiefProblemActivity.this,"請選擇責任課隊");
            return;
        }
        if(etCaptain.getText().toString().equals("")){
            ToastUtils.showLong(DutyChiefProblemActivity.this,"責任隊長不可為空");
            return;
        }

        upMsessage();
    }

    private void upMsessage() {

        final String utl = Constants.HTTP_DUTY_CHIEF_PROBLEM_UPDATE_SERVLET;
        paramMap.put("person", change(etPerson.getText().toString()));
        paramMap.put("captain", change(etCaptain.getText().toString()));
        paramMap.put("check_date", tvTime.getText().toString());
        paramMap.put("position", change(etPosition.getText().toString()));
        paramMap.put("condition", change(etCondition.getText().toString()));
        paramMap.put("duty_date", duty_date);
        paramMap.put("name", name);
        Log.e("-----paramMap---------", paramMap.toString());

        showDialog();

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                try {
                    HttpURLConnection b = HttpConnectionUtil.doPostPicture(utl, paramMap, null);
                    if (b!=null){
                        dismissDialog();
                        Log.e("---------", "==fff===" + b);
                        if (b.getResponseCode()==200){
                            Message message = new Message();
                            message.what = MESSAGE_UP;
                            message.obj = b.getResponseMessage();
                            mHandler.sendMessage(message);

                        }else{
                            Message message = new Message();
                            message.what = MESSAGE_UP;
                            message.obj = b.getResponseMessage();
                            mHandler.sendMessage(message);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();}

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(DutyChiefProblemActivity.this, msg.obj.toString());
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
//                case MESSAGE_SHOW://顯示提醒
//                    setText();
//                    tvShow.setVisibility(View.VISIBLE);
//                    tvShow.setText(msg.obj.toString());
            }
            super.handleMessage(msg);
        }
    };

    private void setText() {
        teamList = new ArrayList<>();
        for (int i = 0;i<empFileList.size();i++){

            teamList.add(change1(empFileList.get(i).getID()+","+empFileList.get(i).getAQ1()+"-"+empFileList.get(i).getAQ2()+"-"+empFileList.get(i).getAQ3()+"-"+empFileList.get(i).getAQ4()));
        }
        mAdapter = new EmpListAdapter(DutyChiefProblemActivity.this,teamList);
        lvGate.setAdapter(mAdapter);

        //稽核門崗列表,選中后1:tit賦值,2:列表隱藏
        mAdapter.OnClickSetText(new EmpListAdapter.OnClickSetText() {
            @Override
            public void OnClickxt(String tit) {
                etPosition.setText(tit.split(",")[1]);
                etPosition.setTag(tit.split(",")[0]);
                paramMap.put("mengang", tit.split(",")[0]);
                mAdapter.SearchCity("");
                llLIstGate.setVisibility(View.GONE);
            }
        });

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
            JChineseConvertor jChineseConvertor = JChineseConvertor
                    .getInstance();
            changeText = jChineseConvertor.t2s(changeText);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeText;
    }
    private void aboutAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("二維碼掃描信息")
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
