package com.example.administrator.yanfoxconn.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CarLogListAdapter;
import com.example.administrator.yanfoxconn.bean.CarLogReturnM;
import com.example.administrator.yanfoxconn.utils.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 行車日誌 退件界面
 * Created by song on 2018/1/11.
 */

public class CarLogReturnActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.lv_log)
    ListView lvLogReturn;
    @BindView(R.id.btn_add)
    Button btnAdd;//添加按鈕不顯示

    private List<CarLogReturnM> carLogReturnMList;
    private CarLogListAdapter carLogListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_log_list);
        ButterKnife.bind(this);

        carLogReturnMList = new ArrayList<>();
        carLogReturnMList = (List<CarLogReturnM>) getIntent().getSerializableExtra("returnList");
        carLogListAdapter = new CarLogListAdapter(this, carLogReturnMList,true);
        lvLogReturn.setAdapter(carLogListAdapter);

        btnBack.setOnClickListener(this);
        lvLogReturn.setOnItemClickListener(this);
        btnAdd.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left:
                finish();
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
