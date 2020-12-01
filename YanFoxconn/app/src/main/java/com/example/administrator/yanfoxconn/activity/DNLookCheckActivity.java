package com.example.administrator.yanfoxconn.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.MyGridViewAdapter;
import com.example.administrator.yanfoxconn.bean.AbnormalMessage;
import com.example.administrator.yanfoxconn.bean.DNCheckMessage;
import com.example.administrator.yanfoxconn.bean.FileName;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.CustomLruCacheUtils;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
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
 * 宿舍查驗 查驗詳情界面
 * Created by song on 2020/4/28. 08:17
 */
public class DNLookCheckActivity extends BaseActivity implements View.OnClickListener {
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
    TextView tvAddress; //位置
    @BindView(R.id.tv_man)
    TextView tvMan;//人員信息
    @BindView(R.id.tv_time)
            TextView tvTime;//時間
    @BindView(R.id.tv_result)
    TextView tvResult;//選項
    @BindView(R.id.tv_remarks)
            TextView tvRemarks;//描述
    @BindView(R.id.lv_picture)
    ListView lvPicture;//圖片列表
    private ViewGroup group;

    private String flag;//提交類型
    private String url;//請求url
    private String result="";//返回結果
    private DNCheckMessage messageList;//異常信息
    private List<FileName> fileNameList;//圖片名稱
    Bitmap image = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dn_look_check);

        ButterKnife.bind(this);
        btnDelete.setOnClickListener(this);
        btnDelete.setText("刪除");

        flag = getIntent().getStringExtra("flag");
        if (flag.equals("R")){
            tvTitle.setText("人員信息查看");
        }else{tvTitle.setText("環境信息查看");}
        messageList = (DNCheckMessage) getIntent().getSerializableExtra("dnCheckMessageList");
        fileNameList = (List<FileName>)getIntent().getSerializableExtra("photoList");
        init();
    }


    public void init(){
        group = (ViewGroup) findViewById(R.id.ll_get_image);
        tvAddress.setText(messageList.getJc_area()+"-"+messageList.getJc_building()+"-"+messageList.getJc_room());
        if (flag.equals("R")){
            tvMan.setVisibility(View.VISIBLE);
            tvMan.setText( messageList.getJc_bed()+"床-"+messageList.getEmp_name());
        }
        tvTime.setText(messageList.getCreate_date());
//        int result = messageList.getJc_result().split(".").length;
        for (int i =0;i<messageList.getJc_result().size();i++){
           result = result+messageList.getJc_result().get(i).getName()+";";
           Log.e("----","result=="+result);
        }
                tvResult.setText(result);
                tvRemarks.setText(messageList.getJc_remarks());
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(this);
                btnBack.setOnClickListener(this);

                for (int i=0;i<fileNameList.size();i++){
                    ImageView imageView = new ImageView(DNLookCheckActivity.this);
                    String imgStr =fileNameList.get(i).getExce_filename1();
                    byte[] decode = Base64.decode(imgStr,Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decode,0,decode.length);
                    imageView.setImageBitmap(bitmap);
                    group.addView(imageView);
                }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_title_right://刪除
                delete();
                break;
            case R.id.btn_title_left://返回
                finish();
                break;
        }
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
//                case MESSAGE_SET_TEXT:
//                    tvAddress.setText(messageList.get(0).getExce_add());
//                    tvTime.setText(messageList.get(0).getExce_time());
//                    tvDescription.setText(messageList.get(0).getExce_desp());
//
//                    break;
                case MESSAGE_SHOW_IMAGE:
                    addGroupImage((Bitmap) msg.obj);
                    break;
                case MESSAGE_TOAST:
                    ToastUtils.showShort(DNLookCheckActivity.this,"刪除成功");
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //刪除某項異常
    private void delete(){
        showDialog();
        url = Constants.HTTP_SUSHE_DELETE_SERVLET + "?jc_id=" + messageList.getJc_id()+"&flag="+flag;
        Log.e("-------fff-----","url======="+url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                Log.e("--fff----","ddddddddddd==="+result);
                dismissDialog();

                if (result != null) {
                    Log.e("---------", "==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        Message messageDelete = new Message();
                    messageDelete.what = MESSAGE_TOAST;
                    messageDelete.obj = jsonObject.get("errMessage").getAsString();
                    mHandler.sendMessage(messageDelete);
                }
            }}
        }.start();
    }
}
