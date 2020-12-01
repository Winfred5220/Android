package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.CarLogMessage;
import com.example.administrator.yanfoxconn.bean.CarLogReturnM;
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
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 選擇維護日誌 日期
 * Created by song on 2017/12/11.
 */

public class CarLogDayActivity extends BaseActivity implements View.OnClickListener {

    private final int MESSAGE_CAR_LOG = 1;
    private final int MESSAGE_TOAST = 2;

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_return)
    Button btnReturn;//退件
    @BindView(R.id.btn_yesterday)
    Button btnYesterday;//昨日
    @BindView(R.id.btn_today)
    Button btnToday;//今日

    private Date curDate;//當前時間
    private String dateTime;//用於傳遞的時間

    private String result;
    private List<CarLogMessage> carLogMessageList;
    private List<CarLogReturnM> carLogReturnMList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_log_day);
        ButterKnife.bind(this);

        tvTitle.setText("日期選擇");
        btnBack.setOnClickListener(this);
        btnToday.setOnClickListener(this);
        btnYesterday.setOnClickListener(this);
        btnReturn.setOnClickListener(this);

        if (getIntent().getBooleanExtra("isShow",false)){
            btnReturn.setVisibility(View.VISIBLE);
            carLogReturnMList = new ArrayList<>();
            carLogReturnMList = (List<CarLogReturnM>) getIntent().getSerializableExtra("returnList");
        }else{
            btnReturn.setVisibility(View.GONE);
        }

        curDate = new Date(System.currentTimeMillis());//获取当前时间

        btnYesterday.setText("昨日：" + getDay(curDate, -1));
        btnToday.setText("今日：" + getDay(curDate, 0));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_yesterday://昨日

                getLogList(getDay(curDate, -1));
                FoxContext.getInstance().setLogDate( "-1");
                dateTime = getDay(curDate, -1);
                FoxContext.getInstance().setLogDateTime(dateTime);
                break;
            case R.id.btn_today://今日

                FoxContext.getInstance().setLogDate("0");
                getLogList(getDay(curDate, 0));
                dateTime = getDay(curDate, 0);
                FoxContext.getInstance().setLogDateTime(dateTime);
                break;
            case R.id.btn_return://退件列表
                Intent intent = new Intent(CarLogDayActivity.this,CarLogReturnActivity.class);
                intent.putExtra("returnList", (Serializable) carLogReturnMList);
                startActivity(intent);
                break;
        }
    }

    /**
     * 獲取日期
     *
     * @param date 當前系統時間
     * @param num  0當天時間;-1昨天時間
     * @return
     */
    public static String getDay(Date date, int num) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, num);
        date = calendar.getTime();
        String str = formatter.format(date);

        return str;
    }

    public void getLogList(final String date) {
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        showDialog();
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
                        String xcId = "";
                        if (jsonObject.get("data").getAsJsonArray()!=null){
                            Log.e("--------","data   null");
                            JsonArray array = jsonObject.get("data").getAsJsonArray();
                            carLogMessageList = new ArrayList<CarLogMessage>();
                            for (JsonElement type : array) {
                            CarLogMessage humi = gson.fromJson(type, CarLogMessage.class);
                            carLogMessageList.add(humi);
                        }
                        if (carLogMessageList.size()!=0){
                              xcId = carLogMessageList.get(0).getXc_id();}
                        }
//                        changeDate = textDateTime;
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = xcId;
                        mHandler.sendMessage(message);

                    }
                } else {
                    ToastUtils.showShort(CarLogDayActivity.this, "請求不成功");
                }

            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showShort(CarLogDayActivity.this, "此時間無日誌數據!");

                    Intent intent = new Intent(CarLogDayActivity.this, CarLogUpAcE.class);
                    intent.putExtra("date", dateTime);
                    intent.putExtra("id", msg.obj.toString());
                    startActivity(intent);
                    break;

                case MESSAGE_CAR_LOG://
                    Intent intent1 = new Intent(CarLogDayActivity.this,CarLogListActivity.class);
                    intent1.putExtra("logList", (Serializable) carLogMessageList);
                    intent1.putExtra("date", dateTime);
                    startActivity(intent1);

                    break;
            }
            super.handleMessage(msg);
        }
    };
}
