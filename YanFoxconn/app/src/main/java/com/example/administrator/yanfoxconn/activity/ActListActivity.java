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
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CommonFormsCjfkListAdapter;
import com.example.administrator.yanfoxconn.bean.AQ110Message;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 人資活動發佈列表
 * Created by S1007989 on 2021/5/18.
 */
public class ActListActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_DELETE_SUCCESS = 3;//刪除成功，刷新列表
    private final int MESSAGE_NOT_NET = 4;//顯示提醒

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.lv_people)
    SwipterMenuTest lvPeople;

    private List<AQ110Message> gcHeads;
    private CommonFormsCjfkListAdapter gcPeopleAdapter;
    private SwipeMenuCreator creator;
    private int width,height;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_list);
        ButterKnife.bind(this);
        WindowManager wm = this.getWindowManager();

        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        btnBack.setOnClickListener(this);
        tvTitle.setText("處警反饋列表");

        //初始化
        creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(ActListActivity.this);
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

                SwipeMenuItem doneItem = new SwipeMenuItem(ActListActivity.this);
                doneItem.setBackground(new ColorDrawable(getResources().getColor(R.color.color_42D42B)));
                doneItem.setWidth(dp2px(90));
                doneItem.setTitle("結案");
                doneItem.setTitleSize(18);
                doneItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(doneItem);

            }
        };

        //ItemOnLongClick1();

    }


    private void ItemOnLongClick1() {
//注：setOnCreateContextMenuListener是与下面onContextItemSelected配套使用的
        lvPeople.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v,
                                                    ContextMenu.ContextMenuInfo menuInfo) {

                        //1.通过手动添加来配置上下文菜单选项
                        menu.add(0, 0, 0, "曾加");
                        menu.add(0, 1, 0, "修改");
                        menu.add(0, 2, 0, "删除");

                        //2.通过xml文件来配置上下文菜单选项
//                        MenuInflater mInflater = getMenuInflater();
//                        mInflater.inflate(R.menu.menu_cpc, menu);
                        //super.onCreateContextMenu(menu, v, menuInfo);
                    }
                });
    }

    // 长按菜单响应函数
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        //MID = (int) info.id;// 这里的info.id对应的就是数据库中_id的值
        int posi = info.position;
        switch (item.getItemId()) {
            case 0:
                // 添加操作
                Toast.makeText(ActListActivity.this,
                        gcHeads.get(posi).getWJ_REMARK(),
                        Toast.LENGTH_SHORT).show();
                break;

            case 1:
                // 删除操作
                Toast.makeText(ActListActivity.this,
                        gcHeads.get(posi).getOTHER(),
                        Toast.LENGTH_SHORT).show();
                break;

            case 2:
                // 删除ALL操作
                Toast.makeText(ActListActivity.this,
                        gcHeads.get(posi).getJC_TYPE(),
                        Toast.LENGTH_SHORT).show();
                break;
//            case R.id.add:
//                // 添加操作
//                Toast.makeText(CommonFormsCjfkListActivity.this,
//                        gcHeads.get(posi).getWJ_REMARK(),
//                        Toast.LENGTH_SHORT).show();
//                break;
//
//            case R.id.update:
//                // 删除操作
//                Toast.makeText(CommonFormsCjfkListActivity.this,
//                        gcHeads.get(posi).getOTHER(),
//                        Toast.LENGTH_SHORT).show();
//                break;
//
//            case R.id.delete:
//                // 删除ALL操作
//                Toast.makeText(CommonFormsCjfkListActivity.this,
//                        gcHeads.get(posi).getJC_TYPE(),
//                        Toast.LENGTH_SHORT).show();
//                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);

    }

private void ItemOnLongClick2() {
    lvPeople.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                       final int position, long id) {
            //list.remove(arg2);
            new AlertDialog.Builder(ActListActivity.this)
                    .setTitle("对Item进行操作")
                    .setItems(R.array.thread_menu,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    String[] PK = getResources()
                                            .getStringArray(
                                                    R.array.thread_menu);
                                    Toast.makeText(
                                            ActListActivity.this,
                                            PK[which], Toast.LENGTH_LONG)
                                            .show();
                                    if (PK[which].equals("删除")) {
                                        // 按照这种方式做删除操作，这个if内的代码有bug，实际代码中按需操作
//
                                    }
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                }
                            }).show();
            return true;
        }
    });
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
                break;
        }
    }

    private void search(String id) {

        showDialog();
        final String url = Constants.HTTP_COMMON_FORMS_110CJFK_LIST_SERVLET;

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
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        gcHeads = new ArrayList<AQ110Message>();
                        for (JsonElement type : array) {
                            AQ110Message humi = gson.fromJson(type, AQ110Message.class);
                            gcHeads.add(humi);
                        }
                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else {
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

                    ToastUtils.showLong(ActListActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值

                    // 监测用户在ListView的SwipeMenu侧滑事件。
                    lvPeople.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
                        @Override
                        public void onSwipeStart(int pos) {
                            Log.e("----位置:" + pos, "开始侧滑...");

                        }
                        @Override
                        public void onSwipeEnd(int pos) {
                            Log.e("----位置:" + pos, "侧滑结束...");
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
                                    Intent intent = new Intent(ActListActivity.this, CommonFormsCjfkActivity.class);
                                    intent.putExtra("msg", (Serializable) gcHeads.get(position));
                                    intent.putExtra("from", "add");
                                    startActivity(intent);
//                                    Toast.makeText(GCSerchActivityTest.this, "打开:"+position,Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    Intent intent2 = new Intent(ActListActivity.this, CommonFormsCjfkActivity.class);
                                    intent2.putExtra("msg", (Serializable) gcHeads.get(position));
                                    intent2.putExtra("from", "end");
                                    startActivity(intent2);
//                                    Toast.makeText(GCSerchActivityTest.this, "结案:"+position,Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            // false : 当用户触发其他地方的屏幕时候，自动收起菜单。
                            // true : 不改变已经打开菜单的样式，保持原样不收起。
                            return false;
                        }
                    });
                    gcPeopleAdapter = new CommonFormsCjfkListAdapter(ActListActivity.this, gcHeads);
                    lvPeople.setAdapter(gcPeopleAdapter);
                    break;
                case MESSAGE_DELETE_SUCCESS://提交響應

                    break;
                case MESSAGE_NOT_NET:
                    ToastUtils.showLong(ActListActivity.this, "網絡錯誤，請稍後重試！");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        search("");
    }



}
