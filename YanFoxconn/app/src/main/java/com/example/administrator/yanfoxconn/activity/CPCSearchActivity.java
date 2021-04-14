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
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CPCSearchAdapter;
import com.example.administrator.yanfoxconn.adapter.GCPeopleAdapter;
import com.example.administrator.yanfoxconn.bean.CPCMessage;
import com.example.administrator.yanfoxconn.bean.GCHead;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * wang 2021/4/8
 * 成品倉無紙化 銷單模糊查詢界面
 */
public class CPCSearchActivity extends BaseActivity implements View.OnClickListener {
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

    private List<CPCMessage> cpcHead;
    private CPCSearchAdapter cpcSearchAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpc_search);
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
        final String url = Constants.HTTP_CPC_SEARCH_SERVLET + "?id=" + id;

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
                        cpcHead = new ArrayList<CPCMessage>();

                        for (JsonElement type : array) {
                            CPCMessage humi = gson.fromJson(type, CPCMessage.class);
                            cpcHead.add(humi);
                        }

                        Message message = new Message();
                        message.what = Constants.MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
                        cpcHead = new ArrayList<CPCMessage>();
                        Message message = new Message();
                        message.what = Constants.MESSAGE_SET_TEXT;
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
                    ToastUtils.showLong(CPCSearchActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case Constants.MESSAGE_SET_TEXT://text賦值
                    cpcSearchAdapter = new CPCSearchAdapter(CPCSearchActivity.this, cpcHead);
                    lvSingle.setAdapter(cpcSearchAdapter);
                    lvSingle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(CPCSearchActivity.this,CPCReleaseActivity.class);
                            intent.putExtra("ex_no",cpcHead.get(position).getEx_no());
                            startActivity(intent);
//                            lvSingle.setAdapter(null);
                        }
                    });

                    break;
                case Constants.MESSAGE_DELETE_SUCCESS://提交響應
                    search(etSearch.getText().toString());
                    break;
                case Constants.MESSAGE_NOT_NET:
                    ToastUtils.showLong(CPCSearchActivity.this, "網絡錯誤，請稍後重試！");
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
                   
                        delPeople(cpcHead.get(position).getEx_no());
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
        search(etSearch.getText().toString());
    }


}
