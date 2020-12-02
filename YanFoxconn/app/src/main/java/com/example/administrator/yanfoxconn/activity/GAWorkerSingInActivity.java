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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Song on 2020/10/29
 * description：人資臨時工簽到主界面
 */
public class GAWorkerSingInActivity extends BaseActivity implements View.OnClickListener {

    private final int MESSAGE_TOAST = 2;//showToast
    private final int MESSAGE_SET_UI = 1;//獲取狀態后,設置控件內容
    private final int MESSAGE_NOT_NET = 3;//網絡問題

    private String url;//URL地址
    private String result;//返回结果

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.ll_ga_main)
    LinearLayout llCheck;//選擇某個界面
    @BindView(R.id.btn_sign_start)
    Button btnSignStart;//簽到
    @BindView(R.id.btn_up_ab)
    Button btnUpAb;//異常上傳
    @BindView(R.id.btn_ab_done)
    Button btnAbDone;//異常整改
    @BindView(R.id.btn_sign_end)
    Button btnSignEnd;//簽退
    private Intent intent;
    private String type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dz_food);
        ButterKnife.bind(this);
        tvTitle.setText("选择操作");
        btnBack.setText("返回");
        Log.e("-----", "DZFoodCheckActivity");
        llCheck.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnSignStart.setOnClickListener(this);
        btnUpAb.setOnClickListener(this);
        btnAbDone.setOnClickListener(this);
        btnSignEnd.setOnClickListener(this);
        intent = new Intent(GAWorkerSingInActivity.this, DZFoodMainActivity.class);
        type = getIntent().getStringExtra("type");
        FoxContext.getInstance().setType(type);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_sign_start://簽到
                Intent intent = new Intent(GAWorkerSingInActivity.this, QrCodeActivity.class);
                intent.putExtra("title", "二維碼掃描");
                intent.putExtra("num", "GA");
                intent.putExtra("inOut","in");
                intent.putExtra("scId","");//掃描主鍵
                startActivity(intent);
                break;
            case R.id.btn_up_ab://上傳異常
                Intent intent2 = new Intent(GAWorkerSingInActivity.this, GAUpAbnormalActivity.class);
                intent2.putExtra("message", (Serializable) messageList);
                startActivity(intent2);
                break;
            case R.id.btn_ab_done://異常查看及整改

                FoxContext.getInstance().setTakePic("W0");
                Intent intentAb = new Intent(GAWorkerSingInActivity.this, DZFoodAbListActivty.class);
                intentAb.putExtra("canbie","GA");//餐别
                startActivity(intentAb);
                break;
            case R.id.btn_sign_end://簽退
                Intent intent1 = new Intent(GAWorkerSingInActivity.this, QrCodeActivity.class);
                intent1.putExtra("title", "二維碼掃描");
                intent1.putExtra("num", "GA");
                intent1.putExtra("inOut","out");
                intent1.putExtra("scId",messageList.get(0).getSc_id());//掃描主鍵
                startActivity(intent1);
                finish();
                break;

        }
    }


    /**
     * 获取签到状态
     * @param type      //模块类别
     * @param createrId //工号
     */
    private List<ComScanViewMessage> messageList;
    private String gName;//课组

    public void getScanStatue(String type,String createrId){
        showDialog();
        url = Constants.HTTP_ZW_SCAN_STATUS+"?type="+type+"&creator_id="+createrId;
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
                        gName=jsonObject.get("g_name").getAsString();
                        FoxContext.getInstance().setgName(gName);
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

                    ToastUtils.showShort(GAWorkerSingInActivity.this, msg.obj.toString()+",请签到！");

                    tvTitle.setText("签到");
                    btnSignEnd.setClickable(false);
                    btnSignEnd.setBackgroundColor(getResources().getColor(R.color.color_858585));
                    btnUpAb.setClickable(false);
                    btnUpAb.setBackgroundColor(getResources().getColor(R.color.color_858585));
                    btnSignStart.setClickable(true);
                    btnSignStart.setBackgroundColor(getResources().getColor(R.color.color_74afdb));
                    btnAbDone.setClickable(true);
                    btnAbDone.setBackgroundColor(getResources().getColor(R.color.color_74afdb));
                    break;
                case MESSAGE_SET_UI://有签到状态，不可签到，可签退
//                    if (messageList.get(0).getSc_lat().equals("Y")){//N已簽到未簽退，Y一簽到簽退完成一次監餐，不可重複監餐
//                        finish();
//                        ToastUtils.showLong(GAWorkerSingInActivity.this,"签到签退今天已完成，請勿重複操作！");
//                    }else{

                    btnAbDone.setClickable(true);
                    btnAbDone.setBackgroundColor(getResources().getColor(R.color.color_74afdb));
                        btnUpAb.setClickable(true);
                    btnUpAb.setBackgroundColor(getResources().getColor(R.color.color_74afdb));
                        btnSignEnd.setClickable(true);
                    btnSignEnd.setBackgroundColor(getResources().getColor(R.color.color_74afdb));
                        btnSignStart.setClickable(false);
                    btnSignStart.setBackgroundColor(getResources().getColor(R.color.color_858585));
//                    }

//                    ToastUtils.showShort(DZFoodMainActivity.this, "...");
                    break;
                case MESSAGE_NOT_NET://网络请求失败
                    ToastUtils.showLong(GAWorkerSingInActivity.this, msg.obj.toString());
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        getScanStatue( FoxContext.getInstance().getType(), FoxContext.getInstance().getLoginId());
    }
}
