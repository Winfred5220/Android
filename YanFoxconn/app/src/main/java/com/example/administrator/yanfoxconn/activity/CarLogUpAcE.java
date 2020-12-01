package com.example.administrator.yanfoxconn.activity;

import android.app.TimePickerDialog;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
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

import org.json.JSONArray;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 提交行車日誌界面 日期控件簡化
 * Created by song on 2017/12/11.
 */

public class CarLogUpAcE extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_SET_TEXT = 1;//tv賦值

    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    private ArrayList<String> imagePaths = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPaths = new ArrayList<String>();//圖片壓縮后地址
    private ImageCaptureManager captureManager; // 相机拍照处理类

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_driver)
    TextView tvDriver;//司機
    @BindView(R.id.tv_code)
    TextView tvCode;//車牌號
    @BindView(R.id.et_start_add)
    EditText etStartAdd;//起點
    @BindView(R.id.et_mid_add)
    EditText etMidAdd;//途徑
    @BindView(R.id.et_end_add)
    EditText etEndAdd;//終點
    @BindView(R.id.tv_start_date)
    TextView tvStartDate;//起始日期
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;//起始時間
    @BindView(R.id.et_start_xc)
    EditText etStartXc;//起始里程
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;//結束時間
    @BindView(R.id.tv_end_date)
    TextView tvEndDate;//結束
    @BindView(R.id.cb_end_date_yesterday)
    CheckBox cbEndDateYesday;//
    @BindView(R.id.cb_end_date_today)
    CheckBox cbEndDateToday;//
    @BindView(R.id.tr_layout)
    LinearLayout ll;
    @BindView(R.id.et_end_xc)
    EditText etEndXc;//結束里程
    @BindView(R.id.et_road_fee)
    EditText etRoadFee;//路橋費
    @BindView(R.id.et_part_fee)
    EditText etPartFee;//停車費
    @BindView(R.id.et_delay_eat)
    EditText etDelayEat;//誤餐費
    @BindView(R.id.et_remark_xc)
    EditText etRemarkXc;//備註
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;//空白图片占位
    @BindView(R.id.gv_photo)
    GridView gridView;//图片显示区域
    private GridAdapter gridAdapter;


    private String date;//提交日誌日期,最大值
    private String id;//主鍵Id
    private TimeDatePickerDialog timeDatePickerDialog;
    private String initStartDateTime; // 初始化开始时间
    private String initToMax;//初始最大日期

    private String minDate;//最小日期
    private String maxDate;//最大日期

    //    private Date startTime, endTime;//比較起始和結束時間大小
    private String startDate;//開始時間
    private String endDateYe;//結束時間,昨日
    private String endDate;//結束時間
    private String getEndDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_log_upe);
        ButterKnife.bind(this);
//        timeDatePickerDialog = new TimeDatePickerDialog(CarLogUpAcE.this);

        tvTitle.setText("日誌提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setText("提交");

        ivEmpty.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        cbEndDateYesday.setOnClickListener(this);
        cbEndDateToday.setOnClickListener(this);

        tvDriver.setText(FoxContext.getInstance().getName());
        tvCode.setText(FoxContext.getInstance().getDep());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        initStartDateTime = formatterUse.format(curDate);
        initToMax = formatterDate.format(curDate);
//        tvStartTime.setText(formatter.format(curDate));
//        tvEndTime.setText(formatter.format(curDate));

        Bundle bundle = this.getIntent().getExtras();
        date = bundle.getString("date");
        id = bundle.getString("id");


        if (FoxContext.getInstance().getLogDate().equals("-1")) {//昨天
            startDate = date;
            endDateYe = date;
            endDate = initToMax;
            tvStartDate.setText(date);
            getEndDate = date;
            cbEndDateYesday.setText(date);
            cbEndDateYesday.setChecked(true);
            cbEndDateToday.setText(initToMax);
//            minDate = date;
//            maxDate = initToMax;
            Log.e("-----------------", "startDate==" + startDate + "==endDateYe==" + endDateYe + "==endDate==" + initToMax);
        } else {//今天
            startDate = date;
            endDate = date;
            tvStartDate.setText(date);
            tvEndDate.setVisibility(View.VISIBLE);
            tvEndDate.setText(date);
            ll.setVisibility(View.GONE);
            cbEndDateYesday.setVisibility(View.GONE);
            getEndDate = date;
//            minDate = date;
//            maxDate = date;
            Log.e("-----------------", "startDate==" + startDate + "==endDate==" + endDate);
        }

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 3 ? 3 : cols;
        gridView.setNumColumns(cols);

        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePaths.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(CarLogUpAcE.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(1); // 最多选择照片数量，默认为1
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(CarLogUpAcE.this);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.iv_empty:
                PhotoPickerIntent intent = new PhotoPickerIntent(CarLogUpAcE.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(1); // 最多选择照片数量，默认为1
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
            case R.id.btn_title_right://提交
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//                try {
//                    startTime = dateFormat.parse(tvStartTime.getText().toString());
//                    endTime = dateFormat.parse(tvEndTime.getText().toString());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }

                if (FoxContext.getInstance().getLoginId().equals("")) {
                    ToastUtils.showShort(this, "司機信息獲取失敗,請重新登陸");
                } else if (etStartAdd.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "起點為空");
                } else if (etEndAdd.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "終點為空");
                } else if (tvStartTime.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "起始時間為空");
                } else if (etStartXc.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "起始里程為空");
                } else if (tvEndTime.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "結束時間為空");
                } else if (etEndXc.getText().toString().equals("")) {
                    ToastUtils.showShort(this, "結束里程為空");
                } else
