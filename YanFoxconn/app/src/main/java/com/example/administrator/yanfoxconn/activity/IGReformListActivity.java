package com.example.administrator.yanfoxconn.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.AbnormalListAdapter;
import com.example.administrator.yanfoxconn.adapter.IGCheckLvAdapter;
import com.example.administrator.yanfoxconn.adapter.MyGridViewAdapter;
import com.example.administrator.yanfoxconn.bean.IGMessage;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 宿舍寄存 異常整改列表界面
 * @Author song
 * @Date 4/24/21 11:52 AM
 */
public class IGReformListActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_UPLOAD = 1;
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_SPINNER = 6;//spinner賦值

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
        @BindView(R.id.sp_zg)
        Spinner spZG;//整改選項
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    MyGridView gridView;//图片显示区域
    private GridAdapter gridAdapter;
    private List<String> mZG;//選項
    private List<IGMessage> spList;
    private String shCode;
    private IGMessage igMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ig_reform_list);
        ButterKnife.bind(this);

        tvTitle.setText("異常整改");
        btnBack.setText("返回");
        btnUp.setText(R.string.up_abnormal);
        btnUp.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);

        shCode = getIntent().getStringExtra("sh_code");
        igMessage = (IGMessage) getIntent().getSerializableExtra("igMessage");

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(IGReformListActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(IGReformListActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });

        getOptionInfo("ZG");//獲取下拉列表信息
    }

    /**
     * 獲取下拉內容
     * @param flag 儲位狀態ZY，KZ，YC
     */
    private void getOptionInfo(String flag){

        final String url = Constants.HTTP_OPTION_INFO + "?flag=" + flag;

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
                        spList = new ArrayList<IGMessage>();

                        for (JsonElement type : array) {
                            IGMessage humi = gson.fromJson(type, IGMessage.class);
                            spList.add(humi);

                        }
                        Message message = new Message();
                        message.what = MESSAGE_SET_SPINNER;
                        mHandler.sendMessage(message);

                    } else {
//                            Log.e("-----------", "result==" + result);
//                            Message message = new Message();
//                            message.what = MESSAGE_TOAST;
//                            message.obj = jsonObject.get("errMessage").getAsString();
//                            mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(IGReformListActivity.this, R.string.net_mistake);
                    break;
                case MESSAGE_TOAST:
                    ToastUtils.showShort(IGReformListActivity.this, msg.obj.toString());
                    finish();
                    break;
                case MESSAGE_SET_SPINNER:
                    setSpinner();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private String str,strFlag;


    public void setSpinner() {
        mZG = new ArrayList<>();
        mZG.add("請選擇倉庫");
        for (int i = 0; i < spList.size(); i++) {
            mZG.add(spList.get(i).getNAME());
        }
        spZG.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mZG));
        spZG.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str = mZG.get(position);
                strFlag = spList.get(position).getSTATUS();
                if (str.equals("請選擇原因")) {

                } else {

                }
//                Log.e("---------", "最喜欢的水果是：" + str);
//                paramMap.put("kedui",str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(IGReformListActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_title_right://提交
                Log.e("-------", "btn_title_right");
                check();

                break;
        }
    }

    public void check() {

        if (str.equals("")) {
            ToastUtils.showShort(this, "請選擇查驗類型");
        } else if (str.contains("其他") && etDescription.getText().toString().equals("")) {
            ToastUtils.showShort(this, "請填寫文字描述");
        } else if (imagePaths == null) {
                    ToastUtils.showLong(IGReformListActivity.this, "請上傳照片!");

        }else{
            normal2new();
        }
    }

    public void normal2new() {
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        final String url = Constants.HTTP_INVENTORY_EXCE_ZG; //此處寫上自己的URL
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();

        object.addProperty("sh_code", shCode);
        object.addProperty("si_creator_id", FoxContext.getInstance().getLoginId());
        object.addProperty("si_creator", FoxContext.getInstance().getName());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sl_code", igMessage.getSL_CODE());
        jsonObject.addProperty("si_goods", igMessage.getREMARK());
        jsonObject.addProperty("si_error", str);
        jsonObject.addProperty("status", strFlag);
        jsonObject.addProperty("si_remark", etDescription.getText().toString());
        jsonObject.addProperty("si_id_old", igMessage.getID());
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
//                jsonObject1.addProperty("file", picBase64Code);
                jsonObject1.addProperty("file", sign_dir);
                photoArray.add(jsonObject1);
            }
        }
        object.add("photo", photoArray);
array.add(jsonObject);
        object.add("body", array);
        Log.e("----------","object="+object.toString());
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
                    FileUtil.deletePhotos(IGReformListActivity.this);
                }
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
                Glide.with(IGReformListActivity.this)
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