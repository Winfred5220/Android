package com.example.administrator.yanfoxconn.utils;

import com.example.administrator.yanfoxconn.activity.ExListViewActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeDateUtils {

    private static SimpleDateFormat formatters = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static int day;//相差天數
    private static int hour;//相差小時數

    public static int daysDeviation(String dateStart, String dateEnd) {//相差天數

        try {
            Date startDate = formatters.parse(dateStart);
            Date endDate = formatters.parse(dateEnd);

            long diff = endDate.getTime() - startDate.getTime();//这样得到的差值是微秒级别

            long days = diff / (1000 * 60 * 60 * 24);

//            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
//
//            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
//
//            System.out.println(""+days+"天"+hours+"小时"+minutes+"分");

            day = (int) days;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return day;
        }
    }

    public static int hoursDeviation(String dateStart, String dateEnd) {//相差小時數

        try {
            Date startDate = formatters.parse(dateStart);
            Date endDate = formatters.parse(dateEnd);

            long diff = endDate.getTime() - startDate.getTime();//这样得到的差值是微秒级别

//            long days = diff / (1000 * 60 * 60 * 24);
//
//            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
//
//            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
//
//            System.out.println(""+days+"天"+hours+"小时"+minutes+"分");
            long hours = diff / (1000 * 60 * 60);
            hour = (int) hours;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return hour;
        }
    }

    /**
     *
     * @param strH   開始時間的 小時
     * @param strM   開始時間的 分鐘
     * @param endH   結束時間的 小時
     * @param endM   結束時間的 分鐘
     * @return
     */
    public static boolean isContain(int strH,int strM,int endH,int endM){
        Boolean timeRight = false;
        SimpleDateFormat formatters = new SimpleDateFormat("HH:mm");
        Date curDates = new Date(System.currentTimeMillis());// 获取当前时间
        String strs = formatters.format(curDates);
//            System.out.println(strs);
        //开始时间
        int sth = strH;//小时
        int stm = strM;//分
        //结束时间
        int eth = endH;//小时
        int etm = endM;//分

        String[] dds = new String[]{};

        // 分取系统时间 小时分
        dds = strs.split(":");
        int dhs = Integer.parseInt(dds[0]);
        int dms = Integer.parseInt(dds[1]);

        if (sth <= dhs && dhs <= eth) {
            if (sth == dhs && stm <= dms) {
//                    System.out.println("在外围内");
                timeRight = true;
            } else if (dhs == eth && etm >= dms) {
//                    System.out.println("在外围内");
                timeRight = true;
            } else if (sth != dhs && dhs != eth) {
//                    System.out.println("在外围內");
                timeRight = true;
            }
        }

        return timeRight;
    }
}
