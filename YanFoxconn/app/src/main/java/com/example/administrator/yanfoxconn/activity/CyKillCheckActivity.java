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
import com.example.administrator.yanfoxconn.adapter.CzKillCheckLvAdapter;
import com.example.administrator.yanfoxconn.bean.CyCarMessage;
import com.example.administrator.yanfoxconn.bean.CzKillCheckLv;
import com.example.administrator.yanfoxconn.bean.FileName;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
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
 * create by song
 * on 2020/2/28
 * 物流消殺 查驗和刪除界面
 */
public class CyKillCheckActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_SET_TEXT = 2;//tv賦值
    private final int MESSAGE_TOAST = 1;//showToast
    private final int MESSAGE_SHOW_IMAGE = 3;//顯示圖片
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnAdd;//維護

    @BindView(R.id.tv_sq)
    TextView tvSq;//單號
    @BindView(R.id.tv_driver)
    TextView tvDriver;//司機
    @BindView(R.id.tv_car_num)
    TextView tvCarNum;//車牌號
    @BindView(R.id.tv_card_id)
    TextView tvCardId;//身份證
    @BindView(R.id.tv_area)
    TextView tvArea;//区域
    @BindView(R.id.lv_abnormal)
    ListView lvAbnormal;//列表
    @BindView(R.id.lv_picture)
    ListView lvPicture;//圖片列表
    private ViewGroup group;

    private String result;
    private List<CzKillCheckLv> checkLists;
    private CzKillCheckLvAdapter adapter;
    private String url;//請求url
    private List<FileName> fileNameList;//圖片名稱
    private String fenNo;
    Bitmap image = null;
    private CyCarMessage cyCarMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cz_kill_check);
        ButterKnife.bind(this);
        tvTitle.setText("消殺記錄");
        btnAdd.setText("刪除");

        //王寶臣要求,只有他有刪除權限,所以此處寫死賬號
        if (FoxContext.getInstance().getLoginId().equals("F2706331")||FoxContext.getInstance().getLoginId().equals("F3416918")||FoxContext.getInstance().getLoginId().equals("S0090872")){
        btnAdd.setVisibility(View.VISIBLE);}
        btnBack.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        fenNo=getIntent().getStringExtra("fenNo");
        cyCarMessage = (CyCarMessage) getIntent().getSerializableExtra("carMessage");

        tvSq.setText(cyCarMessage.getAPPLY_NO());
        tvDriver.setText(cyCarMessage.getDRIVER_NAME());
        tvCarNum.setText(cyCarMessage.getCAR_NUM());
        tvCardId.setText(cyCarMessage.getDRIVER_CARD());
        group = (ViewGroup) findViewById(R.id.ll_get_image);

        showDialog();
        getAbnormal();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:
                delAbnormal();//刪除
                break;
        }
    }
    JsonArray array;
    Gson gson;
    private void delAbnormal(){
        showDialog();
        url = Constants.HTTP_XS_SAFEDATADEL_SERVLET + "?xs_no=" + fenNo;
        Log.e("-----","fff--"+url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                gson = new Gson();
//                Log.e("---fff--result----", result.toString());

                if (result != null) {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "刪除成功!";
                    mHandler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "請求不成功";
                    mHandler.sendMessage(message);
            }   }
        }.start();
    }

    private void getAbnormal() {
//        showDialog();
        url = Constants.HTTP_XS_CZ_SAFEDATAQUERY_SERVLET + "?xs_no=" + fenNo;
        Log.e("-----","fff--"+url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                 gson = new Gson();
//                Log.e("---fff--result----", result.toString());

                if (result != null) {
                    Log.e("---fff--result----", result.toString());
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                         array = jsonObject.get("data").getAsJsonArray();
                        checkLists = new ArrayList<CzKillCheckLv>();

                        for (JsonElement type : array) {
                            CzKillCheckLv humi = gson.fromJson(type, CzKillCheckLv.class);
                            checkLists.add(humi);

                        }
                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = checkLists;
                        mHandler.sendMessage(message);

                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array1 = jsonObject1.get("photo").getAsJsonArray();
                        fileNameList = new ArrayList<FileName>();
                        for (JsonElement type1 : array1) {
                            FileName humi1 = gson.fromJson(type1, FileName.class);
                            fileNameList.add(humi1);
                        }

                        for (int i = 0; i < fileNameList.size(); i++) {
                            Log.e("-----fileNameList----", fileNameList.get(i).getREC_NAME().toString());
//                            stringVoidBitmapAsyncTask.execute(fileNameList.get(i).getExce_filename1());
                            image = getBitmapFromServer(fileNameList.get(i).getREC_NAME());

                            Message messageFile = new Message();
                            messageFile.what = MESSAGE_SHOW_IMAGE;
                            messageFile.obj = image;
                            mHandler.sendMessage(messageFile);
                        }
                    } else {
                        Log.e("--------", "dd=jsonObject.get(\"errMessage\").getAsString()==" + jsonObject.get("errMessage").getAsString());
//                                ToastUtils.showShort(RouteListActivity.this, jsonObject.get("errMessage").getAsString());
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
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SET_TEXT:
//                    tvAddress.setText(messageList.get(0).getExce_add());
//                    tvTime.setText(messageList.get(0).getExce_time());
//                    tvDescription.setText(messageList.get(0).getExce_desp());
                    adapter = new CzKillCheckLvAdapter(CyKillCheckActivity.this,checkLists);
                    lvAbnormal.setAdapter(adapter);

                    break;
                case MESSAGE_SHOW_IMAGE:
                    addGroupImage((Bitmap) msg.obj);
                    break;
                case MESSAGE_TOAST:
                    ToastUtils.showShort(CyKillCheckActivity.this,msg.obj.toString());
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        getAbnormal();
    }

    //加載圖片
    public static Bitmap getBitmapFromServer(String imagePath) {
        String imgPath = Constants.HTTP_XS_GET_IMG + imagePath;

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
}
