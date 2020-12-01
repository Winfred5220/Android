package com.example.administrator.yanfoxconn.constant;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.text.TextUtils;

//import com.baidu.mapapi.SDKInitializer;

import com.example.administrator.yanfoxconn.JPush.Logger;
import com.example.administrator.yanfoxconn.utils.LocationService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import androidx.multidex.MultiDex;
import cn.jpush.android.api.JPushInterface;


/**
 * Created by  on 15-4-19.
 */
public class FoxApplication extends Application {
    public com.example.administrator.yanfoxconn.utils.LocationService locationService;
    public Vibrator mVibrator;

    private static final String TAG = "JIGUANG-Example";
    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d(TAG, "[FoxApplication] onCreate");

        FoxContext.init(this);
        init();
        /***
         * 初始化定位sdk，建议在Application中创建
         * * 主Application，所有百度定位SDK的接口说明请参考线上文档：http://developer.baidu.com/map/loc_refer/index.html
         *
         * 百度定位SDK官方网站：http://developer.baidu.com/map/index.php?title=android-locsdk
         *
         * 直接拷贝com.baidu.location.service包到自己的工程下，简单配置即可获取定位结果，也可以根据demo内容自行封装
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
//        SDKInitializer.initialize(getApplicationContext());
        //if (BuildConfig.DEBUG) {
//            CrashHandler crashHandler = CrashHandler.getInstance();
//            crashHandler.init(getApplicationContext());
//            VolleyLog.DEBUG = BuildConfig.DEBUG;
//       // }
//        LeakCanary.install(this);
//        ZXingLibrary.initDisplayOpinion(this);

        //極光
        //初始化SDK
//        JPushInterface.setDebugMode(true);//正式版的時候設置false,關閉調試
//        JPushInterface.init(this);
//        //建議添加tag標籤,發送消息的之後就可以指定tag標籤來發送了
//        Set set = new HashSet<>();
//        set.add("AndroidTest");//名字任意,可多添加幾個
//        JPushInterface.setTags(this,set,null);//設置標籤
//        //開始推送....
//        JPushInterface.resumePush(this);
//        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
//        JPushInterface.init(this);     		// 初始化 JPush
    }

    public void init() {
//        String sessionId = getSharedPreferences(Constants.SETTING, MODE_PRIVATE).getString(
//                Constants.SESSIONID, "");
//        if (!TextUtils.isEmpty(sessionId)) {
//            onLogin(sessionId);
//        }
        closeAndroid10Dialog();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public void onLogin(String newSessionId) {
        if (TextUtils.isEmpty(newSessionId)) {
            return;
        }
//        WingaContext.getInstance().setLogin(true);
//        WingaContext.getInstance().setUserName(getSharedPreferences(Constants.SETTING, MODE_PRIVATE).getString(Constants.USER_NAME, ""));
//        WingaContext.getInstance().setNickName(getSharedPreferences(Constants.SETTING, MODE_PRIVATE).getString(Constants.NICK_NAME, ""));
//        WingaContext.getInstance().setSessionId(getSharedPreferences(Constants.SETTING, MODE_PRIVATE).getString(Constants.SESSIONID, ""));
//        WingaContext.getInstance().setPassWord(getSharedPreferences(Constants.SETTING, MODE_PRIVATE).getString(Constants.PASS_WORD, ""));
//        WingaContext.getInstance().setAvatarUrl(getSharedPreferences(Constants.SETTING, MODE_PRIVATE).getString(Constants.AVATAR_URL, ""));
//        WingaContext.getInstance().setIsRemind(getSharedPreferences(Constants.SETTING, MODE_PRIVATE).getBoolean(Constants.MESSAGEREMIND, true));
//        WingaContext.getInstance().setDeleteEvent(getSharedPreferences(Constants.SETTING, MODE_PRIVATE).getBoolean(Constants.DELETEEVENT, true));
//        WingaContext.getInstance().setDisplayAll(getSharedPreferences(Constants.SETTING, MODE_PRIVATE).getBoolean(Constants.DISPLAY_ALL, true));
//        WingaContext.getInstance().setSeatDesk(getSharedPreferences(Constants.SETTING, MODE_PRIVATE).getBoolean(Constants.SEAT_DESK, true));
//        WingaContext.getInstance().setUserAuthority(getSharedPreferences(Constants.SETTING, MODE_PRIVATE).getInt(Constants.USER_AUTHORITY, 0));
    }

    public void closeAndroid10Dialog(){
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
