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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.GCPeopleAdapter;
import com.example.administrator.yanfoxconn.bean.GCHead;
import com.example.administrator.yanfoxconn.constant.Constants;
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
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * song 2020/12/3
 * 安保部 健康追蹤 模糊查詢界面
 * 测试策划
 */
public class GCSerchActivityTest extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_DELETE_SUCCESS = 3;//刪除成功，刷新列表
    private final int MESSAGE_NOT_NET = 4;//顯示提醒

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_search)
    Button btnSearch;//搜索
    @BindView(R.id.et_search)
    EditText etSearch;//搜索文字s
    @BindView(R.id.lv_people)
    SwipterMenuTest lvPeople;

    private List<GCHead> gcHeads;
    private GCPeopleAdapter gcPeopleAdapter;
    private SwipeMenuCreator creator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gc_search);
        ButterKnife.bind(this);

        btnBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this::onClick);

        tvTitle.setText("體症異常追蹤");

        //初始化
        creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(GCSerchActivityTest.this);
                //设置背景
                openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.color_f5a306)));
                //设置宽，一定要设置不然显示不出来
                openItem.setWidth(dp2px(90));
                //设置标题
                openItem.setTitle("追蹤");
                //设置文字大小
                openItem.setTitleSize(18);
                //设置文字颜色
                openItem.setTitleColor(Color.WHITE);
                //添加到listview中
                menu.addMenuItem(openItem);

                SwipeMenuItem doneItem = new SwipeMenuItem(GCSerchActivityTest.this);
                doneItem.setBackground(new ColorDrawable(getResources().getColor(R.color.color_42D42B)));
                doneItem.setWidth(dp2px(90));
                doneItem.setTitle("結案");
                doneItem.setTitleSize(18);
                doneItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(doneItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(GCSerchActivityTest.this);
                deleteItem.setWidth(dp2px(90));
                deleteItem.setTitle("刪除");
                deleteItem.setTitleSize(16);
                deleteItem.setTitleColor(Color.RED);
                menu.addMenuItem(deleteItem);
            }
        };

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
                search(etSearch.getText().toString());
                break;
        }
    }

    private void search(String id) {

        showDialog();
        final String url = Constants.HTTP_BODY_SELECT + "?json=" + id;

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
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        gcHeads = new ArrayList<GCHead>();

                        for (JsonElement type : array) {
                            GCHead humi = gson.fromJson(type, GCHead.class);
                            gcHeads.add(humi);
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

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

                    ToastUtils.showLong(GCSerchActivityTest.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值

                    lvPeople.setMenuCreator(creator);
                    lvPeople.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                            //index的值就是在SwipeMenu依次添加SwipeMenuItem顺序值，类似数组的下标。
                            //从0开始，依次是：0、1、2、3...
                            switch (index) {
                                case 0:
                                    Intent intent = new Intent(GCSerchActivityTest.this, GCUpOrDoneActivity.class);
                                    intent.putExtra("people", (Serializable) gcHeads.get(position));
                                    intent.putExtra("from", "add");
                                    startActivity(intent);
//                                    Toast.makeText(GCSerchActivityTest.this, "打开:"+position,Toast.LENGTH_SHORT).show();
                                    break;

                                case 1:
                                    Intent intent2 = new Intent(GCSerchActivityTest.this, GCUpOrDoneActivity.class);
                                    intent2.putExtra("people", (Serializable) gcHeads.get(position));
                                    intent2.putExtra("from", "end");
                                    startActivity(intent2);
//                                    Toast.makeText(GCSerchActivityTest.this, "结案:"+position,Toast.LENGTH_SHORT).show();
                                    break;
                                case 2:
                                    delAlert("確認是否刪除！", position);
//                                    Toast.makeText(GCSerchActivityTest.this, "删除:"+position,Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            // false : 当用户触发其他地方的屏幕时候，自动收起菜单。
                            // true : 不改变已经打开菜单的样式，保持原样不收起。
                            return false;
                        }
                    });
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
                    gcPeopleAdapter = new GCPeopleAdapter(GCSerchActivityTest.this, gcHeads);
                    lvPeople.setAdapter(gcPeopleAdapter);
                    break;
                case MESSAGE_DELETE_SUCCESS://提交響應

                    search(etSearch.getText().toString());
                    break;
                case MESSAGE_NOT_NET:
                    ToastUtils.showLong(GCSerchActivityTest.this, "網絡錯誤，請稍後重試！");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 刪除單頭
     *
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

    private void delAlert(String msg, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                   
                        delPeople(gcHeads.get(position).getIn_Random_Id());
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
}
