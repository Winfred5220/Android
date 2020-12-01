package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 實際巡檢人員維護
 * Created by song on 2017/11/22.
 */

public class ActualPeopleActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;
    @BindView(R.id.tv_title)
     TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
     Button btnBack;//返回

    private Button btnUpName;//提交按鈕
    private LinearLayout container;

    private String result;//返回結果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_people);
        ButterKnife.bind(this);
        tvTitle.setText("巡檢人員維護");
        btnBack.setOnClickListener(this);

        Button btnAdd = findViewById(R.id.btn);

        btnAdd.setOnClickListener(this);
        // 父控件
        container = (LinearLayout) findViewById(R.id.ll_actual);
        btnUpName = findViewById(R.id.btn_up_name);
        btnUpName.setOnClickListener(this);
        Log.e("--======","222222222222");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                // 根据tag区分是新增view还是删除view
                String tag = (String) view.getTag();
                if ("-".equals(tag)) {
                    // 删除view
                    // 获取子控件
                    View child = (View) view.getParent();
                    // 从父控件中移除子控件
                    container.removeView(child);
                } else {
                    // 新增view
                    // 创建子控件实例
                    View child = LayoutInflater.from(ActualPeopleActivity.this).inflate(R.layout.edt, container, false);
                    // 获取其中的button
                    View btn = child.findViewById(R.id.btn);
                    // 监听点击事件
                    btn.setOnClickListener(this);
                    // 设置删除的tag
                    btn.setTag("-");
                    btn.setBackgroundDrawable(ContextCompat.getDrawable(this.getApplication(), R.mipmap.icon_minus));

                    final EditText editText = child.findViewById(R.id.et_name);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            //输入内容之前你想做什么
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            //输入的时候你想做什么
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            //输入之后你想做什么                num[cout]=Integer.parseInt(editText.getText().toString());
                            Log.e("-------------", "et=====" + editText.getText());
                        }
                    });
                    // 添加进父控件
                    container.addView(child);
                }
                // 请求重绘
                container.invalidate();

                break;
            case R.id.btn_up_name:
                StringBuilder names = new StringBuilder("");
                for (int i = 0; i < container.getChildCount(); i++) {
//            String names;
                    View childAt = container.getChildAt(i);
                    EditText hotelName = (EditText) childAt.findViewById(R.id.et_name);

                    if (!hotelName.getText().toString().equals("")) {
                        if (i == 0 || names.equals("")) {
                            names.append(hotelName.getText());
                        } else {
                            names.append("," + hotelName.getText());
                        }
                    } else {
                        ToastUtils.showShort(this, "不能有空數據,請確認!!!!");
                        names = new StringBuilder("");
                        break;
                    }
                }
                Log.e("---------", "hotelName===" + names);
                try {
                    String name =     URLEncoder.encode(URLEncoder.encode(names.toString(), "UTF-8"), "UTF-8");
                   if(name.equals("")||name.equals(null)){
                       ToastUtils.showShort(this, "不能有空數據,請確認!!!!");
                   }else{
                       upName(name);
                   }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_title_left:
                finish();
                break;
        }


    }

    /**
     * 巡檢人員名稱
     * @param names
     */
    private void upName(String names){

        showDialog();

        final String url = Constants.HTTP_PATORLMAN_SERVLET + "?type=" + FoxContext.getInstance().getRoles() + "&man=" + names;

        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Gson gson = new Gson();
                Log.e("----fff-url----", url.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Intent intent = new Intent(ActualPeopleActivity.this,ExListViewActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
            }
        }}.start();

    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showShort(ActualPeopleActivity.this,msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
