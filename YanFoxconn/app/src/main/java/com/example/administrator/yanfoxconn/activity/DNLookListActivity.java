package com.example.administrator.yanfoxconn.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.AbnormalListAdapter;
import com.example.administrator.yanfoxconn.adapter.DNCheckListAdapter;
import com.example.administrator.yanfoxconn.adapter.DNLookListAdapter;
import com.example.administrator.yanfoxconn.bean.DNCheckMessage;
import com.example.administrator.yanfoxconn.bean.FileName;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 宿舍查驗 查看列表界面
 * Created by song on 2020/4/28. 08:18
 */
public class DNLookListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_UPLOAD = 1;
    private final int MESSAGE_SEE_ABNORMAL = 3;

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.et_start)
    EditText etStart;//開始時間
    @BindView(R.id.et_end)
    EditText etEnd;//結束時間
    @BindView(R.id.btn_check)
    Button btnCheck;//查看
    @BindView(R.id.lv_look)
    ListView lvLook;


    private String[] qrResult;//宿舍信息
    private DNCheckMessage dnCheckMessage;//人員信息
    private String flag;//提交類型
    private String result;//接口返回結果
    private String url = null;
    private List<DNCheckMessage> dnCheckMessageList;//查驗詳情信息
    private DNLookListAdapter dnLookListAdapter;
    private List<FileName> fileNameList;//圖片名稱
    Bitmap image = null;

    private Date dateStart, dateEnd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dn_look_listl);
        ButterKnife.bind(this);

        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);

        btnCheck.setOnClickListener(this);
        lvLook.setOnItemClickListener(this);
        dnCheckMessageList = new ArrayList<DNCheckMessage>();
        etStart.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showDateStart();
                    return true;
                }
                return false;
            }
        });
        etStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDateStart();
                }
            }
        });
        etEnd.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showDateEnd();
                    return true;
                }
                return false;
            }
        });
        etEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDateEnd();
                }
            }
        });
        qrResult = getIntent().getStringExtra("qrResult").split(",");
        flag = getIntent().getStringExtra("flag");
        if (flag.equals("R")) {
            dnCheckMessage = (DNCheckMessage) getIntent().getSerializableExtra("dnCheckMessageList");
            tvTitle.setText("人員信息查看");
        } else {
            tvTitle.setText("環境信息查看");
        }
        checkInput();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()) {
            case R.id.lv_look://列表點擊事件
                Intent intent = new Intent(DNLookListActivity.this, DNLookCheckActivity.class);
                intent.putExtra("flag", flag);
                intent.putExtra("jcId", dnCheckMessageList.get(position).getJc_id());
                intent.putExtra("dnCheckMessageList", (Serializable) dnCheckMessageList.get(position));
                intent.putExtra("photoList",(Serializable)fileNameList);
                startActivity(intent);
                break;
        }
    }

    Calendar calendar1 = Calendar.getInstance();
    Calendar calendar2 = Calendar.getInstance();
    DatePickerDialog datePickerDialog1;
    DatePickerDialog datePickerDialog2;

    protected void showDateStart() {

        datePickerDialog1 = new DatePickerDialog(DNLookListActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int mon = monthOfYear + 1;
                etStart.setText(year + "-" + mon + "-" + dayOfMonth);
            }
        }, calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH));
        datePickerDialog1.show();

    }

    protected void showDateEnd() {

        datePickerDialog2 = new DatePickerDialog(DNLookListActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int mon = monthOfYear + 1;
                etEnd.setText(year + "-" + mon + "-" + dayOfMonth);
            }
        }, calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH));
        datePickerDialog2.show();

    }

    public void checkInput() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {

            Log.e("---------------", "checkInput1");
            if (etEnd.getText().toString().equals("") && etStart.getText().toString().equals("")) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
                formatter.setTimeZone(TimeZone.getTimeZone("GMT+08")); //设置时区
                Date curDate = new Date(System.currentTimeMillis()); //获取当前时间
                String createDate = formatter.format(curDate);   //格式转换
                String beforeDate = getPastDate(10);
                etEnd.setText(createDate);
                etStart.setText(beforeDate);
                getList();
                Log.e("---------------", "checkInput2");
            } else {
                dateStart = dateFormat.parse(etStart.getText().toString());
                dateEnd = dateFormat.parse(etEnd.getText().toString());
                if (dateStart.getTime() > dateEnd.getTime()) {
                    dateStart = dateFormat.parse(etStart.getText().toString());
                    dateEnd = dateFormat.parse(etEnd.getText().toString());
                    Toast.makeText(DNLookListActivity.this, "开始时间需要小于结束时间哦~~",
                            Toast.LENGTH_SHORT).show();
                } else {
                    getList();
                }
            }
