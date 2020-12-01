package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.OutKeepGridAdapter;
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
 * 營建 維保巡檢 分區/分類型 公用界面
 * 宿舍巡檢
 * Created by song on 2018/3/1.
 */

public class OutKeepFirstActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;//showToast
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.gv_out_keep)
    GridView gvOutKeep;//選項

    private String url;//請求地址
    private String result;//返回結果

    private OutKeepGridAdapter outKeepGridAdapter;

    public String[] group = {"A區", "C區", "D/G區", "E區"};
    public int[] groupIcon = {R.mipmap.icon_wba, R.mipmap.icon_wbc, R.mipmap.icon_wbdc, R.mipmap.icon_wbe};

    public String[] bgGroup = {"A區", "C區", "E區", "幹部區", "D區"};
    public String[] bgGroupType = {"BL", "BM", "BN", "BO", "BQ"};
    public int[] bgGroupIcon = {R.mipmap.icon_a, R.mipmap.icon_c, R.mipmap.icon_e, R.mipmap.icon_gb, R.mipmap.icon_d};

    public String[] buGroup = {"企劃","研磨化成","塗裝","模具","機加","壓鑄","陽極電鍍"};
    public String[] buGroupType= {"BW","BX","BY","BZ","CA","CB","CC"};
    public int[] buGroupIcon = {R.mipmap.icon_qihua, R.mipmap.icon_yanmo, R.mipmap.icon_tuzhuang, R.mipmap.icon_moju,R.mipmap.icon_jijia, R.mipmap.icon_yazhu, R.mipmap.icon_diandu};


    private List<String> groupList;//分區名稱
    private List<Integer> iconList;//圖標
    private List<String> groupTypeList;//宿舍類型
    private String roles;//當前用戶權限
    private List<String> rolesList;//當前用戶權限

    private Bundle bundle;
    private String type;
    private List<DimemsionMenu> dimemsionMenuList;//宿舍棟菜單

    private List<RouteMessage> routeMessageList;//巡檢點信息列表

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_keep_first);
        ButterKnife.bind(this);
        bundle = this.getIntent().getExtras();
        type = bundle.getString("type");

        tvTitle.setText(bundle.getString("title"));

        btnBack.setOnClickListener(this);
        setList();

        outKeepGridAdapter = new OutKeepGridAdapter(OutKeepFirstActivity.this, groupList, iconList, groupTypeList, rolesList, type);

        gvOutKeep.setAdapter(outKeepGridAdapter);
    }

    private void setList() {

        roles = FoxContext.getInstance().getRoles();
        rolesList = new ArrayList<String>();
        String spStr[] = roles.split(",");
        for (int i = 0; i < spStr.length; i++) {
            rolesList.add(i, spStr[i]);
        }

        //分區名稱
        groupList = new ArrayList<String>();
        if (type.equals("V0")) {
            for (int i = 0; i < group.length; i++) {

                groupList.add(i, group[i]);
            }
        } else if (type.equals("BU")){
            for (int i = 0; i < buGroup.length; i++) {

                groupList.add(i, buGroup[i]);
                Log.e("---------","buGroup[i]=="+buGroup[i]);
            }
        }else{
            for (int i = 0; i < bgGroup.length; i++) {

                groupList.add(i, bgGroup[i]);
                Log.e("---------","bgGroup[i]=="+bgGroup[i]);
            }
        }
        //分區圖標
        iconList = new ArrayList<Integer>();
        if (type.equals("V0")) {
            for (int i = 0; i < groupIcon.length; i++) {
                iconList.add(i, groupIcon[i]);
            }
        } else if (type.equals("BU")){
            for (int i = 0; i < buGroupIcon.length; i++) {

                iconList.add(i, buGroupIcon[i]);
            }
        }else{
            for (int i = 0; i < bgGroupIcon.length; i++) {

                iconList.add(i, bgGroupIcon[i]);
            }
        }


        //宿舍分類
        groupTypeList = new ArrayList<String>();
        if (type.equals("BU")){
            for (int i = 0; i < buGroupType.length; i++) {
                groupTypeList.add(i, buGroupType[i]);
            }
        }else {
            for (int i = 0; i < bgGroupType.length; i++) {
                groupTypeList.add(i, bgGroupType[i]);
            }
        }
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    /**
     * 獲取宿舍列表
     */
    public void getDormitory(String type, final int position) {
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
                dismissDialog();
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

                        Intent intent = new Intent(OutKeepFirstActivity.this, DimemsionMenuActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("title", groupList.get(position));
                        intent.putExtra("type", groupTypeList.get(position));
                        intent.putExtra("dimemsionMenuList", (Serializable) dimemsionMenuList);
                        startActivity(intent);
                    }
                }
            }
        }.start();

    }

    /**
     * 根據角色權限,獲取巡檢路線列表
     * @param type
     */
    public void getRouteList(String type) {
        if (type.equals("W0")){
            FoxContext.getInstance().setTakePic("W0");
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
//                        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
////
//                            mHandler.sendEmptyMessage(0);
//                        } else {
                            Intent intent = new Intent(getApplication(), RouteListActivity.class);
                            intent.putExtra("routeList", (Serializable) routeMessageList);
                            intent.putExtra("Dflag", "");//用於判定是否為宿舍巡檢,以方便按樓棟傳值,其他地方值為空
                            startActivity(intent);
//                        }

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
                        ToastUtils.showShort(OutKeepFirstActivity.this, "沒有巡檢數據!");
                    } else {
                        ToastUtils.showShort(OutKeepFirstActivity.this, msg.obj.toString());
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
