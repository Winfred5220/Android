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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CPCBodyListAdapter;
import com.example.administrator.yanfoxconn.adapter.JqtbListAdapter;
import com.example.administrator.yanfoxconn.adapter.ZhiyinshuiCheckAdapter;
import com.example.administrator.yanfoxconn.bean.CPCMessage;
import com.example.administrator.yanfoxconn.bean.SelectModel;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.constant.ImageCaptureManager;
import com.example.administrator.yanfoxconn.intent.PhotoPickerIntent;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.utils.WatermarkUtil;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import taobe.tec.jcc.JChineseConvertor;

/**
 * Created by S1007989 on 2021/4/10.成品倉放行貨物確認界面
 */
public  class CPCReleaseActivity extends BaseActivity implements OnClickListener {
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.sp_gate)
    Spinner spGate;//放行門崗

    @BindView(R.id.tv_no)
    TextView tvNo;//銷單號
    @BindView(R.id.rv_option)
    RecyclerView rvOption;//物品列表

    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private Context mcontext;
    private Bitmap mSignBitmap;
    private String signPath;
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private String ex_no,statue;//銷單號
    private List<CPCMessage> cpcBody;//物品列表
    private CPCBodyListAdapter cpcBodyListAdapter;//物品列表适配器
    private GridAdapter gridAdapter;
    private String url;//地址
    private String gate = "請選擇";//門崗
    private String mac = "";//Mac地址
    private boolean isClicked = true;//判斷是否點擊
    private ArrayList<String> teamList;
    HashMap<Integer, String> isConfirmOkMap;//存放editText值的map
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpc_release);
        ButterKnife.bind(this);

        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //获取当前时间
        String initStartDateTime = formatterUse.format(new Date(System.currentTimeMillis()));
        //加水印
        WatermarkUtil.getInstance().show(this, FoxContext.getInstance().getLoginId()+"\n"+FoxContext.getInstance().getName(),initStartDateTime);

        ex_no = getIntent().getStringExtra("ex_no");
        statue = getIntent().getStringExtra("statue");
        teamList = getIntent().getStringArrayListExtra("teamList");

        tvNo.setText(ex_no);
        tvTitle.setText("出貨確認");
        //btnSign.setOnClickListener(signListener);
        btnBack.setOnClickListener(this);
        if (statue==null){
            btnUp.setOnClickListener(this);
            btnUp.setVisibility(View.VISIBLE);
            btnUp.setText("提交");
        }
        //ivEmpty.setOnClickListener(this);
        mcontext=this.getApplicationContext();
        //門崗下拉列表選擇
        spGate.setAdapter(new ArrayAdapter<String>(CPCReleaseActivity.this, android.R.layout.simple_list_item_1, teamList));
        spGate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                gate=teamList.get(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        getMessage();

//        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
//        cols = cols < 4 ? 4 : cols;
//        gridView.setNumColumns(cols);
//
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position == imagePaths.size()) {
//                    PhotoPickerIntent intent = new PhotoPickerIntent(CPCReleaseActivity.this);
//                    intent.setSelectModel(SelectModel.MULTI);
//                    intent.setShowCarema(true); // 是否显示拍照
//                    intent.setMaxTotal(1); // 最多选择照片数量，默认为1
//                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
//
//                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
//                } else {
//                    PhotoPreviewIntent intent = new PhotoPreviewIntent(CPCReleaseActivity.this);
//                    intent.setCurrentItem(position);
//                    intent.setPhotoPaths(imagePaths);
//                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
//                }
//            }
//        });


    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//
//            switch (requestCode) {
//                // 选择照片
//                case REQUEST_CAMERA_CODE:
//                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
//                    break;
//                // 预览
//                case REQUEST_PREVIEW_CODE:
//                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT));
//                    break;
//                // 调用相机拍照
//                case ImageCaptureManager.REQUEST_TAKE_PHOTO:
//                    if (captureManager.getCurrentPhotoPath() != null) {
//                        captureManager.galleryAddPic();
//
//                        ArrayList<String> paths = new ArrayList<>();
//                        paths.add(captureManager.getCurrentPhotoPath());
//                        loadAdpater(paths);
//                        Log.e("==========", paths.get(1));
//                    }
//                    break;
//
//            }
//        }
//    }


//    private void loadAdpater(ArrayList<String> paths) {
//        if (imagePaths == null) {
//            imagePaths = new ArrayList<>();
//        }
//        imagePaths.clear();
//        imagePaths.addAll(paths);
//
//        if (paths.size() > 0) {
//            ivEmpty.setVisibility(View.GONE);
//        } else {
//            ivEmpty.setVisibility(View.VISIBLE);
//        }
//
//        try {
//            JSONArray obj = new JSONArray(imagePaths);
//            Log.e("--", obj.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (gridAdapter == null) {
//            gridAdapter = new GridAdapter(imagePaths);
//            gridView.setAdapter(gridAdapter);
//            Log.e("----------------", "ddd==" + imagePaths.size());
//        } else {
//            gridAdapter.notifyDataSetChanged();
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:
                if (isClicked){
                    check();
                    isClicked = false;
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            isClicked = true;
                        }
                    }, 2000);
                }else{
                    ToastUtils.showShort(CPCReleaseActivity.this,R.string.multiple_click);
                }
                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(CPCReleaseActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(1); // 最多选择照片数量，默认为1
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
        }
    }

    //獲取銷單貨物信息
    private void getMessage() {

        final String url = Constants.HTTP_CPC_JSON_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        object.addProperty("flag","cpcGetBodyList");
        object.addProperty("ex_no",ex_no);
        object.addProperty("mac",mac);

        Log.e("-----object------",  object.toString());

        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
            return;
        }

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
                    Log.e("----url-----",  url);
                    String result = HttpConnectionUtil.doPostJsonObject(url, object);
                    Gson gson = new Gson();
                    if (result != null) {
                        dismissDialog();
                        Log.e("-----result----", result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String errCode = jsonObject.get("errCode").getAsString();
                        if (errCode.equals("200")) {
                            JsonArray array = jsonObject.get("result").getAsJsonArray();
                            cpcBody = new ArrayList<CPCMessage>();
                            for (JsonElement type : array) {
                                CPCMessage humi = gson.fromJson(type, CPCMessage.class);
                                cpcBody.add(humi);
                            }

                            Message message = new Message();
                            message.what = Constants.MESSAGE_SET_TEXT;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);

                        } else{
                            Message message = new Message();
                            message.what = Constants.MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                        }
                    }else{
                        Message message = new Message();
                        message.what = Constants.MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    //單筆確認前檢查
    private void checkAndConfirm(int position) {

        final String url = Constants.HTTP_CPC_JSON_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        HashMap<Integer, ArrayList<String>> imagePathsMap; //存放圖片地址的map
        HashMap<Integer, String> etMap;//存放editText值的map

//        BDLocation location = FoxContext.getInstance().getLocation();
//        if (location == null || location.getLongitude() == 0.0 || location.getLatitude() == 0.0) {
//            ToastUtils.showLong(this, "位置信息獲取失敗請稍後或移動后重試!");
//        }
        String num = CPCBodyListAdapter.getAdapterMessageMap().get(position).getNum();
        //Log.e("----num---",num);
        String unit = CPCBodyListAdapter.getAdapterMessageMap().get(position).getUnit();
        //Log.e("----unit---",unit);

        imagePathsMap = ZhiyinshuiCheckAdapter.getImagePathsMap();
        etMap = ZhiyinshuiCheckAdapter.getEtMap();

        //JsonArray array = new JsonArray();
        object.addProperty("flag","cpcConfirm");
        object.addProperty("ex_no",ex_no);
        object.addProperty("ex_lh",cpcBody.get(position).getEx_lh());
        object.addProperty("ex_item",cpcBody.get(position).getEx_item());
        object.addProperty("ex_release_count", num);
        object.addProperty("ex_release_unit", unit);
        object.addProperty("ex_release_door", gate);
        object.addProperty("ex_releaser", FoxContext.getInstance().getName());

        //object.add("info", array);

        Log.e("-----object------",  object.toString());

        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
            return;
        }
        if (gate.equals("請選擇")) {
            ToastUtils.showShort(this, "請選擇門崗");
            return;
        }
        if (num.equals("0")) {
            ToastUtils.showShort(this, "放行數量不正確");
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
                            message.what = Constants.MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);

                        } else{
                            Message message = new Message();
                            message.what = Constants.MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                        }
                    }else{
                        Message message = new Message();
                        message.what = Constants.MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    FileUtil.deletePhotos(CPCReleaseActivity.this);
                }
            }
        }.start();
    }
    //最終確認前檢查
    private void check() {

        final String url = Constants.HTTP_CPC_JSON_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();

        isConfirmOkMap = CPCBodyListAdapter.getIsConfirmOkMap();
        //遍历map集合
        for (int key : isConfirmOkMap.keySet()) {
            Log.e("-----------", "isConfirmOkMap.get(key)===" + isConfirmOkMap.get(key));
            //判断是否已选择，如果已选择，则添加进selectList
            if (isConfirmOkMap.get(key).equals("N")) {
                ToastUtils.showShort(this, "未全部確認OK");
                return;
            }
        }
        object.addProperty("flag","cpcConfirmAll");
        object.addProperty("ex_no",ex_no);
        object.addProperty("ex_release_door", gate);
        object.addProperty("ex_releaser", FoxContext.getInstance().getName());

        Log.e("-----object------",  object.toString());

        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
            return;
        }
        if (gate.equals("請選擇")) {
            ToastUtils.showShort(this, "請選擇門崗");
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
                            message.what = Constants.MESSAGE_UP;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);

                        } else{
                            Message message = new Message();
                            message.what = Constants.MESSAGE_UP;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                        }
                    }else{
                        Message message = new Message();
                        message.what = Constants.MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    FileUtil.deletePhotos(CPCReleaseActivity.this);
                }
            }
        }.start();
    }
    //NG提交
    private void NGUpload(String text,String ex_lh,String ex_item) {

        final String url = Constants.HTTP_CPC_JSON_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        object.addProperty("flag","cpcNGUP");
        object.addProperty("REC_SN",ex_no);
        object.addProperty("REC_MARK", gate);
        object.addProperty("REC_NAME", FoxContext.getInstance().getName());
        object.addProperty("REC_STATUE", ex_lh);
        object.addProperty("REC_REMARK", text);
        object.addProperty("REC_ITEM", ex_item);
        Log.e("-----object------",  object.toString());

        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
            return;
        }
        if (gate.equals("請選擇")) {
            ToastUtils.showShort(this, "請選擇門崗");
            return;
        }
        if (text.replaceAll(" ","").equals("")) {
            ToastUtils.showShort(this, "請輸入NG描述");
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
                            message.what = Constants.MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);

                        } else{
                            Message message = new Message();
                            message.what = Constants.MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                        }
                    }else{
                        Message message = new Message();
                        message.what = Constants.MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    FileUtil.deletePhotos(CPCReleaseActivity.this);
                }
            }
        }.start();
    }
    //取消NG
    private void NGDelete(String ex_lh,String ex_item) {

        final String url = Constants.HTTP_CPC_JSON_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        object.addProperty("flag","cpcNGDelete");
        object.addProperty("REC_SN",ex_no);
        object.addProperty("REC_STATUE", ex_lh);
        object.addProperty("REC_ITEM", ex_item);
        Log.e("-----object------",  object.toString());

        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
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
                            message.what = Constants.MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);

                        } else{
                            Message message = new Message();
                            message.what = Constants.MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                        }
                    }else{
                        Message message = new Message();
                        message.what = Constants.MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    FileUtil.deletePhotos(CPCReleaseActivity.this);
                }
            }
        }.start();
    }
    //撤回確認
    private void recall(String ex_lh,String ex_item) {

        final String url = Constants.HTTP_CPC_JSON_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        object.addProperty("flag","cpcRecall");
        object.addProperty("ex_no",ex_no);
        object.addProperty("ex_lh", ex_lh);
        object.addProperty("ex_item", ex_item);
        Log.e("-----object------",  object.toString());

        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
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
                            message.what = Constants.MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);

                        } else{
                            Message message = new Message();
                            message.what = Constants.MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                        }
                    }else{
                        Message message = new Message();
                        message.what = Constants.MESSAGE_NETMISTAKE;
                        mHandler.sendMessage(message);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    FileUtil.deletePhotos(CPCReleaseActivity.this);
                }
            }
        }.start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case Constants.MESSAGE_TOAST://Toast彈出
                    aboutAlert(msg.obj.toString(),Constants.MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case Constants.MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(CPCReleaseActivity.this,R.string.net_mistake);
                    break;
                case Constants.MESSAGE_SET_TEXT://text賦值
                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case Constants.MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),Constants.MESSAGE_UP);
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
        //貨物列表賦值
        LinearLayoutManager layoutManager = new LinearLayoutManager(CPCReleaseActivity.this);
        rvOption.setLayoutManager(layoutManager);
        rvOption.setItemViewCacheSize(500);
        cpcBodyListAdapter = new CPCBodyListAdapter(mcontext, cpcBody);
        //列表設置長按菜單
        cpcBodyListAdapter.setOnItemClickListener(new CPCBodyListAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(View view, int pos) {
                //View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(CPCReleaseActivity.this, view);
                Menu menu = popupMenu.getMenu();
                // 通过代码添加菜单项
                menu.add(Menu.NONE, Menu.FIRST + 0, 1, "撤回");
                menu.add(Menu.NONE, Menu.FIRST + 1, 1, "NG");
                menu.add(Menu.NONE, Menu.FIRST + 2, 1, "取消NG");
                // 通过XML文件添加菜单项,menu布局
                //popupMenu.getMenuInflater().inflate(R.menu.menu_cpc, menu);
                /**
                 * popmenu的监听
                 */
                PopupMenu.OnMenuItemClickListener menuItem = new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        isConfirmOkMap = CPCBodyListAdapter.getIsConfirmOkMap();
                        if (isConfirmOkMap.get(pos).equals("Y")){
                            ToastUtils.showShort(CPCReleaseActivity.this, "已確認OK,不可操作");
                            return false;
                        }
                        switch (item.getItemId()) {
                            case Menu.FIRST + 0:
                                recall(cpcBody.get(pos).getEx_lh(),cpcBody.get(pos).getEx_item());
                                break;
                            case Menu.FIRST + 1:
                                inputAlert(cpcBody.get(pos).getEx_lh(),cpcBody.get(pos).getEx_item());
                                break;
                            case Menu.FIRST + 2:
                                NGDelete(cpcBody.get(pos).getEx_lh(),cpcBody.get(pos).getEx_item());
                                break;
                        }
                        return false;
                    }
                };
                popupMenu.setOnMenuItemClickListener(menuItem);
                popupMenu.show();

            }
        });
        //單條信息確認
        cpcBodyListAdapter.setOnConfirmClickListener(new CPCBodyListAdapter.OnConfirmClickListener() {
            @Override
            public void onConfirmclick(int position) {
                if (isClicked){
                    checkAndConfirm(position);
                    isClicked = false;
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            isClicked = true;
                        }
                    }, 2000);
                }else{
                    ToastUtils.showShort(CPCReleaseActivity.this,R.string.multiple_click);
                }

            }
        });
        rvOption.setAdapter(cpcBodyListAdapter);

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
                Glide.with(CPCReleaseActivity.this)
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

//    private OnClickListener signListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            WritePadDialog writeTabletDialog = new WritePadDialog(CPCReleaseActivity.this, new DialogListener() {
//                @Override
//                public void refreshActivity(Object object) {
//
//                    mSignBitmap = (Bitmap) object;
//                    signPath = createFile();
//                    ArrayList<String> arrayList = new ArrayList<String>();
//                    arrayList.add(signPath);
//                    loadAdpater(arrayList);
////                    ivSign.setImageBitmap(mSignBitmap);
//
//                }
//            });
//            writeTabletDialog.show();
//        }
//    };
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
                CPCReleaseActivity.this.sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦

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
                        if (type==Constants.MESSAGE_TOAST){
                           // finish();
                            dismissDialog();
                            getMessage();
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
                        if (type==Constants.MESSAGE_UP){
                           finish();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void inputAlert(String ex_lh,String ex_item) {
        final EditText inputServer = new EditText(CPCReleaseActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(CPCReleaseActivity.this);
         builder.setTitle("請输入NG描述").setView(inputServer)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
               .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        String text = inputServer.getText().toString();
                        NGUpload(text,ex_lh,ex_item);
                    }
                });
        builder.show();
    }



}
