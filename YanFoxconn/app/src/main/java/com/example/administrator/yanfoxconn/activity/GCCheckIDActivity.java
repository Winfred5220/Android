package com.example.administrator.yanfoxconn.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.EmpListAdapter;
import com.example.administrator.yanfoxconn.bean.EmpFile;
import com.example.administrator.yanfoxconn.bean.EmpMessage;
import com.example.administrator.yanfoxconn.bean.GEMenLiu;
import com.example.administrator.yanfoxconn.bean.GEPeopleMsg;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.ChangeTextUtils;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ImageZipUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.util.CharsetUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import taobe.tec.jcc.JChineseConvertor;

/**
 *安保部健康追蹤
 * song 2020/11/28
 * 1員工  2供應商
 */
public class GCCheckIDActivity extends BaseActivity implements View.OnClickListener
{
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_SET_MEN = 3;//提交信息回復
    private final int MESSAGE_NOT_NET = 4;//顯示提醒
    private final int MESSAGE_SUCCESS = 5;//掃描失敗彈出框

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
    @BindView(R.id.tv_dep)
    TextView tvDep;//部門
    @BindView(R.id.et_gate_post)
    EditText etGatePost;//稽核門崗
    @BindView(R.id.lv_gate)
    MyListView lvGate;//稽核門崗列表
    @BindView(R.id.tr_list_gate)
    TableRow trLIstGate;//稽核課隊門崗列表
    @BindView(R.id.sp_area)
    Spinner spArea;//留觀地點
    @BindView(R.id.et_temp)
    EditText etTemp;//體溫
    @BindView(R.id.et_description)
    EditText etDescription;//描述
    @BindView(R.id.tv_description)
    TextView tvDescription;//描述

    private String id,flag;
    List<GEPeopleMsg>  gePeopleMsgs;
    List<GEMenLiu> geMens,geLius;
    private List<String> teamList;
    private EmpListAdapter mAdapter;
    private List<EmpMessage> empMessagesList;
    private List<EmpFile> empFileList;
    private String Sliu;
    private int num = 0;//輸入框初始值
    private int mMaxNum =250;//輸入框最大值
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gc_check_id);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("result");
        flag = getIntent().getStringExtra("check");
        getPeopleMessage(id,flag);


        tvTitle.setText("體症異常紀錄");
        btnBack.setText("返回");
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);

        etGatePost.setTag("0");

        //搜索关键字
        etGatePost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                trLIstGate.setVisibility(View.VISIBLE);
                String a = etGatePost.getText().toString();
                //调用适配器里面的搜索方法
                mAdapter.SearchCity(a);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

        etTemp.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!etTemp.getText().toString().equals("")){
                    if (Float.valueOf(etTemp.getText().toString())>42){
                        ToastUtils.showShort(GCCheckIDActivity.this,"請注意溫度填寫！");
                    }}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etDescription.addTextChangedListener(new TextWatcher() {
            //紀錄存入的字數
            private CharSequence wordNum;
            private int selectionStart;
            private int selectionEnd;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //實時紀錄輸入的字數
                wordNum = charSequence;


            }

            @Override
            public void afterTextChanged(Editable editable) {
                int number = num + editable.length();
                //TextView 顯示剩餘字數
                tvDescription.setText(""+number+"/250");
                selectionStart = etDescription.getSelectionStart();
                selectionEnd = etDescription.getSelectionEnd();
                //判斷大於最大值
                if (wordNum.length()>mMaxNum){
                    //刪除多餘輸入的字（不會顯示出來）
                    editable.delete(selectionStart-1,selectionEnd);
                    int tempSelection = selectionEnd;
                    etDescription.setText(editable);
                    etDescription.setSelection(tempSelection);//設置光標在最後
                    ToastUtils.showLong(GCCheckIDActivity.this,"最多輸入250字！ ");
                }
            }
        });

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
                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_SUCCESS://提交成功
