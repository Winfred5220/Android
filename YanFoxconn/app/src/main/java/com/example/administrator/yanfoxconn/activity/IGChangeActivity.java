package com.example.administrator.yanfoxconn.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.IGMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description 宿舍寄存 儲位變更主界面
 * @Author song
 * @Date 4/15/21 11:04 AM
 */
public class IGChangeActivity extends BaseActivity implements View.OnClickListener {

    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_NEW_STORE = 1;//帶出倉庫列表
    private final int MESSAGE_DELETE_SUCCESS = 3;//刪除成功，刷新列表
    private final int MESSAGE_NOT_NET = 4;//顯示提醒
    private final int MESSAGE_SET_TEXT = 5;//顯示信息

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_type)
    TextView tvType;//
    @BindView(R.id.tv_num)
    TextView tvNum;//
    @BindView(R.id.tv_store)
    TextView tvStore;//
    @BindView(R.id.sp_store)
    Spinner spStore;//選擇倉庫
    @BindView(R.id.tr_new_store)
    TableRow trNewStore;//新儲位行
    @BindView(R.id.sp_sl)
    Spinner spNewStore;//新儲位

    private List<String>  stores,slList;

    private List<IGMessage> msgList, storeList;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
Log.e("-------","IGChangeActivity");
        setContentView(R.layout.activity_ig_change);
        ButterKnife.bind(this);
        tvTitle.setText("更換儲位");

        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnUp.setVisibility(View.VISIBLE);
        getLocationById(getIntent().getStringExtra("id"), FoxContext.getInstance().getName());
    }

    /**
     * 掃碼帶出 儲位信息
     * @param sl_code 儲位編碼
     *
     */
    private void getLocationById(String sl_code,String name){
        showDialog();
        String zwuser = null;
        try {
            zwuser = URLEncoder.encode(URLEncoder.encode(name, "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String url = Constants.HTTP_LOCATION_BY_ID + "?sl_code=" + sl_code+"&zwuser="+zwuser ;

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForGet(url);

                dismissDialog();
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data1").getAsJsonArray();

                        msgList = new ArrayList<IGMessage>();

                        for (JsonElement type : array) {
                            IGMessage humi = gson.fromJson(type, IGMessage.class);
                            msgList.add(humi);
                        }

                        JsonArray array2 = jsonObject.get("data2").getAsJsonArray();

                        storeList = new ArrayList<IGMessage>();

                        for (JsonElement type : array2) {
                            IGMessage humi = gson.fromJson(type, IGMessage.class);
                            storeList.add(humi);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
//                        Log.e("-----------", "result==" + result);
//                        Message message = new Message();
//                        message.what = MESSAGE_TOAST;
//                        message.obj = jsonObject.get("errMessage").getAsString();
//                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
    }

    private String store;

    /**
     * 倉庫下拉列表
     */
    public void setSpinner() {
        stores = new ArrayList<>();
        stores.add("請選擇倉庫");
        for (int i = 0; i < storeList.size(); i++) {
            stores.add(storeList.get(i).getSh_code());
        }
        spStore.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stores));
        spStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                store = stores.get(position);
                if (store.equals("請選擇倉庫")) {

                } else {
                    getStoreBySl(store);

                }
//                Log.e("---------", "最喜欢的水果是：" + str);
//                paramMap.put("kedui",str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private String slcode;
    /**
     * 儲位下拉列表
     */
    public void setSlSpinner() {

        spNewStore.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, slList));
        spNewStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                slcode = slList.get(position);
//                Log.e("---------", "最喜欢的水果是：" + str);
//                paramMap.put("kedui",str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //根據倉庫編號 獲取儲位
    private void getStoreBySl(String shCode) {

        final String url = Constants.HTTP_LOCATION_FIND + "?sh_code=" + shCode ;

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
                        slList = new ArrayList<String>();

                        for (JsonElement type : array) {
                            String humi = gson.fromJson(type, String.class);
                            slList.add(humi);

                        }

                        Message message = new Message();
                        message.what = MESSAGE_NEW_STORE;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
    }

    private void upNewStore(String id,String slId,String shCode,String nSlCode){
        showDialog();
        final String url = Constants.HTTP_LOCATION_UPDATE + "?id=" + id+"&sl_id="+slId+"&sh_code="+shCode+"&n_sl_code="+nSlCode ;

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
                    dismissDialog();
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {


                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
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
                case MESSAGE_NEW_STORE:
                    trNewStore.setVisibility(View.VISIBLE);
                    setSlSpinner();
                    break;
                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

                    ToastUtils.showLong(IGChangeActivity.this, msg.obj.toString());
                    finish();
                    break;
                case MESSAGE_SET_TEXT:
                    if (msgList.size()>0){
                    tvType.setText(msgList.get(0).getS_DEPOSIT_NAME());
                            tvNum.setText("1");
                    tvStore.setText(msgList.get(0).getSL_CODE());
                    setSpinner();
                    }else{
                        ToastUtils.showLong(IGChangeActivity.this,"此儲位無寄存信息！");
                        finish();
                    }
                break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_right://提交
                if (store.equals("請選擇倉庫")){
                    ToastUtils.showShort(IGChangeActivity.this,"請選擇倉庫");
                }else{
upNewStore(msgList.get(0).getID(),getIntent().getStringExtra("id"),store,slcode);}
                break;
            case R.id.btn_title_left://返回
                finish();
                break;
        }
    }
}
