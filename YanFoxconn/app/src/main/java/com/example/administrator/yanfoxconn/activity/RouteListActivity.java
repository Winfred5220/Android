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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.RouteListAdapter;
import com.example.administrator.yanfoxconn.bean.AbnormalMessage;
import com.example.administrator.yanfoxconn.bean.RouteMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.IconPopupWindow;
import com.example.administrator.yanfoxconn.widget.SwipeListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
 * 巡檢進度表界面
 * Created by song on 2017/8/29.
 */

public class RouteListActivity extends BaseActivity implements View.OnClickListener {
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
    SwipeListView lvRouteList;//巡檢進度表
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    private IconPopupWindow iconPopupWindow;

    private List<RouteMessage> routeMessageList;
    private RouteListAdapter routeListAdapter;

    private List<AbnormalMessage> abnormalMessageList;

    private String url;//請求地址
    private String qrResult;//二維碼內含結果
    private String result;//二維碼返回結果

    private int qrCount = 0;//總的掃描次數
    private String nowDateTime; // 獲取當前时间

    private String flag;//查詢標誌
    private String id;//
    private int width;
    private  int height;
    private String dFlag;//判斷是否為宿舍巡檢,用不同方法更新列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        ButterKnife.bind(this);
        WindowManager wm = this.getWindowManager();

         width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        Log.e("------", "MainActivityGao==width=="+width+"==height=="+height);
        routeMessageList = (ArrayList<RouteMessage>) getIntent().getSerializableExtra("routeList");
        dFlag =  getIntent().getStringExtra("Dflag");

        tvTitle.setText(R.string.title_route);
        btnBack.setVisibility(View.VISIBLE);
        ibRight.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(this);
        ibRight.setOnClickListener(this);

        ibRight.setBackgroundResource(R.mipmap.title_more_icon);

