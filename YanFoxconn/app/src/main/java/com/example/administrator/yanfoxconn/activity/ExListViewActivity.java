package com.example.administrator.yanfoxconn.activity;


import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.MyExpandableListViewAdapter;
import com.example.administrator.yanfoxconn.bean.CarListMessage;
import com.example.administrator.yanfoxconn.bean.CarLogReturnM;
import com.example.administrator.yanfoxconn.bean.CyCarMessage;
import com.example.administrator.yanfoxconn.bean.DimemsionMenu;
import com.example.administrator.yanfoxconn.bean.EventMessage;
import com.example.administrator.yanfoxconn.bean.RouteMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxApplication;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.LocationService;
import com.example.administrator.yanfoxconn.utils.MyLocationListener;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.utils.WatermarkUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * 主界面 二級菜單
 * Created by song on 2017/11/17.
 */

public class ExListViewActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_GPS = 1;//判斷GPS
    private final int MESSAGE_TOAST = 2;//showToast

    private String url;//請求地址
    private String result;//返回結果
    private List<RouteMessage> routeMessageList;//巡檢點信息列表
    private List<CarLogReturnM> carLogReturnMList;//退件信息
    private List<DimemsionMenu> dimemsionMenuList;//餐飲區菜單
    private List<CarListMessage> carListMessages;//碼頭放行車輛信息
    private List<EventMessage> eventMessageList;//人資活動表
    private List<CyCarMessage> cyCarMessages;//物流消殺車輛

    private ExpandableListView expandableListView;
    private TextView tvTitle;//標題
    private Button btnBack;//返回

    private String initStartDateTime; // 初始化开始时间
    private Date startDate = null;//開始時間
    private Date endDate = null;//結束時間
    private Date curDate = null;//當前時間
    private SimpleDateFormat formatter;
    private Boolean timeRight = false;

