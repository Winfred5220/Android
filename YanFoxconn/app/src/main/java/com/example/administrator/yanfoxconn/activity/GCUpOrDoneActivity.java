package com.example.administrator.yanfoxconn.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.GCHealthAdapter;
import com.example.administrator.yanfoxconn.bean.ComScanViewMessage;
import com.example.administrator.yanfoxconn.bean.GCBody;
import com.example.administrator.yanfoxconn.bean.GCHead;
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
import com.example.administrator.yanfoxconn.widget.SwipeListView;
import com.example.administrator.yanfoxconn.widget.SwipeListViewOne;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 安保部 健康追蹤 追蹤或結案共用介界面
 * Created by song on 2017/9/7.
 */

public class GCUpOrDoneActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private final int MESSAGE_TOAST = 2;//showToast
    private final int MESSAGE_SET_LIST = 0;//獲取狀態后,設置控件內容
    private final int MESSAGE_DELETE_SUCCESS = 1;//刪除成功，刷新列表
    private final int MESSAGE_NOT_NET = 3;//網絡問題

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.tv_id)
    TextView tvId;//工號
    @BindView(R.id.tv_name)
    TextView tvName;//姓名
    @BindView(R.id.tv_identity)
    TextView tvIdentity;//身份證
    @BindView(R.id.tv_pro)
    TextView tvPro;//產品處
    @BindView(R.id.et_temp)
    EditText etTemp;//體溫
    @BindView(R.id.et_description)
    EditText etDescription;//描述
    @BindView(R.id.tv_description)
    TextView tvDescription;//描述
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    MyGridView gvPhoto;//图片显示区域
    @BindView(R.id.lv_people)
    SwipeListViewOne lvPeople;
    @BindView(R.id.rg_done)
    RadioGroup rgDone;//結案顯示
    @BindView(R.id.rtb_yes)
    RadioButton rtbYes;//正常結案
    @BindView(R.id.rtb_no)
    RadioButton rtbNo;//異常結案

    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;

    private GCHead gcHeads ;
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private GridAdapter gridAdapter;
    private GCHealthAdapter healthAdapter;
    private List<GCBody> bodys;

    private String url;
    private String result;
    private String from;//add追蹤，，end結案
    private String done="Y";//結案
    private int num = 0;//輸入框初始值
    private int mMaxNum =250;//輸入框最大值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gc_up_or_done);
        ButterKnife.bind(this);


        btnBack.setText("返回");
        btnUp.setText("提交");
        btnBack.setOnClickListener(this);
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        rgDone.setOnCheckedChangeListener(this);
        rtbYes.setOnClickListener(this::onClick);
        rtbNo.setOnClickListener(this::onClick);
        rtbYes.setChecked(true);

        from = getIntent().getStringExtra("from");
        gcHeads = (GCHead) getIntent().getSerializableExtra("people");
        tvId.setText(gcHeads.getIn_Number());
        tvName.setText(gcHeads.getIn_Name());
        tvIdentity.setText(gcHeads.getIn_Sex());
        tvPro.setText(gcHeads.getIn_Department());
        ivEmpty.setOnClickListener(this);

        if (from.equals("end")){
            tvTitle.setText("結案上傳");
            ivEmpty.setVisibility(View.VISIBLE);
            lvPeople.setVisibility(View.GONE);
            rgDone.setVisibility(View.VISIBLE);
        }else{
            tvTitle.setText("追蹤上傳");
            ivEmpty.setVisibility(View.GONE);
            lvPeople.setVisibility(View.VISIBLE);
            getBodys(gcHeads.getIn_Random_Id());
        }

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gvPhoto.setNumColumns(cols);

        // preview
        gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(GCUpOrDoneActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(GCUpOrDoneActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });
        etTemp.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!etTemp.getText().toString().equals("")){
                if (Float.valueOf(etTemp.getText().toString())>42){
                    ToastUtils.showShort(GCUpOrDoneActivity.this,"請注意溫度填寫！");
                }}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etDescription.addTextChangedListener(new TextWatcher() {
            //紀錄存入的字數
            private CharSequence wordNum;
            private int selectionStart;
            private int selectionEnd;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //實時紀錄輸入的字數
                wordNum = charSequence;


            }

            @Override
            public void afterTextChanged(Editable editable) {
                int number = num + editable.length();
                //TextView 顯示剩餘字數
                tvDescription.setText(""+number+"/250");
                selectionStart = etDescription.getSelectionStart();
                selectionEnd = etDescription.getSelectionEnd();
                //判斷大於最大值
                if (wordNum.length()>mMaxNum){
                    //刪除多餘輸入的字（不會顯示出來）
                    editable.delete(selectionStart-1,selectionEnd);
                    int tempSelection = selectionEnd;
                    etDescription.setText(editable);
                    etDescription.setSelection(tempSelection);//設置光標在最後
                    ToastUtils.showLong(GCUpOrDoneActivity.this,"最多輸入250字！ ");
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
                if (etTemp.getText().toString().equals("")||etDescription.getText().toString().equals("")){

                    ToastUtils.showShort(GCUpOrDoneActivity.this,"請注意體溫與追蹤紀錄的填寫！");
                }else if(etTemp.getText().toString().equals("")||etDescription.getText().toString().equals("")){

                    upAlert("請注意體溫與追蹤紀錄的填寫,确认提交嗎？", MESSAGE_TOAST);
                }else{
                    upAlert("确认提交嗎？", MESSAGE_TOAST);}
                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(GCUpOrDoneActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
        }
    }

    Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showLong(GCUpOrDoneActivity.this, msg.obj.toString());
                    finish();
                    break;
                case MESSAGE_SET_LIST://List賦值
                    healthAdapter = new GCHealthAdapter(GCUpOrDoneActivity.this,bodys);
                    lvPeople.setAdapter(healthAdapter);
                    healthAdapter.setOnClickListenerSeeOrAdd(new GCHealthAdapter.OnClickListenerSeeOrAdd() {
                        @Override
                        public void OnClickListenerDel(int position) {
                            delAlert("確認是否刪除！",position);

                        }
                    });
                    break;

                case MESSAGE_NOT_NET:
                    ToastUtils.showLong(GCUpOrDoneActivity.this, "網絡問題請退出重試!");
                    finish();
                    break;
                case MESSAGE_DELETE_SUCCESS:
                    getBodys(gcHeads.getIn_Random_Id());
                    break;
            }
            super.handleMessage(msg);
        }
    };


    //查看單身
    private void getBodys(String randomId){

        showDialog();
        final String url = Constants.HTTP_BODY_DELETE_SELECT+"?In_Random_Id="+randomId;

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForGet(url);
                Log.e("---------", "result==fff===" + result);
                dismissDialog();
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("result1").getAsJsonArray();
                        bodys = new ArrayList<GCBody>();

                        for (JsonElement type : array) {
                            GCBody humi = gson.fromJson(type, GCBody.class);
                            bodys.add(humi);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_LIST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else{
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            } }.start();
    }

    //增加追蹤
    private void add() {
        final String url = Constants.HTTP_HEALTH_TRACE_COMMIT; //此處寫上自己的URL

        JsonObject object = new JsonObject();

        object.addProperty("In_Random_Id", gcHeads.getIn_Random_Id());
        object.addProperty("In_Category", gcHeads.getIn_Category());
        object.addProperty("In_Name", gcHeads.getIn_Name());
        object.addProperty("In_Department", gcHeads.getIn_Department());
        object.addProperty("T_Description",etDescription.getText().toString());
        object.addProperty("T_Tempature", etTemp.getText().toString());
        object.addProperty("T_Createor", FoxContext.getInstance().getName());
        object.addProperty("T_Createor_id" +
                "", FoxContext.getInstance().getLoginId());

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
                    FileUtil.deletePhotos(GCUpOrDoneActivity.this);
                }
            }
        }.start();
    }

    //結案
    private void end() {
        final String url = Constants.HTTP_CASE_FISH; //此處寫上自己的URL

        JsonObject object = new JsonObject();

        object.addProperty("In_Random_Id", gcHeads.getIn_Random_Id());
        object.addProperty("In_Category", gcHeads.getIn_Category());
        object.addProperty("In_Name", gcHeads.getIn_Name());
        object.addProperty("In_Department", gcHeads.getIn_Department());
        object.addProperty("In_C_description", etDescription.getText().toString());
        object.addProperty("In_C_result", done);

        object.addProperty("In_C_Name", FoxContext.getInstance().getName());
        object.addProperty("In_C_Number", FoxContext.getInstance().getLoginId());

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
        object.add("In_C_photo", photoArray);
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
                    FileUtil.deletePhotos(GCUpOrDoneActivity.this);
                }
            }
        }.start();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rtb_yes:
                done = "Y";
                break;
            case R.id.rtb_no:
                done = "N";
                break;
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
                Glide.with(GCUpOrDoneActivity.this)
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

                            if (from.equals("end")){
                                 if (imagePaths==null||imagePaths.size()==0){
                                    ToastUtils.showLong(GCUpOrDoneActivity.this,"请上传异常照片！");
                                }else {
                                end();}
                            }else{

                                add();
                            }
                            }

                })
        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void del(String tId){
        showDialog();
        final String url = Constants.HTTP_BODY_DELETE+"?T_Id="+tId;

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForGet(url);

                dismissDialog();
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
//                        JsonArray array = jsonObject.get("result").getAsJsonArray();


                        Message message = new Message();
                        message.what = MESSAGE_DELETE_SUCCESS;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else if(errCode.equals("500")){
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = "體溫數據異常，無法結案，請聯繫相關負責人！";
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
    private void delAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        del(bodys.get(type).getT_Id());
                    }

                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
