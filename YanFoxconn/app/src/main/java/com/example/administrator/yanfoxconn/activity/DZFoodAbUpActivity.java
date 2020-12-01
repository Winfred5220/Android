package com.example.administrator.yanfoxconn.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.example.administrator.yanfoxconn.bean.ComScanViewMessage;
import com.example.administrator.yanfoxconn.bean.DZFoodType;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Song
 * on 2020/8/20
 * Description：人資監餐 異常提交界面
 */
public class DZFoodAbUpActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;//showToast
    private final int MESSAGE_SET_TYPE2 = 0;//獲取狀態后,設置控件內容
    private final int MESSAGE_SET_TYPE3 = 1;//獲取狀態后,設置控件內容
    private final int MESSAGE_NOT_NET = 3;//網絡問題

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.tv_name)
    TextView tvName;//餐廳名稱
    @BindView(R.id.tv_score)
    TextView tvScore;//分數
    @BindView(R.id.sp_type1)
    Spinner spType1;//班导记录
    @BindView(R.id.sp_type2)
    Spinner spType2;//班导记录
    @BindView(R.id.sp_type3)
    Spinner spType3;//班导记录
    @BindView(R.id.et_description)
    EditText etDescription;//描述
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    GridView gvPhoto;//图片显示区域

    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;

    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private GridAdapter gridAdapter;

    private String[] type = {"菜品質量類", "服務質量類", "環境衛生類"};
    private List<String> type1;
    private List<DZFoodType> type2;
    private List<DZFoodType> type3;
    private String selectType1;

    private String url;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dz_food_ab_up);
        ButterKnife.bind(this);

        tvName.setText(getIntent().getStringExtra("name"));
        tvTitle.setText("異常上傳");
        btnBack.setText("返回");
        btnUp.setText("提交");
        btnBack.setOnClickListener(this);
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);

        type1 = new ArrayList<String>();
        type2 = new ArrayList<DZFoodType>();
        type3 = new ArrayList<DZFoodType>();

        for (int i = 0; i < type.length; i++) {
            type1.add(type[i]);
        }

        spType1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, type1));
        spType1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    selectType1 = type1.get(position);
                    try {
                        getType2(URLEncoder.encode(URLEncoder.encode(type1.get(position), "UTF-8"), "UTF-8"), FoxContext.getInstance().getType());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ivEmpty.setOnClickListener(this);
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gvPhoto.setNumColumns(cols);

        // preview
        gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(DZFoodAbUpActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(DZFoodAbUpActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });

    }


    /**
     * 根據類型1 獲取類型2
     *
     * @param name1 類型1
     * @param type  權限
     */
    private void getType2(String name1, String type) {
//        showDialog();
        url = Constants.HTTP_RZ_TYPE_SEARCH1 + "?name1=" + name1 + "&type=" + type;
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "=getcarLog=fff===" + url);
                Gson gson = new Gson();
                String response = result;

                if (result != null) {
                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        type2 = new ArrayList<DZFoodType>();
                        for (JsonElement type : array) {
                            DZFoodType humi = gson.fromJson(type, DZFoodType.class);
                            type2.add(humi);
                        }
                        Message message = new Message();
                        message.what = MESSAGE_SET_TYPE2;

                        mHandler.sendMessage(message);
                    } else {
//                        isShowReturn = false;
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_NOT_NET;
                    message.obj = "網絡問題請求失敗,請重試!";
                    mHandler.sendMessage(message);
                }
//                dismissDialog();
            }
        }.start();
    }

    /**
     * 根據類型1,2 獲取類型3
     *
     * @param name1 類型1
     * @param name2 類型2
     * @param type  權限
     */
    private void getType3(String name1, String name2, String type) {

        showDialog();
        url = Constants.HTTP_RZ_TYPE_SEARCH2 + "?name1=" + name1 + "&name2=" + name2 + "&type=" + type;
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "=getcarLog=fff===" + url);
                Gson gson = new Gson();
                String response = result;

                if (result != null) {
                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        type3 = new ArrayList<DZFoodType>();
                        for (JsonElement type : array) {
                            DZFoodType humi = gson.fromJson(type, DZFoodType.class);
                            type3.add(humi);
                        }
                        Message message = new Message();
                        message.what = MESSAGE_SET_TYPE3;

                        mHandler.sendMessage(message);
                    } else {
//                        isShowReturn = false;
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_NOT_NET;
                    message.obj = "網絡問題請求失敗,請重試!";
                    mHandler.sendMessage(message);
                }
                dismissDialog();
            }
        }.start();
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
            gvPhoto.setAdapter(gridAdapter);
            Log.e("----------------", "ddd==" + imagePaths.size());
        } else {
            gridAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_title_right://提交
                upAlert("确认提交異常嗎？", MESSAGE_TOAST);

                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(DZFoodAbUpActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
        }
    }

    private String ids;//获取ID用于上传
    Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showLong(DZFoodAbUpActivity.this, msg.obj.toString());
                    finish();
                    break;
                case MESSAGE_SET_TYPE2:
                    List<String> ty2 = new ArrayList<String>();
                    for (int i = 0; i < type2.size(); i++) {
                        ty2.add(type2.get(i).getName2());
                    }
                    spType2.setAdapter(new ArrayAdapter<String>(DZFoodAbUpActivity.this, android.R.layout.simple_list_item_1, ty2));
                    spType2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            type2.get(position);
                            try {
                                getType3(URLEncoder.encode(URLEncoder.encode(selectType1, "UTF-8"), "UTF-8"), URLEncoder.encode(URLEncoder.encode(type2.get(position).getName2(), "UTF-8"), "UTF-8"), FoxContext.getInstance().getType());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    break;
                case MESSAGE_SET_TYPE3:
                    List<String> ty3 = new ArrayList<String>();
                    for (int i = 0; i < type3.size(); i++) {
                        ty3.add(type3.get(i).getName3());
                    }
                    spType3.setAdapter(new ArrayAdapter<String>(DZFoodAbUpActivity.this, android.R.layout.simple_list_item_1, ty3));
                    spType3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            tvScore.setText(type3.get(position).getScore());
                            ids = type3.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    break;
                case MESSAGE_NOT_NET:
                    ToastUtils.showLong(DZFoodAbUpActivity.this, "網絡問題請退出重試!");
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //提交前檢查
    private void check() {
        final String url = Constants.HTTP_RZ_EXCE_UPLOAD; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();

        object.addProperty("type", FoxContext.getInstance().getType());
        object.addProperty("sc_id", getIntent().getStringExtra("scId"));
        object.addProperty("sc_creator", FoxContext.getInstance().getName());
        object.addProperty("sc_creator_id", FoxContext.getInstance().getLoginId());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", ids);
        jsonObject.addProperty("desp", etDescription.getText().toString());
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
        }
        jsonObject.add("photo", photoArray);
        array.add(jsonObject);
        Log.e("-----object------", object.toString());
        object.add("info", array);
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
                        message.what = MESSAGE_NOT_NET;
                        mHandler.sendMessage(message);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    FileUtil.deletePhotos(DZFoodAbUpActivity.this);
                }
            }
        }.start();
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
                Glide.with(DZFoodAbUpActivity.this)
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

    private void upAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (imagePaths==null||imagePaths.size()==0){
                            ToastUtils.showLong(DZFoodAbUpActivity.this,"请上传菜品照片！");
                        }else if (FoxContext.getInstance().getType().equals("")){
                            ToastUtils.showLong(DZFoodAbUpActivity.this,"请确认登录状态！重新登录！");
                        }else{
                            check();}
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}