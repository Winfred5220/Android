package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.DZFoodAbLvAdapter;
import com.example.administrator.yanfoxconn.bean.DZFoodAbList;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Song
 * on 2020/8/20
 * Description：人資監餐 異常列表界面
 *              总务临时工 异常列表界面
 */
public class DZFoodAbListActivty extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息
    private final int MESSAGE_JUMP = 4;//跳轉維護界面
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_CHECK = 6;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;//表格标题
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnUpAb;//新增
    @BindView(R.id.lv_abnormal)
    ListView listView;//異常列表
    private DZFoodAbLvAdapter lvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dz_food_ab_list);
        ButterKnife.bind(this);

        tvTitle.setText("異常列表");
        btnBack.setText("返回");
        btnUpAb.setText("新增");
        btnBack.setOnClickListener(this);
        btnUpAb.setOnClickListener(this);

        if (getIntent().getStringExtra("canbie").equals("GA")){
            llTitle.setVisibility(View.GONE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(DZFoodAbListActivty.this,GAExceDetailActivity.class);
                    intent.putExtra("exceId",lists.get(position).getExce_id());
                    startActivity(intent);
                }
            });
        }else{
            btnUpAb.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DZFoodAbListActivty.this,DZFoodAbCheckActivity.class);
                intent.putExtra("exceId",lists.get(position).getExce_id());
                intent.putExtra("canbie",getIntent().getStringExtra("canbie"));
                intent.putExtra("dimId",getIntent().getStringExtra("dimId"));
                intent.putExtra("name",getIntent().getStringExtra("name"));//餐厅名称
                startActivity(intent);
            }
        });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:
                Intent intent = new Intent(DZFoodAbListActivty.this, DZFoodAbUpActivity.class);
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("scId", getIntent().getStringExtra("scId"));
                startActivity(intent);
                break;
        }
    }

    private List<DZFoodAbList> lists;
    private void getAbList(String type, String canbie, String dimId) {
        String url = Constants.HTTP_RZ_EXCE_VIEW + "?type=" + type + "&canbie=" + canbie + "&dim_id=" + dimId;
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);
                Log.e("---------", "==fff===" + url);
                dismissDialog();
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        lists = new ArrayList<DZFoodAbList>();
                        for (JsonElement type : array) {
                            DZFoodAbList humi = gson.fromJson(type, DZFoodAbList.class);
                            lists.add(humi);
                        }
                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
                        lists = new ArrayList<DZFoodAbList>();
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                    finish();
                }
            }
        }.start();
    }

    private void getGAAbList(String loginId) {
        String url = Constants.HTTP_ZW_EXCE_VIEW + "?creator_id=" + loginId ;
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);
                Log.e("---------", "==fff===" + url);
                dismissDialog();
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        lists = new ArrayList<DZFoodAbList>();
                        for (JsonElement type : array) {
                            DZFoodAbList humi = gson.fromJson(type, DZFoodAbList.class);
                            lists.add(humi);
                        }
                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
                        lists = new ArrayList<DZFoodAbList>();
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                    finish();
                }
            }
        }.start();
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    lvAdapter = new DZFoodAbLvAdapter(DZFoodAbListActivty.this, lists,"");
                    listView.setAdapter(lvAdapter);
                    ToastUtils.showLong(DZFoodAbListActivty.this, msg.obj.toString());
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(DZFoodAbListActivty.this, R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    if (lists != null) {
                        if (getIntent().getStringExtra("canbie").equals("GA")){

                            lvAdapter = new DZFoodAbLvAdapter(DZFoodAbListActivty.this, lists,"GA");
                            listView.setAdapter(lvAdapter);
                        }else {
//                        LinearLayoutManager layoutManager = new LinearLayoutManager(ZhiyinshuiCheckActivity.this);
//                        rvOption.setLayoutManager(layoutManager);
                        lvAdapter = new DZFoodAbLvAdapter(DZFoodAbListActivty.this, lists,"");
                        listView.setAdapter(lvAdapter);}
                    } else {
                        ToastUtils.showShort(DZFoodAbListActivty.this, "沒有數據!");
                    }
                    break;
                case MESSAGE_UP://提交響應
//                    worningAlert(msg.obj.toString(), MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
                    break;
                case MESSAGE_SET_CHECK://點檢項列表賦值



                    break;

                case MESSAGE_JUMP://跳轉維護異常界面
//                    worningAlert(msg.obj.toString(), MESSAGE_JUMP);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getStringExtra("canbie").equals("GA")){
            getGAAbList(FoxContext.getInstance().getLoginId());
        }else{
        getAbList(FoxContext.getInstance().getType(), getIntent().getStringExtra("canbie"), getIntent().getStringExtra("dimId"));
    }}
}