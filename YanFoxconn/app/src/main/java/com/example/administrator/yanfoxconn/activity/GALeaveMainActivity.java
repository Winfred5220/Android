package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.EmpFile;
import com.example.administrator.yanfoxconn.bean.EmpMessage;
import com.example.administrator.yanfoxconn.bean.GAWork;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.DateTimePickDialogUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.CustomTimePickerDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description 总务临时工请假主界面
 * @Author song
 * @Date 5/27/21 8:41 AM
 */
public class GALeaveMainActivity extends BaseActivity implements View.OnClickListener{

    private final int MESSAGE_TOAST = 2;//showToast
    private final int MESSAGE_SET_TEXT = 1;//獲取狀態后,設置控件內容
    private final int MESSAGE_NOT_NET = 3;//網絡問題
    private final int MESSAGE_UP = 4;//提交响应


    private String initStartDateTime; // 初始化开始时间
    private Date selectTime = null;//所選時間
    private Date curDate = null;//當前時間
    private String curDates;//當前時間
    private SimpleDateFormat formatter;

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交
    @BindView(R.id.tv_id)
    TextView tvId;//工号
    @BindView(R.id.tv_name)
    TextView tvName;//姓名
    @BindView(R.id.tv_gname)
    TextView tvGName;//课组
    @BindView(R.id.tv_gpost)
    TextView tvGPost;//岗位
    @BindView(R.id.tv_start_date)
    TextView tvStartDate;//开始時間
    @BindView(R.id.tv_end_date)
    TextView tvEndDate;//结束時間
    @BindView(R.id.tv_time)
    TextView tvTime;//请假时长
    @BindView(R.id.tv_class)
    TextView tvClass;//班别
    @BindView(R.id.et_reason)
    EditText etReason;//请假原因

    private List<GAWork> gaWorks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ga_leave_main);
        ButterKnife.bind(this);

        tvTitle.setText("请假界面");
        btnBack.setText("返回");
        btnUp.setText("提交");
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatterUse = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        initStartDateTime = formatterUse.format(curDate);
        tvStartDate.setText(formatter.format(curDate));
        tvEndDate.setText(formatter.format(curDate));
        curDates = formatter.format(curDate);

        getMessage(FoxContext.getInstance().getLoginId(),FoxContext.getInstance().getType());
    }

    //獲取临时工信息
    private void getMessage(String creatorId,String type){
        showDialog();
        final String url = Constants.HTTP_ZW_LEAVA_PEOPLE_MSG+"?creator_id="+creatorId+"&type="+type;

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
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        gaWorks = new ArrayList<GAWork>();

                        for (JsonElement type : array) {
                            GAWork humi = gson.fromJson(type, GAWork.class);
                            gaWorks.add(humi);
                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else{
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            } }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

                    ToastUtils.showLong(GALeaveMainActivity.this, msg.obj.toString());
//                    finish();
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
    private void setText(){
        tvId.setText(gaWorks.get(0).getP_empId());
        tvName.setText(gaWorks.get(0).getP_empName());
        tvGName.setText(gaWorks.get(0).getG_name());
        tvGPost.setText(gaWorks.get(0).getG_post());
        String classType="";
        if (gaWorks.get(0).getC_type().equals("D")){
            classType="白班";
        }else{
            classType="夜班";}
        if (gaWorks.get(0).getC_up_start().equals(gaWorks.get(0).getC_down_start())){

            tvClass.setText(classType+"\u3000\u3000"+gaWorks.get(0).getC_up_start()+"-"+gaWorks.get(0).getC_up_end());
        }else{

            tvClass.setText(classType+"\u3000\u3000"+gaWorks.get(0).getC_up_start()+"-"+gaWorks.get(0).getC_up_end()+"\u3000\u3000"+gaWorks.get(0).getC_down_start()+"-"+gaWorks.get(0).getC_down_end());
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start_date:

                DateTimePickDialogUtil dateTimeS = new DateTimePickDialogUtil(
                        GALeaveMainActivity.this, initStartDateTime);

                dateTimeS.dateTimePicKDialog30(tvStartDate, "", "");
                break;
            case R.id.tv_end_date:

                DateTimePickDialogUtil dateTimeE = new DateTimePickDialogUtil(
                        GALeaveMainActivity.this, initStartDateTime);

                dateTimeE.dateTimePicKDialog30(tvEndDate, "", "");
                break;
        }
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
