package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CarTypeListAdapter;
import com.example.administrator.yanfoxconn.adapter.ZhiyinshuiExceListAdapter;
import com.example.administrator.yanfoxconn.bean.CarType;
import com.example.administrator.yanfoxconn.bean.ExceInfo;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * 車輛點檢進度查看
 * 車輛類型選擇
 */
public class CarCheckProgressTypeListActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息
    private final int MESSAGE_JUMP = 4;//跳轉維護界面
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_CHECK = 6;

    private List<CarType> carTypeList;
    private CarTypeListAdapter mCarTypeListAdapter;//點檢列表適配器

    private String dim_id;//二維碼主鍵
    private String type,user_type;//類別,人員類別
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交


    @BindView(R.id.lv_option)
    ListView lvOption;//異常列表
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhiyinshui_exce_list);
        ButterKnife.bind(this);
        dim_id = getIntent().getStringExtra("dim_id");
        type = getIntent().getStringExtra("type");
        user_type = getIntent().getStringExtra("user_type");
        tvTitle.setText("選擇車輛類別");
        btnBack.setOnClickListener(this);

        lvOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CarCheckProgressTypeListActivity.this, ComAbRouteListActivity.class);
                FoxContext.getInstance().setType(carTypeList.get(i).getType());
                intent.putExtra("type",carTypeList.get(i).getType());
                intent.putExtra("flag","C");
                startActivity(intent);
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_release:
                // upMessage(qrResult[0], FoxContext.getInstance().getLoginId());
                break;
            case R.id.btn_title_right:
                aboutAlert(getResources().getString(R.string.update_confirm),MESSAGE_UP);
                break;
        }
    }

    private void getMessage(){

        showDialog();
        final String url;
//        SimpleDateFormat sdfAfter = new SimpleDateFormat("yyyy-MM-dd");
//        String nowDateTime = sdfAfter.format(Calendar.getInstance().getTime());

        url = Constants.HTTP_CAR_TYPE ;

        Log.e("-----------", "url==" + url);
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
                        carTypeList = new ArrayList<CarType>();

                            for (JsonElement type : array) {
                                CarType humi = gson.fromJson(type, CarType.class);
                                carTypeList.add(humi);
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


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(CarCheckProgressTypeListActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    if (carTypeList!=null&&carTypeList.size()!=0){
                        mCarTypeListAdapter = new CarTypeListAdapter(CarCheckProgressTypeListActivity.this,carTypeList);
                        lvOption.setAdapter(mCarTypeListAdapter);
                    }
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
                    break;
                case MESSAGE_JUMP://跳轉維護異常界面
                    worningAlert(msg.obj.toString(),MESSAGE_JUMP);
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
                            finish();
                        }else if(type==MESSAGE_UP){
//                            finish();
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

    @Override
    protected void onStart() {
        super.onStart();
        getMessage();
    }
}