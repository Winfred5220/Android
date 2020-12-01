package com.example.administrator.yanfoxconn.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.ToastUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HubSelectInputActivity extends BaseActivity implements View.OnClickListener , TimeDatePickerDialog.TimePickerDialogInterface{

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回

    @BindView(R.id.btn_before)
    Button btnBefore;//前一天
    @BindView(R.id.tv_date)
    TextView tvDate;//日期
    @BindView(R.id.btn_after)
    Button btnAfter;//後一天
    @BindView(R.id.et_name)
    EditText etName;//姓名
    @BindView(R.id.btn_clear)
    Button btnClear;//清除
    @BindView(R.id.btn_selt)
    Button btnSelt;//查詢

    private String nowDateTime;//获取当前时间
    private String changeDate;//改變的時間,同時用於設置tvDate
    private Calendar noChangeTime = Calendar.getInstance();
    private Calendar changeDateTime = Calendar.getInstance();

    private TimeDatePickerDialog timeDatePickerDialog;
    private SimpleDateFormat formatter;
    private SimpleDateFormat formattery = new SimpleDateFormat("yyyy");
    private SimpleDateFormat formatterm = new SimpleDateFormat("MM");
    private SimpleDateFormat formatterd = new SimpleDateFormat("dd");
    private int mYear, mMonth, mDay;
    private String area;//获取區域
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_select_input);
        ButterKnife.bind(this);

        formatter = new SimpleDateFormat("yyyy-MM-dd");
        noChangeTime = Calendar.getInstance();
        nowDateTime = formatter.format(noChangeTime.getTime());
        mYear = Integer.parseInt(formattery.format(noChangeTime.getTime()));
        mMonth = Integer.parseInt(formatterm.format(noChangeTime.getTime()));
        mDay = Integer.parseInt(formatterd.format(noChangeTime.getTime()));
        tvDate.setText(nowDateTime);

        tvTitle.setText("簽收查詢");
        area = getIntent().getStringExtra("area");
        btnBack.setOnClickListener(this);
        btnBefore.setOnClickListener(this);
        btnAfter.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        btnSelt.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        delBottonShow();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_clear:
                etName.setText("");
                break;
            case R.id.btn_selt:
                Intent intent = new Intent(HubSelectInputActivity.this,HubSelectActivity.class);
                intent.putExtra("area",area);
                intent.putExtra("name",etName.getText().toString());
                intent.putExtra("date",tvDate.getText().toString());
                startActivity(intent);
                break;
            case R.id.btn_before:
                SimpleDateFormat sdfBefore = new SimpleDateFormat("yyyy-MM-dd");
                changeDate = sdfBefore.format(getBeforeDay(changeDateTime).getTime());
                mYear = Integer.parseInt(formattery.format(changeDateTime.getTime()));
                mMonth = Integer.parseInt(formatterm.format(changeDateTime.getTime()));
                mDay = Integer.parseInt(formatterd.format(changeDateTime.getTime()));
                tvDate.setText(changeDate);
                break;
            case R.id.btn_after:
                SimpleDateFormat sdfAfter = new SimpleDateFormat("yyyy-MM-dd");
                changeDate = sdfAfter.format(getAfterDay(changeDateTime).getTime());
                mYear = Integer.parseInt(formattery.format(changeDateTime.getTime()));
                mMonth = Integer.parseInt(formatterm.format(changeDateTime.getTime()));
                mDay = Integer.parseInt(formatterd.format(changeDateTime.getTime()));
                tvDate.setText(changeDate);
                break;
            case R.id.tv_date:
                timeDatePickerDialog = new TimeDatePickerDialog(HubSelectInputActivity.this,mYear, mMonth-1, mDay);
                timeDatePickerDialog.showDatePickerDialog();
                break;
        }
    }
    /*清空按钮 显示或隐藏*/
    private void delBottonShow() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    btnClear.setAlpha(0);
                } else {
                    btnClear.setAlpha(1);
                }
            }
        });
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
//        if (cl.compareTo(noChangeTime) > 0) {
//            ToastUtils.showShort(HubSelectInputActivity.this, "未到日期不可查詢");
//            btnAfter.setClickable(false);
//
//            day = cl.get(Calendar.DATE);
//            cl.set(Calendar.DATE, day - 1);
//        } else if (cl.compareTo(noChangeTime) < 0) {
//            btnAfter.setClickable(true);
//        }
        return cl;
    }

    //时间选择器----------确定
    @Override
    public void positiveListener() {

        try {
            changeDateTime.setTime(formatter.parse(timeDatePickerDialog.getTimeDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mYear = timeDatePickerDialog.getYear();
        mDay = timeDatePickerDialog.getDay();
        mMonth = timeDatePickerDialog.getMonth();
        tvDate.setText(timeDatePickerDialog.getDate());
    }

    //时间选择器-------取消
    @Override
    public void negativeListener() {
        if (timeDatePickerDialog.getTimeDate().equals("")) {
            ToastUtils.showShort(this, "請選擇時間");
        }
    }

}
