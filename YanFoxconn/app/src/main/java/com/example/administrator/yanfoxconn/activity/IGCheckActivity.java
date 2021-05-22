package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.IGMessage;
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

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description 宿舍寄存 倉庫盤點主界面
 * @Author song
 * @Date 4/24/21 11:52 AM
 */
public class IGCheckActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;//請求成功
    private final int MESSAGE_SET_TEXT = 1;//賦值

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題

    @BindView(R.id.tv_id)
    TextView tvId;//倉庫編碼
    @BindView(R.id.tv_zy)
    TextView tvZY;//在用儲位數量
    @BindView(R.id.tv_kz)
    TextView tvKZ;//空置儲位數量
    @BindView(R.id.tv_yc)
    TextView tvYC;//異常數量
    @BindView(R.id.tv_date)
    TextView tvDate;//上次盤點日期

    private IGMessage storMessage;
    private String id;//倉庫代碼

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ig_check);
        ButterKnife.bind(this);

        tvTitle.setText("倉庫盤點");
        btnBack.setText("返回");

        btnBack.setOnClickListener(this);
        tvZY.setOnClickListener(this);
        tvKZ.setOnClickListener(this);
        tvYC.setOnClickListener(this);
        id = getIntent().getStringExtra("id");
    }

    private void getStoreInfoById(String sh_code,String zwuser){
        showDialog();
        String name = null;
        try {
            name = URLEncoder.encode(URLEncoder.encode(zwuser, "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String url = Constants.HTTP_INVENTORY_STORE_INFO + "?sh_code=" + sh_code + "&zwuser=" + name;

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);

                dismissDialog();
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonObject data = jsonObject.get("data").getAsJsonObject();

                        storMessage = new IGMessage();

                        storMessage.setSh_code(data.get("sh_code").getAsString());
                        storMessage.setZY(data.get("ZY").getAsString());
                        storMessage.setKZ(data.get("KZ").getAsString());
                        storMessage.setYC(data.get("YC").getAsString());
                        storMessage.setSI_CREATE_DATE(data.get("SI_CREATE_DATE").getAsString());

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = data;
                        mHandler.sendMessage(message);

                    } else {
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getStoreInfoById(id, FoxContext.getInstance().getName());
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showLong(IGCheckActivity.this, msg.obj.toString());
                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值

                  tvId.setText(storMessage.getSh_code());
                    tvZY.setText(storMessage.getZY());
                    tvKZ.setText(storMessage.getKZ());
                    tvYC.setText(storMessage.getYC());
                    tvDate.setText(storMessage.getSI_CREATE_DATE());
                    break;
                    }
                    super.handleMessage(msg);
            }
        };
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.tv_zy://在用
                Intent intent = new Intent(IGCheckActivity.this,IGCheckListActivity.class);
                intent.putExtra("sh_code",id);
                intent.putExtra("flag","ZY");
                startActivity(intent);
                break;
            case R.id.tv_kz://空置
                Intent intent1 = new Intent(IGCheckActivity.this,IGCheckListActivity.class);
                intent1.putExtra("sh_code",id);
                intent1.putExtra("flag","KZ");
                startActivity(intent1);
                break;
            case R.id.tv_yc://異常

                Intent intent2 = new Intent(IGCheckActivity.this,IGCheckListActivity.class);
                intent2.putExtra("sh_code",id);
                intent2.putExtra("flag","YC");
                startActivity(intent2);
                break;

        }
    }
}
