package com.example.administrator.yanfoxconn.utils;

import android.util.Log;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 中文金额转数字工具
 * @date 2019/7/19 13:44
 */
public class ChineseAmountUtil {

    private static char[] cnNum = new char[]{'零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'};
    private static char[] unitArr = new char[]{'厘', '分', '角', '圆', '拾', '佰', '仟', '万', '亿'};
    public static String NUMB = "零壹贰叁肆伍陆柒捌玖";
    public static String UNIT = "元拾佰仟万拾佰仟亿拾佰仟";
    /**
     * 中文金额转数字
     * @param chineseNumber 中文金额
     * @return
     */
    public static BigDecimal chinese2Number(String chineseNumber) {
        BigDecimal result = new BigDecimal(0);
        int lastUnitIndex = 0, num = 0;
        chineseNumber = chineseNumber.replace("元", "圆");
        for (int i = 0; i < chineseNumber.length(); i++) {
            boolean isUnit = true;
            char c = chineseNumber.charAt(i);
            for (int j = 0; j < cnNum.length; j++) {
                // 是数字
                if (c == cnNum[j]) {
                    // 数字值 = 索引
                    num = j;
                    isUnit = false;
                    break;
                }
            }
            if (isUnit) {
                // 第一个就是单位，如：拾伍万圆整
                if (i == 0) {
                    num = 1;
                }
                int unitIndex = getUnitIndex(c);
                BigDecimal unit = getUnit(c);
                if (unitIndex > lastUnitIndex) {
                    result = result.add(new BigDecimal(num)).multiply(unit);
                } else {
                    result = result.add(new BigDecimal(num).multiply(unit));
                }
                lastUnitIndex = unitIndex;
                num = 0;
            }
        }
        return result.setScale(2,BigDecimal.ROUND_DOWN);
    }

    private static int getUnitIndex(char c) {
        for (int j = 0; j < unitArr.length; j++) {
            if (c == unitArr[j]) {
                return j;
            }
        }
        return 0;
    }

    private static BigDecimal getUnit(char c) {
        double num = 0;
        int unitIndex = getUnitIndex(c);
        switch (unitIndex) {
            // '厘', '分', '角', '圆', '拾', '佰', '仟', '万', '亿'
            case 4:
                num = 10;
                break;
            case 5:
                num = 100;
                break;
            case 6:
                num = 1000;
                break;
            case 7:
                num = 10000;
                break;
            case 8:
                num = 100000000;
                break;
            case 3:
                num = 1;
                break;
            case 2:
                num = 0.1;
                break;
            case 1:
                num = 0.01;
                break;
            case 0:
                num = 0.001;
                break;
            default:
                break;
        }
        return new BigDecimal(num);
    }
    /**
     * 转换为小写（小数部分无效）
     * @param money 例如：壹仟贰佰叁拾肆万伍仟陆佰柒拾捌元
     * @return
     */
    public static String toNumber(String money){
        double ret = 0, temp = 0;
        int nowNumb = 0,tInt = 0, i = 0, j = 0;
        while (i < money.length()) {
            tInt = NUMB.indexOf(money.charAt(i));
            if(tInt>-1){//数值情况
                nowNumb = tInt;
                if(tInt>0){//非零情况，零直接跳过，无须处理
                    j = UNIT.indexOf(money.charAt(i+1));
                    if(j>0 && j<4){//单位为 <拾佰仟> 的情况
                        temp += nowNumb * Math.pow(10, j);
                        i++;//下次循环跳过刚刚读过的 <拾佰仟> 单位
                    }else{//单位为其他情况的，直接把结果暂存到 temp
                        temp += nowNumb;
                    }
                }
            }else{//非数值情况，将暂存temp根据单位存到ret中，并清空暂存temp
                if(money.charAt(i)=='亿'){
                    ret += temp * 10000 * 10000;
                    temp = 0;
                } else if(money.charAt(i)=='万'){
                    ret += temp * 10000;
                    temp = 0;
                } else if(money.charAt(i)=='元') {
                    ret += temp;
                    temp = 0;
                }
            }
            i++;
        }
        DecimalFormat df = new DecimalFormat("########0.00");
        return "" + df.format(ret);
    }

    public static void main(String args[]) {
        String s1 = "壹万伍仟肆佰壹拾圆叁角伍分肆厘";
        String s2 = "捌万陆仟肆佰壹拾圆整";
        String s3 = "壹万伍仟肆佰壹拾元贰角捌分肆厘";
        String s4 = "拾壹亿壹仟万伍仟肆佰壹拾元贰角捌分肆厘";
        String s5 = "拾伍万圆";
        Log.e("s1", chinese2Number(s1)+"");

    }

}

