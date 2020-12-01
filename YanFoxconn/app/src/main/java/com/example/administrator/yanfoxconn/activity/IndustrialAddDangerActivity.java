package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CaseListAdapter;
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
import com.example.administrator.yanfoxconn.widget.MyGridView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import taobe.tec.jcc.JChineseConvertor;
/**
 * 工安巡檢添加隱患
 * Created by wang on 2019/8/4.
 */
public class IndustrialAddDangerActivity extends BaseActivity implements View.OnClickListener{

    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private String signPath="";
    private String[] typeData = {"請選擇","消防安全類","電氣安全類","機械安全類","危化管理類","特種設備類","施工管理類","職業危害類","教育訓練類","其他安全類"};
    private String[] floorData = {"樓層","1F","1.5F","2F","3F","4F"};
    private String[] TechnologyData = {"請選擇","成型","組裝","SMT","塗裝","衝壓","倉庫","辦公室","無塵室","模具廠","配電室","打料房","餐廳","鞋櫃區","吸塑","中小型塑模","華北檢測","表面處理一","機房","電鍍","蝕刻","水洗","閒置區","磨具","機加","化成","壓鑄"};
    private List<String> tungData = new ArrayList<>();//樓棟列表
    private List<String> productData =  new ArrayList<>();//產品處列表
    private List<String> caseData =  new ArrayList<>();//專案名稱列表
    private CaseListAdapter mAdapter;
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private GridAdapter gridAdapter;
    private String url;//地址
    private String result;//网络获取结果
    private String caseId;//
    private String address;//
    private String department;//
    private String type;//隱患類型
    final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題

    @BindView(R.id.sp_type)
    Spinner spType;//隱患類型
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    MyGridView gridView;//图片显示区域
    @BindView(R.id.btn_sign)
    Button btnSign;//簽名按鈕
    @BindView(R.id.iv_sign)
    ImageView ivSign;//簽名按鈕
    @BindView(R.id.tv_address)
    TextView tvAddress;//隱患地點
    @BindView(R.id.et_position)
    EditText etPosition;//方位
    @BindView(R.id.tv_department)
    TextView tvDepart;//責任單位
    @BindView(R.id.sp_supervisor)
    Spinner spSupervisor;//責任主管
    @BindView(R.id.sp_owner)
    Spinner spOwner;//隱患類型
    @BindView(R.id.et_describe)
    EditText etDescribe;//隱患描述
    @BindView(R.id.et_person)
    EditText etPerson;//陪查人
//    @InjectView(R.id.lv_case)
//    MyListView lvCase;//專案名稱

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industrial_add_danger);
        ButterKnife.bind(this);

        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());
//        tvDate.setText(formatter.format(curDate));
        caseId = getIntent().getStringExtra("caseId");
        address = getIntent().getStringExtra("address");
        department = getIntent().getStringExtra("department");

        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);
        btnSign.setOnClickListener(signListener);
        tvAddress.setText(address);
        tvDepart.setText(department);
        FoxContext.getInstance().setTakePic("W0");
        tvTitle.setText("添加隱患");

//        區域下拉列表選擇
        spType.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, typeData));
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = typeData[position];

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //樓層下拉列表選擇
//        spSupervisor.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, floorData));
//        spSupervisor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                floor = floorData[position];
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
        //工藝下拉列表選擇
