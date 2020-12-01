package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.DZFoodPhotoAdapter;
import com.example.administrator.yanfoxconn.bean.ExcePhoto;
import com.example.administrator.yanfoxconn.constant.Constants;
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
 * on 2020/8/14
 * Description：人資監餐 菜品照片查看 和 删除
 */
public class DZFoodPhotoCheckActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_SET_PHOTO = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_DEL_TRUE = 0;//網絡錯誤

    private List<ExcePhoto> photos;
    private DZFoodPhotoAdapter adapter;

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.lv_check_photo)
    ListView lvCheckPhoto;

    private String scId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dz_food_photo_check);
        ButterKnife.bind(this);

        tvTitle.setText("菜品查看");

        btnBack.setOnClickListener(this);

        scId = getIntent().getStringExtra("scId");
        toSeeAbnormalActivity( scId);
        lvCheckPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] location = new int[2];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Rect frame = new Rect();
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                    int statusBarHeight = frame.top;

                    view.getLocationOnScreen(location);
                    location[1] += 0;
                } else {
                    view.getLocationOnScreen(location);
                }
                view.invalidate();
                int width = view.getWidth();
                int height = view.getHeight();

                Intent intent = new Intent(DZFoodPhotoCheckActivity.this, BigImageActivity.class);
                Bundle b = new Bundle();
                b.putString(BigImageActivity.QR_IMAGE_URL, photos.get(position).getEXCE_FILENAME1());
                intent.putExtras(b);
//                                intent.putExtra(BigImageActivity.PHOTO_SELECT_POSITION,);
                intent.putExtra(BigImageActivity.PHOTO_SELECT_X_TAG, location[0]);
                intent.putExtra(BigImageActivity.PHOTO_SELECT_Y_TAG, location[1]);
                intent.putExtra(BigImageActivity.PHOTO_SELECT_W_TAG, width);
                intent.putExtra(BigImageActivity.PHOTO_SELECT_H_TAG, height);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });
        lvCheckPhoto.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                worningAlert( "確認刪除圖片嗎？", i);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_title_left:
            finish();
            break;

        }
    }
    private String result;
    private void toSeeAbnormalActivity(String scId) {
        showDialog();
        final String url = Constants.HTTP_RZ_FOOD_VIEW + "?sc_id=" + scId;
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

                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        photos = new ArrayList<ExcePhoto>();
                        for (JsonElement type : array) {
                            ExcePhoto humi = gson.fromJson(type, ExcePhoto.class);
                            photos.add(humi);
                        }
                        Message message = new Message();
                        message.what = MESSAGE_SET_PHOTO;
                        message.obj = photos;
                        mHandler.sendMessage(message);
                    } else  {
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
    private void delete(String scId,  String item) {
        showDialog();
        final String url = Constants.HTTP_RZ_FOOD_DELETE + "?sc_id=" + scId + "&item=" + item;
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
                    message.what = MESSAGE_DEL_TRUE;
                    message.obj = "刪除成功";
                    mHandler.sendMessage(message);

                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
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
                    ToastUtils.showLong(DZFoodPhotoCheckActivity.this, msg.obj.toString());

                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(DZFoodPhotoCheckActivity.this, R.string.net_mistake);
                    break;
                case MESSAGE_SET_PHOTO://text賦值
                    adapter = new DZFoodPhotoAdapter(DZFoodPhotoCheckActivity.this,photos);
                    lvCheckPhoto.setAdapter(adapter);
                    break;
                case MESSAGE_DEL_TRUE:
                    toSeeAbnormalActivity( scId);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private void worningAlert(String msg, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub

                            delete(photos.get(position).getEXCE_ID(),  photos.get(position).getEXCE_ITEM());

                    }
                })
        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}