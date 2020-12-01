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
import com.example.administrator.yanfoxconn.adapter.CarLogListAdapter;
import com.example.administrator.yanfoxconn.bean.CarLogMessage;
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

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 行車日誌列表
 * Created by song on 2017/12/12.
 */

public class CarLogListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final int MESSAGE_CAR_LOG = 1;
    private final int MESSAGE_TOAST = 2;

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_add)
    Button btnAdd;//添加日誌 按鈕
    @BindView(R.id.lv_log)
    ListView lvLog;

    private List<CarLogMessage> carLogMessageList;
    private CarLogListAdapter carLogListAdapter;
    private String date;

    private String result;
//    private List<CarLogMessage> carLogMessageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_log_list);
        ButterKnife.bind(this);

        tvTitle.setText("日誌列表");

        btnBack.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        lvLog.setOnItemClickListener(this);

        carLogMessageList = new ArrayList<>();
        carLogMessageList = (List<CarLogMessage>) this.getIntent().getSerializableExtra("logList");
        date = this.getIntent().getStringExtra("date");

        if (carLogMessageList.size() == 0) {
            Message message = new Message();
            message.what = MESSAGE_TOAST;
            message.obj = "沒有數據";
            mHandler.sendMessage(message);
        }

        carLogListAdapter = new CarLogListAdapter(this, carLogMessageList,false);
        lvLog.setAdapter(carLogListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()) {
            case R.id.lv_log://列表點擊事件

                Intent intent = new Intent(CarLogListActivity.this, CarLogDetailsActivity.class);
                intent.putExtra("xcId",carLogMessageList.get(position).getXc_id());
                intent.putExtra("xcItem",carLogMessageList.get(position).getXc_item());

                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_add://添加
                Intent intent = new Intent(CarLogListActivity.this, CarLogUpAcE.class);
                intent.putExtra("date", FoxContext.getInstance().getLogDateTime());
                intent.putExtra("id", carLogMessageList.get(0).getXc_id());
                startActivity(intent);
                break;
        }
    }

    public void getLogList(final String date) {
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
//        showDialog();
        final String url = Constants.HTTP_CAR_RECORD_SELECT + "?xc_date=" + date + "&car_driver_id=" + FoxContext.getInstance().getLoginId();
        Log.e("-------fff--", "url==" + url);
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
                        carLogMessageList = new ArrayList<CarLogMessage>();
                        for (JsonElement type : array) {
                            CarLogMessage humi = gson.fromJson(type, CarLogMessage.class);
                            carLogMessageList.add(humi);
                        }
//                        Log.e("-----------routelist", "abnormalMessageList==" + abnormalList.size());

//                        changeDate = textDateTime;
                        Message message = new Message();
                        message.what = MESSAGE_CAR_LOG;
                        message.obj = carLogMessageList;
                        mHandler.sendMessage(message);

                    } else if (errCode.equals("400")) {

//                        changeDate = textDateTime;
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = date;
                        mHandler.sendMessage(message);

                        carLogMessageList.clear();
                    }
                } else {
                    ToastUtils.showShort(CarLogListActivity.this, "請求不成功");
                }

            }
        }.start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showShort(CarLogListActivity.this, "此時間無日誌數據!");
                    carLogListAdapter = new CarLogListAdapter(CarLogListActivity.this, carLogMessageList,false);
                    lvLog.setAdapter(carLogListAdapter);
                    finish();
//                    Intent intent = new Intent(CarLogListActivity.this, CarLogUpActivity.class);
//                    intent.putExtra("date", msg.obj.toString());
//                    intent.putExtra("id", "");
//                    startActivity(intent);
                    break;

                case MESSAGE_CAR_LOG://

                    carLogListAdapter = new CarLogListAdapter(CarLogListActivity.this, carLogMessageList,false);
                    lvLog.setAdapter(carLogListAdapter);

                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("----------","onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("----------","onRestart");
        getLogList(FoxContext.getInstance().getLogDateTime());
    }
}