//            else if (dateStart.getTime() > new Date().getTime()) {
//                Toast.makeText(DNLookListActivity.this, "开始时间大于现在时间哦~~",
//                        Toast.LENGTH_SHORT).show();
//                return false;
//            } else if (dateEnd.getTime() > new Date().getTime()) {
//                Toast.makeText(DNLookListActivity.this, "结束时间大于现在时间哦~~",
//                        Toast.LENGTH_SHORT).show();
//                return false;
//            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(DNLookListActivity.this, "数据格式有误！", Toast.LENGTH_SHORT)
                    .show();

        }

    }

    public void getList() {
        showDialog();
        String room = null;//房間
        String building = null;//棟
        String area = null;//區
        try {
            area = URLEncoder.encode(URLEncoder.encode(qrResult[0], "UTF-8"), "UTF-8");
            building = URLEncoder.encode(URLEncoder.encode(qrResult[1].toString(), "UTF-8"), "UTF-8");
            room = URLEncoder.encode(URLEncoder.encode(qrResult[3], "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (flag.equals("R")) {
            url = Constants.HTTP_SUSHE_VIEW_SERVLET + "?area=" + area + "&building=" + building + "&room=" + room + "&bed=" + dnCheckMessage.getREGULAREMP_BEDNUM() + "&flag=" + "R" + "&start_date=" + etStart.getText().toString() + "&end_date=" + etEnd.getText().toString();
        } else {
            url = Constants.HTTP_SUSHE_VIEW_SERVLET + "?area=" + area + "&building=" + building + "&room=" + room + "&bed=" + "" + "&flag=" + "H" + "&start_date=" + etStart.getText().toString() + "&end_date=" + etEnd.getText().toString();

        }
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                result = HttpUtils.queryStringForPost(url);
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                String response = result;
                dismissDialog();
                if (result != null) {
                    Log.e("---------", "==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        dnCheckMessageList = new ArrayList<DNCheckMessage>();
                        for (JsonElement type : array) {
                            DNCheckMessage humi = gson.fromJson(type, DNCheckMessage.class);
                            dnCheckMessageList.add(humi);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SEE_ABNORMAL;
                        message.obj = dnCheckMessageList;
                        mHandler.sendMessage(message);

                        JsonObject jsonObject1 = new JsonParser().parse(String.valueOf(array.get(0))).getAsJsonObject();
                        JsonArray array1 = jsonObject1.get("fileName").getAsJsonArray();
                        fileNameList = new ArrayList<FileName>();
                        for (JsonElement type1 : array1) {
                            FileName humi1 = gson.fromJson(type1, FileName.class);
                            fileNameList.add(humi1);

                        }

//                        for (int i = 0; i < fileNameList.size(); i++) {
//                            Log.e("-----fileNameList----", fileNameList.get(i).getExce_filename1().toString());
////                            stringVoidBitmapAsyncTask.execute(fileNameList.get(i).getExce_filename1());
//                            image = getBitmapFromServer(fileNameList.get(i).getExce_filename1());
//
//                            Message messageFile = new Message();
//                            messageFile.what = MESSAGE_SHOW_IMAGE;
//                            messageFile.obj = image;
//                            mHandler.sendMessage(messageFile);
//                        }
                    } else if (errCode.equals("400")) {
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = "此時間段無查驗數據!";
                        mHandler.sendMessage(message);
                        dnCheckMessageList.clear();
                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    mHandler.sendMessage(message);
                    message.obj = "請求不成功";
//                    ToastUtils.showShort(DNCheckListActivity.this, "請求不成功");
                }
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showShort(DNLookListActivity.this, msg.obj.toString());
                    dnLookListAdapter = new DNLookListAdapter(DNLookListActivity.this, dnCheckMessageList, flag);
                    lvLook.setAdapter(dnLookListAdapter);
                    break;

                case MESSAGE_SEE_ABNORMAL://列表賦值
                    dnLookListAdapter = new DNLookListAdapter(DNLookListActivity.this, dnCheckMessageList, flag);
                    lvLook.setAdapter(dnLookListAdapter);
                    setListViewHeightBasedOnChildren(lvLook);
                    break;

            }
            super.handleMessage(msg);
        }
    };

    /**
     * 用於解決scrollview嵌套listview導致只顯示一條數據的問題
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        DNLookListAdapter listAdapter = (DNLookListAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_check:
                Log.e("---------------", "checkInput");
                checkInput();
                break;
            case R.id.btn_title_left:
                finish();
                break;
        }
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        Log.e(null, result);
        return result;
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkInput();
    }
}
