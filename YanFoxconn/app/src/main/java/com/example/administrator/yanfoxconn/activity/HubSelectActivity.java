package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.HubReceiveListAdapter;
import com.example.administrator.yanfoxconn.bean.HubList;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.NormalLoadPictrue;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HubSelectActivity extends BaseActivity implements View.OnClickListener{

    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_NETMISTAKE = 4;//網絡錯誤
    private SimpleDateFormat formatterold = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String area,name,date;
    private String url;//地址
    private String result;//网络获取结果
    private List<HubList> hubReceiveList;//物品列表
    private HubReceiveListAdapter hubReceiveListAdapter;//物品列表适配器
    private Dialog dia;
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.lv_selt)
    MyListView lvSelt;//物品列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_select);
        ButterKnife.bind(this);
        tvTitle.setText("領取記錄");
        btnBack.setOnClickListener(this);
        area = getIntent().getStringExtra("area");
        name = getIntent().getStringExtra("name");
        date = getIntent().getStringExtra("date");
        Log.e("-----------", "onCreate: " + area + " " + name + " " + date);
        getMessage(area, date, name);

        lvSelt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("------position------", "position: "+hubReceiveList.get(i).getRECEIVE_NAME() );
                String imgPath = Constants.HTTP_HUB_IMG + hubReceiveList.get(i).getRECEIVE_NAME();
                dia = new Dialog(HubSelectActivity.this, R.style.edit_AlertDialog_style);
                dia.setContentView(R.layout.activity_start_dialog);
                ImageView imageView = (ImageView) dia.findViewById(R.id.iv_image);
                new NormalLoadPictrue().getPicture(imgPath,imageView);
                //选择true的话点击其他地方可以使dialog消失，为false的话不会消失
                dia.setCanceledOnTouchOutside(true); // Sets whether this dialog is
                Window w = dia.getWindow();
                WindowManager.LayoutParams lp = w.getAttributes();
                lp.x = 0;
                lp.y = 40;
                dia.onWindowAttributesChanged(lp);
                dia.show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
        }
    }

    private void getMessage(String area, String date,String name) {
        showDialog();

        try {
            String area1 =  URLEncoder.encode(URLEncoder.encode(area.toString(), "UTF-8"), "UTF-8");
            String name1 =  URLEncoder.encode(URLEncoder.encode(name.toString(), "UTF-8"), "UTF-8");
            url = Constants.HTTP_HUB_TAKE_VIEW_SERVLET + "?area=" + area1 + "&receive_date=" + date + "&applyer=" + name1;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("-----------", "url-----" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        String ss = jsonObject.get("data").toString();
                        hubReceiveList = new ArrayList<HubList>();
                        try {
                            JSONArray array = new JSONArray(ss);
                            for(int i=0;i<array.length();i++){
                                JSONObject goods = array.getJSONObject(i);// 遍历 jsonarray 数组，把每一个对象转成 json 对象
                                HubList hub = new HubList();
                                hub.setORDER_NO(goods.getString("ORDER_NO"));
                                hub.setRECEIVE_COUNT(goods.getString("RECEIVE_COUNT"));
                                try {
                                    hub.setRECEIVE_DATE(formatter.format(formatterold.parse(goods.getString("RECEIVE_DATE"))));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                hub.setMATERIAL_NAME(goods.getString("MATERIAL_NAME"));
                                hub.setRECEIVE_NAME(goods.getString("RECEIVE_NAME"));
                                hubReceiveList.add(hub);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                        dismissDialog();

                    } else{
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                } else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
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
                    ToastUtils.showLong(HubSelectActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
                    break;
//                case MESSAGE_SHOW://顯示提醒
//                    setText();
//                    tvShow.setVisibility(View.VISIBLE);
//                    tvShow.setText(msg.obj.toString());
            }
            super.handleMessage(msg);
        }
    };

    private void setText() {
        if(hubReceiveList!=null){
            hubReceiveListAdapter = new HubReceiveListAdapter(hubReceiveList);
            lvSelt.setAdapter(hubReceiveListAdapter);
        }else {
            ToastUtils.showShort(HubSelectActivity.this, "沒有數據!");
        }
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
