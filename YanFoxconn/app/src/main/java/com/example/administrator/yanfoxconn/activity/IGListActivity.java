package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.GCPeopleAdapter;
import com.example.administrator.yanfoxconn.adapter.IGListAdapter;
import com.example.administrator.yanfoxconn.bean.GCHead;
import com.example.administrator.yanfoxconn.bean.IGMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.SwipterMenuTest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description宿舍寄存 申請和拍配列表介面
 * @Author song
 * @Date 2021/3/31 17:10
 */
public class IGListActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_DELETE_SUCCESS = 3;//刪除成功，刷新列表
    private final int MESSAGE_NOT_NET = 4;//顯示提醒

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnAdd;//新增

    @BindView(R.id.btn_qr)
    Button btnQR;//在職掃碼查詢
    @BindView(R.id.btn_leave)
    Button btnLeave;//離職輸入工號
    @BindView(R.id.btn_check)
    Button btnCheck;//倉庫盤點
    @BindView(R.id.btn_change)
    Button btnChange;//儲位變更
    //    @BindView(R.id.btn_search)
//    Button btnSearch;//搜索
//    @BindView(R.id.et_search)
//    EditText etSearch;//搜索文字s
    @BindView(R.id.lv_people)
    SwipterMenuTest lvPeople;

    private List<IGMessage> igMessages;
    private IGListAdapter igListAdapter;
    private SwipeMenuCreator creator;

    private String from;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ig_list);
        ButterKnife.bind(this);

        from = getIntent().getStringExtra("from");


        btnAdd.setText("新增");
        btnBack.setOnClickListener(this);
//        btnSearch.setOnClickListener(this::onClick);
        btnAdd.setOnClickListener(this);
        btnQR.setOnClickListener(this);
        btnLeave.setOnClickListener(this);
        btnChange.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
        tvTitle.setText("排配列表");
        if (from.equals("leave")) {
            btnLeave.setVisibility(View.GONE);
            btnQR.setVisibility(View.GONE);
            btnCheck.setVisibility(View.GONE);
            btnChange.setVisibility(View.GONE);
            //初始化
            creator = new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {
                    SwipeMenuItem openItem = new SwipeMenuItem(IGListActivity.this);
                    //设置背景
                    openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.color_f5a306)));
                    //设置宽，一定要设置不然显示不出来
                    openItem.setWidth(dp2px(90));
                    //设置标题
                    openItem.setTitle("排配");
                    //设置文字大小
                    openItem.setTitleSize(18);
                    //设置文字颜色
                    openItem.setTitleColor(Color.WHITE);
                    //添加到listview中
                    menu.addMenuItem(openItem);

                    SwipeMenuItem doneItem = new SwipeMenuItem(IGListActivity.this);
                    doneItem.setBackground(new ColorDrawable(getResources().getColor(R.color.color_42D42B)));
                    doneItem.setWidth(dp2px(90));
                    doneItem.setTitle("領取");
                    doneItem.setTitleSize(18);
                    doneItem.setTitleColor(Color.WHITE);
                    menu.addMenuItem(doneItem);
                }
            };
        } else {
            btnAdd.setVisibility(View.VISIBLE);

            //初始化
            creator = new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {
                    SwipeMenuItem openItem = new SwipeMenuItem(IGListActivity.this);
                    //设置背景
                    openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.color_f5a306)));
                    //设置宽，一定要设置不然显示不出来
                    openItem.setWidth(dp2px(90));
                    //设置标题
                    openItem.setTitle("排配");
                    //设置文字大小
                    openItem.setTitleSize(18);
                    //设置文字颜色
                    openItem.setTitleColor(Color.WHITE);
                    //添加到listview中
                    menu.addMenuItem(openItem);


                    SwipeMenuItem addItem = new SwipeMenuItem(IGListActivity.this);
                    addItem.setWidth(dp2px(90));
                    addItem.setBackground(new ColorDrawable(getResources().getColor(R.color.color_74afdb)));
                    addItem.setTitle("刪除");
                    addItem.setTitleSize(18);
                    addItem.setTitleColor(Color.WHITE);
                    menu.addMenuItem(addItem);
                }
            };
        }

    }

    public int dp2px(float dipValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_search:
//                search(etSearch.getText().toString());
                break;
            case R.id.btn_title_right:
                Intent intent = new Intent(IGListActivity.this, CarWriteIdActivity.class);
                intent.putExtra("from", "IG");
                startActivity(intent);
                break;
            case R.id.btn_qr:
                Intent intent2 = new Intent(IGListActivity.this, QrCodeActivity.class);
                intent2.putExtra("title", "二維碼掃描");
                intent2.putExtra("num", "storeQr");
                startActivity(intent2);
                break;
            case R.id.btn_leave:
                Intent intent1 = new Intent(IGListActivity.this, CarWriteIdActivity.class);
                intent1.putExtra("from", "leave");
                startActivity(intent1);
                break;
            case R.id.btn_check:

                break;
            case R.id.btn_change:
                Log.e("-------","btn_change");
                Intent intent3 = new Intent(IGListActivity.this, QrCodeActivity.class);
                intent3.putExtra("title", "二維碼掃描");
                intent3.putExtra("num", "IGChange");
                startActivity(intent3);
                break;
        }
    }

