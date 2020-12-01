package com.example.administrator.yanfoxconn.activity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.OutKeepSecondGridAdapter;
import com.example.administrator.yanfoxconn.bean.RouteMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 營建 維保分區后 分類界面
 * Created by song on 2018/3/2.
 */

public class OutKeepSecondActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_GPS = 1;//判斷GPS
    private final int MESSAGE_TOAST = 2;//showToast
     private String url;//請求地址
    private String result;//返回結果
     private List<RouteMessage> routeMessageList;//巡檢點信息列表
    private LocationManager locManager;

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.gv_out_keep)
    GridView gvOutKeep;

    public String[][] gridViewChild = {
            {"A區電梯", "A區軟化水", "A區天車", "A區空壓系統", "A區冰水系統", "A區高壓專線", "A區空調", "A區熱力系統"},
            {"C區太陽能", "C區電梯", "C區空調", "C區熱力系統", "C區高壓專線", "C區軟化水", "C區天車", "C區換熱站"},
            {"D/G區天車", "D/G區電梯", "D/G區高壓專線", "D/G區熱力系統", "D/G區軟化水"},
            {"E區空壓系統", "E區冰水系統", "E區軟化水", "E區電梯", "E區天車", "E區空調", "E區高壓專線", "E區熱力系統"}};
    public String[][] gridViewType = {
            {"AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH"},
            {"AI", "AJ", "AK", "AL", "AM", "AN", "AO", "AP"},
            {"AR", "AQ", "AS", "AT", "AU"},
            {"AV", "AW", "AX", "AY", "AZ", "BA", "BB", "BC"}};
    public int[][] img = {
            {R.mipmap.icon_anquan, R.mipmap.icon_lianluo, R.mipmap.icon_anquan, R.mipmap.icon_lianluo, R.mipmap.icon_anquan,
                    R.mipmap.icon_lianluo, R.mipmap.icon_anquan, R.mipmap.icon_lianluo},
            {R.mipmap.icon_anquan, R.mipmap.icon_lianluo, R.mipmap.icon_anquan, R.mipmap.icon_lianluo, R.mipmap.icon_anquan,
                    R.mipmap.icon_lianluo, R.mipmap.icon_anquan, R.mipmap.icon_lianluo},
            {R.mipmap.icon_anquan, R.mipmap.icon_lianluo, R.mipmap.icon_anquan, R.mipmap.icon_lianluo, R.mipmap.icon_anquan},
            {R.mipmap.icon_anquan, R.mipmap.icon_lianluo, R.mipmap.icon_anquan, R.mipmap.icon_lianluo, R.mipmap.icon_anquan,
                    R.mipmap.icon_lianluo, R.mipmap.icon_anquan, R.mipmap.icon_lianluo}};

    private List<String> item;
    private List<Integer> icon;
    private List<List<String>> itemList;//分區內分類名稱
    private List<List<String>> itemTypeList;//分區內分類類型名稱
    private List<List<Integer>> itemImgList;//分區內分類圖標

    private String roles;//當前用戶權限
    private List<String> rolesList;//當前用戶權限

    private OutKeepSecondGridAdapter secondGridAdapter;

    private int positionGrid ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_keep_second);
        ButterKnife.bind(this);

        locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Bundle bundle = this.getIntent().getExtras();
        tvTitle.setText(bundle.getString("title"));
        positionGrid = bundle.getInt("position");
        btnBack.setOnClickListener(this);

        setList();
        secondGridAdapter = new OutKeepSecondGridAdapter(this,rolesList,itemList.get(positionGrid),itemTypeList.get(positionGrid),itemImgList.get(positionGrid));
        gvOutKeep.setAdapter(secondGridAdapter);
        secondGridAdapter.setOnClickListener(new OutKeepSecondGridAdapter.OnClickListener() {

            @Override
            public void OnClickListenerFalse(int position) {
                Log.e("-------", "沒有權限");
                ToastUtils.showLong(OutKeepSecondActivity.this, "您沒有該巡檢權限,請確認!");
            }

            @Override
            public void OnClickListenerTrue(int position) {
                Log.e("-------", "有權限");

//                mContext.getRouteList(FoxContext.getInstance().getTypes()[position]);
                OutKeepSecondActivity.this.getRouteList(itemTypeList.get(positionGrid).get(position));

            }
        });
    }

    private void setList() {
        //用戶全部權限
        roles = FoxContext.getInstance().getRoles();
        rolesList = new ArrayList<String>();
        String spStr[] = roles.split(",");
        for (int i = 0; i < spStr.length; i++) {
            rolesList.add(i, spStr[i]);
        }

        // 所有分组的所有子项的 GridView 数据集合
        itemList = new ArrayList<>();
        for (int i = 0; i < gridViewChild.length; i++) {
            // 每个分组下的每个子项的 GridView 数据集合
            item = new ArrayList<>();
            for (int j = 0; j < gridViewChild[i].length; j++) {
                item.add(gridViewChild[i][j]);
            }
            itemList.add(i, item);
        }

        itemTypeList = new ArrayList<>();
        for (int i = 0; i < gridViewType.length; i++) {
            // 每个分组下的每个子项的 GridView 数据集合
            item = new ArrayList<>();
            for (int j = 0; j < gridViewType[i].length; j++) {
                item.add(gridViewType[i][j]);
            }
            itemTypeList.add(i, item);
        }

        itemImgList = new ArrayList<>();
        for (int i = 0; i < img.length; i++) {
            // 每个分组下的每个子项的 GridView 数据集合
            icon = new ArrayList<>();
            for (int j = 0; j < img[i].length; j++) {
                icon.add(img[i][j]);
            }
            itemImgList.add(i, icon);
        }
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    /**
     * 根據角色權限,獲取巡檢路線列表
     *
     * @param type
     */
    public void getRouteList(String type) {
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
                dismissDialog();
                if (result != null) {
                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("data").getAsJsonArray();
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
                            intent.putExtra("Dflag","");//用於判定是否為宿舍巡檢,以方便按樓棟傳值,其他地方值為空
                            startActivity(intent);
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
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    if (msg.obj.equals("失敗")) {
                        ToastUtils.showShort(OutKeepSecondActivity.this, "沒有巡檢數據!");
                    } else {
                        ToastUtils.showShort(OutKeepSecondActivity.this, msg.obj.toString());
                    }
                    break;
                case MESSAGE_GPS:

                    ToastUtils.showShort(OutKeepSecondActivity.this, "请开启GPS导航...");
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        setList();
        Log.e("-----onStart", "MainActivityGaoonResume");
    }
}
