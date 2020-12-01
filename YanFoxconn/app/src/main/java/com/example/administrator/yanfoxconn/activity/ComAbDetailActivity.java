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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.AbnormalMessage;
import com.example.administrator.yanfoxconn.bean.ExcePhoto;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
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
 * on 2020/7/31
 * Description：
 */
public class ComAbDetailActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_SET_PHOTO = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息
    private final int MESSAGE_JUMP = 4;//跳轉維護界面
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_CHECK = 6;

    //    private List<ZhiyinshuiExceMsg> mZhiyinshuiExceMsg;//點檢異常項
    private List<ExcePhoto> photos;


    String exceId, dimId;
    private AbnormalMessage abnormalMessage;
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnDel;//提交


    @BindView(R.id.ll_get_image)
    LinearLayout llGetImage;//異常圖片
    @BindView(R.id.tv_content)
    TextView tvContent;//點檢項
    @BindView(R.id.tv_description)
    TextView tvDescribe;//描述
    @BindView(R.id.tv_time)
    TextView tvTime;//時間

    private String creater = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_ab_detail);
        ButterKnife.bind(this);

        tvTitle.setText("異常詳情");
        btnDel.setText("刪除");
        if (creater.equals(FoxContext.getInstance().getLoginId())) {//如果是從點檢進度列表進入,且登錄賬號與異常創建人工號一致,才顯示刪除按鈕
            btnDel.setVisibility(View.VISIBLE);
        } else {
            btnDel.setVisibility(View.GONE);
        }
        btnDel.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        abnormalMessage = (AbnormalMessage) getIntent().getSerializableExtra("ab");
        dimId = getIntent().getStringExtra("dimId");
        exceId = abnormalMessage.getExce_id();
        tvContent.setText(abnormalMessage.getContent());
        tvDescribe.setText(abnormalMessage.getExce_desp());
        tvTime.setText(abnormalMessage.getExce_time());
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
                    ToastUtils.showLong(ComAbDetailActivity.this, msg.obj.toString());

                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(ComAbDetailActivity.this, R.string.net_mistake);
                    break;
                case MESSAGE_SET_PHOTO://text賦值
                    for (int i = 0; i < photos.size(); i++) {
                        ImageView imageView = new ImageView(ComAbDetailActivity.this);
                        String imgStr = photos.get(i).getFile();
                        Log.e("------imgStr-----", "======" + imgStr);
                        byte[] decode = Base64.decode(imgStr, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                        imageView.setImageBitmap(bitmap);
                        llGetImage.addView(imageView);
                    }
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
                        delete(dimId, "D", exceId);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String result;

    private void toSeeAbnormalActivity(String exceId) {
        showDialog();
        final String url = Constants.HTTP_WATER_PHOTO_INFO_SERVLET + "?exce_id=" + exceId;
        Log.e("-------fff--", "url==" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Gson gson = new Gson();
                Log.e("--fff---result----", result.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("photo").getAsJsonArray();
                        photos = new ArrayList<ExcePhoto>();
                        for (JsonElement type : array) {
                            ExcePhoto humi = gson.fromJson(type, ExcePhoto.class);
                            photos.add(humi);
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

    private void delete(String dimId, String flag, String exceId) {
        showDialog();
        final String url = Constants.HTTP_WATER_EXCE_DELETE + "?exce_id=" + exceId + "&dim_id=" + dimId + "&flag=" + flag;
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
        toSeeAbnormalActivity(exceId);
    }
}