//    private LocationManager lm;
//    private AMapLocationClient locationClient = null;
//    private AMapLocationClientOption locationOption = null;

    public String[] group = {"物流防疫", "安全保障服務部", "工業安全部", "總務", "營建", "產品處", "碼頭物流", "車輛服務", "海關協管", "煙台工會", "人力資源部","華北商務","品質保證處","越南廠"}; //"NME安全部", "VIP安全部", "PME安全部",
    public String[][] gridViewChild = {
            {"消殺點檢"},//物流防疫
            {"安保部值星", "春安值星", "一大隊固定崗", "一大隊巡邏崗", "二大隊固定崗", "二大隊巡邏崗", "三大隊固定崗", "三大隊巡邏崗", "機動隊巡邏崗", "機動隊固定崗", "常用表單", "物品放行", "移動設備管控", "廢料出廠", "值班課長", "三防隱患通報"},//安全保障服務部
            {"工安巡檢","洗眼器點檢","危化品暫存柜","危化品暫存倉","吸煙區","鋰電池防火","有限空間"},//工業安全部
            {"宿舍巡檢", "餐飲巡檢", "消殺巡檢", "宿舍查驗","直飲水","生活垃圾清運","餐廳巡檢","臨時工簽到"},//總務
            {"A區巡檢", "E區巡檢", "C區巡檢", "D/G區巡檢", "維保巡檢","工程項目點檢","空調防疫點檢","配電箱點檢","路燈射燈點檢"},//營建
            {"NME", "VIP", "PME", "EBL", "PWB","HEC"},//產品處
            {"碼頭網站", "出口碼頭", "HUB倉", "碼頭巡檢"},//碼頭物流
            {"候車亭巡檢", "叉車廠商巡檢","車輛巡檢","車調車輛巡檢","叉車球車巡檢"},//車調服務
            {"人工跨區", "跨區申請單", "車輛跨區"},//海關協管
            {"餐監會"},//煙台工會
            {"活動生成", "活動簽到", "退訓放行", "班導志","人資監餐","教室點檢"},//人力資源部
            {"販賣機巡檢","流動攤位巡檢","門市房巡檢"},//華北商務
            {"QA","SMT","ME"},//品質保證處
            {"越南巡更"}
    };
    public String[][] gridViewType = {
            {"CY"},//物流防疫
            {"A0", "T0", "H0", "K0", "I0", "L0", "J0", "M0", "R0", "S0", "CD", "CE", "CL", "CR", "CU", "CX"},//安保部
            {"CT","FE","FF","FG","FV","FX","FZ"},//工業安全部
            {"BG", "BP", "ZXS", "DN","DQ","DT","FH","GA"},//總務
            {"N0", "O0", "P0", "Q0", "V0","DR","FS","FW","GD"},//營建
            {"NME", "VIP", "PME", "EBL", "PWB","HEC"},//產品處
            {"F0", "E0", "CI", "EC"},//碼頭物流
            {"BS", "CK", "EA","FQ","BV"},//車調服務
            {"BK", "BT", "CQ"},//海關協管
            {"W0"},//煙台工會
            {"X0", "Y0", "DO", "DP","DZ","ED"},//人力資源部
            {"DS","FT","FU"},//華北商務
            {"QAQ","SMT","MEM"},//品質保證處
            {"GB"}//越南巡更
    };
    public int[][] img = {
            {R.mipmap.icon_xiaosha},//物流防疫
            {R.mipmap.icon_anquan, R.mipmap.icon_lianluo, R.mipmap.icon_yidadui1, R.mipmap.icon_yidadui_1,
                    R.mipmap.icon_yidadui2, R.mipmap.icon_yidadui_2, R.mipmap.icon_yidadui3, R.mipmap.icon_yidadui_3,
                    R.mipmap.icon_jidongx, R.mipmap.icon_jidongg, R.mipmap.icon_biaodan, R.mipmap.icon_wupin,
                    R.mipmap.icon_mobile, R.mipmap.icon_scrap_leave, R.mipmap.icon_duty, R.mipmap.icon_sanfang},//安保部
            {R.mipmap.icon_gongan, R.mipmap.icon_control_room, R.mipmap.icon_control_room, R.mipmap.icon_control_room,
                    R.mipmap.icon_xiyanqu, R.mipmap.icon_control_room, R.mipmap.icon_control_room},//工業安全部
            {R.mipmap.icon_sushe, R.mipmap.icon_canyin, R.mipmap.icon_xiaosha, R.mipmap.icon_control_room,
                    R.mipmap.icon_zhiyinshui,R.mipmap.icon_laji, R.mipmap.icon_canyin, R.mipmap.icon_control_room},//總務
            {R.mipmap.icon_yingjian, R.mipmap.icon_yingjian, R.mipmap.icon_yingjian, R.mipmap.icon_yingjian,
                    R.mipmap.icon_yingjian, R.mipmap.icon_yingjian, R.mipmap.icon_kongtiao, R.mipmap.icon_yingjian, R.mipmap.icon_yingjian},//營建
            {R.mipmap.icon_nme, R.mipmap.icon_vip, R.mipmap.icon_pme, R.mipmap.icon_ebl, R.mipmap.icon_pwb1,
                    R.mipmap.icon_hec},//產品處
            {R.mipmap.icon_chuwang, R.mipmap.icon_chukou, R.mipmap.icon_hub,R.mipmap.icon_kuaqusao},//碼頭物流
            {R.mipmap.icon_ting, R.mipmap.icon_forklift,R.mipmap.icon_cheliang, R.mipmap.icon_cross_car,
                    R.mipmap.icon_forklift},//車調服務
            {R.mipmap.icon_kuaqusao, R.mipmap.icon_kuaqushen, R.mipmap.icon_cross_car},//海關協管
            {R.mipmap.icon_shijian},//煙台工會
            {R.mipmap.icon_huodong, R.mipmap.icon_qiandao, R.mipmap.icon_retreat, R.mipmap.icon_bandao,
                    R.mipmap.icon_jiancan, R.mipmap.icon_jiaoshi},//人力資源部
            {R.mipmap.icon_fanmaiji,R.mipmap.icon_tanwei,R.mipmap.icon_menshifang},//華北商務
            {R.mipmap.icon_pzbzc,R.mipmap.icon_pzbzc,R.mipmap.icon_pzbzc},//品質保證處
            {R.mipmap.icon_pzbzc},//越南
    };

    /**
     * 每个分组的名字的集合
     */
    private List<String> groupList;

    /**
     * 每个分组下的每个子项的 GridView 数据集合
     */
    private List<String> itemGridList;

    /**
     * 所有分组的所有子项的 GridView 数据集合
     */
    private List<List<String>> itemList;

    /**
     * 所有分組的所有子項的GridView數據集合,角色分類
     */
    private List<List<String>> itemTypeList;
    private String roles;//當前用戶權限
    private List<String> rolesList;//當前用戶權限

    /**
     * 所有分組的所有子項的GridView數據集合,圖片顯示
     */
    private List<Integer> itemGridImgList;
    private List<List<Integer>> itemImgList;

    private boolean isShowReturn = false;//是否顯示退件按鈕
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private LocationService locationService;
    private LocationManager locManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex);

        //加水印
