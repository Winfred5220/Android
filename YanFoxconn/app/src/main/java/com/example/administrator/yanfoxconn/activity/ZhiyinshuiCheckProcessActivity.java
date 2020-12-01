package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.ZhiyinshuiProcessAdapter;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiExceMsg;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 直飲水點檢
 *
 * 進度查看
 */
public class ZhiyinshuiCheckProcessActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_BUILDING = 6;//教師賦值
    private final int MESSAGE_SET_AREA = 7;//區域賦值
    private final int MESSAGE_SET_FLOOR = 8;//问卷賦值

    private List<ZhiyinshuiExceMsg> mZhiyinshuiExceMsg;//點檢異常項
    private ZhiyinshuiProcessAdapter mZhiyinshuiProcessAdapter;//點檢列表適配器
    private List<String> areaData = new ArrayList<>();//區域列表
    private List<String> buildingData = new ArrayList<>();//樓棟列表
    private List<String> floorData = new ArrayList<>();//樓層列表
    private String area="";    //區域
    private String building="";//樓棟
    private String floor="";   //樓層
    private String type="";    //類別
    private String url2="";    //獲取點檢進度轉碼

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.sp_area)
    Spinner spArea;//區域
    @BindView(R.id.sp_building)
    Spinner spBuilding;//樓棟
    @BindView(R.id.sp_floor)
    Spinner spFloor;//樓層
    @BindView(R.id.btn_finished)
    Button btnFinished;//已完成
    @BindView(R.id.btn_unfinished)
    Button btnUnfinished;//未完成
    @BindView(R.id.lv_process)
    ListView lvProcess;//進度列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhiyinshui_check_process);
        ButterKnife.bind(this);

        tvTitle.setText("點檢進度");
        btnBack.setOnClickListener(this);
        btnFinished.setOnClickListener(this);
        btnUnfinished.setOnClickListener(this);

        type = getIntent().getStringExtra("type");
        getArea();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_finished:
                btnFinished.setBackgroundColor(getResources().getColor(R.color.color_42D42B));
                btnUnfinished.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
                getMessage("Y");
                break;
            case R.id.btn_unfinished:
                btnFinished.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
                btnUnfinished.setBackgroundColor(getResources().getColor(R.color.color_42D42B));
                getMessage("N");
                break;
        }

    }

    public void getArea(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象

                    String url= Constants.HTTP_WATER_AREA_SERVLET;

                    Log.e("------url------",url);
                    Request request = new Request.Builder()
                            .url(url)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Log.e("------result------",result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String ss = jsonObject.get("result").getAsJsonArray().toString();
                        JSONArray data = new JSONArray(ss);
                        areaData.clear();
                        for (int i=0;i<data.length();i++){
                            JSONObject object = data.getJSONObject(i);
                            Log.e("bs_area",object.getString("bs_area"));
                            areaData.add(object.getString("bs_area"));
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_AREA;
                        mHandler.sendMessage(message);
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    } else{
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
    public void getBuilding(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    String area1 =  URLEncoder.encode(URLEncoder.encode(area, "UTF-8"), "UTF-8");
                    String url= Constants.HTTP_WATER_BUILDING_SERVLET +"?area="+area1;
                    Log.e("------url------",url);
                    Request request = new Request.Builder()
                            .url(url)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Log.e("------result------",result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String ss = jsonObject.get("result").getAsJsonArray().toString();
                        JSONArray data = new JSONArray(ss);
                        buildingData.clear();buildingData.add("選擇");
                        for (int i=0;i<data.length();i++){
                            JSONObject object = data.getJSONObject(i);
                            Log.e("bs_build",object.getString("bs_build"));
                            buildingData.add(object.getString("bs_build"));
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_BUILDING;
                        mHandler.sendMessage(message);
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    } else{
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
    public void getFloor(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象

                    String area1 =  URLEncoder.encode(URLEncoder.encode(area, "UTF-8"), "UTF-8");
                    String building1 =  URLEncoder.encode(URLEncoder.encode(building, "UTF-8"), "UTF-8");
                    String url= Constants.HTTP_WATER_FLOOR_SERVLET +"?area="+area1+"&building="+building1;

                    Log.e("------url------",url);
                    Request request = new Request.Builder()
                            .url(url)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Log.e("------result------",result);

                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String ss = jsonObject.get("result").getAsJsonArray().toString();
                        JSONArray data = new JSONArray(ss);
                        floorData.clear();floorData.add("選擇");
                        for (int i=0;i<data.length();i++){
                            JSONObject object = data.getJSONObject(i);
                            Log.e("bs_layer",object.getString("bs_layer"));
                            floorData.add(object.getString("bs_layer"));
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_FLOOR;
                        mHandler.sendMessage(message);
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    } else{
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
    private void getMessage(String status){
        showDialog();

        try {
            if(building.equals("選擇"))building = "";
            if (floor.equals("選擇"))floor = "";
            String area1 =  URLEncoder.encode(URLEncoder.encode(area, "UTF-8"), "UTF-8");
            String building1 =  URLEncoder.encode(URLEncoder.encode(building, "UTF-8"), "UTF-8");
            String floor1 =  URLEncoder.encode(URLEncoder.encode(floor, "UTF-8"), "UTF-8");
            url2 = Constants.HTTP_WATER_PROCESS_SERVLET +"?flag=S&area="+area1+"&building="+building1+"&floor="+floor1+"&status=" + status+"&type="+type;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url2);
                dismissDialog();
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        mZhiyinshuiExceMsg = new ArrayList<ZhiyinshuiExceMsg>();

                        for (JsonElement type : array) {
                            ZhiyinshuiExceMsg humi = gson.fromJson(type, ZhiyinshuiExceMsg.class);
                            mZhiyinshuiExceMsg.add(humi);

                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else{
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                    finish();
                }
            } }.start();
    }
    public void setArea(){

        //區域下拉列表選擇
        spArea.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, areaData));
        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area = areaData.get(position);
                getBuilding();
                btnFinished.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
                btnUnfinished.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    public void setBuilding(){

        //樓棟下拉列表選擇
        spBuilding.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, buildingData));
        spBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                building = buildingData.get(position);
                if (building.equals("選擇")){
                    floorData.clear();
                    floorData.add("選擇");
                    setFloor();
                }else getFloor();
                btnFinished.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
                btnUnfinished.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    public void setFloor(){
        //樓層下拉列表選擇
        spFloor.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, floorData));
        spFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                floor = floorData.get(position);
                btnFinished.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
                btnUnfinished.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(ZhiyinshuiCheckProcessActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_UP);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                case MESSAGE_SET_TEXT://進度賦值
                    mZhiyinshuiProcessAdapter = new ZhiyinshuiProcessAdapter(ZhiyinshuiCheckProcessActivity.this,mZhiyinshuiExceMsg);
                    lvProcess.setAdapter(mZhiyinshuiProcessAdapter);
                    if (mZhiyinshuiExceMsg != null&&mZhiyinshuiExceMsg.size()!=0) {
                    } else {
                        ToastUtils.showShort(ZhiyinshuiCheckProcessActivity.this, "沒有數據!");
                    }
                    break;
                case MESSAGE_SET_BUILDING://樓棟賦值
                    setBuilding();
                    break;
                case MESSAGE_SET_AREA://區域賦值
                    setArea();
                    break;
                case MESSAGE_SET_FLOOR://樓層賦值
                    setFloor();
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
                        if (type==MESSAGE_TOAST){

                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void worningAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (type==MESSAGE_TOAST){

                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}