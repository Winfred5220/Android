package com.example.administrator.yanfoxconn.activity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.AbnormalMessage;
import com.example.administrator.yanfoxconn.bean.FileName;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.CustomLruCacheUtils;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 異常詳情界面
 * Created by song on 2017/9/15.
 */

public class AbnormalDetailsActivity extends BaseActivity implements View.OnClickListener {

    private final int MESSAGE_SET_TEXT = 2;//tv賦值
    private final int MESSAGE_TOAST = 1;//showToast
    private final int MESSAGE_SHOW_IMAGE = 3;//顯示圖片
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnDelete;//刪除
    @BindView(R.id.tv_address)
    TextView tvAddress;//地址
    @BindView(R.id.tv_time)
    TextView tvTime;//時間
    @BindView(R.id.tv_description)
    TextView tvDescription;//異常描述
    @BindView(R.id.lv_picture)
    ListView lvPicture;//圖片列表
    private ViewGroup group;
//    @BindView(R.id.ll_get_image)
//    LinearLayout llGetImage;//圖片區域
//    @BindView(R.id.iv_dd)
//    ImageView imdd;
    private String exceId;//異常ID
    private boolean isSelf;//是否為個人異常
    private String url;//請求url
    private String result;//返回結果
    private List<AbnormalMessage> messageList;//異常信息
    private List<FileName> fileNameList;//圖片名稱
    Bitmap image = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abnormal_details);
        ButterKnife.bind(this);

        init();

        getAbnormal();
    }

    private void init() {
        group = (ViewGroup) findViewById(R.id.ll_get_image);
        exceId = getIntent().getExtras().getString("exceId");
        isSelf = getIntent().getExtras().getBoolean("type");

        tvTitle.setText("異常詳情");

        btnBack.setVisibility(View.VISIBLE);
        if (isSelf){
            Log.e("-e------","ooo");
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setText("刪除");
        }else{

            Log.e("-e------","eeo");
        }

        btnBack.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

    }

    private void getAbnormal() {
        showDialog();
        url = Constants.HTTP_EXCEPTION_DETAIL_SERVLET + "?id=" + exceId;
        Log.e("-----","fff--"+url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Gson gson = new Gson();
                Log.e("---fff--result----", result.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        messageList = new ArrayList<AbnormalMessage>();

                        for (JsonElement type : array) {
                            AbnormalMessage humi = gson.fromJson(type, AbnormalMessage.class);
                            messageList.add(humi);

                        }
                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = messageList;
                        mHandler.sendMessage(message);

                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();
                        fileNameList = new ArrayList<FileName>();
                        for (JsonElement type1 : array1) {
                            FileName humi1 = gson.fromJson(type1, FileName.class);
                            fileNameList.add(humi1);

                        }

                        for (int i = 0; i < fileNameList.size(); i++) {
                            Log.e("-----fileNameList----", fileNameList.get(i).getExce_filename1().toString());
//                            stringVoidBitmapAsyncTask.execute(fileNameList.get(i).getExce_filename1());
                            image = getBitmapFromServer(fileNameList.get(i).getExce_filename1());

                            Message messageFile = new Message();
                            messageFile.what = MESSAGE_SHOW_IMAGE;
                            messageFile.obj = image;
                            mHandler.sendMessage(messageFile);
                        }


                    } else {
                        Log.e("--------", "dd=jsonObject.get(\"errMessage\").getAsString()==" + jsonObject.get("errMessage").getAsString());
//                                ToastUtils.showShort(RouteListActivity.this, jsonObject.get("errMessage").getAsString());
//                        Message message = new Message();
//                        message.what = 2;
//                        message.obj = jsonObject.get("errMessage").getAsString();
//                        mHandler.sendMessage(message);
                    }
                } else {
                    ToastUtils.showShort(AbnormalDetailsActivity.this, "請求不成功");
                }

            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right://刪除
                delete();
                break;
        }
    }

    //加載圖片
    public static Bitmap getBitmapFromServer(String imagePath) {
        String imgPath = Constants.HTTP_GET_IMG + imagePath;

        Log.e("---------", "==fff===" + imgPath);
        HttpGet get = new HttpGet(imgPath);
        HttpClient client = new DefaultHttpClient();
        Bitmap bitmap = null;
        CustomLruCacheUtils customLruCache = CustomLruCacheUtils.getInstance();
        bitmap = customLruCache.getBitmapFromMemoryCache(imagePath);
        //先从缓存中读取图片，如果缓存中不存在，再请求网络，从网络读取图片添加至LruCache中
        //启动app后第一次bitmap为null,会先从网络中读取添加至LruCache,如果app没销毁,再执行读取图片操作时
        //就会优先从缓存中读取
        if (bitmap == null) {
            //从网络中读取图片数据

            HttpResponse response = null;
            try {
                response = client.execute(get);
                Log.e("---------", "==fff===" + response);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            bitmap = BitmapFactory.decodeStream(is);   // 关键是这句代码
            customLruCache.addBitmapToMemoryCache(imagePath, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;

    }

    //顯示圖片
    private void addGroupImage(Bitmap bitmap) {
        WindowManager wm = this.getWindowManager();

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        Log.e("--------", "width==" + width + "==height==" + height);
        ImageView imageView = new ImageView(this);
//        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));  //设置图片宽高
        imageView.setLayoutParams(new ViewGroup.LayoutParams(width, width));  //设置图片宽高

        imageView.setImageBitmap(bitmap); //图片资源
        group.addView(imageView); //动态添加图片

    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SET_TEXT:
                    tvAddress.setText(messageList.get(0).getExce_add());
                    tvTime.setText(messageList.get(0).getExce_time());
                    tvDescription.setText(messageList.get(0).getExce_desp());

                    break;
                case MESSAGE_SHOW_IMAGE:
                    addGroupImage((Bitmap) msg.obj);
                    break;
                case MESSAGE_TOAST:
                    ToastUtils.showShort(AbnormalDetailsActivity.this,"刪除成功");
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //刪除某項異常
    private void delete(){
        showDialog();
        url = Constants.HTTP_EXCEPTION_DELETE_SERVLET + "?id=" + exceId;
        Log.e("-------fff-----","url======="+url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                Log.e("--fff----","ddddddddddd==="+result);
                dismissDialog();

                if (result.equals("OK")){
                    Message messageDelete = new Message();
                    messageDelete.what = MESSAGE_TOAST;
                    messageDelete.obj = result;
                    mHandler.sendMessage(messageDelete);
                }
            }
    }.start();
    }


}
