package com.example.administrator.yanfoxconn.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.InputListAdapter;
import com.example.administrator.yanfoxconn.bean.HubList;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangqian on 2019/2/14.Hub倉輸入區域界面
 */

public class HubInputActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_NETMISTAKE = 4;//網絡錯誤
    private String area;//區域
    private String url;//地址
    private String result;//网络获取结果
    private List<HubList> minputList;//人名列表
    private InputListAdapter minputListAdapter;//輸入列表适配器

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回

    @BindView(R.id.lv_input)
    ListView lvInput;//人名列表

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_input);
        ButterKnife.bind(this);

        tvTitle.setText("請選擇");
        btnBack.setOnClickListener(this);
        area = getIntent().getStringExtra("area");
        Log.e("----------", "area===" + area);
        lvInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HubInputActivity.this,HubGoodsActivity.class);
                intent.putExtra("area",area);
                intent.putExtra("dep",minputList.get(i).getAPPLYER_ADD());
                intent.putExtra("name",minputList.get(i).getAPPLYER());
                intent.putExtra("phone",minputList.get(i).getAPPLYER_TEL());
                startActivity(intent);
            }
        });
    }
    private void setText() {
        if(minputList != null){
            minputListAdapter = new InputListAdapter(HubInputActivity.this, minputList);
            lvInput.setAdapter(minputListAdapter);
        }else {
            ToastUtils.showShort(HubInputActivity.this, "沒有數據!");
        }
    }
    private void getMessage(String area) {

        try {
            String area1 =  URLEncoder.encode(URLEncoder.encode(area.toString(), "UTF-8"), "UTF-8");
            url = Constants.HTTP_HUB_AREA_SERVLET + "?area=" + area1 ;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("-----------", "url-----" + url);
        showDialog();
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        String ss = jsonObject.get("data").toString();
                        minputList = new ArrayList<HubList>();
                        try {
                            JSONArray array = new JSONArray(ss);
                            for(int i=0;i<array.length();i++){
                                JSONObject goods = array.getJSONObject(i);// 遍历 jsonarray 数组，把每一个对象转成 json 对象
                                HubList input = new HubList();
                                input.setAPPLYER(goods.getString("APPLYER"));
                                input.setAPPLYER_ADD(goods.getString("APPLYER_ADD"));
                                input.setAPPLYER_TEL(goods.getString("APPLYER_TEL"));
                                minputList.add(input);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                } else{
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
                    ToastUtils.showLong(HubInputActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
                    dismissDialog();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
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
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.e("---------", "onStart");
        getMessage(area);
    }
}
