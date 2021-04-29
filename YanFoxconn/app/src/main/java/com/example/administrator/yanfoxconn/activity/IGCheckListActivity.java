package com.example.administrator.yanfoxconn.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.ZhiyinshuiCheckAdapter;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiCheckMsg;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.ToastUtils;

import java.util.HashMap;
import java.util.List;

/**
 * @Description 宿舍寄存 倉庫盤點 列表界面
 * @Author song
 * @Date 4/24/21 11:52 AM
 */
public class IGCheckListActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.rv_option)
    RecyclerView rvOption;//點檢項目列表

    private ZhiyinshuiCheckAdapter comAbAdapter;//點檢列表適配器
    private HashMap<Integer, String> isSelected = new HashMap<>();//用户存储条目的选择状态
    private List<Integer> selectList;//用于存放無異常的条目
    private List<Integer> noselectList;//用于存放有異常的条目
    private int key1, key2,key3 = 0;//提交時管控未填寫信息
    private String url;//請求地址
    private String result;//請求返回結果
    private String type;//類型
    private List<ZhiyinshuiCheckMsg> mCheckMsgList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ig_check_list);

        ButterKnife.bind(this);
        tvTitle.setText("巡檢界面");
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        //getLocationInfo(sh_code,flag);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
        case R.id.btn_title_left:
        finish();
        break;
        case R.id.btn_title_right:

        break;}
    }
}