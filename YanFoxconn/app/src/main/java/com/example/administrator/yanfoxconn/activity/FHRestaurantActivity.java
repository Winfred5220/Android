package com.example.administrator.yanfoxconn.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.ZhiyinshuiCheckAdapter;
import com.example.administrator.yanfoxconn.bean.FHMessage;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiCheckMsg;
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
 * 總務餐廳巡檢 點檢分類主界面
 * song 2020/09/25
 */
public class FHRestaurantActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息
    private final int MESSAGE_JUMP = 4;//跳轉維護界面
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_CHECK = 6;
    private final int MESSAGE_GET_DIM_LOCAL = 0;//獲取dimLocal

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnCheck;//複查
    
    @BindView(R.id.btn_flue)
    Button btnFlue;//煙道安全
    @BindView(R.id.btn_electrical)
    Button btnElectrical;//用电安全
    @BindView(R.id.btn_fire)
    Button btnFire;//消防安全
    @BindView(R.id.btn_food)
    Button btnFood;//食品安全
    @BindView(R.id.btn_health)
    Button btnHealth;//卫生安全
    @BindView(R.id.btn_give)
    Button btnGive;//供餐安全
    @BindView(R.id.btn_food_check)
    Button btnFoodCheck;//食材抽查

    private String url;//請求地址
    private String result;//請求返回結果
    private String flag;//D,點位巡檢
    private String[] qrResult;//二維碼內含結果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fh_restaurant);
        ButterKnife.bind(this);
        qrResult = getIntent().getStringExtra("result").split(",");
        flag = getIntent().getStringExtra("flag");

        tvTitle.setText("餐廳巡檢");
        btnBack.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
        btnCheck.setVisibility(View.VISIBLE);
        btnCheck.setText("複查");
        btnFood.setOnClickListener(this);
        btnFoodCheck.setOnClickListener(this);
        btnHealth.setOnClickListener(this);
        btnGive.setOnClickListener(this);
        btnFire.setOnClickListener(this);
        btnElectrical.setOnClickListener(this);
        btnFlue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_title_right://複查
                Intent intent0 = new Intent(FHRestaurantActivity.this, ZhiyinshuiExceListActivity.class);
                intent0.putExtra("dim_id",qrResult[1]);
                intent0.putExtra("type", "ZWCT");
                startActivity(intent0);
                break;
            case R.id.btn_flue://煙道安全
                FoxContext.getInstance().setType("FH");
                Intent intent = new Intent(FHRestaurantActivity.this, ComAbnormalUpActivity.class);
                intent.putExtra("flag", flag);
                intent.putExtra("result", getIntent().getStringExtra("result"));
                intent.putExtra("relType","FH");
                startActivity(intent);
                break;
            case R.id.btn_electrical://用电安全
                FoxContext.getInstance().setType("FI");
                Intent intent1 = new Intent(FHRestaurantActivity.this, ComAbnormalUpActivity.class);
                intent1.putExtra("flag", flag);
                intent1.putExtra("result", getIntent().getStringExtra("result"));
                intent1.putExtra("relType","FI");
                startActivity(intent1);
                break;
            case R.id.btn_fire://消防安全
                FoxContext.getInstance().setType("FJ");
                Intent intent2 = new Intent(FHRestaurantActivity.this, ComAbnormalUpActivity.class);
                intent2.putExtra("flag", flag);
                intent2.putExtra("result", getIntent().getStringExtra("result"));
                intent2.putExtra("relType","FJ");
                startActivity(intent2);
                break;
            case R.id.btn_food://食品安全
                FoxContext.getInstance().setType("FK");
                Intent intent3 = new Intent(FHRestaurantActivity.this, ComAbnormalUpActivity.class);
                intent3.putExtra("flag", flag);
                intent3.putExtra("result", getIntent().getStringExtra("result"));
                intent3.putExtra("relType","FK");
                startActivity(intent3);
                break;
            case R.id.btn_health://卫生安全
                FoxContext.getInstance().setType("FL");
                Intent intent4 = new Intent(FHRestaurantActivity.this, ComAbnormalUpActivity.class);
                intent4.putExtra("flag", flag);
                intent4.putExtra("result", getIntent().getStringExtra("result"));
                intent4.putExtra("relType","FL");
                startActivity(intent4);
                break;
            case R.id.btn_food_check://食材抽查
                FoxContext.getInstance().setType("FM");
                Intent intent6 = new Intent(FHRestaurantActivity.this, ComAbnormalUpActivity.class);
                intent6.putExtra("flag", flag);
                intent6.putExtra("result", getIntent().getStringExtra("result"));
                intent6.putExtra("relType","FM");
                startActivity(intent6);
                break;
            case R.id.btn_give://供餐安全
                FoxContext.getInstance().setType("FN");
                Intent intent5 = new Intent(FHRestaurantActivity.this, ComAbnormalUpActivity.class);
                intent5.putExtra("flag", flag);
                intent5.putExtra("result", getIntent().getStringExtra("result"));
                intent5.putExtra("relType","FN");
                startActivity(intent5);
                break;
        }
    }

