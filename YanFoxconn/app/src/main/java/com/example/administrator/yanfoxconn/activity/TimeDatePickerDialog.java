package com.example.administrator.yanfoxconn.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.example.administrator.yanfoxconn.R;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by song on 2017/9/7.
 */

public class TimeDatePickerDialog {
    private Context mContext;
    private AlertDialog.Builder mAlertDialog;
    private int mHour, mMinute;
    private TimePickerDialogInterface timePickerDialogInterface;
    private TimePicker mTimePicker;
    private DatePicker mDatePicker;
    private int mTag = 0;
    private int mYear, mMonth, mDay;
    private String timeDate;
    private DatePicker.OnDateChangedListener mOnDateChangedListener;

    public TimeDatePickerDialog(Context context,int Year,int Month,int Day) {
        super();
        mContext = context;
        mYear = Year;
        mMonth = Month;
        mDay = Day;
        timePickerDialogInterface = (TimePickerDialogInterface) context;
    }

    /**
     * 初始化DatePicker
     *
     * @return
     */
    private View initDatePicker() {
        View inflate = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_time_date_picker, null);
        mTimePicker = (TimePicker) inflate
                .findViewById(R.id.timePicker);
        mDatePicker = (DatePicker) inflate
                .findViewById(R.id.datePicker);
        mTimePicker.setVisibility(View.GONE);
        mDatePicker.init(mYear,mMonth,mDay,mOnDateChangedListener);
        resizePikcer(mDatePicker);
        return inflate;
    }

    /**
     * 初始化TimePicker
     *
     * @return
     */
    private View initTimePicker() {
        View inflate = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_time_date_picker, null);
        mTimePicker = (TimePicker) inflate
                .findViewById(R.id.timePicker);
        mDatePicker = (DatePicker) inflate
                .findViewById(R.id.datePicker);
        mDatePicker.setVisibility(View.GONE);
        mTimePicker.setIs24HourView(true);
        resizePikcer(mTimePicker);
        return inflate;
    }

    private View initDateAndTimePicker() {
        View inflate = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_time_date_picker, null);
        mTimePicker = (TimePicker) inflate
                .findViewById(R.id.timePicker);
        mDatePicker = (DatePicker) inflate
                .findViewById(R.id.datePicker);
        mTimePicker.setIs24HourView(true);
        resizePikcer(mTimePicker);
        resizePikcer(mDatePicker);
        return inflate;
    }

    /**
     * 创建dialog
     *
     * @param view
     */
    private void initDialog(View view) {
        mAlertDialog.setPositiveButton("确定",
                new android.content.DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();

                        if (mTag == 0) {
                            getTimePickerValue();
                        } else if (mTag == 1) {
                            getDatePickerValue();
                        } else if (mTag == 2) {
                            getDatePickerValue();
                            getTimePickerValue();
                        }
                        timePickerDialogInterface.positiveListener();

                    }
                });
        mAlertDialog.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        timePickerDialogInterface.negativeListener();
                        dialog.dismiss();
                    }
                });
        mAlertDialog.setView(view);
    }

    /**
     * 显示时间选择器
     */
    public void showTimePickerDialog() {
        mTag=0;
        View view = initTimePicker();
        mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setTitle("选择时间");
        initDialog(view);
        mAlertDialog.show();
    }

    /**
     * 显示日期选择器
     */
    public void showDatePickerDialog() {
        mTag=1;
        View view = initDatePicker();
        mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setTitle("选择日期");
        initDialog(view);
        mAlertDialog.show();
    }
    /**
     * 显示日期选择器
     */
    public void showDateAndTimePickerDialog() {
        mTag=2;
        View view = initDateAndTimePicker();
        mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setTitle("选择时间");
        initDialog(view);
        mAlertDialog.show();
    }

    /*
    * 调整numberpicker大小
    */
    private void resizeNumberPicker(NumberPicker np) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        np.setLayoutParams(params);
    }

    /**
     * 调整FrameLayout大小
     *
     * @param tp
     */
    private void resizePikcer(FrameLayout tp) {
        List<NumberPicker> npList = findNumberPicker(tp);
        for (NumberPicker np : npList) {
            resizeNumberPicker(np);
        }
    }

    /**
     * 得到viewGroup里面的numberpicker组件
     *
     * @param viewGroup
     * @return
     */
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    public int getYear() {
        return mYear;
    }

    public int getDay() {
        return mDay;
    }

    public int getMonth() {
        //返回的时间是0-11
        return mMonth+1;
    }

    public int getMinute() {
        return mMinute;
    }

    public int getHour() {
        return mHour;
    }

    /**
     * 获取日期选择的值
     */
    private void getDatePickerValue() {
        mYear = mDatePicker.getYear();
        mMonth = mDatePicker.getMonth();
        mDay= mDatePicker.getDayOfMonth();
    }

    /**
     * 获取时间选择的值
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getTimePickerValue() {
        // api23这两个方法过时
        mHour = mTimePicker.getHour();// timePicker.getHour();
        mMinute = mTimePicker.getMinute();// timePicker.getMinute();
    }

    public interface TimePickerDialogInterface {
        public void positiveListener();
        public void negativeListener();
    }

    public String getTimeDate(){
        timeDate = getYear()+"-"+change(getMonth())+"-"+change(getDay())+" "+getHour()+":"+getMinute();
        return timeDate;
    }
    public String getDate(){
        timeDate = getYear()+"-"+change(getMonth())+"-"+change(getDay());
        return timeDate;
    }
    public String change(int t){
        if (t<10){
            return "0"+t;
        }else {
            return ""+t;
        }
    }
}