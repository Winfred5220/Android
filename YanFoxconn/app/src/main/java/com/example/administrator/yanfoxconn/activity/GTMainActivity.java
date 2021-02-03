package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.GTMainBtnAdapter;
import com.example.administrator.yanfoxconn.adapter.MyGridViewAdapter;
import com.example.administrator.yanfoxconn.bean.GTMain;
import com.example.administrator.yanfoxconn.bean.GTMainBtn;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyGridView;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 營建 工程管理 主界面 和 更改時間界面共用
 * song 2021-1-18
 */
public class GTMainActivity extends BaseActivity implements View.OnClickListener , AdapterView.OnItemClickListener, TimeDatePickerDialog.TimePickerDialogInterface
{
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//showToast
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤

    private String url;//請求地址
    private String result;//返回結果

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_name)
    TextView tvName;//工程名稱
    @BindView(R.id.tv_expect_end_time)
    TextView tvExpectEndTime;//預計完成時間
    @BindView(R.id.tv_win_vendor)
    TextView tvWinVendor;//中標廠商
    @BindView(R.id.tv_supplier)
    TextView tvSupplier;//廠商負責人
    @BindView(R.id.tv_position)
    TextView tvPosition;//位置
    @BindView(R.id.tv_date)
    TextView tvDate;//時間
    @BindView(R.id.tr_date)
    TableRow trDate;//時間
    @BindView(R.id.btn_up)
    Button btnUp;//提交

    @BindView(R.id.gv_btn)
    MyGridView myGridView;//點檢按鈕

    private GTMain gtMain;//工程數據
    private List<GTMainBtn> gtMainBtns;//按鈕數據
    private GTMainBtnAdapter gtMainBtnAdapter;

    private String qrResult;//二維碼掃描結果
    private String flag;//設備還是器材S
    private String name;//工程名稱
    private String todayWork;//今日施工內容
    private int progress;//當前施工進度

    private  Calendar noChangeTime = Calendar.getInstance();
    private String nowDateTime;//获取当前时间
    private String changeDate;//改變的時間,同時用於設置tvDate
    private Calendar changeDateTime = Calendar.getInstance();
    static TimeDatePickerDialog timeDatePickerDialog;
    private static SimpleDateFormat formatter;
    private SimpleDateFormat formattery = new SimpleDateFormat("yyyy");
    private SimpleDateFormat formatterm = new SimpleDateFormat("MM");
    private SimpleDateFormat formatterd = new SimpleDateFormat("dd");
    private static int mYear, mMonth, mDay;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gt_main);
        ButterKnife.bind(this);

        tvTitle.setText("工程點檢");
        btnBack.setText("返回");

        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        qrResult = getIntent().getStringExtra("result").split(";")[0];
        flag= getIntent().getStringExtra("flag");

        if (getIntent().getStringExtra("from").equals("qr")){
            getBtnMessage();
        }else{
            gtMain= (GTMain) getIntent().getSerializableExtra("message");
            myGridView.setVisibility(View.GONE);
            btnUp.setVisibility(View.VISIBLE);
            trDate.setVisibility(View.VISIBLE);
            tvName.setText(gtMain.getProject_name());
            tvExpectEndTime.setText(gtMain.getExpect_enddate());
            tvWinVendor.setText(gtMain.getWin_vendor());
            tvSupplier.setText(gtMain.getSupplier());
            tvPosition.setText(gtMain.getBuilding());
        }

        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (gtMainBtns.get(position).getFlag().equals("N")){

                }else{
                    FoxContext.getInstance().setType(gtMainBtns.get(position).getCtype());
                    Intent intent = new Intent(GTMainActivity.this,GTAbUpActivity.class);
                    intent.putExtra("flag",flag);
                    intent.putExtra("dimId",qrResult);
                    intent.putExtra("name",name);
                    intent.putExtra("progress",progress);
                    intent.putExtra("todayWork",todayWork);
                    startActivity(intent);
                }
            }
        });
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        noChangeTime = Calendar.getInstance();
        mYear = Integer.parseInt(formattery.format(noChangeTime.getTime()));
        mMonth = Integer.parseInt(formatterm.format(noChangeTime.getTime()));
        mDay = Integer.parseInt(formatterd.format(noChangeTime.getTime()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_up://提交
                setNewDate(qrResult,tvDate.getText().toString());
                break;
            case R.id.tv_date://時間控件
                timeDatePickerDialog = new TimeDatePickerDialog(GTMainActivity.this,mYear, mMonth-1, mDay);
                timeDatePickerDialog.showDatePickerDialog();
                break;
        }
    }

    /**
     * 設置新的日期
     * @param no    工程案號
     * @param date  新的日期
     */
    private void setNewDate(String no,String date){
        showDialog();
        final String url = Constants.HTTP_YJ_UPDATE_BY_NO + "?nextwork_date=" + date + "&no=" + no ;
        Log.e("-------fff--", "url==" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForGet(url);
                dismissDialog();
                Gson gson = new Gson();

                if (result != null) {
                    Log.e("--fff---result----", result.toString());
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();

                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = jsonObject.get("errMessage").getAsString();
                    mHandler.sendMessage(message);

                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "請求不成功";
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }
    /**
     * h獲取點檢類別
//     * @param no    工程案號
//     * @param creator_id  點檢人工號
     */

    private void getBtnMessage(){
        showDialog();
        url = Constants.HTTP_YJ_SCAN + "?no=" + qrResult + "&creator_id=" + FoxContext.getInstance().getLoginId();
        Log.e("-----", "fff--" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Gson gson = new Gson();
                Log.e("---fff--result----", result.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonObject jsonObject1 = jsonObject.get("data").getAsJsonObject();

//                        gtMain = new ArrayList<>();
//                        for (JsonElement type:array1){
//                            GTMain humi = gson.fromJson(type,GTMain.class);
//                            gtMain.add(humi);
//                        }
                        JsonArray array2 = jsonObject1.get("menuList").getAsJsonArray();
                        gtMainBtns = new ArrayList<>();
                        for (JsonElement type:array2){
                            GTMainBtn humi = gson.fromJson(type,GTMainBtn.class);
                            gtMainBtns.add(humi);
                        }



                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject1.get("proInfo").getAsJsonObject();
                        mHandler.sendMessage(message);
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
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(GTMainActivity.this, R.string.net_mistake);
                    break;
                case MESSAGE_TOAST:
                    ToastUtils.showShort(GTMainActivity.this, msg.obj.toString());
                    finish();
                    break;
                case MESSAGE_SET_TEXT:
                    JsonObject proInfo= (JsonObject) msg.obj;
                    tvName.setText(proInfo.get("project_name").getAsString());
                    name = proInfo.get("project_name").getAsString();
                    tvExpectEndTime.setText(proInfo.get("expect_enddate").getAsString());
                    tvWinVendor.setText(proInfo.get("win_vendor").getAsString());
                    tvSupplier.setText(proInfo.get("supplier").getAsString());
                    tvPosition.setText(proInfo.get("building").getAsString());
                     progress = proInfo.get("progress").getAsInt();
                   todayWork = proInfo.get("today_work").getAsString();

                    gtMainBtnAdapter = new GTMainBtnAdapter(GTMainActivity.this,gtMainBtns);
                    myGridView.setAdapter(gtMainBtnAdapter);
                    break;
            }
            super.handleMessage(msg);
        }};


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

    }
    //时间选择器----------确定
    public void positiveListener() {
        try {
            changeDateTime.setTime(formatter.parse(timeDatePickerDialog.getTimeDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mYear = timeDatePickerDialog.getYear();
        mDay = timeDatePickerDialog.getDay();
        mMonth = timeDatePickerDialog.getMonth();
        tvDate.setText(timeDatePickerDialog.getDate());
    }

    //时间选择器-------取消
    public  void negativeListener() {
        if (timeDatePickerDialog.getTimeDate().equals("")) {
            ToastUtils.showShort(GTMainActivity.this, "請選擇時間");
        }
    }
}
