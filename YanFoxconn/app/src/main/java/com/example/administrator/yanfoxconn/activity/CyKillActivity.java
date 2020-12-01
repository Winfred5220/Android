package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.CyCarMessage;
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
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 物流消殺車輛 維護及提交界面
 */
public class CyKillActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_SET_TEXT = 1;//tv賦值

    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_sq)
    TextView tvSq;//單號
    @BindView(R.id.tv_driver)
    TextView tvDriver;//司機
    @BindView(R.id.tv_car_num)
    TextView tvCarNum;//車牌號
    @BindView(R.id.tv_card_id)
    TextView tvCardId;//身份證號
    @BindView(R.id.tv_area)
    TextView tvArea;//区域
    @BindView(R.id.et_temperature)
    EditText etTemperature;//體溫
    @BindView(R.id.et_description)
    EditText etDes;//異常描述
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    MyGridView gridView;//图片显示区域
    private GridAdapter gridAdapter;

    private String[] qrResult;//掃碼結果
    private String type;//來向：cy 必須輸入體溫，cz 體溫不必須
    private CyCarMessage cyCarMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cy_kill);
        ButterKnife.bind(this);

        tvTitle.setText("消殺點檢");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setText("提交");
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);

        type = getIntent().getStringExtra("type");

cyCarMessage = (CyCarMessage) getIntent().getSerializableExtra("carMessage");
        tvSq.setText(cyCarMessage.getAPPLY_NO());
        tvDriver.setText(cyCarMessage.getDRIVER_NAME());
        tvCarNum.setText(cyCarMessage.getCAR_NUM());
        tvCardId.setText(cyCarMessage.getDRIVER_CARD());


        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 3 ? 3 : cols;
        gridView.setNumColumns(cols);

        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(CyKillActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(9); // 最多选择照片数量，默认为1
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(CyKillActivity.this);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(CyKillActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(9); // 最多选择照片数量，默认为1
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
            case R.id.btn_title_right:
                if (etTemperature.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "請填寫溫度");
                }else if(!etTemperature.getText().toString().equals("")&&Float.parseFloat(etTemperature.getText().toString())>42||etTemperature.getText().toString().substring(etTemperature.getText().toString().length()-1).equals(".")){
                        ToastUtils.showShort(this, "請填寫正確格式溫度");
                }else if(FoxContext.getInstance().getName().equals("")){
                    ToastUtils.showShort(this, "賬號異常，請重新登錄");
                } else if (imagePaths==null) {
                    ToastUtils.showShort(this, "請選擇圖片");
                }else{

                normal2();
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
                Glide.with(CyKillActivity.this)
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

    public void normal2() {
        Log.e("--------------------"," btnUp.setClickable(false);" +
                "");
        btnUp.setClickable(false);
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        final String url = Constants.HTTP_XS_CY_SAFEDATASAVE_SERVLET; //此處寫上自己的URL
        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡
        showDialog();

        paramMap.put("xs_id", cyCarMessage.getFEN_APPLY_NO());
        paramMap.put("xs_no", cyCarMessage.getFEN_APPLY_NO());
        paramMap.put("xs_tw", etTemperature.getText().toString());
        paramMap.put("xs_remark", etDes.getText().toString());
        paramMap.put("xs_recorder", FoxContext.getInstance().getName());

        final Map<String, File> fileMap = new HashMap<String, File>(); //檔全部添加到Map裡

        for (int i = 0; i < imagePaths.size(); i++) {
            File pictureFile = new File(imagePaths.get(i)); //通過路徑獲取檔
//            fileMap.put("file" + i, pictureFile);//添加第一個文件
            final String pic_path = imagePaths.get(i);
            String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
            String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
            //getFilesDir().getAbsolutePath()+"compressPic.jpg";
            //调用压缩图片的方法，返回压缩后的图片path
            final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 80);
            final File compressedPic = new File(compressImage);
            imgZipPaths.add(i,compressImage);
            Log.e("----com---",compressImage);
            if (compressedPic.exists()) {
                Log.e("-------------","图片压缩上传");
                fileMap.put("file"+i,compressedPic);//添加第一個文件

            }else{//直接上传
                fileMap.put("file"+i,pictureFile);
            }
        }

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                try {

                    HttpURLConnection b = HttpConnectionUtil.doPostPicture(url, paramMap, fileMap);
                    Log.e("---------", "==fff===" + url);
                    if (b!=null){

                        Log.e("---------", "==fff===" + b);
                        if (b.getResponseCode()==200){
                            FileUtil.deletePhotos(CyKillActivity.this);
//                            delete(imagePaths);
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = b.getResponseMessage();
                            mHandler.sendMessage(message);
                            dismissDialog();
                            finish();
                        }else{
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = b.getResponseMessage();
                            mHandler.sendMessage(message);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    FileUtil.deletePhotos(CyKillActivity.this);
                }
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showShort(CyKillActivity.this,msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //刪除已上傳的照片，新方法
//    public void delete(List<String> imagePath){
//        for (int i = 0;i<imagePath.size();i++){
//            String where=MediaStore.Audio.Media.DATA+" like \""+imagePath.get(i)+"%"+"\"";
//            int id=  this.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,where,null);
//
////            deleteDir(imagePath.get(i));
//            Log.e("--------------------","id=="+id);
//        }
//    }


}
