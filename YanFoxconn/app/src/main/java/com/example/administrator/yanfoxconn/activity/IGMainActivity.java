package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.IGListAdapter;
import com.example.administrator.yanfoxconn.adapter.IGStoreLvAdapter;
import com.example.administrator.yanfoxconn.bean.BodyONE;
import com.example.administrator.yanfoxconn.bean.EmpFile;
import com.example.administrator.yanfoxconn.bean.EmpMessage;
import com.example.administrator.yanfoxconn.bean.GCBody;
import com.example.administrator.yanfoxconn.bean.GEMenLiu;
import com.example.administrator.yanfoxconn.bean.IGMessage;
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
import com.example.administrator.yanfoxconn.widget.MyGridView;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description 宿舍寄存主界面
 * @Author song
 * @Date 2021/3/28 16:47
 */
public class IGMainActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_SET_LIST = 1;
    private final int MESSAGE_SET_TYPE = 0;
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_SPINNER = 6;//spinner賦值
    private final int MESSAGE_SET_GOOD_LIST = 4;//物品信息
    private final int MESSAGE_SET_STORE_LIST = 3;//倉位信息
    private final int MESSAGE_SET_STORE_QR = 7;//掃碼判斷是排配還是領取

    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.tv_id)
    TextView tvId;//工號
    @BindView(R.id.tv_name)
    TextView tvName;//姓名
    @BindView(R.id.tv_cpc)
    TextView tvCpc;//部門
    @BindView(R.id.tv_room)
    TextView tvRoom;//房間
    @BindView(R.id.tv_phone)
    TextView tvPhone;//電話
    @BindView(R.id.tr_time)
    TableRow trTime;//時間行
    @BindView(R.id.tv_time_date)
    TextView tvTimeDate;//時間日期
    @BindView(R.id.tr_btn_show)
    TableRow trBtnShow;//點擊顯示行
    @BindView(R.id.btn_show)
    Button btnShow;//點擊顯示
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    MyGridView gvPhoto;//图片显示区域
    @BindView(R.id.tr_store)
    TableRow trStore;//倉庫下拉行
    @BindView(R.id.sp_store)
    Spinner spStore;//倉庫
    @BindView(R.id.tr_store_lv)
    TableRow trStoreLv;//帶儲位物品排配列表行
    @BindView(R.id.lv_store)
    RecyclerView lvStore;//帶儲位物品排配列表

    @BindView(R.id.tr_done)
    TableRow trDone;//本人領取行
    @BindView(R.id.rg_done)
    RadioGroup rgDone;//本人領取
    @BindView(R.id.rtb_yes)
    RadioButton rtbYes;//本人領取正常
    @BindView(R.id.rtb_no)
    RadioButton rtbNo;//本人領取異常
    @BindView(R.id.tr_d_id)
    TableRow trDId;//代領人工號行
    @BindView(R.id.et_d_id)
    EditText etDId;//代領人工號
    @BindView(R.id.tr_d_name)
    TableRow trDName;//代領人姓名行
    @BindView(R.id.et_d_name)
    EditText etDName;//代領人姓名

    private LinearLayout container;

    private String result;//返回結果
    private List<IGMessage> typeList, storeList;
    private List<String> types, stores, deposit;
    private List<BodyONE> bodyOne;
    private IGStoreLvAdapter storeLvAdapter;

    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private GridAdapter gridAdapter;
    private String initStartDateTime; // 初始化开始时间

    private String from;
    private String sID;//單號  storeQr使用
    private String statue;//狀態
    private IGMessage peopleMsg;

    private String done = "";//是否是本人領取，為空則是未選擇，不能確認

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ig_main);
        ButterKnife.bind(this);
        from = getIntent().getStringExtra("from");
        if (from.equals("add")) {
            tvTitle.setText("申請單");
            getInfo(getIntent().getStringExtra("id"));
            trStore.setVisibility(View.GONE);
            trStoreLv.setVisibility(View.GONE);
            btnUp.setText("提交");
        } else if (from.equals("store")) {
            trTime.setVisibility(View.GONE);
            trBtnShow.setVisibility(View.GONE);
            trStore.setVisibility(View.VISIBLE);
            trStoreLv.setVisibility(View.VISIBLE);
            tvTitle.setText("寄存排配");
            peopleMsg = (IGMessage) getIntent().getSerializableExtra("people");

            btnUp.setText("確認");
            getPersonInfo(peopleMsg.getS_ID());
        } else if (from.equals("leave")) {
            trTime.setVisibility(View.GONE);
            trBtnShow.setVisibility(View.GONE);
//            trStore.setVisibility(View.VISIBLE);
            trStoreLv.setVisibility(View.VISIBLE);
            tvTitle.setText("離職領取");
            trDone.setVisibility(View.VISIBLE);
            btnUp.setText("確認");
            ivEmpty.setVisibility(View.GONE);
            trStore.setVisibility(View.GONE);

            getPersonInfo(getIntent().getStringExtra("id"));
        } else if (from.equals("storeQr")) {
//            trTime.setVisibility(View.GONE);
//            trBtnShow.setVisibility(View.GONE);
////            trStore.setVisibility(View.VISIBLE);
//            trStoreLv.setVisibility(View.VISIBLE);
//            tvTitle.setText("在職領取");
//            trDone.setVisibility(View.VISIBLE);
//            ivEmpty.setVisibility(View.GONE);
//            trStore.setVisibility(View.GONE);
////            getPersonInfo(getIntent().getStringExtra("result"));
//
//            btnUp.setText("確認");
            getPersonInfo(getIntent().getStringExtra("id"));
        }

        rgDone.setOnCheckedChangeListener(this);
        rtbYes.setOnClickListener(this::onClick);
        rtbNo.setOnClickListener(this::onClick);

        btnUp.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        ivEmpty.setOnClickListener(this);
        tvTimeDate.setOnClickListener(this);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        initStartDateTime = formatterUse.format(curDate);
        tvTimeDate.setText(formatter.format(curDate));
        Log.e("--======", "222222222222");
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gvPhoto.setNumColumns(cols);

        // preview
        gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(IGMainActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(IGMainActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                // 根据tag区分是新增view还是删除view
                String tag = (String) view.getTag();
                if ("-".equals(tag)) {
                    // 删除view
                    // 获取子控件
                    View child = (View) view.getParent();
                    // 从父控件中移除子控件
                    container.removeView(child);
                } else {
                    // 新增view
                    // 创建子控件实例
                    View child = LayoutInflater.from(IGMainActivity.this).inflate(R.layout.edt_ig, container, false);
                    // 获取其中的button
                    View btn = child.findViewById(R.id.btn);
                    // 监听点击事件
                    btn.setOnClickListener(this);
                    // 设置删除的tag
                    btn.setTag("-");
                    btn.setBackgroundDrawable(ContextCompat.getDrawable(this.getApplication(), R.mipmap.icon_minus));

                    final EditText editText = child.findViewById(R.id.et_num);
                    final Spinner spType = child.findViewById(R.id.sp_type);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            //输入内容之前你想做什么
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            //输入的时候你想做什么
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            //输入之后你想做什么                num[cout]=Integer.parseInt(editText.getText().toString());
                            Log.e("-------------", "et=====" + editText.getText());
                        }
                    });
                    types = new ArrayList<>();
                    for (int i = 0; i < typeList.size(); i++) {

                        types.add(typeList.get(i).getST_NAME());

                    }
                    spType.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, types));
                    spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


