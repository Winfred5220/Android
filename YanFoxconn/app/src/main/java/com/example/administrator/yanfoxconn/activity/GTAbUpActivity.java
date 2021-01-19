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

import com.baidu.location.BDLocation;
import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.GBYueNanCheckAdapter;
import com.example.administrator.yanfoxconn.adapter.GTAbUPAdapter;
import com.example.administrator.yanfoxconn.bean.SelectModel;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiCheckMsg;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Song
 * on 2021/1/5
 * Description：營建工程點檢 帶點檢項的巡檢維護界面
 */
public class GTAbUpActivity extends BaseActivity implements View.OnClickListener,TimeDatePickerDialog.TimePickerDialogInterface {

    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息
    private final int MESSAGE_JUMP = 4;//跳轉維護界面
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_CHECK = 6;
    private final int MESSAGE_GET_DIM_LOCAL = 0;//獲取dimLocal

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.tv_d_name)
    TextView tvDName;//點位名稱
    @BindView(R.id.rv_option)
    RecyclerView rvOption;//點檢項目列表
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    GridView gvPhoto;//图片显示区域

    private String flag;//D,點位巡檢
    private String qrResult;//project_no
    private String dimLocal;//點位位置用於提交
    private GTAbUPAdapter comAbAdapter;//點檢列表適配器
    private HashMap<Integer, String> isSelected = new HashMap<>();//用户存储条目的选择状态
    private List<Integer> selectList;//用于存放無異常的条目
    private List<Integer> noselectList;//用于存放有異常的条目
    private List<Integer> inputTureList;//用于存放輸入正常的条目
    private List<Integer> inputFalseList;//用于存放輸入異常的条目
    private List<Integer> selectTureList;//用于存放下拉正常的条目
    private List<Integer> selectFalseList;//用于存放下拉異常的条目
    private int key1, key2,key3 = 0;//提交時管控未填寫信息
    private String url;//請求地址
    private String result;//請求返回結果
    private String type;//類型
    private List<ZhiyinshuiCheckMsg> mCheckMsgList;

