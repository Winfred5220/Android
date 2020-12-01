package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CarListAdapter;
import com.example.administrator.yanfoxconn.bean.CyCarMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 物流消殺 已提交車輛列表
 */
public class CyDelListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


    private final int MESSAGE_TOAST = 2;//showToast
    private final int MESSAGE_GET_LIST = 1;//請求成功

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回

    @BindView(R.id.lv_car_list)
    MyListView lvRouteList;//巡檢進度表
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題

    private CarListAdapter carListAdapter;
    private String from,code;//來源和車牌號
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cy_car_list);
        ButterKnife.bind(this);


        btnBack.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(this);
        lvRouteList.setOnItemClickListener(this);

        from=getIntent().getStringExtra("from");
        if (from.equals("two")){
            tvTitle.setText("特殊車輛列表");
            code=getIntent().getStringExtra("code");
        }else if (from.equals("one")){
            tvTitle.setText("已消殺車輛列表");
        }else{
            tvTitle.setText("已消殺特殊車輛列表");
        }
//            cyCarMessageList = new ArrayList<>();
//            cyCarMessageList = (List<CyCarMessage>) getIntent().getSerializableExtra("list");

//            if (cyCarMessageList != null) {
//                carListAdapter = new CarListAdapter(CyDelListActivity.this, cyCarMessageList,"cy");
//                lvRouteList.setAdapter(carListAdapter);
////            setListViewHeightBasedOnChildren(lvRouteList);
////            clickSeeOrAdd();
//            } else {
//                ToastUtils.showShort(this, "沒有數據!");
//            }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                FoxContext.getInstance().setTakePic("");
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()) {
            case R.id.lv_car_list://列表點擊事件
                if (from.equals("one")||from.equals("three")){
                    Intent intent = new Intent(CyDelListActivity.this, CyKillCheckActivity.class);
                    intent.putExtra("carMessage", (Serializable) cyCarMessages.get(position));
                    intent.putExtra("fenNo", cyCarMessages.get(position).getFEN_APPLY_NO());
                    startActivity(intent);
                }else if (from.equals("two")){
                    if (cyCarMessages.get(position).getFLAG().equals("")){
                        Intent intent = new Intent(CyDelListActivity.this, CyKillActivity.class);
                        intent.putExtra("carMessage", (Serializable)cyCarMessages.get(position));
                        intent.putExtra("type","ty");
                        startActivity(intent);}else{

                        Intent intent = new Intent(CyDelListActivity.this, CyKillCheckActivity.class);
                        intent.putExtra("carMessage", (Serializable) cyCarMessages.get(position));
                        intent.putExtra("fenNo", cyCarMessages.get(position).getFEN_APPLY_NO());
                        startActivity(intent);
                    }
                }
                break;
        }
    }

    /**
     * 物流消殺車輛 1:未上傳照片 2:已上傳照片
     */
    private String url;//請求地址
    private String result;//返回結果
    private List<CyCarMessage> cyCarMessages;//物流消殺車輛

    public void getCyCarList(String flag) {
        showDialog();
        url = Constants.HTTP_CY_SAFEVIEW_SERVLET + "?flag="+flag;
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "=getcarLog=fff===" + url);
                Gson gson = new Gson();
                String response = result;
                Log.e("---------", "result==fff===" + response);

                if (result != null) {
                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        cyCarMessages = new ArrayList<CyCarMessage>();
                        for (JsonElement type : array) {
                            CyCarMessage humi = gson.fromJson(type, CyCarMessage.class);
                            cyCarMessages.add(humi);
                        }
                        Message message = new Message();
                        message.what = MESSAGE_GET_LIST;
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
                    message.what = MESSAGE_TOAST;
                    message.obj = "無數據";
                    mHandler.sendMessage(message);

                }
                dismissDialog();
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    if (msg.obj.equals("失敗")) {
                        ToastUtils.showShort(CyDelListActivity.this, "沒有車輛數據!");

                    } else {
                        ToastUtils.showShort(CyDelListActivity.this, msg.obj.toString());

                        if (cyCarMessages!=null) {
                            cyCarMessages.clear();
                            carListAdapter = new CarListAdapter(CyDelListActivity.this, cyCarMessages, "cydel");
                            lvRouteList.setAdapter(carListAdapter);
                        }
                    }
                    break;
                case MESSAGE_GET_LIST:

                    if (from.equals("one")||from.equals("three")){
                    carListAdapter = new CarListAdapter(CyDelListActivity.this, cyCarMessages, "cydel");
                    lvRouteList.setAdapter(carListAdapter);
                    }else{

                        carListAdapter = new CarListAdapter(CyDelListActivity.this, cyCarMessages, "two");
                        lvRouteList.setAdapter(carListAdapter);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("---------", "onStart");
        if (from.equals("one")) {
            getCyCarList("2");
        }else if (from.equals("two")){

            try {
                getCyCarList(URLEncoder.encode(URLEncoder.encode(code.toString(), "UTF-8"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else{
            getCyCarList("3");
        }
    }


}
