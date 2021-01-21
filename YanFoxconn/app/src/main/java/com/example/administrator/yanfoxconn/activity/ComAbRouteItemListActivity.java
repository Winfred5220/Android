package com.example.administrator.yanfoxconn.activity;

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
import com.example.administrator.yanfoxconn.adapter.AbnormalListAdapter;
import com.example.administrator.yanfoxconn.bean.AbnormalMessage;
import com.example.administrator.yanfoxconn.bean.ComScanViewMessage;
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 查看已點檢點位 點檢項詳情列表界面
 * Created by song on 2020/7/30.
 */

public class ComAbRouteItemListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_SEE_ABNORMAL = 3;
    private final int MESSAGE_UPLOAD=1;

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_d_name)
    TextView tvDName;
    @BindView(R.id.lv_abnormal)
    ListView lvAbnormal;//列表
    @BindView(R.id.btn_before)
    Button btnBefore;//前一天
    @BindView(R.id.tv_date)
    TextView tvDate;//日期
    @BindView(R.id.btn_after)
    Button btnAfter;//後一天


    private String dimId;
    private String type;
    private String scId;
    private AbnormalListAdapter abnormalListAdapter;

    private String result;

    private String nowDateTime;//获取当前时间
    private String changeDate;//改變的時間,同時用於設置tvDate

    private Calendar noChangeTime = Calendar.getInstance();
    private Calendar changeDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_see_abnormal);
        ButterKnife.bind(this);
        tvTitle.setText("點檢詳情列表");

        tvDName.setText(getIntent().getStringExtra("dName"));

        tvDName.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnAfter.setVisibility(View.GONE);
        btnBefore.setVisibility(View.GONE);
        tvDate.setVisibility(View.GONE);
        lvAbnormal.setOnItemClickListener(this);



        Log.e("-----------","nowDateTime=="+nowDateTime);

        dimId = getIntent().getStringExtra("dimId");
        type = FoxContext.getInstance().getType();
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()) {
            case R.id.lv_abnormal://列表點擊事件
                if (getIntent().getStringExtra("from").equals("GCGL")){
                    Intent intent = new Intent(ComAbRouteItemListActivity.this, GTDetailActivity.class);
                    intent.putExtra("scId",comScanViewMessages.get(position).getSc_id());
                    intent.putExtra("dimId",comScanViewMessages.get(position).getDim_id());

                    startActivity(intent);
                }else{
                Intent intent = new Intent(ComAbRouteItemListActivity.this, ComAbAbnormalListActivity.class);
                intent.putExtra("dimId",comScanViewMessages.get(position).getDim_id());
                intent.putExtra("dName",comScanViewMessages.get(position).getDim_locale());
                intent.putExtra("scId",comScanViewMessages.get(position).getSc_id());
                intent.putExtra("creater",comScanViewMessages.get(position).getSc_creator_id());
                startActivity(intent);}
                break;
        }
    }


    private List<ComScanViewMessage>  comScanViewMessages;
    private String url;
    private void getRouteItemList(String type,String dimId) {
        showDialog();
        url = Constants.HTTP_WATER_SCAN_VIEW_SERVLET + "?type="+type+"&dim_id="+dimId;
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                result = HttpUtils.queryStringForPost(url);
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                String response = result;
                dismissDialog();
                if (result != null) {
                    Log.e("---------", "==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        if (array!=null) {
                            comScanViewMessages = new ArrayList<ComScanViewMessage>();
                            for (JsonElement type : array) {
                                ComScanViewMessage humi = gson.fromJson(type, ComScanViewMessage.class);
                                comScanViewMessages.add(humi);
                            }

                            Message message = new Message();
                            message.what = MESSAGE_UPLOAD;
                            mHandler.sendMessage(message);
                        }else {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = "沒有已巡檢點位.";
                            mHandler.sendMessage(message);
                        }
                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "請求不成功";
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    private void getGTProgressItemList(String type,String dimId) {
        showDialog();
        url = Constants.HTTP_YJ_LIST + "?creator_id="+type+"&no="+dimId;
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                result = HttpUtils.queryStringForPost(url);
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                String response = result;
                dismissDialog();
                if (result != null) {
                    Log.e("---------", "==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        if (array!=null) {
                            comScanViewMessages = new ArrayList<ComScanViewMessage>();
                            for (JsonElement type : array) {
                                ComScanViewMessage humi = gson.fromJson(type, ComScanViewMessage.class);
                                comScanViewMessages.add(humi);
                            }

                            Message message = new Message();
                            message.what = MESSAGE_UPLOAD;
                            mHandler.sendMessage(message);
                        }else {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = "沒有已巡檢點位.";
                            mHandler.sendMessage(message);
                        }
                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "請求不成功";
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    if (msg.obj.equals("400")) {
//                        abnormalListAdapter = new AbnormalListAdapter(ComAbRouteItemListActivity.this, comScanViewMessages,"D");
//                        lvAbnormal.setAdapter(abnormalListAdapter);
//                        ToastUtils.showLong(ComAbRouteItemListActivity.this, "此點無異常數據!");
////                        tvDate.setText(changeDate);
//                    } else {
                        ToastUtils.showShort(ComAbRouteItemListActivity.this, msg.obj.toString());
//                        tvDate.setText(nowDateTime);
//                    }
                    break;
                case MESSAGE_SEE_ABNORMAL://跳轉異常列表
//                    abnormalListAdapter = new AbnormalListAdapter(ComAbRouteItemListActivity.this, comScanViewMessages,"D");
//                    lvAbnormal.setAdapter(abnormalListAdapter);
//
//                    tvDate.setText(changeDate);
                    break;
                case MESSAGE_UPLOAD:
                    if (getIntent().getStringExtra("from").equals("GCGL")){
                        abnormalListAdapter = new AbnormalListAdapter(ComAbRouteItemListActivity.this, comScanViewMessages,"GCGL");
                        lvAbnormal.setAdapter(abnormalListAdapter);
                    }else{
                    abnormalListAdapter = new AbnormalListAdapter(ComAbRouteItemListActivity.this, comScanViewMessages,"Item");
                    lvAbnormal.setAdapter(abnormalListAdapter);}
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getStringExtra("from").equals("GCGL")){
            getGTProgressItemList( FoxContext.getInstance().getLoginId(), dimId);
        }else{
            getRouteItemList( type, dimId);
        }

    }
}
