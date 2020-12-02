package com.example.administrator.yanfoxconn.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.DZFoodAbList;
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
 * on 2020/8/20
 * Description：人資監餐 異常查看及刪除界面
 */
public class DZFoodAbCheckActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_SET_PHOTO = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    @BindView(R.id.tv_title)
    TextView tvTitle;//标题
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnDel;//删除
    @BindView(R.id.tv_name)
    TextView tvName;//餐厅名称
    @BindView(R.id.tv_time)
    TextView tvTime;//日期
    @BindView(R.id.tv_type1)
    TextView tvType1;//类型1
    @BindView(R.id.tv_type2)
    TextView tvType2;//类型2
    @BindView(R.id.tv_type3)
    TextView tvType3;//类型3
    @BindView(R.id.tv_score)
    TextView tvScore;//分数
    @BindView(R.id.tv_desp)
    TextView tvDesp;//描述
    @BindView(R.id.ll_get_image)
    LinearLayout llGetImage;//異常圖片
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dz_food_ab_check);
        ButterKnife.bind(this);
        tvTitle.setText("異常查看");
        btnBack.setText("返回");
        btnDel.setText("刪除");

        btnBack.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnDel.setVisibility(View.VISIBLE);
        getAbMessage(FoxContext.getInstance().getType(), getIntent().getStringExtra("canbie"), getIntent().getStringExtra("dimId"),getIntent().getStringExtra("exceId"));
    }
    private List<ExcePhoto> photos;
    private List<DZFoodAbList> lists;
    private void getAbMessage(String type, String canbie, String dimId,String exceId) {
        String url = Constants.HTTP_RZ_EXCE_VIEW + "?type=" + type + "&canbie=" + "" +"&exce_id="+exceId;
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);
                Log.e("---------", "==fff===" + url);
                dismissDialog();
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        lists = new ArrayList<DZFoodAbList>();
                        for (JsonElement type : array) {
                            DZFoodAbList humi = gson.fromJson(type, DZFoodAbList.class);
                            lists.add(humi);
                        }
                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();

                        JsonArray arrayp = jsonObject1.get("photo").getAsJsonArray();
                        photos = new ArrayList<ExcePhoto>();
                        for (JsonElement type : arrayp) {
                            ExcePhoto humi = gson.fromJson(type, ExcePhoto.class);
                            photos.add(humi);
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
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                    finish();
                }
            }
        }.start();
    }


    private String result;
    private void delete(String type,  String exceId) {
        showDialog();
        final String url = Constants.HTTP_RZ_EXCE_DELETE + "?type=" + type + "&exce_id=" + exceId;
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
                    message.obj = "刪除成功";
                    mHandler.sendMessage(message);

                    finish();
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "請求不成功";
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
                    ToastUtils.showLong(DZFoodAbCheckActivity.this, msg.obj.toString());

                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(DZFoodAbCheckActivity.this, R.string.net_mistake);
                    break;
                case MESSAGE_SET_PHOTO://text賦值
                    tvName.setText(getIntent().getStringExtra("name"));
                    tvTime.setText(lists.get(0).getExce_create_date());
                    tvType1.setText(lists.get(0).getName1());
                    tvType2.setText(lists.get(0).getName2());
                    tvType3.setText(lists.get(0).getName3());
                    tvScore.setText(lists.get(0).getScore());
                    tvDesp.setText(lists.get(0).getExce_desp());
//                    tvType1.setText(lists.get(0).getName1());
                    Log.e("---------------","photos.size()=="+photos.size());
                    for (int i = 0; i < photos.size(); i++) {
                        ImageView imageView = new ImageView(DZFoodAbCheckActivity.this);
                        String imgStr = photos.get(i).getExce_filename1();
                        Log.e("------imgStr-----", "======" + imgStr);
                        byte[] decode = Base64.decode(imgStr, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                        imageView.setImageBitmap(bitmap);
                        imageView.setPadding(0,5,0,5);
                        llGetImage.addView(imageView);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_title_left:
finish();
                break;
            case R.id.btn_title_right:

                delete(FoxContext.getInstance().getType(),getIntent().getStringExtra("exceId"));
                break;
        }
    }
}