//    private void search(String id) {
//
//        showDialog();
//        final String url = Constants.HTTP_BODY_SELECT + "?json=" + id;
//
//        new Thread() {
//            @Override
//            public void run() {
//                //把网络访问的代码放在这里
//                String result = HttpUtils.queryStringForGet(url);
//
//                dismissDialog();
//                Log.e("---------", "==fff===" + url);
//                Gson gson = new Gson();
//                if (result != null) {
//                    Log.e("---------", "result==fff===" + result);
//
//                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
//                    String errCode = jsonObject.get("errCode").getAsString();
//                    if (errCode.equals("200")) {
//                        Log.e("--fff---------", "result==" + result);
//                        JsonArray array = jsonObject.get("result").getAsJsonArray();
//                        gcHeads = new ArrayList<GCHead>();
//
//                        for (JsonElement type : array) {
//                            GCHead humi = gson.fromJson(type, GCHead.class);
//                            gcHeads.add(humi);
//                        }
//
//                        Message message = new Message();
//                        message.what = MESSAGE_SET_TEXT;
//                        message.obj = jsonObject.get("errMessage").getAsString();
//                        mHandler.sendMessage(message);
//
//                    } else {
//                        Log.e("-----------", "result==" + result);
//                        Message message = new Message();
//                        message.what = MESSAGE_TOAST;
//                        message.obj = jsonObject.get("errMessage").getAsString();
//                        mHandler.sendMessage(message);
//                    }
//                }
//            }
//        }.start();
//    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

                    ToastUtils.showLong(IGListActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    if (from.equals("leave")) {
                        // 监测用户在ListView的SwipeMenu侧滑事件。
                        lvPeople.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
                            @Override
                            public void onSwipeStart(int pos) {
                                Log.d("位置:" + pos, "开始侧滑...");

                            }

                            @Override
                            public void onSwipeEnd(int pos) {
                                Log.d("位置:" + pos, "侧滑结束.");
                            }
                        });
                        lvPeople.setMenuCreator(creator);
                        lvPeople.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                                //index的值就是在SwipeMenu依次添加SwipeMenuItem顺序值，类似数组的下标。
                                //从0开始，依次是：0、1、2、3...
                                switch (index) {
                                    case 0:
                                        if (igMessages.get(position).getS_STATUS().equals("已申請")) {
                                            Intent intent = new Intent(IGListActivity.this, IGMainActivity.class);
                                            intent.putExtra("people", (Serializable) igMessages.get(position));
                                            intent.putExtra("from", "store");
                                            startActivity(intent);
                                        } else {
                                            ToastUtils.showShort(IGListActivity.this, igMessages.get(position).getS_STATUS()+"數據，請勿重複操作！");
                                        }
//                                    Toast.makeText(GCSerchActivityTest.this, "打开:"+position,Toast.LENGTH_SHORT).show();
                                        break;

                                    case 1:
                                        if (igMessages.get(position).getS_LEAVE_FLAG().equals("N")) {
                                            ToastUtils.showShort(IGListActivity.this, "未離職人員，請掃碼領取！");
                                        } else if (igMessages.get(position).getS_STATUS().equals("已排配")){
                                            Log.e("--------------","yipaifeiweilingqu==="+igMessages.get(position).getS_ID());
                                            Intent intent2 = new Intent(IGListActivity.this, IGMainActivity.class);
                                            intent2.putExtra("people", (Serializable) igMessages.get(position));
                                            intent2.putExtra("from", "leave");
                                            startActivity(intent2);
                                        }else{
                                            ToastUtils.showShort(IGListActivity.this, igMessages.get(position).getS_STATUS()+"數據，請勿重複操作！");

                                        }
//                                    Toast.makeText(GCSerchActivityTest.this, "结案:"+position,Toast.LENGTH_SHORT).show();
                                        break;

                                }
                                // false : 当用户触发其他地方的屏幕时候，自动收起菜单。
                                // true : 不改变已经打开菜单的样式，保持原样不收起。
                                return false;
                            }
                        });
                        igListAdapter = new IGListAdapter(IGListActivity.this, igMessages, "leave");
                        lvPeople.setAdapter(igListAdapter);
                    } else {

                        // 监测用户在ListView的SwipeMenu侧滑事件。
                        lvPeople.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
                            @Override
                            public void onSwipeStart(int pos) {
                                Log.d("位置:" + pos, "开始侧滑...");
                            }
                            @Override
                            public void onSwipeEnd(int pos) {
                                Log.d("位置:" + pos, "侧滑结束.");
                            }
                        });
                        lvPeople.setMenuCreator(creator);
                        lvPeople.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                                //index的值就是在SwipeMenu依次添加SwipeMenuItem顺序值，类似数组的下标。
                                //从0开始，依次是：0、1、2、3...
                                switch (index) {
                                    case 0:
                                        if (igMessages.get(position).getS_STATUS().equals("已申請")) {
                                            Intent intent = new Intent(IGListActivity.this, IGMainActivity.class);
                                            intent.putExtra("people", (Serializable) igMessages.get(position));
                                            intent.putExtra("from", "store");
                                            startActivity(intent);
                                        } else {
                                            ToastUtils.showShort(IGListActivity.this, igMessages.get(position).getS_STATUS()+"數據，請勿重複操作！");
                                        }
//                                    Toast.makeText(GCSerchActivityTest.this, "打开:"+position,Toast.LENGTH_SHORT).show();
                                        break;

                                    case 1:
                                        if (igMessages.get(position).getS_STATUS().equals("已申請")) {
                                            delAlert("確認是否刪除！", position);
                                        } else {
                                            ToastUtils.showShort(IGListActivity.this, igMessages.get(position).getS_STATUS()+"數據，不予刪除！");
                                        }
                                        break;
                                }
                                // false : 当用户触发其他地方的屏幕时候，自动收起菜单。
                                // true : 不改变已经打开菜单的样式，保持原样不收起。
                                return false;
                            }
                        });
                        igListAdapter = new IGListAdapter(IGListActivity.this, igMessages, "IG");
                        lvPeople.setAdapter(igListAdapter);
                    }
                    break;
                case MESSAGE_DELETE_SUCCESS://提交響應
                    getStore(FoxContext.getInstance().getName(), "zw");
