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
import android.text.Editable;
import android.text.TextWatcher;
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

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.SanFangListAdapter;
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
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import butterknife.BindView;
import butterknife.ButterKnife;
import taobe.tec.jcc.JChineseConvertor;

public class SanFangActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_NETMISTAKE = 4;

    private String initStartDateTime; // 初始化开始时间
    private SimpleDateFormat formatter;
    private Date selectTime = null;//所選時間
    private Date curDate = null;//當前時間


    private List<String> teamList;
    private List<String> teamList1;
    private List<String> teamList2;
    private List<String> teamList3;
    private SanFangListAdapter mAdapter;
    private List<EmpMessage> empMessagesList;
    private List<EmpMessage> empMessagesList1;
    private List<EmpMessage> empMessagesList2;
    private List<EmpMessage> empMessagesList3;

    private String areaSp = "";//區域
    private String typeSp = "";//隱患類別
    private String teamSp = "";//稽核課隊


    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private GridAdapter gridAdapter;

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_date)
    TextView tvDate;//時間日期
    @BindView(R.id.et_gate_post)
    EditText etGatePost;//責任單位
    @BindView(R.id.lv_gate)
    MyListView lvGate;//責任單位列表
    @BindView(R.id.tr_list_gate)
    TableRow trLIstGate;//責任單位列表
    @BindView(R.id.et_zong)
    EditText etZong;//樓棟
    @BindView(R.id.et_wrong)
    EditText etWrong;//隱患描述
    @BindView(R.id.sp_area)
    Spinner spArea;//區域
    @BindView(R.id.sp_type)
    Spinner spType;//隱患類別
    @BindView(R.id.sp_team)
    Spinner spTeam;//稽核科隊

    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    GridView gridView;//图片显示区域

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_san_fang);
        ButterKnife.bind(this);

        tvTitle.setText("三防隱患");
        btnBack.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);
        btnUp.setText(R.string.up_abnormal);
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);


        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        initStartDateTime = formatterUse.format(curDate);
        tvDate.setText(formatter.format(curDate));

        getMessage();
        //搜索关键字
        etGatePost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                trLIstGate.setVisibility(View.VISIBLE);
                String a = etGatePost.getText().toString();
                //调用适配器里面的搜索方法
                mAdapter.SearchCity(a);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(SanFangActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(SanFangActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:
                check();
                break;
            case R.id.tv_date:
                DateTimePickDialogUtil dateTimePicKDialog1 = new DateTimePickDialogUtil(SanFangActivity.this, initStartDateTime);
                dateTimePicKDialog1.dateTimePicKDialog(tvDate, "", "");
                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(SanFangActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
        }
    }

    private void check(){
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
            return;
        }
        if (etGatePost.getText().toString()==null||etGatePost.getText().toString().equals("")) {
            ToastUtils.showShort(this, "責任單位不能為空");
        }else if (etZong.getText().toString()==null||etZong.getText().toString().equals("")) {
            ToastUtils.showShort(this, "樓棟不能為空");
        }else if (etWrong.getText().toString()==null||etWrong.getText().toString().equals("")) {
            ToastUtils.showShort(this, "隱患描述不能為空");
        }else if (imagePaths==null) {
            ToastUtils.showShort(this, "請拍照或選擇圖片");
        }else{
            upMessage();
        }


    }

    private void getMessage(){
        showDialog();
        final String url = Constants.HTTP_SANFANG_SERVLET+"?type=1";

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);

                dismissDialog();
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("file1").getAsJsonArray();
                        empMessagesList = new ArrayList<EmpMessage>();
                        for (JsonElement type : array) {
                            EmpMessage humi = gson.fromJson(type, EmpMessage.class);
                            empMessagesList.add(humi);

                        }

                        JsonArray array1 = jsonObject.get("file2").getAsJsonArray();
                        empMessagesList1 = new ArrayList<EmpMessage>();
                        for (JsonElement type1 : array1) {
                            EmpMessage humi1 = gson.fromJson(type1, EmpMessage.class);
                            empMessagesList1.add(humi1);
                        }

                        JsonArray array2 = jsonObject.get("file3").getAsJsonArray();
                        empMessagesList2 = new ArrayList<EmpMessage>();
                        for (JsonElement type1 : array2) {
                            EmpMessage humi1 = gson.fromJson(type1, EmpMessage.class);
                            empMessagesList2.add(humi1);
                        }

                        JsonArray array3 = jsonObject.get("file4").getAsJsonArray();
                        empMessagesList3 = new ArrayList<EmpMessage>();
                        for (JsonElement type1 : array3) {
                            EmpMessage humi1 = gson.fromJson(type1, EmpMessage.class);
                            empMessagesList3.add(humi1);
                        }


                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else{
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                }
            } }.start();
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

                    ToastUtils.showLong(SanFangActivity.this, msg.obj.toString());
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
                case MESSAGE_NETMISTAKE://Toast
                    ToastUtils.showLong(SanFangActivity.this,R.string.net_mistake);
                    break;

            }
            super.handleMessage(msg);
        }
    };

    private void setText() {

        teamList = new ArrayList<>();
        for (int i = 0; i < empMessagesList.size(); i++) {

            teamList.add(change1(empMessagesList.get(i).getNAME()));

        }
        teamList1 = new ArrayList<>();
        for (int i = 0; i < empMessagesList1.size(); i++) {

            teamList1.add(change1(empMessagesList1.get(i).getNAME()));

        }
        teamList2 = new ArrayList<>();
        for (int i = 0; i < empMessagesList2.size(); i++) {

            teamList2.add(change1(empMessagesList2.get(i).getNAME()));

        }
        teamList3 = new ArrayList<>();
        for (int i = 0; i < empMessagesList3.size(); i++) {

            teamList3.add(change1(empMessagesList3.get(i).getNAME()));

        }

        mAdapter = new SanFangListAdapter(SanFangActivity.this, teamList);
        lvGate.setAdapter(mAdapter);

        //稽核門崗列表,選中后1:tit賦值,2:列表隱藏
        mAdapter.OnClickSetText(new SanFangListAdapter.OnClickSetText() {
            @Override
            public void OnClickxt(String tit) {
                etGatePost.setText(tit);
//                paramMap.put("mengang", tit.split(",")[0]);
                mAdapter.SearchCity("");
                trLIstGate.setVisibility(View.GONE);
            }
        });


        //區域下拉列表選擇
        spArea.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, teamList1));
        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaSp = teamList1.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //隱患類別下拉列表選擇
        spType.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, teamList2));
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeSp = teamList2.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //課隊下拉列表選擇
        spTeam.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, teamList3));
        spTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                teamSp = teamList3.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                Glide.with(SanFangActivity.this)
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


    public void upMessage() {
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        final String url = Constants.HTTP_SANFANG_UPLOAD_SERVLET; //此處寫上自己的URL
        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡

        paramMap.put("flag", "SF");
        paramMap.put("creat_date", tvDate.getText().toString());
        paramMap.put("bumen", etGatePost.getText().toString());
        paramMap.put("cpc", areaSp);
        paramMap.put("kedui", teamSp);
        paramMap.put("wj_address", typeSp);
        paramMap.put("wj_remark", etZong.getText().toString());
        paramMap.put("other", etWrong.getText().toString());
        paramMap.put("login_code",FoxContext.getInstance().getLoginId());
        paramMap.put("login_name", FoxContext.getInstance().getName());

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
                } finally {
                    FileUtil.deletePhotos(SanFangActivity.this);
                }
            }
        }.start();
    }

    //简体转成繁体
    public String change(String changeText) {
        try {
            JChineseConvertor jChineseConvertor = JChineseConvertor.getInstance();
            changeText = jChineseConvertor.s2t(changeText);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeText;
    }

    //繁体转成简体
    public String change1(String changeText) {
        try {
            JChineseConvertor jChineseConvertor = JChineseConvertor.getInstance();
            changeText = jChineseConvertor.t2s(changeText);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeText;
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
