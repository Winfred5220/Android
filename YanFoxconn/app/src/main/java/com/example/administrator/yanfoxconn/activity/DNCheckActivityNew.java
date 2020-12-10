package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.DNCheckMessage;
import com.example.administrator.yanfoxconn.bean.DNSpinner;
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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 宿舍查验 界面 更改提交方式
 * Created by song on 2017/9/7.
 */

public class DNCheckActivityNew extends BaseActivity implements View.OnClickListener {


    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_UPLOAD = 1;
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤

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
    @BindView(R.id.ll_cbList)
    LinearLayout llcbList;//多選位置
    //    @BindView(R.id.sp_why)
    //    Spinner spWhy;//宿舍選項
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    MyGridView gridView;//图片显示区域

    private GridAdapter gridAdapter;

    private String mFlag;//異常形式
    private String mScId;//掃描主鍵
    private List<String> mWhy;//選項
    private String why;

    private List<DNSpinner> dnSpinnerList;
    private String[] qrResult;//宿舍信息
    private DNCheckMessage dnCheckMessageList;//人員信息
    private String flag;//提交類型
    private String hChanPin;//環境異常提交的產品處
    private String isEmpty;//是否為空房間

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dn_checkl);
        Bundle bundle = this.getIntent().getExtras();
        ButterKnife.bind(this);

        tvTitle.setText("查房維護");
        btnUp.setText(R.string.up_abnormal);

        btnBack.setVisibility(View.VISIBLE);
        btnUp.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);


        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(DNCheckActivityNew.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(DNCheckActivityNew.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });


        qrResult = getIntent().getStringExtra("qrResult").split(",");
        flag = getIntent().getStringExtra("flag");
        if (flag.equals("R")) {
            dnCheckMessageList = (DNCheckMessage) getIntent().getSerializableExtra("dnCheckMessageList");
            tvTitle.setText("人員信息維護");
        } else {
            isEmpty= getIntent().getStringExtra("isEmpty");
            tvTitle.setText("環境信息維護");
            hChanPin=getIntent().getStringExtra("hChanPin");
        }
        getSpinner();//獲取下拉列表信息
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
                PhotoPickerIntent intent = new PhotoPickerIntent(DNCheckActivityNew.this);
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
                check();

                break;

        }
    }

    private String str = "";
    private List<Integer> tag;

    public void check() {

        tag = new ArrayList<>();

        for (CheckBox checkBox : checkBoxList) {
            if (checkBox.isChecked()) {
                str += checkBox.getText().toString() + ".";
                tag.add((Integer) (checkBox.getTag()));
            }
        }

        if (str.equals("")) {
            ToastUtils.showShort(this, "請選擇查驗類型");
        } else if (str.contains("其他") && etDescription.getText().toString().equals("")) {
            ToastUtils.showShort(this, "請填寫文字描述");
        } else if (imagePaths == null) {
            for (int i = 0; i < tag.size(); i++) {
                if (dnSpinnerList.get(tag.get(i)).getPhoto_flag().equals("Y")) {
                    ToastUtils.showLong(DNCheckActivityNew.this, "包含需提交照片項目,請上傳照片!");
                    break;
                }
                if (i == (tag.size() - 1) && dnSpinnerList.get(tag.get(i)).getPhoto_flag().equals("N")) {
                    normal2new();
                }

            }
        }else{
            normal2new();
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
                Glide.with(DNCheckActivityNew.this)
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

    public void normal2new() {
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        final String url = Constants.HTTP_SUSHE_JCOKSERVLET; //此處寫上自己的URL
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();

        object.addProperty("jc_area", qrResult[0]);
        object.addProperty("jc_building", qrResult[1]);
        object.addProperty("jc_floor", qrResult[2]);
        object.addProperty("jc_room", qrResult[3]);
        if (flag.equals("R")) {
            object.addProperty("jc_bed", dnCheckMessageList.getREGULAREMP_BEDNUM());
            object.addProperty("emp_no", dnCheckMessageList.getREGULAREMP_NO());
            object.addProperty("emp_name", dnCheckMessageList.getREGULAREMP_NAME());
            object.addProperty("jc_chanpin", dnCheckMessageList.getREGULAREMP_CHANPIN());
            object.addProperty("jc_department", dnCheckMessageList.getREGULAREMP_DEPARTMENT());
            object.addProperty("jc_banbie", dnCheckMessageList.getREGULAREMP_BANBIE());

        } else {
            object.addProperty("jc_bed", "");
            object.addProperty("emp_no", "");
            object.addProperty("emp_name", "");
            object.addProperty("jc_chanpin", hChanPin);
            object.addProperty("jc_department", "");
            object.addProperty("jc_banbie", "");


        }

        object.addProperty("jc_remarks", etDescription.getText().toString());
        object.addProperty("jc_flag", flag);
        object.addProperty("create_name", FoxContext.getInstance().getName());
        object.addProperty("create_no", FoxContext.getInstance().getLoginId());
        object.addProperty("jc_result", str);

        for (int i = 0; i < tag.size(); i++) {
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("id", dnSpinnerList.get(tag.get(i)).getId());
            resultObj.addProperty("name", dnSpinnerList.get(tag.get(i)).getName());
            resultObj.addProperty("yc_flag", dnSpinnerList.get(tag.get(i)).getYc_flag());
            resultObj.addProperty("ksh_flag", dnSpinnerList.get(tag.get(i)).getKsh_flag());
            resultObj.addProperty("ksh_yctype", dnSpinnerList.get(tag.get(i)).getKsh_yctype());
            array.add(resultObj);
        }
        object.add("jc_result", array);
        JsonArray photoArray = new JsonArray();
        if (imagePaths != null) {
            for (int k = 0; k < imagePaths.size(); k++) {
                final String pic_path = imagePaths.get(k);
                Log.e("------pic_path-------", pic_path);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path = sign_dir + File.separator + System.currentTimeMillis() + k + ".jpg";
                Log.e("------_path-------", _path);
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 50);
                Log.e("-------compressImage------", compressImage);
                String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
                Log.e("-------picBase64Code-------", "====" + picBase64Code);
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("file", picBase64Code);
                photoArray.add(jsonObject1);
            }
        }
        object.add("photo", photoArray);
        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
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
                    FileUtil.deletePhotos(DNCheckActivityNew.this);
                }
            }
        }.start();
    }

    public void normal2() {
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        final String url = Constants.HTTP_SUSHE_JCOKSERVLET; //此處寫上自己的URL
        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡
//        paramMap.put("jc_area", qrResult[0]);
//        paramMap.put("jc_building", qrResult[1]);
//        paramMap.put("jc_floor", qrResult[2]);
//        paramMap.put("jc_room", qrResult[3]);
//        if (flag.equals("R")) {
//            paramMap.put("jc_bed", dnCheckMessageList.getREGULAREMP_BEDNUM());
//            paramMap.put("emp_no", dnCheckMessageList.getREGULAREMP_NO());
//            paramMap.put("emp_name", dnCheckMessageList.getREGULAREMP_NAME());
//            paramMap.put("jc_chanpin", dnCheckMessageList.getREGULAREMP_CHANPIN());
//            paramMap.put("jc_department", dnCheckMessageList.getREGULAREMP_DEPARTMENT());
//            paramMap.put("jc_banbie", dnCheckMessageList.getREGULAREMP_BANBIE());
//
//        } else {
////            paramMap.put("jc_bed", "");
////            paramMap.put("emp_no","");
////            paramMap.put("emp_name","");
//        }
//        paramMap.put("jc_result", str);
//        paramMap.put("jc_remarks", etDescription.getText().toString());
//        paramMap.put("jc_flag", flag);
//        paramMap.put("create_name", FoxContext.getInstance().getName());
//        paramMap.put("create_no", FoxContext.getInstance().getLoginId());
////        String path = "********"; //此處寫上要上傳的檔在系統中的路徑
////        final File pictureFile = new File(path); //通過路徑獲取檔
        final Map<String, File> fileMap = new HashMap<String, File>(); //檔全部添加到Map裡
        if (imagePaths != null) {
            for (int i = 0; i < imagePaths.size(); i++) {
                File pictureFile = new File(imagePaths.get(i)); //通過路徑獲取檔
                final String pic_path = imagePaths.get(i);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path = sign_dir + File.separator + System.currentTimeMillis() + i + ".jpg";
                //调用压缩图片的方法，返回压缩后的图片path
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 80);
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
        } else {

            fileMap.put("file0", getFile());
        }


        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                try {
                    showDialog();
//                    HttpURLConnection b = HttpConnectionUtil.doPostPicture(url, paramMap, fileMap);
                    String b = HttpConnectionUtil.doPostPictureLog(url, paramMap, fileMap);

                    Log.e("---------", "==fff===" + url);
                    if (b != null) {
                        dismissDialog();
                        Log.e("---------", "==fff===" + b);
                        if (b.equals("ok")) {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = "成功";
                            mHandler.sendMessage(message);
                            FileUtil.deletePhotos(DNCheckActivityNew.this);
                            finish();
                        } else {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = b;
                            mHandler.sendMessage(message);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    FileUtil.deletePhotos(DNCheckActivityNew.this);
                }
            }
        }.start();
    }


    //s上传默认图片
    public File getFile() {

        Resources res = getResources();
        BitmapDrawable d = (BitmapDrawable) res.getDrawable(R.mipmap.ic_no_pic);
        Bitmap img = d.getBitmap();

        String fn = "image_moren.png";
        String path = getFilesDir() + File.separator + fn;
        try {
            OutputStream os = new FileOutputStream(path);
            img.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }

        File file = new File(path);
        return file;

    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showShort(DNCheckActivityNew.this, msg.obj.toString());
                    finish();
                    break;
                case MESSAGE_UPLOAD://Toast彈出
                    mWhy = new ArrayList<String>();
                    for (int i = 0; i < dnSpinnerList.size(); i++) {
                        mWhy.add(dnSpinnerList.get(i).getName());

                    }
                    if (mWhy.size() > 0) {
                        setCheckBox();
                    } else {
                        ToastUtils.showShort(DNCheckActivityNew.this, "下拉框數據有誤，請刷新重試！");
                    }
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(DNCheckActivityNew.this, R.string.net_mistake);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private String result;//接口返回結果
    String url = null;

    public void getSpinner() {
        showDialog();

        url = Constants.HTTP_SUSHE_TYPESERVLET + "?flag=" + flag;

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                result = HttpUtils.queryStringForPost(url);
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                String response = result;
                dismissDialog();
                if (result != null) {
                    Log.e("---------", "==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        dnSpinnerList = new ArrayList<DNSpinner>();
                        for (JsonElement type : array) {
                            DNSpinner humi = gson.fromJson(type, DNSpinner.class);
                            dnSpinnerList.add(humi);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_UPLOAD;
                        mHandler.sendMessage(message);

                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
//                        ToastUtils.showShort(DNCheckListActivity.this, jsonObject.get("errMessage").getAsString());
                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    mHandler.sendMessage(message);
                    message.obj = "請求不成功";
//                    ToastUtils.showShort(DNCheckListActivity.this, "請求不成功");
                }
            }
        }.start();
    }

    private List<CheckBox> checkBoxList = new ArrayList<CheckBox>();

    //多選
    public void setCheckBox() {
        for (int i = 0; i < mWhy.size(); i++) {
            CheckBox checkBox = (CheckBox) View.inflate(this, R.layout.check_box, null);
            checkBox.setText(mWhy.get(i));
            checkBox.setTag(i);
            if (flag.equals("H")&&isEmpty.equals("Y")&&i==17){
                checkBox.setChecked(true);
            }
            llcbList.addView(checkBox);
            checkBoxList.add(checkBox);
        }
    }


}