//                Log.e("---------", "最喜欢的水果是：" + str);
//                paramMap.put("kedui",str);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    // 添加进父控件
                    container.addView(child);
                }
                // 请求重绘
                container.invalidate();

                break;
            case R.id.btn_title_right:
                if (from.equals("storeQr")) {
                    if (statue.equals("已申請")) {
                        upStoreView();
                    } else {
                        if (done.equals("")) {
                            ToastUtils.showShort(IGMainActivity.this, "請選擇領取狀體");
                        } else if (done.equals("N")) {
                            if (etDId.getText().toString().equals("") || etDName.getText().toString().equals("")) {
                                ToastUtils.showShort(IGMainActivity.this, "請填寫代領人資料");
                            } else {
                                upLeaveView();
                            }
                        } else {
                            upLeaveView();
                        }
                    }
                } else if (from.equals("leave")) {
                    if (done.equals("")) {
                        ToastUtils.showShort(IGMainActivity.this, "請選擇領取狀體");
                    } else if (done.equals("N")) {
                        if (etDId.getText().toString().equals("") || etDName.getText().toString().equals("")) {
                            ToastUtils.showShort(IGMainActivity.this, "請填寫代領人資料");
                        } else {
                            upLeaveView();
                        }
                    } else {
                        upLeaveView();
                    }
                } else if (from.equals("store")) {
                    upStoreView();
                } else {
                    List<String> type = new ArrayList<>();
                    List<String> num = new ArrayList<>();
                    for (int i = 0; i < container.getChildCount(); i++) {
//            String names;
                        View childAt = container.getChildAt(i);
                        EditText hotelNum = (EditText) childAt.findViewById(R.id.et_num);
                        Spinner hotelType = (Spinner) childAt.findViewById(R.id.sp_type);
                        if (!hotelNum.getText().toString().equals("")) {
                            num.add(hotelNum.getText().toString());
                        } else {
                            ToastUtils.showShort(this, "不能有空數據,請確認!!!!");
                            num.clear();
                            break;
                        }
                        if (!hotelType.getSelectedItem().toString().equals("")) {

                            type.add(hotelType.getSelectedItem().toString());
                        } else {
                            ToastUtils.showShort(this, "不能有空數據,請確認!!!!");
                            type.clear();
                            break;
                        }
                    }

                    if (num.size() == 0 || num.equals(null) || type.size() == 0 || type.equals(null)) {
                        ToastUtils.showShort(this, "不能有空數據,請確認!!!!");
                    } else {
                        upName(num, type);
                    }
                }
                break;
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_show:
                // 创建子控件实例
                View child = LayoutInflater.from(IGMainActivity.this).inflate(R.layout.edt_ig, container, false);
                final Button btn = child.findViewById(R.id.btn);
                btn.setOnClickListener(this);
                final Spinner spType = child.findViewById(R.id.sp_type);
                final EditText editText = child.findViewById(R.id.et_num);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        //输入内容之前你想做什么
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //输入的时候你想做什么
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        //输入之后你想做什么                num[cout]=Integer.parseInt(editText.getText().toString());
                        Log.e("-------------", "et=====" + editText.getText());
                    }
                });

                types = new ArrayList<>();
                for (int i = 0; i < typeList.size(); i++) {

                    types.add(typeList.get(i).getST_NAME());

                }

                spType.setAdapter(new ArrayAdapter<String>(IGMainActivity.this, android.R.layout.simple_list_item_1, types));
                spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


