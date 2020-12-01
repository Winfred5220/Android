//package com.example.administrator.yanfoxconn.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.drawable.ColorDrawable;
//import android.graphics.drawable.Drawable;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.location.LocationProvider;
//import android.os.Bundle;
//import android.oVERSION.SDKs.Handler;
//import android.os.Message;
//import android.provider.Settings;
//import android.util.Log;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.GridView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.location.AMapLocationClientOption;
//import com.amap.api.location.AMapLocationListener;
//import com.example.administrator.yanfoxconn.R;
//import com.example.administrator.yanfoxconn.adapter.TypesGridAdapter;
//import com.example.administrator.yanfoxconn.bean.RouteMessage;
//import com.example.administrator.yanfoxconn.constant.Constants;
//import com.example.administrator.yanfoxconn.constant.FoxContext;
//import com.example.administrator.yanfoxconn.utils.BaseActivity;
//import com.example.administrator.yanfoxconn.utils.HttpUtils;
//import com.example.administrator.yanfoxconn.utils.ToastUtils;
//import com.example.administrator.yanfoxconn.utils.StringUtils;
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//
//import static java.security.AccessController.getContext;
//
//
///**
// * 主页
// */
//public class MainActivityGao extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
//
//    private final int MESSAGE_GPS = 1;//判斷GPS
//    private final int MESSAGE_TOAST = 2;//showToast
//
//    @InjectView(R.id.tv_title)
//    TextView tvTitle;//標題
//    @InjectView(R.id.btn_title_left)
//    Button btnLogout;//退出登錄
//    @InjectView(R.id.gv_type_list)
//    GridView gvTypes;//巡檢類型列表
//
//    private String role;//巡檢類型
//    private String url;//請求地址
//    private String result;//返回結果
//
//    private List<RouteMessage> routeMessageList;//巡檢點信息列表
//    private List<String> roleList = new ArrayList<>();//巡檢類型列表
//    private TypesGridAdapter typesAdapter;
//
//    private LocationManager lm;
//
//    private AMapLocationClient locationClient = null;
//    private AMapLocationClientOption locationOption = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        WindowManager wm = this.getWindowManager();
//
//        int width = wm.getDefaultDisplay().getWidth();
//        int height = wm.getDefaultDisplay().getHeight();
//        Log.e("------", "MainActivityGao==width=="+width+"==height=="+height);
//        ButterKnife.inject(this);
//        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        //判断GPS是否正常启动
//        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
//            //返回开启GPS导航设置界面
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivityForResult(intent, 0);
//            return;
//        }
//
//        //初始化定位
//        initLocation();
//        startLocation();
//
//        init();
//    }
//
//    /**
//     * 初始化控件
//     */
//    private void init() {
//        tvTitle.setText("富士康系統");
//        btnLogout.setText("退出登錄");
//        btnLogout.setVisibility(View.VISIBLE);
//        btnLogout.setOnClickListener(this);
//
//        role = FoxContext.getInstance().getRoles();
//
//        String spStr[] = role.split(",");
//        for (int i = 0; i < spStr.length; i++) {
//            roleList.add(i, spStr[i]);
//        }
//        typesAdapter = new TypesGridAdapter(MainActivityGao.this, roleList);
//        gvTypes.setAdapter(typesAdapter);
//
//
//
//        typesAdapter.setOnClickListener(new TypesGridAdapter.OnClickListener() {
//            @Override
//            public void OnClickListenerFalse(int position) {
//                Log.e("-------", "沒有權限");
//                ToastUtils.showLong(MainActivityGao.this, "您沒有該巡檢權限,請確認!");
//            }
//
//            @Override
//            public void OnClickListenerTrue(int position) {
//                Log.e("-------", "有權限");
//
//                getRouteList(FoxContext.getInstance().getTypes()[position]);
//            }
//
//            @Override
//            public void OnClickWebView(int position) {
//                Intent intent = new Intent(MainActivityGao.this,WebViewActivity.class);
//                intent.putExtra("name",FoxContext.getInstance().getTypesName()[position]);
//                startActivity(intent);
//            }
//        });
//    }
//
//    /**
//     * 初始化定位
//     *
//     * @author hongming.wang
//     * @since 2.8.0
//     */
//    private void initLocation() {
//        //初始化client
//        locationClient = new AMapLocationClient(this.getApplicationContext());
//        locationOption = getDefaultOption();
//        //设置定位参数
//        locationClient.setLocationOption(locationOption);
//        // 设置定位监听
//        locationClient.setLocationListener(locationListener);
//        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//    }
//
//    /**
//     * 默认的定位参数
//     *
//     * @author hongming.wang
//     * @since 2.8.0
//     */
//    private AMapLocationClientOption getDefaultOption() {
//        AMapLocationClientOption mOption = new AMapLocationClientOption();
//        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
//        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
//        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
//        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
//        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
//        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
//        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
//        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
//        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
//        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
//        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
//        return mOption;
//    }
//
//    /**
//     * 定位监听
//     */
//    AMapLocationListener locationListener = new AMapLocationListener() {
//        @Override
//        public void onLocationChanged(AMapLocation location) {
//            if (null != location) {
//
//                StringBuffer sb = new StringBuffer();
//                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
//                if (location.getErrorCode() == 0) {
//                    sb.append("定位成功" + "\n");
//                    sb.append("定位类型: " + location.getLocationType() + "\n");
//                    sb.append("经    度    : " + location.getLongitude() + "\n");
//                    sb.append("纬    度    : " + location.getLatitude() + "\n");
//                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
//                    sb.append("提供者    : " + location.getProvider() + "\n");
//
//                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
//                    sb.append("角    度    : " + location.getBearing() + "\n");
//                    // 获取当前提供定位服务的卫星个数
//                    sb.append("星    数    : " + location.getSatellites() + "\n");
//                    sb.append("国    家    : " + location.getCountry() + "\n");
//                    sb.append("省            : " + location.getProvince() + "\n");
//                    sb.append("市            : " + location.getCity() + "\n");
//                    sb.append("城市编码 : " + location.getCityCode() + "\n");
//                    sb.append("区            : " + location.getDistrict() + "\n");
//                    sb.append("区域 码   : " + location.getAdCode() + "\n");
//                    sb.append("地    址    : " + location.getAddress() + "\n");
//                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
//                    //定位完成的时间
//                    sb.append("定位时间: " + StringUtils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
//                } else {
//                    //定位失败
//                    sb.append("定位失败" + "\n");
//                    sb.append("错误码:" + location.getErrorCode() + "\n");
//                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
//                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
//                }
//                //定位之后的回调时间
//                sb.append("回调时间: " + StringUtils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
//
//                //解析定位结果，
//                String result = sb.toString();
////                tvResult.setText(result);
//                FoxContext.getInstance().setLocation(location);
//                Log.e("----gaode", location.getLongitude() + "==" + location.getLatitude());
//            } else {
////                tvResult.setText("定位失败，loc is null");
//            }
//        }
//    };
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btn_title_left://退出登錄
//
//                Intent toLogin = new Intent(MainActivityGao.this, LoginActivity.class);
//                FoxContext.getInstance().setType("");
//                FoxContext.getInstance().setName("");
//                FoxContext.getInstance().setLoginId("");
//                startActivity(toLogin);
//                finish();
//                break;
//        }
//    }
//
//    private void getRouteList(String type) {
//        showDialog();
//        url = Constants.HTTP_DIMEMSION_SERVLET + "?type=" + type;
//        Log.e("-------", "type==" + type);
//
//        FoxContext.getInstance().setType(type);
//        new Thread() {
//            @Override
//            public void run() {
//                //把网络访问的代码放在这里
//                result = HttpUtils.queryStringForPost(url);
//
//                Log.e("---------", "==fff===" + url);
//                Gson gson = new Gson();
//                String response = result;
//                dismissDialog();
//                if (result != null) {
//                    Log.e("---------", "result==fff===" + response);
//                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
//                    String errCode = jsonObject.get("errCode").getAsString();
//                    if (errCode.equals("200")) {
//
//                        JsonArray array = jsonObject.get("data").getAsJsonArray();
//                        routeMessageList = new ArrayList<RouteMessage>();
//                        for (JsonElement type : array) {
//                            RouteMessage humi = gson.fromJson(type, RouteMessage.class);
//                            routeMessageList.add(humi);
//                        }
//                        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
////
//                            mHandler.sendEmptyMessage(0);
//                        } else {
//                            Intent intent = new Intent(getApplication(), RouteListActivity.class);
//                            intent.putExtra("routeList", (Serializable) routeMessageList);
//                            startActivity(intent);
//                        }
//
//                    } else {
//                        Message message = new Message();
//                        message.what = MESSAGE_TOAST;
//                        message.obj = jsonObject.get("errMessage").getAsString();
//                        mHandler.sendMessage(message);
////                        ToastUtils.showShort(MainActivityGao.this, jsonObject.get("errMessage").getAsString());
//                    }
//                } else {
//                    Message message = new Message();
//                    message.what = MESSAGE_TOAST;
//                    message.obj = "無數據";
//                    mHandler.sendMessage(message);
//                }
//            }
//        }.start();
//    }
//
//    Handler mHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MESSAGE_TOAST://Toast彈出
//                    if (msg.obj.equals("失敗")) {
//                        ToastUtils.showShort(MainActivityGao.this, "沒有巡檢數據!");
//                    } else {
//                        ToastUtils.showShort(MainActivityGao.this, msg.obj.toString());
//                    }
//                    break;
//                case  MESSAGE_GPS:
//
//                    ToastUtils.showShort(MainActivityGao.this, "请开启GPS导航...");
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };
//
//    @Override
//    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//    }
//
//    /**
//     * 开始定位
//     *
//     * @author hongming.wang
//     * @since 2.8.0
//     */
//    private void startLocation() {
//        //根据控件的选择，重新设置定位参数
////        resetOption();
//        // 设置定位参数
//        locationClient.setLocationOption(locationOption);
//        // 启动定位
//        locationClient.startLocation();
//    }
//
//    /**
//     * 停止定位
//     *
//     * @author hongming.wang
//     * @since 2.8.0
//     */
//    private void stopLocation() {
//        // 停止定位
//        locationClient.stopLocation();
//    }
//
//    /**
//     * 销毁定位
//     *
//     * @author hongming.wang
//     * @since 2.8.0
//     */
//    private void destroyLocation() {
//        if (null != locationClient) {
//            /**
//             * 如果AMapLocationClient是在当前Activity实例化的，
//             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
//             */
//            locationClient.onDestroy();
//            locationClient = null;
//            locationOption = null;
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.e("-----onResume", "MainActivityGaoonResume");
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        init();
//        Log.e("-----onRestart", "MainActivityGaoonRestart");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        destroyLocation();
//        Log.e("---------", "onDestroy");
//    }
//
//}
