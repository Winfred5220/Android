package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CrossListsAdapter;
import com.example.administrator.yanfoxconn.bean.CrossScanList;
import com.example.administrator.yanfoxconn.bean.CrossScanMessage;
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
 * 人工跨區無紙化作業 掃描界面
 * Created by song on 2018/3/30.
 */

public class CrossScanActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_show)
    TextView tvShow;
    @BindView(R.id.tv_packing_no)
    TextView tvNo;//單號
    @BindView(R.id.tv_flow_to)
    TextView tvFlowTo;//流向
    @BindView(R.id.tv_start_name)
    TextView tvStart;//出發地
    @BindView(R.id.tv_end_name)
    TextView tvEnd;//目的地
    @BindView(R.id.tv_type)
    TextView tvType;//種類
    @BindView(R.id.tv_num)
    TextView tvNum;//件數
    @BindView(R.id.tv_name)
    TextView tvName;//攜帶者
    @BindView(R.id.lv_list)
    ListView listView;//列表
    @BindView(R.id.btn_ok)
    Button btnOk;//確認

    private String qrResult;//掃描結果
    private String url;//地址
    private String result;//
    private List<CrossScanMessage> crossMessage;//信息
    private List<CrossScanList> crossScanLists;//列表

    private CrossListsAdapter crossListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_scan);
        ButterKnife.bind(this);

        Bundle bundle = this.getIntent().getExtras();
        qrResult = bundle.getString("result");

        tvTitle.setText("人工跨區無紙化作業");
        btnBack.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        if (FoxContext.getInstance().getLoginId().equals("YTJW")){
           btnOk.setVisibility(View.GONE);
        }
        getMessage(qrResult);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_ok:
                if (FoxContext.getInstance().getLoginId().equals("")){
                    Log.e("--wu--","FoxContext.getInstance().getLoginId()=="+FoxContext.getInstance().getLoginId());
                    ToastUtils.showLong(CrossScanActivity.this,"賬號出現問題,請重新登陸!");
                }else{
                    Log.e("--you--","FoxContext.getInstance().getLoginId()=="+FoxContext.getInstance().getLoginId());
//                    String name =     URLEncoder.encode(URLEncoder.encode(names.toString(), "UTF-8"), "UTF-8");
                    upMessage(tvNo.getText().toString(), FoxContext.getInstance().getLoginId());}
                break;
        }
    }

    private void getMessage(final String qrResult) {
        showDialog();
        url = Constants.HTTP_EXPORTIO_SERVLET + "?code=" + qrResult;
        Log.e("-----------", "url-----" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();

                Gson gson = new Gson();
                Log.e("----fff-url----", url.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        crossMessage = new ArrayList<CrossScanMessage>();

                        for (JsonElement type : array) {
                            CrossScanMessage humi = gson.fromJson(type, CrossScanMessage.class);
                            crossMessage.add(humi);

                        }

                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();
                        crossScanLists = new ArrayList<CrossScanList>();
                        for (JsonElement type1 : array1) {
                            CrossScanList humi1 = gson.fromJson(type1, CrossScanList.class);
                            crossScanLists.add(humi1);
                        }


                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else if(errCode.equals("300")){
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        crossMessage = new ArrayList<CrossScanMessage>();

                        for (JsonElement type : array) {
                            CrossScanMessage humi = gson.fromJson(type, CrossScanMessage.class);
                            crossMessage.add(humi);

                        }

                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();
                        crossScanLists = new ArrayList<CrossScanList>();
                        for (JsonElement type1 : array1) {
                            CrossScanList humi1 = gson.fromJson(type1, CrossScanList.class);
                            crossScanLists.add(humi1);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SHOW;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }else{
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
    }

    private void upMessage(String id, String scode) {
        showDialog();

        url = Constants.HTTP_EXPORTIOUPDATE_SERVLET + "?id=" + id + "&scode=" + scode;
        Log.e("-----------", "url-----" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();

                Gson gson = new Gson();
                Log.e("----fff-url----", url.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
//                        JsonArray array = jsonObject.get("data").getAsJsonArray();
//                        carScanList = new ArrayList<CarScan>();
//                        for (JsonElement type : array) {
//                            CarScan humi = gson.fromJson(type, CarScan.class);
//                            carScanList.add(humi);
//                        }

                        Message message = new Message();
                        message.what = MESSAGE_UP;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    } else  {
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_UP;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                    break;
                case MESSAGE_SHOW://顯示提醒
                    setText();
                    tvShow.setVisibility(View.VISIBLE);
                    tvShow.setText(msg.obj.toString());
            }
            super.handleMessage(msg);
        }
    };

    private void setText() {
        tvNo.setText(crossMessage.get(0).getBCQ01());
        tvFlowTo.setText(crossMessage.get(0).getBCQ00());
        tvStart.setText(crossMessage.get(0).getBCQ22());
        tvEnd.setText(crossMessage.get(0).getBCQ23());
        tvType.setText(crossMessage.get(0).getBCQ09());
        tvNum.setText(crossMessage.get(0).getBCQ08());
        tvName.setText(crossMessage.get(0).getBCQ06());

        crossListAdapter = new CrossListsAdapter(CrossScanActivity.this, crossScanLists);
        listView.setAdapter(crossListAdapter);
        setListViewHeightBasedOnChildren(listView);
    }

    /**
     * 用於解決scrollview嵌套listview導致只顯示一條數據的問題
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
         // 获取ListView对应的Adapter
        CrossListsAdapter listAdapter = (CrossListsAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }


    private void aboutAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("二維碼掃描信息")
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
