package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.EmpListAdapter;
import com.example.administrator.yanfoxconn.adapter.SanFangListAdapter;
import com.example.administrator.yanfoxconn.bean.AQ110Message;
import com.example.administrator.yanfoxconn.bean.ActivityMsg;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import taobe.tec.jcc.JChineseConvertor;

/**
 * 人資活動發佈結束
 * Created by S1007989 on 2021/5/18.
 */

public class ActEndActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTittle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//結束/提交

    @BindView(R.id.rb_person)
    RadioButton rbPerson;//個人
    @BindView(R.id.rb_team)
    RadioButton rbTeam;//團隊
    @BindView(R.id.et_act_name)
    EditText etActName;//活動名稱
    @BindView(R.id.et_act_rules)
    EditText etActRules;//活動規則
    @BindView(R.id.et_act_award)
    EditText etActAward;//獎品設置
    @BindView(R.id.tv_time_start)
    TextView tvStartTime;//活動開始時間
    @BindView(R.id.tv_end_sign)
    TextView tvEndSignDate;//報名截止時間
    @BindView(R.id.tr_team_num)
    TableRow trTeamNum;//隊伍上限數量TableRow顯示隱藏
    @BindView(R.id.et_num_team)
    EditText etNumTeam;//隊伍上限數量
    @BindView(R.id.et_num_person)
    EditText etNumPerson;//活動上限人數

    @BindView(R.id.tl_end)
    TableLayout tlEnd;//結束信息TableLayout
    @BindView(R.id.ll_photo)
    LinearLayout llPhoto;//結束信息圖片LinearLayout
    @BindView(R.id.tv_time_end)
    TextView tvEndTime;//活動結束時間
    @BindView(R.id.et_num_now)
    EditText etNumNow;//現場活動人數


    @BindView(R.id.et_code)
    EditText etCode;//冠軍工號
    @BindView(R.id.lv_gate)
    MyListView lvGate;//冠軍工號帶出
    @BindView(R.id.tv_name)
    TextView tvName;//冠軍姓名
    @BindView(R.id.tr_list_gate)
    TableRow trLIstGate;//參賽人員列表
    @BindView(R.id.et_score)
    EditText etScore;//冠軍得分
    @BindView(R.id.tv_sign_now)
    TextView tvSignNow;//已報人數/隊伍


    @BindView(R.id.et_code2)
    EditText etCode2;//亞軍工號
    @BindView(R.id.lv_gate2)
    MyListView lvGate2;//亞軍工號帶出
    @BindView(R.id.tv_name2)
    TextView tvName2;//亞軍姓名
    @BindView(R.id.tr_list_gate2)
    TableRow trLIstGate2;//參賽人員列表2
    @BindView(R.id.et_score2)
    EditText etScore2;//亞軍得分

    @BindView(R.id.iv_empty1)
    ImageView ivEmptyOne;//空白图片占位
    @BindView(R.id.gv_photo1)
    GridView gridViewOne;//图片显示区域
    @BindView(R.id.iv_empty2)
    ImageView ivEmptyTwo;//空白图片占位
    @BindView(R.id.gv_photo2)
    GridView gridViewTwo;//图片显示区域
    @BindView(R.id.iv_empty3)
    ImageView ivEmptyThree;//空白图片占位
    @BindView(R.id.gv_photo3)
    GridView gridViewThree;//图片显示区域
    @BindView(R.id.tv_num_team)
    TextView tvNumTeam;//隊伍上限數量TV
    @BindView(R.id.tv_num_person)
    TextView tvNumPerson;//活動上限人數TV


    private static final int REQUEST_CAMERA_CODE_ONE = 111;
    private static final int REQUEST_PREVIEW_CODE_ONE = 221;
    private static final int REQUEST_CAMERA_CODE_TWO = 112;
    private static final int REQUEST_PREVIEW_CODE_TWO = 222;
    private static final int REQUEST_CAMERA_CODE_THREE = 113;
    private static final int REQUEST_PREVIEW_CODE_THREE = 223;
    private ArrayList<String> imagePathsOne = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPathsOne = new ArrayList<String>();//圖片壓縮后地址
    private ArrayList<String> imagePathsTwo = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPathsTwo = new ArrayList<String>();//圖片壓縮后地址
    private ArrayList<String> imagePathsThree = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPathsThree = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private GridAdapter gridAdapterOne;
    private GridAdapter gridAdapterTwo;
    private GridAdapter gridAdapterThree;

    private String initStartDateTime; // 初始化开始时间
    private Date selectEndTime = null;//所選時間
    private Date curDate = null;//當前時間
    private String curDates;//當前時間
    private SimpleDateFormat formatter;
    private ActEndActivity mContext = this;
    private boolean b = true;
    private String act_id; //活動ID
    private List<ActivityMsg> actSignList;
    private SanFangListAdapter mAdapter,mAdapter2;
    private boolean isClicked = true;//判斷是否點擊
    private ActivityMsg activityMsg;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_view_and_end);
        ButterKnife.bind(this);

        //getMessage();//根據工號獲得信息
        tvTittle.setText("活動詳情");
        btnUp.setText("結束");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        ivEmptyOne.setOnClickListener(this);
        ivEmptyTwo.setOnClickListener(this);
        ivEmptyThree.setOnClickListener(this);
        //tvStartTime.setOnClickListener(this);
        //tvEndSignDate.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        rbPerson.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    trTeamNum.setVisibility(View.GONE);
                }else{
                    trTeamNum.setVisibility(View.VISIBLE);
                }
            }
        });
        activityMsg = (ActivityMsg) getIntent().getSerializableExtra("item");
        if (activityMsg!=null){
            if (activityMsg.getAct_type().equals("個人賽")){
                rbPerson.setChecked(true);
                rbPerson.setEnabled(false);
                rbTeam.setEnabled(false);
                etNumPerson.setText(activityMsg.getAct_num_team());
            }else {
                rbTeam.setChecked(true);
                rbPerson.setEnabled(false);
                rbTeam.setEnabled(false);
                etNumTeam.setText(activityMsg.getAct_num_team());
                etNumPerson.setText(activityMsg.getAct_num_person());
                tvNumPerson.setText("單個隊伍人數");
            }
            etActName.setText(activityMsg.getAct_name());
            etActRules.setText(activityMsg.getAct_rules());
            etActAward.setText(activityMsg.getAct_award());
            tvStartTime.setText(activityMsg.getAct_time_start().substring(0,16));
            tvEndSignDate.setText(activityMsg.getAct_end_sign().substring(0,16));
            tvSignNow.setText(activityMsg.getAct_sign_now());
            act_id = activityMsg.getAct_id();
            etActName.setEnabled(false);
            etActRules.setEnabled(false);
            etActAward.setEnabled(false);
            etNumPerson.setEnabled(false);
            etNumTeam.setEnabled(false);

            int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
            cols = cols < 4 ? 4 : cols;
            gridViewOne.setNumColumns(cols);
            gridViewTwo.setNumColumns(cols);
            gridViewThree.setNumColumns(cols);

            // preview
            gridViewOne.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == imagePathsOne.size()) {
                        PhotoPickerIntent intent = new PhotoPickerIntent(mContext);
                        intent.setSelectModel(SelectModel.MULTI);
                        intent.setShowCarema(true); // 是否显示拍照
                        intent.setMaxTotal(1); // 最多选择照片数量，默认为1
                        intent.setSelectedPaths(imagePathsOne); // 已选中的照片地址， 用于回显选中状态

                        startActivityForResult(intent, REQUEST_CAMERA_CODE_ONE);
                    } else {
                        PhotoPreviewIntent intent = new PhotoPreviewIntent(mContext);
                        intent.setCurrentItem(position);
                        intent.setPhotoPaths(imagePathsOne);

                        startActivityForResult(intent, REQUEST_PREVIEW_CODE_ONE);
                    }
                }
            });
            gridViewTwo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == imagePathsTwo.size()) {
                        PhotoPickerIntent intent = new PhotoPickerIntent(mContext);
                        intent.setSelectModel(SelectModel.MULTI);
                        intent.setShowCarema(true); // 是否显示拍照
                        intent.setMaxTotal(3); // 最多选择照片数量，默认为1
                        intent.setSelectedPaths(imagePathsTwo); // 已选中的照片地址， 用于回显选中状态

                        startActivityForResult(intent, REQUEST_CAMERA_CODE_TWO);
                    } else {
                        PhotoPreviewIntent intent = new PhotoPreviewIntent(mContext);
                        intent.setCurrentItem(position);
                        intent.setPhotoPaths(imagePathsTwo);

                        startActivityForResult(intent, REQUEST_PREVIEW_CODE_TWO);
                    }
                }
            });
            gridViewThree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == imagePathsThree.size()) {
                        PhotoPickerIntent intent = new PhotoPickerIntent(mContext);
                        intent.setSelectModel(SelectModel.MULTI);
                        intent.setShowCarema(true); // 是否显示拍照
                        intent.setMaxTotal(1); // 最多选择照片数量，默认为1
                        intent.setSelectedPaths(imagePathsThree); // 已选中的照片地址， 用于回显选中状态

                        startActivityForResult(intent, REQUEST_CAMERA_CODE_THREE);
                    } else {
                        PhotoPreviewIntent intent = new PhotoPreviewIntent(mContext);
                        intent.setCurrentItem(position);
                        intent.setPhotoPaths(imagePathsThree);

                        startActivityForResult(intent, REQUEST_PREVIEW_CODE_THREE);
                    }
                }
            });
        }


        //Log.e("-----activityMsg.getAct_id()-----", activityMsg.getAct_id() );
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        initStartDateTime = formatterUse.format(curDate);
        //tvStartTime.setText(formatter.format(curDate));
        //tvEndSignDate.setText(formatter.format(curDate));
        tvEndTime.setText(formatter.format(curDate));
        curDates = formatter.format(curDate);

        getMessage();

        //搜索关键字
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                trLIstGate.setVisibility(View.VISIBLE);
                String a = etCode.getText().toString();
                //调用适配器里面的搜索方法
                mAdapter.SearchCity(a);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }

        });
        //搜索关键字
        etCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                trLIstGate2.setVisibility(View.VISIBLE);
                String a = etCode2.getText().toString();
                //调用适配器里面的搜索方法
                mAdapter2.SearchCity(a);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }

        });
        //rbPerson.setChecked(true);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left://返回
                finish();
                break;
            case R.id.btn_title_right://提交
                if (!activityMsg.getAct_sign_now().equals("0")){
                    if (b){
                        btnUp.setText("提交");
                        tlEnd.setVisibility(View.VISIBLE);
                        llPhoto.setVisibility(View.VISIBLE);
                        b = false;
                    }else {
                        //ToastUtils.showShort(mContext,"提交數據");
                        if (isClicked){
                            aboutAlert(getResources().getString(R.string.update_confirm),Constants.MESSAGE_UP);
                            isClicked = false;
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    isClicked = true;
                                }
                            }, 2000);
                        }else{
                            ToastUtils.showShort(ActEndActivity.this,R.string.multiple_click);
                        }
                    }
                }else {
                    ToastUtils.showShort(ActEndActivity.this,"還沒有人報名，暫時無法結束活動！");
                }

                break;
            case R.id.tv_time_start:
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        mContext, initStartDateTime);
                dateTimePicKDialog.dateTimePicKDialog(tvStartTime,"","");
                break;
            case R.id.tv_end_sign:
                DateTimePickDialogUtil dateTimePicKDialog2 = new DateTimePickDialogUtil(
                        mContext, initStartDateTime);
                dateTimePicKDialog2.dateTimePicKDialog(tvEndSignDate,"","");
                break;
            case R.id.tv_time_end:
                DateTimePickDialogUtil dateTimePicKDialog3 = new DateTimePickDialogUtil(
                        mContext, initStartDateTime);
                dateTimePicKDialog3.dateTimePicKDialog(tvEndTime,"","");
                break;
            case R.id.iv_empty1:
                PhotoPickerIntent intent = new PhotoPickerIntent(mContext);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(1); // 最多选择照片数量，默认为3
                intent.setSelectedPaths(imagePathsOne); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent, REQUEST_CAMERA_CODE_ONE);
                break;
            case R.id.iv_empty2:
                PhotoPickerIntent intent2 = new PhotoPickerIntent(mContext);
                intent2.setSelectModel(SelectModel.MULTI);
                intent2.setShowCarema(true); // 是否显示拍照
                intent2.setMaxTotal(3); // 最多选择照片数量，默认为3
                intent2.setSelectedPaths(imagePathsTwo); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent2, REQUEST_CAMERA_CODE_TWO);
                break;
            case R.id.iv_empty3:
                PhotoPickerIntent intent3 = new PhotoPickerIntent(mContext);
                intent3.setSelectModel(SelectModel.MULTI);
                intent3.setShowCarema(true); // 是否显示拍照
                intent3.setMaxTotal(1); // 最多选择照片数量，默认为3
                intent3.setSelectedPaths(imagePathsThree); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent3, REQUEST_CAMERA_CODE_THREE);
                break;
        }
    }
    //獲取報名人員
    private void getMessage() {
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
            return;
        }
        final String url = Constants.HTTP_ACTIVITY_JSON_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();
        object.addProperty("flag","actSignCheck");
        object.addProperty("act_id",act_id);

        Log.e("-----object------",  object.toString());

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
                    Log.e("---------", "==fff===" + url);
                    String result = HttpConnectionUtil.doPostJsonObject(url, object);
                    dismissDialog();
                    Gson gson = new Gson();
                    if (result != null) {
                        Log.e("---------", "result==fff===" + result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String errCode = jsonObject.get("errCode").getAsString();
                        if (errCode.equals("200")) {
                            JsonArray array = jsonObject.get("result").getAsJsonArray();
                            actSignList = new ArrayList<ActivityMsg>();

                            for (JsonElement type : array) {
                                ActivityMsg humi = gson.fromJson(type, ActivityMsg.class);
                                actSignList.add(humi);
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


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case Constants.MESSAGE_TOAST://Toast彈出

                   ToastUtils.showLong(ActEndActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case Constants.MESSAGE_SET_TEXT://text賦值
                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case Constants.MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),Constants.MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                    break;
                case Constants.MESSAGE_ALERT://提交響應
                    worningAlert(msg.obj.toString(),Constants.MESSAGE_TOAST);
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
        List<String> codeList = new ArrayList<>();
        HashMap<String,String> nameMap = new HashMap<String,String>();
        for (int i = 0;i<actSignList.size();i++){
            codeList.add(actSignList.get(i).getSign_code());
            nameMap.put(actSignList.get(i).getSign_code(),actSignList.get(i).getSign_name());
        }
        mAdapter = new SanFangListAdapter(ActEndActivity.this,codeList);
        lvGate.setAdapter(mAdapter);
        mAdapter2 = new SanFangListAdapter(ActEndActivity.this,codeList);
        lvGate2.setAdapter(mAdapter2);
        //
        mAdapter.OnClickSetText(new SanFangListAdapter.OnClickSetText() {
            @Override
            public void OnClickxt(String tit) {
                etCode.setText(tit);
                tvName.setText(nameMap.get(tit));
                mAdapter.SearchCity("");
                trLIstGate.setVisibility(View.GONE);
            }
        });
        //
        mAdapter2.OnClickSetText(new SanFangListAdapter.OnClickSetText() {
            @Override
            public void OnClickxt(String tit) {
                etCode2.setText(tit);
                tvName2.setText(nameMap.get(tit));
                mAdapter2.SearchCity("");
                trLIstGate2.setVisibility(View.GONE);
            }
        });
    }
    //提交前檢查
    private void check(){
        if(FoxContext.getInstance().getLoginId().equals("")){
            ToastUtils.showLong(mContext,"登錄超時,請退出重新登錄!");
            return;
        }

        if (etCode.getText().toString().replaceAll(" ","").equals("")){
            ToastUtils.showShort(mContext,"請輸入冠軍工號！");
            return;
        }
        if (tvName.getText().toString().replaceAll(" ","").equals("")){
            ToastUtils.showShort(mContext,"請輸入冠軍姓名！");
            return;
        }
        if (etScore.getText().toString().replaceAll(" ","").equals("")){
            ToastUtils.showShort(mContext,"請輸入冠軍分數！");
            return;
        }
        try {
            selectEndTime = formatter.parse(tvEndTime.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (curDate.getTime()-selectEndTime.getTime()<0){
            ToastUtils.showShort(mContext,"請检查活動結束時間！");
            return;
        }

        if (imagePathsOne==null) {
            ToastUtils.showShort(this, "請上傳參加人員照片");
            return;
        }
        if (imagePathsTwo==null) {
            ToastUtils.showShort(this, "請上傳活動期間的照片");
            return;
        }
        if (imagePathsThree==null) {
            ToastUtils.showShort(this, "請上傳獲獎人合影");
            return;
        }
        upMsessage();
    }

    //提交信息
    private void upMsessage() {

        final String url = Constants.HTTP_ACTIVITY_JSON_SERVLET; //此處寫上自己的URL

        JsonObject object = new JsonObject();

        JsonArray photoArray1 = new JsonArray();
        JsonArray photoArray2 = new JsonArray();
        JsonArray photoArray3 = new JsonArray();

        if (imagePathsOne != null) {
            for (int i = 0; i < imagePathsOne.size(); i++) {
                File pictureFile = new File(imagePathsOne.get(i)); //通過路徑獲取檔
                final String pic_path = imagePathsOne.get(i);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 30);
                String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("file", picBase64Code);
                photoArray1.add(jsonObject1);
            }
            object.add("photo1", photoArray1);
        }
        if (imagePathsTwo != null) {
            for (int i = 0; i < imagePathsTwo.size(); i++) {
                File pictureFile = new File(imagePathsTwo.get(i)); //通過路徑獲取檔
//            fileMap.put("file" + i, pictureFile);//添加第一個文件
                final String pic_path = imagePathsTwo.get(i);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
                //getFilesDir().getAbsolutePath()+"compressPic.jpg";
                //调用压缩图片的方法，返回压缩后的图片path
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 30);
                String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("file", picBase64Code);
                photoArray2.add(jsonObject1);
            }
            object.add("photo2", photoArray2);
        }
        if (imagePathsThree != null) {
            for (int i = 0; i < imagePathsThree.size(); i++) {
                File pictureFile = new File(imagePathsThree.get(i)); //通過路徑獲取檔
                final String pic_path = imagePathsThree.get(i);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 30);
                String picBase64Code = ImageZipUtils.imageToBase64(compressImage);
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("file", picBase64Code);
                photoArray3.add(jsonObject1);
            }
            object.add("photo3", photoArray3);
        }

        object.addProperty("flag","actEnd");
        object.addProperty("act_id",act_id);
        object.addProperty("act_num_now",etNumNow.getText().toString());
        object.addProperty("act_time_end",tvEndTime.getText().toString());
        object.addProperty("code1",etCode.getText().toString());
        object.addProperty("score1",etScore.getText().toString());
        object.addProperty("code2",etCode2.getText().toString());
        object.addProperty("score2", etScore2.getText().toString());

        Log.e("----object----",  object.toString());

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
                    Log.e("-----url----",  url);
                    String result = HttpConnectionUtil.doPostJsonObject(url, object);
                    if (result != null) {
                        dismissDialog();
                        Log.e("----result-----",  result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        String errCode = jsonObject.get("errCode").getAsString();
                        if (errCode.equals("200")) {
                            Message message = new Message();
                            message.what = Constants.MESSAGE_ALERT;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);

                        } else{
                            Message message = new Message();
                            message.what = Constants.MESSAGE_ALERT;
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
                    FileUtil.deletePhotos(mContext);
                }
            }
        }.start();
    }


    //照片上傳 返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE_ONE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT), requestCode);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE_ONE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT), requestCode);
                    break;
                // 选择照片
                case REQUEST_CAMERA_CODE_TWO:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT), requestCode);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE_TWO:
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT), requestCode);
                    break;
                // 选择照片
                case REQUEST_CAMERA_CODE_THREE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT), requestCode);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE_THREE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT), requestCode);
                    break;

                // 调用相机拍照
                case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                    if (captureManager.getCurrentPhotoPath() != null) {
                        captureManager.galleryAddPic();

                        ArrayList<String> paths = new ArrayList<>();
                        paths.add(captureManager.getCurrentPhotoPath());
                        loadAdpater(paths, requestCode);
                        Log.e("==========", paths.get(1));
                    }
                    break;
            }
        }
    }

    private void loadAdpater(ArrayList<String> paths, int code) {
        switch (code) {
            case REQUEST_CAMERA_CODE_ONE:
                loadOne(paths);
                break;
            case REQUEST_PREVIEW_CODE_ONE:
                loadOne(paths);
                break;
            case REQUEST_CAMERA_CODE_TWO:
                loadTwo(paths);
                break;
            case REQUEST_PREVIEW_CODE_TWO:
                loadTwo(paths);
                break;
            case REQUEST_CAMERA_CODE_THREE:
                loadThree(paths);
                break;
            case REQUEST_PREVIEW_CODE_THREE:
                loadThree(paths);
                break;
        }

    }

    private void loadOne(ArrayList<String> paths) {
        if (imagePathsOne == null) {
            imagePathsOne = new ArrayList<>();
        }
        imagePathsOne.clear();
        imagePathsOne.addAll(paths);

        if (paths.size() > 0) {
            ivEmptyOne.setVisibility(View.GONE);
        } else {
            ivEmptyOne.setVisibility(View.VISIBLE);
        }

        try {
            JSONArray obj = new JSONArray(imagePathsOne);
            Log.e("--", obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gridAdapterOne == null) {
            gridAdapterOne = new GridAdapter(imagePathsOne);
            gridViewOne.setAdapter(gridAdapterOne);
            Log.e("----------------", "ddd==" + imagePathsOne.size());
        } else {
            gridAdapterOne.notifyDataSetChanged();
        }
    }

    private void loadTwo(ArrayList<String> paths) {
        if (imagePathsTwo == null) {
            imagePathsTwo = new ArrayList<>();
        }
        imagePathsTwo.clear();
        imagePathsTwo.addAll(paths);

        if (paths.size() > 0) {
            ivEmptyTwo.setVisibility(View.GONE);
        } else {
            ivEmptyTwo.setVisibility(View.VISIBLE);
        }

        try {
            JSONArray obj = new JSONArray(imagePathsTwo);
            Log.e("--", obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gridAdapterTwo == null) {
            gridAdapterTwo = new GridAdapter(imagePathsTwo);
            gridViewTwo.setAdapter(gridAdapterTwo);
            Log.e("----------------", "ddd==" + imagePathsTwo.size());
        } else {
            gridAdapterTwo.notifyDataSetChanged();
        }
    }

    private void loadThree(ArrayList<String> paths) {
        if (imagePathsThree == null) {
            imagePathsThree = new ArrayList<>();
        }
        imagePathsThree.clear();
        imagePathsThree.addAll(paths);

        if (paths.size() > 0) {
            ivEmptyThree.setVisibility(View.GONE);
        } else {
            ivEmptyThree.setVisibility(View.VISIBLE);
        }

        try {
            JSONArray obj = new JSONArray(imagePathsThree);
            Log.e("--", obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gridAdapterThree == null) {
            gridAdapterThree = new GridAdapter(imagePathsThree);
            gridViewThree.setAdapter(gridAdapterThree);
            Log.e("----------------", "ddd==" + imagePathsThree.size());
        } else {
            gridAdapterThree.notifyDataSetChanged();
        }
    }

    private void aboutAlert(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("確認信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (type==Constants.MESSAGE_TOAST){
                            finish();
                        }else if(type==Constants.MESSAGE_UP){
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
    private void worningAlert(String msg, final int t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (t==Constants.MESSAGE_TOAST) {
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
                Glide.with(mContext)
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
