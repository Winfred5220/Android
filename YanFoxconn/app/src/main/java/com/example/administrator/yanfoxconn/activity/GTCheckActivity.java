package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.ZhiyinshuiProcessAdapter;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiExceMsg;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description 營建工程管理 今日施工查詢界面
 * @Author song
 * @Date 2021/1/11 10:20
 */
public class GTCheckActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_BUILDING = 6;//教師賦值
    private final int MESSAGE_SET_AREA = 7;//區域賦值
    private final int MESSAGE_SET_FLOOR = 8;//问卷賦值

    private List<ZhiyinshuiExceMsg> mZhiyinshuiExceMsg;//點檢異常項
    private ZhiyinshuiProcessAdapter mZhiyinshuiProcessAdapter;//點檢列表適配器

    private String type="";    //類別
    private String url2="";    //獲取點檢進度轉碼

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題

    @BindView(R.id.et_area)
    EditText etArea;//區
    @BindView(R.id.et_changshang)
    EditText etChangShang;//中標廠商
    @BindView(R.id.et_id)
    EditText etId;//工程案號
    @BindView(R.id.et_name)
    EditText etName;//工程名稱
    @BindView(R.id.et_danwei)
    EditText etDanWei;//申請單位

    @BindView(R.id.btn_today)
    Button btnToday;//今日施工
    @BindView(R.id.btn_undo)
    Button btnUnDo;//未排施工
    @BindView(R.id.lv_process)
    ListView lvProcess;//進度列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhiyinshui_check_process);
        ButterKnife.bind(this);

        tvTitle.setText("今日施工");
        btnBack.setOnClickListener(this);
        btnToday.setOnClickListener(this);
        btnUnDo.setOnClickListener(this);

        type = getIntent().getStringExtra("type");
        itemOnLongClick();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_today:
                btnToday.setBackgroundColor(getResources().getColor(R.color.color_7edbf4));
                btnUnDo.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
                getMessage("Y");
                tvTitle.setText("今日施工");
                break;
            case R.id.btn_undo:
                btnToday.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
                btnUnDo.setBackgroundColor(getResources().getColor(R.color.color_7edbf4));
                getMessage("N");
                tvTitle.setText("今日未施工");
                break;
        }

    }


    private void getMessage(String status){
        showDialog();

//        try {
//         url2 = Constants.HTTP_WATER_PROCESS_SERVLET +"?flag=S&area="+area1+"&building="+building1+"&floor="+floor1+"&status=" + status+"&type="+type;
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url2);
                dismissDialog();
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        mZhiyinshuiExceMsg = new ArrayList<ZhiyinshuiExceMsg>();

                        for (JsonElement type : array) {
                            ZhiyinshuiExceMsg humi = gson.fromJson(type, ZhiyinshuiExceMsg.class);
                            mZhiyinshuiExceMsg.add(humi);

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
                    ToastUtils.showLong(GTCheckActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_UP);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                case MESSAGE_SET_TEXT://進度賦值
                    mZhiyinshuiProcessAdapter = new ZhiyinshuiProcessAdapter(GTCheckActivity.this,mZhiyinshuiExceMsg);
                    lvProcess.setAdapter(mZhiyinshuiProcessAdapter);
                    if (mZhiyinshuiExceMsg != null&&mZhiyinshuiExceMsg.size()!=0) {
                    } else {
                        ToastUtils.showShort(GTCheckActivity.this, "沒有數據!");
                    }
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

                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void itemOnLongClick(){
        lvProcess.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0,0,0,"修改日期");
                contextMenu.add(0,1,0,"查看異常");
            }
        });
    }
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
            case 0:
                ToastUtils.showShort(GTCheckActivity.this,"修改");
                break;
            case 1:
                ToastUtils.showShort(GTCheckActivity.this,"刪除");
                break;
        }
        return super.onContextItemSelected(item);
    }
}
