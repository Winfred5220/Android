package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CaseListAdapter;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import taobe.tec.jcc.JChineseConvertor;

public class IndustrialAddActivity extends BaseActivity {

    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤

    private String[] areaData = {"區域","A區","C區","E區","D區","G區"};
    private String[] floorData = {"樓層","1F","1.5F","2F","3F","4F"};
    private String[] TechnologyData = {"請選擇","成型","組裝","SMT","塗裝","衝壓","倉庫","辦公室","無塵室","模具廠","配電室","打料房","餐廳","鞋櫃區","吸塑","中小型塑模","華北檢測","表面處理一","機房","電鍍","蝕刻","水洗","閒置區","磨具","機加","化成","壓鑄"};
    private List<String> tungData = new ArrayList<>();//樓棟列表
    private List<String> productData =  new ArrayList<>();//產品處列表
    private List<String> caseData =  new ArrayList<>();//專案名稱列表
    private CaseListAdapter mAdapter;
    private String url;//地址
    private String result;//网络获取结果
    private String area;//
    private String tung;//
    private String floor;//
    private String product;//
    private String technology;//

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.tv_date)
    TextView tvDate;//時間
    @BindView(R.id.sp_area)
    Spinner spArea;//區域
    @BindView(R.id.sp_floor)
    Spinner spFloor;//樓層
    @BindView(R.id.sp_tung)
    Spinner spTung;//樓棟
    @BindView(R.id.sp_product)
    Spinner spProduct;//產品處
    @BindView(R.id.sp_technology)
    Spinner spTechnology;//工藝
    @BindView(R.id.et_case_name)
    EditText etCase;//專案名稱
    @BindView(R.id.et_supervisor)
    EditText etSupervisor;//責任主管
    @BindView(R.id.et_check_person)
    EditText etCheckPerson;//巡查人
    @BindView(R.id.lv_case)
    MyListView lvCase;//專案名稱
    @BindView(R.id.btn_up)
    Button btnUp;//提交

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industrial_add);
        ButterKnife.bind(this);

        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());
        tvDate.setText(formatter.format(curDate));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  upCheck();
            }
        });
        tvTitle.setText("添加基本信息");

        //區域下拉列表選擇
        spArea.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, areaData));
        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area = areaData[position];
                tungData.clear();productData.clear();caseData.clear();
                tungData.add("樓棟");productData.add("請選擇");
                getTungAndProductData(areaData[position]);
                setTungAndProduct();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //樓層下拉列表選擇
        spFloor.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, floorData));
        spFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                floor = floorData[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //工藝下拉列表選擇
        spTechnology.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, TechnologyData));
        spTechnology.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                technology = TechnologyData[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //搜索关键字
        etCase.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lvCase.setVisibility(View.VISIBLE);
                String a = etCase.getText().toString().toUpperCase();
                //调用适配器里面的搜索方法
                mAdapter.SearchCity(a);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }

        });


    }

    public void setText(){
//        Log.e("--------caseData--------", caseData.toString() );
        mAdapter = new CaseListAdapter(IndustrialAddActivity.this,caseData);
        lvCase.setAdapter(mAdapter);

        //稽核門崗列表,選中后1:tit賦值,2:列表隱藏
        mAdapter.OnClickSetText(new CaseListAdapter.OnClickSetText() {
            @Override
            public void OnClickxt(String tit) {
                etCase.setText(tit);
                mAdapter.SearchCity("");
                lvCase.setVisibility(View.GONE);
            }
        });
    }

    public void upCheck(){
        if(area.equals("區域")){
            ToastUtils.showShort(IndustrialAddActivity.this,"請選擇區域");
            return;
        }else if(tung.equals("樓棟")){
            ToastUtils.showShort(IndustrialAddActivity.this,"請選擇樓棟");
            return;
        }else if(floor.equals("樓層")){
            ToastUtils.showShort(IndustrialAddActivity.this,"請選擇樓層");
            return;
        }else if(product.equals("請選擇")){
            ToastUtils.showShort(IndustrialAddActivity.this,"請選擇產品處");
            return;
        }else if(technology.equals("請選擇")){
            ToastUtils.showShort(IndustrialAddActivity.this,"請選擇工藝");
            return;
        }else if(etCase.getText().toString().equals("")||etCase.getText().toString()==null){
            ToastUtils.showShort(IndustrialAddActivity.this,"請輸入專案名稱");
            return;
        }else if(etSupervisor.getText().toString().equals("")||etSupervisor.getText().toString()==null){
            ToastUtils.showShort(IndustrialAddActivity.this,"請輸入責任主管");
            return;
        }else if(etCheckPerson.getText().toString().equals("")||etCheckPerson.getText().toString()==null){
            ToastUtils.showShort(IndustrialAddActivity.this,"請輸入巡查人");
            return;
        }
        upMessage(area,tung,floor,product,technology,change(etCase.getText().toString()),etSupervisor.getText().toString(),etCheckPerson.getText().toString());
    }

    public void setTungAndProduct(){

        //樓棟下拉列表選擇
        spTung.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tungData));
        spTung.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tung = tungData.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //產品處下拉列表選擇
        spProduct.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, productData));
        spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                product = productData.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void getTungAndProductData(final String area){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    String area1 =  URLEncoder.encode(URLEncoder.encode(area.toString(), "UTF-8"), "UTF-8");
                    Request request = new Request.Builder()
                            .url(Constants.HTTP_TUNGANDPRODUCT_SERVLET + "?area=" + area1)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        String result = response.body().string();
//                        Log.d("kwwl","response.code()=="+response.code());
//                        Log.d("kwwl","response.message()=="+response.message());
//                        Log.d("kwwl","res=="+result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String ss = jsonObject.get("data").getAsJsonArray().toString();
                        JSONArray data = new JSONArray(ss);
                        for (int i=0;i<data.length();i++){
                            JSONObject object = data.getJSONObject(i);
                            Log.e("object.getString(name1)",object.getString("name1"));
                            tungData.add(object.getString("name1"));
                        }

                        String ss1 = jsonObject.get("data1").getAsJsonArray().toString();
                        JSONArray data1 = new JSONArray(ss1);
                        for (int i=0;i<data1.length();i++){
                            JSONObject object1 = data1.getJSONObject(i);
                            productData.add(object1.getString("name1"));
                        }

                        String ss2 = jsonObject.get("data2").getAsJsonArray().toString();
                        JSONArray data2 = new JSONArray(ss2);
                        for (int i=0;i<data2.length();i++){
                            JSONObject object2 = data2.getJSONObject(i);
                            caseData.add(change1(object2.getString("name1")));
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        mHandler.sendMessage(message);
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
                    ToastUtils.showLong(IndustrialAddActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                case MESSAGE_SET_TEXT://text賦值
                    setText();
                    break;
                  }
            super.handleMessage(msg);
        }
    };

    //提交数据
    private void upMessage(String area, String tung,String floor,String product,String technology,String caseName,String supervisor,String inspector){

        try {
            String area1 =  URLEncoder.encode(URLEncoder.encode(area.toString(), "UTF-8"), "UTF-8");
            String tung1 =  URLEncoder.encode(URLEncoder.encode(tung.toString(), "UTF-8"), "UTF-8");
            String product1 =  URLEncoder.encode(URLEncoder.encode(product.toString(), "UTF-8"), "UTF-8");
            String technology1 =  URLEncoder.encode(URLEncoder.encode(technology.toString(), "UTF-8"), "UTF-8");
            String caseName1 =  URLEncoder.encode(URLEncoder.encode(caseName.toString(), "UTF-8"), "UTF-8");
            String supervisor1 =  URLEncoder.encode(URLEncoder.encode(supervisor.toString(), "UTF-8"), "UTF-8");
            String inspector1 =  URLEncoder.encode(URLEncoder.encode(inspector.toString(), "UTF-8"), "UTF-8");
            url = Constants.HTTP_INDUSSAFE_DEPARTUPDATE_SERVLET + "?area=" + area1 + "&tung=" + tung1 + "&floor=" + floor +"&product=" + product1 + "&technology=" + technology1 + "&caseName=" + caseName1+ "&supervisor=" + supervisor1 +"&inspector=" + inspector1;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("-----------", "url-----" + url);
        showDialog();
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();

                if (result != null) {
                    Log.e("---------", "result==fff===" + result);

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);

                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else{
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_UP;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                }
            } }.start();
    }

    //简体转成繁体
    public String change(String changeText) {
        try {
            JChineseConvertor jChineseConvertor = JChineseConvertor.getInstance();
            changeText = jChineseConvertor.s2t(changeText);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeText;
    }
    //繁体转成简体
    public String change1(String changeText) {
        try {
            JChineseConvertor jChineseConvertor = JChineseConvertor
                    .getInstance();
            changeText = jChineseConvertor.t2s(changeText);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeText;
    }
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
                            finish();
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
                            finish();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
