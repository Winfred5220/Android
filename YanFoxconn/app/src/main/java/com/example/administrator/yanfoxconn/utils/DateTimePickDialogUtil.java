package com.example.administrator.yanfoxconn.utils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.administrator.yanfoxconn.R;

/**
 * Created by song on 2017/9/8.
 */

public class DateTimePickDialogUtil implements DatePicker.OnDateChangedListener,
        TimePicker.OnTimeChangedListener {
    private DatePicker datePicker;
    private TimePicker timePicker;
    private NumberPicker mMinuteSpinner;
    private AlertDialog ad;
    private String dateTime;
    private String initDateTime;
    private Activity activity;
    private String minDate;

    /**
     * 日期时间弹出选择框构造函数
     *
     * @param activity     ：调用的父activity
     * @param initDateTime 初始日期时间值，作为弹出窗口的标题和日期时间初始值
     */
    public DateTimePickDialogUtil(Activity activity, String initDateTime) {
        this.activity = activity;
        this.initDateTime = initDateTime;

    }



    public void init(DatePicker datePicker, TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();
        if (!(null == initDateTime || "".equals(initDateTime))) {
            calendar = this.getCalendarByInintData(initDateTime);
        } else {
            initDateTime = calendar.get(Calendar.YEAR) + "年"
                    + calendar.get(Calendar.MONTH) + "月"
                    + calendar.get(Calendar.DAY_OF_MONTH) + "日 "
                    + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE);
        }

        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));
        } else {
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        }

    }
    public void init30(DatePicker datePicker, TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();
        if (!(null == initDateTime || "".equals(initDateTime))) {
            calendar = this.getCalendarByInintData(initDateTime);
        } else {
            initDateTime = calendar.get(Calendar.YEAR) + "年"
                    + calendar.get(Calendar.MONTH) + "月"
                    + calendar.get(Calendar.DAY_OF_MONTH) + "日 "
                    + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE);
        }

        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {

            Date dateMin = sdf.parse(minDate);
            datePicker.setMinDate(0);
            Log.e("-------minDate----","dateMminDatein.getTime()=="+dateMin.getTime());
            datePicker.setMinDate(dateMin.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));
        } else {
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        }
        setNumberPickerTextSize(timePicker);



    }
    String[] minuts = new String[]{"00","30"};//间隔30的数组，用来表示可设置的分钟值

    /** * 获得timePicker里面的android.widget.NumberPicker组件 （有两个android.widget.NumberPicker组件--hour，minute） * @param viewGroup * @return */
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup)
    {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;

        Log.e("-----------","dateMin.6getTime()=="+minDate);
        if (null != viewGroup)
        {
            for (int i = 0; i < viewGroup.getChildCount(); i++)
            {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker)
                {
                    npList.add((NumberPicker)child);
                }
                else if (child instanceof LinearLayout)
                {
                    List<NumberPicker> result = findNumberPicker((ViewGroup)child);
                    if (result.size() > 0)
                    {
                        return result;
                    }
                }
            }
        }

        return npList;
    }

    /** * 查找timePicker里面的android.widget.NumberPicker组件 ，并对其进行时间间隔设置 * @param viewGroup TimePicker timePicker */
    private void setNumberPickerTextSize(ViewGroup viewGroup){
        List<NumberPicker> npList = findNumberPicker(viewGroup);
        Log.e("-----------","dateMin.7getTime()=="+minDate);
        if (null != npList)
        {
            for (NumberPicker mMinuteSpinner : npList)
            {
// System.out.println("mMinuteSpinner.toString()="+mMinuteSpinner.toString());
                if(mMinuteSpinner.toString().contains("id/minute")){//对分钟进行间隔设置

                    mMinuteSpinner.setMinValue(0);
                    mMinuteSpinner.setMaxValue(minuts.length-1);
                    mMinuteSpinner.setDisplayedValues(minuts);  //这里的minuts是一个String数组，就是要显示的分钟值
                }
                //对小时进行间隔设置 使用 if(mMinuteSpinner.toString().contains("id/hour")){}便可
            }
        }
    }

    public void initNoTime(DatePicker datePicker) {
        Calendar calendar = Calendar.getInstance();
        if (!(null == initDateTime || "".equals(initDateTime))) {
            calendar = this.getCalendarNoTimeByInintData(initDateTime);
        } else {
            initDateTime = calendar.get(Calendar.YEAR) + "年"
                    + calendar.get(Calendar.MONTH) + "月"
                    + calendar.get(Calendar.DAY_OF_MONTH) + "日 "
                   ;
        }

        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);

    }

    /**
     * 弹出日期时间选择框方法
     *
     * @param inputDate :为需要设置的日期时间文本编辑框
     * @return
     */
    public AlertDialog dateTimePicKDialog(final TextView inputDate, final String minDate, final String maxDate) {
        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.dialog_time_date_picker, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datePicker);
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timePicker);


        if (!maxDate.equals("") && !minDate.equals("")) {
//            Log.e("-----------","maxDate====="+maxDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
//                stringToDate(maxDate,"yyyy-MM-dd HH:mm:ss");

//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
////                    Log.e("-----------","M====="+maxDate);
//                    Date dateMax = sdf.parse(maxDate);
//                    Date dateMin = sdf.parse(minDate);
//                    SimpleDateFormat sdf1= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
//                    String dateStrMax=sdf1.format(dateMax);
//                    String dateStrMin=sdf1.format(dateMin);
//                    Log.e("-----------","dateStrMax====="+dateStrMax);
//                    Log.e("-----------","dateStrMin====="+dateStrMin);
////                    Calendar c1 = Calendar.getInstance();
////                    c1.setTime(dateMax);
////                    c1.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
//////                    c1.add(Calendar.HOUR_OF_DAY,-8);
////                    datePicker.setMaxDate(c1.getTimeInMillis());
////                    Calendar c2 = Calendar.getInstance();
////                    c2.setTime(dateMin);
////                    c2.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
////                    c2.add(Calendar.HOUR_OF_DAY,-8);
////                    datePicker.setMinDate(c2.getTimeInMillis());
////                    Log.e("-----------","c1.getTimeInMillis()=="+c1.getTimeInMillis());
////                    Log.e("-----------","c2.getTimeInMillis()=="+c2.getTimeInMillis());
//                }else{
                Date dateMax = sdf.parse(maxDate);
                Date dateMin = sdf.parse(minDate);

                datePicker.setMinDate(dateMin.getTime());
                datePicker.setMaxDate(dateMax.getTime());
                Log.e("-----------","dateMax.getTime()=="+dateMax.getTime());
                Log.e("-----------","dateMin.getTime()=="+dateMin.getTime());
//                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            Log.e("-----------","dateMin.getTime()=="+maxDate);
        }
        init(datePicker, timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(this);

        ad = new AlertDialog.Builder(activity)
                .setTitle(initDateTime)
                .setView(dateTimeLayout)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        inputDate.setText(dateTime);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
//                        inputDate.setText("");
                    }
                }).show();

        onDateChanged(null, 0, 0, 0);
        return ad;
    }
    public AlertDialog dateTimePicKDialog30(final TextView inputDate, final String minDate, final String maxDate) {
        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.dialog_time_date_picker, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datePicker);
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timePicker);

        this.minDate=minDate;
        init30(datePicker, timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(this);




        Log.e("-----------","dateMin.1getTime()=="+minDate);
        ad = new AlertDialog.Builder(activity)
                .setTitle(initDateTime)
                .setView(dateTimeLayout)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        inputDate.setText(dateTime);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();

        Log.e("-----------","dateMin.2getTime()=="+minDate);
        onTimeChanged30(null, 0,0);

        Log.e("-----------","dateMin.3getTime()=="+minDate);
        return ad;
    }
    public AlertDialog dateTimePicKDialog(final TextView inputDate, final String maxDate) {
        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.dialog_time_date_picker, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datePicker);
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timePicker);


        if (!maxDate.equals("")) {
//            Log.e("-----------","maxDate====="+maxDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
//                stringToDate(maxDate,"yyyy-MM-dd HH:mm:ss");

//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
////                    Log.e("-----------","M====="+maxDate);
//                    Date dateMax = sdf.parse(maxDate);
//                    Date dateMin = sdf.parse(minDate);
//                    SimpleDateFormat sdf1= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
//                    String dateStrMax=sdf1.format(dateMax);
//                    String dateStrMin=sdf1.format(dateMin);
//                    Log.e("-----------","dateStrMax====="+dateStrMax);
//                    Log.e("-----------","dateStrMin====="+dateStrMin);
////                    Calendar c1 = Calendar.getInstance();
////                    c1.setTime(dateMax);
////                    c1.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
//////                    c1.add(Calendar.HOUR_OF_DAY,-8);
////                    datePicker.setMaxDate(c1.getTimeInMillis());
////                    Calendar c2 = Calendar.getInstance();
////                    c2.setTime(dateMin);
////                    c2.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
////                    c2.add(Calendar.HOUR_OF_DAY,-8);
////                    datePicker.setMinDate(c2.getTimeInMillis());
////                    Log.e("-----------","c1.getTimeInMillis()=="+c1.getTimeInMillis());
////                    Log.e("-----------","c2.getTimeInMillis()=="+c2.getTimeInMillis());
//                }else{
                Date dateMax = sdf.parse(maxDate);

                datePicker.setMaxDate(dateMax.getTime());
//                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            Log.e("-----------","dateMin.getTime()=="+maxDate);
        }
        init(datePicker, timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(this);
        ad = new AlertDialog.Builder(activity)
                .setTitle(initDateTime)
                .setView(dateTimeLayout)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        inputDate.setText(dateTime);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
//                        inputDate.setText("");
                    }
                }).show();

        onDateChanged(null, 0, 0, 0);

        return ad;
    }

    public AlertDialog dateTimePicKNoTimeDialog(final TextView inputDate, final String maxDate) {
        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.dialog_date_picker, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datePicker);


        if (!maxDate.equals("")) {
//            Log.e("-----------","maxDate====="+maxDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date dateMax = sdf.parse(maxDate);

                datePicker.setMaxDate(dateMax.getTime());
                Log.e("-----------","dateMax.getTime()=="+dateMax.getTime());
//                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            Log.e("-----------","dateMin.getTime()=="+maxDate);
        }
        initNoTime(datePicker);

        ad = new AlertDialog.Builder(activity)
                .setTitle(initDateTime)
                .setView(dateTimeLayout)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        inputDate.setText(dateTime);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
