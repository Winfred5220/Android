package com.example.administrator.yanfoxconn.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.AbnormalListAdapter;
import com.example.administrator.yanfoxconn.adapter.IGCheckLvAdapter;
import com.example.administrator.yanfoxconn.adapter.ZhiyinshuiCheckAdapter;
import com.example.administrator.yanfoxconn.bean.IGMessage;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiCheckMsg;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.constant.ImageCaptureManager;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description 宿舍寄存 倉庫盤點 列表界面
 * @Author song
 * @Date 4/24/21 11:52 AM
 */
public class IGCheckListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_GET_OPTION = 0;//獲取下拉菜單
    private final int MESSAGE_GET_WRONG=6;//獲取異常信息列表

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.tv_du)
    TextView tvDu;//用於異常列表顯示時，不顯示操作
    @BindView(R.id.rv_option)
    RecyclerView rvOption;//點檢項目列表
    @BindView(R.id.lv_wrong)
    MyListView lvWrong;

    private IGCheckLvAdapter comAbAdapter;//點檢列表適配器
    private HashMap<Integer, String> isSelected = new HashMap<>();//用户存储条目的选择状态
    private List<Integer> selectList;//用于存放無異常的条目
    private List<Integer> noselectList;//用于存放有異常的条目
    private int key1, key2,key3 = 0;//提交時管控未填寫信息
    private String url;//請求地址
    private String result;//請求返回結果
    private String type;//類型
    private List<IGMessage> mCheckMsgList,spList,wrongList;
    private String sh_code,flag;
    private AbnormalListAdapter wrongAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ig_check_list);

        ButterKnife.bind(this);
        tvTitle.setText("巡檢界面");
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        lvWrong.setOnItemClickListener(this);

        sh_code = getIntent().getStringExtra("sh_code");
        flag = getIntent().getStringExtra("flag");
        if (flag.equals("YC")){
            getLocationYCInfo(sh_code,flag);
        }else{
        getLocationInfo(sh_code,flag);}
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
        case R.id.btn_title_left:
        finish();
        break;
            case R.id.btn_title_right:
                check();
                break;
        }
    }

    /**
     * 獲取儲位信息
     * @param sh_code 倉庫名稱
     * @param flag    儲位狀態ZY，KZ，YC
     */
    private void getLocationInfo(String sh_code,String flag){
            showDialog();

            final String url = Constants.HTTP_INVENTORY_LOCATION_INFO + "?sh_code=" + sh_code + "&flag=" + flag;

            new Thread() {
                @Override
                public void run() {
                    //把网络访问的代码放在这里
                    String result = HttpUtils.queryStringForPost(url);

                    Log.e("---------", "==fff===" + url);
                    Gson gson = new Gson();
                    if (result != null) {
                        Log.e("---------", "result==fff===" + result);

                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String errCode = jsonObject.get("errCode").getAsString();
                        if (errCode.equals("200")) {
                            Log.e("--fff---------", "result==" + result);
                            JsonArray array = jsonObject.get("data").getAsJsonArray();
                            mCheckMsgList = new ArrayList<IGMessage>();

                            for (JsonElement type : array) {
                                IGMessage humi = gson.fromJson(type, IGMessage.class);
                                mCheckMsgList.add(humi);

                            }
                            Message message = new Message();
                            message.what = MESSAGE_GET_OPTION;
                            mHandler.sendMessage(message);

                        } else {
                            Log.e("-----------", "result==" + result);
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                        }
                    }
                }
            }.start();
        }

    /**
     * 獲取異常儲位信息
     * @param sh_code 倉庫名稱
     * @param flag    儲位狀態ZY，KZ，YC
     */
    private void getLocationYCInfo(String sh_code,String flag){
        showDialog();

        final String url = Constants.HTTP_INVENTORY_LOCATION_INFO + "?sh_code=" + sh_code + "&flag=" + flag;

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
dismissDialog();
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        wrongList = new ArrayList<IGMessage>();

                        for (JsonElement type : array) {
                            IGMessage humi = gson.fromJson(type, IGMessage.class);
                            wrongList.add(humi);

                        }
                        Message message = new Message();
                        message.what = MESSAGE_GET_WRONG;
                        mHandler.sendMessage(message);

                    } else {
                            Log.e("-----------", "result==" + result);
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
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
                        message.what = MESSAGE_SET_TEXT;
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
                    ToastUtils.showLong(IGCheckListActivity.this, R.string.net_mistake);
                    break;
                case MESSAGE_TOAST:
                    ToastUtils.showShort(IGCheckListActivity.this, msg.obj.toString());
                    finish();
                    break;
                case MESSAGE_GET_OPTION:
                    getOptionInfo("YC");
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    LinearLayoutManager layoutManager = new LinearLayoutManager(IGCheckListActivity.this);
                    rvOption.setLayoutManager(layoutManager);
                    rvOption.setItemViewCacheSize(500);
                   comAbAdapter = new IGCheckLvAdapter(IGCheckListActivity.this,mCheckMsgList,spList,isSelected);
                   rvOption.setAdapter(comAbAdapter);
                    break;
                case MESSAGE_GET_WRONG:
                    tvDu.setVisibility(View.GONE);
                    rvOption.setVisibility(View.GONE);
                    lvWrong.setVisibility(View.VISIBLE);
                    wrongAdapter = new AbnormalListAdapter(IGCheckListActivity.this, wrongList,"IG");
                    lvWrong.setAdapter(wrongAdapter);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private ImageCaptureManager captureManager; // 相机拍照处理类

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                // 选择照片
                case REQUEST_CAMERA_CODE:

                        Log.e("==========", "Picker===" + comAbAdapter.getPosition());
                        comAbAdapter.loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT), comAbAdapter.getPosition());

                    break;
                // 预览
                case REQUEST_PREVIEW_CODE:

                        comAbAdapter.loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT), comAbAdapter.getPosition());
                        Log.e("==========", "Preview===" + comAbAdapter.getPosition());

                    break;
                // 调用相机拍照
                case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                    if (captureManager.getCurrentPhotoPath() != null) {
                        captureManager.galleryAddPic();
                        ArrayList<String> paths = new ArrayList<>();
                        paths.add(captureManager.getCurrentPhotoPath());

                            comAbAdapter.loadAdpater(paths, comAbAdapter.getPosition());
                            Log.e("==========", paths.get(1));}

                    break;
            }
        }
    }

    //提交前檢查
    private void check() {

        url = Constants.HTTP_INVENTORY_OK; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        HashMap<Integer, ArrayList<String>> imagePathsMap; //存放圖片地址的map
        HashMap<Integer, String> etMap;//存放editText值的map
        HashMap<Integer, String> etCheckMap;//存放editText值的map
        HashMap<Integer,String> etCheckFlagMap;//flag

        imagePathsMap = IGCheckLvAdapter.getImagePathsMap();
        etMap = IGCheckLvAdapter.getEtMap();
        etCheckFlagMap = IGCheckLvAdapter.getEtCheckFlagMap();
        etCheckMap = IGCheckLvAdapter.getEtCheckMap();
        JsonArray array = new JsonArray();


            object.addProperty("sh_code", getIntent().getStringExtra("sh_code"));
       object.addProperty("si_creator", FoxContext.getInstance().getName());
        object.addProperty("si_creator_id", FoxContext.getInstance().getLoginId());

        selectList = new ArrayList<>();
        noselectList = new ArrayList<>();
        //清除之前选择的数据
        selectList.clear();//選項true
        noselectList.clear();//選項false
        //遍历map集合
        for (int key : isSelected.keySet()) {
            //Log.e("-----------", "isSelected.get(key)===" + isSelected.get(key));
            //判断是否已选择，如果已选择，则添加进selectList
            if (isSelected.get(key).equals("wrong")) {
                selectList.add(key);
            }
//            else if(isSelected.get(key).equals("false")){
//                noselectList.add(key);
//            } else  if (isSelected.get(key).equals("select_true")){
//                selectTureList.add(key);
//            } else if (isSelected.get(key).equals("select_false")){
//                selectFalseList.add(key);
//            }

        }

//        Log.e("----------", "selectList" + selectList.toString());
//        Log.e("----------", "noselectList" + noselectList.toString());
//        Log.e("----------", "inputTureList" + inputTureList.toString());
//        Log.e("----------", "inputFalseList" + inputFalseList.toString());
//        Log.e("----------", "selectTureList" + selectTureList.toString());
//        Log.e("----------", "selectFalseList" + selectFalseList.toString());

        for (int i = 0; i < selectList.size(); i++) {
            int j = selectList.get(i);
            if (etMap.get(j) == null || etMap.get(j).equals("")) {
                key1++;
                //Log.e("-----------", "key1-----" + key1);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("sl_code", mCheckMsgList.get(j).getSL_CODE());
            jsonObject.addProperty("si_goods", mCheckMsgList.get(j).getREMARK());

            jsonObject.addProperty("si_error", etCheckMap.get(j));

            jsonObject.addProperty("status", etCheckFlagMap.get(j));
            jsonObject.addProperty("si_remark", etMap.get(j));
            JsonArray photoArray = new JsonArray();
            if (imagePathsMap.get(j) != null && imagePathsMap.get(j).size()!=0) {
                for (int k = 0; k < imagePathsMap.get(j).size(); k++) {
                    final String pic_path = imagePathsMap.get(j).get(k);
                    //Log.e("------pic_path-------", pic_path);
                    String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                    String _path =  sign_dir + File.separator  + System.currentTimeMillis() +j+k+ ".jpg";
                    //Log.e("------_path-------", _path);
                    final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 50);
                    //Log.e("----compressImage---", compressImage);
                    String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
                    //Log.e("----picBase64Code----", "===="+ picBase64Code);
                    JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("file", picBase64Code);
//                    jsonObject1.addProperty("file", sign_dir);
                    photoArray.add(jsonObject1);
                }
            }else {
                key2++;
                //Log.e("-----------", "key2-----" + key2);
            }
            jsonObject.add("photo", photoArray);
            array.add(jsonObject);
        }

        object.add("body", array);

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
                            //Log.e("-----------", "result==" + result);
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
                    FileUtil.deletePhotos(IGCheckListActivity.this);
                }
            }
        }.start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(IGCheckListActivity.this,IGReformListActivity.class);
        intent.putExtra("sh_code",sh_code);
        intent.putExtra("igMessage",wrongList.get(position));
        startActivity(intent);
    }
}