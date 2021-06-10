package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.EmpFile;
import com.example.administrator.yanfoxconn.bean.EmpMessage;
import com.example.administrator.yanfoxconn.bean.GAWork;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.DateTimePickDialogUtil;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ImageZipUtils;
import com.example.administrator.yanfoxconn.utils.TimeDateUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.CustomTimePickerDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.text.ParseException;
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

    private String upStartTime,upEndTime,downStartTime,downEndTime="";//班别 上午开始，上午结束，下午开始，下午结束 时间
    private Boolean isAllDay=true;//班别是否是全天，true全天，false半天
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
        etReason.setOnClickListener(this);

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
                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());

                    break;
                case MESSAGE_NOT_NET://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

                    ToastUtils.showLong(GALeaveMainActivity.this, "网络问题请稍后重试！");
//                    finish();
                    break;
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
            classType="夜班";
        }
        if (gaWorks.get(0).getC_up_start().equals(gaWorks.get(0).getC_down_start())){
            isAllDay=false;
            tvClass.setText(classType+"\u3000\u3000"+gaWorks.get(0).getC_up_start()+"-"+gaWorks.get(0).getC_up_end());
            upStartTime = gaWorks.get(0).getC_up_start();
            upEndTime = gaWorks.get(0).getC_up_end();
        }else{
            isAllDay=true;
            tvClass.setText(classType+"\u3000\u3000"+gaWorks.get(0).getC_up_start()+"-"+gaWorks.get(0).getC_up_end()+"\u3000\u3000"+gaWorks.get(0).getC_down_start()+"-"+gaWorks.get(0).getC_down_end());
            upStartTime = gaWorks.get(0).getC_up_start();
            upEndTime = gaWorks.get(0).getC_up_end();
            downStartTime = gaWorks.get(0).getC_down_start();
            downEndTime = gaWorks.get(0).getC_down_end();
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
            case R.id.btn_title_right://提交
                upLeave();
                break;
            case R.id.btn_title_left:
                 finish();
                 break;
            case R.id.et_reason:
                try {
                    getLeaveTime(tvStartDate.getText().toString(),tvEndDate.getText().toString(),upStartTime,upEndTime,downStartTime,downEndTime,curDates);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.e("---------","etreason");
                break;
        }
    }

    /**
     * 结算请假时间
     * @param startDate      请假开始日期时间
     * @param endDate        请假结束日期时间
     * @param upStartTime    班别上午开始时间
     * @param upEndTime      班别上午结束时间
     * @param downStartTime  班别下午开始时间
     * @param downEndTime    班别下午结束时间
     * @param nowDate        当前时间日期
     */
    private void getLeaveTime(String startDate,String endDate,String upStartTime,String upEndTime,String downStartTime,String downEndTime,String nowDate) throws ParseException {
        Float hours=null;
        if (isAllDay){//全天
            if (getIsToday(startDate,endDate)){//一天
                 Log.e("-----------","全-");
                 if (TimeDateUtils.getTimeLessEqual(getTime(startDate),upStartTime)&&TimeDateUtils.getTimeGreater(getTime(endDate),upStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),upEndTime)){
                     tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(upStartTime,getTime(endDate))));
                 }else if (TimeDateUtils.getTimeLessEqual(getTime(startDate),upStartTime)&&TimeDateUtils.getTimeGreater(getTime(endDate),upEndTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),downStartTime)){
                     tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(upStartTime,upEndTime)));
                 }else if (TimeDateUtils.getTimeLessEqual(getTime(startDate),upStartTime)&&TimeDateUtils.getTimeGreater(getTime(endDate),downStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),downEndTime)){
                     tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(upStartTime,upEndTime)+TimeDateUtils.getTimeHours(downStartTime,getTime(endDate))));
                 }else if(TimeDateUtils.getTimeLessEqual(getTime(startDate),upStartTime)&&TimeDateUtils.getTimeGreater(getTime(endDate),downEndTime)){
                     tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(upStartTime,upEndTime)+TimeDateUtils.getTimeHours(downStartTime,downEndTime)));
                 }else if (TimeDateUtils.getTimeGreater(getTime(startDate),upStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(startDate),upEndTime)&&TimeDateUtils.getTimeGreater(getTime(endDate),upStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),upEndTime)){
                     tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(getDate(startDate),getTime(endDate))));
                 }else if (TimeDateUtils.getTimeGreater(getTime(startDate),upStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(startDate),upEndTime)&&TimeDateUtils.getTimeGreater(getTime(endDate),upEndTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),downStartTime)){
                     tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(getDate(startDate),upEndTime)));
                 }else if (TimeDateUtils.getTimeGreater(getTime(startDate),upStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(startDate),upEndTime)&&TimeDateUtils.getTimeGreater(getTime(endDate),downStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),downEndTime)){
                     tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(getDate(startDate),upEndTime)+TimeDateUtils.getTimeHours(downStartTime,getDate(endDate))));
                 }else if (TimeDateUtils.getTimeGreater(getTime(startDate),upStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(startDate),upEndTime)&&TimeDateUtils.getTimeGreater(getTime(endDate),downEndTime)){
                     tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(getDate(startDate),upEndTime)+TimeDateUtils.getTimeHours(downStartTime,downEndTime)));
                 }else if (TimeDateUtils.getTimeGreater(getTime(startDate),upEndTime)&&TimeDateUtils.getTimeLessEqual(getTime(startDate),downStartTime)&&TimeDateUtils.getTimeGreater(getTime(endDate),downStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),upEndTime)){
                     tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(downStartTime,getTime(endDate))));
                 }else if (TimeDateUtils.getTimeGreater(getTime(startDate),upEndTime)&&TimeDateUtils.getTimeLessEqual(getTime(startDate),downStartTime)&&TimeDateUtils.getTimeGreater(getTime(endDate),downEndTime)){
                     tvTime.setText(String.valueOf( TimeDateUtils.getTimeHours(downStartTime,downEndTime)));
                 }else if (TimeDateUtils.getTimeGreater(getTime(startDate),downStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(startDate),downEndTime)&&TimeDateUtils.getTimeGreater(getTime(endDate),downStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),upEndTime)){
                     tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(getTime(startDate),getTime(endDate))));
                 }else if (TimeDateUtils.getTimeGreater(getTime(startDate),downStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(startDate),downEndTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),upEndTime)){
                     tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(getTime(startDate),downEndTime)));
                 }else{
                     ToastUtils.showShort(GALeaveMainActivity.this,"请假时间选择有误，请重新确认！");
                 }
            }else{//跨天
                 Log.e("-----------","全跨");
                 if (TimeDateUtils.getTimeLessEqual(getTime(startDate),upStartTime)){
                     hours=TimeDateUtils.getTimeHours(upStartTime,upEndTime)+TimeDateUtils.getTimeHours(downStartTime,downEndTime)+getQuantianKuatian(endDate, upStartTime, upEndTime, downStartTime, downEndTime);
                 }else if (TimeDateUtils.getTimeGreater(getTime(startDate),upStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(startDate),upEndTime)){
                     hours=TimeDateUtils.getTimeHours(getTime(startDate),upEndTime)+TimeDateUtils.getTimeHours(downStartTime,downEndTime)+getQuantianKuatian(endDate, upStartTime, upEndTime, downStartTime, downEndTime);
                 }else if (TimeDateUtils.getTimeGreater(getTime(startDate),upEndTime)&&TimeDateUtils.getTimeLessEqual(getTime(startDate),downStartTime)){
                     hours=TimeDateUtils.getTimeHours(downStartTime,downEndTime)+getQuantianKuatian(endDate, upStartTime, upEndTime, downStartTime, downEndTime);
                 }else if (TimeDateUtils.getTimeGreater(getTime(startDate),downStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(startDate),downEndTime)){
                     hours=TimeDateUtils.getTimeHours(getTime(startDate),downEndTime)+getQuantianKuatian(endDate, upStartTime, upEndTime, downStartTime, downEndTime);
                 }else if (TimeDateUtils.getTimeGreater(getTime(startDate),downStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(startDate),downEndTime)){
                     hours=getQuantianKuatian(endDate, upStartTime, upEndTime, downStartTime, downEndTime);
                 }
                 if (TimeDateUtils.daysDeviation(startDate,endDate)>=2){
                     tvTime.setText(String.valueOf(hours+(TimeDateUtils.daysDeviation(startDate,endDate)-1)*(TimeDateUtils.getTimeHours(upStartTime,upEndTime)+TimeDateUtils.getTimeHours(downStartTime,downEndTime))));
                 }else{
                     tvTime.setText(String.valueOf(hours));
                 }
                }
        }else{//半天
            if (getIsToday(startDate,endDate)){//一天
                Log.e("-----------","半一");
                if (TimeDateUtils.getTimeLessEqual(getTime(startDate),upStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),upEndTime)){
                    tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(upStartTime,getTime(endDate))));
                }else if (TimeDateUtils.getTimeGreater(getTime(startDate),upStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),upEndTime)){
                    tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(getTime(startDate),getTime(endDate))));
                }else if (TimeDateUtils.getTimeGreater(getTime(startDate),upStartTime)&&TimeDateUtils.getTimeGreater(getTime(endDate),upEndTime)){
                    tvTime.setText(String.valueOf(TimeDateUtils.getTimeHours(getTime(startDate),upEndTime)));
                }else{
                    ToastUtils.showShort(GALeaveMainActivity.this,"请假时间选择有误，请重新确认！");
                }
            }else{//跨天
                Log.e("-----------","半跨");
                if (TimeDateUtils.getTimeLessEqual(getTime(startDate),upStartTime)){
                    hours=TimeDateUtils.getTimeHours(upStartTime,upEndTime)+getBantianKuatian(endDate,upStartTime,upEndTime);
                }else if (TimeDateUtils.getTimeGreater(getTime(startDate),upStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(startDate),upEndTime)){
                    hours=TimeDateUtils.getTimeHours(getTime(startDate),upEndTime)+getBantianKuatian(endDate,upStartTime,upEndTime);
                }else if (TimeDateUtils.getTimeGreater(getTime(startDate),upEndTime)){
                    hours=getBantianKuatian(endDate,upStartTime,upEndTime);
                }
                if (TimeDateUtils.daysDeviation(startDate,endDate)>=2){
                    tvTime.setText(String.valueOf(hours+(TimeDateUtils.daysDeviation(startDate,endDate)-1)*TimeDateUtils.getTimeHours(upStartTime,upEndTime)));
                }else{
                    tvTime.setText(String.valueOf(hours));
                }
            }
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

    /**
     * 判断请假是否为一天
     * @param startDate 开始时间
     * @param endDate   结束时间
     */
    private Boolean getIsToday(String startDate,String endDate){
        if (startDate.split(" ")[0].equals(endDate.split(" ")[0])){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 获取小时
     * @param date
     * @return
     */
    private String getTime(String date){
        return date.split(" ")[1];
    }

    /**
     * 获取年月日
     * @param date
     * @return
     */
    private String getDate(String date){
        return date.split(" ")[0];
    }

    /**
     * 班别 半天 ，请假 跨天 的 最后一天
     * 即结束时间当天的请假时长
     * @return
     */
    private float getBantianKuatian(String endDate,String upStartTime,String upEndTime)throws ParseException {
        if(TimeDateUtils.getTimeLessEqual(getTime(endDate),upStartTime)){
            return (float) 0;
        }else if(TimeDateUtils.getTimeGreater(getTime(endDate),upStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),upEndTime)){
            return TimeDateUtils.getTimeHours(upStartTime,getTime(endDate));
        }else if(TimeDateUtils.getTimeGreater(getTime(endDate),upEndTime)){
            return TimeDateUtils.getTimeHours(upStartTime,upEndTime);
        }else{
            return (float) 0;
        }
    }

    private float getQuantianKuatian(String endDate,String upStartTime,String upEndTime,String downStartTime,String downEndTime)throws ParseException{
        if (TimeDateUtils.getTimeLessEqual(getTime(endDate),upStartTime)){
            return (float)0;
        }else if (TimeDateUtils.getTimeGreater(getTime(endDate),upStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),upEndTime)){
            return TimeDateUtils.getTimeHours(upStartTime,getTime(endDate));
        }else if (TimeDateUtils.getTimeGreater(getTime(endDate),upEndTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),downStartTime)){
            return TimeDateUtils.getTimeHours(upStartTime,upEndTime);
        }else if (TimeDateUtils.getTimeGreater(getTime(endDate),downStartTime)&&TimeDateUtils.getTimeLessEqual(getTime(endDate),downEndTime)){
            return TimeDateUtils.getTimeHours(upStartTime,upEndTime)+TimeDateUtils.getTimeHours(downStartTime,getTime(endDate));
        }else if (TimeDateUtils.getTimeGreater(getTime(endDate),downEndTime)){
            return TimeDateUtils.getTimeHours(upStartTime,upEndTime)+TimeDateUtils.getTimeHours(downStartTime,downEndTime);
        }else{
            return (float)0;
        }
    }

    /**
     * 请假信息提交
     */
    private  void upLeave(){
            final String url = Constants.HTTP_ZW_LEAVE_QJ_UP; //此處寫上自己的URL

            JsonObject object = new JsonObject();

        object.addProperty("p_empId", FoxContext.getInstance().getLoginId());
        object.addProperty("p_empName", FoxContext.getInstance().getName());
        object.addProperty("type", FoxContext.getInstance().getType());
            object.addProperty("qj_start_date", tvStartDate.getText().toString());
            object.addProperty("qj_end_date", tvEndDate.getText().toString());
            object.addProperty("qj_time", tvTime.getText().toString());
            object.addProperty("qj_reason", etReason.getText().toString());
            object.addProperty("zg_no", gaWorks.get(0).getZg_no());
            object.addProperty("c_id",gaWorks.get(0).getC_id());
            object.addProperty("g_id",gaWorks.get(0).getG_id());

            Log.e("-----object------", object.toString());
            //開啟一個新執行緒，向伺服器上傳資料
            new Thread() {
                public void run() {
                    //把网络访问的代码放在这里
                    try {
                        showDialog();
                        Log.e("---------", "==fff===" + url);
                        String result = HttpConnectionUtil.doPostJsonObject(url, object);
                        if (result != null) {
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
                            dismissDialog();
                        } else {
                            Message message = new Message();
                            message.what = MESSAGE_NOT_NET;
                            mHandler.sendMessage(message);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();


    }
}