//                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
                    ToastUtils.showLong(GCCheckIDActivity.this, msg.obj.toString());
                    finish();
                    break;
                case MESSAGE_NOT_NET:
                    ToastUtils.showLong(GCCheckIDActivity.this, "網絡錯誤，請稍後重試！");
                    break;
                case MESSAGE_SET_MEN:
                    setMen();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private void setText(){
        if (flag.equals("2")){
        tvId.setText(gePeopleMsgs.get(0).getWORKNO());
        tvName.setText(gePeopleMsgs.get(0).getCHINESENAME());
        tvIdentity.setText(gePeopleMsgs.get(0).getSEX());
        tvDep.setText(gePeopleMsgs.get(0).getORGNAME());
        }else{
            tvId.setText(gePeopleMsgs.get(0).getSF_CODE());
            tvName.setText(gePeopleMsgs.get(0).getNAME());
            tvIdentity.setText(gePeopleMsgs.get(0).getSEX());
            tvDep.setText(gePeopleMsgs.get(0).getDANWEI());
        }
        List<String> men=new ArrayList<>();
        List<String> liu=new ArrayList<>();
        for (int i=0;i<geMens.size();i++){
            men.add(geMens.get(i).getIn_Door());
        }
        for (int i=0;i<geLius.size();i++){
            liu.add(geLius.get(i).getIn_Door());
        }
        getMen();
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
if (etDescription.getText().toString().equals("")||etTemp.getText().toString().equals("")){
    ToastUtils.showShort(GCCheckIDActivity.this,"有值為空，請注意！");
}else if (etGatePost.getTag().equals("0")){
    ToastUtils.showShort(GCCheckIDActivity.this,"請選擇稽核門崗!");

}else{
                check();}
                break;
        }
    }

    //提交前檢查
    private void check() {
        final String url = Constants.HTTP_HEALTH_COMMIT; //此處寫上自己的URL

        JsonObject object = new JsonObject();

        object.addProperty("type", "GC");
        object.addProperty("In_Statues", "未結案");
        object.addProperty("In_Tempature", etTemp.getText().toString());
        object.addProperty("In_Others", etDescription.getText().toString());
        object.addProperty("In_Door", men);
        object.addProperty("In_Observation", Sliu);
        object.addProperty("flag", flag);
        object.addProperty("In_Createor",FoxContext.getInstance().getName());
        object.addProperty("In_Createor_id",FoxContext.getInstance().getLoginId());
        if (flag.equals("2")){

            object.addProperty("In_id",id);
            object.addProperty("In_Department",gePeopleMsgs.get(0).getORGNAME());
            object.addProperty("In_Tel",gePeopleMsgs.get(0).getPRIVATETEL());
            object.addProperty("In_Name",gePeopleMsgs.get(0).getCHINESENAME());
            object.addProperty("In_Sex",gePeopleMsgs.get(0).getSEX());
        }else{

            object.addProperty("In_id",id);
            object.addProperty("In_Department",gePeopleMsgs.get(0).getDANWEI());
            object.addProperty("In_Tel",gePeopleMsgs.get(0).getTEL());
            object.addProperty("In_Name",gePeopleMsgs.get(0).getNAME());
            object.addProperty("In_Sex",gePeopleMsgs.get(0).getSEX());
        }

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
                            message.what = MESSAGE_SUCCESS;
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
    //獲取門崗
    private void getMen(){
        showDialog();
        final String url = Constants.HTTP_COMMON_FORMS_SERVLET+"?code="+id;

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
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        empMessagesList = new ArrayList<EmpMessage>();

                        for (JsonElement type : array) {
                            EmpMessage humi = gson.fromJson(type, EmpMessage.class);
                            empMessagesList.add(humi);

                        }

                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();
                        empFileList = new ArrayList<EmpFile>();
                        for (JsonElement type1 : array1) {
                            EmpFile humi1 = gson.fromJson(type1, EmpFile.class);
                            empFileList.add(humi1);
                        }


                        Message message = new Message();
                        message.what = MESSAGE_SET_MEN;
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

    private String men;
    private ChangeTextUtils changeTextUtils = new ChangeTextUtils();
    private void setMen(){
        teamList = new ArrayList<>();
        for (int i = 0;i<empFileList.size();i++){

            teamList.add(change1(empFileList.get(i).getID()+","+empFileList.get(i).getAQ1()+"-"+empFileList.get(i).getAQ2()+"-"+empFileList.get(i).getAQ3()+"-"+empFileList.get(i).getAQ4()));

        }
        mAdapter = new EmpListAdapter(GCCheckIDActivity.this,teamList);
        lvGate.setAdapter(mAdapter);

        //稽核門崗列表,選中后1:tit賦值,2:列表隱藏
        mAdapter.OnClickSetText(new EmpListAdapter.OnClickSetText() {
            @Override
            public void OnClickxt(String tit) {
                etGatePost.setText(tit.split(",")[1]);
                etGatePost.setTag(tit.split(",")[0]);
                men= changeTextUtils.simToTra(tit.split(",")[1]);

                mAdapter.SearchCity("");
                trLIstGate.setVisibility(View.GONE);
            }
        });

    }
    //繁体转成简体
    public String change1(String changeText) {
        try {
            JChineseConvertor jChineseConvertor = JChineseConvertor.getInstance();
            changeText = jChineseConvertor.t2s(changeText);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeText;
    }

}