        if (routeMessageList != null) {
        routeListAdapter = new RouteListAdapter(RouteListActivity.this, routeMessageList);
        lvRouteList.setAdapter(routeListAdapter);
        setListViewHeightBasedOnChildren(lvRouteList);
        clickSeeOrAdd();
        } else {
            ToastUtils.showShort(this, "沒有數據!");
        }
    }

    private void clickSeeOrAdd() {
        routeListAdapter.setOnClickListenerSeeOrAdd(new RouteListAdapter.OnClickListenerSeeOrAdd() {
            @Override
            public void OnClickListenerSee(int position) {
                flag = "SINGLE";
                id = routeMessageList.get(position).getDim_id();
                toSeeAbnormalActivity("SINGLE", routeMessageList.get(position).getDim_id(), null);
            }

            @Override
            public void OnClickListenerAdd(int position) {
                if (routeMessageList.get(position).getCount().equals("0")) {
                    ToastUtils.showShort(RouteListActivity.this, "未巡檢不能添加異常");

                } else {
//                        ToastUtils.showShort(RouteListActivity.this, "添加");
                    Intent intent = new Intent(RouteListActivity.this, UpAbnormalActivity.class);
                    intent.putExtra("flag", "SINGLE");
                    intent.putExtra("scid", routeMessageList.get(position).getSc_id());
//                        Log.e("-----", "scid" + routeMessageList.get(position).getSc_id());
                    intent.putExtra("address", routeMessageList.get(position).getDim_locale());
                    startActivity(intent);

                }
            }
        });
    }

    /**
     * 跳轉查看異常列表界面
     *
     * @param flag
     * @param id
     */
    private void toSeeAbnormalActivity(String flag, String id, final String loginId) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        nowDateTime = formatter.format(curDate);
        showDialog();
        if (loginId != null) {
            url = Constants.HTTP_EXCEPTION_VIEW_SERVLET + "?flag=" + flag + "&id=" + FoxContext.getInstance().getType() +
                    "&time=" + nowDateTime + "&login_id=" + loginId;
        } else if (flag.equals("SINGLE")) {
            url = Constants.HTTP_EXCEPTION_VIEW_SERVLET + "?flag=" + flag + "&id=" + id +
                    "&time=" + nowDateTime;
        } else {
            url = Constants.HTTP_EXCEPTION_VIEW_SERVLET + "?flag=" + flag + "&id=" + FoxContext.getInstance().getType() +
                    "&time=" + nowDateTime;
        }
        Log.e("---------", "==fff===" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Gson gson = new Gson();
                Log.e("---fff--result----", result.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();

                    JsonArray array = jsonObject.get("data").getAsJsonArray();
                    abnormalMessageList = new ArrayList<AbnormalMessage>();
                    for (JsonElement type : array) {
                        AbnormalMessage humi = gson.fromJson(type, AbnormalMessage.class);
                        abnormalMessageList.add(humi);
                    }
                    Log.e("-----------routelist", "abnormalMessageList==" + abnormalMessageList.size());
                    Message message = new Message();
                    if (loginId != null) {
                        message.what = MESSAGE_SELF_ABNORMAL;
                    } else {
                        message.what = MESSAGE_SEE_ABNORMAL;
                    }

                    message.obj = abnormalMessageList;
                    mHandler.sendMessage(message);

                } else {
                    ToastUtils.showShort(RouteListActivity.this, "請求不成功");
                }
            }
        }.start();
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
                    iconPopupWindow = new IconPopupWindow(RouteListActivity.this,
                            paramOnClickListener, width/2,
                            height/3);
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
                    Intent toQrActivity = new Intent(RouteListActivity.this, QrCodeActivity.class);
                    toQrActivity.putExtra("title","二維碼掃描");
                    toQrActivity.putExtra("num","2");
                    startActivityForResult(toQrActivity, SCANNIN_GREQUEST_CODE);
                    break;
                case R.id.ll_add_error:
                    for (int i = 0; i < routeMessageList.size(); i++) {
                        qrCount = qrCount + Integer.valueOf(routeMessageList.get(i).getCount());
                    }
                    if (qrCount > 0) {
                    Intent intent = new Intent(RouteListActivity.this, UpAbnormalActivity.class);
                    intent.putExtra("flag", "ALL");
                    intent.putExtra("scid", "NULL");
                    intent.putExtra("address", "");
                    startActivity(intent);
                    } else {
                        ToastUtils.showLong(RouteListActivity.this, "未開始掃描不能上傳異常");
                    }
                    break;
                case R.id.ll_query_error:
                    flag = "ALL";
                    id = FoxContext.getInstance().getType();
                    toSeeAbnormalActivity(flag, id, null);
                    break;
                case R.id.ll_self_error:
                    flag = "SELF";
                    if (FoxContext.getInstance().getLoginId().equals("")) {
                        ToastUtils.showShort(RouteListActivity.this, "登錄超時,請重新登陸");
                    }
                    id = FoxContext.getInstance().getType();
                    toSeeAbnormalActivity(flag, id, FoxContext.getInstance().getLoginId());
                    break;

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
                            ToastUtils.showLong(RouteListActivity.this,"登錄驗證失效,請重新登陸!!");
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
                        updateRouteList();
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
                    routeListAdapter = new RouteListAdapter(RouteListActivity.this, routeMessageList);
                    lvRouteList.setAdapter(routeListAdapter);
                    setListViewHeightBasedOnChildren(lvRouteList);
                    clickSeeOrAdd();
                    break;
                case MESSAGE_TOAST://Toast彈出
                    if (msg.obj.equals("失敗")) {
                        ToastUtils.showShort(RouteListActivity.this, "此點無異常數據!");
                    } else {
                        ToastUtils.showShort(RouteListActivity.this, msg.obj.toString());
                    }
                    break;
                case MESSAGE_SEE_ABNORMAL:

                    Intent intent = new Intent(RouteListActivity.this, AbnormalListActivity.class);
                    intent.putExtra("flag", flag);
                    intent.putExtra("id", id);
                    intent.putExtra("abnormalList", (Serializable) msg.obj);
                    startActivity(intent);
                    break;
                case MESSAGE_SELF_ABNORMAL:

                    Intent intents = new Intent(RouteListActivity.this, AbnormalSelfListActivity.class);
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
        RouteListAdapter listAdapter = (RouteListAdapter) listView.getAdapter();
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

    //刷新站點列表
    private void updateRouteList() {
        showDialog();
        if (dFlag.equals("")){

            url = Constants.HTTP_DIMEMSION_SERVLET + "?type=" + FoxContext.getInstance().getType();
        }else{

            url = Constants.HTTP_DIMEMSION_NEXT_SERVLET + "?type=" + FoxContext.getInstance().getType()+"&flag="+dFlag;
        }

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

                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        routeMessageList = new ArrayList<RouteMessage>();
                        for (JsonElement type : array) {
                            RouteMessage humi = gson.fromJson(type, RouteMessage.class);
                            routeMessageList.add(humi);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_UPLOAD;
                        mHandler.sendMessage(message);

                    } else {
                        ToastUtils.showShort(RouteListActivity.this, jsonObject.get("errMessage").getAsString());
                    }
                } else {
                    ToastUtils.showShort(RouteListActivity.this, "請求不成功");
                }
            }
        }.start();
    }
}
