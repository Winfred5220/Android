package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.EmpListAdapter;
import com.example.administrator.yanfoxconn.bean.EmpFile;
import com.example.administrator.yanfoxconn.bean.EmpMessage;
import com.example.administrator.yanfoxconn.bean.SelectModel;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.constant.ImageCaptureManager;
import com.example.administrator.yanfoxconn.intent.PhotoPickerIntent;
import com.example.administrator.yanfoxconn.intent.PhotoPreviewIntent;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.DateTimePickDialogUtil;
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
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 私家車違規登記表
 * Created by wang on 2020/12/11.
 */

public class CommonFormsPrivateCarActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒

    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类

    private String[] wrongPosition = {"請選擇","A區東大門停車場","E區宿舍崗","PWB北大門","E12停車場",
            "E07南大門","C區西大門停車場","C區北大門停車場","A區西大門","E05停車場","E03停車場","D區北小門","其他"};

    private String[] wrong = {"請選擇","駕駛摩托車未戴頭盔","駕駛電動車未戴頭盔","乘坐摩托車未戴頭盔","乘坐電動車未戴頭盔","其他"};
    private String[] teamData = {"請選擇","一大隊一中隊","一大隊二中隊","二大隊一中隊","二大隊二中隊","三大隊","機動巡邏隊"};

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTittle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_gate_date)
    TextView tvGateDate;//稽核時間
    @BindView(R.id.sp_position)
    Spinner spPosition;//違規地點
    @BindView(R.id.et_other)//違規原因
    EditText etOther;
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    GridView gridView;//图片显示区域
    private GridAdapter gridAdapter;

    private String wrongPositionSp = "";//違規地點
    private String wrongSp = "";//違規描述
    private String team = "";//稽核課隊

    private String initStartDateTime; // 初始化开始时间
    private SimpleDateFormat formatter;
    private Date selectTime = null;//所選時間
    private Date curDate = null;//當前時間

    private String result;//錄入的工號
    final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡
    private EmpListAdapter mAdapter;
    private List<EmpMessage> empMessagesList;
    private List<EmpFile> empFileList;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_car);

        ButterKnife.bind(this);

        result = getIntent().getStringExtra("result");

        //getMessage();//根據工號獲得信息
        tvTittle.setText("私家車違規登記表");
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);
        tvGateDate.setOnClickListener(this);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        initStartDateTime = formatterUse.format(curDate);
        tvGateDate.setText(formatter.format(curDate));

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(CommonFormsPrivateCarActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(3); // 最多选择照片数量，默认为3
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(CommonFormsPrivateCarActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });

        Arrays.sort(wrongPosition);
        //違規地點下拉列表選擇
        spPosition.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wrongPosition));
        spPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = wrongPosition[position];
                wrongPositionSp = wrongPosition[position];
                Log.e("---------", "最喜欢的水果是：" + str);
                if (str.equals("其他")){
                    //trOtherPosition.setVisibility(View.VISIBLE);
                }else{
                    //trOtherPosition.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_title_right://提交
                check();
                break;
            case R.id.tv_gate_date:
                DateTimePickDialogUtil dateTimePicKDialog2 = new DateTimePickDialogUtil(CommonFormsPrivateCarActivity.this, initStartDateTime);
                dateTimePicKDialog2.dateTimePicKDialog(tvGateDate, "", "");
                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(CommonFormsPrivateCarActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(3); // 最多选择照片数量，默认为3
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
        }
    }

    private void check(){
        if(FoxContext.getInstance().getLoginId().equals("")){
            ToastUtils.showLong(CommonFormsPrivateCarActivity.this,"登錄超時,請退出重新登登錄!");
            return;
        }
        try {
            selectTime = formatter.parse(tvGateDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (selectTime.getTime()-curDate.getTime()>0){
            ToastUtils.showShort(CommonFormsPrivateCarActivity.this,"請检查稽核時間是否在當前時間之前");
            return;
        }
        if (wrongPositionSp.equals("請選擇")){
            ToastUtils.showShort(CommonFormsPrivateCarActivity.this,"請選擇違規地點!");
            return;
        }
        if (wrongSp.equals("請選擇")){
            ToastUtils.showShort(CommonFormsPrivateCarActivity.this,"請選擇違規描述!");
            return;
        }
        if (team.equals("請選擇")){
            ToastUtils.showShort(CommonFormsPrivateCarActivity.this,"請選擇稽核課隊!");
            return;
        }

        if (wrongSp.equals("其他")){
            if(etOther.getText().toString().equals("")){
                ToastUtils.showShort(CommonFormsPrivateCarActivity.this,"請填寫違規描述!");
                return;
            }else{
                paramMap.put("wj", etOther.getText().toString());
            }
        }else{
            paramMap.put("wj",wrongSp);
        }
        if (wrongPositionSp.equals("其他")){
//            if(etOtherPosition.getText().toString().equals("")){
//                ToastUtils.showShort(CommonFormsPrivateCarActivity.this,"請填寫違規地點!");
//                return;
//            }else{
//                paramMap.put("wj_address", etOtherPosition.getText().toString());
//            }
        }else{
            paramMap.put("wj_address",wrongPositionSp);
        }
        if (imagePaths==null) {
            ToastUtils.showShort(this, "請選擇圖片");
            return;
        }

        //upMsessage();
    }
    private void getMessage(){
        showDialog();
        final String url = Constants.HTTP_COMMON_FORMS_TWO_WHEEL_SERVLET+"?code="+result;

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
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        empMessagesList = new ArrayList<EmpMessage>();

                        for (JsonElement type : array) {
                            EmpMessage humi = gson.fromJson(type, EmpMessage.class);
                            empMessagesList.add(humi);

                        }

                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();
                        empFileList = new ArrayList<EmpFile>();
                        for (JsonElement type1 : array1) {
                            EmpFile humi1 = gson.fromJson(type1, EmpFile.class);
                            empFileList.add(humi1);
                        }


                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    }else{
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            } }.start();
    }

    private void upMsessage(){

        final String url = Constants.HTTP_COMMON_FORMS_PHOTOS_SERVLET;
//        final String url = "http://192.168.1.112:8080/Server/CommonFormsupload_photoservlet";//            二輪車
       // flag 標誌位C  code 工號  wj_address違規地點  wj違紀描述  kedui 稽核課隊   (有圖片)       final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡

        paramMap.put("flag", "C");
      //  paramMap.put("code", tvID.getText().toString());
        paramMap.put("login_code", FoxContext.getInstance().getLoginId());
        paramMap.put("login_name",FoxContext.getInstance().getName());
        paramMap.put("WORKNO",empMessagesList.get(0).getWORKNO());
        paramMap.put("CHINESENAME",empMessagesList.get(0).getCHINESENAME());
        paramMap.put("BU_CODE",empMessagesList.get(0).getBU_CODE());
        paramMap.put("ORGNAME",empMessagesList.get(0).getORGNAME());
        paramMap.put("CZC03",empMessagesList.get(0).getCZC03());
        paramMap.put("INCUMBENCYSTATE",empMessagesList.get(0).getINCUMBENCYSTATE());
        paramMap.put("date0",tvGateDate.getText().toString());

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
                //  uploadFileByOkHTTP(context, actionUrl, compressedPic);
//                showDialog("111"+compressImage);
                fileMap.put("file"+i,compressedPic);//添加第一個文件

            }else{//直接上传
                // uploadFileByOkHTTP(context, actionUrl, pictureFile);
//                showDialog("222"+entryFile.getValue());
                fileMap.put("file"+i,pictureFile);
            }
        }

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
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
                } finally {
                    FileUtil.deletePhotos(CommonFormsPrivateCarActivity.this);
                }
            }
        }.start();
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

                    ToastUtils.showLong(CommonFormsPrivateCarActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
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

    private void setText() {
//        tvID.setText(empMessagesList.get(0).getWORKNO());
//        tvName.setText(empMessagesList.get(0).getCHINESENAME());
//        tvPro.setText(empMessagesList.get(0).getBU_CODE());
//        tvDep.setText(empMessagesList.get(0).getCZC03());
//        tvShow.setVisibility(View.VISIBLE);
//        tvShow.setText("年度已違規 "+empMessagesList.get(0).getYEAR_COUNT()+" 次");
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
                Glide.with(CommonFormsPrivateCarActivity.this)
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


}
