package com.example.administrator.yanfoxconn.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.EventTimeAdapter;
import com.example.administrator.yanfoxconn.bean.EventTime;
import com.example.administrator.yanfoxconn.utils.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 人資活動簽到時間列表界面
 * Created by song on 2018/7/25.
 */

public class EventTimeLvActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.lv_event_time)
    ListView lvTime;//列表

    private List<EventTime> eventTimeList;
    private EventTimeAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_time_list);
        ButterKnife.bind(this);

        tvTitle.setText("加班情況表");
        btnBack.setOnClickListener(this);

        if (getIntent().getSerializableExtra("list")!=null){
        eventTimeList= (List<EventTime>) getIntent().getSerializableExtra("list");}
        adapter = new EventTimeAdapter(EventTimeLvActivity.this,eventTimeList);
        lvTime.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left:
                finish();
                break;

        }
    }
}
