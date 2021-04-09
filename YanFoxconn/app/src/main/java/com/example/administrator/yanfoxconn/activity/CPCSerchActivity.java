package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.GCPeopleAdapter;
import com.example.administrator.yanfoxconn.bean.GCHead;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.example.administrator.yanfoxconn.widget.SwipterMenuTest;
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
 * wang 2021/4/8
 * 成品倉無紙化 銷單模糊查詢界面
 */
public class CPCSerchActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_search)

    Button btnSearch;//搜索
    @BindView(R.id.et_search)
    EditText etSearch;//搜索文字s
    @BindView(R.id.lv_single)
    MyListView lvSingle;

    private List<GCHead> gcHeads;
    private GCPeopleAdapter gcPeopleAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gc_search);
        ButterKnife.bind(this);

        btnBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this::onClick);
        tvTitle.setText("銷單查詢");

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_search:
                search(etSearch.getText().toString());
                break;
        }
    }

    private void search(String id) {

        showDialog();
        final String url = Constants.HTTP_BODY_SELECT + "?id=" + id;

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
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        gcHeads = new ArrayList<GCHead>();

                        for (JsonElement type : array) {
                            GCHead humi = gson.fromJson(type, GCHead.class);
                            gcHeads.add(humi);
                        }

                        Message message = new Message();
                        message.what = Constants.MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
                        Message message = new Message();
                        message.what = Constants.MESSAGE_TOAST;
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

                case Constants.MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
                    ToastUtils.showLong(CPCSerchActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case Constants.MESSAGE_SET_TEXT://text賦值
                    gcPeopleAdapter = new GCPeopleAdapter(CPCSerchActivity.this, gcHeads);
                    lvSingle.setAdapter(gcPeopleAdapter);
                    break;
                case Constants.MESSAGE_DELETE_SUCCESS://提交響應
                    search(etSearch.getText().toString());
                    break;
                case Constants.MESSAGE_NOT_NET:
                    ToastUtils.showLong(CPCSerchActivity.this, "網絡錯誤，請稍後重試！");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 刪除單頭
     * @param roundId 單頭號
     */
    private void delPeople(String roundId) {
        showDialog();
        final String url = Constants.HTTP_HEAD_DELETE + "?In_Random_Id=" + roundId;

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
//                        JsonArray array = jsonObject.get("result").getAsJsonArray();


                        Message message = new Message();
                        message.what = Constants.MESSAGE_DELETE_SUCCESS;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
                        Message message = new Message();
                        message.what = Constants.MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
    }

    private void delAlert(String msg, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                   
                        delPeople(gcHeads.get(position).getIn_Random_Id());
                    }

                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }



}
