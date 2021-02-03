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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.OCRMessage;
import com.example.administrator.yanfoxconn.bean.RetreatMsg;
import com.example.administrator.yanfoxconn.bean.RouteMessage;
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
import com.example.administrator.yanfoxconn.utils.TimeDateUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 無法點檢異常上傳
 * Created by wang on 2021/01/30.
 */
public class AbnormalCantCheckActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_RECORD = 6;
    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private String url;//地址
    private String result;//网络获取结果
    private String code;//工號或身份證號
    private List<String> positionList = new ArrayList<>();//位置
    private List<String> dimIdList = new ArrayList<>();//位置id
    private List<String> countList = new ArrayList<>();//加減分數
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private String posi="";//位置
    private String dimId="";//位置id
    private String type="";//類別
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.sp_position)
    Spinner spPosition;//位置
    @BindView(R.id.et_describe)
    EditText etDescribe;//原因

    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    GridView gridView;//图片显示区域
    private GridAdapter gridAdapter;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;//確認

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cant_check);
        ButterKnife.bind(this);

        tvTitle.setText("上報異常");
        btnBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);
        type="FX,HC,HD";

        getRecord(type);

        //選取圖片
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(AbnormalCantCheckActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(3); // 最多选择照片数量，默认为3
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(AbnormalCantCheckActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
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
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_confirm:
                check();
                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(AbnormalCantCheckActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(3); // 最多选择照片数量，默认为3
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
        }
    }

    //給记录類別賦值
    public void setPosition(){

        //課程下拉列表選擇
        spPosition.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, positionList));
        spPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posi = positionList.get(position);
                dimId = dimIdList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    //獲取位置信息
    public void getRecord(String type){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    String type1 =  URLEncoder.encode(URLEncoder.encode(type.toString(), "UTF-8"), "UTF-8");
                    String url=Constants.HTTP_CANT_CHECK_BSAE +"?type="+type1;
                    Log.e("------url------",url);
                    Request request = new Request.Builder()
                            .url(url)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Log.e("------result------",result);
//                        Log.d("kwwl","response.code()=="+response.code());
//                        Log.d("kwwl","response.message()=="+response.message());
//                        Log.d("kwwl","res=="+result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String ss = jsonObject.get("data").getAsJsonArray().toString();
                        JSONArray data = new JSONArray(ss);
                        positionList.clear();dimIdList.clear();
                        for (int i=0;i<data.length();i++){
                            JSONObject object = data.getJSONObject(i);
                            Log.e("----dim_remark----",object.getString("dim_remark"));
                            positionList.add(object.getString("dim_locale"));
                            dimIdList.add(object.getString("dim_id"));
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_RECORD;
                        mHandler.sendMessage(message);
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    } else{
                        Message message = new Message();
                        message.what = MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private void check(){
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
            return;
        }
        if (posi.equals("請選擇")){
            ToastUtils.showShort(AbnormalCantCheckActivity.this,"請選擇地點!");
            return;
        }
        if(etDescribe.getText().toString().equals("")){
            ToastUtils.showLong(AbnormalCantCheckActivity.this,"請輸入原因!");
            return;
        }
        if (imagePaths==null) {
            ToastUtils.showShort(this, "請上傳圖片!");
            return;
        }
        btnConfirm.setClickable(false);
        aboutAlert("確定要提交嗎？",MESSAGE_SHOW);
    }

    //提交数据
    private void upMessage(){
        final String url = Constants.HTTP_CANT_CHECK_UP; //此處寫上自己的URL
        JsonObject object = new JsonObject();
        JsonArray photoArray = new JsonArray();

        object.addProperty("dim_id",dimId);//地點ID
        object.addProperty("position",posi);//地點
        object.addProperty("reason",etDescribe.getText().toString());//原因
        object.addProperty("login_code",FoxContext.getInstance().getLoginId());//點檢人工號
        object.addProperty("login_name",FoxContext.getInstance().getName());//點檢人姓名

        for (int i = 0; i < imagePaths.size(); i++) {
            File pictureFile = new File(imagePaths.get(i)); //通過路徑獲取檔
//            fileMap.put("file" + i, pictureFile);//添加第一個文件
            final String pic_path = imagePaths.get(i);
            String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
            String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
            //getFilesDir().getAbsolutePath()+"compressPic.jpg";
            //调用压缩图片的方法，返回压缩后的图片path
            final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 80);
            String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("file", picBase64Code);
            photoArray.add(jsonObject);
        }
        object.add("photo", photoArray);
        Log.e("----object----",object.toString() );
            //開啟一個新執行緒，向伺服器上傳資料
            new Thread() {
                public void run() {
                    //把网络访问的代码放在这里
                    try {
                        showDialog();
                        String result = HttpConnectionUtil.doPostJsonObject(url, object);
                        Log.e("===result===",  result);
                        dismissDialog();
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String errCode = jsonObject.get("errCode").getAsString();
                        if (errCode.equals("200")) {
                                Message message = new Message();
                                message.what = MESSAGE_UP;
                                message.obj = "提交成功！";
                                mHandler.sendMessage(message);
                        } else{
                            Message message = new Message();
                            message.what = MESSAGE_UP;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(AbnormalCantCheckActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值

//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
                    break;
                case MESSAGE_SET_RECORD://班導記錄賦值
                    setPosition();
            }
            super.handleMessage(msg);
        }
    };
    //提示彈出框
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
                        }else if(type==MESSAGE_SHOW){
                            upMessage();
                        }
                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (type==MESSAGE_TOAST){

                        }else if(type==MESSAGE_SHOW){
                            btnConfirm.setClickable(true);
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
                Glide.with(AbnormalCantCheckActivity.this)
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
