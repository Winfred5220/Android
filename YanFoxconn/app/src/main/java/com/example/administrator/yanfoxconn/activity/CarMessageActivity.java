package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.ZhiyinshuiCheckAdapter;
import com.example.administrator.yanfoxconn.bean.CarCheckMessage;
import com.example.administrator.yanfoxconn.bean.ForkliftMessage;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiMsg;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.TimeDateUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * 車輛點檢  掃描二維碼帶出資料
 * Created by song on 2020/10/13.
 */
public class CarMessageActivity extends BaseActivity {

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tr_1)
    TableRow tr1;//區域
    @BindView(R.id.tr_2)
    TableRow tr2;//部門
    @BindView(R.id.tr_3)
    TableRow tr3;//車輛歸屬
    @BindView(R.id.tr_4)
    TableRow tr4;//品牌
    @BindView(R.id.tr_5)
    TableRow tr5;//車輛類型
    @BindView(R.id.tr_6)
    TableRow tr6;//車牌號
    @BindView(R.id.tr_7)
    TableRow tr7;//車架號
    @BindView(R.id.tr_8)
    TableRow tr8;//型號
    @BindView(R.id.tr_9)
    TableRow tr9;//噸位
    @BindView(R.id.tr_10)
    TableRow tr10;//車輛生產日期
    @BindView(R.id.tr_11)
    TableRow tr11;//合約起止日期
    @BindView(R.id.tr_12)
    TableRow tr12;//下次年檢日期
    @BindView(R.id.tr_13)
    TableRow tr13;//電瓶容量
    @BindView(R.id.tr_14)
    TableRow tr14;//電壓
    @BindView(R.id.tr_15)
    TableRow tr15;//
    @BindView(R.id.tr_16)
    TableRow tr16;//配發日期
    @BindView(R.id.tr_17)
    TableRow tr17;//荷載人數
    @BindView(R.id.tr_18)
    TableRow tr18;//駕駛員
    @BindView(R.id.tr_19)
    TableRow tr19;//駕齡
    @BindView(R.id.tr_20)
    TableRow tr20;//燃油類型
    @BindView(R.id.tr_21)
    TableRow tr21;//油耗
    @BindView(R.id.tr_22)
    TableRow tr22;//登記日期
    @BindView(R.id.tr_23)
    TableRow tr23;//使用年限
    @BindView(R.id.tr_24)
    TableRow tr24;//報廢日期
    @BindView(R.id.tr_25)
    TableRow tr25;//保養週期
    @BindView(R.id.tr_26)
    TableRow tr26;//保險公司
    @BindView(R.id.tr_27)
    TableRow tr27;//保險起止日期
    @BindView(R.id.tr_28)
    TableRow tr28;//車輛年檢日期
    @BindView(R.id.tr_29)
    TableRow tr29;//氣瓶檢驗日期
    @BindView(R.id.tv_1)
    TextView tv1;//區域
    @BindView(R.id.tv_2)
    TextView tv2;//部門
    @BindView(R.id.tv_3)
    TextView tv3;//車輛歸屬
    @BindView(R.id.tv_4)
    TextView tv4;//品牌
    @BindView(R.id.tv_5)
    TextView tv5;//車輛類型
    @BindView(R.id.tv_6)
    TextView tv6;//車牌號
    @BindView(R.id.tv_7)
    TextView tv7;//車架號
    @BindView(R.id.tv_8)
    TextView tv8;//車型
    @BindView(R.id.tv_9)
    TextView tv9;//噸位
    @BindView(R.id.tv_10)
    TextView tv10;//車輛生產日期
    @BindView(R.id.tv_11)
    TextView tv11;//合約起止日期
    @BindView(R.id.tv_12)
    TextView tv12;//下次年檢日期
    @BindView(R.id.tv_13)
    TextView tv13;//電瓶容量
    @BindView(R.id.tv_14)
    TextView tv14;//電壓
    @BindView(R.id.tv_15)
    TextView tv15;//
    @BindView(R.id.tv_16)
    TextView tv16;//配發日期
    @BindView(R.id.tv_17)
    TextView tv17;//荷載人數
    @BindView(R.id.tv_18)
    TextView tv18;//駕駛員
    @BindView(R.id.tv_19)
    TextView tv19;//駕齡
    @BindView(R.id.tv_20)
    TextView tv20;//燃油類型
    @BindView(R.id.tv_21)
    TextView tv21;//油耗
    @BindView(R.id.tv_22)
    TextView tv22;//登記日期
    @BindView(R.id.tv_23)
    TextView tv23;//使用年限
    @BindView(R.id.tv_24)
    TextView tv24;//報廢日期
    @BindView(R.id.tv_25)
    TextView tv25;//保養週期
    @BindView(R.id.tv_26)
    TextView tv26;//保險公司
    @BindView(R.id.tv_27)
    TextView tv27;//保險起止日期
    @BindView(R.id.tv_28)
    TextView tv28;//車輛年檢日期
    @BindView(R.id.tv_29)
    TextView tv29;//氣瓶檢驗日期

    @BindView(R.id.btn_xunjian)
    Button btnXunjian;//巡檢
    @BindView(R.id.btn_xiaosha)
    Button btnXiaosha;//消殺


    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_UP = 3;//提交信息回復
    private final int MESSAGE_JUMP = 4;//跳轉維護界面
    private final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    private final int MESSAGE_SET_CHECK = 6;

    private String result,type,user_type;//單號,類別
    private String status;//狀態碼
    private List<CarCheckMessage> mCarCheckMessage;//基本信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_message);
        ButterKnife.bind(this);

        result = getIntent().getStringExtra("result");
        //Log.e("------------","-----result------"+result);
        if(FoxContext.getInstance().getDep().equals("")){
            ToastUtils.showLong(CarMessageActivity.this,"部門為空，請重新登錄！");
        }else if(FoxContext.getInstance().getDep().equals("車輛廠商")){
            user_type ="vendor";
        }else if (FoxContext.getInstance().getDep().equals("車輛司機")){
            user_type ="driver";
        }else{
            ToastUtils.showLong(CarMessageActivity.this,"您沒有點檢權限！");
            finish();
        }

        if (result!=null&&!result.equals("")&&result.length()>=2){
            type = result.substring(0,2);
            Log.e("------------","-----type------"+type);

            if(type.equals("FP")||type.equals("FO")||type.equals("EI")){//車調車輛
                btnXiaosha.setVisibility(View.VISIBLE);
            }

             if(!type.equals("EV")&&!type.equals("EZ")&&!type.equals("EO")&&!type.equals("EI")&&!type.equals("FO")&&
                !type.equals("FP")&&!type.equals("GF")&&!type.equals("GG")&&!type.equals("GH")&&!type.equals("GI")&&
                !type.equals("GJ")&&!type.equals("GK")&&!type.equals("GL")&&!type.equals("GM")&&!type.equals("GN")){
                ToastUtils.showShort(this, "二維碼掃描有誤！");
             finish();
             }else if (!FoxContext.getInstance().getRoles().contains(type)){
                 ToastUtils.showLong(CarMessageActivity.this,"您沒有點檢"+type+"權限！");
                 finish();
             }
        }else{
            ToastUtils.showShort(this, "二維碼掃描有誤！");
            finish();
        }


        tvTitle.setText("本車基本資料");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnXunjian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                if(type.equals("FP")||type.equals("FO")||type.equals("EI")){//車調車輛
                    intent=new Intent(CarMessageActivity.this,CarSystemCheckActivity.class);
                }else{
                    intent=new Intent(CarMessageActivity.this,CarCheckActivity.class);
                }

                intent.putExtra("result",result);
                intent.putExtra("type",type);
                intent.putExtra("user_type",user_type);
                intent.putExtra("msg", (Serializable) mCarCheckMessage);
                startActivity(intent);
                finish();
            }
        });
        btnXiaosha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CarMessageActivity.this,CarCheckXSActivity.class);
                intent.putExtra("result",result);
                intent.putExtra("type",type);
                intent.putExtra("user_type","vendor");
                intent.putExtra("msg", (Serializable) mCarCheckMessage);
                startActivity(intent);
                finish();
            }
        });
        getMessage();
    }


    private void getMessage(){
        showDialog();
//        final String url = Constants.HTTP_WATER_SCAN_SERVLET +"?flag=S"+"&dim_id="+result+"&type="+type;+-
        final String url = Constants.HTTP_CAR_MESSAGE_SERVLET +"?type="+type+"&dim_id="+result+"&user_type="+user_type;
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForPost(url);
                Log.e("---------", "==fff===" + url);
                dismissDialog();
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonObject jsonObject2 = jsonObject.get("result").getAsJsonObject();
                        status = jsonObject2.get("status").getAsString();
                        if(status.equals("0-N")){//正常帶出信息
                            JsonArray array = jsonObject2.get("info").getAsJsonArray();
                            mCarCheckMessage = new ArrayList<CarCheckMessage>();

                            for (JsonElement type : array) {
                                CarCheckMessage humi = gson.fromJson(type, CarCheckMessage.class);
                                mCarCheckMessage.add(humi);
                            }

                            Message message = new Message();
                            message.what = MESSAGE_SET_TEXT;
                            message.obj = jsonObject.get("errMessage").getAsString();
                            mHandler.sendMessage(message);
                        }else if(status.equals("0-Y")){
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj ="上次掃描時間："+jsonObject2.get("last_date").getAsString().substring(0,16)+jsonObject2.get("msg").getAsString();
                            mHandler.sendMessage(message);
                        }else if(status.equals("1-N")){
                            Message message = new Message();
                            message.what = MESSAGE_JUMP;
                            message.obj = "狀態：維修中，點擊確定將跳轉到異常列表界面";
                            mHandler.sendMessage(message);
                        }else if(status.equals("1-Y")){
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = "該車輛處於停用或報廢狀態！";
                            mHandler.sendMessage(message);
                        }else {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = "未知錯誤！";
                            mHandler.sendMessage(message);
                        }

                    } else{
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                    finish();
                }
            } }.start();
    }

    private void getMessage2(){

        showDialog();
        //final String url = Constants.HTTP_FORKLIFT_MESSAGE_SERVLET +"?bianhao=" + rel[0] + "&type=" + rel[1];
        final String url = Constants.HTTP_CAR_MESSAGE_SERVLET +"?type="+type+"&dim_id="+result+"&user_type="+user_type;

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
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        mCarCheckMessage = new ArrayList<CarCheckMessage>();

                        for (JsonElement type : array) {
                            CarCheckMessage humi = gson.fromJson(type, CarCheckMessage.class);
                            mCarCheckMessage.add(humi);

                        }

                        Message message = new Message();
                        message.what = MESSAGE_SET_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else{
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                    finish();
                }
            } }.start();
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
                    ToastUtils.showLong(CarMessageActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    setText();
                    break;
                case MESSAGE_UP://提交響應
                    worningAlert(msg.obj.toString(),MESSAGE_TOAST);
//                    ToastUtils.showLong(CrossScanActivity.this, msg.obj.toString());
                    break;
                case MESSAGE_SET_CHECK://點檢項列表賦值

                    break;
                case MESSAGE_JUMP://跳轉維護異常界面
                    worningAlert(msg.obj.toString(),MESSAGE_JUMP);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setText() {
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy");
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前年份
        String years = formatter2.format(curDate);
//        Log.e("========", "years====="+years);
        String curDateTime  = formatter1.format(curDate);

//        if (mCarCheckMessage.get(0).getStatus().equals("W")){//維修中
//            btnWeixiu.setVisibility(View.VISIBLE);
//        }else if (mCarCheckMessage.get(0).getStatus().equals("Y")){//正常
//
//        }else if (mCarCheckMessage.get(0).getStatus().equals("F")){//報廢
//
//        }else if (mCarCheckMessage.get(0).getStatus().equals("N")){//停用
//
//        }

        tv1.setText(mCarCheckMessage.get(0).getArea());
        tv2.setText(mCarCheckMessage.get(0).getBumen());
        tv3.setText(mCarCheckMessage.get(0).getChe_belong());
        tv4.setText(mCarCheckMessage.get(0).getChe_brand());
        tv5.setText(mCarCheckMessage.get(0).getChe_type());
        tv6.setText(mCarCheckMessage.get(0).getChepai());
        tv7.setText(mCarCheckMessage.get(0).getBianhao());//車架號
        tv8.setText(mCarCheckMessage.get(0).getXinghao());
        tv9.setText(mCarCheckMessage.get(0).getDunwei());
        String sc_date=mCarCheckMessage.get(0).getSc_date().replaceAll("00:00:00.0","");
        if(sc_date.equals("1900-01-01 ")||sc_date.equals(""))sc_date="";
        tv10.setText(sc_date);
        String hy_startdate=mCarCheckMessage.get(0).getHy_startdate().replaceAll("00:00:00.0","");
        if(hy_startdate.equals("1900-01-01 ")||hy_startdate.equals(""))hy_startdate="";
        String hy_enddate=mCarCheckMessage.get(0).getHy_enddate().replaceAll("00:00:00.0","");
        if(hy_enddate.equals("1900-01-01 ")||hy_enddate.equals(""))hy_enddate="";

        if (!hy_startdate.equals("")&&!hy_enddate.equals("")){
            int hy_date_dev = TimeDateUtils.daysDeviation(curDateTime,hy_enddate+"00:00");
            Log.e("----------", "hy_date_dev: "+hy_date_dev);
            if (hy_date_dev<=30){//提前30天預警
                tv11.setTextColor(getResources().getColor(R.color.color_d73d19));
            }
            tv11.setText(hy_startdate+"至 "+hy_enddate);
        }


        String nianjian_datex=mCarCheckMessage.get(0).getNianjian_date().replaceAll("00:00:00.0","");
        if(nianjian_datex.equals("1900-01-01 ")||nianjian_datex.equals(""))tv12.setText("");else {
            tv12.setText(nianjian_datex);
            int nianjian_date_dev = TimeDateUtils.daysDeviation(curDateTime,nianjian_datex+"00:00");
            Log.e("----------", "nianjian_date_dev: "+nianjian_date_dev);
            if (nianjian_date_dev<=30){//提前30天預警
                tv12.setTextColor(getResources().getColor(R.color.color_d73d19));
            }
        }
        tv13.setText(mCarCheckMessage.get(0).getCapacity());
        tv14.setText(mCarCheckMessage.get(0).getVoltage());
//        tv15.setText(mCarCheckMessage.get(0).getBianhao());
        tv16.setText(mCarCheckMessage.get(0).getSc_date());//配發日期
        tv17.setText(mCarCheckMessage.get(0).getHezai());
        tv18.setText(mCarCheckMessage.get(0).getDriver_name());
//        tv19.setText(mCarCheckMessage.get(0).getDriver_years());//駕齡
        String driver_years=mCarCheckMessage.get(0).getDriver_years().replaceAll("00:00:00.0","");
        if(driver_years.equals("1900-01-01 ")||driver_years.equals(""))tv19.setText("");else tv19.setText((Integer.parseInt(years)-Integer.parseInt(driver_years.substring(0,4)))+"");
        tv20.setText(mCarCheckMessage.get(0).getFuel_type());
        tv21.setText(mCarCheckMessage.get(0).getConsump());
        String register_date=mCarCheckMessage.get(0).getRegister_date().replaceAll("00:00:00.0","");
        if(register_date.equals("1900-01-01 ")||register_date.equals("")) {
            tv22.setText("");;tv23.setText("");//使用年限
        }else{
            tv22.setText(register_date);
            tv23.setText((Integer.parseInt(years)-Integer.parseInt(register_date.substring(0,4)))+"");
        }

        String scrap_date=mCarCheckMessage.get(0).getScrap_date().replaceAll("00:00:00.0","");
        if(scrap_date.equals("1900-01-01 ")||scrap_date.equals(""))scrap_date="";
        tv24.setText(scrap_date);
        tv25.setText(mCarCheckMessage.get(0).getMaintenance());
        tv26.setText(mCarCheckMessage.get(0).getInsur_company());

        String insur_startdate=mCarCheckMessage.get(0).getInsur_startdate().replaceAll("00:00:00.0","");
        if(insur_startdate.equals("1900-01-01 ")||insur_startdate.equals(""))insur_startdate="";
        String insur_enddate=mCarCheckMessage.get(0).getInsur_enddate().replaceAll("00:00:00.0","");
        if(insur_enddate.equals("1900-01-01 ")||insur_enddate.equals(""))insur_enddate="";

        if (!insur_startdate.equals("")&&!insur_enddate.equals("")){
            int insur_date_dev = TimeDateUtils.daysDeviation(curDateTime,insur_enddate+"00:00");
            if (insur_date_dev<=30){//提前30天預警
                tv27.setTextColor(getResources().getColor(R.color.color_d73d19));
            }
            tv27.setText(insur_startdate+"至 "+insur_enddate);
        }

        String nianjian_date=mCarCheckMessage.get(0).getNianjian_date().replaceAll("00:00:00.0","");
        if(nianjian_date.equals("1900-01-01 ")||nianjian_date.equals(""))nianjian_date="";
        int nianjian_date_dev = TimeDateUtils.daysDeviation(curDateTime,nianjian_date+"00:00");
        if (nianjian_date_dev<=30){//提前30天預警
            tv29.setTextColor(getResources().getColor(R.color.color_d73d19));
        }
        tv28.setText(nianjian_date);

        String gas_date=mCarCheckMessage.get(0).getGas_date().replaceAll("00:00:00.0","");
        if(gas_date.equals("1900-01-01 ")||gas_date.equals(""))gas_date="";
        int gas_date_dev = TimeDateUtils.daysDeviation(curDateTime,gas_date+"00:00");
        if (gas_date_dev<=30){//提前30天預警
            tv29.setTextColor(getResources().getColor(R.color.color_d73d19));
        }
        tv29.setText(gas_date);

        if(type.equals("EO")||type.equals("EV")){//叉車
            tr1.setVisibility(View.VISIBLE);tr2.setVisibility(View.VISIBLE);
            tr3.setVisibility(View.VISIBLE);tr4.setVisibility(View.VISIBLE);
            tr5.setVisibility(View.VISIBLE);tr6.setVisibility(View.VISIBLE);
            tr7.setVisibility(View.VISIBLE);tr8.setVisibility(View.VISIBLE);
            tr9.setVisibility(View.VISIBLE);tr10.setVisibility(View.VISIBLE);
            tr11.setVisibility(View.VISIBLE);tr12.setVisibility(View.VISIBLE);
            tr13.setVisibility(View.VISIBLE);tr14.setVisibility(View.VISIBLE);
        }else if (type.equals("EZ")){//球車
            tr1.setVisibility(View.VISIBLE);tr2.setVisibility(View.VISIBLE);
            tr4.setVisibility(View.VISIBLE);tr5.setVisibility(View.VISIBLE);
            tr6.setVisibility(View.VISIBLE);tr8.setVisibility(View.VISIBLE);
            tr10.setVisibility(View.VISIBLE);tr11.setVisibility(View.VISIBLE);
            tr14.setVisibility(View.VISIBLE);tr17.setVisibility(View.VISIBLE);
        }else if(type.equals("EI")||type.equals("FO")||type.equals("FP")){//車調
            tr2.setVisibility(View.VISIBLE);tr3.setVisibility(View.VISIBLE);
            tr4.setVisibility(View.VISIBLE);tr5.setVisibility(View.VISIBLE);
            tr6.setVisibility(View.VISIBLE);tr11.setVisibility(View.VISIBLE);
            tr18.setVisibility(View.VISIBLE);tr19.setVisibility(View.VISIBLE);
            tr20.setVisibility(View.VISIBLE);tr21.setVisibility(View.VISIBLE);
            tr22.setVisibility(View.VISIBLE);tr23.setVisibility(View.VISIBLE);
            tr24.setVisibility(View.VISIBLE);tr25.setVisibility(View.VISIBLE);
            tr26.setVisibility(View.VISIBLE);tr27.setVisibility(View.VISIBLE);
            tr28.setVisibility(View.VISIBLE);tr29.setVisibility(View.VISIBLE);
        }else if(type.equals("GF")||type.equals("GG")||type.equals("GH")||type.equals("GI")||type.equals("GJ")){//總務
            tr2.setVisibility(View.VISIBLE);tr3.setVisibility(View.VISIBLE);
            tr4.setVisibility(View.VISIBLE);tr5.setVisibility(View.VISIBLE);
            tr6.setVisibility(View.VISIBLE);tr20.setVisibility(View.VISIBLE);
            tr22.setVisibility(View.VISIBLE);tr23.setVisibility(View.VISIBLE);
            tr24.setVisibility(View.VISIBLE);tr25.setVisibility(View.VISIBLE);
            tr26.setVisibility(View.VISIBLE);tr27.setVisibility(View.VISIBLE);
            tr28.setVisibility(View.VISIBLE);
        }else if(type.equals("GK")||type.equals("GL")){//安保部摩托車 吉普車
            tr1.setVisibility(View.VISIBLE);
            tr4.setVisibility(View.VISIBLE);tr5.setVisibility(View.VISIBLE);
            tr6.setVisibility(View.VISIBLE);tr17.setVisibility(View.VISIBLE);
            tr18.setVisibility(View.VISIBLE);tr19.setVisibility(View.VISIBLE);
            tr20.setVisibility(View.VISIBLE);tr23.setVisibility(View.VISIBLE);
            tr24.setVisibility(View.VISIBLE);tr25.setVisibility(View.VISIBLE);
            tr26.setVisibility(View.VISIBLE);tr27.setVisibility(View.VISIBLE);
            tr28.setVisibility(View.VISIBLE);
        }else if(type.equals("GM")){//安保部電動車
            tr1.setVisibility(View.VISIBLE);tr4.setVisibility(View.VISIBLE);
            tr5.setVisibility(View.VISIBLE);tr6.setVisibility(View.VISIBLE);
            tr16.setVisibility(View.VISIBLE);tr17.setVisibility(View.VISIBLE);
            tr18.setVisibility(View.VISIBLE);tr23.setVisibility(View.VISIBLE);
            tr24.setVisibility(View.VISIBLE);
        }
        else if(type.equals("GN")){//安保部消防車
            tr1.setVisibility(View.VISIBLE);tr17.setVisibility(View.VISIBLE);
            tr4.setVisibility(View.VISIBLE);tr5.setVisibility(View.VISIBLE);
            tr6.setVisibility(View.VISIBLE);tr16.setVisibility(View.VISIBLE);
            tr18.setVisibility(View.VISIBLE);tr23.setVisibility(View.VISIBLE);
            tr24.setVisibility(View.VISIBLE);
        }

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
    private void worningAlert(String msg, final int t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub
                        if (t==MESSAGE_TOAST){
                            finish();
                        }else if (t==MESSAGE_JUMP){
                            Intent intent = new Intent(CarMessageActivity.this, CarExceListActivity.class);
                            intent.putExtra("dim_id",result);
                            intent.putExtra("type",type);
                            intent.putExtra("user_type",user_type);
                            startActivity(intent);
                            finish();
                            //ToastUtils.showShort(CarMessageActivity.this, "跳轉異常界面!");
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
