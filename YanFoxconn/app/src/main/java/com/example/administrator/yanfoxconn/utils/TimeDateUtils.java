package com.example.administrator.yanfoxconn.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeDateUtils {

    private static SimpleDateFormat formatters = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static int day;//相差天數
    private static int hour;//相差小時數
    //相差天數
    public static int daysDeviation(String dateStart, String dateEnd) {

        try {
            Date startDate = formatters.parse(dateStart);
            Date endDate = formatters.parse(dateEnd);
            //这样得到的差值是微秒级别
            long diff = endDate.getTime() - startDate.getTime();

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
    //相差小時數
    public static int hoursDeviation(String dateStart, String dateEnd) {

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
    //相差分鐘數
    public static int minutesDeviation(String dateStart, String dateEnd) {

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
            long hours = diff / (1000 * 60 );
            hour = (int) hours;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return hour;
        }
    }
    //相差秒數
    public static int secondDeviation(String dateStart, String dateEnd) {

        try {
            Date startDate = formatters.parse(dateStart);
            Date endDate = formatters.parse(dateEnd);

            long diff = endDate.getTime() - startDate.getTime();//这样得到的差值是微秒级别

//          long days = diff / (1000 * 60 * 60 * 24);
//          long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
//          long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
//          System.out.println(""+days+"天"+hours+"小时"+minutes+"分");

            long hours = diff / (1000);
            hour = (int) hours;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return hour;
        }
    }

    /**
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

    /**
     * 判断时间大小:小于和等于为true
     * @param startTime
     * @param endTime
     * @return
     */
    public static Boolean getTimeLessEqual (String startTime, String endTime) throws ParseException {
Boolean t =null;
        Log.e("------",startTime+"===="+endTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");//时-分

            Date date1 = dateFormat.parse(startTime);//开始时间
            Date date2 = dateFormat.parse(endTime);//结束时间

        Log.e("------",date1.toString()+"==Date=="+date2.toString());
            if (date1.getTime()<date2.getTime()){
                t = true;
            }else if (date1.getTime()==date2.getTime()){
                t = true;
            }else {
                t = false;
            }

        return t;
    }
    /**
     * 判断时间大小:大于为true
     * @param startTime
     * @param endTime
     * @return
     */
    public static Boolean getTimeGreater (String startTime, String endTime){

        Log.e("------",startTime+"===="+startTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");// 时-分
        try {
            Date date1 = dateFormat.parse(startTime);//开始时间
            Date date2 = dateFormat.parse(endTime);//结束时间
            // 1 结束时间小于开始时间 2 开始时间与结束时间相同 3 结束时间大于开始时间
            if (date1.getTime()>date2.getTime()){
                return true;
            }else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 计算相差小时数
     * @param dateStart
     * @param dateEnd
     * @return
     */
    private static float hou;
    public static float getTimeHours(String dateStart, String dateEnd) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");// 时-分
        try {
            Date startDate = dateFormat.parse(dateStart);
            Date endDate = dateFormat.parse(dateEnd);
            float diff = endDate.getTime() - startDate.getTime();//这样得到的差值是微秒级别
            float hours = diff / (1000 * 60 * 60);
            hou = (float) hours;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            return hou;
        }
    }
}
