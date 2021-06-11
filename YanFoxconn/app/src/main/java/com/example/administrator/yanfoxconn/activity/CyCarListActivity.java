package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CarListAdapter;
import com.example.administrator.yanfoxconn.adapter.RouteListAdapter;
import com.example.administrator.yanfoxconn.bean.CyCarMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.ComAbPopupWindow;
import com.example.administrator.yanfoxconn.widget.ExListView;
import com.example.administrator.yanfoxconn.widget.IconPopupWindow;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.example.administrator.yanfoxconn.widget.SwipeListView;
import com.example.administrator.yanfoxconn.widget.ThreePopupWindow;
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
 * 物流消殺 未提交車輛列表
 */
public class CyCarListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final int MESSAGE_TOAST = 2;//showToast
    private final int MESSAGE_GET_LIST = 1;//請求成功

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回

    @BindView(R.id.ib_title_right)
    ImageButton ibRight;//右上角菜單
    @BindView(R.id.lv_car_list)
    ExListView lvRouteList;//巡檢進度表
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    private ThreePopupWindow threePopupWindow;

    private String url;//請求地址
    private String result;//返回結果
    private List<CyCarMessage> cyCarMessages;//物流消殺車輛
    private List<CyCarMessage> mList = new ArrayList<>();;//物流消殺車輛
    private CarListAdapter carListAdapter;

    private int width;
    private  int height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cy_car_list);
        ButterKnife.bind(this);
        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        tvTitle.setText("未消殺車輛列表");
        btnBack.setVisibility(View.VISIBLE);

        ibRight.setVisibility(View.VISIBLE);
        ibRight.setBackgroundResource(R.mipmap.title_more_icon);


        btnBack.setOnClickListener(this);
        ibRight.setOnClickListener(this);
        lvRouteList.setOnItemClickListener(this);
        getCyCarList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                FoxContext.getInstance().setTakePic("");
                break;
            case R.id.ib_title_right:

                if (threePopupWindow == null) {
                    // 自定义的单击事件
                    OnClickLintener paramOnClickListener = new OnClickLintener();
                    threePopupWindow = new ThreePopupWindow(CyCarListActivity.this,
                            paramOnClickListener, width/2,
                            height/3);
                    // 监听窗口的焦点事件，点击窗口外面则取消显示
                    threePopupWindow.getContentView().setOnFocusChangeListener(
                            new View.OnFocusChangeListener() {

                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        threePopupWindow.dismiss();
                                    }
                                }
                            });
                }
                //设置默认获取焦点
                threePopupWindow.setFocusable(true);
                //以某个控件的x和y的偏移量位置开始显示窗口
                threePopupWindow.showAsDropDown(ibRight, 0, 0);
                //如果窗口存在，则更新
                threePopupWindow.update();
                break;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()) {
            case R.id.lv_car_list://列表點擊事件

                Intent intent = new Intent(CyCarListActivity.this, CyKillActivity.class);
                intent.putExtra("carMessage", (Serializable)cyCarMessages.get(position));
                intent.putExtra("type","ty");
                startActivity(intent);
                break;
        }
    }

    /**
     * 物流消殺車輛 1:未上傳照片 2:已上傳照片
     */

    public void getCyCarList() {
        showDialog();
        url = Constants.HTTP_CY_SAFEVIEW_SERVLET+"?flag=1";
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "=getcarLog=fff===" + url);
                Gson gson = new Gson();
                String response = result;
                Log.e("---------", "result==fff===" + response);
                cyCarMessages = new ArrayList<CyCarMessage>();
                if (result != null) {
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();

                        for (JsonElement type : array) {
                            CyCarMessage humi = gson.fromJson(type, CyCarMessage.class);
                            cyCarMessages.add(humi);
                        }
                        Message message = new Message();
                        message.what = MESSAGE_GET_LIST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    } else {
//                        isShowReturn = false;
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                        cyCarMessages.clear();
                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "無數據";
                    mHandler.sendMessage(message);
                }
                dismissDialog();
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    if (msg.obj.equals("失敗")) {
                        ToastUtils.showShort(CyCarListActivity.this, "沒有車輛數據!");
                    } else {
                        ToastUtils.showShort(CyCarListActivity.this, msg.obj.toString());
                        carListAdapter = new CarListAdapter(CyCarListActivity.this, mList,"cy");
                        loadData();
                        lvRouteList.setAdapter(carListAdapter);
                        lvRouteList.setOnLoadMoreListener(new ExListView.OnLoadMoreListener() {
                            @Override
                            public void loadMore() {
                                loadData();
                            }
                        });
                    }
                    break;
                case MESSAGE_GET_LIST:

                    carListAdapter = new CarListAdapter(CyCarListActivity.this, mList,"cy");
                    loadData();
                    lvRouteList.setAdapter(carListAdapter);
                    lvRouteList.setOnLoadMoreListener(new ExListView.OnLoadMoreListener() {
                        @Override
                        public void loadMore() {
                            loadData();
                        }
                    });
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private void loadData() {
        int size = mList.size();
        if (size < cyCarMessages.size()) {
            for (int i = size; i < size + 20; i++) {
                mList.add(cyCarMessages.get(i));
            }
        }
        lvRouteList.setLoadCompleted(size >= cyCarMessages.size() ? true : false);
        carListAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onStart() {

        super.onStart();
        Log.e("---------", "onStart");

    }
    class OnClickLintener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_one:
                    Intent intent = new Intent(CyCarListActivity.this, CyDelListActivity.class);
                    intent.putExtra("from","one");
                    startActivity(intent);
                    break;
                case R.id.ll_two:
                    Intent intent2 = new Intent(CyCarListActivity.this, CYInputCarNoActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.ll_three:
                    Intent intent3 = new Intent(CyCarListActivity.this, CyDelListActivity.class);
                    intent3.putExtra("from","three");
                    startActivity(intent3);

                default:
                    break;
            }
        }
    }

}
