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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.EmpListAdapter;
import com.example.administrator.yanfoxconn.adapter.IntelligenceGoodsListAdapter;
import com.example.administrator.yanfoxconn.bean.EmpFile;
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
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import taobe.tec.jcc.JChineseConvertor;

/**
 * Created by wangqian on 2019/07/01.
 */

public class GoodsIntelligenceActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_NETMISTAKE = 4;//網絡錯誤
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 5;//顯示提醒
    private final int MESSAGE_CHOISE = 6;//選擇

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
    TextView tvEffDate;//攜出時段
    @BindView(R.id.tv_out_to)
    TextView tvOutTo;//攜出流向
    @BindView(R.id.tv_text2)
    TextView tvText2;//攜入時段
    @BindView(R.id.tv_text1)
    TextView tvText1;//攜入地點
    @BindView(R.id.tv_release_date)
    TextView tvReleaseDate;//放行時間
    @BindView(R.id.tv_cir_mode)
    TextView tvCirMode;//流通方式
    @BindView(R.id.lv_goods)
    MyListView lvGoods;//物品列表
    @BindView(R.id.et_release_gate)
    EditText etRelGate;//放行門崗
    @BindView(R.id.tr_list_gate)
    TableRow trListGate;
    @BindView(R.id.lv_gate)
    MyListView lvGate;//放行門崗列表
    @BindView(R.id.et_duty_guard)
    TextView tvDutyGuard;//当值警卫

    private String code;//工號或車牌
    private String id;//放行單號后4位
    private String url;//地址
    private String result;//网络获取结果
    private List<GoodsMessage> goodsMessage;//物品信息
    private List<GoodsMessage> goodsList;//物品列表
    private IntelligenceGoodsListAdapter goodsListAdapter;//物品列表适配器

    private List<String> teamList;
    private EmpListAdapter mAdapter;
    private List<EmpFile> empFileList;
    private AlertDialog alertDialog1; //信息框
    private String[] dandan;//有多個單號時的單號

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_intelligence);
        ButterKnife.bind(this);

        code = getIntent().getStringExtra("code");
        id = getIntent().getStringExtra("id");

        tvTitle.setText("智慧物品放行");
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
        getMessage(code,id);

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
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(GoodsIntelligenceActivity.this, initStartDateTime);
                dateTimePicKDialog.dateTimePicKDialog(tvReleaseDate,"","");
                break;
        }
    }
    //提交前檢查
    private void check(){
        if(FoxContext.getInstance().getLoginId().equals("")){
            ToastUtils.showLong(GoodsIntelligenceActivity.this,"登錄超時,請退出重新登登錄!");
            return;
        }
        try {
            selectTime = formatter.parse(tvReleaseDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (selectTime.getTime()-curDate.getTime()>0){
            ToastUtils.showShort(GoodsIntelligenceActivity.this,"放行時間不能超過當前時間");
            return;
        }
        if (etRelGate.getTag().equals("0")){
            ToastUtils.showShort(GoodsIntelligenceActivity.this,"請選擇放行門崗!");
            return;
        }
        upMsessage(code,tvEffNum.getText().toString(),change(etRelGate.getText().toString()),FoxContext.getInstance().getLoginId().toString(),FoxContext.getInstance().getName().toString(),tvCirMode.getText().toString(),tvReleaseDate.getText().toString());
    }

    private void getMessage(String code, String id) {
        showDialog();
        try {
            String code1 =  URLEncoder.encode(URLEncoder.encode(code.toString(), "UTF-8"), "UTF-8");
            url = Constants.HTTP_INTELLIGENCE_GOODS_SERVLET + "?flag=CC" + "&code=" + code1 + "&id=" + id;
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

                    }else if (errCode.equals("300")) {
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
                            dandan=new String[array.length()];
                            for (int i=0;i<array.length();i++) {
                                JSONObject dan = array.getJSONObject(i);// 遍历 jsonarray 数组，把每一个对象转成 json 对象
                                dandan[i]=dan.getString("DOCUMENT_NO");
                                Log.e("-------dandan---------",dandan[i]);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Message message = new Message();
                        message.what = MESSAGE_CHOISE;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    }else{
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
        final String[] items = dan;
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("請選擇單號");
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getMessage(code,items[i]);
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = alertBuilder.create();
        alertDialog1.show();
        alertDialog1.setCanceledOnTouchOutside(false);
    }

    //提交数据
    private void upMsessage(String code, String id,String menggang,String scode,String sname,String type,String creat_date){
        showDialog();
        try {
            String code1 =  URLEncoder.encode(URLEncoder.encode(code.toString(), "UTF-8"), "UTF-8");
            String sname1 =  URLEncoder.encode(URLEncoder.encode(sname.toString(), "UTF-8"), "UTF-8");
            String type1 =  URLEncoder.encode(URLEncoder.encode(type.toString(), "UTF-8"), "UTF-8");
            String creat_date1 =  URLEncoder.encode(URLEncoder.encode(creat_date.toString(), "UTF-8"), "UTF-8");
            String menggang1 =  URLEncoder.encode(URLEncoder.encode(menggang.toString(), "UTF-8"), "UTF-8");
            url = Constants.HTTP_INTELLIGENCE_GOODS_SERVLET + "?flag=TT" + "&code=" + code1 + "&id=" + id + "&mengang=" + menggang1 + "&sname=" + sname1+ "&type=" + type1 +"&scode=" + scode +"&creat_date=" + creat_date1;
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
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(GoodsIntelligenceActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_CHOISE://text賦值
                    showList(dandan);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
                case MESSAGE_SHOW://顯示提醒
                    setText();
                    tvShow.setVisibility(View.VISIBLE);
                    tvShow.setText(msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setText() {

        tvEffNum.setText(goodsMessage.get(0).getDOCUMENT_NO());
        tvDutyGuard.setText(FoxContext.getInstance().getName());
        if(goodsMessage.get(0).getBRING_OUT().equals("ON")){
            if(goodsMessage.get(0).getOUT_FLAG().equals("in")){
                tvCirMode.setText("廠區內");
            }else if(goodsMessage.get(0).getOUT_FLAG().equals("out")){
                tvCirMode.setText("跨廠區");
            }else{
                tvCirMode.setText("廠區外");
            }
            tvOutTo.setText(goodsMessage.get(0).getTAKETO_PLACE().replaceAll("###"," ").replaceAll("@@@"," >>> "));
            tvEffDate.setText(goodsMessage.get(0).getOUT_STDATE()+" >>> "+goodsMessage.get(0).getOUT_ENDDATE());
        }else{
            tvCirMode.setText("攜入");
            tvText2.setText("攜入時段");
            tvText1.setText("攜入地點");
            tvOutTo.setText(goodsMessage.get(0).getTAKEIN_PLACE());
            tvEffDate.setText(goodsMessage.get(0).getIN_STDATE()+" >>> "+goodsMessage.get(0).getIN_ENDDATE());
        }

        goodsListAdapter = new IntelligenceGoodsListAdapter(GoodsIntelligenceActivity.this, goodsList);
        lvGoods.setAdapter(goodsListAdapter);

        teamList = new ArrayList<>();
        for (int i = 0;i<empFileList.size();i++){
            teamList.add(change1(empFileList.get(i).getID()+","+empFileList.get(i).getAQ1()+"-"+empFileList.get(i).getAQ2()+"-"+empFileList.get(i).getAQ3()+"-"+empFileList.get(i).getAQ4()));
        }
        mAdapter = new EmpListAdapter(GoodsIntelligenceActivity.this,teamList);
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


