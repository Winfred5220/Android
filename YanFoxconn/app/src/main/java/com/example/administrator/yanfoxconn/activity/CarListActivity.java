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

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CarListAdapter;
import com.example.administrator.yanfoxconn.bean.CarListMessage;
import com.example.administrator.yanfoxconn.bean.CarPictureMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.CarPopupWindow;
import com.example.administrator.yanfoxconn.widget.SwipeListView;
import com.example.administrator.yanfoxconn.widget.SwipeListViewThree;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 碼頭車輛放行列表
 * Created by song on 2018/5/14.
 */

public class CarListActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_UPLOAD = 1;
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_DILOG = 3;
    private final int MESSAGE_SET_TEXT = 4;//掃描成功賦值

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.ib_title_right)
    ImageButton ibRight;//右側彈出菜單
    @BindView(R.id.lv_car_list)
    SwipeListViewThree slvCar;//車輛列表


    private CarPopupWindow CarPopupWindow;
    private int width;
    private int height;

    private List<CarListMessage> carListMessages;
    private CarListAdapter carListAdapter;

    private List<CarPictureMessage> carPictureMessages;

    private String url;//請求地址
    private String result;//二維碼返回結果
    private String packingNo;//銷單號

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);
        ButterKnife.bind(this);
        WindowManager wm = this.getWindowManager();

        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        tvTitle.setText("碼頭放行列表");
        btnBack.setVisibility(View.VISIBLE);
        ibRight.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(this);
        ibRight.setOnClickListener(this);
        ibRight.setBackgroundResource(R.mipmap.title_more_icon);

