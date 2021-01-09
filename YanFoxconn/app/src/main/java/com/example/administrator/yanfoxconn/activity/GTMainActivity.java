package com.example.administrator.yanfoxconn.activity;

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
import com.example.administrator.yanfoxconn.adapter.MyGridViewAdapter;
import com.example.administrator.yanfoxconn.bean.GTMainBtn;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyGridView;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    @BindView(R.id.gv_btn)
    MyGridView myGridView;//點檢按鈕

    private List<GTMainBtn> gtMainBtns;//按鈕數據
    private MyGridViewAdapter myGridViewAdapter;

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
//                        dimLocal = jsonObject.get("dim_locale").getAsString();
////
//                        Message message = new Message();
//                        message.what = MESSAGE_GET_DIM_LOCAL;
//                        message.obj = dimLocal;
//                        mHandler.sendMessage(message);
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
            }
            super.handleMessage(msg);
        }};
}
