package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.AbnormalListAdapter;
import com.example.administrator.yanfoxconn.bean.AbnormalMessage;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 查看異常 列表界面
 * Created by song on 2020/7/30.
 */

public class ComAbAbnormalListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_SEE_ABNORMAL = 3;

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_d_name)
    TextView tvDName;
    @BindView(R.id.lv_abnormal)
    ListView lvAbnormal;//列表
    @BindView(R.id.btn_before)
    Button btnBefore;//前一天
    @BindView(R.id.tv_date)
    TextView tvDate;//日期
    @BindView(R.id.btn_after)
    Button btnAfter;//後一天


    private String dimId;
    private String type;
    private String scId;
    private String creater="";//點檢人工號,用於查看詳情時,是否顯示刪除
    private List<AbnormalMessage> abnormalList;
    private AbnormalListAdapter abnormalListAdapter;

    private String result;

    private String nowDateTime;//获取当前时间
    private String changeDate;//改變的時間,同時用於設置tvDate

    private Calendar noChangeTime = Calendar.getInstance();
    private Calendar changeDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_see_abnormal);
        ButterKnife.bind(this);
        tvTitle.setText("異常列表");
        tvDName.setText(getIntent().getStringExtra("dName"));

        tvDName.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnBefore.setOnClickListener(this);
        btnAfter.setOnClickListener(this);
        btnAfter.setClickable(false);
        lvAbnormal.setOnItemClickListener(this);

        noChangeTime = Calendar.getInstance();
        SimpleDateFormat sdfAfter = new SimpleDateFormat("yyyy-MM-dd");
        nowDateTime = sdfAfter.format(noChangeTime.getTime());
        tvDate.setText(nowDateTime);

        Log.e("-----------","nowDateTime=="+nowDateTime);

        dimId = getIntent().getStringExtra("dimId");
        type = FoxContext.getInstance().getType();
        creater = getIntent().getStringExtra("creater");
        scId = getIntent().getStringExtra("scId");
        if (!scId.equals("")){
            btnBefore.setVisibility(View.GONE);
                    btnAfter.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_before:
                SimpleDateFormat sdfBefore = new SimpleDateFormat("yyyy-MM-dd");
                String dateBefore = sdfBefore.format(getBeforeDay(changeDateTime).getTime());
                toSeeAbnormalActivity(dimId, type, dateBefore,scId);
                break;
            case R.id.btn_after:
                SimpleDateFormat sdfAfter = new SimpleDateFormat("yyyy-MM-dd");
                String dateAfter = sdfAfter.format(getAfterDay(changeDateTime).getTime());
                toSeeAbnormalActivity(dimId, type, dateAfter,scId);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()) {
            case R.id.lv_abnormal://列表點擊事件
                Intent intent = new Intent(ComAbAbnormalListActivity.this, ComAbDetailActivity.class);
                intent.putExtra("dimId",dimId);
                intent.putExtra("ab",(Serializable)abnormalList.get(position));
                intent.putExtra("creater",creater);
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取当前时间的前一天时间
     *
     * @param cl
     * @return
     */
    private Calendar getBeforeDay(Calendar cl) {
        //使用roll方法进行向前回滚
        //cl.roll(Calendar.DATE, -1);
        //使用set方法直接进行设置
        if (cl.equals(noChangeTime)) {

            btnAfter.setClickable(false);
        } else {
            int day = cl.get(Calendar.DATE);
            cl.set(Calendar.DATE, day - 1);

            btnAfter.setClickable(true);
        }
        return cl;
    }

    /**
     * 获取当前时间的后一天时间
     *
     * @param cl
     * @return
     */
    private Calendar getAfterDay(Calendar cl) {
        //使用roll方法进行回滚到后一天的时间
        //cl.roll(Calendar.DATE, 1);
        //使用set方法直接设置时间值
        int day = cl.get(Calendar.DATE);
        cl.set(Calendar.DATE, day + 1);
        if (cl.compareTo(noChangeTime) > 0) {
            ToastUtils.showShort(ComAbAbnormalListActivity.this, "未到日期不可查詢");
            btnAfter.setClickable(false);

            day = cl.get(Calendar.DATE);
            cl.set(Calendar.DATE, day - 1);
        } else if (cl.compareTo(noChangeTime) < 0) {
            btnAfter.setClickable(true);
        }
        return cl;
    }

    /**
     * 獲取點位的異常記錄
     * @param dimId         二維碼主鍵
     * @param type           權限
     * @param textDateTime   時間
     */
    private void toSeeAbnormalActivity(String dimId, String type, final String textDateTime,String scId) {

        showDialog();
        final String url = Constants.HTTP_WATER_EXCE_VIEW_INFO_SERVLET + "?dim_id=" + dimId + "&type=" + type +
                "&date=" + textDateTime+"&sc_id="+scId;
        Log.e("-------fff--", "url==" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Gson gson = new Gson();
                Log.e("--fff---result----", result.toString());
                if (result != null) {
                    abnormalList = new ArrayList<AbnormalMessage>();
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("result").getAsJsonArray();

                        for (JsonElement type : array) {
                            AbnormalMessage humi = gson.fromJson(type, AbnormalMessage.class);
                            abnormalList.add(humi);
                        }
                        Log.e("-----------routelist", "abnormalMessageList==" + abnormalList.size());

                        changeDate = textDateTime;
                        Message message = new Message();
                        message.what = MESSAGE_SEE_ABNORMAL;
                        message.obj = abnormalList;
                        mHandler.sendMessage(message);

                    } else if (errCode.equals("400")) {

                        changeDate = textDateTime;
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = "400";
                        mHandler.sendMessage(message);
                        abnormalList.clear();


                    }
                } else {
                    ToastUtils.showShort(ComAbAbnormalListActivity.this, "請求不成功");
                }

            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    if (msg.obj.equals("400")) {
                        abnormalListAdapter = new AbnormalListAdapter(ComAbAbnormalListActivity.this, abnormalList,"D");
                        lvAbnormal.setAdapter(abnormalListAdapter);
                        ToastUtils.showLong(ComAbAbnormalListActivity.this, "此點無異常數據!");
                        tvDate.setText(changeDate);
                    } else {
                        ToastUtils.showShort(ComAbAbnormalListActivity.this, msg.obj.toString());
                        tvDate.setText(nowDateTime);
                    }
                    break;
                case MESSAGE_SEE_ABNORMAL://跳轉異常列表
                    abnormalListAdapter = new AbnormalListAdapter(ComAbAbnormalListActivity.this, abnormalList,"D");
                    lvAbnormal.setAdapter(abnormalListAdapter);

                    tvDate.setText(changeDate);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        toSeeAbnormalActivity(dimId, FoxContext.getInstance().getType(),nowDateTime,scId);
    }
}
