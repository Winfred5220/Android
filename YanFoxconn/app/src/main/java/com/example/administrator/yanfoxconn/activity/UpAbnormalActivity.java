package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
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
import android.widget.EditText;
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
import com.example.administrator.yanfoxconn.utils.DateTimePickDialogUtil;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.ImageZipUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import org.json.JSONArray;
import java.io.File;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 舊版 異常維護 界面
 *
 * Created by song on 2017/9/7.
 */

public class UpAbnormalActivity extends BaseActivity implements View.OnClickListener{


    private final int MESSAGE_TOAST = 2;

    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.et_description)
    EditText etDescription;//描述
    @BindView(R.id.et_address)
    EditText etAddress;//位置
    @BindView(R.id.tv_time_date)
    TextView tvTimeDate;//時間日期
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    GridView gridView;//图片显示区域

    private GridAdapter gridAdapter;
    private String initStartDateTime; // 初始化开始时间

    private String mFlag;//異常形式
    private String mScId;//掃描主鍵
    private String mAddress;//異常地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_abnormal);
        Bundle bundle = this.getIntent().getExtras();
        ButterKnife.bind(this);

        tvTitle.setText(R.string.activity_abnormal);
        btnUp.setText(R.string.up_abnormal);

        btnBack.setVisibility(View.VISIBLE);
        btnUp.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);
        tvTimeDate.setOnClickListener(this);

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(UpAbnormalActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(UpAbnormalActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        initStartDateTime = formatterUse.format(curDate);
        tvTimeDate.setText(formatter.format(curDate));
        mFlag = bundle.getString("flag");
        mScId = bundle.getString("scid");
        mAddress = bundle.getString("address");

        if (mAddress.equals("") && mFlag.equals("ALL")) {
            etAddress.setText("");
            etAddress.setEnabled(true);
        } else {
            etAddress.setText(mAddress);
            etAddress.setEnabled(false);
        }

        Log.e("----------", mFlag);
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
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(UpAbnormalActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right://提交
                Log.e("-------", "btn_title_right");

                if (etAddress.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "請填寫地址位置");
                } else if (etDescription.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "請填寫文字描述");
                } else if (tvTimeDate.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "請選擇時間");
                } else if (imagePaths==null) {
                    ToastUtils.showShort(this, "請選擇圖片");
                } else {
                    normal2();
                }
                break;
            case R.id.tv_time_date:

                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        UpAbnormalActivity.this, initStartDateTime);

                dateTimePicKDialog.dateTimePicKDialog(tvTimeDate,"","");
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
                Glide.with(UpAbnormalActivity.this)
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
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        final String url = Constants.HTTP_UPLOAD_FILE_SERVLET; //此處寫上自己的URL
        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡

        paramMap.put("exce_flag", mFlag);
        paramMap.put("exce_type", FoxContext.getInstance().getType());
        paramMap.put("sc_id", mScId);
        paramMap.put("exce_add", etAddress.getText().toString());
        paramMap.put("exce_time", tvTimeDate.getText().toString());
        paramMap.put("exce_desp", etDescription.getText().toString());
        paramMap.put("exce_name", FoxContext.getInstance().getName());
        paramMap.put("login_id",FoxContext.getInstance().getLoginId());

        final Map<String, File> fileMap = new HashMap<String, File>(); //檔全部添加到Map裡

        for (int i = 0; i < imagePaths.size(); i++) {
            File pictureFile = new File(imagePaths.get(i)); //通過路徑獲取檔
            final String pic_path = imagePaths.get(i);
            Log.e("----pic_path-------", pic_path);
            String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
            String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
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
                    showDialog();
                    HttpURLConnection b = HttpConnectionUtil.doPostPicture(url, paramMap, fileMap);
                    Log.e("---------", "==fff===" + url);
                    if (b!=null){
                        dismissDialog();
                        Log.e("---------", "==fff===" + b);
                        if (b.getResponseCode()==200){
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = b.getResponseMessage();
                            mHandler.sendMessage(message);
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
                    FileUtil.deletePhotos(UpAbnormalActivity.this);
                }
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showShort(UpAbnormalActivity.this,msg.obj.toString());
                    FileUtil.deletePhotos(UpAbnormalActivity.this);
                    finish();
                break;
            }
            super.handleMessage(msg);
        }
    };

}