private List<FHMessage> fhMessage;
    /**
     * 獲取點檢類目
     *
     * @param type  權限
     * @param dimId 二維碼主鍵
     * @param flag  類型S為器材,D為點位
     */
    private void getDimId(String type, String dimId, String flag) {
        showDialog();
        url = Constants.HTTP_WATER_SCAN_SERVLET + "?type=" + "ZWCT" + "&dim_id=" + dimId + "&flag=" + flag;
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
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        fhMessage = new ArrayList<FHMessage>();

                        for (JsonElement type : array) {
                            FHMessage humi = gson.fromJson(type, FHMessage.class);
                            fhMessage.add(humi);

                        }
                        Message message = new Message();
                        message.what = MESSAGE_GET_DIM_LOCAL;

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
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_GET_DIM_LOCAL:
//                    ToastUtils.showLong(FHRestaurantActivity.this,"dddddddddddddddd");
//                    tvDName.setText(dimLocal);
//                    getItem((String) msg.obj);
                    for (int i = 0;i<fhMessage.size();i++){
                        if (Integer.valueOf(fhMessage.get(i).getCount().toString())>=Integer.valueOf(fhMessage.get(i).getDim_rate().toString())){
                            switch (i){
                                case 0:
                                    btnFlue.setClickable(false);
                                    btnFlue.setBackgroundColor(getResources().getColor(R.color.color_616161));
                                    break;
                                case 1:
                                    btnElectrical.setClickable(false);
                                    btnElectrical.setBackgroundColor(getResources().getColor(R.color.color_616161));
                                    break;
                                case 2:
                                    btnFire.setClickable(false);
                                    btnFire.setBackgroundColor(getResources().getColor(R.color.color_616161));
                                    break;
                                case 3:
                                    btnFood.setClickable(false);
                                    btnFood.setBackgroundColor(getResources().getColor(R.color.color_616161));
                                    break;
                                case 4:
                                    btnHealth.setClickable(false);
                                    btnHealth.setBackgroundColor(getResources().getColor(R.color.color_616161));
                                    break;
                                case 5:
                                    btnFoodCheck.setClickable(false);
                                    btnFoodCheck.setBackgroundColor(getResources().getColor(R.color.color_616161));
                                    break;
                                case 6:
                                    btnGive.setClickable(false);
                                    btnGive.setBackgroundColor(getResources().getColor(R.color.color_616161));
                                    break;
                            }
                        }
                    }

                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(FHRestaurantActivity.this, R.string.net_mistake);


                    break;
                case MESSAGE_TOAST:
                    ToastUtils.showShort(FHRestaurantActivity.this, msg.obj.toString());
                    finish();
                    break;
//                case MESSAGE_SET_TEXT://text賦值
//                    setText();
//
//                    if (status.equals("0-N")){
//                        getCheckMessage();
//                    }else {
//                        aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    }
//                    break;
                
//                case MESSAGE_SET_CHECK://點檢項列表賦值

//                    if (mCheckMsgList != null) {
//                        LinearLayoutManager layoutManager = new LinearLayoutManager(ComAbnormalUpActivity.this);
//                        rvOption.setLayoutManager(layoutManager);
//                        comAbAdapter = new ZhiyinshuiCheckAdapter(ComAbnormalUpActivity.this, mCheckMsgList, isSelected);
//                        rvOption.setAdapter(comAbAdapter);
//                    } else {
//                        ToastUtils.showShort(ComAbnormalUpActivity.this, "沒有數據!");
//                    }

//                    break;

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        getDimId("ZWCT", qrResult[1], "D");
    }
}