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
import com.example.administrator.yanfoxconn.adapter.CarCrossListsAdapter;
import com.example.administrator.yanfoxconn.adapter.CrossListsAdapter;
import com.example.administrator.yanfoxconn.bean.CrossScanList;
import com.example.administrator.yanfoxconn.bean.CrossScanMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 車輛跨區無紙化作業界面
 * Created by wang on 2019/05/06.
 */

public class CrossCarActivity extends BaseActivity implements View.OnClickListener {

    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_show)
    TextView tvShow;//提示信息
    @BindView(R.id.tv_packing_no)
    TextView tvNo;//單號
    @BindView(R.id.tv_flow_to)
    TextView tvFlowTo;//流向
    @BindView(R.id.tv_start_add)
    TextView tvStart;//出發地
    @BindView(R.id.tv_end_add)
    TextView tvEnd;//目的地
    @BindView(R.id.tv_type)
    TextView tvType;//包裝種類
    @BindView(R.id.tv_num)
    TextView tvNum;//包裝件數
    @BindView(R.id.tv_plate)
    TextView tvPlate;//車牌號
    @BindView(R.id.tv_xiangmen)
    TextView tvXiangmen;//箱門數
    @BindView(R.id.tv_feng)
    TextView tvFeng;//封號
    @BindView(R.id.lv_feng)
    MyListView lvFeng;//封號
    @BindView(R.id.lv_goods)
    MyListView lvGoods;//物品
    @BindView(R.id.btn_release)
    Button btnRelease;//放行

    private String qrResult;//掃描結果
    private String url;//地址
    private String result;
    private List<CrossScanMessage> crossMessage;//信息
    private List<CrossScanList> crossScanLists;//封號列表
    private List<CrossScanList> crossGoodsLists;//物品列表
    private CarCrossListsAdapter carcrossListAdapter;
    private CrossListsAdapter crossListAdapter;

    private String fenghao="";//封號

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_car);
        ButterKnife.bind(this);

        Bundle bundle = this.getIntent().getExtras();
        qrResult = bundle.getString("result");

        tvTitle.setText("車輛跨區無紙化作業");
        btnBack.setOnClickListener(this);
        btnRelease.setOnClickListener(this);
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        if (FoxContext.getInstance().getLoginId().equals("YTJW")){
            btnRelease.setVisibility(View.GONE);
        }
        getMessage(qrResult);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_release:
                if (FoxContext.getInstance().getLoginId().equals("")){
                    ToastUtils.showLong(CrossCarActivity.this,"賬號出現問題,請重新登陸!");
                }else{
                    upMessage(tvNo.getText().toString(), FoxContext.getInstance().getLoginId(),fenghao);
                }
                break;
        }
    }

    private void getMessage(String qrResult) {
        showDialog();

        try {
            String qrResult1 = URLEncoder.encode(URLEncoder.encode(qrResult.toString(), "UTF-8"), "UTF-8");
            url = Constants.HTTP_EXPORTIO_SERVLET_CHE + "?chepai=" + qrResult1;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("-----------", "url-----" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Log.e("-----result---------", "result==" + result);
                Gson gson = new Gson();
                if (result != null) {
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        crossMessage = new ArrayList<CrossScanMessage>();

                        for (JsonElement type : array) {
                            CrossScanMessage humi = gson.fromJson(type, CrossScanMessage.class);
                            crossMessage.add(humi);
                        }

                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();

                        crossScanLists = new ArrayList<CrossScanList>();
                        for (JsonElement type1 : array1) {
                            CrossScanList humi1 = gson.fromJson(type1, CrossScanList.class);
                            crossScanLists.add(humi1);
                        }
                        ArrayList<String> arrFeng = new ArrayList<String>();
                        for(int i=0;i<crossScanLists.size();i++){
                            arrFeng.add(crossScanLists.get(i).getQIANFENG().toString());
                            fenghao = arrFeng.toString().substring(1,arrFeng.toString().length()-1).replace(",","/").replace(" ","");
                        }

                        JsonObject jsonObject2 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array2 = jsonObject2.get("file2").getAsJsonArray();

                        crossGoodsLists = new ArrayList<CrossScanList>();
                        for (JsonElement type2 : array2) {
                            CrossScanList humi2 = gson.fromJson(type2, CrossScanList.class);
                            crossGoodsLists.add(humi2);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else if(errCode.equals("300")){
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        crossMessage = new ArrayList<CrossScanMessage>();

                        for (JsonElement type : array) {
                            CrossScanMessage humi = gson.fromJson(type, CrossScanMessage.class);
                            crossMessage.add(humi);
                        }

                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();

                        crossScanLists = new ArrayList<CrossScanList>();
                        for (JsonElement type1 : array1) {
                            CrossScanList humi1 = gson.fromJson(type1, CrossScanList.class);
                            crossScanLists.add(humi1);
                        }
                        fenghao = crossMessage.get(0).getBCQ182();

                        JsonObject jsonObject2 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array2 = jsonObject2.get("file2").getAsJsonArray();

                        crossGoodsLists = new ArrayList<CrossScanList>();
                        for (JsonElement type2 : array2) {
                            CrossScanList humi2 = gson.fromJson(type2, CrossScanList.class);
                            crossGoodsLists.add(humi2);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SHOW;
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
            }
        }.start();
    }

    private void upMessage(String id, String scode,String qianfeng) {
        showDialog();
        Log.e("------fenghao-------",fenghao);
        url = Constants.HTTP_EXPORTIOUPDATE_SERVLET_CHE + "?id=" + id + "&scode=" + scode +"&qianfeng=" + qianfeng;
        Log.e("-----------", "url-----" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Log.e("----result--------", "result==" + result);
                if (result != null) {
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Message message = new Message();
                        message.what = MESSAGE_UP;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    } else  {
                        Message message = new Message();
                        message.what = MESSAGE_UP;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
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
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(CrossCarActivity.this,R.string.net_mistake);
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

        tvNo.setText(crossMessage.get(0).getBCQ01());
        tvFlowTo.setText(crossMessage.get(0).getBCQ00());
        tvStart.setText(crossMessage.get(0).getBCQ13());
        tvEnd.setText(crossMessage.get(0).getBCQ14());
        tvType.setText(crossMessage.get(0).getBCQ09());
        tvNum.setText(crossMessage.get(0).getBCQ08());
        tvPlate.setText(crossMessage.get(0).getBCQ06());
        tvXiangmen.setText(crossMessage.get(0).getBCQ03());

        if(!crossMessage.get(0).getBCQ182().equals("")) {
            tvFeng.setVisibility(View.VISIBLE);
            tvFeng.setText(crossMessage.get(0).getBCQ182());
        }else if(toInt(crossMessage.get(0).getBCQ03())==0){
            tvFeng.setVisibility(View.VISIBLE);
            tvFeng.setText("此車無箱櫃");
        }else if(toInt(crossMessage.get(0).getBCQ03())==crossScanLists.size()){
            carcrossListAdapter = new CarCrossListsAdapter(CrossCarActivity.this, crossScanLists);
            lvFeng.setAdapter(carcrossListAdapter);
        }else{
            carcrossListAdapter = new CarCrossListsAdapter(CrossCarActivity.this, crossScanLists);
            lvFeng.setAdapter(carcrossListAdapter);
            ToastUtils.showShort(CrossCarActivity.this, "鉛封號數量與箱門數不符");
//            btnRelease.setBackgroundColor(ContextCompat.getColor(CrossCarActivity.this, R.color.color_c3c3c3));
            btnRelease.setBackgroundColor(getResources().getColor(R.color.color_c3c3c3));
//            btnRelease.setTextColor(ContextCompat.getColor(CrossCarActivity.this, R.color.color_303030));
            btnRelease.setTextColor(getResources().getColor(R.color.color_303030));
            btnRelease.setClickable(false);
        }

        crossListAdapter = new CrossListsAdapter(CrossCarActivity.this, crossGoodsLists);
        lvGoods.setAdapter(crossListAdapter);

    }

    /** * String 转换int */
    public int toInt(String tostring) {
        return Integer.parseInt(tostring);
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
