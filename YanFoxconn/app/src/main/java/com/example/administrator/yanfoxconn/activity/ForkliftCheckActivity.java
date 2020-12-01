package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.ForkliftCheckAdapter;
import com.example.administrator.yanfoxconn.bean.ForkliftMessage;
import com.example.administrator.yanfoxconn.bean.SelectModel;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.constant.ImageCaptureManager;
import com.example.administrator.yanfoxconn.intent.PhotoPickerIntent;
import com.example.administrator.yanfoxconn.intent.PhotoPreviewIntent;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ImageZipUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyGridView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangqian on 2019/3/5.叉車點檢界面
 */

public class ForkliftCheckActivity extends BaseActivity implements View.OnClickListener {

    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;

    private Context mcontext;
    private String result;//車牌號和TYPE
    private String[] rel = new String[2];
    private List<ForkliftMessage> mForkliftChecks;//點檢信息
    private ForkliftCheckAdapter mForkliftCheckAdapter;//點檢列表
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private GridAdapter gridAdapter;
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();//用户存储条目的选择状态
    private List<Integer> selectList;//用于存放無異常的条目
    private List<Integer> noselectList;//用于存放有異常的条目
    private int key1, key2 ,i= 0;//提交時管控未填寫信息

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.lv_check)
    ListView lvCheck;//點檢項目列表
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    MyGridView gridView;//图片显示区域

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forklift_check);
        ButterKnife.bind(this);
        btnBack.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        result = getIntent().getStringExtra("result");
        rel = result.split(",");
        tvTitle.setText("巡檢項目");
        mcontext = this.getApplicationContext();
        getMessage();
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(ForkliftCheckActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(3); // 最多选择照片数量，默认为3
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(ForkliftCheckActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT));
                    break;
                // 调用相机拍照
                case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                    if (captureManager.getCurrentPhotoPath() != null) {
                        captureManager.galleryAddPic();
                        ArrayList<String> paths = new ArrayList<>();
                        paths.add(captureManager.getCurrentPhotoPath());
                        loadAdpater(paths);
                        Log.e("==========", paths.get(1));
                    }
                    break;
            }
        }
    }

    private void loadAdpater(ArrayList<String> paths) {

        if (imagePaths == null) {
            imagePaths = new ArrayList<>();
        }
        imagePaths.clear();
        imagePaths.addAll(paths);

        if (paths.size() > 0) {
            ivEmpty.setVisibility(View.GONE);
        } else {
            ivEmpty.setVisibility(View.VISIBLE);
        }

        try {
            JSONArray obj = new JSONArray(imagePaths);
            Log.e("--", obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gridAdapter == null) {
            gridAdapter = new GridAdapter(imagePaths);
            gridView.setAdapter(gridAdapter);
            Log.e("----------------", "ddd==" + imagePaths.size());
        } else {
            gridAdapter.notifyDataSetChanged();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:
                if(i==0){
                    check();
                }else{
                    ToastUtils.showShort(ForkliftCheckActivity.this,"已提交過，請勿重複提交");
                }
                i++;
                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(ForkliftCheckActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(3); // 最多选择照片数量，默认为3
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
        }
    }

    private void getMessage() {
        showDialog();
        final String url = Constants.HTTP_FORKLIFT_CHECK_SERVLET + "?bianhao=" + rel[0] + "&type=" + rel[1];
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);

                dismissDialog();
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        mForkliftChecks = new ArrayList<ForkliftMessage>();

                        for (JsonElement type : array) {
                            ForkliftMessage humi = gson.fromJson(type, ForkliftMessage.class);
                            mForkliftChecks.add(humi);
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

    private void setText() {

        if (mForkliftChecks != null) {
            mForkliftCheckAdapter = new ForkliftCheckAdapter(mcontext, isSelected, mForkliftChecks);
            lvCheck.setAdapter(mForkliftCheckAdapter);
        } else {
            ToastUtils.showShort(ForkliftCheckActivity.this, "沒有數據!");
        }

    }

    private class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;

        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
        }

        @Override
        public int getCount() {
            if (listUrls.size() == 3) {
                return 3;
            } else {
                return listUrls.size() + 1;
            }

        }

        @Override
        public String getItem(int position) {
            return listUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_image, null);
                imageView = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(imageView);
                // 重置ImageView宽高
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(columnWidth, columnWidth);
//                imageView.setLayoutParams(params);
            } else {
                imageView = (ImageView) convertView.getTag();
            }
            if (position == listUrls.size()) {
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_addpic_unfocused));
                if (position == 3) {
                    imageView.setVisibility(View.GONE);
                }
            } else {
                Glide.with(ForkliftCheckActivity.this)
                        .load(new File(getItem(position)))
                        .placeholder(R.mipmap.default_error)
                        .error(R.mipmap.default_error)
                        .centerCrop()
                        .crossFade()
                        .into(imageView);
            }
            return convertView;
        }
    }

    //提交前檢查
    private void check() {

        final String url = Constants.HTTP_FORKLIFT_UPDATE_SERVLET; //此處寫上自己的URL
        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡
        final Map<String, File> fileMap = new HashMap<String, File>(); //檔全部添加到Map裡

        JsonArray array = new JsonArray();

        selectList = new ArrayList<>();
        noselectList = new ArrayList<>();
        //清除之前选择的数据
        selectList.clear();//無異常
        noselectList.clear();//有異常
        //遍历map集合
        for (int key : isSelected.keySet()) {
            Log.e("-----------","isSelected.get(key)==="+isSelected.get(key));
            //判断是否已选择，如果已选择，则添加进selectList
            if (isSelected.get(key)) {
                selectList.add(key);
            } else {
                noselectList.add(key);
            }
        }

        Log.e("----------", "selectList" + selectList.toString());

        for (int i = 0; i < selectList.size(); i++) {
            int j = selectList.get(i);
            JsonObject jsonObject = new JsonObject();
            if (mForkliftChecks.get(j).getFlag().equals("A")) {
                if (mForkliftChecks.get(j).getMessage() == null || mForkliftChecks.get(j).getMessage().equals("")) {
                    key1++;
                }
                jsonObject.addProperty("id", mForkliftChecks.get(j).getId());
                jsonObject.addProperty("abnormal", mForkliftChecks.get(j).getMessage());
                jsonObject.addProperty("isnormal", mForkliftChecks.get(j).getIsnormal());

            } else {
                jsonObject.addProperty("id", mForkliftChecks.get(j).getId());
                jsonObject.addProperty("abnormal", mForkliftChecks.get(j).getAbnormal());
                mForkliftChecks.get(j).setIsnormal("Y");
                jsonObject.addProperty("isnormal", mForkliftChecks.get(j).getIsnormal());
            }
            array.add(jsonObject);
        }

        Log.e("----------", "noselectList" + noselectList.toString());

        for (int i = 0; i < noselectList.size(); i++) {
            int j = noselectList.get(i);
            if (mForkliftChecks.get(j).getAbnormal() == null || mForkliftChecks.get(j).getAbnormal().equals("")) {
                key2++;
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", mForkliftChecks.get(j).getId());
            jsonObject.addProperty("abnormal", mForkliftChecks.get(j).getAbnormal());
            mForkliftChecks.get(j).setIsnormal("N");
            jsonObject.addProperty("isnormal", mForkliftChecks.get(j).getIsnormal());
            array.add(jsonObject);
        }

        Log.e("-----------", "array-----" + array.toString());

        paramMap.put("bianhao", rel[0]);
        paramMap.put("type", rel[1]);
        paramMap.put("creat_name", FoxContext.getInstance().getName().toString());
        paramMap.put("updateInfo", array.toString());

        Log.e("-----------", "paramMap-----" + paramMap.toString());

        if (key1 > 0) {
            ToastUtils.showShort(this, "請填寫信息");
            key1 = 0; key2 = 0;

        } else if (key2 > 0) {
            ToastUtils.showShort(this, "請填寫異常信息");
            key1 = 0; key2 = 0;

        } else if (imagePaths == null) {
            ToastUtils.showShort(this, "請拍照或選擇圖片");
        } else {

            for (int i = 0; i < imagePaths.size(); i++) {
                File pictureFile = new File(imagePaths.get(i)); //通過路徑獲取檔
//              fileMap.put("file" + i, pictureFile);//添加第一個文件
                final String pic_path = imagePaths.get(i);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
//                getFilesDir().getAbsolutePath()+"compressPic.jpg";
//                调用压缩图片的方法，返回压缩后的图片path
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 80);
                final File compressedPic = new File(compressImage);
                imgZipPaths.add(i, compressImage);
                Log.e("----com---", compressImage);
                if (compressedPic.exists()) {
                    Log.e("-------------", "图片压缩上传");
//                uploadFileByOkHTTP(context, actionUrl, compressedPic);
//                showDialog("111"+compressImage);
                    fileMap.put("file" + i, compressedPic);//添加第一個文件
                } else {//直接上传
//                uploadFileByOkHTTP(context, actionUrl, pictureFile);
//                showDialog("222"+entryFile.getValue());
                    fileMap.put("file" + i, pictureFile);
                }
            }

            //開啟一個新執行緒，向伺服器上傳資料
            new Thread() {
                public void run() {
                    //把网络访问的代码放在这里
                    try {
                        showDialog();
                        Log.e("---------", "==fff===" + url);
                        HttpURLConnection b = HttpConnectionUtil.doPostPicture(url, paramMap, fileMap);
                        if (b != null) {
                            dismissDialog();
                            Log.e("---------", "==fff===" + b);
                            if (b.getResponseCode() == 200) {
                                Message message = new Message();
                                message.what = MESSAGE_TOAST;
                                message.obj = b.getResponseMessage();
                                mHandler.sendMessage(message);
                                String filepath = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos" + File.separator;
                                FileUtil.deleteDir(filepath,ForkliftCheckActivity.this);
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);

                            } else {
                                Message message = new Message();
                                message.what = MESSAGE_TOAST;
                                message.obj = b.getResponseMessage();
                                mHandler.sendMessage(message);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        FileUtil.deletePhotos(ForkliftCheckActivity.this);
                    }
                }
            }.start();
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    aboutAlert(msg.obj.toString(), MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(ForkliftCheckActivity.this, R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(), MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                    break;
//                case MESSAGE_SHOW://顯示提醒
//                    setText();
//                    tvShow.setVisibility(View.VISIBLE);
//                    tvShow.setText(msg.obj.toString());
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
                        if (type == MESSAGE_TOAST) {
                            finish();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void worningAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (type == MESSAGE_TOAST) {
                            finish();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
