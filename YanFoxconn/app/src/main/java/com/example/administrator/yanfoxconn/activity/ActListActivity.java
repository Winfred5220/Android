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
import com.example.administrator.yanfoxconn.adapter.ActListAdapter;
import com.example.administrator.yanfoxconn.adapter.CommonFormsCjfkListAdapter;
import com.example.administrator.yanfoxconn.bean.ActivityMsg;
import com.example.administrator.yanfoxconn.bean.CPCMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.SwipterMenuTest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 人資活動發佈列表
 * Created by S1007989 on 2021/5/18.
 */
public class ActListActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnNew;//新增
    @BindView(R.id.tv_tip)
    TextView tvTip;//提示

    @BindView(R.id.lv_people)
    SwipterMenuTest lvPeople;

    private List<ActivityMsg> actList;
    private ActListAdapter actAdapter;
    private SwipeMenuCreator creator;
    private int width,height;
    private ActListActivity mContext = this;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_list);
        ButterKnife.bind(this);
        WindowManager wm = this.getWindowManager();

        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        btnBack.setOnClickListener(this);
        btnNew.setText("新增");
        btnNew.setVisibility(View.VISIBLE);
        btnNew.setOnClickListener(this);
        tvTitle.setText("活動列表");
        lvPeople.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mContext,ActEndActivity.class);
                intent.putExtra("item",actList.get(i));
                startActivity(intent);
            }
        });
        //初始化
