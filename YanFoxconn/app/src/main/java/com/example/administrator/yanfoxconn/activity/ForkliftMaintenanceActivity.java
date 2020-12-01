package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForkliftMaintenanceActivity extends BaseActivity implements View.OnClickListener{

    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤

    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private GridAdapter gridAdapter;
    private String danhao;//單號
    private List<ForkliftMessage> mForkliftMessage;

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交


    @BindView(R.id.tv_bianhao)
    TextView tvBianhao;//車架號
    @BindView(R.id.tv_chepai)
    TextView tvChepai;//車牌號
    @BindView(R.id.tv_hours)
    TextView tvHours;//小時數
    @BindView(R.id.tv_address)
    TextView tvAddress;//位置
    @BindView(R.id.tv_user_or)
    TextView tvUserOr;//是否影響使用
    @BindView(R.id.tv_bx_person)
    TextView tvBxPerson;//報修人
    @BindView(R.id.tv_contact)
    TextView tvContact;//聯繫方式
    @BindView(R.id.tv_bx_date)
    TextView tvBxDate;//報修時間
    @BindView(R.id.tv_wish_date)
    TextView tvWishDate;//預期完成時間
    @BindView(R.id.tv_question)
    TextView tvQuestion;//故障問題
    @BindView(R.id.et_suggest)
    TextView tvSuggest;//維修建議
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    MyGridView gridView;//图片显示区域
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forklift_maintenance);
        ButterKnife.bind(this);

        tvTitle.setText("維 修");
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);
        tvContact.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        danhao = getIntent().getStringExtra("danhao");
        getMessage();
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(ForkliftMaintenanceActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(3); // 最多选择照片数量，默认为3
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(ForkliftMaintenanceActivity.this);
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
                Glide.with(ForkliftMaintenanceActivity.this)
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
        switch (view.getId()){
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:
                check();
                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(ForkliftMaintenanceActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(3); // 最多选择照片数量，默认为3
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
            case R.id.tv_contact:
                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                String TEL = "tel:"+ tvContact.getText().toString();
                intent1.setData(Uri.parse(TEL));
                startActivity(intent1);
                break;
        }
    }

    private void getMessage(){
        showDialog();
        final String url = Constants.HTTP_FORKLIFT_MAINTENANCE_SERVLET +"?danhao=" + danhao;
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                Log.e("---------", "==fff===" + url);
                String result = HttpUtils.queryStringForPost(url);
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        mForkliftMessage = new ArrayList<ForkliftMessage>();
                        for (JsonElement type : array) {
                            ForkliftMessage humi = gson.fromJson(type, ForkliftMessage.class);
                            mForkliftMessage.add(humi);

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

    private void setText(){
        tvChepai.setText(mForkliftMessage.get(0).getChepai());
        tvBianhao.setText(mForkliftMessage.get(0).getBianhao());
        tvHours.setText(mForkliftMessage.get(0).getHours());
        tvAddress.setText(mForkliftMessage.get(0).getAddress());
        tvUserOr.setText(mForkliftMessage.get(0).getUser_or());
        tvBxPerson.setText(mForkliftMessage.get(0).getBx_name());
        tvContact.setText(mForkliftMessage.get(0).getBx_tel());
        tvBxDate.setText(mForkliftMessage.get(0).getBx_date().substring(0,16));
        tvWishDate.setText(mForkliftMessage.get(0).getWish_date().substring(0,16));
        tvQuestion.setText(mForkliftMessage.get(0).getQuestion());
    }

    //提交前檢查
    private void check() {

        final String url = Constants.HTTP_FORKLIFT_MAINTENANCE_UPDATE_SERVLET; //此處寫上自己的URL
        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡
        final Map<String, File> fileMap = new HashMap<String, File>(); //檔全部添加到Map裡

        paramMap.put("danhao", danhao);
        paramMap.put("repair_advice",tvSuggest.getText().toString());
        paramMap.put("repair_name", FoxContext.getInstance().getName().toString());


        Log.e("-----------", "paramMap-----" + paramMap.toString());

        if(tvSuggest.getText().toString().equals("")){
            ToastUtils.showShort(this, "請填寫維修建議");
        }else if (imagePaths == null) {
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
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                    FileUtil.deletePhotos(ForkliftMaintenanceActivity.this);
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
                    ToastUtils.showLong(ForkliftMaintenanceActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
                    dismissDialog();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
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
                        if (type==MESSAGE_TOAST){
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
                        if (type==MESSAGE_TOAST){
                            finish();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
