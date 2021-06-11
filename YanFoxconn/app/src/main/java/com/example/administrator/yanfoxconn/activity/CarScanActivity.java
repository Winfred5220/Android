package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.CarScan;
import com.example.administrator.yanfoxconn.bean.OCRMessage;
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
import com.example.administrator.yanfoxconn.utils.RecognizeService;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 條形碼掃描 顯示界面
 * Created by song on 2017/11/28.
 */

public class CarScanActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_SET_TEXT = 1;//tv賦值
    private static final int REQUEST_CODE_ACCURATE_BASIC = 107;
    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private boolean hasGotToken = false;
    private boolean isCab;//標記是櫃號還是封號
    private AlertDialog.Builder alertDialog;
    private List<OCRMessage> messageList;

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_packing_no)
    TextView tvPackingNo;
    @BindView(R.id.tv_cn_board)
    TextView tvCnBoard;
    @BindView(R.id.tv_dri_name)
    TextView tvDriName;
    @BindView(R.id.tv_forward_name)
    TextView tvForwardName;
    @BindView(R.id.tv_booking_no)
    TextView tvBookingNo;
    @BindView(R.id.tv_pack_11)
    TextView tvPack11;
    @BindView(R.id.tv_pack_12)
    TextView tvPack12;
    @BindView(R.id.et_container)
    EditText etContainer;
    @BindView(R.id.tv_container)
    TextView tvContainer;
    @BindView(R.id.et_sealer)
    EditText etSealer;
    @BindView(R.id.tv_container_sealer)
    TextView tvContainerSealer;
    @BindView(R.id.et_boxgw)
    EditText etBoxgw;
    @BindView(R.id.tv_loadport)
    TextView tvLoadPort;
    @BindView(R.id.tv_destination)
    TextView tvDestination;
    @BindView(R.id.tv_department)
    TextView tvDepartment;
    @BindView(R.id.tv_factory)
    TextView tvFactory;
    @BindView(R.id.et_building)
    EditText etBuilding;
    @BindView(R.id.tv_term)
    TextView tvTerm;
    @BindView(R.id.tv_cab_scan)
    TextView tvCabScan;
    @BindView(R.id.tv_seal_scan)
    TextView tvSealScan;
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    GridView gridView;//图片显示区域
    //    private int columnWidth;
    private GridAdapter gridAdapter;

    private String qrResult;//掃描結果
    private String url;//地址
    private String result;//
    private List<CarScan> carScanList;//信息列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_scan);
        ButterKnife.bind(this);
        Bundle bundle = this.getIntent().getExtras();
        qrResult = bundle.getString("result");
        initAccessTokenWithAkSk();
        tvTitle.setText("派車單信息");
        btnBack.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setText("提交");
        btnUp.setOnClickListener(this);
        tvCabScan.setOnClickListener(this);
        tvSealScan.setOnClickListener(this);
        getMessage(qrResult);

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(CarScanActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(3); // 最多选择照片数量，默认为1
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(CarScanActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);

                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });
    }

    private void getMessage(String qrResult) {
        showDialog();
        url = Constants.HTTP_CAR_SCAN_SERVLET + "?id=" + qrResult;

        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();

                Gson gson = new Gson();
                Log.e("----fff-url----", url.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        carScanList = new ArrayList<CarScan>();
                        for (JsonElement type : array) {
                            CarScan humi = gson.fromJson(type, CarScan.class);
                            carScanList.add(humi);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        mHandler.sendMessage(message);
                    } else {
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showLong(CarScanActivity.this, msg.obj.toString());
                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
                    break;

            }
            super.handleMessage(msg);
        }
    };

    private void setText() {
        tvPackingNo.setText(carScanList.get(0).getPacking_no());
        tvCnBoard.setText(carScanList.get(0).getCn_board());
        tvDriName.setText(carScanList.get(0).getDri_name());
        tvForwardName.setText(carScanList.get(0).getForwarder_name());
        tvBookingNo.setText(carScanList.get(0).getBookingno());
        tvPack11.setText(carScanList.get(0).getPackingquantity11());
        tvPack12.setText(carScanList.get(0).getPackingquantity12());
        tvContainer.setText(carScanList.get(0).getContainer());
        tvContainerSealer.setText(carScanList.get(0).getSealer());
        tvLoadPort.setText(carScanList.get(0).getLoadport());
        tvDestination.setText(carScanList.get(0).getDestination());
        tvDepartment.setText(carScanList.get(0).getDepartment());
        tvFactory.setText(carScanList.get(0).getFactory());
        etBuilding.setText(carScanList.get(0).getBuilding());
        tvTerm.setText(carScanList.get(0).getTerm());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("-----------", "requestCode, resultCode, data===" + requestCode + "-" + resultCode);

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
                // 文字識別
                case REQUEST_CODE_ACCURATE_BASIC:
                    RecognizeService.recAccurateBasic(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                            new RecognizeService.ServiceListener() {
                                @Override
                                public void onResult(String result) {

                                    Gson gson = new Gson();
                                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                                    JsonArray array = jsonObject.get("words_result").getAsJsonArray();
                                    messageList = new ArrayList<OCRMessage>();
                                    if (array.size() == 0) {
                                        ToastUtils.showLong(CarScanActivity.this, "拍照無效,請手動輸入!");
                                    } else {
                                        for (JsonElement type : array) {
                                            OCRMessage humi = gson.fromJson(type, OCRMessage.class);
                                            messageList.add(humi);
                                        }

                                        infoPopText(messageList.get(messageList.size() - 1).getWords());
                                    }

                                }
                            });
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
            case R.id.tv_cab_scan:
                isCab=true;
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent1 = new Intent(CarScanActivity.this, CameraActivity.class);
                intent1.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent1.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent1, REQUEST_CODE_ACCURATE_BASIC);

                break;
            case R.id.tv_seal_scan:
                isCab=false;
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent2 = new Intent(CarScanActivity.this, CameraActivity.class);
                intent2.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent2.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent2, REQUEST_CODE_ACCURATE_BASIC);

                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(CarScanActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(3); // 最多选择照片数量，默认为3
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
            case R.id.btn_title_right://提交
                Log.e("-------", "btn_title_right");
//                if (tvTerm.getText().toString().equals("海運")){
//                    if (etContainer.getText().toString().equals("")) {
//                        ToastUtils.showShort(this, "請填櫃號");
//                    } else if ((!etContainer.getText().toString().equals(tvContainer.getText().toString())) && tvTerm.getText().toString().equals("海運")) {
//                        ToastUtils.showShort(this, "櫃號不相符,請確認!");
//                    } else if (etSealer.getText().toString().equals("")) {
//                        ToastUtils.showShort(this, "請填封條號");
//                    } else if ((!etSealer.getText().toString().equals(tvContainerSealer.getText().toString())) ) {
//                        ToastUtils.showShort(this, "封條號不相符,請確認!");
//                    }else if (etBoxgw.getText().toString().equals("")) {
//                        ToastUtils.showShort(this, "請填箱皮重");
//                    } else if (imagePaths == null) {
//                        ToastUtils.showShort(this, "請選擇圖片");
//                    } else {
//                        normal2();
//                    }
//
//                }else if (etBoxgw.getText().toString().equals("")) {
//                    ToastUtils.showShort(this, "請填箱皮重");
//                } else if (imagePaths == null) {
//                    ToastUtils.showShort(this, "請選擇圖片");
//                } else {
//                    normal2();
//                }

                /**
                 * 不管海运陆运,获取的tvContainer柜号不为空的话,必须判断etContainer不为空,且与tvContainer相等.
                 * 若获取tvContainer柜号为空,则不做任何判断
                 * 改于2020/10/19副理让改的
                 */
                if (tvTerm.getText().toString().equals("海運")) {

                    if (etSealer.getText().toString().equals("")) {
                        ToastUtils.showShort(this, "請填封條號");
                    } else if ((!etSealer.getText().toString().equals(tvContainerSealer.getText().toString()))) {
                        ToastUtils.showShort(this, "封條號不相符,請確認!");
                    } else if (etBoxgw.getText().toString().equals("")) {
                        ToastUtils.showShort(this, "請填箱皮重");
                    } else if (imagePaths == null) {
                        ToastUtils.showShort(this, "請選擇圖片");
                    } else if (tvContainer.getText().toString() != "") {
                        if (etContainer.getText().toString().equals("")) {
                            ToastUtils.showShort(this, "請填櫃號");
                        } else if ((!etContainer.getText().toString().equals(tvContainer.getText().toString())) ) {
                            ToastUtils.showShort(this, "櫃號不相符,請確認!");
                        }else{
                            normal2();
                        }
                    } else {
                        normal2();
                    }
                } else if (etBoxgw.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "請填箱皮重");
                } else if (imagePaths == null) {
                    ToastUtils.showShort(this, "請選擇圖片");
                } else if (tvContainer.getText().toString() != "") {
                    if (etContainer.getText().toString().equals("")) {
                        ToastUtils.showShort(this, "請填櫃號");
                    } else if ((!etContainer.getText().toString().equals(tvContainer.getText().toString()))) {
                        ToastUtils.showShort(this, "櫃號不相符,請確認!");
                    }else{
                        normal2();
                    }
                } else {
                    normal2();
                }
                break;

        }
    }

    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }
    /**
     * 以license文件方式初始化
     */
    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("licence方式获取token失败", error.getMessage());
            }
        }, getApplicationContext());
    }

    /**
     * 用明文ak，sk初始化
     */
    private void initAccessTokenWithAkSk() {
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, getApplicationContext(), "d6PEEyy6O8WHXGHnmA4vApQo", "3h2BO8M1Kv4IGhXuRg4ylshPt6mNBeWw");
    }

    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initAccessToken();
        } else {
            Toast.makeText(getApplicationContext(), "需要android.permission.READ_PHONE_STATE", Toast.LENGTH_LONG).show();
        }
    }


    private void infoPopText(final String result) {
//        alertText("", result);
        if(isCab){
            etContainer.setText(result);
        }else{
            etSealer.setText(result);
        }
        Log.e("-------------", "ocr===" + result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        OCR.getInstance(this).release();
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
                Glide.with(CarScanActivity.this)
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
        final String url = Constants.HTTP_CAR_UPDATE_SERVLET; //此處寫上自己的URL

        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡

        String packingNo = tvPackingNo.getText().toString();
        String [] b = packingNo.split(" ");

        paramMap.put("id", qrResult);
        paramMap.put("container", etContainer.getText().toString());
        paramMap.put("sealer", etSealer.getText().toString());
        paramMap.put("boxgw", etBoxgw.getText().toString());
        paramMap.put("createor", FoxContext.getInstance().getName());
        paramMap.put("packing_no",b[0]);
        paramMap.put("buildingno", etBuilding.getText().toString());
//        paramMap.put("exce_desp", etDescription.getText().toString());
//        paramMap.put("exce_name", FoxContext.getInstance().getName());
//        paramMap.put("login_id",FoxContext.getInstance().getLoginId());


//        String path = "********"; //此處寫上要上傳的檔在系統中的路徑
//        final File pictureFile = new File(path); //通過路徑獲取檔

        final Map<String, File> fileMap = new HashMap<String, File>(); //檔全部添加到Map裡

        for (int i = 0; i < imagePaths.size(); i++) {
            File pictureFile = new File(imagePaths.get(i)); //通過路徑獲取檔
//            fileMap.put("file" + i, pictureFile);//添加第一個文件
            final String pic_path = imagePaths.get(i);
            String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
            String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";

            //getFilesDir().getAbsolutePath()+"compressPic.jpg";
            //调用压缩图片的方法，返回压缩后的图片path
            final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 30);
            final File compressedPic = new File(compressImage);
            imgZipPaths.add(i, compressImage);
            Log.e("----com---", compressImage);
            if (compressedPic.exists()) {
                Log.e("-------------", "图片压缩上传");
                fileMap.put("file" + i, compressedPic);//添加第一個文件

            } else {//直接上传
                fileMap.put("file" + i, pictureFile);
            }
        }


        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                try {
                    showDialog();
                    HttpURLConnection b = HttpConnectionUtil.doPostPicture(url, paramMap, fileMap);
                    Log.e("---------", "==fff===" + url);
                    if (b != null) {
                        dismissDialog();
                        Log.e("---------", "==fff===" + b);
                        if (b.getResponseCode() == 200) {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = "成功";
                            mHandler.sendMessage(message);
                            FileUtil.deletePhotos(CarScanActivity.this);
                            finish();
                        } else {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = b.getResponseMessage();
                            mHandler.sendMessage(message);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    FileUtil.deletePhotos(CarScanActivity.this);
                }
            }
        }.start();
    }

}