//                        inputDate.setText("");
                    }
                }).show();

        onDateNoTimeChanged(null, 0, 0, 0);
        return ad;
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        if (minDate.equals("")){
            onDateChanged30(null,0,0,0);
        }else{
        Log.e("-----------","dateMin.ttttttgetTime()=="+minDate);
        onDateChanged(null, 0, 0, 0);}
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

        if (minDate.equals("")){
        // 获得日历实例
        Calendar calendar = Calendar.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            calendar.set(datePicker.getYear(), datePicker.getMonth(),
                    datePicker.getDayOfMonth(), timePicker.getHour(),
                    timePicker.getMinute());
        } else {
            calendar.set(datePicker.getYear(), datePicker.getMonth(),
                    datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                    timePicker.getCurrentMinute());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Log.e("-----------","dateMin.ddddddgetTime()=="+minDate);
        dateTime = sdf.format(calendar.getTime());
        ad.setTitle(dateTime);}else{
            onDateChanged30(null,0,0,0);
        }
    }
    public void onTimeChanged30(TimePicker view, int hourOfDay, int minute) {

        Log.e("-----------","dateMin.4getTime()=="+minDate);
        onDateChanged30(null, 0, 0, 0);
    }
    public void onDateChanged30(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        // 获得日历实例
        Calendar calendar = Calendar.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            calendar.set(datePicker.getYear(), datePicker.getMonth(),
                    datePicker.getDayOfMonth(), timePicker.getHour(),
                    timePicker.getMinute()*30);
        } else {
            calendar.set(datePicker.getYear(), datePicker.getMonth(),
                    datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                    timePicker.getCurrentMinute()*30);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Log.e("-----------","dateMin.5getTime()=="+minDate);
        Log.e("----------","000000000calendar.getTime()====="+calendar.getTime().toString());
        dateTime = sdf.format(calendar.getTime());
        ad.setTitle(dateTime);
    }

    public void onDateNoTimeChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        // 获得日历实例
        Calendar calendar = Calendar.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            calendar.set(datePicker.getYear(), datePicker.getMonth(),
                    datePicker.getDayOfMonth(), timePicker.getHour(),
                    timePicker.getMinute());
        } else {
            calendar.set(datePicker.getYear(), datePicker.getMonth(),
                    datePicker.getDayOfMonth());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        dateTime = sdf.format(calendar.getTime());
        ad.setTitle(dateTime);
    }

    /**
     * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
     *
     * @param initDateTime 初始日期时间值 字符串型
     * @return Calendar
     */
    private Calendar getCalendarByInintData(String initDateTime) {
        Calendar calendar = Calendar.getInstance();

        Log.e("-----------","dateMin.8getTime()=="+minDate);
        // 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
        String date = spliteString(initDateTime, "日", "index", "front"); // 日期
        String time = spliteString(initDateTime, "日", "index", "back"); // 时间

        String yearStr = spliteString(date, "年", "index", "front"); // 年份
        String monthAndDay = spliteString(date, "年", "index", "back"); // 月日

        String monthStr = spliteString(monthAndDay, "月", "index", "front"); // 月
        String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日

        String hourStr = spliteString(time, ":", "index", "front"); // 时
        String minuteStr = spliteString(time, ":", "index", "back"); // 分

        int currentYear = Integer.valueOf(yearStr.trim()).intValue();
        int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
        int currentDay = Integer.valueOf(dayStr.trim()).intValue();
        int currentHour = Integer.valueOf(hourStr.trim()).intValue();
        int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

        calendar.set(currentYear, currentMonth, currentDay, currentHour,
                currentMinute);
        return calendar;
    }

    /**
     * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
     *
     * @param initDateTime 初始日期时间值 字符串型
     * @return Calendar
     */
    private Calendar getCalendarNoTimeByInintData(String initDateTime) {
        Calendar calendar = Calendar.getInstance();

        // 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
        String date = spliteString(initDateTime, "日", "index", "front"); // 日期

        String yearStr = spliteString(date, "年", "index", "front"); // 年份
        String monthAndDay = spliteString(date, "年", "index", "back"); // 月日

        String monthStr = spliteString(monthAndDay, "月", "index", "front"); // 月
        String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日


        int currentYear = Integer.valueOf(yearStr.trim()).intValue();
        int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
        int currentDay = Integer.valueOf(dayStr.trim()).intValue();

        calendar.set(currentYear, currentMonth, currentDay);
        return calendar;
    }


    /**
     * 截取子串
     *
     * @param srcStr      源串
     * @param pattern     匹配模式
     * @param indexOrLast
     * @param frontOrBack
     * @return
     */
    public static String spliteString(String srcStr, String pattern,
                                      String indexOrLast, String frontOrBack) {
        String result = "";
        int loc = -1;
        if (indexOrLast.equalsIgnoreCase("index")) {
            loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
        } else {
            loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
        }
        if (frontOrBack.equalsIgnoreCase("front")) {
            if (loc != -1)
                result = srcStr.substring(0, loc); // 截取子串
        } else {
            if (loc != -1)
                result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
        }
        return result;
    }

//    public static Date stringToDate(String strTime, String formatType)
//            throws ParseException {
//        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
//        Date date = null;
//        Log.e("-----------","strTime==="+strTime);
//        date = formatter.parse(strTime);
//        Log.e("-----------","date.time==="+date.getTime());
//        return date;
//    }
}