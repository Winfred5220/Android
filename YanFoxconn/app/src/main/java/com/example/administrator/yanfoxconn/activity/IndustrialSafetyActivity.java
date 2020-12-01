package com.example.administrator.yanfoxconn.activity;


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

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.DepartListAdapter;
import com.example.administrator.yanfoxconn.bean.DepartListMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.SwipeListView;
import com.example.administrator.yanfoxconn.widget.WorkSafetyWindow;
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
 * 工安巡檢專案 巡檢單位列表
 * Created by wang on 2019/7/29.
 */

public class IndustrialSafetyActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_UPLOAD = 1;
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_DILOG = 3;
    private final int MESSAGE_SET_TEXT = 4;//掃描成功賦值
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.ib_title_right)
    ImageButton ibRight;//右側彈出菜單
    @BindView(R.id.tv_show)
    TextView tvShow;//提示
    @BindView(R.id.lv_case_list)
    SwipeListView slvCase;//巡檢單位列表


    private WorkSafetyWindow workSafetyWindow;
    private int width;
    private int height;

    private List<DepartListMessage> departListMessage;
    private DepartListAdapter departListAdapter;


    private String url;//請求地址
    private String result;//二維碼返回結果
    boolean canAdd = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industrial_safety_list);
        ButterKnife.bind(this);
        WindowManager wm = this.getWindowManager();

        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        tvTitle.setText("工安巡檢");
        btnBack.setVisibility(View.VISIBLE);
        ibRight.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(this);
        ibRight.setOnClickListener(this);

        ibRight.setBackgroundResource(R.mipmap.title_more_icon);

    }

    //listview 側滑點擊事件
    public void clickGoorup() {
        departListAdapter.setOnClickListenerUpOrSee(new DepartListAdapter.OnClickListenerUpOrSee() {
            @Override//查看
            public void OnClickListenerSee(int position) {
                Intent intent = new Intent(IndustrialSafetyActivity.this,IndustrialDangerSeeActivity.class);
                intent.putExtra("caseId",departListMessage.get(position).getId());
                startActivity(intent);
            }
            @Override//上傳
            public void OnClickListenerUp(int position) {
                Intent intent = new Intent(IndustrialSafetyActivity.this,IndustrialAddDangerActivity.class);
                intent.putExtra("caseId",departListMessage.get(position).getId());
                intent.putExtra("address",departListMessage.get(position).getArea()+"-"+departListMessage.get(position).getTung()+"-"+departListMessage.get(position).getFloor());
                intent.putExtra("department",departListMessage.get(position).getProduct()+"-"+departListMessage.get(position).getTechnology());
                startActivity(intent);
            }
        });
    }


    //界面按鈕 點擊事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.ib_title_right:

                if (workSafetyWindow == null) {
                    // 自定义的单击事件
                    OnClickLintener paramOnClickListener = new OnClickLintener();
                    workSafetyWindow = new WorkSafetyWindow(IndustrialSafetyActivity.this,
                            paramOnClickListener, width / 2,
                            height / 4);
                    // 监听窗口的焦点事件，点击窗口外面则取消显示
                    workSafetyWindow.getContentView().setOnFocusChangeListener(
                            new View.OnFocusChangeListener() {

                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        workSafetyWindow.dismiss();
                                    }
                                }
                            });
                }
                //设置默认获取焦点
                workSafetyWindow.setFocusable(true);
                //以某个控件的x和y的偏移量位置开始显示窗口
                workSafetyWindow.showAsDropDown(ibRight, 0, 0);
                //如果窗口存在，则更新
                workSafetyWindow.update();
                break;
        }
    }

    //右上角菜單點擊事件
    class OnClickLintener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_add://添加被巡檢單位
                    Intent intent = new Intent(IndustrialSafetyActivity.this, IndustrialAddActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ll_people://巡檢/陪查人
                    if(canAdd){
                        Intent intent1 = new Intent(IndustrialSafetyActivity.this, IndustrialPersonActivity.class);
                        startActivity(intent1);
                    }else{
                        ToastUtils.showShort(IndustrialSafetyActivity.this, "請先添加被巡檢單位!");
                    }
                    break;
                case R.id.ll_text://優/缺/建議
                    if(canAdd){
                    Intent intent2 = new Intent(IndustrialSafetyActivity.this, IndustrialSuggestActivity.class);
                    startActivity(intent2);
                    }else{
                        ToastUtils.showShort(IndustrialSafetyActivity.this, "請先添加被巡檢單位!");
                    }
                    break;
                case R.id.ll_see://隱患匯總
//                    Intent intent1 = new Intent(IndustrialSafetyActivity.this, CarWriteIdActivity.class);
//                    intent1.putExtra("from", "carList");
//                    startActivity(intent1);
                    break;
                default:
                    break;
            }
        }
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case MESSAGE_TOAST://Toast彈出
//                    if (msg.obj.equals("失敗")) {
//                        ToastUtils.showShort(IndustrialSafetyActivity.this, "沒有巡檢數據!");
//                    } else {
//                        ToastUtils.showShort(IndustrialSafetyActivity.this, msg.obj.toString());
//                    }
//                    break;
                case MESSAGE_UPLOAD:
                    departListAdapter = new DepartListAdapter(IndustrialSafetyActivity.this, departListMessage);
                    slvCase.setAdapter(departListAdapter);
                    setListViewHeightBasedOnChildren(slvCase);
                    clickGoorup();
                    tvShow.setVisibility(View.GONE);
                    canAdd = true;
                    break;
                case MESSAGE_DILOG:
                    tvShow.setVisibility(View.VISIBLE);
                    canAdd = false;
                    ToastUtils.showShort(IndustrialSafetyActivity.this, "沒有數據!");
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(IndustrialSafetyActivity.this,R.string.net_mistake);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    /**
     * 專案列表信息獲取
     */
    public void getDepartList() {
        showDialog();
        url = Constants.HTTP_INDUSSAFE_DEPARTLISTVIEW_SERVLET;
        Log.e("---------url--------", url );
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Gson gson = new Gson();
                dismissDialog();
                if (result != null) {
                    Log.e("---------result--------", result );
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                    if(array.size()!=0){
                        departListMessage = new ArrayList<DepartListMessage>();
                        for (JsonElement type : array) {
                            DepartListMessage humi = gson.fromJson(type, DepartListMessage.class);
                            departListMessage.add(humi);
                        }
                            Message message = new Message();
                            message.what = MESSAGE_UPLOAD;
                            mHandler.sendMessage(message);
                    }else{
                            Message message = new Message();
                            message.what = MESSAGE_DILOG;
                            mHandler.sendMessage(message);
                        }

                }else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                }
            }
        }.start();

    }

    /**
     * 用於解決scrollview嵌套listview導致只顯示一條數據的問題
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        DepartListAdapter listAdapter = (DepartListAdapter) listView.getAdapter();
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

    @Override
    protected void onStart() {
        super.onStart();
        getDepartList();
    }

}