//                    if (startTime.getTime() >= endTime.getTime()) {
//                    ToastUtils.showShort(this, "起始時間需小於結束時間");
//                } else
                    if (Integer.parseInt(etStartXc.getText().toString()) >= Integer.parseInt(etEndXc.getText().toString())) {
                        ToastUtils.showShort(this, "注意起始'\'結束里程數據錯誤");
                    } else if (!etPartFee.getText().toString().equals("") && imagePaths == null) {
                        ToastUtils.showShort(this, "停車費請提交發票圖片");
                    } else if (!etRoadFee.getText().toString().equals("") && imagePaths == null) {
                        ToastUtils.showShort(this, "過橋費請提交發票圖片");
                    }
//                    else if(){}
                    else {
                        normal2();
                    }
                break;
            case R.id.tv_start_time:
//                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
//                        CarLogUpAcE.this, initStartDateTime);
//                if (FoxContext.getInstance().getLogDate().equals("-1")) {
//                    dateTimePicKDialog.dateTimePicKDialog(tvStartTime, minDate, minDate);
//                } else {
//
//                    dateTimePicKDialog.dateTimePicKDialog(tvStartTime, minDate, maxDate);
//                }
                showTimePickerDialog("s");
                break;
            case R.id.tv_end_time:
//                DateTimePickDialogUtil dateTimePicKDialog1 = new DateTimePickDialogUtil(
//                        CarLogUpAcE.this, initStartDateTime);
//
//                dateTimePicKDialog1.dateTimePicKDialog(tvEndTime, minDate, maxDate);
                showTimePickerDialog("e");

                break;
            case R.id.cb_end_date_today:
                cbEndDateYesday.setChecked(false);
                getEndDate = cbEndDateToday.getText().toString();
                break;
            case R.id.cb_end_date_yesterday:
                cbEndDateToday.setChecked(false);
                getEndDate = cbEndDateYesday.getText().toString();
                break;

        }
    }

    private Calendar mCalendar;
    private String time;

    private void showTimePickerDialog(final String type) {
        mCalendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(CarLogUpAcE.this,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                mCalendar.set(Calendar.HOUR, i);
                mCalendar.set(Calendar.MINUTE, i1);

//                SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
//                Toast.makeText(getApplicationContext(), "" + format.format(mCalendar.getTime()), Toast.LENGTH_SHORT).show();
                time = i + ":" + i1;
                Log.e("--------", "time==" + i + ":" + i1);
                if (type.equals("s")) {
                    tvStartTime.setText(time);
                } else {
                    tvEndTime.setText(time);
                }
            }
        }, mCalendar.get(Calendar.HOUR), mCalendar.get(Calendar.MINUTE), true);
        dialog.show();

    }
