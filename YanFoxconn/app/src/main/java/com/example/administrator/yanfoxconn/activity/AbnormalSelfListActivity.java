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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.AbnormalListAdapter;
import com.example.administrator.yanfoxconn.bean.AbnormalMessage;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 個人異常查看 列表界面
 * Created by song on 2017/9/13.
 */

public class AbnormalSelfListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_SEE_ABNORMAL = 3;

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.lv_abnormal)
    ListView lvAbnormal;//列表
    @BindView(R.id.rl_time)
    RelativeLayout rltime;

    private String url;//請求地址
    private String flag;
    private String scId;
    private List<AbnormalMessage> abnormalList;
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
        tvTitle.setText("異常列表");

        rltime.setVisibility(View.GONE);

        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        lvAbnormal.setOnItemClickListener(this);

        noChangeTime = Calendar.getInstance();
        SimpleDateFormat sdfAfter = new SimpleDateFormat("yyyy-MM-dd");
        nowDateTime = sdfAfter.format(noChangeTime.getTime());
        Log.e("-----------","nowDateTime=="+nowDateTime);

        flag = getIntent().getStringExtra("flag");
        scId = getIntent().getStringExtra("id");
        abnormalList = (List<AbnormalMessage>) getIntent().getSerializableExtra("abnormalList");


        if (abnormalList.size() == 0) {
            Message message = new Message();
            message.what = MESSAGE_TOAST;
            message.obj = "沒有數據";
            mHandler.sendMessage(message);
        }

        abnormalListAdapter = new AbnormalListAdapter(AbnormalSelfListActivity.this, abnormalList);
        lvAbnormal.setAdapter(abnormalListAdapter);

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
                Intent intent = new Intent(AbnormalSelfListActivity.this, AbnormalDetailsActivity.class);
                intent.putExtra("exceId", abnormalList.get(position).getExce_id());
                intent.putExtra("type",true);
                startActivity(intent);
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("========","onResume");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        updateList();
        Log.e("========","onRestart");
    }


    private void updateList(){//刷新列表
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        url = Constants.HTTP_EXCEPTION_VIEW_SERVLET + "?flag=" + flag + "&id=" + FoxContext.getInstance().getType() +
                "&time=" + nowDateTime+"&login_id="+FoxContext.getInstance().getLoginId();
        Log.e("---------", "==fff===" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Gson gson = new Gson();
                Log.e("--fff---result----", result.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        abnormalList = new ArrayList<AbnormalMessage>();
                        for (JsonElement type : array) {
                            AbnormalMessage humi = gson.fromJson(type, AbnormalMessage.class);
                            abnormalList.add(humi);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SEE_ABNORMAL;
                        message.obj = abnormalList;
                        mHandler.sendMessage(message);

                    } else if (errCode.equals("400")) {

                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = "400";
                        mHandler.sendMessage(message);

                        abnormalList.clear();
                    }
                }

            }
        }.start();
    }



    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    if (msg.obj.equals("400")) {
                        abnormalListAdapter = new AbnormalListAdapter(AbnormalSelfListActivity.this, abnormalList);
                        lvAbnormal.setAdapter(abnormalListAdapter);
                        ToastUtils.showShort(AbnormalSelfListActivity.this, "此點無異常數據!");

                    } else {
                        ToastUtils.showShort(AbnormalSelfListActivity.this, msg.obj.toString());

                    }
                    break;
                case MESSAGE_SEE_ABNORMAL://跳轉異常列表
                    abnormalListAdapter = new AbnormalListAdapter(AbnormalSelfListActivity.this, abnormalList);
                    lvAbnormal.setAdapter(abnormalListAdapter);

                    break;
            }
            super.handleMessage(msg);
        }
    };
}
