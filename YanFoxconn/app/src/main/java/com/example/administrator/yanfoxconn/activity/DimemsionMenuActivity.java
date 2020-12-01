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
import com.example.administrator.yanfoxconn.adapter.DimemsionNextGridAdapter;
import com.example.administrator.yanfoxconn.bean.DimemsionMenu;
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
 * 宿舍 分棟界面
 * Created by song on 2018/3/2.
 */

public class DimemsionMenuActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_GPS = 1;//判斷GPS
    private final int MESSAGE_TOAST = 2;//showToast

    private int[][] icon={
            {R.mipmap.icon_11,R.mipmap.icon_12,R.mipmap.icon_13,R.mipmap.icon_14,R.mipmap.icon_15,R.mipmap.icon_16,R.mipmap.icon_17,R.mipmap.icon_18,R.mipmap.icon_19,R.mipmap.icon_20,R.mipmap.icon_21,R.mipmap.icon_22,R.mipmap.icon_23,R.mipmap.icon_24},
            {R.mipmap.icon_c1,R.mipmap.icon_c2,R.mipmap.icon_c3,R.mipmap.icon_c4,R.mipmap.icon_c5,R.mipmap.icon_c6,R.mipmap.icon_c7,R.mipmap.icon_c8},
            {R.mipmap.icon_01,R.mipmap.icon_02,R.mipmap.icon_03,R.mipmap.icon_04,R.mipmap.icon_08,R.mipmap.icon_09,R.mipmap.icon_10,R.mipmap.icon_11,R.mipmap.icon_12,R.mipmap.icon_16},
            {R.mipmap.icon_06,R.mipmap.icon_10,R.mipmap.icon_a,R.mipmap.icon_b,R.mipmap.icon_c},
            {R.mipmap.icon_01,R.mipmap.icon_02,R.mipmap.icon_03,R.mipmap.icon_04,R.mipmap.icon_05}
    };

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

    private List<String> itemList;//分區內樓棟名稱
    private List<String> itemTypeList;//分區內樓棟編號
//    private List<Integer> itemImgList;

    /**
     * 所有分組的所有子項的GridView數據集合,圖片顯示
     */
    private List<Integer> itemGridImgList;
    private List<List<Integer>> itemImgList;//分區內分類圖標

    private String roles;//當前用戶權限
    private List<String> rolesList;//當前用戶權限
    private String type;//維保或宿舍
    private List<DimemsionMenu> dimemsionMenuList;

    private DimemsionNextGridAdapter secondGridAdapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_keep_second);
        ButterKnife.bind(this);

        locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Bundle bundle = this.getIntent().getExtras();
        tvTitle.setText(bundle.getString("title"));

        type = bundle.getString("type");
        dimemsionMenuList = (ArrayList<DimemsionMenu>)bundle.getSerializable("dimemsionMenuList");
        btnBack.setOnClickListener(this);


        setList();

        secondGridAdapter = new DimemsionNextGridAdapter(this,rolesList,itemList,itemTypeList,itemImgList.get(bundle.getInt("position")));
        gvOutKeep.setAdapter(secondGridAdapter);
        secondGridAdapter.setOnClickListener(new DimemsionNextGridAdapter.OnClickListener() {

            @Override
            public void OnClickListenerFalse(int position) {
                Log.e("-------", "沒有權限");
                ToastUtils.showLong(DimemsionMenuActivity.this, "您沒有該巡檢權限,請確認!");
            }

            @Override
            public void OnClickListenerTrue(int position) {
                Log.e("-------", "有權限");
//                mContext.getRouteList(FoxContext.getInstance().getTypes()[position]);
                getRouteList(type,itemTypeList.get(position));
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
        for (int i = 0; i < dimemsionMenuList.size(); i++) {

            itemList.add(i, dimemsionMenuList.get(i).getDim_flag_name());
        }

        itemTypeList = new ArrayList<>();
        for (int i = 0; i < dimemsionMenuList.size(); i++) {

            itemTypeList.add(i, dimemsionMenuList.get(i).getDim_flag());
        }

        itemImgList = new ArrayList<>();
        for (int i = 0; i < icon.length; i++) {
            // 每个分组下的每个子项的 GridView 数据集合
            itemGridImgList = new ArrayList<>();
            for (int j = 0; j < icon[i].length; j++) {
                itemGridImgList.add(icon[i][j]);
            }
            itemImgList.add(i, itemGridImgList);
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
    public void getRouteList(String type, final String flag) {
        showDialog();
        url = Constants.HTTP_DIMEMSION_NEXT_SERVLET + "?type=" + type+"&flag="+flag;

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
                            intent.putExtra("Dflag",flag);//用於判定是否為宿舍巡檢,以方便按樓棟傳值,其他地方值為空
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
                        ToastUtils.showShort(DimemsionMenuActivity.this, "沒有巡檢數據!");
                    } else {
                        ToastUtils.showShort(DimemsionMenuActivity.this, msg.obj.toString());
                    }
                    break;
                case MESSAGE_GPS:

                    ToastUtils.showShort(DimemsionMenuActivity.this, "请开启GPS导航...");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        setList();
        Log.e("-----onResume", "MainActivityGaoonResume");
    }
}
