/**
 * 
 */
package com.example.administrator.yanfoxconn.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

//import com.amap.api.location.AMapLocation;

import android.text.TextUtils;

/**
 * 辅助工具类
 * @创建时间： 2015年11月24日 上午11:46:50
 * @项目名称： AMapLocationDemo2.x
 * @author hongming.wang
 * @文件名称: StringUtils.java
 * @类型名称: StringUtils
 */
public class StringUtils {


	/**
	 * 按條件截取字符串
	 * @param strMessqge 完整字符串
	 * @param cut 截取條件
	 * @return
	 */
	public static String getSubstr(String strMessqge , String cut){

		String a[] = strMessqge.split(cut);
		return a[0];
	}


}
