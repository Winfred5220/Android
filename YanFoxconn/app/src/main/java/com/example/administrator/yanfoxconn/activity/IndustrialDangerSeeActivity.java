package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.IndustrialDangerListAdapter;
import com.example.administrator.yanfoxconn.bean.DangerListMessage;
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

public class IndustrialDangerSeeActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_UPLOAD = 1;
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_DILOG = 3;
    private final int MESSAGE_SET_TEXT = 4;//掃描成功賦值
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.lv_danger_list)
    MyListView lvDanger;//隱患列表

    private List<DangerListMessage> dangerListMessage;
    private IndustrialDangerListAdapter industrialDangerListAdapter;
    private String url;//請求地址
    private String result;//二維碼返回結果
    private String caseId;//專案ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industrial_danger_see);
        ButterKnife.bind(this);

        tvTitle.setText("工安巡檢");
        btnBack.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(this);
        caseId = getIntent().getStringExtra("caseId");

        lvDanger.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) {
                aboutAlert("確定要刪除嗎？",i);
                return false;
            }
        });
        lvDanger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    //界面按鈕 點擊事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.ib_title_right:

                break;
        }
    }

    /**
     * 隱患信息列表獲取
     */
    public void getDangerList(String caseId) {
        showDialog();
        url = Constants.HTTP_INDUSSAFE_DANGER_VIEW_SERVLET + "?caseId=" + caseId;
        Log.e("---------url--------", url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Gson gson = new Gson();
                dismissDialog();
                if (result != null) {
                    Log.e("---------result--------", result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    JsonArray array = jsonObject.get("data").getAsJsonArray();
                    if (array.size() != 0) {
                        dangerListMessage = new ArrayList<DangerListMessage>();
                        for (JsonElement type : array) {
                            DangerListMessage humi = gson.fromJson(type, DangerListMessage.class);
                            dangerListMessage.add(humi);
                        }
                        Message message = new Message();
                        message.what = MESSAGE_UPLOAD;
                        mHandler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_DILOG;
                        mHandler.sendMessage(message);
                    }

                } else {
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                }
            }
        }.start();

    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
                    break;
                case MESSAGE_UPLOAD:
                    industrialDangerListAdapter = new IndustrialDangerListAdapter(IndustrialDangerSeeActivity.this, dangerListMessage);
                    lvDanger.setAdapter(industrialDangerListAdapter);

                    break;
                case MESSAGE_DILOG:
                    ToastUtils.showShort(IndustrialDangerSeeActivity.this, "沒有數據!");
                    finish();
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(IndustrialDangerSeeActivity.this, R.string.net_mistake);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     *隱患刪除
     */
    public void deleteDangerList(String id) {
        showDialog();

        url = Constants.HTTP_INDUSSAFE_DANGER_DELETE_SERVLET+"?id="+id;
        Log.e("---------url--------", url );
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                Log.e("---------result--------", result );
                dismissDialog();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else{
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

    private void aboutAlert(String msg, final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        deleteDangerList(dangerListMessage.get(i).getId());
//                        ToastUtils.showShort(DutyChiefProblemSeeActivity.this,i+"");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
//                                deleteProblemList();
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
                            getDangerList(caseId);
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDangerList(caseId);
    }

}