//        WatermarkUtil.getInstance().show(this, FoxContext.getInstance().getLoginId()+"\n"+FoxContext.getInstance().getName());
        // 可以自定义水印文字颜色、大小和旋转角度
//         WatermarkUtil.getInstance()
//                .setText("Fantasy BlogDemo")
//                .setTextColor(0xAE000000)
//                .setTextSize(16)
//                .setRotation(-30)
//                .show(this);

        //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
        //原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //注册监听函数
//        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (FoxContext.getInstance().getRoles().equals("G0")) {

        } else {

            //判断GPS是否正常启动
            if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
                //返回开启GPS导航设置界面
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 0);
//            return;
            }
//            locationService.start();// 定位SDK
            //初始化定位
//            initLocation();
//            startLocation();
        }


        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btn_title_left);

        tvTitle.setText("安全巡檢");
        btnBack.setText("退出登錄");
        btnBack.setOnClickListener(this);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableList);

        setExListView();
    }


    /**
     * 設置二級菜單的數據,以及顯示方式
     */
    private void setExListView() {

        roles = FoxContext.getInstance().getRoles() + ",NME,VIP,PME,EBL,PWB,ZXS,GAN,HEC,QAQ,SMT,MEM";//ZXS:總務消殺巡檢 GAN:工安巡檢
        rolesList = new ArrayList<String>();
        String spStr[] = roles.split(",");
        for (int i = 0; i < spStr.length; i++) {
            rolesList.add(i, spStr[i]);
        }

        // 分组
        groupList = new ArrayList<>();
        for (int i = 0; i < group.length; i++) {
            groupList.add(group[i]);
        }

        // 所有分组的所有子项的 GridView 数据集合
        itemList = new ArrayList<>();
        for (int i = 0; i < gridViewChild.length; i++) {
            // 每个分组下的每个子项的 GridView 数据集合
            itemGridList = new ArrayList<>();
            for (int j = 0; j < gridViewChild[i].length; j++) {
                itemGridList.add(gridViewChild[i][j]);
            }
            itemList.add(i, itemGridList);
        }

        itemTypeList = new ArrayList<>();
        for (int i = 0; i < gridViewType.length; i++) {
            // 每个分组下的每个子项的 GridView 数据集合
            itemGridList = new ArrayList<>();
            for (int j = 0; j < gridViewType[i].length; j++) {
                itemGridList.add(gridViewType[i][j]);
            }
            itemTypeList.add(i, itemGridList);
        }

        itemImgList = new ArrayList<>();
        for (int i = 0; i < img.length; i++) {
            // 每个分组下的每个子项的 GridView 数据集合
            itemGridImgList = new ArrayList<>();
            for (int j = 0; j < img[i].length; j++) {
                itemGridImgList.add(img[i][j]);
            }
            itemImgList.add(i, itemGridImgList);
        }

        // 创建适配器
        MyExpandableListViewAdapter adapter = new MyExpandableListViewAdapter(ExListViewActivity.this,
                groupList, itemList, itemTypeList, rolesList, itemImgList);
        expandableListView.setAdapter(adapter);
        // 隐藏分组指示器
        expandableListView.setGroupIndicator(null);
        // 默认展开第一组
        expandableListView.expandGroup(FoxContext.getInstance().getGroupPosition());
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < group.length; i++) {
                    if (groupPosition != i) {
                        Log.e("--------ex", "d==" + groupPosition);
                        expandableListView.collapseGroup(i);
                        FoxContext.getInstance().setGroupPosition(groupPosition);
                    }
                }
            }

        });
    }

    /**
     * 根據角色權限,獲取巡檢路線列表
     *
     * @param type
     */
    public void getRouteList(String type) {
        if (type.equals("W0")) {//食監會   //營建|| type.equals("N0") || type.equals("O0") || type.equals("P0") || type.equals("Q0") || type.equals("V0")
            FoxContext.getInstance().setTakePic("W0");//及時拍照
        }

        if (type.equals("BS")) {//候車亭時間管控

            SimpleDateFormat formatters = new SimpleDateFormat("HH:mm");
            Date curDates = new Date(System.currentTimeMillis());// 获取当前时间
            String strs = formatters.format(curDates);
//            System.out.println(strs);
            //开始时间
            int sth = 8;//小时
            int stm = 30;//分
            //结束时间
            int eth = 16;//小时
            int etm = 00;//分

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


            if (!timeRight) {
                ToastUtils.showLong(ExListViewActivity.this, "巡檢時間：8:30-16:00");
                return;
            }
        }

        showDialog();
        url = Constants.HTTP_DIMEMSION_SERVLET + "?type=" + type;

        FoxContext.getInstance().setType(type);
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                String response = result;

                if (result != null) {
                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        if (!array.toString().equals("")) {
                            routeMessageList = new ArrayList<RouteMessage>();
                            for (JsonElement type : array) {
                                RouteMessage humi = gson.fromJson(type, RouteMessage.class);
                                routeMessageList.add(humi);
                            }
                            if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//
                                mHandler.sendEmptyMessage(0);
                            } else {
                                Intent intent = new Intent(getApplication(), RouteListActivity.class);
                                intent.putExtra("routeList", (Serializable) routeMessageList);
                                intent.putExtra("Dflag", "");//用於判定是否為宿舍巡檢,以方便按樓棟傳值,其他地方值為空
                                startActivity(intent);
                            }
                        } else {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = "無數據";
                            mHandler.sendMessage(message);
                        }

                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
//                        ToastUtils.showShort(MainActivityGao.this, jsonObject.get("errMessage").getAsString());
                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "無數據";
                    mHandler.sendMessage(message);
                }
                dismissDialog();
            }
        }.start();

    }

    /**
     * 碼頭放行車輛列表信息獲取
     */
    public void getCarList() {
        showDialog();
        url = Constants.HTTP_CARVIEW_SERVLET;
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "=getcarLog=fff===" + url);
                Gson gson = new Gson();
                String response = result;

                if (result != null) {
                    Intent intent = new Intent(ExListViewActivity.this, CarListActivity.class);
                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        carListMessages = new ArrayList<CarListMessage>();
                        for (JsonElement type : array) {
                            CarListMessage humi = gson.fromJson(type, CarListMessage.class);
                            carListMessages.add(humi);
                        }
                        intent.putExtra("list", (Serializable) carListMessages);
//                        isShowReturn = true;
                        startActivity(intent);

                    } else {
//                        isShowReturn = false;
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                        if (errCode.equals("400")) {
                            intent.putExtra("list", (Serializable) null);
                            startActivity(intent);
                        }
                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "無數據";
                    mHandler.sendMessage(message);
                }
                dismissDialog();
            }
        }.start();

    }

    /**
     * 司機行車日誌
     */
    public void getReturnLog() {
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        showDialog();
        url = Constants.HTTP_CAR_LOG_RETURN + "?car_driver_id=" + FoxContext.getInstance().getLoginId();
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "=getReturnLog=fff===" + url);
                Gson gson = new Gson();
                String response = result;

                if (result != null) {

                    Intent intent = new Intent(ExListViewActivity.this, CarLogDayActivity.class);
                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        carLogReturnMList = new ArrayList<CarLogReturnM>();
                        for (JsonElement type : array) {
                            CarLogReturnM humi = gson.fromJson(type, CarLogReturnM.class);
                            carLogReturnMList.add(humi);
                        }
                        intent.putExtra("returnList", (Serializable) carLogReturnMList);
                        isShowReturn = true;

                    } else {
                        isShowReturn = false;
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }

                    intent.putExtra("isShow", isShowReturn);
                    startActivity(intent);
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "無數據";
                    mHandler.sendMessage(message);
                }
                dismissDialog();
            }


        }.start();
    }

    /**
     * 獲取餐飲列表
     */
    public void getDormitory(String type) {
        showDialog();
        url = Constants.HTTP_MENU_SERVLET + "?type=" + type;

        FoxContext.getInstance().setType(type);
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                String response = result;

                if (result != null) {
                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        dimemsionMenuList = new ArrayList<DimemsionMenu>();
                        for (JsonElement type1 : array) {
                            DimemsionMenu humi = gson.fromJson(type1, DimemsionMenu.class);
                            dimemsionMenuList.add(humi);
                        }

                        Intent intent = new Intent(ExListViewActivity.this, DimemsionMenuActivity.class);
                        intent.putExtra("position", "");
                        intent.putExtra("title", "餐飲巡檢");
                        intent.putExtra("type", "BP");
                        intent.putExtra("dimemsionMenuList", (Serializable) dimemsionMenuList);
                        startActivity(intent);
                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "無數據";
                    mHandler.sendMessage(message);
                }
                dismissDialog();
            }
        }.start();

    }

    /**
     * 獲取活動列表
     */
    public void getEventList(String type) {

        FoxContext.getInstance().setType(type);
        Log.e("---------", "isVersionUpdate===");
        showDialog();
        final String eventCreator = Constants.HTTP_BARCODE_VIEW_SERVLET + "?dim_type=" + FoxContext.getInstance().getType();
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(eventCreator);
                Log.e("---------", "fffff=url==" + eventCreator.toString());

                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "fffff===" + result.toString());
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        eventMessageList = new ArrayList<EventMessage>();
                        for (JsonElement type : array) {
                            EventMessage humi = gson.fromJson(type, EventMessage.class);
                            eventMessageList.add(humi);
                        }
