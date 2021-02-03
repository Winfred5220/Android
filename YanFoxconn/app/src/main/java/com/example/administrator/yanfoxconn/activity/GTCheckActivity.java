package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.administrator.yanfoxconn.adapter.GTCheckListAdapter;
import com.example.administrator.yanfoxconn.adapter.ZhiyinshuiProcessAdapter;
import com.example.administrator.yanfoxconn.bean.GEPeopleMsg;
import com.example.administrator.yanfoxconn.bean.GTMain;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiExceMsg;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
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

    private List<GTMain> gtMains;//項目信息
    private GTCheckListAdapter gtCheckListAdapter;//列表適配器

    private String type="";    //類別
    private String url="";    //獲取點檢進度

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

    @BindView(R.id.btn_search)
    Button btnSearch;//查詢
    @BindView(R.id.btn_today)
    Button btnToday;//今日施工
    @BindView(R.id.btn_undo)
    Button btnUnDo;//未排施工
    @BindView(R.id.lv_process)
    ListView lvProcess;//進度列表

    private String flag="Y";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gt_check);
        ButterKnife.bind(this);

        tvTitle.setText("今日施工");
        btnBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnToday.setOnClickListener(this);
        btnUnDo.setOnClickListener(this);

//        type = getIntent().getStringExtra("type");
        lvProcess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (flag.equals("Y")){
                    ToastUtils.showLong(GTCheckActivity.this,"長按查看");
                }else{
                    Intent intent0 = new Intent(GTCheckActivity.this,GTMainActivity.class);
                    intent0.putExtra("result",gtMains.get(i).getProject_no());
                    intent0.putExtra("flag","S");
                    intent0.putExtra("from","check");
                    intent0.putExtra("message",gtMains.get(i));
                    startActivity(intent0);
                }
            }
        });

        lvProcess.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;
                itemOnLongClick();
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_search:
                getMessage(flag);
                break;
            case R.id.btn_today:
                btnToday.setBackgroundColor(getResources().getColor(R.color.color_7edbf4));
                btnUnDo.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
                flag="Y";
                getMessage(flag);
                tvTitle.setText("今日施工");
                break;
            case R.id.btn_undo:
                btnToday.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
                btnUnDo.setBackgroundColor(getResources().getColor(R.color.color_7edbf4));
                flag="N";
                getMessage(flag);
                tvTitle.setText("今日未施工");
                break;
        }

    }


    private void getMessage(String flag){

         url = Constants.HTTP_YJ_VIEW ;

        JsonObject object = new JsonObject();
        object.addProperty("project_no", etId.getText().toString());//工程案號
        object.addProperty("flag", flag);//今日施工Y 今日不施工N
        object.addProperty("area", etArea.getText().toString());//區域
        object.addProperty("creator_id", FoxContext.getInstance().getLoginId());//工號
        object.addProperty("win_vendor", etChangShang.getText().toString());//廠商
        object.addProperty("project_name", etName.getText().toString());//工程名稱
        object.addProperty("cpc", etDanWei.getText().toString());//產品處

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
                    Log.e("---------", "==fff===" + url);
                    String result = HttpConnectionUtil.doPostJsonObject(url, object);
                    if (result != null) {
                        Gson gson = new Gson();
                        Log.e("---------", "result==fff===" + result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String errCode = jsonObject.get("errCode").getAsString();
                        gtMains = new ArrayList<GTMain>();
                        if (errCode.equals("200")) {
                            JsonArray array = jsonObject.get("data").getAsJsonArray();

                            for (JsonElement type : array) {
                                GTMain humi = gson.fromJson(type, GTMain.class);
                                gtMains.add(humi);
                            }
                            Message message = new Message();
                            message.what = MESSAGE_SET_TEXT;
//                            message.obj = jsonObject.get("errMessage").getAsString();

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
                        message.what = MESSAGE_NETMISTAKE;
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
                case MESSAGE_TOAST://Toast彈出
                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
                    if (gtMains!=null&&gtMains.size()>0){

                        gtMains.clear();
                    }
                    dismissDialog();
                    gtCheckListAdapter = new GTCheckListAdapter(GTCheckActivity.this,gtMains,flag);
                    lvProcess.setAdapter(gtCheckListAdapter);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
//                    finish();
                    Log.e("---------", "result==fff===" + msg.obj.toString());
                    Log.e("---------", "==fff===" + url);

                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(GTCheckActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_UP);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                case MESSAGE_SET_TEXT://進度賦值

                    dismissDialog();
                    gtCheckListAdapter = new GTCheckListAdapter(GTCheckActivity.this,gtMains,flag);
                    lvProcess.setAdapter(gtCheckListAdapter);
//                    if (gtMains != null&&gtMains.size()!=0) {
//
//                        ToastUtils.showShort(GTCheckActivity.this, "有數據!");
//
//                    } else {
//                        ToastUtils.showShort(GTCheckActivity.this, "沒有數據!");
//                    }

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
                            dismissDialog();
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
int position;
    private void itemOnLongClick(){

        lvProcess.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0,0,0,"修改日期");
                if (flag.equals("Y")){
                contextMenu.add(0,1,0,"查看異常");}
            }
        });
    }
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();


        switch (item.getItemId()){
            case 0:
                if (gtMains.get(position).getCount()>0){

                    ToastUtils.showShort(GTCheckActivity.this,"已點檢過，無法修改作業時間！");
                }else{

                    Intent intent0 = new Intent(GTCheckActivity.this,GTMainActivity.class);
                    intent0.putExtra("result",gtMains.get(position).getProject_no());
                    intent0.putExtra("flag","S");
                    intent0.putExtra("from","check");
                    intent0.putExtra("message",gtMains.get(position));
                    startActivity(intent0);
                }
                break;
            case 1:
                Intent intent = new Intent(GTCheckActivity.this,ComAbRouteItemListActivity.class);
                intent.putExtra("dimId",gtMains.get(position).getProject_no());
                intent.putExtra("dName",gtMains.get(position).getProject_name());
                intent.putExtra("creatorId",FoxContext.getInstance().getLoginId());
                intent.putExtra("from","GCGL");
//                intent.putExtra("scId",routeMessageList.get(position).getSc_id());
                startActivity(intent);
                break;
        }
        return super.onContextItemSelected(item);
    }
}
