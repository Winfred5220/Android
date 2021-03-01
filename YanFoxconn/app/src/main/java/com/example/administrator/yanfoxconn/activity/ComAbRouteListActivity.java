package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.ComAbRouteListAdapter;
import com.example.administrator.yanfoxconn.adapter.RouteListAdapter;
import com.example.administrator.yanfoxconn.bean.AbnormalMessage;
import com.example.administrator.yanfoxconn.bean.ComScanViewMessage;
import com.example.administrator.yanfoxconn.bean.RouteMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.ComAbPopupWindow;
import com.example.administrator.yanfoxconn.widget.IconPopupWindow;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.example.administrator.yanfoxconn.widget.SwipeListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 公用點檢  巡檢進度表界面
 * Created by song on 2020/7/29.
 */

public class ComAbRouteListActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_DIALOG = 0;
    private final int MESSAGE_UPLOAD = 1;
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_SEE_ABNORMAL = 3;
    private final int MESSAGE_SELF_ABNORMAL = 4;

    private final static int SCANNIN_GREQUEST_CODE = 1;

    public static final int MENU_QRCODE = 0;//二維碼掃描
    public static final int MENU_ADD_ERROR = 1;//上報異常
    public static final int MENU_QUERY_ERROR = 2;//查詢異常
    public static final int MENU_SELF_ERROR = 3;//刪除異常

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.ib_title_right)
    ImageButton ibRight;//右側彈出菜單
    @BindView(R.id.lv_route_list)
    MyListView lvRouteList;//巡檢進度表
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    private ComAbPopupWindow iconPopupWindow;

    private List<RouteMessage> routeMessageList;
    private ComAbRouteListAdapter routeListAdapter;

    private List<AbnormalMessage> abnormalMessageList;

    private String url;//請求地址
    private String qrResult;//二維碼內含結果
    private String result;//二維碼返回結果

    private int qrCount = 0;//總的掃描次數
    private String nowDateTime; // 獲取當前时间

    private String flag;//查詢車輛C
    private String id;//
    private String type;//查詢類別
    private int width;
    private  int height;
    private String statut="Y";//點檢狀態,Y已點檢,N未點檢

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_ab_route_list);
        ButterKnife.bind(this);
        WindowManager wm = this.getWindowManager();

        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        Log.e("------", "MainActivityGao==width=="+width+"==height=="+height);
        flag = getIntent().getStringExtra("flag");
        if (flag==null)flag="";
        type = getIntent().getStringExtra("type");
        if (type==null)type="";
        tvTitle.setText("已點檢進度");
        btnBack.setVisibility(View.VISIBLE);
        ibRight.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(this);
        ibRight.setOnClickListener(this);

        ibRight.setBackgroundResource(R.mipmap.title_more_icon);

        lvRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("-------","進入點位異常列表==="+position);
               if (!flag.equals("C")) {
//                getRouteItemList( FoxContext.getInstance().getType(),routeMessageList.get(position).getDim_id());
                   if (!routeMessageList.get(position).getCount().equals("0")) {
                       Intent intent = new Intent(ComAbRouteListActivity.this, ComAbRouteItemListActivity.class);
                       intent.putExtra("dimId", routeMessageList.get(position).getDim_id());
                       intent.putExtra("dName", routeMessageList.get(position).getDim_locale());
                       intent.putExtra("creatorId", FoxContext.getInstance().getLoginId());
                       intent.putExtra("from", "Com");
//                intent.putExtra("scId",routeMessageList.get(position).getSc_id());
                       startActivity(intent);
                   } else {
                       ToastUtils.showLong(ComAbRouteListActivity.this, "無異常可查看!");
                   }
               }
            }
        });
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                FoxContext.getInstance().setTakePic("");
                break;
            case R.id.ib_title_right:

                if (iconPopupWindow == null) {
                    // 自定义的单击事件
                    OnClickLintener paramOnClickListener = new OnClickLintener();
                    iconPopupWindow = new ComAbPopupWindow(ComAbRouteListActivity.this,
                            paramOnClickListener, width/2,
                            height/6);
                    // 监听窗口的焦点事件，点击窗口外面则取消显示
                    iconPopupWindow.getContentView().setOnFocusChangeListener(
                            new View.OnFocusChangeListener() {

                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        iconPopupWindow.dismiss();
                                    }
                                }
                            });
                }
                //设置默认获取焦点
                iconPopupWindow.setFocusable(true);
                //以某个控件的x和y的偏移量位置开始显示窗口
                iconPopupWindow.showAsDropDown(ibRight, 0, 0);
                //如果窗口存在，则更新
                iconPopupWindow.update();
                break;
        }
    }




    class OnClickLintener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_qr_code:
                    Intent intent = new Intent(ComAbRouteListActivity.this, QrCodeActivity.class);
                    intent.putExtra("title", "掃描二維碼");
                    intent.putExtra("num", FoxContext.getInstance().getType());
                    startActivity(intent);
                    finish();
                    break;
                case R.id.ll_n:
                    if (statut.equals("Y")){
                        tvTitle.setText("未點檢進度");
                        statut = "N";
                    }else{
                        tvTitle.setText("已點檢進度");
                        statut = "Y";
                    }
                    if (flag.equals("C")){
                        updateRouteList("C",type,statut,"","","");
                    }else{
                        updateRouteList("D",FoxContext.getInstance().getType(),statut,"","","");
                    }

                    break;
                case R.id.ll_query_error:
                    if (flag.equals("C")){
                        FoxContext.getInstance().setType(type);
                    }
                    Intent intentAb = new Intent(ComAbRouteListActivity.this,ComAbDListActivity.class);

                    startActivity(intentAb);
                    break;