//                        getVersionCode = Integer.parseInt(eventMessageList.get(0).getId());
                        Intent intent = new Intent(ExListViewActivity.this, EventListActivity.class);
                        intent.putExtra("type", "X0");
                        intent.putExtra("eventList", (Serializable) eventMessageList);
                        startActivity(intent);
//                        Message message = new Message();
//                        message.what = MESSAGE_UPLOAD;
//                        mHandler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                        Intent intent = new Intent(ExListViewActivity.this, EventListActivity.class);
                        intent.putExtra("type", "X0");
                        intent.putExtra("eventList", (Serializable) null);
                        startActivity(intent);
                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "無數據";
                    mHandler.sendMessage(message);
                }
                dismissDialog();
            }
        }.start();


    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    if (msg.obj.equals("失敗")) {
                        ToastUtils.showShort(ExListViewActivity.this, "沒有巡檢數據!");
                    } else {
                        ToastUtils.showShort(ExListViewActivity.this, msg.obj.toString());
                    }
                    break;
                case MESSAGE_GPS:

                    ToastUtils.showShort(ExListViewActivity.this, "请开启GPS导航...");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
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

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */

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

    /**
     * 定位监听
     */
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

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
//    private void startLocation() {
//        //根据控件的选择，重新设置定位参数
////        resetOption();
//        // 设置定位参数
//        locationClient.setLocationOption(locationOption);
//        // 启动定位
//        locationClient.startLocation();
//    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
//    private void stopLocation() {
//        // 停止定位
//        locationClient.stopLocation();
//    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
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

    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
