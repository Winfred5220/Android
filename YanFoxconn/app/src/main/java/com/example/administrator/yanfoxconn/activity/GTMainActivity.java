package com.example.administrator.yanfoxconn.activity;

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
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GTMainActivity extends BaseActivity implements View.OnClickListener {
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

    @BindView(R.id.gv_btn)
    MyGridView myGridView;//點檢按鈕

    private List<GTMain> gtMain;//工程數據
    private List<GTMainBtn> gtMainBtns;//按鈕數據
    private GTMainBtnAdapter gtMainBtnAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gt_main);
        ButterKnife.bind(this);

        tvTitle.setText("工程點檢");
        btnBack.setText("返回");

        btnBack.setOnClickListener(this);

        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            }
        });
        getBtnMessage();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left://返回
                finish();
                break;
        }
    }

    /**
     * h獲取點檢類別
//     * @param no    工程案號
//     * @param creator_id  點檢人工號
     */
//    private void getBtnMessage(String no,String creator_id){
    private void getBtnMessage(){
        showDialog();
        url = Constants.HTTP_YJ_SCAN + "?no=" + "YT18105067" + "&creator_id=" + FoxContext.getInstance().getLoginId();
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
                    tvExpectEndTime.setText(proInfo.get("expect_enddate").getAsString());
                    tvWinVendor.setText(proInfo.get("win_vendor").getAsString());
                    tvSupplier.setText(proInfo.get("supplier").getAsString());
                    tvPosition.setText(proInfo.get("building").getAsString());

                    gtMainBtnAdapter = new GTMainBtnAdapter(GTMainActivity.this,gtMainBtns);
                    myGridView.setAdapter(gtMainBtnAdapter);
                    break;
            }
            super.handleMessage(msg);
        }};

    public Bitmap returnBitMap(String url){
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