//    //时间选择器----------确定
//    @Override
//    public void positiveListener() {
//        int hour = timeDatePickerDialog.getYear();
//        int minute = timeDatePickerDialog.getMinute();
//
//        tvStartTime.setText(timeDatePickerDialog.getTimeDate());
//        tvEndTime.setText(timeDatePickerDialog.getTimeDate());
//    }
//
//    //时间选择器-------取消
//    @Override
//    public void negativeListener() {
//        if (timeDatePickerDialog.getTimeDate().equals("")) {
//            ToastUtils.showShort(this, "請選擇時間");
//        }
//    }

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
                Glide.with(CarLogUpAcE.this)
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

    public void normal2() {
        if (FoxContext.getInstance().getLoginId().equals("")) {
            ToastUtils.showShort(this, "登錄超時,請重新登陸");
        }
        final String url = Constants.HTTP_CAR_RECORD_INSERT; //此處寫上自己的URL

        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡

        paramMap.put("xc_date", date);
        paramMap.put("car_driver_id", FoxContext.getInstance().getLoginId());
        paramMap.put("id", id);
        paramMap.put("start_add", etStartAdd.getText().toString());
        paramMap.put("mid_add", etMidAdd.getText().toString());
        paramMap.put("end_add", etEndAdd.getText().toString());
        paramMap.put("start_time", tvStartDate.getText().toString() + " " + tvStartTime.getText().toString());
        paramMap.put("xc_start", etStartXc.getText().toString());
        paramMap.put("end_time", getEndDate + " " + tvEndTime.getText().toString());
        paramMap.put("xc_end", etEndXc.getText().toString());
        paramMap.put("road_fee", etRoadFee.getText().toString());
        paramMap.put("part_fee", etPartFee.getText().toString());
        paramMap.put("delay_eat", etDelayEat.getText().toString());
        paramMap.put("xc_remark", etRemarkXc.getText().toString());

//        String path = "********"; //此處寫上要上傳的檔在系統中的路徑
//        final File pictureFile = new File(path); //通過路徑獲取檔

        final Map<String, File> fileMap = new HashMap<String, File>(); //檔全部添加到Map裡
        if (!(imagePaths == null)) {
            for (int i = 0; i < imagePaths.size(); i++) {
                File pictureFile = new File(imagePaths.get(i)); //通過路徑獲取檔
//            fileMap.put("file" + i, pictureFile);//添加第一個文件
                final String pic_path = imagePaths.get(i);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
                //getFilesDir().getAbsolutePath()+"compressPic.jpg";
                //调用压缩图片的方法，返回压缩后的图片path
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 30);
                final File compressedPic = new File(compressImage);
                imgZipPaths.add(i, compressImage);
                Log.e("----com---", compressImage);
                if (compressedPic.exists()) {
                    Log.e("-------------", "图片压缩上传");
                    //  uploadFileByOkHTTP(context, actionUrl, compressedPic);
//                showDialog("111"+compressImage);
                    fileMap.put("file" + i, compressedPic);//添加第一個文件

                } else {//直接上传
                    // uploadFileByOkHTTP(context, actionUrl, pictureFile);
//                showDialog("222"+entryFile.getValue());
                    fileMap.put("file" + i, pictureFile);
                }
            }
        }

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                try {
                    showDialog();
                    String b = HttpConnectionUtil.doPostPictureLog(url, paramMap, fileMap);
                    Log.e("---------", "==fff===" + url);
                    if (b != null) {
                        dismissDialog();
                        Log.e("---------", "==fff===" + b);
                        if (b.equals("0")) {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = "提交成功";
                            mHandler.sendMessage(message);
                            finish();
                        } else if (b.equals("1")) {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = "行駛時間與之前記錄衝突";
                            mHandler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = "未知錯誤,提交失敗,聯繫電話:37066";
                            mHandler.sendMessage(message);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    FileUtil.deletePhotos(CarLogUpAcE.this);
                }
            }
        }.start();
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showLong(CarLogUpAcE.this, msg.obj.toString());
                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值
//                    setText();
                    break;

            }
            super.handleMessage(msg);
        }
    };


    /**
     * 獲取日期
     *
     * @param date 當前系統時間
     * @param num  0當天時間;-1昨天時間
     * @return
     */
    public static String getDay(Date date, int num) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, num);
        date = calendar.getTime();
        String str = formatter.format(date);

        return str;
    }

}