//        creator = new SwipeMenuCreator() {
//            @Override
//            public void create(SwipeMenu menu) {
//                SwipeMenuItem openItem = new SwipeMenuItem(mContext);
//                //设置背景
//                openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.color_f5a306)));
//                //设置宽，一定要设置不然显示不出来
//                openItem.setWidth(dp2px(90));
//                //设置标题
//                openItem.setTitle("追蹤");
//                //设置文字大小
//                openItem.setTitleSize(18);
//                //设置文字颜色
//                openItem.setTitleColor(Color.WHITE);
//                //添加到listview中
//                menu.addMenuItem(openItem);
//
//                SwipeMenuItem doneItem = new SwipeMenuItem(mContext);
//                doneItem.setBackground(new ColorDrawable(getResources().getColor(R.color.color_42D42B)));
//                doneItem.setWidth(dp2px(90));
//                doneItem.setTitle("結案");
//                doneItem.setTitleSize(18);
//                doneItem.setTitleColor(Color.WHITE);
//                menu.addMenuItem(doneItem);
//
//            }
//        };

        ItemOnLongClick1();

    }


    private void ItemOnLongClick1() {
//注：setOnCreateContextMenuListener是与下面onContextItemSelected配套使用的
        lvPeople.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v,
                                                    ContextMenu.ContextMenuInfo menuInfo) {

                        //1.通过手动添加来配置上下文菜单选项
                        //menu.add(0, 0, 0, "結束");
                        //menu.add(0, 1, 0, "修改");
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
                // 結束
                Toast.makeText(mContext,
                        actList.get(posi).getAct_id(),
                        Toast.LENGTH_SHORT).show();
                break;
            case 1:
                // 修改操作
                Toast.makeText(mContext,
                        actList.get(posi).getAct_award(),
                        Toast.LENGTH_SHORT).show();
                break;
            case 2:
                // 删除操作
//                SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                Date curDate = new Date(System.currentTimeMillis());
//                String curDates = formatterUse.format(curDate);
//                Log.e("----curDates----", curDates+"--"+actList.get(posi).getAct_end_sign().substring(0,16));
//                if (curDates.compareTo(actList.get(posi).getAct_end_sign().substring(0,16)) < 0){
                    delMessage(actList.get(posi).getAct_id());
//                }else {
//                    ToastUtils.showShort(mContext,"活動已經開始報名，不可刪除！");
//                }

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
            new AlertDialog.Builder(mContext)
                    .setTitle("对Item进行操作")
                    .setItems(R.array.thread_menu,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    String[] PK = getResources()
                                            .getStringArray(
                                                    R.array.thread_menu);
                                    Toast.makeText(
                                            mContext,
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
            case R.id.btn_title_right:
                Intent intent = new Intent(mContext,ActReleaseActivity.class);
                startActivity(intent);
                break;
        }
    }

    //查詢活動
    private void getMessage() {
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
            return;
        }
        final String url = Constants.HTTP_ACTIVITY_JSON_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        object.addProperty("flag","actList");
        Log.e("-----object------",  object.toString());

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
                            actList = new ArrayList<ActivityMsg>();

                            for (JsonElement type : array) {
                                ActivityMsg humi = gson.fromJson(type, ActivityMsg.class);
                                actList.add(humi);
                            }

                            Message message = new Message();
                            message.what = Constants.MESSAGE_SET_TEXT;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);

                        } else{
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
    //刪除活動
    private void delMessage(String act_id) {
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
            return;
        }
        final String url = Constants.HTTP_ACTIVITY_JSON_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        object.addProperty("flag","actDelete");
        object.addProperty("act_id",act_id);

        Log.e("-----object------",  object.toString());

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
                            Message message = new Message();
                            message.what = Constants.MESSAGE_TOAST;
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
                    worningAlert(msg.obj.toString(),Constants.MESSAGE_TOAST);
                    //ToastUtils.showLong(mContext, msg.obj.toString());
//                    finish();
                    break;
                case Constants.MESSAGE_SET_TEXT://text賦值

//                    // 监测用户在ListView的SwipeMenu侧滑事件。
//                    lvPeople.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
//                        @Override
//                        public void onSwipeStart(int pos) {
//                            Log.e("----位置:" + pos, "开始侧滑...");
//
//                        }
//                        @Override
//                        public void onSwipeEnd(int pos) {
//                            Log.e("----位置:" + pos, "侧滑结束...");
//                        }
//                    });
//                    lvPeople.setMenuCreator(creator);
//                    lvPeople.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                            //index的值就是在SwipeMenu依次添加SwipeMenuItem顺序值，类似数组的下标。
//                            //从0开始，依次是：0、1、2、3...
//                            switch (index) {
//                                case 0:
//                                    Intent intent = new Intent(mContext, CommonFormsCjfkActivity.class);
//                                    intent.putExtra("msg", (Serializable) actList.get(position));
//                                    intent.putExtra("from", "add");
//                                    startActivity(intent);
////                                    Toast.makeText(GCSerchActivityTest.this, "打开:"+position,Toast.LENGTH_SHORT).show();
//                                    break;
//                                case 1:
//                                    Intent intent2 = new Intent(mContext, CommonFormsCjfkActivity.class);
//                                    intent2.putExtra("msg", (Serializable) actList.get(position));
//                                    intent2.putExtra("from", "end");
//                                    startActivity(intent2);
////                                    Toast.makeText(GCSerchActivityTest.this, "结案:"+position,Toast.LENGTH_SHORT).show();
//                                    break;
//                            }
//                            // false : 当用户触发其他地方的屏幕时候，自动收起菜单。
//                            // true : 不改变已经打开菜单的样式，保持原样不收起。
//                            return false;
//                        }
//                    });
                    if (actList!=null && actList.size()==0) {
                        tvTip.setVisibility(View.VISIBLE);
                        actAdapter = new ActListAdapter(mContext, actList);
                        lvPeople.setAdapter(actAdapter);
                    }else if (actList!=null && actList.size()!=0){
                        tvTip.setVisibility(View.GONE);
                        actAdapter = new ActListAdapter(mContext, actList);
                        lvPeople.setAdapter(actAdapter);
                    }else{
                        tvTip.setVisibility(View.VISIBLE);
                    }
                    break;
                case Constants.MESSAGE_DELETE_SUCCESS://提交響應

                    break;
                case Constants.MESSAGE_NOT_NET:
                    ToastUtils.showLong(mContext, "網絡錯誤，請稍後重試！");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        getMessage();
    }

    private void aboutAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("確認信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (type==Constants.MESSAGE_TOAST){
                            finish();
                        }else if(type==Constants.MESSAGE_UP){
                            //check();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (type==Constants.MESSAGE_TOAST){
                            finish();
                        }else if(type==Constants.MESSAGE_UP){
                            //check();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void worningAlert(String msg, final int t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (t==Constants.MESSAGE_TOAST) {
                            getMessage();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