private int progress=0;//施工進度
    private String todayWork="";//今日施工內容

    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private static final int REQUEST_GT_CAMERA_CODE = 33;
    private static final int REQUEST_GT_PREVIEW_CODE = 44;

    private ArrayList<String> imagePaths = null;//圖片未壓縮地址

    private GridAdapter gridAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gt_ab_up);

        ButterKnife.bind(this);
        tvTitle.setText("巡檢界面");
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        qrResult = getIntent().getStringExtra("dimId");
        flag = getIntent().getStringExtra("flag");
        type = FoxContext.getInstance().getType();
        dimLocal = getIntent().getStringExtra("name");
        tvDName.setText(getIntent().getStringExtra("name"));
        progress = getIntent().getIntExtra("progress",0);
        todayWork = getIntent().getStringExtra("todayWork");
        getItem(type, qrResult, flag);

        ivEmpty.setOnClickListener(this);
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gvPhoto.setNumColumns(cols);

        // preview
        gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(GTAbUpActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(9); // 最多选择照片数量，默认为1
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult(intent, REQUEST_GT_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(GTAbUpActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_GT_PREVIEW_CODE);
                }
            }
        });
    }




    /**
     * 獲取點檢項目
     * @param type  權限
     * @param dimId 二維碼主鍵
     * @param flag  類型S為器材,D為點位
     */
    public void getItem(String type, String dimId, String flag) {
        showDialog();
        url = Constants.HTTP_WATER_INFO_SERVLET + "?type=" + type + "&dim_id=" + dimId + "&flag=" + flag;


        Log.e("-----", "fff--" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForGet(url);
                dismissDialog();
                Gson gson = new Gson();
                Log.e("---fff--result----", result.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
//
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        mCheckMsgList = new ArrayList<ZhiyinshuiCheckMsg>();

                        for (JsonElement type : array) {
                            ZhiyinshuiCheckMsg humi = gson.fromJson(type, ZhiyinshuiCheckMsg.class);
                            mCheckMsgList.add(humi);

                        }
                        Message message = new Message();
                        message.what = MESSAGE_SET_CHECK;
//                        message.obj = messageList;
                        mHandler.sendMessage(message);
                    } else {

                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString() + ",請聯繫部門管理人員維護資料!";
                        mHandler.sendMessage(message);
                    }
                } else {
                    ToastUtils.showShort(GTAbUpActivity.this, "請求不成功");
                }

            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(GTAbUpActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_GT_CAMERA_CODE);
                break;
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:
                if (mCheckMsgList != null) {

                    check();
                } else {
                    ToastUtils.showLong(GTAbUpActivity.this, "無點檢項,提交無效!");
                }
                break;
        }
    }
    //提交前檢查
    private void check() {

        url = Constants.HTTP_YJ_SCAN_OK; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        HashMap<Integer, ArrayList<String>> imagePathsMap; //存放圖片地址的map
        HashMap<Integer, String> etMap;//存放editText值的map
        HashMap<Integer, String> etCheckMap;//存放editText值的map
        BDLocation location = FoxContext.getInstance().getLocation();
        if (location == null || location.getLongitude() == 0.0 || location.getLatitude() == 0.0) {
            ToastUtils.showLong(this, "位置信息獲取失敗請稍後或移動后重試!");
        }
        imagePathsMap = GTAbUPAdapter.getImagePathsMap();
        etMap = GTAbUPAdapter.getEtMap();
        etCheckMap = GTAbUPAdapter.getEtCheckMap();
        JsonArray array = new JsonArray();

            object.addProperty("dim_id", qrResult);


        object.addProperty("type", FoxContext.getInstance().getType());
        object.addProperty("dim_locale", dimLocal);
        object.addProperty("flag", "D");
        object.addProperty("sc_lat", FoxContext.getInstance().getLocation().getLatitude());
        object.addProperty("sc_lng", FoxContext.getInstance().getLocation().getLongitude());
        object.addProperty("sc_creator", FoxContext.getInstance().getName());
        object.addProperty("sc_creator_id", FoxContext.getInstance().getLoginId());

        selectList = new ArrayList<>();
        noselectList = new ArrayList<>();
        inputTureList = new ArrayList<>();
        inputFalseList = new ArrayList<>();
        selectTureList = new ArrayList<>();
        selectFalseList = new ArrayList<>();
        //清除之前选择的数据
        selectList.clear();//選項true
        noselectList.clear();//選項false
        inputTureList.clear();//輸入正常
        inputFalseList.clear();//輸入異常
        selectTureList.clear();//下拉正常
        selectFalseList.clear();//下拉異常
        //遍历map集合
        for (int key : isSelected.keySet()) {
            Log.e("-----------", "isSelected.get(key)===" + isSelected.get(key));
            //判断是否已选择，如果已选择，则添加进selectList
            if (isSelected.get(key).equals("true")) {
                selectList.add(key);
            } else if(isSelected.get(key).equals("false")){
                noselectList.add(key);
            } else if (isSelected.get(key).equals("input_true")) {
                inputTureList.add(key);
            } else if(isSelected.get(key).equals("input_false")){
                inputFalseList.add(key);
            } else if (isSelected.get(key).equals("select_true")){
                selectTureList.add(key);
            } else if (isSelected.get(key).equals("select_false")){
                selectFalseList.add(key);
            }

        }

//        Log.e("----------", "selectList" + selectList.toString());
//        Log.e("----------", "noselectList" + noselectList.toString());
//        Log.e("----------", "inputTureList" + inputTureList.toString());
//        Log.e("----------", "inputFalseList" + inputFalseList.toString());
//        Log.e("----------", "selectTureList" + selectTureList.toString());
//        Log.e("----------", "selectFalseList" + selectFalseList.toString());

        for (int i = 0; i < selectList.size(); i++) {
            int j = selectList.get(i);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", mCheckMsgList.get(j).getId());
            jsonObject.addProperty("desp", "");

            if (mCheckMsgList.get(j).getOption().get(0).getFlag().equals("0")){//正常
                jsonObject.addProperty("result", mCheckMsgList.get(j).getOption().get(0).getOpt());
            }else {//異常
                jsonObject.addProperty("result", mCheckMsgList.get(j).getOption().get(1).getOpt());
            }
            jsonObject.addProperty("opt_flag", 0);
            JsonArray photoArray = new JsonArray();
            jsonObject.add("photo", photoArray);
            array.add(jsonObject);
        }

        for (int i = 0; i < noselectList.size(); i++) {
            int j = noselectList.get(i);
            if (etMap.get(j) == null || etMap.get(j).equals("")) {
                key1++;
                Log.e("-----------", "key1-----" + key1);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", mCheckMsgList.get(j).getId());
            if (mCheckMsgList.get(j).getOption().get(0).getFlag().equals("0")){
                jsonObject.addProperty("result", mCheckMsgList.get(j).getOption().get(1).getOpt());
            }else {
                jsonObject.addProperty("result", mCheckMsgList.get(j).getOption().get(0).getOpt());
            }
            jsonObject.addProperty("opt_flag", 1);
            jsonObject.addProperty("desp", etMap.get(j));
            JsonArray photoArray = new JsonArray();

            if (imagePathsMap.get(j) != null && imagePathsMap.get(j).size()!=0) {
                for (int k = 0; k < imagePathsMap.get(j).size(); k++) {
                    final String pic_path = imagePathsMap.get(j).get(k);
                    Log.e("------pic_path-------", pic_path);
                    String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                    String _path =  sign_dir + File.separator  + System.currentTimeMillis() +j+k+ ".jpg";
                    Log.e("------_path-------", _path);
                    final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 50);
                    Log.e("-------compressImage------", compressImage);
                    String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
                    Log.e("-------picBase64Code-------", "===="+ picBase64Code);
                    JsonObject jsonObject1 = new JsonObject();
                    jsonObject1.addProperty("file", picBase64Code);
                    photoArray.add(jsonObject1);
                }
            }else {
                key2++;
                Log.e("-----------", "key2-----" + key2);
            }
            jsonObject.add("photo", photoArray);
            array.add(jsonObject);
        }

        for (int i = 0; i < inputTureList.size(); i++) {
            int j = inputTureList.get(i);
            if (etCheckMap.get(j) == null || etCheckMap.get(j).equals("")) {
                key3++;
                Log.e("-----------", "key3-----" + key3);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", mCheckMsgList.get(j).getId());
            jsonObject.addProperty("desp", mCheckMsgList.get(j).getContent());
            jsonObject.addProperty("result", etCheckMap.get(j));
            jsonObject.addProperty("opt_flag", 0);
            JsonArray photoArray = new JsonArray();
            jsonObject.add("photo", photoArray);
            array.add(jsonObject);
            if (mCheckMsgList.get(j).getContent().equals("進度-累計進度")){
                if (Integer.parseInt(etCheckMap.get(j))<progress){
                    ToastUtils.showLong(GTAbUpActivity.this,"請注意工程進度填寫有誤，小於上次進度！");
                    return;
                }
            }
        }

        for (int i = 0; i < inputFalseList.size(); i++) {
            int j = inputFalseList.get(i);
            if (etCheckMap.get(j) == null || etCheckMap.get(j).equals("")) {
                key3++;
                Log.e("-----------", "key3-----" + key3);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", mCheckMsgList.get(j).getId());
            jsonObject.addProperty("desp", "");
            jsonObject.addProperty("result", etCheckMap.get(j));
            jsonObject.addProperty("opt_flag", 1);
            JsonArray photoArray = new JsonArray();
            jsonObject.add("photo", photoArray);
            array.add(jsonObject);
        }

        for (int i = 0; i < selectTureList.size(); i++) {
            int j = selectTureList.get(i);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", mCheckMsgList.get(j).getId());
            jsonObject.addProperty("desp", "");
            jsonObject.addProperty("result", etCheckMap.get(j));
            jsonObject.addProperty("opt_flag", 0);
            JsonArray photoArray = new JsonArray();
            jsonObject.add("photo", photoArray);
            array.add(jsonObject);
        }

        for (int i = 0; i < selectFalseList.size(); i++) {
            int j = selectFalseList.get(i);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", mCheckMsgList.get(j).getId());
            jsonObject.addProperty("desp", etMap.get(j));
            jsonObject.addProperty("result", etCheckMap.get(j));
            jsonObject.addProperty("opt_flag", 1);

            JsonArray photoArray = new JsonArray();

            if (imagePathsMap.get(j) != null && imagePathsMap.get(j).size()!=0) {
                for (int k = 0; k < imagePathsMap.get(j).size(); k++) {
                    final String pic_path = imagePathsMap.get(j).get(k);
                    Log.e("------pic_path-------", pic_path);
                    String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                    String _path =  sign_dir + File.separator  + System.currentTimeMillis() +j+k+ ".jpg";
                    Log.e("------_path-------", _path);
                    final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 50);
                    Log.e("-------compressImage------", compressImage);
                    String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
                    Log.e("-------picBase64Code-------", "===="+ picBase64Code);
                    JsonObject jsonObject1 = new JsonObject();
                    jsonObject1.addProperty("file", picBase64Code);
                    photoArray.add(jsonObject1);
                }
            }
            jsonObject.add("photo", photoArray);
            array.add(jsonObject);
        }
        object.add("info", array);

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
        object.add("xc_photo", photoArray);

        Log.e("-----object------", object.toString());


        if (key1 > 0) {
            ToastUtils.showShort(this, "請填寫異常信息");
            key1 = 0;
            key2 = 0;
            return;
        }
        if (key2 > 0) {
            ToastUtils.showShort(this, "請上傳圖片");
            key2 = 0;
            return;
        }
        if (key3 > 0) {
            ToastUtils.showShort(this, "請請填寫點檢信息");
            key3 = 0;
            return;
        }



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
                    FileUtil.deletePhotos(GTAbUpActivity.this);
                }
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_GET_DIM_LOCAL:


                        getItem(type, qrResult, flag);


                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(GTAbUpActivity.this, R.string.net_mistake);
                    break;
                case MESSAGE_TOAST:
                    ToastUtils.showShort(GTAbUpActivity.this, msg.obj.toString());
                    finish();
                    break;
//                case MESSAGE_SET_TEXT://text賦值
//                    setText();
//
//                    if (status.equals("0-N")){
//                        getCheckMessage();
//                    }else {
//                        aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    }
//                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(), MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
                    break;
                case MESSAGE_SET_CHECK://點檢項列表賦值

                    if (mCheckMsgList != null) {

                            LinearLayoutManager layoutManager = new LinearLayoutManager(GTAbUpActivity.this);
                            rvOption.setLayoutManager(layoutManager);
                            rvOption.setItemViewCacheSize(500);
                            comAbAdapter = new GTAbUPAdapter(GTAbUpActivity.this, mCheckMsgList, isSelected,progress,todayWork);
                            rvOption.setAdapter(comAbAdapter);

                    } else {
                        ToastUtils.showShort(GTAbUpActivity.this, "沒有數據!");
                    }

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
                        if (imagePaths==null||imagePaths.size()==0){
                            ToastUtils.showLong(GTAbUpActivity.this,"请上传菜品照片！");
                        }else if (type == MESSAGE_TOAST) {
                            finish();
                        } else if (type == MESSAGE_UP) {
                            check();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub

//                        if (type==MESSAGE_TOAST){
//                            finish();
//                        }else if(type==MESSAGE_UP){
//                            check();
//                        }
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
                        } else if (type == MESSAGE_JUMP) {
                            ToastUtils.showShort(GTAbUpActivity.this, "跳轉異常界面!");
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub

//                        if (type==MESSAGE_TOAST){
//                            finish();
//                        }else if(type==MESSAGE_UP){
//                            check();
//                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private ImageCaptureManager captureManager; // 相机拍照处理类

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("---------","resultCode=="+resultCode);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    Log.e("==========", "Picker==="+ comAbAdapter.getPosition());
                    comAbAdapter.loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT), comAbAdapter.getPosition());

                    break;
                // 预览
                case REQUEST_PREVIEW_CODE:
                    comAbAdapter.loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT), comAbAdapter.getPosition());
                    Log.e("==========", "Preview==="+ comAbAdapter.getPosition());
                    break;
                // 调用相机拍照
                case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                    if (captureManager.getCurrentPhotoPath() != null) {
                        captureManager.galleryAddPic();
                        ArrayList<String> paths = new ArrayList<>();
                        paths.add(captureManager.getCurrentPhotoPath());
                        comAbAdapter.loadAdpater(paths, comAbAdapter.getPosition());
                        Log.e("==========", paths.get(1));
                    }
                    break;



                    // 选择照片
                    case REQUEST_GT_CAMERA_CODE:
                        loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
                        break;
                    // 预览
                    case REQUEST_GT_PREVIEW_CODE:
                        loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT));
                        break;


                }

        }
    }
    //时间选择器----------确定
    @Override
    public void positiveListener() {

        GTAbUPAdapter.positiveListener();
    }

    //时间选择器-------取消
    @Override
    public void negativeListener() {

        GTAbUPAdapter.negativeListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        GTAbUPAdapter.getImagePathsMap().clear();
        GTAbUPAdapter.getEtMap().clear();
        GTAbUPAdapter.getEtCheckMap().clear();
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
                Glide.with(GTAbUpActivity.this)
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

}
