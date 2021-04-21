package com.example.administrator.yanfoxconn.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.utils.BaseActivity;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description 宿舍寄存 儲位變更主界面
 * @Author song
 * @Date 4/15/21 11:04 AM
 */
public class IGChangeActivity extends BaseActivity {
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_type)
    TextView tvType;//
    @BindView(R.id.tv_num)
    TextView tvNum;//
    @BindView(R.id.tv_store)
    TextView tvStore;//
    @BindView(R.id.sp_store)
    Spinner spStore;//選擇倉庫
    @BindView(R.id.tr_new_store)
    TableRow trNewStore;//新儲位行
    @BindView(R.id.tv_new_store)
    TextView tvNewStore;//新儲位

    @Override
    public void onCreate( Bundle savedInstanceState,  PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.activity_ig_change);
        ButterKnife.bind(this);

    }
}
