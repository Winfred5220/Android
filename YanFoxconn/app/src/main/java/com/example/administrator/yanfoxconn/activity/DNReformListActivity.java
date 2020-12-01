package com.example.administrator.yanfoxconn.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.DNReformAdapter;
import com.example.administrator.yanfoxconn.adapter.RouteListAdapter;
import com.example.administrator.yanfoxconn.adapter.ZhiyinshuiProcessAdapter;
import com.example.administrator.yanfoxconn.bean.DNReform;
import com.example.administrator.yanfoxconn.bean.DNSpinner;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiExceMsg;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.example.administrator.yanfoxconn.widget.SwipeListView;
import com.example.administrator.yanfoxconn.widget.SwipeListViewOne;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Song
 * on 2020/10/16
 * Description：宿舍異常維護 列表界面
 */
public class DNReformListActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_BUILDING = 6;//棟
    private final int MESSAGE_SET_FLOOR = 7;//層
    private final int MESSAGE_SET_ROOM = 8;//房間

    private String[] areaSource = {"富錦家園", "八角", "富康家園", "富泰家園", "幹部樓"};
    private String area = "";    //區域
    private String building = "";//樓棟
    private String floor = "";   //樓層
    private String room = "";//房間
    private List<String> areaData = new ArrayList<>();//區域列表
    private List<String> buildingData = new ArrayList<>();//樓棟列表
    private List<String> floorData = new ArrayList<>();//樓層列表
    private List<String> roomData = new ArrayList<>();//房間列表

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.ll_tr)
    LinearLayout llTR;//查詢條件部分
    @BindView(R.id.sp_area)
    Spinner spArea;//區域
    @BindView(R.id.sp_building)
    Spinner spBuilding;//樓棟
    @BindView(R.id.sp_floor)
    Spinner spFloor;//樓層
    @BindView(R.id.sp_room)
    Spinner spRoom;//房間
    @BindView(R.id.btn_check)
    Button btnCheck;//查詢
    @BindView(R.id.lv_dn_remark)
    SwipeListViewOne listView;

    private DNReformAdapter dnReformAdapter;
    private List<DNReform> reformList;
    private List<DNReform> chongZuReformList;

    private String from;//來源，all顯示條件查詢，全部異常；self不顯示條件查詢，屋內異常
    private String[] qrResult;//宿舍信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dn_reform_lv);
        ButterKnife.bind(this);

        tvTitle.setText("異常整改");
        btnBack.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
        from = getIntent().getStringExtra("from");
        if (from.equals("self")) {
            qrResult = getIntent().getStringExtra("qrResult").split(",");
            llTR.setVisibility(View.GONE);
        }

        if (reformList != null) {
            setListViewHeightBasedOnChildren(listView);
            clickSeeOrAdd();
        }
    }

    /**
     * 用於解決scrollview嵌套listview導致只顯示一條數據的問題
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        RouteListAdapter listAdapter = (RouteListAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    private void clickSeeOrAdd() {
        dnReformAdapter.setOnClickListenerSeeOrAdd(new DNReformAdapter.OnClickListenerSeeOrAdd() {
            @Override
            public void OnClickListenerSee(int position) {
                Log.e("------", "");
                Intent intent = new Intent(DNReformListActivity.this, DZSignInOutActivity.class);
                intent.putExtra("flag", "DN");
                intent.putExtra("reform", (Serializable) chongZuReformList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_check://查詢
                getList();
                break;
        }
    }

    //獲取區域
    private void getArea() {
        for (int i = 0; i < areaSource.length; i++) {
            areaData.add(areaSource[i]);
        }
        setArea();
    }

    public void getBuilding() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    String area1 = URLEncoder.encode(URLEncoder.encode(area, "UTF-8"), "UTF-8");
                    String url = Constants.HTTP_SUSHE_BUILDING + "?jc_area=" + area1;
                    Log.e("------url------", url);
                    Request request = new Request.Builder()
                            .url(url)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Log.e("------result------", result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String ss = jsonObject.get("data").getAsJsonArray().toString();
                        JSONArray data = new JSONArray(ss);
                        buildingData.clear();
                        buildingData.add("選擇");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            Log.e("SUSHE_BUILDING", object.getString("SUSHE_BUILDING"));
                            buildingData.add(object.getString("SUSHE_BUILDING"));
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_BUILDING;
                        mHandler.sendMessage(message);
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void getFloor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象

                    String area1 = URLEncoder.encode(URLEncoder.encode(area, "UTF-8"), "UTF-8");
                    String building1 = URLEncoder.encode(URLEncoder.encode(building, "UTF-8"), "UTF-8");
                    String url = Constants.HTTP_SUSHE_FLOOR + "?jc_area=" + area1 + "&jc_building=" + building1;

                    Log.e("------url------", url);
                    Request request = new Request.Builder()
                            .url(url)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Log.e("------result------", result);

                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String ss = jsonObject.get("data").getAsJsonArray().toString();
                        JSONArray data = new JSONArray(ss);
                        floorData.clear();
                        floorData.add("選擇");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            Log.e("bs_layer", object.getString("SUSHE_FLOOR"));
                            floorData.add(object.getString("SUSHE_FLOOR"));
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_FLOOR;
                        mHandler.sendMessage(message);
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getRoom() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象

                    String area1 = URLEncoder.encode(URLEncoder.encode(area, "UTF-8"), "UTF-8");
                    String building1 = URLEncoder.encode(URLEncoder.encode(building, "UTF-8"), "UTF-8");
                    String floor1 = URLEncoder.encode(URLEncoder.encode(floor, "UTF-8"), "UTF-8");
                    String url = Constants.HTTP_SUSHE_ROOM + "?jc_area=" + area1 + "&jc_building=" + building1 + "&jc_floor=" + floor1;

                    Log.e("------url------", url);
                    Request request = new Request.Builder()
                            .url(url)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Log.e("------result------", result);

                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String ss = jsonObject.get("data").getAsJsonArray().toString();
                        JSONArray data = new JSONArray(ss);
                        roomData.clear();
                        roomData.add("選擇");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            Log.e("bs_layer", object.getString("SUSHE_ROOM"));
                            roomData.add(object.getString("SUSHE_ROOM"));
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_ROOM;
                        mHandler.sendMessage(message);
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setArea() {

        //區域下拉列表選擇
        spArea.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, areaData));
        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area = areaData.get(position);
                getBuilding();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void setBuilding() {

        //樓棟下拉列表選擇
        spBuilding.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, buildingData));
        spBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                building = buildingData.get(position);
                if (building.equals("選擇")) {
                    floorData.clear();
                    floorData.add("選擇");
                    setFloor();
                } else getFloor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void setFloor() {
        //樓層下拉列表選擇
        spFloor.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, floorData));
        spFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                floor = floorData.get(position);
                floor = floorData.get(position);
                if (floor.equals("選擇")) {
                    roomData.clear();
                    roomData.add("選擇");
                    setRoom();
                } else getRoom();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void setRoom() {
        //房間下拉列表選擇
        spRoom.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, roomData));
        spRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                room = roomData.get(position);
                if (building.equals("選擇") || floor.equals("選擇") || room.equals("選擇")) {

                } else {
                    getList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private String url2 = "";

    public void getList() {

        try {
            if (from.equals("self")) {
                String area1 = URLEncoder.encode(URLEncoder.encode(qrResult[0], "UTF-8"), "UTF-8");
                String building1 = URLEncoder.encode(URLEncoder.encode(qrResult[1], "UTF-8"), "UTF-8");
                String floor1 = URLEncoder.encode(URLEncoder.encode(qrResult[2], "UTF-8"), "UTF-8");
                String room1 = URLEncoder.encode(URLEncoder.encode(qrResult[3], "UTF-8"), "UTF-8");
                url2 = Constants.HTTP_SUSHE_REFORMLIST_SERVLET + "?area=" + area1 + "&building=" + building1 + "&floor=" + floor1 + "&room=" + room1;
                getListHandler(url2);
            } else if (building.equals("選擇") || floor.equals("選擇")) {
                ToastUtils.showLong(DNReformListActivity.this, "請正確選擇區,棟,層,房間的信息!");
            } else {
                String area1 = URLEncoder.encode(URLEncoder.encode(area, "UTF-8"), "UTF-8");
                String building1 = URLEncoder.encode(URLEncoder.encode(building, "UTF-8"), "UTF-8");
                String floor1 = URLEncoder.encode(URLEncoder.encode(floor, "UTF-8"), "UTF-8");
                String room1 = "";
                if (room.equals("選擇")) {
                    room1 = "";
                } else {
                    room1 = URLEncoder.encode(URLEncoder.encode(room, "UTF-8"), "UTF-8");
                }
                url2 = Constants.HTTP_SUSHE_REFORMLIST_SERVLET + "?area=" + area1 + "&building=" + building1 + "&floor=" + floor1 + "&room=" + room1;

                getListHandler(url2);}

        Log.e("------url2------", url2);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    private void getListHandler(String url){

        showDialog();
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        reformList = new ArrayList<DNReform>();

                        for (JsonElement type : array) {
                            DNReform humi = gson.fromJson(type, DNReform.class);
                            reformList.add(humi);
                        }
                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                    finish();
                }
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    aboutAlert(msg.obj.toString(), MESSAGE_TOAST);
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(DNReformListActivity.this, R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://進度賦值
                    chongZuReformList = new ArrayList<>();

                    List<List<DNSpinner>> result = new ArrayList<>();
                    List<DNSpinner> newResult;
                    List<DNReform> newReform = new ArrayList<>();

                    for (int i = 0; i < reformList.size(); i++) {
                        for (int j = 0; j < reformList.get(i).getJc_result().size(); j++) {
                            newResult = new ArrayList<>();
                            newResult.add(reformList.get(i).getJc_result().get(j));

                            result.add(newResult);
                            newReform.add(reformList.get(i));
                        }
                    }

                    for (int i = 0; i < result.size(); i++) {
                        DNReform lsReform = new DNReform();
                        lsReform.setJc_id(newReform.get(i).getJc_id());
                        lsReform.setJc_room(newReform.get(i).getJc_room());
                        lsReform.setJc_bed(newReform.get(i).getJc_bed());
                        lsReform.setEmp_name(newReform.get(i).getEmp_name());
                        lsReform.setJc_date(newReform.get(i).getJc_date().substring(0, 10));
                        lsReform.setJc_result(result.get(i));
                        chongZuReformList.add(i, lsReform);
                        Log.e("------------", "result.get(i)==" + result.get(i).get(0).getName());
                    }

                    dnReformAdapter = new DNReformAdapter(DNReformListActivity.this, chongZuReformList);
                    listView.setAdapter(dnReformAdapter);
                    clickSeeOrAdd();
                    break;
                case MESSAGE_SET_BUILDING://樓棟賦值
                    setBuilding();
                    break;
                case MESSAGE_SET_FLOOR://樓層賦值
                    setFloor();
                    break;
                case MESSAGE_SET_ROOM://區域賦值
                    setRoom();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void aboutAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (type == MESSAGE_TOAST) {
                            if (chongZuReformList != null) {
                                chongZuReformList.clear();
                                dnReformAdapter = new DNReformAdapter(DNReformListActivity.this, chongZuReformList);
                                listView.setAdapter(dnReformAdapter);
                            }
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (from.equals("self")) {
            getList();
        } else if (building.equals("") || floor.equals("")) {
            getArea();
        }else{
            getList();
        }
    }
}