//                case R.id.ll_self_error:
//                    flag = "SELF";
//                    if (FoxContext.getInstance().getLoginId().equals("")) {
//                        ToastUtils.showShort(ComAbRouteListActivity.this, "登錄超時,請重新登陸");
//                    }
//                    id = FoxContext.getInstance().getType();
//                    toSeeAbnormalActivity(flag, id, FoxContext.getInstance().getLoginId());
//                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {

                    String name = null;
                    qrResult = data.getExtras().get("result").toString();
                    String spStr[] = qrResult.split(",");
                    BDLocation location = FoxContext.getInstance().getLocation();
                    String type = spStr[1].substring(0,2);
                    Log.e("----",type);

                    try {
                        name = URLEncoder.encode(URLEncoder.encode(FoxContext.getInstance().getName(), "UTF-8"), "UTF-8");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (location == null || location.getLongitude() == 0.0 || location.getLatitude() == 0.0) {
                        ToastUtils.showLong(this, "位置信息獲取失敗請稍後或移動后重試!");
                    } else if (!FoxContext.getInstance().getType().equals(type)) {
                        Log.e("-------------","FoxContext.getInstance().getType()=="+FoxContext.getInstance().getType()+"==type=="+type);
                        Log.e("------------","route");
                        ToastUtils.showLong(this, "權限不符合,請確認!");
                    } else {

                        if (name.equals("")||FoxContext.getInstance().getLoginId().equals("")){
                            ToastUtils.showLong(ComAbRouteListActivity.this,"登錄驗證失效,請重新登陸!!");
                        }else{
                            showDialog();
                            url = Constants.HTTP_DIMEMSION_STATE_SERVLET + "?id=" + spStr[1] + "&name=" + name +
                                    "&lng=" + FoxContext.getInstance().getLocation().getLongitude() +
                                    "&lat=" + FoxContext.getInstance().getLocation().getLatitude() +
                                    "&login_id=" + FoxContext.getInstance().getLoginId();
                            Log.e("---------", "==fff===" + url);
                            new Thread() {
                                @Override
                                public void run() {
                                    result = HttpUtils.queryStringForPost(url);
                                    Log.e("---------", "==fff===" + result);
                                    dismissDialog();

                                    if (result != null) {
                                        FoxContext.getInstance().setLocation(null);
                                        Message message = new Message();
                                        message.what = MESSAGE_DIALOG;
                                        mHandler.sendMessage(message);
                                    }
                                }
                            }.start();
                        }
                    }
                }
                break;
        }
    }


    private void aboutAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("二維碼掃描信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
//                        updateRouteList();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DIALOG://掃描后彈出
                    aboutAlert(result.toString());
                    break;
                case MESSAGE_UPLOAD://刷新列表
                    routeListAdapter = new ComAbRouteListAdapter(ComAbRouteListActivity.this, routeMessageList);
                    lvRouteList.setAdapter(routeListAdapter);
                    setListViewHeightBasedOnChildren(lvRouteList);

                    break;
                case MESSAGE_TOAST://Toast彈出
                    if (msg.obj.equals("失敗")) {
                        ToastUtils.showShort(ComAbRouteListActivity.this, "此點無異常數據!");
                    } else {
                        ToastUtils.showShort(ComAbRouteListActivity.this, msg.obj.toString());
                    }
                    break;
                case MESSAGE_SEE_ABNORMAL:

                    Intent intent = new Intent(ComAbRouteListActivity.this, AbnormalListActivity.class);
                    intent.putExtra("flag", flag);
                    intent.putExtra("id", id);
                    intent.putExtra("abnormalList", (Serializable) msg.obj);
                    startActivity(intent);
                    break;
                case MESSAGE_SELF_ABNORMAL:

                    Intent intents = new Intent(ComAbRouteListActivity.this, AbnormalSelfListActivity.class);
                    intents.putExtra("flag", flag);
                    intents.putExtra("id", id);
                    intents.putExtra("abnormalList", (Serializable) msg.obj);
                    startActivity(intents);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 用於解決scrollview嵌套listview導致只顯示一條數據的問題
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ComAbRouteListAdapter listAdapter = (ComAbRouteListAdapter) listView.getAdapter();
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


    /**
     * 獲取巡檢進度列表
     * @param flag     D:點位  S:設備
     * @param type     權限
     * @param status   是否可用,Y或N
     * @param area     區,可為空
     * @param building 棟,可為空
     * @param floor    層,可為空
     */
    private void updateRouteList(String flag,String type,String status,String area,String building,String floor) {
        showDialog();
            url = Constants.HTTP_WATER_PROCESS_SERVLET + "?flag="+flag+"&type="+type+"&status="+status+"&area="+area+"&building"+building+"&floor="+floor;
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                result = HttpUtils.queryStringForPost(url);
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                String response = result;
                dismissDialog();
                if (result != null) {
                    Log.e("---------", "==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        if (array!=null) {
                            routeMessageList = new ArrayList<RouteMessage>();
                            for (JsonElement type : array) {
                                RouteMessage humi = gson.fromJson(type, RouteMessage.class);
                                routeMessageList.add(humi);
                            }

                            Message message = new Message();
                            message.what = MESSAGE_UPLOAD;
                            mHandler.sendMessage(message);
                        }else {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = "沒有已巡檢點位.";
                            mHandler.sendMessage(message);
                        }
                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "請求不成功";
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (flag.equals("C")){
            updateRouteList("C",type,statut,"","","");
        }else {
            updateRouteList("D",FoxContext.getInstance().getType(),statut,"","","");
        }

    }

}
