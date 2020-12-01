package com.example.administrator.yanfoxconn.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 活動生成 界面
 * Created by song on 2018/6/26.
 */

public class EventBuildActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_OK = 1;

    @BindView(R.id.tv_title)
     TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
     Button btnBack;//返回
    @BindView(R.id.btn_create)
     Button btnBuild;//生成
    @BindView(R.id.name_event)
    EditText etName;

    private String result;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_build);

        ButterKnife.bind(this);

        tvTitle.setText("活動列表");

        btnBack.setOnClickListener(this);
        btnBuild.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left://返回
                finish();
            case R.id.btn_create://生成活動
                if (etName.getText().toString()!=null){
                createEvent();}else{
                    ToastUtils.showShort(this,"活動名稱不能為空");
                }
        }
    }

    public void createEvent(){
        Log.e("---------", "isVersionUpdate===");
        String name = null;
        String eventName = null;
        try {
            name = URLEncoder.encode(URLEncoder.encode(FoxContext.getInstance().getName(), "UTF-8"), "UTF-8");
            eventName = URLEncoder.encode(URLEncoder.encode(etName.getText().toString(), "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        showDialog();
        final String eventCreator = Constants.HTTP_BARCODE_CREATE_SERVLET + "?dim_type="+FoxContext.getInstance().getType()+"&dim_locale="+ eventName+"&createor="+name;
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(eventCreator);
                Log.e("---------", "fffff=url==" + eventCreator.toString());
                dismissDialog();
//                Gson gson = new Gson();
                Log.e("-----i----", "isVersionUpdate");
                if (result != null) {
                    Log.e("---------", "fffff===" + result.toString());
//                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
//                    String errCode = jsonObject.get("errCode").getAsString();
//                    if (errCode.equals("200")) {
//                        JsonArray array = jsonObject.get("data").getAsJsonArray();
//                        eventMessageList = new ArrayList<EventMessage>();
//                        for (JsonElement type : array) {
//                            EventMessage humi = gson.fromJson(type, EventMessage.class);
//                            eventMessageList.add(humi);
//                        }
//                        getVersionCode = Integer.parseInt(eventMessageList.get(0).getId());

                        Message message = new Message();
                        message.what = MESSAGE_OK;
                        message.obj = result;
                        mHandler.sendMessage(message);
//                    }
                }
            }
        }.start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showShort(EventBuildActivity.this, msg.obj.toString());
                    break;
                case MESSAGE_OK://是否更新的彈出框
                    ToastUtils.showShort(EventBuildActivity.this, msg.obj.toString());
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