//        locationService.unregisterListener(mListener); //注销掉监听
//        locationService.stop(); //停止定位服务
        Log.e("----------", "stop");
        super.onStop();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // -----------location config ------------
        locationService = ((FoxApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
//        startLocation.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (startLocation.getText().toString().equals(getString(R.string.startlocation))) {
        locationService.start();// 定位SDK
        // start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
//                    startLocation.setText(getString(R.string.stoplocation));
//                } else {
//                    locationService.stop();
//                    startLocation.setText(getString(R.string.startlocation));
//                }
//            }
//        });
    }


    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
//
                Log.e("----------", "經度==" + location.getLongitude() + "緯度==" + location.getLatitude());
//                logMsg(sb.toString());
                FoxContext.getInstance().setLocation(location);

//                Log.e("----gaode", location.getLongitude() + "==" + location.getLatitude());
            } else {
                Message message = new Message();
                message.what = MESSAGE_TOAST;
                message.obj = "\"定位失败，loc is null\"";
                mHandler.sendMessage(message);

            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        setExListView();
        Log.e("-----onResume", "MainActivityGaoonResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        init();
        locationService.start();// 定位SDK
        Log.e("-----onRestart", "MainActivityGaoonRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        destroyLocation();
        locationService.stop();// 定位SDK
        Log.e("---------", "onDestroy");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
        }
    }
}
