package com.example.administrator.yanfoxconn.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.GEMenLiu;
import com.example.administrator.yanfoxconn.bean.GEPeopleMsg;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ImageZipUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *安保部健康追蹤
 * song 2020/11/28
 * 1員工  2供應商
 */
public class GCCheckIDActivity extends BaseActivity implements View.OnClickListener
{
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_NOT_NET = 4;//顯示提醒

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.tv_id)
    TextView tvId;//工號
    @BindView(R.id.tv_name)
    TextView tvName;//姓名
    @BindView(R.id.tv_identity)
    TextView tvIdentity;//身份證
    @BindView(R.id.tv_pro)
    TextView tvPro;//產品處
    @BindView(R.id.tv_dep)
    TextView tvDep;//部門
    @BindView(R.id.sp_team)
    Spinner spTeam;//門崗
    @BindView(R.id.sp_area)
    Spinner spArea;//留觀地點
    @BindView(R.id.et_temp)
    EditText etTemp;//體溫
    @BindView(R.id.et_desp)
    EditText etDesp;//詳細描述

    private String id,flag;
    List<GEPeopleMsg>  gePeopleMsgs;
    List<GEMenLiu> geMens,geLius;
    private String Smen,Sliu;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gc_check_id);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("result");
        flag = getIntent().getStringExtra("check");
        getPeopleMessage(id,flag);

        tvTitle.setText("健康異常紀錄");
        btnBack.setText("返回");
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);

    }

    /**
     * 獲取人員信息
     * @param id    工號或身分證
     * @param flag  廠商或員工
     */
    private void getPeopleMessage(String id,String flag){
        showDialog();
        final String url = Constants.HTTP_HEALTH_SCAN+"?In_id="+id+"&flag="+flag;

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);

                dismissDialog();
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("result1").getAsJsonArray();
                        gePeopleMsgs = new ArrayList<GEPeopleMsg>();

                        for (JsonElement type : array) {
                            GEPeopleMsg humi = gson.fromJson(type, GEPeopleMsg.class);
                            gePeopleMsgs.add(humi);
                        }
                        //門崗
                        JsonArray array2 = jsonObject.get("result2").getAsJsonArray();
                        geMens = new ArrayList<GEMenLiu>();

                        for (JsonElement type : array2) {
                            GEMenLiu humi = gson.fromJson(type, GEMenLiu.class);
                            geMens.add(humi);
                        }
                        //留觀地點
                        JsonArray array3 = jsonObject.get("result3").getAsJsonArray();
                        geLius = new ArrayList<GEMenLiu>();

                        for (JsonElement type : array3) {
                            GEMenLiu humi = gson.fromJson(type, GEMenLiu.class);
                            geLius.add(humi);
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
                }
            } }.start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

                    ToastUtils.showLong(GCCheckIDActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_UP://提交響應
//                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                    break;
                case MESSAGE_NOT_NET:
                    ToastUtils.showLong(GCCheckIDActivity.this, "網絡錯誤，請稍後重試！");
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private void setText(){
        tvId.setText(gePeopleMsgs.get(0).getWORKNO());
        tvName.setText(gePeopleMsgs.get(0).getCHINESENAME());
        tvIdentity.setText(gePeopleMsgs.get(0).getSEX());
        tvDep.setText(gePeopleMsgs.get(0).getORGNAME());
        List<String> men=new ArrayList<>();
        List<String> liu=new ArrayList<>();
        for (int i=0;i<geMens.size();i++){
            men.add(geMens.get(i).getIn_Door());
        }
        //稽核課隊下拉列表選擇
        spTeam.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, men));
        spTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               Smen = men.get(position);

//                Log.e("---------", "最喜欢的水果是：" + str);
//                paramMap.put("kedui",str);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        for (int i=0;i<geLius.size();i++){
            liu.add(geLius.get(i).getIn_Door());
        }
        //稽核課隊下拉列表選擇
        spArea.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, liu));
        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Sliu = liu.get(position);

//                Log.e("---------", "最喜欢的水果是：" + str);
//                paramMap.put("kedui",str);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:

                check();
                break;
        }
    }

    //提交前檢查
    private void check() {
        final String url = Constants.HTTP_HEALTH_COMMIT; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();

        object.addProperty("type", "GC");
        object.addProperty("In_Tempature", etTemp.getText().toString());
        object.addProperty("In_Others", etDesp.getText().toString());
        object.addProperty("In_Door", Smen);
        object.addProperty("In_Observation", Sliu);
        object.addProperty("flag", flag);
        object.addProperty("In_id",id);
        object.addProperty("In_Department",gePeopleMsgs.get(0).getORGNAME());
        object.addProperty("In_Tel",gePeopleMsgs.get(0).getPRIVATETEL());
        object.addProperty("In_Name",gePeopleMsgs.get(0).getCHINESENAME());
        object.addProperty("In_Sex",gePeopleMsgs.get(0).getSEX());
        object.addProperty("In_Createor",FoxContext.getInstance().getName());
        object.addProperty("In_Createor_id",FoxContext.getInstance().getLoginId());

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
                    Log.e("---------", "==fff===" + url);
                    String result = HttpConnectionUtil.doPostJsonObject(url, object);
                    if (result != null) {
                        dismissDialog();
                        Log.e("---------", "result==fff===" + result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String errCode = jsonObject.get("errCode").getAsString();
                        if (errCode.equals("200")) {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
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
                        message.what = MESSAGE_NOT_NET;
                        mHandler.sendMessage(message);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }
        }.start();
    }

}
