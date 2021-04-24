package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CPCSearchAdapter;
import com.example.administrator.yanfoxconn.adapter.GCPeopleAdapter;
import com.example.administrator.yanfoxconn.bean.CPCMessage;
import com.example.administrator.yanfoxconn.bean.GCHead;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.GetMacAddressUtil;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * wang 2021/4/8
 * 成品倉無紙化 銷單模糊查詢界面
 */

public class CPCSearchActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnView;//當天已放行查看
    @BindView(R.id.btn_search)
    Button btnSearch;//搜索
    @BindView(R.id.et_search)
    EditText etSearch;//搜索文字s
    @BindView(R.id.lv_single)
    MyListView lvSingle;

    private List<CPCMessage> cpcHead;
    private CPCSearchAdapter cpcSearchAdapter;
    private String mac = "",ex_dep="",statue="";//Mac地址
    private ArrayList<String> teamList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpc_search);
        ButterKnife.bind(this);

        btnBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        tvTitle.setText("銷單查詢");
        btnView.setOnClickListener(this);
        btnView.setVisibility(View.VISIBLE);
        btnView.setText("已完成");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_search:
                search();
                break;
            case R.id.btn_title_right:

                if (FoxContext.getInstance().getLoginId().equals("")) {
                    ToastUtils.showShort(this, "登錄超時,請重新登陸");
                    return;
                }
                Intent intent = new Intent(CPCSearchActivity.this, WebViewLandScapeActivity.class);
                intent.putExtra("role","IR");
                intent.putExtra("code",FoxContext.getInstance().getLoginId());
                startActivity(intent);
                break;
        }
    }

    //查詢銷單號
    private void search() {

        final String url = Constants.HTTP_CPC_JSON_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        object.addProperty("flag","cpcSearch");
        object.addProperty("id",etSearch.getText().toString().replaceAll(" ",""));
        object.addProperty("ex_dep",ex_dep);

        Log.e("-----object------",  object.toString());

        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
            return;
        }

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
                    Log.e("---------", "==fff===" + url);
                    String result = HttpConnectionUtil.doPostJsonObject(url, object);
                    dismissDialog();
                    Gson gson = new Gson();
                    if (result != null) {
                        Log.e("---------", "result==fff===" + result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String errCode = jsonObject.get("errCode").getAsString();
                        if (errCode.equals("200")) {
                            JsonArray array = jsonObject.get("result").getAsJsonArray();
                            cpcHead = new ArrayList<CPCMessage>();

                            for (JsonElement type : array) {
                                CPCMessage humi = gson.fromJson(type, CPCMessage.class);
                                cpcHead.add(humi);
                            }

                            Message message = new Message();
                            message.what = Constants.MESSAGE_SET_TEXT;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);

                        } else{
                            cpcHead = new ArrayList<CPCMessage>();
                            Message message = new Message();
                            message.what = Constants.MESSAGE_SET_TEXT;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                        }
                    }else{
                        Message message = new Message();
                        message.what = Constants.MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
    //根據MAC地址獲取綁定的門崗及產品處
    private void getBaseMessage() {

        final String url = Constants.HTTP_CPC_JSON_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        object.addProperty("flag","cpcGetMacBase");
        object.addProperty("mac",mac);

        Log.e("-----object------",  object.toString());

        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
            return;
        }

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
                    Log.e("---------", "==fff===" + url);
                    String result = HttpConnectionUtil.doPostJsonObject(url, object);
                    dismissDialog();
                    Gson gson = new Gson();
                    if (result != null) {
                        Log.e("---------", "result==fff===" + result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String errCode = jsonObject.get("errCode").getAsString();
                        if (errCode.equals("200")) {
                            JsonArray array = jsonObject.get("result2").getAsJsonArray();
                            teamList = new ArrayList<>();
                            teamList.add("請選擇");
                            for (int i=0;i<array.size();i++){
                               String temp = array.get(i).getAsJsonObject().get("name").getAsString();
                               teamList.add(temp);
                           }
                            JsonArray array2 = jsonObject.get("result").getAsJsonArray();
                            for (int i=0;i<array2.size();i++){
                                String temp = array2.get(i).getAsJsonObject().get("name").getAsString();
                                ex_dep += temp+",";
                            }
                            ex_dep=ex_dep.substring(0,ex_dep.length()-1);
                            //Log.e("------ex_dep------", ex_dep );

                            Message message = new Message();
                            message.what = Constants.MESSAGE_SET_MATOU;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);

                        } else{
                            Message message = new Message();
                            message.what = Constants.MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                        }
                    }else{
                        Message message = new Message();
                        message.what = Constants.MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                        finish();
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

                case Constants.MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
                    ToastUtils.showLong(CPCSearchActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case Constants.MESSAGE_SET_TEXT://text賦值
                    cpcSearchAdapter = new CPCSearchAdapter(CPCSearchActivity.this, cpcHead);
                    lvSingle.setAdapter(cpcSearchAdapter);
                    if (statue.equals("N")) {
                        lvSingle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(CPCSearchActivity.this, CPCReleaseActivity.class);
                                intent.putExtra("ex_no", cpcHead.get(position).getEx_no());
                                intent.putStringArrayListExtra("teamList", teamList);
                                startActivity(intent);
//                            lvSingle.setAdapter(null);
                            }
                        });
                    }else {
                        lvSingle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(CPCSearchActivity.this, CPCReleaseActivity.class);
                                intent.putExtra("ex_no", cpcHead.get(position).getEx_no());
                                intent.putStringArrayListExtra("teamList", teamList);
                                intent.putExtra("statue", "Y");
                                startActivity(intent);
//                            lvSingle.setAdapter(null);
                            }
                        });
                    }
                    break;
                case Constants.MESSAGE_DELETE_SUCCESS://提交響應

                    break;
                case Constants.MESSAGE_SET_MATOU://
                    search();
                    break;
                case Constants.MESSAGE_NOT_NET:
                    ToastUtils.showLong(CPCSearchActivity.this, "網絡錯誤，請稍後重試！");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 刪除單頭
     * @param roundId 單頭號
     */
    private void delPeople(String roundId) {
        showDialog();
        final String url = Constants.HTTP_HEAD_DELETE + "?In_Random_Id=" + roundId;

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForGet(url);
                dismissDialog();
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
//                        JsonArray array = jsonObject.get("result").getAsJsonArray();


                        Message message = new Message();
                        message.what = Constants.MESSAGE_DELETE_SUCCESS;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
                        Message message = new Message();
                        message.what = Constants.MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
    }

    private void delAlert(String msg, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                   
                        delPeople(cpcHead.get(position).getEx_no());
                    }

                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    /**
     * Android 获取mac地址
     */
    public void getMacAddress(Context context) {
        mac = GetMacAddressUtil.getMacAddress(context);
        if (mac != "") {
            //Toast.makeText(this,"mac獲取成功"+mac, LENGTH_SHORT).show();
            Log.e("----mac----", mac);
            getBaseMessage();
            //Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(mac, 500);
            //ivQrCode.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "Mac地址未獲取!", LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ex_dep="";
        getMacAddress(this);

    }


}