//                Log.e("---------", "最喜欢的水果是：" + str);
//                paramMap.put("kedui",str);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                container.addView(child);
                btnShow.setClickable(false);
                btnShow.setBackgroundColor(getResources().getColor(R.color.color_858585));
                break;
            case R.id.tv_time_date:

                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        IGMainActivity.this, initStartDateTime);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                dateTimePicKDialog.dateTimePicKNoTimeDialog(tvTimeDate, formatter.format(new Date(System.currentTimeMillis()))
                );
                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(IGMainActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
        }
    }

    /**
     * 提交行李信息
     *
     * @param num  數量
     * @param type 類別
     */
    private void upName(List<String> num, List<String> type) {
        final String url = Constants.HTTP_STORE_APPLY_OK;
        JsonObject object = new JsonObject();
        object.addProperty("s_user_id", tvId.getText().toString());
        object.addProperty("s_user_name", tvName.getText().toString());
        object.addProperty("s_user_dep", tvCpc.getText().toString());
        object.addProperty("s_bedid", tvRoom.getText().toString());
        object.addProperty("s_tel", tvPhone.getText().toString());
        object.addProperty("s_zw_applyer", FoxContext.getInstance().getName());
        object.addProperty("s_takeback_expect_date", tvTimeDate.getText().toString());
        JsonArray array = new JsonArray();
        for (int i = 0; i < num.size(); i++) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("s_deposit_name", type.get(i));
            jsonObject.addProperty("s_deposit_count", num.get(i).toString());
            array.add(jsonObject);
        }

        object.add("body", array);
        JsonArray photoArray = new JsonArray();

        if (imagePaths != null && imagePaths.size() != 0) {
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
        } else {
            ToastUtils.showShort(IGMainActivity.this, "請拍照！");
        }
        object.add("photo", photoArray);
        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
                    Log.e("---------", "==fff===" + url);
                    String result = HttpConnectionUtil.doPostJsonObject(url, object);
                    Log.e("----------", "obj===" + object.toString());
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
                    FileUtil.deletePhotos(IGMainActivity.this);
                }
            }
        }.start();

    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showShort(IGMainActivity.this, msg.obj.toString());
                    finish();
                    break;
                case MESSAGE_SET_LIST://設置信息
                    JsonObject mes = (JsonObject) msg.obj;
                    tvId.setText(mes.get("REGULAREMP_NO").getAsString());
                    tvName.setText(mes.get("REGULAREMP_NAME").getAsString());
                    tvCpc.setText(mes.get("REGULAREMP_DEPARTMENT").getAsString());
                    tvRoom.setText(mes.get("REGULAREMP_BEDID").getAsString());
                    tvPhone.setText(mes.get("REGULAREMP_TEL").getAsString());
                    getType();
                    break;
                case MESSAGE_SET_TYPE:

                    // 父控件
                    container = (LinearLayout) findViewById(R.id.ll_actual);

                    break;
                case MESSAGE_NETMISTAKE://Toast彈出
                    ToastUtils.showLong(IGMainActivity.this, R.string.net_mistake);
                    break;
                case MESSAGE_SET_SPINNER:
                    setSpinner();
                    break;
                case MESSAGE_SET_GOOD_LIST:
                    JsonObject mesBody = (JsonObject) msg.obj;
                    tvId.setText(mesBody.get("S_USER_ID").getAsString());
                    tvName.setText(mesBody.get("S_USER_NAME").getAsString());
                    tvCpc.setText(mesBody.get("S_USER_DEP").getAsString());
                    tvRoom.setText(mesBody.get("S_BEDID").getAsString());
                    tvPhone.setText(mesBody.get("S_TEL").getAsString());
                    LinearLayoutManager layoutManager = new LinearLayoutManager(IGMainActivity.this);
                    lvStore.setLayoutManager(layoutManager);
                    lvStore.setItemViewCacheSize(500);


                    storeLvAdapter = new IGStoreLvAdapter(IGMainActivity.this, bodyOne, "store");
                    lvStore.setAdapter(storeLvAdapter);
                    getStore(peopleMsg.getS_ID(), FoxContext.getInstance().getName());

                    break;
                case MESSAGE_SET_STORE_LIST:
                    lvStore.removeAllViewsInLayout();
                    Log.e("----bodyOne---", bodyOne.size() + "");

                    LinearLayoutManager layoutManager1 = new LinearLayoutManager(IGMainActivity.this);
                    lvStore.setLayoutManager(layoutManager1);
                    lvStore.setItemViewCacheSize(500);
                    storeLvAdapter = new IGStoreLvAdapter(IGMainActivity.this, bodyOne, "deposit", deposit);
                    lvStore.setAdapter(storeLvAdapter);
                    break;
                case MESSAGE_SET_STORE_QR:
                    JsonObject mesBody1 = (JsonObject) msg.obj;
                    tvId.setText(mesBody1.get("S_USER_ID").getAsString());
                    tvName.setText(mesBody1.get("S_USER_NAME").getAsString());
                    tvCpc.setText(mesBody1.get("S_USER_DEP").getAsString());
                    tvRoom.setText(mesBody1.get("S_BEDID").getAsString());
                    tvPhone.setText(mesBody1.get("S_TEL").getAsString());
                    sID = mesBody1.get("S_ID").getAsString();
                    statue = mesBody1.get("S_STATUS").getAsString();
                    LinearLayoutManager layoutManager2 = new LinearLayoutManager(IGMainActivity.this);
                    lvStore.setLayoutManager(layoutManager2);
                    lvStore.setItemViewCacheSize(500);
                    if (mesBody1.get("S_STATUS").getAsString().equals("已申請")) {

                        storeLvAdapter = new IGStoreLvAdapter(IGMainActivity.this, bodyOne, "storeQr");
                        lvStore.setAdapter(storeLvAdapter);

                        trTime.setVisibility(View.GONE);
                        trBtnShow.setVisibility(View.GONE);
                        trStore.setVisibility(View.VISIBLE);
                        trStoreLv.setVisibility(View.VISIBLE);
                        tvTitle.setText("寄存排配");
//            getPersonInfo(getIntent().getStringExtra("result"));

                        btnUp.setText("確認");
                        getStore(mesBody1.get("S_ID").getAsString(), FoxContext.getInstance().getName());
                    } else if (mesBody1.get("S_STATUS").getAsString().equals("已排配")) {
                        trTime.setVisibility(View.GONE);
                        trBtnShow.setVisibility(View.GONE);
//            trStore.setVisibility(View.VISIBLE);
                        trStoreLv.setVisibility(View.VISIBLE);
                        tvTitle.setText("在職領取");
                        trDone.setVisibility(View.VISIBLE);
                        ivEmpty.setVisibility(View.GONE);
                        trStore.setVisibility(View.GONE);
//            getPersonInfo(getIntent().getStringExtra("result"));

                        storeLvAdapter = new IGStoreLvAdapter(IGMainActivity.this, bodyOne, "storeQr");
                        lvStore.setAdapter(storeLvAdapter);
                        btnUp.setText("確認");
                    }else{
                        ToastUtils.showLong(IGMainActivity.this,mesBody1.get("S_STATUS").getAsString()+"請注意！");
                        finish();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //查看員工住宿信息
    private void getInfo(String Id) {

        showDialog();
        final String url = Constants.HTTP_STORE_OERSION_INFO + "?userid=" + Id;

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
                        JsonObject array = jsonObject.get("data").getAsJsonObject();
//                        igMessageList = new ArrayList<IGMessage>();
//
//                        for (JsonElement type : array) {
//                            IGMessage humi = gson.fromJson(type, IGMessage.class);
//                            igMessageList.add(humi);
//                        }


                        Message message = new Message();
                        message.what = MESSAGE_SET_LIST;
                        message.obj = array;
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

    //獲取物品類別
    private void getType() {
        showDialog();
        final String url = Constants.HTTP_STORE_TYPE_INFO;

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
                        typeList = new ArrayList<IGMessage>();

                        for (JsonElement type : array) {
                            IGMessage humi = gson.fromJson(type, IGMessage.class);
                            typeList.add(humi);

                        }
//
//                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
//                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();
//                        empFileList = new ArrayList<EmpFile>();
//                        for (JsonElement type1 : array1) {
//                            EmpFile humi1 = gson.fromJson(type1, EmpFile.class);
//                            empFileList.add(humi1);
//                        }
//
//
                        Message message = new Message();
                        message.what = MESSAGE_SET_TYPE;
                        message.obj = jsonObject.get("errMessage").getAsString();
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

    //查看員工申請信息
    private void getPersonInfo(String sid) {

        showDialog();
        final String url = Constants.HTTP_VIEW_BY_ID + "?sid=" + sid;

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForGet(url);
                Log.e("---------", "result==fff===" + result);

                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonObject object = jsonObject.get("data").getAsJsonObject();

                        JsonArray array = object.get("body1").getAsJsonArray();
                        bodyOne = new ArrayList<BodyONE>();

                        for (JsonElement type : array) {
                            BodyONE humi = gson.fromJson(type, BodyONE.class);
                            bodyOne.add(humi);
                        }

                        if (from.equals("storeQr")) {
                            Message message = new Message();
                            message.what = MESSAGE_SET_STORE_QR;
                            message.obj = object;
                            mHandler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.what = MESSAGE_SET_GOOD_LIST;
                            message.obj = object;
                            mHandler.sendMessage(message);
                        }

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

    //獲取倉庫
    private void getStore(String sid, String zwuser) {

        String name = null;
        try {
            name = URLEncoder.encode(URLEncoder.encode(zwuser, "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String url = Constants.HTTP_STORE_INFO + "?sid=" + sid + "&zwuser=" + name;

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
                        storeList = new ArrayList<IGMessage>();

                        for (JsonElement type : array) {
                            IGMessage humi = gson.fromJson(type, IGMessage.class);
                            storeList.add(humi);

                        }
//
//                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
//                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();
//                        empFileList = new ArrayList<EmpFile>();
//                        for (JsonElement type1 : array1) {
//                            EmpFile humi1 = gson.fromJson(type1, EmpFile.class);
//                            empFileList.add(humi1);
//                        }
//
//
                        Message message = new Message();
                        message.what = MESSAGE_SET_SPINNER;
                        message.obj = jsonObject.get("errMessage").getAsString();
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

    //獲取倉庫 儲位
    private void getStoreView(String sid, String shcode) {
        showDialog();

        final String url = Constants.HTTP_LOCATION_INFO + "?s_id=" + sid + "&sh_code=" + shcode;

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
                        deposit = new ArrayList<>();
//
                        for (JsonElement type : array) {
                            String humi = gson.fromJson(type, String.class);
                            deposit.add(humi);
                        }
//
//                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
//                        JsonArray array1 = jsonObject1.get("file").getAsJsonArray();
//                        empFileList = new ArrayList<EmpFile>();
//                        for (JsonElement type1 : array1) {
//                            EmpFile humi1 = gson.fromJson(type1, EmpFile.class);
//                            empFileList.add(humi1);
//                        }

//
                        Message message = new Message();
                        message.what = MESSAGE_SET_STORE_LIST;
                        message.obj = jsonObject.get("errMessage").getAsString();
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

    //提交儲位排配
    private void upStoreView() {
        final String url = Constants.HTTP_DEPOSIT_OK;
        JsonObject object = new JsonObject();
        if (from.equals("storeQr")) {
            object.addProperty("s_id", sID);
        } else {
            object.addProperty("s_id", peopleMsg.getS_ID());
        }
        object.addProperty("sh_code", store);
        object.addProperty("s_zw_depositer", FoxContext.getInstance().getName());

        List<String> id = new ArrayList<>();
        for (int i = 0; i < bodyOne.size(); i++) {

            Log.e("------","bodyOne.=="+i);
            for (int j = 0; j < Integer.parseInt(bodyOne.get(i).getS_DEPOSIT_COUNT()); j++) {
                id.add(bodyOne.get(i).getID());
                Log.e("------","bodyOne.get(j).getID()=="+j);
            }
        }

        JsonArray array = new JsonArray();
        for (int i = 0; i < deposit.size(); i++) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", id.get(i));
            jsonObject.addProperty("sl_code", deposit.get(i));
            array.add(jsonObject);
        }

        object.add("body", array);
        JsonArray photoArray = new JsonArray();

        if (imagePaths != null && imagePaths.size() != 0) {
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
        } else {
            ToastUtils.showShort(IGMainActivity.this, "請拍照！");
        }
        object.add("photo", photoArray);
        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
                    Log.e("---------", "==fff===" + url);
                    String result = HttpConnectionUtil.doPostJsonObject(url, object);
                    Log.e("----------", "obj===" + object.toString());
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
                    FileUtil.deletePhotos(IGMainActivity.this);
                }
            }
        }.start();

    }

    //提交領取
    private void upLeaveView() {
        final String url = Constants.HTTP_ISSUE_OK;
        JsonObject object = new JsonObject();
        object.addProperty("s_id", getIntent().getStringExtra("id"));
        object.addProperty("s_zw_issuer", FoxContext.getInstance().getName());
        object.addProperty("s_agenter_id", etDId.getText().toString());
        object.addProperty("s_agenter_name", etDName.getText().toString());
        object.addProperty("s_flag", done);

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                //把网络访问的代码放在这里
                try {
                    showDialog();
                    Log.e("---------", "==fff===" + url);
                    String result = HttpConnectionUtil.doPostJsonObject(url, object);
                    Log.e("----------", "obj===" + object.toString());
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
                    FileUtil.deletePhotos(IGMainActivity.this);
                }
            }
        }.start();

    }


    //領取功能詳情
    private void getLeaveInfo(String sid) {

        showDialog();
        final String url = Constants.HTTP_VIEW_BY_ID + "?sid=" + sid;

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
                        JsonObject object = jsonObject.get("data").getAsJsonObject();

                        JsonArray array = object.get("body1").getAsJsonArray();
                        bodyOne = new ArrayList<BodyONE>();

                        for (JsonElement type : array) {
                            BodyONE humi = gson.fromJson(type, BodyONE.class);
                            bodyOne.add(humi);
                        }


                        Message message = new Message();
                        message.what = MESSAGE_SET_GOOD_LIST;
                        message.obj = object;
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

    private List<IGMessage> igMessages;
    private IGListAdapter igListAdapter;

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
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rtb_yes:
                done = "Y";
                trDId.setVisibility(View.GONE);
                trDName.setVisibility(View.GONE);
                break;
            case R.id.rtb_no:
                done = "N";
                trDId.setVisibility(View.VISIBLE);
                trDName.setVisibility(View.VISIBLE);
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
                Glide.with(IGMainActivity.this)
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

    private String store;

    public void setSpinner() {
        stores = new ArrayList<>();
        stores.add("請選擇倉庫");
        for (int i = 0; i < storeList.size(); i++) {
            stores.add(storeList.get(i).getSh_code());
        }
        spStore.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stores));
        spStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                store = stores.get(position);
                if (store.equals("請選擇倉庫")) {

                } else {
                    if (from.equals("storeQr")) {
                        getStoreView(sID, store);
                    } else {
                        getStoreView(peopleMsg.getS_ID(), store);
                    }
                }
//                Log.e("---------", "最喜欢的水果是：" + str);
//                paramMap.put("kedui",str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
