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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.JqtbListAdapter;
import com.example.administrator.yanfoxconn.bean.AQ110Message;
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
 * 人資活動發佈
 * Created by S1007989 on 2021/5/18.
 */

public class ActEndActivity extends BaseActivity implements View.OnClickListener{

    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private String initStartDateTime; // 初始化开始时间
    private Date selectTime = null;//所選時間
    private Date curDate = null;//當前時間
    private String curDates;//當前時間
    private SimpleDateFormat formatter;

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTittle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.rb_in)
    RadioButton rbIn;//內線
    @BindView(R.id.rb_out)
    RadioButton rbOut;//外線
    @BindView(R.id.tv_date)
    TextView tvDate;//時間
    @BindView(R.id.sp_area)
    Spinner spTeam;//警情位置
    @BindView(R.id.et_position)
    EditText etPosition;//警情位置
    @BindView(R.id.et_phone)
    EditText etPhone;//聯繫方式
    @BindView(R.id.et_content)
    EditText etContent;//報警內容
    @BindView(R.id.et_gate_post)
    EditText etGatePost;//中心處置
    @BindView(R.id.lv_gate)
    MyListView lvGate;//中心處置
    @BindView(R.id.tr_list_gate)
    TableRow trLIstGate;//中心處置

    private List<String> teamList;
    private JqtbListAdapter mAdapter;
    private List<String> areaList;
    private List<AQ110Message> keduiList;
    private String teamSp = "";//中心處置
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_release);
        ButterKnife.bind(this);

        getMessage();//根據工號獲得信息
        tvTittle.setText("活動發佈");
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

                    ToastUtils.showLong(ActEndActivity.this, msg.obj.toString());
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
        // tvID.setText(empMessagesList.get(0).getWORKNO());
        //tvName.setText(empMessagesList.get(0).getCHINESENAME());
        //tvPro.setText(empMessagesList.get(0).getBU_CODE());
        // tvDep.setText(empMessagesList.get(0).getCZC03());
        // tvLecel.setText(empMessagesList.get(0).getEMPLOYEELEVEL()+empMessagesList.get(0).getMANAGER());
        // tvJoinDate.setText(empMessagesList.get(0).getJOINGROUPDATE());

        //警情位置下拉列表選擇
        spTeam.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, areaList));
        spTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                teamSp = areaList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        teamList = new ArrayList<>();
        for (int i = 0;i<keduiList.size();i++){
            teamList.add(change1(keduiList.get(i).getKEDUI()));
        }
        mAdapter = new JqtbListAdapter(ActEndActivity.this,teamList);
        lvGate.setAdapter(mAdapter);

        //稽核門崗列表,選中后1:tit賦值,2:列表隱藏
        mAdapter.OnClickSetText(new JqtbListAdapter.OnClickSetText() {
            @Override
            public void OnClickxt(String tit) {
                etGatePost.setText(tit);
                mAdapter.SearchCity("");
                trLIstGate.setVisibility(View.GONE);
            }
        });

    }
    //提交前檢查
    private void check(){
        if(FoxContext.getInstance().getLoginId().equals("")){
            ToastUtils.showLong(ActEndActivity.this,"登錄超時,請退出重新登錄!");
            return;
        }
        if (rbIn.isChecked()==false&&rbOut.isChecked()==false){
            ToastUtils.showShort(ActEndActivity.this,"請選擇報警方式！");
            return;
        }
        try {
            selectTime = formatter.parse(tvDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (selectTime.getTime()-curDate.getTime()>0){
            ToastUtils.showShort(ActEndActivity.this,"請检查報案日期是否在當前時間之前");
            return;
        }

        if (etPosition.getText().toString().replaceAll(" ","").equals("")){
            ToastUtils.showShort(ActEndActivity.this,"警情位置不能為空！");
            return;
        }
        if (etPhone.getText().toString().replaceAll(" ","").equals("")){
            ToastUtils.showShort(ActEndActivity.this,"聯繫方式不能為空！");
            return;
        }
        if (etContent.getText().toString().replaceAll(" ","").equals("")){
            ToastUtils.showShort(ActEndActivity.this,"報警內容不能為空！");
            return;
        }
        if (etGatePost.getText().toString().replaceAll(" ","").equals("")){
            ToastUtils.showShort(ActEndActivity.this,"指揮中心處置不能為空！");
            return;
        }

        upMsessage();
    }
    //提交信息
    private void upMsessage(){

        final String utl = Constants.HTTP_COMMON_FORMS_110JQTB_UP_SERVLET;

        //文本資料全部添加到Map裡
        final Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("flag", "E");//110接處警
        if (rbIn.isChecked()) paramMap.put("cpc", "內線");
        else paramMap.put("cpc", "外線");//報警方式
        paramMap.put("login_code", FoxContext.getInstance().getLoginId());//接警人工號
        paramMap.put("login_name",FoxContext.getInstance().getName());//接警人
        paramMap.put("bumen", teamSp+"區"+etPosition.getText().toString());//警情位置
        paramMap.put("wj_remark", etContent.getText().toString());//報警內容
        paramMap.put("other", etPhone.getText().toString());//聯繫方式
        paramMap.put("kedui", etGatePost.getText().toString());//指揮中心處置
        paramMap.put("ck_type", "N");//狀態（是否結案）

        if(tvDate.getText().toString().equals(curDates)){
            paramMap.put("date0", "");
        }else{
            paramMap.put("date0", tvDate.getText().toString());//報案日期
        }

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
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
            case R.id.tv_date:

                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        ActEndActivity.this, initStartDateTime);

                dateTimePicKDialog.dateTimePicKDialog(tvDate,"","");
                break;
        }
    }
}
