package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.HubSignListAdapter;
import com.example.administrator.yanfoxconn.bean.HubList;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HubSignActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤

    private String area;//區域
    private String url;//地址
    private String id;//單號
    private String person;//簽收人
    private String result;//网络获取结果
    private List<HubList> hubSignList;//物品列表
    private HubSignListAdapter hubSignListAdapter;//物品列表适配器

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.tv_id)
    TextView tvId;//單號
    @BindView(R.id.lv_sign_goods)
    MyListView lvSignGoods;//物品列表
    @BindView(R.id.btn_sign)
    Button btnSign;//簽收按鈕

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_sign);
        ButterKnife.bind(this);
        area = getIntent().getStringExtra("area");
        tvTitle.setText("HUB倉簽收");
        btnSign.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        getMessage(area);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_sign:
                receipt();
                break;
        }
    }

    private void getMessage(String area) {
        showDialog();

        try {
            String area1 =  URLEncoder.encode(URLEncoder.encode(area.toString(), "UTF-8"), "UTF-8");
            url = Constants.HTTP_HUB_RECEIVE_INFO_SERVLET + "?area=" + area1;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("-----------", "url-----" + url);
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
                        hubSignList = new ArrayList<HubList>();
                        try {
                            JSONArray array = new JSONArray(ss);
                            for(int i=0;i<array.length();i++){
                                JSONObject goods = array.getJSONObject(i);// 遍历 jsonarray 数组，把每一个对象转成 json 对象
                                HubList hub = new HubList();
                                hub.setID(goods.getString("ID"));
                                hub.setMATERIAL_NAME(goods.getString("MATERIAL_NAME"));
                                hub.setMATERIAL_SPEC(goods.getString("MATERIAL_SPEC"));
                                hub.setAPPLY_COUNT(goods.getString("APPLY_COUNT"));
                                hub.setUNIT(goods.getString("UNIT"));
                                hubSignList.add(hub);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                        dismissDialog();

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

    private void setText() {
        tvId.setText(hubSignList.get(0).getID());
        if(hubSignList!=null){
            hubSignListAdapter = new HubSignListAdapter(hubSignList);
            lvSignGoods.setAdapter(hubSignListAdapter);
        }else {
            ToastUtils.showShort(HubSignActivity.this, "沒有數據!");
        }
    }

    //確認簽收
    private void receipt(){
        showDialog();

        id=tvId.getText().toString();
        person= FoxContext.getInstance().getName();
        try {
            String area1 =  URLEncoder.encode(URLEncoder.encode(area.toString(), "UTF-8"), "UTF-8");
            String person1 =  URLEncoder.encode(URLEncoder.encode(person.toString(), "UTF-8"), "UTF-8");
            url = Constants.HTTP_HUB_RECEIVE_SERVLET + "?id=" + id  + "&area=" + area1 +"&person=" + person1;
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
                    ToastUtils.showLong(HubSignActivity.this,R.string.net_mistake);
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
