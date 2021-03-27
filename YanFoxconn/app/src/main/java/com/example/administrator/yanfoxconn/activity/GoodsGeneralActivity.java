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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.EmpListAdapter;
import com.example.administrator.yanfoxconn.adapter.GoodsListAdapter;
import com.example.administrator.yanfoxconn.adapter.ZhiyinshuiCheckAdapter;
import com.example.administrator.yanfoxconn.bean.EmpFile;
import com.example.administrator.yanfoxconn.bean.EmpMessage;
import com.example.administrator.yanfoxconn.bean.GoodsMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.DateTimePickDialogUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
 * Created by wangqian on 2018/12/20.
 */

public class GoodsGeneralActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_NETMISTAKE = 4;//網絡錯誤
    private final int MESSAGE_SHOW = 5;//顯示提醒
    private final int MESSAGE_CHOISE = 6;//選擇
    private final int MESSAGE_SET_NAME = 7;//設置攜帶人姓名


    private String initStartDateTime; // 初始化开始时间
    private Date selectTime = null;//所選時間
    private Date curDate = null;//當前時間
    private String curDates;//當前時間
    private SimpleDateFormat formatter;
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.tv_show)
    TextView tvShow;//提示信息
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_eff_num)
    TextView tvEffNum;//放行单号
    @BindView(R.id.tv_eff_data)
    TextView tvEffDate;//有效日期
    @BindView(R.id.tv_out_location)
    TextView tvOutLocation;//流出地点
    @BindView(R.id.tv_rec_unit)
    TextView tvRecUnit;//接收单位
    @BindView(R.id.tv_cir_mode)
    TextView tvCirMode;//流通方式
    @BindView(R.id.rv_goods)
    RecyclerView rvGoods;//物品列表
    @BindView(R.id.et_release_gate)
    EditText etRelGate;//放行門崗
    @BindView(R.id.tv_release_date)
    TextView tvReleaseDate;//放行時間
    @BindView(R.id.tr_jc)
    TableRow trJc;//進出
    @BindView(R.id.rb_j)
    RadioButton rb_j;//進
    @BindView(R.id.tr_list_gate)
    TableRow trListGate;
    @BindView(R.id.lv_gate)
    MyListView lvGate;//放行門崗列表
    @BindView(R.id.tv_duty_guard)
    TextView tvDutyGuard;//当值警卫
    @BindView(R.id.et_code)
    EditText etCode;//工號
    @BindView(R.id.et_name)
    EditText etName;//姓名

    private String code;//工號或車牌
    private String url;//地址
    private String result;//网络获取结果
    private List<GoodsMessage> goodsMessage;//物品信息
    private List<GoodsMessage> goodsList;//物品列表
    private GoodsListAdapter goodsListAdapter;//物品列表适配器
    private String xcode,xname;//攜帶人工號，姓名
    private List<String> teamList;
    private EmpListAdapter mAdapter;
    private List<EmpFile> empFileList;
    private AlertDialog alertDialog1; //信息框
    private String[] dandan;//有多個單號時的單號
    private List<EmpMessage> empMessagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_general);
        ButterKnife.bind(this);

        code = getIntent().getStringExtra("code");

        tvTitle.setText("普通物品放行");
        btnBack.setOnClickListener(this);
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        etRelGate.setTag("0");//用於判斷是否選擇門崗0;為未選擇,其他為選擇
        tvReleaseDate.setOnClickListener(this);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        initStartDateTime = formatterUse.format(curDate);
        tvReleaseDate.setText(formatter.format(curDate));
        curDates = formatter.format(curDate);

        getMessage(code,"");

        //搜索关键字
        etRelGate.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                trListGate.setVisibility(View.VISIBLE);
                String a = etRelGate.getText().toString().toUpperCase();
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
            case R.id.btn_title_right:
                check();
                break;
            case R.id.tv_release_date:
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(GoodsGeneralActivity.this, initStartDateTime);
                dateTimePicKDialog.dateTimePicKDialog(tvReleaseDate,"","");
                break;
        }
    }


    private void getMessage(String code, String id) {
        showDialog();
        try {
            String code1 =  URLEncoder.encode(URLEncoder.encode(code.toString(), "UTF-8"), "UTF-8");
            url = Constants.HTTP_COMMON_GOODS_SERVLET + "?flag=CC" + "&code=" + code1 + "&id=" + id;
            } catch (UnsupportedEncodingException e) {
              e.printStackTrace();
            }
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
                        goodsMessage = new ArrayList<GoodsMessage>();
                        for (JsonElement type : array) {
                            GoodsMessage humi = gson.fromJson(type, GoodsMessage.class);
                            goodsMessage.add(humi);
                        }

                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();
                        empFileList = new ArrayList<EmpFile>();
                        for (JsonElement type1 : array1) {
                            EmpFile humi1 = gson.fromJson(type1, EmpFile.class);
                            empFileList.add(humi1);
                        }

                        JsonObject jsonObject2 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array2 = jsonObject2.get("file2").getAsJsonArray();
                        goodsList = new ArrayList<GoodsMessage>();
                        for (JsonElement type2 : array2) {
                            GoodsMessage humi2 = gson.fromJson(type2, GoodsMessage.class);
                            goodsList.add(humi2);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else if (errCode.equals("300")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        goodsMessage = new ArrayList<GoodsMessage>();
                        for (JsonElement type : array) {
                            GoodsMessage humi = gson.fromJson(type, GoodsMessage.class);
                            goodsMessage.add(humi);
                        }

                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();
                        empFileList = new ArrayList<EmpFile>();
                        for (JsonElement type1 : array1) {
                            EmpFile humi1 = gson.fromJson(type1, EmpFile.class);
                            empFileList.add(humi1);
                        }

                        JsonObject jsonObject2 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array2 = jsonObject2.get("file2").getAsJsonArray();
                        goodsList = new ArrayList<GoodsMessage>();
                        for (JsonElement type2 : array2) {
                            GoodsMessage humi2 = gson.fromJson(type2, GoodsMessage.class);
                            goodsList.add(humi2);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SHOW;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    }else if (errCode.equals("500")) {
                        String ss = jsonObject.get("data").toString();
                        JSONArray array = null;
                        try {
                            array = new JSONArray(ss);
                            Log.e("----array.length()----",array.length()+"");
                            dandan=new String[array.length()];
                            for (int i=0;i<array.length();i++) {
                                JSONObject dan = array.getJSONObject(i);// 遍历 jsonarray 数组，把每一个对象转成 json 对象
                                dandan[i]=dan.getString("OUT00")+"-"+dan.getString("OUT11A")+"-"+dan.getString("OUT05");
                                Log.e("----dandan----",dandan[i]);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Message message = new Message();
                        message.what = MESSAGE_CHOISE;
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

    //彈出選擇單號框
    public void showList(String[] dan){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("請選擇單號");
        alertBuilder.setItems(dan, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getMessage(code,(dan[i].split("-"))[0]);
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = alertBuilder.create();
        alertDialog1.show();
        alertDialog1.setCanceledOnTouchOutside(false);
    }
    //提交前檢查
    private void check(){
        if(FoxContext.getInstance().getLoginId().equals("")){
            ToastUtils.showLong(GoodsGeneralActivity.this,"登錄超時,請退出重新登登錄!");
            return;
        }
        HashMap<String, String> etMap = GoodsListAdapter.getGoodsMap();
        String goodsMsg="";

        for (Map.Entry<String, String> entry : etMap.entrySet()){
            goodsMsg += entry.getKey()+"-"+entry.getValue()+"_";
            Log.e("---goodsMap---", entry.getKey()+"--->"+entry.getValue());
        }
        goodsMsg = goodsMsg.substring(0,goodsMsg.length()-1);
        Log.e("---goodsMsg---", goodsMsg);
        try {
            selectTime = formatter.parse(tvReleaseDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (etCode.getText().toString().replaceAll(" ","").equals("")){
            ToastUtils.showShort(GoodsGeneralActivity.this,"請輸入攜帶人工號!");
            return;
        }else {
            xcode=etCode.getText().toString().replaceAll(" ","");
        }
        if (etName.getText().toString().replaceAll(" ","").equals("")){
            ToastUtils.showShort(GoodsGeneralActivity.this,"請輸入攜帶人姓名!");
            return;
        }else{
            xname=etName.getText().toString().replaceAll(" ","");
        }
        if (selectTime.getTime()-curDate.getTime()>0){
            ToastUtils.showShort(GoodsGeneralActivity.this,"放行時間不能超過當前時間");
            return;
        }
        Log.e("---攜帶人---",  xcode+"--"+xname);
        if (etRelGate.getTag().equals("0")){
            ToastUtils.showShort(GoodsGeneralActivity.this,"請選擇放行門崗!");
            return;
        }

        String jc_type="";
        if (rb_j.isChecked()){
            jc_type="進";
        }else {
            jc_type="出";
        }


        upMsessage(code,tvEffNum.getText().toString(),change(etRelGate.getText().toString()),
                FoxContext.getInstance().getLoginId().toString(),FoxContext.getInstance().getName().toString(),
                tvCirMode.getText().toString(),tvReleaseDate.getText().toString(),jc_type,xcode,xname,goodsMsg);
    }
//提交数据
    private void upMsessage(String code, String id,String mengang,String scode,String sname,String type,
                            String creat_date,String jc_type,String xcode,String xname,String goodsMsg){
        showDialog();
        try {
         String code1 =  URLEncoder.encode(URLEncoder.encode(code.toString(), "UTF-8"), "UTF-8");
         String sname1 =  URLEncoder.encode(URLEncoder.encode(sname.toString(), "UTF-8"), "UTF-8");
         String type1 =  URLEncoder.encode(URLEncoder.encode(type.toString(), "UTF-8"), "UTF-8");
         String creat_date1 =  URLEncoder.encode(URLEncoder.encode(creat_date.toString(), "UTF-8"), "UTF-8");
         String mengang1 =  URLEncoder.encode(URLEncoder.encode(mengang.toString(), "UTF-8"), "UTF-8");
         String jc_type1 =  URLEncoder.encode(URLEncoder.encode(jc_type.toString(), "UTF-8"), "UTF-8");
         String xname1 =  URLEncoder.encode(URLEncoder.encode(xname.toString(), "UTF-8"), "UTF-8");
         String goodsMsg1 =  URLEncoder.encode(URLEncoder.encode(goodsMsg.toString(), "UTF-8"), "UTF-8");
        url = Constants.HTTP_COMMON_GOODS_SERVLET + "?flag=TT" + "&code=" + code1 + "&id=" + id + "&mengang=" +
                 mengang1 + "&sname=" + sname1+ "&type=" + type1 +"&scode=" + scode +"&creat_date=" + creat_date1
                +"&jc_type=" + jc_type1 +"&xcode=" + xcode +"&xname=" + xname1+"&goodsMsg=" + goodsMsg1;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
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
                case MESSAGE_CHOISE://text賦值
                    showList(dandan);
                    break;
                case MESSAGE_SET_NAME://名字賦值
                    etName.setText(empMessagesList.get(0).getCHINESENAME());
                    etCode.setEnabled(false);
                    etName.setEnabled(false);
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(GoodsGeneralActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SHOW://顯示提醒
                    setText();
                    tvShow.setVisibility(View.VISIBLE);
                    tvShow.setText(msg.obj.toString());
            }
            super.handleMessage(msg);
        }
    };

    private void setText() {
        tvEffNum.setText(goodsMessage.get(0).getOUT00());
        tvOutLocation.setText(goodsMessage.get(0).getOUT03());
        tvEffDate.setText(goodsMessage.get(0).getOUT02());
        tvRecUnit.setText(goodsMessage.get(0).getOUT04());
        tvCirMode.setText(goodsMessage.get(0).getOUT11A());

        if (goodsMessage.get(0).getOUT06A()!=null&&!goodsMessage.get(0).getOUT06A().equals("")){
            etCode.setText(goodsMessage.get(0).getOUT06A());
            etName.setText(goodsMessage.get(0).getOUT06B());
            etCode.setEnabled(false);
            etName.setEnabled(false);
        }else{
            etCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // 此处为得到焦点时的处理内容
                    } else {
                        // 此处为失去焦点时的处理内容
                        getXName(etCode.getText().toString().replaceAll(" ","").toUpperCase());
                    }

                }
            });
        }

        if(goodsMessage.get(0).getOUT11A().equals("跨廠區")){
            trJc.setVisibility(View.VISIBLE);
            rb_j.setChecked(true);
        }
        tvDutyGuard.setText(FoxContext.getInstance().getName());


        if (goodsList != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(GoodsGeneralActivity.this);
            rvGoods.setLayoutManager(layoutManager);
            rvGoods.setItemViewCacheSize(500);
            goodsListAdapter = new GoodsListAdapter(GoodsGeneralActivity.this, goodsList);
            rvGoods.setAdapter(goodsListAdapter);
        } else {
            ToastUtils.showShort(GoodsGeneralActivity.this, "沒有物品數據!");
        }
        teamList = new ArrayList<>();
        for (int i = 0;i<empFileList.size();i++){
            teamList.add(change1(empFileList.get(i).getID()+","+empFileList.get(i).getAQ1()+"-"+empFileList.get(i).getAQ2()+"-"+empFileList.get(i).getAQ3()+"-"+empFileList.get(i).getAQ4()));
        }
        mAdapter = new EmpListAdapter(GoodsGeneralActivity.this,teamList);
        lvGate.setAdapter(mAdapter);

        //稽核門崗列表,選中后1:tit賦值,2:列表隱藏
        mAdapter.OnClickSetText(new EmpListAdapter.OnClickSetText() {
            @Override
            public void OnClickxt(String tit) {
                etRelGate.setText(tit.split(",")[1]);
                etRelGate.setTag(tit.split(",")[0]);
//                paramMap.put("mengang", empFileList.get(position).getID());
                mAdapter.SearchCity("");
                trListGate.setVisibility(View.GONE);
            }
        });
    }

    //輸入工號獲取攜帶人姓名
    private void getXName(String xcode){
        showDialog();
        final String url = Constants.HTTP_COMMON_FORMS_SERVLET+"?code="+xcode;

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

                        Message message = new Message();
                        message.what = MESSAGE_SET_NAME;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    }
                }
            } }.start();
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


