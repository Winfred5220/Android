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
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.RetreatMsg;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.TimeDateUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 人資班導
 * Created by wang on 2020/06/22.
 */
public class BandaoInfoActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_RECORD = 6;

    private String url;//地址
    private String result;//网络获取结果
    private String code;//工號或身份證號
    private List<RetreatMsg> retreatMsg;//退訓信息
    private List<String> typeList = new ArrayList<>();//记录類別
    private String type="";//记录類別
    private List<String> recordList = new ArrayList<>();//班導記錄
    private List<String> countList = new ArrayList<>();//加減分數
    private String record="";//班導記錄
    private String count="";//加減分數
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_code)
    TextView tvCode;//工號
    @BindView(R.id.tv_name)
    TextView tvName;//姓名
    @BindView(R.id.tv_sf_code)
    TextView tvSfCode;//身份證
    @BindView(R.id.tv_report_date)
    TextView tvReportDate;//報到日期
    @BindView(R.id.tv_rl_from)
    TextView tvRlFrom;//人力來源
    @BindView(R.id.tv_yg_type)
    TextView tvYgType;//人力性質
    @BindView(R.id.sp_type)
    Spinner spType;//记录類別
    @BindView(R.id.sp_record)
    Spinner spRecord;//班导记录
    @BindView(R.id.tv_count)
    TextView tvCount;//加减分值
    @BindView(R.id.tv_num)
    TextView tvNum;//考评分数
    @BindView(R.id.tv_grade)
    TextView tvGrade;//考评等级
    @BindView(R.id.tv_person)
    TextView tvPerson;//班导人
    @BindView(R.id.btn_confirm)
    Button btnConfirm;//確認

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandao_info);
        ButterKnife.bind(this);

        tvTitle.setText("班導詳情");
        btnBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        code = getIntent().getStringExtra("code");
        getMessage(code);//獲取詳細信息
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_confirm:
                btnConfirm.setClickable(false);
                aboutAlert("確定要提交嗎？",MESSAGE_SHOW);
                break;
        }
    }

    //獲取詳細資料及記錄類別
    private void getMessage(String code) {
        showDialog();

        url = Constants.HTTP_BANDAO_VIEW_SERVLET + "?code=" + code;

        Log.e("-----------", "url-----" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();

                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        retreatMsg = new ArrayList<RetreatMsg>();
                        for (JsonElement type : array) {
                            RetreatMsg humi = gson.fromJson(type, RetreatMsg.class);
                            retreatMsg.add(humi);
                        }

                        String ss = jsonObject.get("data_base").getAsJsonArray().toString();
                        try {
                            JSONArray data = new JSONArray(ss);
                            String qt = "";
                            for (int i=0;i<data.length();i++) {
                                JSONObject object = data.getJSONObject(i);
                                if(object.getString("type").equals("其他")){
                                    qt=object.getString("type");
                                }else {
                                    typeList.add(object.getString("type"));
                                }
                            }
                            typeList.add(qt);
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                }
            } }.start();
    }
    //頁面顯示賦值
    private void setText() {
        //相差天數
        int stayDays = TimeDateUtils.daysDeviation(retreatMsg.get(0).getReport_date(),retreatMsg.get(0).getTx_date());

        tvCode.setText(retreatMsg.get(0).getCode());
        tvName.setText(retreatMsg.get(0).getName());
        tvSfCode.setText(retreatMsg.get(0).getSf_code());
        tvReportDate.setText(retreatMsg.get(0).getReport_date().substring(0,11));
        tvRlFrom.setText(retreatMsg.get(0).getRl_from());
        tvYgType.setText(retreatMsg.get(0).getYg_type());
        tvPerson.setText(FoxContext.getInstance().getLoginId()+"-"+FoxContext.getInstance().getName());

//        tvTxReason.setText(retreatMsg.get(0).getTx_reason());
//        tvTxHuanjie.setText(retreatMsg.get(0).getTx_huanjie());
//        tvTxType.setText(retreatMsg.get(0).getTx_type());
//        tvTxDate.setText(retreatMsg.get(0).getTx_date());
//        tvStayDate.setText(stayDays+"");
//        if(stayDays>=0&&stayDays<7){
//            tvState.setText("新进未报到");//離職狀態
//        }else{
//            tvState.setText("離職");
//        }

    }
    //給记录類別賦值
    public void setType(){

        //課程下拉列表選擇
        spType.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, typeList));
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = typeList.get(position);
                getRecord(type);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    //獲取班導記錄
    public void getRecord(String type){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    String type1 =  URLEncoder.encode(URLEncoder.encode(type.toString(), "UTF-8"), "UTF-8");
                    String url=Constants.HTTP_BANDAO_BASE_SERVLET +"?type="+type1;
                    Log.e("------url------",url);
                    Request request = new Request.Builder()
                            .url(url)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Log.e("------result------",result);
//                        Log.d("kwwl","response.code()=="+response.code());
//                        Log.d("kwwl","response.message()=="+response.message());
//                        Log.d("kwwl","res=="+result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String ss = jsonObject.get("data").getAsJsonArray().toString();
                        JSONArray data = new JSONArray(ss);
                        recordList.clear();countList.clear();
                        for (int i=0;i<data.length();i++){
                            JSONObject object = data.getJSONObject(i);
                            Log.e("object.getString(count)",object.getString("count"));
                            recordList.add(object.getString("content"));
                            countList.add(object.getString("count"));
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_RECORD;
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
    //班導記錄賦值
    private void setRecord(){
        //班導記錄列表選擇
        spRecord.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recordList));
        spRecord.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                record = recordList.get(position);
                count = countList.get(position);
                tvCount.setText(count);
                int temp = Integer.parseInt(retreatMsg.get(0).getTest_num())+Integer.parseInt(count);
                tvNum.setText(temp+"");
                if (temp>=90){
                    tvGrade.setText("A+");
                }else if(temp>=80&&temp<90){
                    tvGrade.setText("A");
                }else if(temp>=70&&temp<80){
                    tvGrade.setText("B");
                }else if(temp>=60&&temp<70){
                    tvGrade.setText("C");
                }else if(temp<60){
                    tvGrade.setText("D");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    //提交数据
    private void upMessage(){
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        final String url = Constants.HTTP_BANDAO_UPDATE_SERVLET; //此處寫上自己的URL
        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡

        paramMap.put("code",retreatMsg.get(0).getCode());
        paramMap.put("sf_code", retreatMsg.get(0).getSf_code());
        paramMap.put("name1", retreatMsg.get(0).getName());
        paramMap.put("report_date", retreatMsg.get(0).getReport_date());
        paramMap.put("rl_from",retreatMsg.get(0).getRl_from());
        paramMap.put("yg_type", retreatMsg.get(0).getYg_type());
        paramMap.put("record_type", type);
        paramMap.put("bandao_record", record);
        paramMap.put("count_num", count);
        paramMap.put("test_num", tvNum.getText().toString());
        paramMap.put("bandao_code", FoxContext.getInstance().getLoginId());
        paramMap.put("bandao_name", FoxContext.getInstance().getName());

        Log.e("-----------", "paramMap-----" + paramMap.toString());

            //開啟一個新執行緒，向伺服器上傳資料
            new Thread() {
                public void run() {
                    //把网络访问的代码放在这里
                    try {
                        showDialog();
                        result = HttpConnectionUtil.doPostPictureLog(url, paramMap, null);
                        Log.e("---------", "==result===" + result);
                        String tip="";
                        if (result.equals("success")) {
                                tip="提交成功！";
                                Message message = new Message();
                                message.what = MESSAGE_UP;
                                message.obj = tip;
                                mHandler.sendMessage(message);
                        } else{
                            tip="提交失敗！";
                            Message message = new Message();
                            message.what = MESSAGE_UP;
                            message.obj = tip;
                            mHandler.sendMessage(message);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(BandaoInfoActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
                    setType();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
                    break;
                case MESSAGE_SET_RECORD://班導記錄賦值
                  setRecord();
            }
            super.handleMessage(msg);
        }
    };
    //提示彈出框
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
                        }else if(type==MESSAGE_SHOW){
                            upMessage();
                        }
                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (type==MESSAGE_TOAST){

                        }else if(type==MESSAGE_SHOW){
                            btnConfirm.setClickable(true);
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