//                    search(etSearch.getText().toString());
                    break;
                case MESSAGE_NOT_NET:
                    ToastUtils.showLong(IGListActivity.this, "網絡錯誤，請稍後重試！");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 刪除申請單
     *
     * @param sid 申請單號
     */
    private void delPeople(String sid) {
        showDialog();
        final String url = Constants.HTTP_STORE_DELETE_BY_ID + "?sid=" + sid;

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
                        Log.e("--fff---------", "result==" + result);
//                        JsonArray array = jsonObject.get("result").getAsJsonArray();


                        Message message = new Message();
                        message.what = MESSAGE_DELETE_SUCCESS;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
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


    /**
     * 獲取列表
     *
     * @param id
     * @param type
     */
    private void getStore(String id, String type) {
        showDialog();
        String name = null;
        try {
            name = URLEncoder.encode(URLEncoder.encode(id, "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String url = Constants.HTTP_STORE_DEPOSIT_LIST + "?id=" + name + "&type=" + type;

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
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();

                        igMessages = new ArrayList<IGMessage>();

                        for (JsonElement type : array) {
                            IGMessage humi = gson.fromJson(type, IGMessage.class);
                            igMessages.add(humi);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
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

    /**
     * 獲取離職領取列表信息
     *
     * @param id
     * @param type
     */
    private void getLeave(String id, String type) {
        showDialog();
        String name = null;
        try {
            name = URLEncoder.encode(URLEncoder.encode(id, "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String url = Constants.HTTP_STORE_DEPOSIT_LIST + "?id=" + name + "&type=" + type;

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
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();

                        igMessages = new ArrayList<IGMessage>();

                        for (JsonElement type : array) {
                            IGMessage humi = gson.fromJson(type, IGMessage.class);
                            igMessages.add(humi);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
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


    private void delAlert(String msg, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        delPeople(igMessages.get(position).getS_ID());
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

    @Override
    protected void onStart() {
        super.onStart();
        if (from.equals("leave")) {
            getLeave(getIntent().getStringExtra("id"), "user");
        } else {
            getStore(FoxContext.getInstance().getName(), "zw");
        }
    }
}
