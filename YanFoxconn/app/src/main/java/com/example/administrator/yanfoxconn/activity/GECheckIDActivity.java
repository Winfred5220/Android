package com.example.administrator.yanfoxconn.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *安保部健康追蹤
 * song 2020/11/28
 * 1員工  2供應商
 */
public class GECheckIDActivity extends BaseActivity {
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒

    @BindView(R.id.tv_id)
    TextView tvId;//工號
    @BindView(R.id.tv_name)
    TextView tvName;//姓名
    @BindView(R.id.tv_identity)
    TextView tvIdentity;//身份證
    @BindView(R.id.tv_pro)
    TextView tvPro;//產品處
    @BindView(R.id.tv_dep)
    TextView tvDep;//部門
    @BindView(R.id.sp_team)
    Spinner spTeam;//門崗
    @BindView(R.id.sp_area)
    Spinner spArea;//留觀地點
    @BindView(R.id.et_temp)
    EditText etTemp;//體溫
    @BindView(R.id.et_desp)
    EditText etDesp;//詳細描述

    private String id,flag;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ge_check_id);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("result");
        flag = getIntent().getStringExtra("check");
        getPeopleMessage(id,flag);

    }

    /**
     * 獲取人員信息
     * @param id    工號或身分證
     * @param flag  廠商或員工
     */
    private void getPeopleMessage(String id,String flag){
        showDialog();
        final String url = Constants.HTTP_HEALTH_SCAN+"?id="+id+"&flag="+flag;

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
//                        JsonArray array = jsonObject.get("data").getAsJsonArray();
//                        empMessagesList = new ArrayList<EmpMessage>();
//
//                        for (JsonElement type : array) {
//                            EmpMessage humi = gson.fromJson(type, EmpMessage.class);
//                            empMessagesList.add(humi);
//
//                        }
//
//                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
//                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();
//                        empFileList = new ArrayList<EmpFile>();
//                        for (JsonElement type1 : array1) {
//                            EmpFile humi1 = gson.fromJson(type1, EmpFile.class);
//                            empFileList.add(humi1);
//                        }
//
//                        Message message = new Message();
//                        message.what = MESSAGE_SET_TEXT;
//                        message.obj = jsonObject.get("errMessage").getAsString();
//                        mHandler.sendMessage(message);

                    } else{
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            } }.start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

                    ToastUtils.showLong(GECheckIDActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值
//                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_UP://提交響應
//                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                    break;
//                case MESSAGE_SHOW://顯示提醒
//                    setText();
//                    tvShow.setVisibility(View.VISIBLE);
//                    tvShow.setText(msg.obj.toString());
            }
            super.handleMessage(msg);
        }
    };
}
