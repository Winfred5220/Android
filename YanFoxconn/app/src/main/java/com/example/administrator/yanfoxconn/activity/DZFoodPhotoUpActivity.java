package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.SelectModel;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.constant.ImageCaptureManager;
import com.example.administrator.yanfoxconn.intent.PhotoPickerIntent;
import com.example.administrator.yanfoxconn.intent.PhotoPreviewIntent;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.ImageZipUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Song
 * on 2020/8/14
 * Description：人資監餐 菜品照片上传界面
 */
public class DZFoodPhotoUpActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;//接口请求失敗彈出框
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnCheck;//查看菜品照片
    @BindView(R.id.btn_up)
    Button btnUp;//提交
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    GridView gvPhoto;//图片显示区域

    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;

    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private String scId;

    private GridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dz_food_photo_up);
        ButterKnife.bind(this);

        tvTitle.setText("菜品上傳");
        btnBack.setText("返回");
        btnCheck.setText("查看");
        btnUp.setText("提交");
        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
        btnCheck.setVisibility(View.VISIBLE);

        scId = getIntent().getStringExtra("scId");
        ivEmpty.setOnClickListener(this);
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gvPhoto.setNumColumns(cols);

        // preview
        gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(DZFoodPhotoUpActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(9); // 最多选择照片数量，默认为1
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(DZFoodPhotoUpActivity.this);
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
                        Log.e("==========", paths.get(9));
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
                PhotoPickerIntent intent = new PhotoPickerIntent(DZFoodPhotoUpActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right://查看已上传菜品照片
                Intent intent1 = new Intent(DZFoodPhotoUpActivity.this, DZFoodPhotoCheckActivity.class);
                intent1.putExtra("scId",scId);
                startActivity(intent1);
                break;
            case R.id.btn_up:

                upAlert("确认提交菜品照片？", MESSAGE_TOAST);

                break;
        }
    }

    //提交前檢查
    private void check() {
        final String url = Constants.HTTP_RZ_FOOD_UPLOAD; //此處寫上自己的URL

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
                    FileUtil.deletePhotos(DZFoodPhotoUpActivity.this);
                }
            }
        }.start();
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(DZFoodPhotoUpActivity.this, R.string.net_mistake);
                    break;
                case MESSAGE_TOAST:
                    ToastUtils.showShort(DZFoodPhotoUpActivity.this, msg.obj.toString());
                    finish();
                    break;

            }
            super.handleMessage(msg);
        }
    };
    private class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;

        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
        }

        @Override
        public int getCount() {
            if (listUrls.size() == 9) {
                return 9;
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
                if (position == 9) {
                    imageView.setVisibility(View.GONE);
                }
            } else {
                Glide.with(DZFoodPhotoUpActivity.this)
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

    private void upAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (imagePaths==null||imagePaths.size()==0){
                            ToastUtils.showLong(DZFoodPhotoUpActivity.this,"请上传菜品照片！");
                        }else if (FoxContext.getInstance().getType().equals("")){
                            ToastUtils.showLong(DZFoodPhotoUpActivity.this,"请确认登录状态！重新登录！");
                        }else{
                        check();}
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}