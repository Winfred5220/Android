package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.EventListAdapter;
import com.example.administrator.yanfoxconn.bean.EventMessage;
import com.example.administrator.yanfoxconn.bean.EventTime;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.SwipeListViewThree;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 活動列表 界面
 * Created by song on 2018/6/26.
 */

public class EventListActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_OK = 1;
    private final int MESSAGE_UPLOAD = 3;
    private final int MESSAGE_NULL = 0;

    @BindView(R.id.tv_title)
     TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
     Button btnBack;//返回
    @BindView(R.id.btn_title_right)
     Button btnBuild;//生成
    @BindView(R.id.lv_event_list)
    SwipeListViewThree swipeListView;//列表

    private List<EventMessage> eventMessageList;
    private EventListAdapter adapter;
    private List<EventTime> eventTimeList;
    private String url;//
    private String result;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        ButterKnife.bind(this);
        tvTitle.setText("活動列表");
//        createEvent();

        btnBuild.setVisibility(View.VISIBLE);
        btnBuild.setText("生成");
        btnBack.setOnClickListener(this);
        btnBuild.setOnClickListener(this);


        eventMessageList = (List<EventMessage>) getIntent().getSerializableExtra("eventList");
        if (eventMessageList != null) {
            adapter = new EventListAdapter(EventListActivity.this, eventMessageList);
            swipeListView.setAdapter(adapter);
            clickGoorup();

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_title_right://生成活動

                Intent intent = new Intent(EventListActivity.this,EventBuildActivity.class);
                startActivity(intent);
                break;
        }
    }

    //listview 側滑點擊事件
    public void clickGoorup() {
        adapter.setOnClickListenerCancelOrShow(new EventListAdapter.OnClickListenerCancelOrShow() {
            @Override//取消
            public void OnClickListenerCancel(int position) {
                aboutAlert(eventMessageList.get(position).getDim_id());
//                cancelEvent(eventMessageList.get(position).getDim_id());
            }

            @Override//查看
            public void OnClickListenerShow(int position) {
                Intent intent = new Intent(EventListActivity.this,EventShowActivity.class);
                intent.putExtra("eventName",eventMessageList.get(position).getDim_locale());
                intent.putExtra("dimId",eventMessageList.get(position).getDim_id());
                startActivity(intent);
            }

            @Override//計時
            public void OnClickListenerTime(int position) {
                getTimeList(eventMessageList.get(position).getDim_id());

            }

        });
    }

    //獲取加班計時列表
    public void getTimeList(String dimId){
        Log.e("---------", "isVersionUpdate===");
        showDialog();
        final String eventCreator = Constants.HTTP_BARCODE_JBVIEW_SERVLET + "?dim_id="+ dimId;

        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(eventCreator);
                Log.e("---------", "fffff=url==" + eventCreator.toString());
                dismissDialog();
                Gson gson = new Gson();
                Log.e("-----i----", "isVersionUpdate");
                if (result != null) {
                    Log.e("---------", "fffff===" + result.toString());
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        eventTimeList = new ArrayList<EventTime>();
                        for (JsonElement type : array) {
                            EventTime humi = gson.fromJson(type, EventTime.class);
                            eventTimeList.add(humi);
                        }
//                        getVersionCode = Integer.parseInt(eventMessageList.get(0).getId());

//                    Message message = new Message();
//                    message.what = MESSAGE_OK;
//                    message.obj = result;
//                    mHandler.sendMessage(message);
                    Intent intent = new Intent(EventListActivity.this,EventTimeLvActivity.class);
                    intent.putExtra("list",(Serializable)eventTimeList);
                    startActivity(intent);
//                    }else {
//                        Message message = new Message();
//                        message.what = MESSAGE_TOAST;
//                        message.obj = jsonObject.get("errMessage").getAsString();
//                        mHandler.sendMessage(message);
                    }else{
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
    }

    //取消活動
    public void cancelEvent(String dimId){
        Log.e("---------", "isVersionUpdate===");
        showDialog();
        final String eventCreator = Constants.HTTP_BARCODE_MOIDY_SERVLET + "?dim_id="+ dimId;

        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(eventCreator);
                Log.e("---------", "fffff=url==" + eventCreator.toString());
                dismissDialog();
                Gson gson = new Gson();
                Log.e("-----i----", "isVersionUpdate");
                if (result != null) {
                    Log.e("---------", "fffff===" + result.toString());
//                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
//                    String errCode = jsonObject.get("errCode").getAsString();
//                    if (errCode.equals("200")) {
//                        JsonArray array = jsonObject.get("data").getAsJsonArray();
//                        eventMessageList = new ArrayList<EventMessage>();
//                        for (JsonElement type : array) {
//                            EventMessage humi = gson.fromJson(type, EventMessage.class);
//                            eventMessageList.add(humi);
//                        }
//                        getVersionCode = Integer.parseInt(eventMessageList.get(0).getId());

                        Message message = new Message();
                        message.what = MESSAGE_OK;
                    message.obj = result;
                        mHandler.sendMessage(message);
//                    }else {
//                        Message message = new Message();
//                        message.what = MESSAGE_TOAST;
//                        message.obj = jsonObject.get("errMessage").getAsString();
//                        mHandler.sendMessage(message);
//                    }
                }
            }
        }.start();
    }

    /*
    *更新活動列表
     */
    public void uploadEventList(){
        Log.e("---------", "isVersionUpdate===");
        showDialog();
        final String eventCreator = Constants.HTTP_BARCODE_VIEW_SERVLET + "?dim_type="+ FoxContext.getInstance().getType();
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(eventCreator);
                Log.e("---------", "fffff=url==" + eventCreator.toString());
                dismissDialog();
                Gson gson = new Gson();
                Log.e("-----i----", "isVersionUpdate");
                if (result != null) {
                    Log.e("---------", "fffff===" + result.toString());
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        eventMessageList = new ArrayList<EventMessage>();
                        for (JsonElement type : array) {
                            EventMessage humi = gson.fromJson(type, EventMessage.class);
                            eventMessageList.add(humi);
                        }
//                        getVersionCode = Integer.parseInt(eventMessageList.get(0).getId());

                        Message message = new Message();
                        message.what = MESSAGE_UPLOAD;
                        mHandler.sendMessage(message);
                    }else {
                        Message message = new Message();
                        message.what = MESSAGE_NULL;
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
                    ToastUtils.showShort(EventListActivity.this, msg.obj.toString());

                    break;
                case MESSAGE_OK://是否更新的彈出框
                uploadEventList();
                    ToastUtils.showShort(EventListActivity.this, msg.obj.toString());
                    break;
                case MESSAGE_NULL://是否更新的彈出框
                    eventMessageList.clear();
                    adapter = new EventListAdapter(EventListActivity.this, eventMessageList);
                    swipeListView.setAdapter(adapter);
                    clickGoorup();

                    ToastUtils.showShort(EventListActivity.this, msg.obj.toString());
                    break;
                case MESSAGE_UPLOAD:
                    adapter = new EventListAdapter(EventListActivity.this, eventMessageList);
                    swipeListView.setAdapter(adapter);
                    clickGoorup();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void aboutAlert(final String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage("确认取消此次活动!")
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        cancelEvent(msg);

                    }
                }).setNegativeButton("取消", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("---------", "onStart");
        uploadEventList();
    }

}
