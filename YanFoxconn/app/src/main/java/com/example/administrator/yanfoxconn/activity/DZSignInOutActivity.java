package com.example.administrator.yanfoxconn.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.DNReform;
import com.example.administrator.yanfoxconn.bean.DZFoodAbList;
import com.example.administrator.yanfoxconn.bean.ExcePhoto;
import com.example.administrator.yanfoxconn.bean.GAWork;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static androidx.core.app.ActivityCompat.startActivityForResult;

/**
 * Created by Song
 * on 2020/8/15
 * Description：人資監餐 簽到簽退,拍照界面
 *              總務宿舍查驗 異常整改，拍照界面
 *              總務臨時工 簽到簽退，拍照界面
 */
public class DZSignInOutActivity extends BaseActivity implements View.OnClickListener {

    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息
    private final int MESSAGE_WORK = 4;//GA总务临时工，工作职能
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_CHECK = 6;
    private final int MESSAGE_GET_DIM_LOCAL = 0;//獲取dimLocal

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題

    @BindView(R.id.ll_ex)
    LinearLayout llEx;//公用異常上傳界面,此處只需要拍照,所以隱藏文字內容
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    GridView gvPhoto;//图片显示区域

    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;

    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类

    private GridAdapter gridAdapter;
    private String canbie;//餐別
    private String[] qrResult;//二維碼掃描結果
    private String inOut;//簽到或簽退
    private String scId;//二維碼主鍵
    private int photoNum;//照片數量

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_abnormal);
        ButterKnife.bind(this);
        llEx.setVisibility(View.GONE);

        btnBack.setText("返回");
        btnUp.setText("提交");
        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        btnUp.setVisibility(View.VISIBLE);

        ivEmpty.setOnClickListener(this);

        if (getIntent().getStringExtra("flag").equals("DN")) {
            photoNum = 9;
            tvTitle.setText("整改拍照");
        } else {
            FoxContext.getInstance().setTakePic("W0");
            photoNum = 1;
            canbie = getIntent().getStringExtra("flag");
            qrResult = getIntent().getStringExtra("result").split(",");
            inOut = getIntent().getStringExtra("inOut");
            scId = getIntent().getStringExtra("scId");
            if (inOut.equals("in")) {
                tvTitle.setText("簽到拍照");
            } else {
                tvTitle.setText("簽退拍照");
            }
        }


        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gvPhoto.setNumColumns(cols);

        // preview
        gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(DZSignInOutActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(photoNum); // 最多选择照片数量，默认为1
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(DZSignInOutActivity.this);
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
            gvPhoto.setAdapter(gridAdapter);
            Log.e("----------------", "ddd==" + imagePaths.size());
        } else {
            gridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(DZSignInOutActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(photoNum); // 最多选择照片数量，默认为9
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right://提交
                if (getIntent().getStringExtra("flag").equals("DN")) {
                    upAlert("是否進行整改照片上傳?", 0);
                } else {
                    upAlert("是否进行（签到/签退）上传？", 0);
                }
                break;

        }
    }

    private class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;

        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
        }

        @Override
        public int getCount() {
            if (listUrls.size() == photoNum) {
                return photoNum;
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
                if (position == photoNum) {
                    imageView.setVisibility(View.GONE);
                }
            } else {
                Glide.with(DZSignInOutActivity.this)
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

    //人資監餐簽到，提交前檢查
    private void check() {
        final String url = Constants.HTTP_RZ_SCAN_SIGNIN; //此處寫上自己的URL

        JsonObject object = new JsonObject();

        object.addProperty("dim_id", qrResult[1]);
        object.addProperty("type", FoxContext.getInstance().getType());
        object.addProperty("dim_locale", qrResult[2]);
        object.addProperty("flag", "D");
        object.addProperty("canbie", canbie);
        object.addProperty("sc_creator", FoxContext.getInstance().getName());
        object.addProperty("sc_creator_id", FoxContext.getInstance().getLoginId());

        JsonArray photoArray = new JsonArray();

        if (imagePaths != null && imagePaths.size() != 0) {
            for (int k = 0; k < imagePaths.size(); k++) {
                final String pic_path = imagePaths.get(k);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path = sign_dir + File.separator + System.currentTimeMillis() + 0 + k + ".jpg";
                Log.e("------_path-------", _path);
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 50);
                Log.e("-------compressImage------", compressImage);
                String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
                Log.e("-------picBase64Code-------", "====" + picBase64Code);
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("file", picBase64Code);
                photoArray.add(jsonObject1);
            }
        } else {
            ToastUtils.showLong(this, "请拍照上传！");
        }
        object.add("photo", photoArray);

        Log.e("-----object------", object.toString());

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
                    Log.e("---------", "==fff===" + url);
                    String result = HttpConnectionUtil.doPostJsonObject(url, object);
                    if (result != null) {
                        Log.e("---------", "result==fff===" + result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String errCode = jsonObject.get("errCode").getAsString();
                        if (errCode.equals("200")) {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);

                        } else {
                            Log.e("-----------", "result==" + result);
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                        }
                        dismissDialog();
                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    FileUtil.deletePhotos(DZSignInOutActivity.this);
                }
            }
        }.start();
    }

    //人資監餐簽退，提交前檢查
    private void checkOut() {
        final String url = Constants.HTTP_RZ_SCAN_SIGNOUT; //此處寫上自己的URL

        JsonObject object = new JsonObject();

        object.addProperty("sc_id", scId);
        object.addProperty("type", FoxContext.getInstance().getType());

        JsonArray photoArray = new JsonArray();

        if (imagePaths != null && imagePaths.size() != 0) {
            for (int k = 0; k < imagePaths.size(); k++) {
                final String pic_path = imagePaths.get(k);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path = sign_dir + File.separator + System.currentTimeMillis() + 0 + k + ".jpg";
                Log.e("------_path-------", _path);
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 50);
                Log.e("-------compressImage------", compressImage);
                String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
                Log.e("-------picBase64Code-------", "====" + picBase64Code);
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("file", picBase64Code);
                photoArray.add(jsonObject1);
            }
        } else {
            Log.e("-----object------", "imagePaths == null");
        }
        object.add("photo", photoArray);

        Log.e("-----object------", object.toString());


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
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    FileUtil.deletePhotos(DZSignInOutActivity.this);
                }
            }
        }.start();
    }

    //宿舍查驗,異常整改照片上傳
    private void upDNPhoto() {
        final String url = Constants.HTTP_SUSHE_REFORMUPDATE_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        DNReform reform = new DNReform();
        reform = (DNReform) getIntent().getSerializableExtra("reform");
        object.addProperty("jc_id", reform.getJc_id());
        object.addProperty("yc_id", reform.getJc_result().get(0).getId());
        object.addProperty("zg_creator", FoxContext.getInstance().getName());

        JsonArray photoArray = new JsonArray();

        if (imagePaths != null && imagePaths.size() != 0) {
            for (int k = 0; k < imagePaths.size(); k++) {
                final String pic_path = imagePaths.get(k);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path = sign_dir + File.separator + System.currentTimeMillis() + 0 + k + ".jpg";
                Log.e("------_path-------", _path);
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 50);
                Log.e("-------compressImage------", compressImage);
                String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
                Log.e("-------picBase64Code-------", "====" + picBase64Code);
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("file", picBase64Code);
                photoArray.add(jsonObject1);
            }
        } else {
            Log.e("-----object------", "imagePaths == null");
        }
        object.add("photo", photoArray);

        Log.e("-----object------", object.toString());


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
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    FileUtil.deletePhotos(DZSignInOutActivity.this);
                }
            }
        }.start();
    }

    //總務臨時工簽到，提交前檢查
    private void GASignIn(){

        BDLocation location = FoxContext.getInstance().getLocation();
        if (location == null || location.getLongitude() == 0.0 || location.getLatitude() == 0.0) {
            ToastUtils.showLong(this, "位置信息獲取失敗請稍後或移動后重試!");
        }
        final String url = Constants.HTTP_ZW_SCAN_SIGNIN; //此處寫上自己的URL

            JsonObject object = new JsonObject();

            object.addProperty("dim_id", qrResult[1]);
            object.addProperty("type", FoxContext.getInstance().getType());
            object.addProperty("dim_locale", qrResult[2]);
            object.addProperty("sc_lng", FoxContext.getInstance().getLocation().getLongitude());
            object.addProperty("sc_lat", FoxContext.getInstance().getLocation().getLatitude());
            object.addProperty("sc_creator", FoxContext.getInstance().getName());
            object.addProperty("sc_creator_id", FoxContext.getInstance().getLoginId());

            JsonArray photoArray = new JsonArray();

            if (imagePaths != null && imagePaths.size() != 0) {
                for (int k = 0; k < imagePaths.size(); k++) {
                    final String pic_path = imagePaths.get(k);
                    String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                    String _path = sign_dir + File.separator + System.currentTimeMillis() + 0 + k + ".jpg";
                    Log.e("------_path-------", _path);
                    final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 50);
                    Log.e("-------compressImage------", compressImage);
                    String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
                    Log.e("-------picBase64Code-------", "====" + picBase64Code);
                    JsonObject jsonObject1 = new JsonObject();
                    jsonObject1.addProperty("file", picBase64Code);
                    photoArray.add(jsonObject1);
                }
            } else {
                ToastUtils.showLong(this, "请拍照上传！");
            }
            object.add("photo", photoArray);

            Log.e("-----object------", object.toString());

            //開啟一個新執行緒，向伺服器上傳資料
            new Thread() {
                public void run() {
                    //把网络访问的代码放在这里
                    try {
                        showDialog();
                        Log.e("---------", "==fff===" + url);
                        String result = HttpConnectionUtil.doPostJsonObject(url, object);
                        if (result != null) {
                            Log.e("---------", "result==fff===" + result);
                            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                            String errCode = jsonObject.get("errCode").getAsString();
                            if (errCode.equals("200")) {
                                Message message = new Message();
                                message.what = MESSAGE_TOAST;
                                message.obj = jsonObject.get("errMessage").getAsString();
                                mHandler.sendMessage(message);

                            } else {
                                Log.e("-----------", "result==" + result);
                                Message message = new Message();
                                message.what = MESSAGE_TOAST;
                                message.obj = jsonObject.get("errMessage").getAsString();
                                mHandler.sendMessage(message);
                            }
                            dismissDialog();
                        } else {
                            Message message = new Message();
                            message.what = MESSAGE_NETMISTAKE;
                            mHandler.sendMessage(message);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        FileUtil.deletePhotos(DZSignInOutActivity.this);
                    }
                }
            }.start();

    }
    //總務臨時工簽退，提交前檢查
    private void GASignOut(){

            final String url = Constants.HTTP_ZW_SCAN_SIGNOUT; //此處寫上自己的URL

            JsonObject object = new JsonObject();

            object.addProperty("sc_id", scId);
            object.addProperty("type", FoxContext.getInstance().getType());

            JsonArray photoArray = new JsonArray();

            if (imagePaths != null && imagePaths.size() != 0) {
                for (int k = 0; k < imagePaths.size(); k++) {
                    final String pic_path = imagePaths.get(k);
                    String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                    String _path = sign_dir + File.separator + System.currentTimeMillis() + 0 + k + ".jpg";
                    Log.e("------_path-------", _path);
                    final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 50);
                    Log.e("-------compressImage------", compressImage);
                    String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
                    Log.e("-------picBase64Code-------", "====" + picBase64Code);
                    JsonObject jsonObject1 = new JsonObject();
                    jsonObject1.addProperty("file", picBase64Code);
                    photoArray.add(jsonObject1);
                }
            } else {
                Log.e("-----object------", "imagePaths == null");
            }
            object.add("photo", photoArray);

            Log.e("-----object------", object.toString());


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
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        FileUtil.deletePhotos(DZSignInOutActivity.this);
                    }
                }
            }.start();

    }

    /**
     * 获取总务临时工，工作职能
     * @param loginId
     */
    List<GAWork> works;
    private void getWorkMessage(String loginId) {
        showDialog();
        final String url = Constants.HTTP_ZW_SCAN_DUTY + "?creator_id=" + loginId;
        Log.e("-------fff--", "url==" + url);
        new Thread() {
            @Override
            public void run() {
                String result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Gson gson = new Gson();
                Log.e("--fff---result----", result.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                         works = new ArrayList<>();
                        for (JsonElement type : array) {
                            GAWork humi = gson.fromJson(type, GAWork.class);
                            works.add(humi);
                        }
                        Message message = new Message();
                        message.what = MESSAGE_WORK;
                        message.obj = works;
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

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(DZSignInOutActivity.this, R.string.net_mistake);
                    finish();
                    break;
                case MESSAGE_TOAST:
                    if (msg.obj.toString().equals("success")) {
                        ToastUtils.showLong(DZSignInOutActivity.this, "成功");
                    } else {
                        ToastUtils.showLong(DZSignInOutActivity.this, msg.obj.toString());
                    }
                    if (getIntent().getStringExtra("flag").equals("GA")&&inOut.equals("in")){
                        getWorkMessage(FoxContext.getInstance().getLoginId());
                    }else{
                    finish();}
                    break;
                case MESSAGE_UP://提交響應
                    break;
                case MESSAGE_WORK:

                    String message = "课组："+works.get(0).getG_name()+"\n"+"岗位："+works.get(0).getG_post()+"\n"+"职责： "+"\n"+works.get(0).getG_duty();
                    worningAlert(message,1);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * GA总务临时工，职能信息
     * @param msg
     * @param position
     */
    private void worningAlert(String msg, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("职能信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void upAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (imagePaths == null || imagePaths.size() == 0) {
                            ToastUtils.showLong(DZSignInOutActivity.this, "请拍照上传！");
                        } else {
                            if (getIntent().getStringExtra("flag").equals("DN")) {

                                upDNPhoto();
                            } else if (getIntent().getStringExtra("flag").equals("GA")){//總務臨時工
                                if (inOut.equals("in")) {//签到
                                    GASignIn();
                                } else {//签退
                                    GASignOut();
                                }
                            }else{
                                if (inOut.equals("in")) {//签到
                                    check();
                                } else {//签退
                                    checkOut();
                                }
                            }
                        }

                    }
                })
        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        })
        ;
        AlertDialog alert = builder.create();
        alert.show();
    }

}