//        carListMessages = (List<CarListMessage>) getIntent().getSerializableExtra("list");
//        if (carListMessages != null) {
//            carListAdapter = new CarListAdapter(CarListActivity.this, carListMessages);
//            slvCar.setAdapter(carListAdapter);
//            setListViewHeightBasedOnChildren(slvCar);
//            clickGoorup();
//        } else {
//            ToastUtils.showShort(this, "沒有數據!");
//        }
    }

    //listview 側滑點擊事件
    public void clickGoorup() {
        carListAdapter.setOnClickListenerGoOrAdd(new CarListAdapter.OnClickListenerGoOrAdd() {
            @Override//放行
            public void OnClickListenerGo(int position) {

                packingNo = carListMessages.get(position).getPacking_no();
                String[] b = packingNo.split(" ");

                getCarPhotoSetDialog(b[0]);

            }

            @Override//上傳
            public void OnClickListenerAdd(int position) {
                String[] b = carListMessages.get(position).getPacking_no().split(" ");

                getCarPhoto(b[0]);
            }

            @Override
            public void OnClickListenerLook(int position) {
                Log.e("-------","position=="+position);
                Intent intent = new Intent(CarListActivity.this,WebViewActivity.class);
                intent.putExtra("role","LMNOP");
                intent.putExtra("num",carListMessages.get(position).getPacking_no());
                startActivity(intent);
            }
        });
    }

    private void aboutAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("放行提示")
                .setMessage(msg)
                .setCancelable(true)

                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub

                        letGo(packingNo);
                    }
                })
                .setNegativeButton("取消", null);


        AlertDialog alert = builder.create();
        alert.show();
    }

    //界面按鈕 點擊事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.ib_title_right:

                if (CarPopupWindow == null) {
                    // 自定义的单击事件
                    OnClickLintener paramOnClickListener = new OnClickLintener();
                    CarPopupWindow = new CarPopupWindow(CarListActivity.this,
                            paramOnClickListener, width / 2,
                            height / 6);
                    // 监听窗口的焦点事件，点击窗口外面则取消显示
                    CarPopupWindow.getContentView().setOnFocusChangeListener(
                            new View.OnFocusChangeListener() {

                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        CarPopupWindow.dismiss();
                                    }
                                }
                            });
                }
                //设置默认获取焦点
                CarPopupWindow.setFocusable(true);
                //以某个控件的x和y的偏移量位置开始显示窗口
                CarPopupWindow.showAsDropDown(ibRight, 0, 0);
                //如果窗口存在，则更新
                CarPopupWindow.update();
                break;
        }
    }




    //右上角菜單點擊事件
    class OnClickLintener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_code://條形碼掃描
                    Intent intent = new Intent(CarListActivity.this, QrCodeActivity.class);
                    intent.putExtra("title", "碼頭條形碼掃描");
                    intent.putExtra("num", "1");
                    startActivity(intent);
                    break;
                case R.id.ll_write://手動輸入
                    Intent intent1 = new Intent(CarListActivity.this, CarWriteIdActivity.class);
                    intent1.putExtra("from", "carList");
                    startActivity(intent1);
                    break;

                default:
                    break;
            }
        }
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    if (msg.obj.equals("失敗")) {
                        ToastUtils.showShort(CarListActivity.this, "沒有巡檢數據!");
                    } else {
                        ToastUtils.showShort(CarListActivity.this, msg.obj.toString());
                    }
                    break;
                case MESSAGE_UPLOAD:
                    if (carListMessages != null) {
                        carListAdapter = new CarListAdapter(CarListActivity.this, carListMessages);
                        slvCar.setAdapter(carListAdapter);
                        setListViewHeightBasedOnChildren(slvCar);
                        clickGoorup();
                    } else {
                        ToastUtils.showShort(CarListActivity.this, "沒有數據!");
                    }
                    break;
                case MESSAGE_DILOG:

                    aboutAlert(msg.obj.toString());
                    break;

            }
            super.handleMessage(msg);
        }
    };

    //獲取照片上傳情況
    public void getCarPhoto(final String packingNo) {
        showDialog();
        url = Constants.HTTP_CARPICTURE_SERVLET + "?packing_no=" + packingNo;
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "=getcarLog=fff===" + url);
                Gson gson = new Gson();
                String response = result;
                dismissDialog();
                if (result != null) {

                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Intent intent = new Intent(CarListActivity.this, CarListUpActivity.class);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        carPictureMessages = new ArrayList<CarPictureMessage>();
                        for (JsonElement type : array) {
                            CarPictureMessage humi = gson.fromJson(type, CarPictureMessage.class);
                            carPictureMessages.add(humi);
                        }
//                        intent.putExtra("returnList", (Serializable) carLogReturnMList);
//                        isShowReturn = true;
                        intent.putExtra("packingNo", packingNo);
                        intent.putExtra("list", (Serializable) carPictureMessages);
                        startActivity(intent);

                    } else {
//                        isShowReturn = false;
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
    }

    //dialog 獲取照片上傳情況  并顯示文字
    public void getCarPhotoSetDialog(final String packingNo) {
        showDialog();
        url = Constants.HTTP_CARPICTURE_SERVLET + "?packing_no=" + packingNo;
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "=getcarLog=fff===" + url);
                Gson gson = new Gson();
                String response = result;
                dismissDialog();
                if (result != null) {
//                    Intent intent = new Intent(CarListActivity.this, CarListUpActivity.class);
                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        carPictureMessages = new ArrayList<CarPictureMessage>();
                        for (JsonElement type : array) {
                            CarPictureMessage humi = gson.fromJson(type, CarPictureMessage.class);
                            carPictureMessages.add(humi);
                        }
//                        intent.putExtra("returnList", (Serializable) carLogReturnMList);
//                        isShowReturn = true;
//                        intent.putExtra("packingNo", packingNo);
//                        intent.putExtra("list", (Serializable) carPictureMessages);
//                        startActivity(intent);
                        String text = "";
                        if (carPictureMessages.get(0).getFilepath().equals("") || carPictureMessages.get(0).getLogopath().equals("") || carPictureMessages.get(0).getShifengpath().equals("") || carPictureMessages.get(0).getRemark8().equals("") || carPictureMessages.get(0).getRemark9().equals("") || carPictureMessages.get(0).getRemark10().equals("")) {
                            if (carPictureMessages.get(0).getFilepath().equals("")) {
                                text = text + "開箱照片,未上傳\\n";
                            }
                            if (carPictureMessages.get(0).getLogopath().equals("")) {
                                text = text + "關箱照片,未上傳\\n";
                            }
                            if (carPictureMessages.get(0).getShifengpath().equals("")) {
                                text = text + "鉛封照片,未上傳\\n";
                            }
                            if (carPictureMessages.get(0).getRemark8().equals("")) {
                                text = text + "三角木照片,未上傳\\n";
                            }
                            if (carPictureMessages.get(0).getRemark9().equals("")) {
                                text = text + "派車單照片,未上傳\\n";
                            }
                            if (carPictureMessages.get(0).getRemark10().equals("")) {
                                text = text + "設備交接單照片,未上傳\\n";
                            }
                        } else {
                            text = "照片都已上傳,可放行.";
                        }

                        Message message = new Message();
                        message.what = MESSAGE_DILOG;
                        message.obj = text.replace("\\n", "\n");
                        mHandler.sendMessage(message);

//
                    } else {
//                        isShowReturn = false;
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
    }


    //放行操作
    public void letGo(String packingNo) {
        showDialog();
        String names = FoxContext.getInstance().getName();
        String name = null;
        try {
            name = URLEncoder.encode(URLEncoder.encode(names, "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            url = Constants.HTTP_CAR_LEAVE_SERVLET + "?packing_no=" +URLEncoder.encode(packingNo,"UTF-8")  + "&createor=" + name;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "=getcarLog=fff===" + url);
                Gson gson = new Gson();
                String response = result;
                dismissDialog();
                if (result != null) {
                    Log.e("---------", "result==fff===" + response);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = result;
                        mHandler.sendMessage(message);

                        uploadCarList();

                    } else {
//                        isShowReturn = false;
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = result;
                        mHandler.sendMessage(message);
                    }

            }
        }.start();
    }

    /**
     * 碼頭放行車輛列表信息獲取
     */
    public void uploadCarList() {
        showDialog();
        url = Constants.HTTP_CARVIEW_SERVLET;
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "=getcarLog=fff===" + url);
                Gson gson = new Gson();
                String response = result;
                dismissDialog();
                if (result != null) {
//                    Intent intent = new Intent(ExListViewActivity.this, CarListActivity.class);
                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        carListMessages = new ArrayList<CarListMessage>();
                        for (JsonElement type : array) {
                            CarListMessage humi = gson.fromJson(type, CarListMessage.class);
                            carListMessages.add(humi);
                        }
//                        intent.putExtra("returnList", (Serializable) carLogReturnMList);
//                        isShowReturn = true;
//                        intent.putExtra("list", (Serializable) carListMessages);
//                        startActivity(intent);
                        Message message = new Message();
                        message.what = MESSAGE_UPLOAD;
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
    /**
     * 用於解決scrollview嵌套listview導致只顯示一條數據的問題
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        CarListAdapter listAdapter = (CarListAdapter) listView.getAdapter();
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
        Log.e("---------", "onStart");
        uploadCarList();
    }
}
