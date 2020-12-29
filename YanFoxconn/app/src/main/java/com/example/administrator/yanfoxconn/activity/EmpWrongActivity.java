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
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.EmpListAdapter;
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
 * 員工違規登記表
 * Created by song on 2018/12/6.
 */

public class EmpWrongActivity extends BaseActivity implements View.OnClickListener{

    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private String initStartDateTime; // 初始化开始时间
    private Date selectTime = null;//所選時間
    private Date curDate = null;//當前時間
    private String curDates;//當前時間
    private SimpleDateFormat formatter;
    private String[] teamData = {"請選擇","一大隊一中隊","一大隊二中隊","二大隊一中隊","二大隊二中隊","三大隊","機動巡邏隊"};

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTittle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_id)
    TextView tvID;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_pro)
    TextView tvPro;
    @BindView(R.id.tv_dep)
    TextView tvDep;
    @BindView(R.id.tv_level)
    TextView tvLecel;//資位
    @BindView(R.id.tv_join_date)
    TextView tvJoinDate;//入廠時間
    @BindView(R.id.tv_date)
    TextView tvDate;//時間
    @BindView(R.id.et_gate_post)
    EditText etGatePost;//稽核門崗
    @BindView(R.id.lv_gate)
    MyListView lvGate;//稽核門崗列表
    @BindView(R.id.tr_list_gate)
    TableRow trLIstGate;//稽核課隊門崗列表
    @BindView(R.id.sp_team)
    Spinner spTeam;//稽核課隊
    @BindView(R.id.et_wrong)
    EditText etWrong;//異常描述 其他 輸入

    private List<String> teamList;
    private EmpListAdapter mAdapter;
    private List<EmpMessage> empMessagesList;
    private List<EmpFile> empFileList;
    private String teamSp = "";//稽核課隊

    private String result;//錄入的工號
    final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_violation);
        ButterKnife.bind(this);

        result = getIntent().getStringExtra("result");

        getMessage();//根據工號獲得信息
        tvTittle.setText("員工違規登記表");
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        tvDate.setOnClickListener(this);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        initStartDateTime = formatterUse.format(curDate);
        tvDate.setText(formatter.format(curDate));
        curDates = formatter.format(curDate);
        etGatePost.setTag("0");

        //搜索关键字
        etGatePost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                trLIstGate.setVisibility(View.VISIBLE);
                String a = etGatePost.getText().toString();
                //调用适配器里面的搜索方法
                mAdapter.SearchCity(a);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }

        });
        //稽核課隊下拉列表選擇
        spTeam.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, teamData));
        spTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = teamData[position];
                teamSp = teamData[position];
                Log.e("---------", "最喜欢的水果是：" + str);
                paramMap.put("kedui",str);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //獲取門崗
    private void getMessage(){
        showDialog();
        final String url = Constants.HTTP_COMMON_FORMS_SERVLET+"?code="+result;

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
                        empMessagesList = new ArrayList<EmpMessage>();

                        for (JsonElement type : array) {
                            EmpMessage humi = gson.fromJson(type, EmpMessage.class);
                            empMessagesList.add(humi);

                        }

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

    private void upMsessage(){
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        final String utl = Constants.HTTP_COMMON_FORMS_UPDATE_SERVLET;
//        A員工違規登記      B員工進出異常
//        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡

        paramMap.put("flag", "A");
        paramMap.put("code", tvID.getText().toString());
        paramMap.put("login_code", FoxContext.getInstance().getLoginId());
        paramMap.put("login_name",FoxContext.getInstance().getName());
        paramMap.put("wj", etWrong.getText().toString());

        if(tvDate.getText().toString().equals(curDates)){
            paramMap.put("date0", "");
        }else{
            paramMap.put("date0", tvDate.getText().toString());

        }

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
                            message.what = MESSAGE_TOAST;
                            message.obj = b.getResponseMessage();
                            mHandler.sendMessage(message);
//                            deleteDir(Environment.getExternalStorageDirectory().getPath().toString() + "/FoxconnDelete/");

                            finish();
                        }else{
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = b.getResponseMessage();
                            mHandler.sendMessage(message);
                        }
                    }
                    Log.e("---","ddddelete");
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

                    ToastUtils.showLong(EmpWrongActivity.this, msg.obj.toString());
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
        tvID.setText(empMessagesList.get(0).getWORKNO());
        tvName.setText(empMessagesList.get(0).getCHINESENAME());
        tvPro.setText(empMessagesList.get(0).getBU_CODE());
        tvDep.setText(empMessagesList.get(0).getCZC03());
        tvLecel.setText(empMessagesList.get(0).getEMPLOYEELEVEL()+empMessagesList.get(0).getMANAGER());
        tvJoinDate.setText(empMessagesList.get(0).getJOINGROUPDATE());

        teamList = new ArrayList<>();
        for (int i = 0;i<empFileList.size();i++){

            teamList.add(change1(empFileList.get(i).getID()+","+empFileList.get(i).getAQ1()+"-"+empFileList.get(i).getAQ2()+"-"+empFileList.get(i).getAQ3()+"-"+empFileList.get(i).getAQ4()));

        }
        mAdapter = new EmpListAdapter(EmpWrongActivity.this,teamList);
        lvGate.setAdapter(mAdapter);

        //稽核門崗列表,選中后1:tit賦值,2:列表隱藏
        mAdapter.OnClickSetText(new EmpListAdapter.OnClickSetText() {
            @Override
            public void OnClickxt(String tit) {
                etGatePost.setText(tit.split(",")[1]);
                etGatePost.setTag(tit.split(",")[0]);
                paramMap.put("mengang", tit.split(",")[0]);
                mAdapter.SearchCity("");
                trLIstGate.setVisibility(View.GONE);
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

    private void check(){
        if(FoxContext.getInstance().getLoginId().equals("")){
            ToastUtils.showLong(EmpWrongActivity.this,"登錄超時,請退出重新登登錄!");
            return;
        }
        if (etGatePost.getTag().equals("0")){
            ToastUtils.showShort(EmpWrongActivity.this,"請選擇稽核門崗!");
            return;
        }
        try {
            selectTime = formatter.parse(tvDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (selectTime.getTime()-curDate.getTime()>0){
            ToastUtils.showShort(EmpWrongActivity.this,"請检查稽核時間是否在當前時間之前");
            return;
        }
        if (teamSp.equals("請選擇")){
            ToastUtils.showShort(EmpWrongActivity.this,"請選擇稽核課隊!");
            return;
        }
        if(etWrong.getText().toString().equals("")){
            ToastUtils.showShort(EmpWrongActivity.this,"請填寫異常描述!");
            return;
        }
        upMsessage();
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
            case R.id.tv_date:

                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        EmpWrongActivity.this, initStartDateTime);

                dateTimePicKDialog.dateTimePicKDialog(tvDate,"","");
                break;
        }
    }
}
