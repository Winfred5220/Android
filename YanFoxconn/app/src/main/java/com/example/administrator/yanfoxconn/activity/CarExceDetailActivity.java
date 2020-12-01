package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.ExcePhoto;
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
import com.example.administrator.yanfoxconn.widget.TouchImageView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CarExceDetailActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息
    private final int MESSAGE_JUMP = 4;//跳轉維護界面
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_CHECK = 6;
    private List<ExcePhoto> photos;
    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private GridAdapter gridAdapter;
    String exce_id,dim_id,type;
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.ll_get_image)
    LinearLayout llGetImage;//異常圖片
    @BindView(R.id.et_describe)
    EditText etDescribe;//描述
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    MyGridView gridView;//图片显示区域

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhiyinshui_exce_detail);
        ButterKnife.bind(this);

        tvTitle.setText("異常詳情");
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);
        exce_id = getIntent().getStringExtra("exce_id");
        dim_id = getIntent().getStringExtra("dim_id");
        type = getIntent().getStringExtra("type");
        getMessage();

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(CarExceDetailActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(3); // 最多选择照片数量，默认为3
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(CarExceDetailActivity.this);
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
                Glide.with(CarExceDetailActivity.this)
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
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:
                aboutAlert(getResources().getString(R.string.update_confirm),MESSAGE_UP);
                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(CarExceDetailActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(3); // 最多选择照片数量，默认为3
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
        }
    }
    private void getMessage(){

        showDialog();

        final String url = Constants.HTTP_WATER_PHOTO_INFO_SERVLET +"?exce_id=" + exce_id;
        Log.e("-----------", "url==" + url);
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
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
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else{
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                    finish();
                }
            } }.start();
    }

    //提交前檢查
    private void check() {

        final String url = Constants.HTTP_CAR_REPAIRED_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();

        object.addProperty("dim_id",dim_id);
        object.addProperty("type", type);
        object.addProperty("exce_id", exce_id);
        object.addProperty("flag", "S");
        object.addProperty("info",etDescribe.getText().toString() );
        object.addProperty("sc_creator", FoxContext.getInstance().getName());
        object.addProperty("sc_creator_id", FoxContext.getInstance().getLoginId());

        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }else if (etDescribe.getText().toString().equals("")) {
            ToastUtils.showShort(this, "請填寫描述信息");
        }else if (imagePaths == null) {
            ToastUtils.showShort(this, "請拍照或選擇圖片");
        }else{
            JsonArray photoArray = new JsonArray();

            for (int i = 0; i < imagePaths.size(); i++) {

                final String pic_path = imagePaths.get(i);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
//                调用压缩图片的方法，返回压缩后的图片path
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 50);
                String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
                Log.e("-------picBase64Code-------", "===="+ picBase64Code);
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("file", picBase64Code);
                photoArray.add(jsonObject1);
            }
            object.add("photo", photoArray);
            Log.e("-----object------",  object.toString());


            //開啟一個新執行緒，向伺服器上傳資料
            new Thread() {
                public void run() {
                    //把网络访问的代码放在这里
                    try {
                        showDialog();
                        Log.e("---------", "==fff===" + url);
                        String result = HttpConnectionUtil.doPostJsonObject(url, object);
                        if (result != null) {
                            dismissDialog();
                            Log.e("---------", "result==fff===" + result);
                            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                            String errCode = jsonObject.get("errCode").getAsString();
                            if (errCode.equals("200")) {
                                Message message = new Message();
                                message.what = MESSAGE_TOAST;
                                message.obj = jsonObject.get("errMessage").getAsString();
                                mHandler.sendMessage(message);

                            } else{
                                Log.e("-----------", "result==" + result);
                                Message message = new Message();
                                message.what = MESSAGE_TOAST;
                                message.obj = jsonObject.get("errMessage").getAsString();
                                mHandler.sendMessage(message);
                            }
                        }else{
                            Message message = new Message();
                            message.what = MESSAGE_NETMISTAKE;
                            mHandler.sendMessage(message);
                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        FileUtil.deletePhotos(CarExceDetailActivity.this);
                    }
                }
            }.start();
        }


    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(CarExceDetailActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    for (int i=0;i<photos.size();i++){
                        TouchImageView imageView = new TouchImageView(CarExceDetailActivity.this);
//                        ImageView imageView = new ImageView(ZhiyinshuiExceDetailActivity.this);
                        String imgStr = photos.get(i).getFile();
                        Log.e("------imgStr-----", "======"+imgStr);
                        byte[] decode = Base64.decode(imgStr, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                        imageView.setImageBitmap(bitmap);
                        //設置點擊放大事件
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
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

                                Intent intent = new Intent(CarExceDetailActivity.this, BigImageActivity.class);
                                Bundle b = new Bundle();
                                b.putString(BigImageActivity.QR_IMAGE_URL, imgStr);
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
                        llGetImage.addView(imageView);
                    }
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
                    break;
                case MESSAGE_JUMP://跳轉維護異常界面
                    worningAlert(msg.obj.toString(),MESSAGE_JUMP);
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
                        if (type==MESSAGE_TOAST){
                            finish();
                        }else if(type==MESSAGE_UP){
                            check();
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
                        if (type==MESSAGE_TOAST){
                            finish();
                        }else if (type==MESSAGE_JUMP){

//                            Intent intent = new Intent(ZhiyinshuiCheckActivity.this,ZhiyinshuiMaintenanceActivity.class);
//                            intent.putExtra("dim_id",mMsgList.get(0).getDim_id());
//                            startActivity(intent);
//                            ToastUtils.showShort(ZhiyinshuiCheckActivity.this, "跳轉異常界面!");
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}