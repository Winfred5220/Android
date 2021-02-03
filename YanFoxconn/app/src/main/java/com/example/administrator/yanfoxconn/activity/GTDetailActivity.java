package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.ComAbRouteListAdapter;
import com.example.administrator.yanfoxconn.adapter.GTDetailLvAdapter;
import com.example.administrator.yanfoxconn.bean.AbnormalMessage;
import com.example.administrator.yanfoxconn.bean.ExcePhoto;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
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
 * Created by Song
 * on 2021/1/16
 * Description：營建 工程管理異常詳情頁
 */
public class GTDetailActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_SET_PHOTO = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息
    private final int MESSAGE_JUMP = 4;//跳轉維護界面
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_CHECK = 6;

    //    private List<ZhiyinshuiExceMsg> mZhiyinshuiExceMsg;//點檢異常項
    private List<ExcePhoto> photos;
    private List<AbnormalMessage> abnormalMessages;
    private GTDetailLvAdapter gtDetailLvAdapter;


    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnDel;//提交
    @BindView(R.id.tv_time)
    TextView tvTime;//查驗時間
    @BindView(R.id.ll_get_image)
    LinearLayout llGetImage;//查驗現場圖片
    @BindView(R.id.lv_detail)
    ListView lvGtDetail;//異常列表

    private String creater = "";
    private String dimId,scId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gt_detail);
        ButterKnife.bind(this);

        tvTitle.setText("異常詳情");
        btnBack.setOnClickListener(this::onClick);
        btnDel.setOnClickListener(this::onClick);
        btnDel.setVisibility(View.VISIBLE);
        btnDel.setText("刪除");

        dimId=getIntent().getStringExtra("dimId");
        scId=getIntent().getStringExtra("scId");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:
                aboutAlert("確定要刪除麼!", MESSAGE_UP);
                break;
        }
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
                    ToastUtils.showLong(GTDetailActivity.this, msg.obj.toString());

                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(GTDetailActivity.this, R.string.net_mistake);
                    break;
                case MESSAGE_SET_PHOTO://text賦值
                    for (int i = 0; i < photos.size(); i++) {
                        ImageView imageView = new ImageView(GTDetailActivity.this);
                        String imgStr = photos.get(i).getEXCE_FILENAME1();
                        Log.e("------imgStr-----", "======" + imgStr);
                        byte[] decode = Base64.decode(imgStr, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                        imageView.setImageBitmap(bitmap);
                        llGetImage.addView(imageView);
                    }
                    gtDetailLvAdapter = new GTDetailLvAdapter(GTDetailActivity.this,abnormalMessages);
                    lvGtDetail.setAdapter(gtDetailLvAdapter);
                    setListViewHeightBasedOnChildren(lvGtDetail);

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
                        delete(dimId,scId);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String result;

    private void getDetail(String scId) {
        showDialog();
        final String url = Constants.HTTP_YJ_EXCE_INFO + "?sc_id=" + scId;
        Log.e("-------fff--", "url==" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForGet(url);
                dismissDialog();
                Gson gson = new Gson();
                Log.e("--fff---result----", result.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
                        JsonArray xcArray = dataObject.get("xcData").getAsJsonArray();
                        photos = new ArrayList<ExcePhoto>();
                        for (JsonElement type : xcArray) {
                            ExcePhoto humi = gson.fromJson(type, ExcePhoto.class);
                            photos.add(humi);
                        }

                        JsonArray ycArray = dataObject.get("ycData").getAsJsonArray();
                        abnormalMessages = new ArrayList<AbnormalMessage>();
                        for (JsonElement type : ycArray) {
                            AbnormalMessage yhumi = gson.fromJson(type, AbnormalMessage.class);
                            abnormalMessages.add(yhumi);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_PHOTO;
                        message.obj = photos;
                        mHandler.sendMessage(message);


                    } else if (errCode.equals("400")) {
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

    private void delete(String dimId,String scId) {
        showDialog();
        final String url = Constants.HTTP_YJ_EXCE_DELETE + "?sc_id=" + scId + "&no=" + dimId ;
        Log.e("-------fff--", "url==" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Gson gson = new Gson();

                if (result != null) {
                    Log.e("--fff---result----", result.toString());
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();

                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = jsonObject.get("errMessage").getAsString();
                    mHandler.sendMessage(message);

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
        getDetail(scId);
    }
    /**
     * 用於解決scrollview嵌套listview導致只顯示一條數據的問題
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        GTDetailLvAdapter listAdapter = (GTDetailLvAdapter) listView.getAdapter();
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

}