//        spSupervisor.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, TechnologyData));
//        spSupervisor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////                technology = TechnologyData[position];
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });


        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(IndustrialAddDangerActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(1); // 最多选择照片数量，默认为3
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(IndustrialAddDangerActivity.this);
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
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(IndustrialAddDangerActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(1); // 最多选择照片数量，默认为3
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right://提交
                upCheck();
                break;

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
            if (listUrls.size() == 1) {
                return 1;
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
                if (position == 1) {
                    imageView.setVisibility(View.GONE);
                }
            } else {
                Glide.with(IndustrialAddDangerActivity.this)
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
    private View.OnClickListener signListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            WritePadDialog writeTabletDialog = new WritePadDialog(IndustrialAddDangerActivity.this, new DialogListener() {
                @Override
                public void refreshActivity(Object object) {

                    Bitmap mSignBitmap = (Bitmap) object;
                    signPath = createFile(mSignBitmap);
//                    ArrayList<String> arrayList = new ArrayList<String>();
//                    arrayList.add(signPath);
//                    loadAdpater(arrayList);
                    ivSign.setVisibility(View.VISIBLE);
                    ivSign.setImageBitmap(mSignBitmap);

                }
            });
            writeTabletDialog.show();
        }
    };
    /**
     * 创建手写签名文件
     *
     * @return
     */
    private String createFile(Bitmap bitmap) {
        ByteArrayOutputStream baos = null;
        String _path = null;

        try {
            String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
            _path =  sign_dir + File.separator  + System.currentTimeMillis() + ".jpg";
            Log.e("------------", _path);
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            baos.flush();
            baos.close();

            byte[] photoBytes = baos.toByteArray();
            if (photoBytes != null) {
                File file = new File(sign_dir);
                if (!file.exists() && !file.isDirectory()) {
                    file.mkdirs();
                }

                new FileOutputStream(new File(_path)).write(photoBytes);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(_path));
                intent.setData(uri);
                IndustrialAddDangerActivity.this.sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦

            }
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return _path;
    }
    public void setText(){
        Log.e("--------caseData--------", caseData.toString() );
        mAdapter = new CaseListAdapter(IndustrialAddDangerActivity.this,caseData);
//        lvCase.setAdapter(mAdapter);

        //稽核門崗列表,選中后1:tit賦值,2:列表隱藏
//        mAdapter.OnClickSetText(new CaseListAdapter.OnClickSetText() {
//            @Override
//            public void OnClickxt(String tit) {
//                etCase.setText(tit);
//                mAdapter.SearchCity("");
//                lvCase.setVisibility(View.GONE);
//            }
//        });
    }

    public void upCheck(){
        if(type.equals("請選擇")){
            ToastUtils.showShort(IndustrialAddDangerActivity.this,"請選擇隱患類型");
            return;
        }else if(imagePaths==null){
            ToastUtils.showShort(IndustrialAddDangerActivity.this,"請拍照");
            return;
        }else if(etDescribe.getText().toString()==null||etDescribe.getText().toString().equals("")){
            ToastUtils.showShort(IndustrialAddDangerActivity.this,"請填寫隱患描述");
            return;
        }else if(etPerson.getText().toString()==null||etPerson.getText().toString().equals("")){
            ToastUtils.showShort(IndustrialAddDangerActivity.this,"請填寫陪查人");
            return;
        }else if(signPath.equals("")){
            ToastUtils.showShort(IndustrialAddDangerActivity.this,"請確認人簽字");
            return;
        }
        upMessage();
    }

    public void setTungAndProduct(){

        //樓棟下拉列表選擇
//        spTung.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tungData));
//        spTung.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                tung = tungData.get(position);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

        //產品處下拉列表選擇
//        spProduct.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, productData));
//        spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                product = productData.get(position);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
    }

    public void getSupervisorAndOwnerData(final String area,final String tung){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    String area1 =  URLEncoder.encode(URLEncoder.encode(area.toString(), "UTF-8"), "UTF-8");
                    String tung1 =  URLEncoder.encode(URLEncoder.encode(tung.toString(), "UTF-8"), "UTF-8");
                    Request request = new Request.Builder()
                            .url(Constants.HTTP_TUNGANDPRODUCT_SERVLET + "?area=" + area1 +"&tung=" + tung1)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Log.d("kwwl","response.code()=="+response.code());
                        Log.d("kwwl","response.message()=="+response.message());
                        Log.d("kwwl","res=="+result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String ss = jsonObject.get("data").getAsJsonArray().toString();
                        JSONArray data = new JSONArray(ss);
                        for (int i=0;i<data.length();i++){
                            JSONObject object = data.getJSONObject(i);
                            Log.e("object.getString(name1)",object.getString("name1"));
                            tungData.add(object.getString("name1"));
                        }

                        String ss1 = jsonObject.get("data1").getAsJsonArray().toString();
                        JSONArray data1 = new JSONArray(ss1);
                        for (int i=0;i<data1.length();i++){
                            JSONObject object1 = data1.getJSONObject(i);
                            productData.add(object1.getString("name1"));
                        }

                        String ss2 = jsonObject.get("data2").getAsJsonArray().toString();
                        JSONArray data2 = new JSONArray(ss2);
                        for (int i=0;i<data2.length();i++){
                            JSONObject object2 = data2.getJSONObject(i);
                            caseData.add(change1(object2.getString("name1")));
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        mHandler.sendMessage(message);
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
                    ToastUtils.showLong(IndustrialAddDangerActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                case MESSAGE_SET_TEXT://text賦值
                    setText();
                    break;
                  }
            super.handleMessage(msg);
        }
    };

    private void upMessage(){

        final String url = Constants.HTTP_INDUSSAFE_DANGER_UPDATE_SERVLET;

        paramMap.put("caseId", caseId);
        paramMap.put("type", type);
        paramMap.put("describe", etDescribe.getText().toString());
        paramMap.put("address", tvAddress.getText().toString() + etPosition.getText().toString());
        paramMap.put("product",tvDepart.getText().toString());
        paramMap.put("supervisor",etPerson.getText().toString());
        paramMap.put("person",etPerson.getText().toString());
        paramMap.put("owner",etPerson.getText().toString());

        final Map<String, File> fileMap = new HashMap<String, File>(); //檔全部添加到Map裡

        imagePaths.add(signPath);
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
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                FileUtil.deletePhotos(IndustrialAddDangerActivity.this);
                                finish();
                                }
                            }, 2000);
                        }else{
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = b.getResponseMessage();
                            mHandler.sendMessage(message);
                        }
                    }
                    Log.e("---","ddddelete");
                } catch (Exception e) {
                    e.printStackTrace();
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
            JChineseConvertor jChineseConvertor = JChineseConvertor
                    .getInstance();
            changeText = jChineseConvertor.t2s(changeText);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeText;
    }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FoxContext.getInstance().setTakePic("");
        String filepath = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos" + File.separator;
        FileUtil.deleteDir(filepath,IndustrialAddDangerActivity.this);
    }

}
