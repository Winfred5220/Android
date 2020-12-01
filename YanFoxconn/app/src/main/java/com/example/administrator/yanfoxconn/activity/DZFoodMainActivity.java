package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.ComScanViewMessage;
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
 * on 2020/8/14
 * Description：人資監餐 主界面
 */
public class DZFoodMainActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;//showToast
    private final int MESSAGE_SET_UI = 1;//獲取狀態后,設置控件內容
    private final int MESSAGE_NOT_NET = 3;//網絡問題

    @BindView(R.id.btn_title_left)
     Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.ll_dz_main)
     LinearLayout llMain;//監餐主界面
    @BindView(R.id.btn_start)
     Button btnStart;//簽到
    @BindView(R.id.btn_up_food)
     Button btnFood;//菜品信息上傳
    @BindView(R.id.btn_up_abnormal)
     Button btnUpAb;//異常問題上傳
    @BindView(R.id.btn_end)
     Button btnEnd;//簽退

    private List<ComScanViewMessage> messageList;//获取签到信息
    private String flag;//餐別
    private String url;//URL地址
    private String result;//返回结果
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dz_food);
        ButterKnife.bind(this);
        tvTitle.setText("餐廳");
        btnBack.setText("返回");
        Log.e("---------------","vDZFoodMainActivity");
        llMain.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnFood.setOnClickListener(this);
        btnUpAb.setOnClickListener(this);
        btnEnd.setOnClickListener(this);

        flag = getIntent().getStringExtra("flag");
//        getStatu(flag, FoxContext.getInstance().getType(),FoxContext.getInstance().getLoginId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_start://簽到
                Intent intent = new Intent(DZFoodMainActivity.this, QrCodeActivity.class);
                intent.putExtra("title", "二維碼掃描");
                intent.putExtra("num", "DZ");
                intent.putExtra("flag",flag);//餐別
                intent.putExtra("inOut","in");
                intent.putExtra("scId","");//掃描主鍵
                startActivity(intent);
                break;
            case R.id.btn_up_food://菜品信息上傳
                Intent intentPho = new Intent(DZFoodMainActivity.this, DZFoodPhotoUpActivity.class);
                intentPho.putExtra("scId",messageList.get(0).getSc_id());//掃描主鍵
                intentPho.putExtra("name",messageList.get(0).getDim_locale());//餐廳名稱
                startActivity(intentPho);
                break;
            case R.id.btn_up_abnormal://異常問題上傳
                Intent intentAb = new Intent(DZFoodMainActivity.this, DZFoodAbListActivty.class);
                intentAb.putExtra("scId",messageList.get(0).getSc_id());//掃描主鍵
                intentAb.putExtra("canbie",flag);//餐别
                intentAb.putExtra("name",messageList.get(0).getDim_locale());//餐厅名称
                intentAb.putExtra("dimId",messageList.get(0).getDim_id());//dimId
                startActivity(intentAb);
                break;
            case R.id.btn_end://簽退
                Intent intent1 = new Intent(DZFoodMainActivity.this, QrCodeActivity.class);
                intent1.putExtra("title", "二維碼掃描");
                intent1.putExtra("num", "DZ");
                intent1.putExtra("flag",flag);//餐別
                intent1.putExtra("inOut","out");
                intent1.putExtra("scId",messageList.get(0).getSc_id());//掃描主鍵
                startActivity(intent1);
                finish();
                break;
        }
    }

    /**
     * 狀態查詢
     * @param flag    餐別
     * @param type    權限
     * @param userId  用戶賬號
     */
    private void getStatu(String flag,String type,String userId) {
        showDialog();
        url = Constants.HTTP_RZ_SCAN_STATUS+"?canbie="+flag+"&creator_id="+userId+"&type="+type;
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "=getcarLog=fff===" + url);
                Gson gson = new Gson();
                String response = result;

                if (result != null) {
                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        messageList = new ArrayList<ComScanViewMessage>();
                        for (JsonElement type : array) {
                            ComScanViewMessage humi = gson.fromJson(type, ComScanViewMessage.class);
                            messageList.add(humi);
                        }
                        Message message = new Message();
                        message.what =MESSAGE_SET_UI;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    } else {
//                        isShowReturn = false;
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_NOT_NET;
                    message.obj = "網絡問題請求失敗,請重試!";
                    mHandler.sendMessage(message);
                }
                dismissDialog();
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://没有签到状态，可签到，不可签退

                        ToastUtils.showShort(DZFoodMainActivity.this, msg.obj.toString());

                    tvTitle.setText("餐廳");
                    btnFood.setClickable(false);
                    btnUpAb.setClickable(false);
                    btnStart.setClickable(true);
                    btnStart.setBackgroundColor(getResources().getColor(R.color.color_74afdb));
                    btnEnd.setClickable(false);
                    btnEnd.setBackgroundColor(getResources().getColor(R.color.color_858585));
                    break;
                case MESSAGE_SET_UI://有签到状态，不可签到，可签退
                    if (messageList.get(0).getSc_lat().equals("Y")){//N已簽到未簽退，Y一簽到簽退完成一次監餐，不可重複監餐
                        finish();
                        ToastUtils.showLong(DZFoodMainActivity.this,"該餐今天已經監餐完成，請勿重複操作！");
                    }else{
                        if (messageList.get(0).getSc_lng().equals("B")){
                            tvTitle.setText("早餐-"+messageList.get(0).getDim_locale());
                        }else if (messageList.get(0).getSc_lng().equals("L")){
                            tvTitle.setText("午餐-"+messageList.get(0).getDim_locale());
                        } else if (messageList.get(0).getSc_lng().equals("D")){
                            tvTitle.setText("晚餐-"+messageList.get(0).getDim_locale());
                        }else if (messageList.get(0).getSc_lng().equals("S")){
                            tvTitle.setText("宵夜-"+messageList.get(0).getDim_locale());
                        }

                        btnFood.setClickable(true);
                        btnUpAb.setClickable(true);
                        btnEnd.setClickable(true);
                        btnEnd.setBackgroundColor(getResources().getColor(R.color.color_74afdb));
                        btnStart.setClickable(false);
                        btnStart.setBackgroundColor(getResources().getColor(R.color.color_858585));
                    }

//                    ToastUtils.showShort(DZFoodMainActivity.this, "...");
                    break;
                case MESSAGE_NOT_NET://网络请求失败
                    ToastUtils.showLong(DZFoodMainActivity.this, msg.obj.toString());
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        getStatu(flag, FoxContext.getInstance().getType(),FoxContext.getInstance().getLoginId());
    }
}
