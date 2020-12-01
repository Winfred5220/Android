package com.example.administrator.yanfoxconn.activity;


import android.app.AlertDialog;
import android.content.Context;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.HubListAdapter;
import com.example.administrator.yanfoxconn.bean.HubList;
import com.example.administrator.yanfoxconn.bean.SelectModel;
import com.example.administrator.yanfoxconn.constant.Constants;
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
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangqian on 2019/2/15.Hub倉領取界面
 */
public  class HubGoodsActivity extends BaseActivity implements OnClickListener {

    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_SHOW = 4;//顯示提醒
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤

    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private Context mcontext;
    private Bitmap mSignBitmap;
    private String signPath;
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private String area;//區域
    private String applyer_add;//部門
    private String applyer ;//姓名
    private String applyer_tel;//電話
    private List<HubList> hubList;//物品列表
    private HubListAdapter hubListAdapter;//物品列表适配器
    private GridAdapter gridAdapter;
    private String url;//地址
    private String result;//网络获取结果
    //用户存储条目的选择状态
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();
    //用于存放已选择的条目
    private List<Integer> selectList = new ArrayList<>();

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.lv_goods)
    MyListView lvGoods;//物品列表
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    MyGridView gridView;//图片显示区域
    @BindView(R.id.btn_sign)
    Button btnSign;//簽名按鈕

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_goods);
        ButterKnife.bind(this);

        area = getIntent().getStringExtra("area");
        applyer_add = getIntent().getStringExtra("dep");
        applyer = getIntent().getStringExtra("name");
        applyer_tel = getIntent().getStringExtra("phone").replaceAll("\\+","%2B").replaceAll(" ","%20");

        Log.e("-----------",  area + "," + applyer_add + "," + applyer + "," + applyer_tel);

        tvTitle.setText("領取物品");
        btnSign.setOnClickListener(signListener);
        btnBack.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        mcontext=this.getApplicationContext();

        getMessage(area,applyer_add,applyer,applyer_tel);

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(HubGoodsActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(1); // 最多选择照片数量，默认为1
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(HubGoodsActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
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
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:
                check();
                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(HubGoodsActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(1); // 最多选择照片数量，默认为1
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
        }
    }


    private void getMessage(String area, String dep,String name,String phone) {
        showDialog();

        try {
            String area1 =  URLEncoder.encode(URLEncoder.encode(area.toString(), "UTF-8"), "UTF-8");
            String dep1  =  URLEncoder.encode(URLEncoder.encode(dep.toString(), "UTF-8"), "UTF-8");
            String name1 =  URLEncoder.encode(URLEncoder.encode(name.toString(), "UTF-8"), "UTF-8");

            url = Constants.HTTP_HUB_LOGIN_SERVLET + "?area=" + area1 + "&applyer_add=" + dep1 + "&applyer=" + name1 + "&applyer_tel=" + phone;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("-----------", "url-----" + url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);

                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        String ss = jsonObject.get("data").toString();
                        hubList = new ArrayList<HubList>();
                        try {
                            JSONArray array = new JSONArray(ss);
                            for(int i=0;i<array.length();i++){
                                JSONObject goods = array.getJSONObject(i);// 遍历 jsonarray 数组，把每一个对象转成 json 对象
                                HubList hub = new HubList();
                                hub.setORDER_NO(goods.getString("ORDER_NO"));
                                hub.setMATERIAL_CODE(goods.getString("MATERIAL_CODE"));
                                hub.setMATERIAL_NAME(goods.getString("MATERIAL_NAME"));
                                hub.setMATERIAL_SPEC(goods.getString("MATERIAL_SPEC"));
                                hub.setSY(goods.getString("SY"));
                                hub.setAPPLY_COUNT(goods.getString("SY"));
                                hubList.add(hub);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                        dismissDialog();

                    } else{
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                } else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                }
            } }.start();
    }

    //提交前檢查
    private void check(){

        final String url = Constants.HTTP_HUB_UP_SERVLET; //此處寫上自己的URL
        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡
        final Map<String, File> fileMap = new HashMap<String, File>(); //檔全部添加到Map裡

        //清除之前选择的数据
        selectList.clear();
        //遍历map集合
        for (int key : isSelected.keySet()) {
            //判断是否已选择，如果已选择，则添加进selectList
            if(isSelected.get(key)){
                selectList.add(key);
            }
        }
        Log.e("----------","selectList"+ selectList.toString());

        ArrayList arrayList = new ArrayList();
        for (int i=0;i<selectList.size();i++){
            int j = selectList.get(i);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("order_no", hubList.get(j).getORDER_NO());
            jsonObject.addProperty("receive_count", hubList.get(j).getAPPLY_COUNT());
            jsonObject.addProperty("material_code", hubList.get(j).getMATERIAL_CODE());
            arrayList.add(jsonObject);
        }

        Log.e("-----------", "arrayList-----" + arrayList.toString());

        paramMap.put("updateInfo", arrayList.toString() );

        if (selectList.size()==0) {
            ToastUtils.showShort(this, "請選擇條目");
        }  else if (imagePaths==null) {
            ToastUtils.showShort(this, "請拍照選擇圖片或手寫簽名");
        } else {
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
                            if (b.getResponseCode() == 200) {
                                Message message = new Message();
                                message.what = MESSAGE_TOAST;
                                message.obj = b.getResponseMessage();
                                mHandler.sendMessage(message);
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {//刪除簽名文件
                                    FileUtil.deletePhotos(HubGoodsActivity.this);
                                    finish();
                                    }
                                }, 1000);

                            } else {
                                Message message = new Message();
                                message.what = MESSAGE_TOAST;
                                message.obj = b.getResponseMessage();
                                mHandler.sendMessage(message);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
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
                    ToastUtils.showLong(HubGoodsActivity.this,R.string.net_mistake);
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
        if(hubList!=null){
        hubListAdapter = new HubListAdapter(mcontext,isSelected, hubList);
        lvGoods.setAdapter(hubListAdapter);
        }else {
            ToastUtils.showShort(HubGoodsActivity.this, "沒有數據!");
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
                Glide.with(HubGoodsActivity.this)
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

    private OnClickListener signListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            WritePadDialog writeTabletDialog = new WritePadDialog(HubGoodsActivity.this, new DialogListener() {
                @Override
                public void refreshActivity(Object object) {

                    mSignBitmap = (Bitmap) object;
                    signPath = createFile();
                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList.add(signPath);
                    loadAdpater(arrayList);
//                    ivSign.setImageBitmap(mSignBitmap);

                }
            });
            writeTabletDialog.show();
        }
    };
    /**
     * 随机生产文件名
     *
     * @return
     */
//    private static String generateFileName() {
//        return UUID.randomUUID().toString();
//    }


    /**
     * 创建手写签名文件
     *
     * @return
     */
    private String createFile() {
        ByteArrayOutputStream baos = null;
        String _path = null;

        try {
            String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
            _path =  sign_dir + File.separator  + System.currentTimeMillis() + ".jpg";
            Log.e("------------", _path);
            baos = new ByteArrayOutputStream();
            mSignBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

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
                HubGoodsActivity.this.sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦

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

}
