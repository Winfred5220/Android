package com.example.administrator.yanfoxconn.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.RouteMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.zxing.camera.CameraManager;
import com.example.administrator.yanfoxconn.zxing.decoding.CaptureActivityHandler;
import com.example.administrator.yanfoxconn.zxing.decoding.InactivityTimer;
import com.example.administrator.yanfoxconn.zxing.view.ViewfinderView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * 二維碼掃描界面
 * Created by song on 2017/9/1.
 */

public class QrCodeActivity extends BaseActivity implements Callback, View.OnClickListener {
    private final int MESSAGE_GPS = 1;//判斷GPS
    private final int MESSAGE_TOAST = 2;//showToast
    private final int MESSAGE_GET_DIM_LOCAL = 3;//獲取點位名稱,用於提交

    @BindView(R.id.viewfinder_view)
    ViewfinderView viewfinderView;//掃描框
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnOpen;//打開手電筒
    @BindView(R.id.btn_list)
    Button btnList;//跳轉巡檢進度列表,用於帶點檢項的巡檢；dn宿舍查驗，用於進入異常整改界面

    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private boolean isOpen = false;
    private String num;
    private String resultString;
    private String type;
    private String inOut="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);
        CameraManager.init(getApplication());
        Bundle bundle = this.getIntent().getExtras();
        tvTitle.setText(bundle.getString("title"));
        num = bundle.getString("num");
        type = bundle.getString("type");
        btnBack.setVisibility(View.VISIBLE);
        btnOpen.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnOpen.setOnClickListener(this);
        btnOpen.setText("手電筒關");
        if (num.equals("cz")||num.equals("EA")){//後期根據不同部門需求,增加判斷
            btnList.setVisibility(View.VISIBLE);
            btnList.setOnClickListener(this);
        }else if (num.equals("dn")){//dn宿舍查驗，點擊顯示異常整改
            btnList.setText("異常整改");
            btnList.setVisibility(View.VISIBLE);
            btnList.setOnClickListener(this);
        }else if (num.equals("borrow")){//設備借用
            btnList.setText("設備查詢");
            btnList.setVisibility(View.VISIBLE);
            btnList.setOnClickListener(this);
        }
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * ����ɨ����
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
         resultString = result.getText();
         Log.e("--------------","resultString==="+resultString);
        if (resultString.equals("")) {
            Toast.makeText(QrCodeActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {

            if (num.equals("1")){//一維碼掃描
                Intent resultIntent = new Intent(QrCodeActivity.this, CarScanActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("result", resultString);
                resultIntent.putExtras(bundle);
                startActivity(resultIntent);
                finish();
            }else if (num.equals("2")){//二維碼掃描
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("result", resultString);
                resultIntent.putExtras(bundle);
                this.setResult(RESULT_OK, resultIntent);
                finish();
            }else if (num.equals("3")){//人資簽到二維碼掃描
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("result", resultString);
                resultIntent.putExtras(bundle);
                this.setResult(3, resultIntent);
                this.finish();
            }else if (num.equals("cross")){//人工跨區無紙化
                Intent resultIntent = new Intent(QrCodeActivity.this, CrossScanActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("result", resultString);
                resultIntent.putExtras(bundle);
                startActivity(resultIntent);
                finish();
            }else if (num.equals("car")){//車輛跨區無紙化
                Intent resultIntent = new Intent(QrCodeActivity.this, CrossCarActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("result", resultString);
                resultIntent.putExtras(bundle);
                startActivity(resultIntent);
                finish();
            }else if (num.equals("wrong")){//常用表單 員工違規登記表
                Intent resultIntent = new Intent(QrCodeActivity.this, EmpWrongActivity.class);
                resultIntent.putExtra("result",resultString);
                startActivity(resultIntent);
                finish();
            }else if (num.equals("inout")){//常用表單 員工進出異常登記表
                Intent resultIntent = new Intent(QrCodeActivity.this, EmpTurnoverActivity.class);
                resultIntent.putExtra("result",resultString);
                startActivity(resultIntent);
                finish();
            }else if(num.equals("vehicle")){//常用表單 二輪車違規
                Intent resultIntent = new Intent(QrCodeActivity.this, TwoWheelVehicleActivity.class);
                resultIntent.putExtra("result",resultString);
                startActivity(resultIntent);
                finish();
            }else if(num.equals("forklift_check")){//叉車巡檢
                if(resultString.contains(",")){
                String[] rel = resultString.split(",");
                    if(rel[1].equals("AA")) {
                        Intent resultIntent = new Intent(QrCodeActivity.this, ForkliftMessageToCheckActivity.class);
                        resultIntent.putExtra("result", resultString);
                        startActivity(resultIntent);
                        finish();
                    }else {
                        Toast.makeText(QrCodeActivity.this,"您掃描的二維碼有誤，請更換二維碼",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(QrCodeActivity.this,"您掃描的二維碼有誤，請更換二維碼",Toast.LENGTH_SHORT).show();
                }
            }else if(num.equals("forklift_repair")){//叉車報修
                if(resultString.contains(",")){
                    String[] rel = resultString.split(",");
                    if(rel[1].equals("AA")) {
                        Intent resultIntent = new Intent(QrCodeActivity.this, ForkliftMessageToRepairActivity.class);
                        resultIntent.putExtra("result", resultString);
                        startActivity(resultIntent);
                        finish();
                    }else {
                        Toast.makeText(QrCodeActivity.this,"您掃描的二維碼有誤，請更換二維碼",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(QrCodeActivity.this,"您掃描的二維碼有誤，請更換二維碼",Toast.LENGTH_SHORT).show();
                }
            }else if(num.equals("mobile")){
                //移動上網設備
                    if(!resultString.equals("")) {
                        Intent resultIntent = new Intent(QrCodeActivity.this, MobilecontrolMessageActivity.class);
                        resultIntent.putExtra("id", resultString.toUpperCase());
                        startActivity(resultIntent);
                        finish();
                    }else {
                        Toast.makeText(QrCodeActivity.this,"您掃描的二維碼有誤，請更換二維碼",Toast.LENGTH_SHORT).show();
                    }
            }else if (num.equals("dn")){
                //總務宿舍查驗
                Intent resultIntent = new Intent(QrCodeActivity.this, DNCheckListActivity.class);
                resultIntent.putExtra("result",resultString);
                startActivity(resultIntent);
                finish();
            } else if (num.equals("do")){
                //人資退訓放行
                Intent resultIntent = new Intent(QrCodeActivity.this, RetreatTrainingInfoActivity.class);
                resultIntent.putExtra("result",resultString);
                startActivity(resultIntent);
                finish();
            } else if (num.equals("dq_check")){
                //直飲水點檢
                Intent resultIntent = new Intent(QrCodeActivity.this, ZhiyinshuiCheckActivity.class);
                resultIntent.putExtra("result",resultString);
                resultIntent.putExtra("type",type);
                startActivity(resultIntent);
                finish();
            } else if (num.equals("dq_maintain")){
                //直飲水維保
                Intent resultIntent = new Intent(QrCodeActivity.this, ZhiyinshuiMaintainActivity.class);
                resultIntent.putExtra("result",resultString);
                resultIntent.putExtra("type",type);
                startActivity(resultIntent);
                finish();
            }else if (num.equals("cz")){  //點位點檢項巡檢
                if (FoxContext.getInstance().getType().equals("GCGL")){
                    Intent intent = new Intent(QrCodeActivity.this, GTMainActivity.class);
                    intent.putExtra("flag", "S");
                    intent.putExtra("result", resultString);
                    intent.putExtra("from","qr");
                    startActivity(intent);
                }else if (FoxContext.getInstance().getType().equals("IG")){
                    Intent intent = new Intent(QrCodeActivity.this, IGChangeActivity.class);
                    intent.putExtra("result", resultString);
                    intent.putExtra("from","qr");
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(QrCodeActivity.this, ComAbnormalUpActivity.class);
                    intent.putExtra("flag", "D");
                    intent.putExtra("result", resultString);
                    startActivity(intent);
                }
                finish();
            }else if (num.equals("fh")){//總務餐廳巡檢
                Intent intent = new Intent(QrCodeActivity.this, FHRestaurantActivity.class);
                intent.putExtra("flag", "D");
                intent.putExtra("result", resultString);
                startActivity(intent);
                finish();
            }else if (num.equals("DZ")){
                //人資監餐簽到
                Intent resultIntent = new Intent(QrCodeActivity.this, DZSignInOutActivity.class);
                resultIntent.putExtra("result",resultString);
                resultIntent.putExtra("flag",getIntent().getStringExtra("flag"));
                resultIntent.putExtra("inOut",getIntent().getStringExtra("inOut"));
                resultIntent.putExtra("scId",getIntent().getStringExtra("scId"));
                startActivity(resultIntent);
                finish();
            }else if (num.equals("GA")){
                //總務臨時工簽到
                Intent resultIntent = new Intent(QrCodeActivity.this, DZSignInOutActivity.class);
                resultIntent.putExtra("result",resultString);
                resultIntent.putExtra("flag","GA");
                resultIntent.putExtra("inOut",getIntent().getStringExtra("inOut"));
                resultIntent.putExtra("scId",getIntent().getStringExtra("scId"));
                startActivity(resultIntent);
                finish();
            }else if (num.equals("EA")){//車輛巡檢
                Intent intent = new Intent(QrCodeActivity.this, CarMessageActivity.class);
                intent.putExtra("flag", "S");
                intent.putExtra("result", resultString);
                startActivity(intent);
                finish();
            }else if (num.equals("borrow")){//車輛巡檢
                Intent intent = new Intent(QrCodeActivity.this, DeviceBorrowMainActivity.class);
                intent.putExtra("result", resultString);
                startActivity(intent);
                finish();
            }else if (num.equals("storeQr")){
                Intent resultIntent = new Intent(QrCodeActivity.this, IGMainActivity.class);
//                resultIntent.putExtra("id", resultString);
                resultIntent.putExtra("id", resultString);
                resultIntent.putExtra("from", "storeQr");
                startActivity(resultIntent);
                finish();
            }else if (num.equals("IGChange")){
                Intent resultIntent = new Intent(QrCodeActivity.this, IGChangeActivity.class);
//                resultIntent.putExtra("id", resultString);
                resultIntent.putExtra("id", resultString);
                resultIntent.putExtra("from", "IGChange");
                startActivity(resultIntent);
                finish();
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:
                if (isOpen) {
                    CameraManager.get().openLight();
                    isOpen=false;
                    btnOpen.setText("手電筒開");
                } else {
                    CameraManager.get().offLight();
                    isOpen=true;
                    btnOpen.setText("手電筒關");
                }
                break;
            case R.id.btn_list://跳轉巡檢進度列表
//                getRouteList(FoxContext.getInstance().getType());
                if (num.equals("dn")){
                    Intent intentR = new Intent(QrCodeActivity.this,DNReformListActivity.class);
                    intentR.putExtra("from","all");
                    startActivity(intentR);
                }else if (FoxContext.getInstance().getType().equals("GCGL")){
                    Intent intent = new Intent(QrCodeActivity.this, GTCheckActivity.class);
                    startActivity(intent);
                }else if (num.equals("borrow")){
                    Intent intent = new Intent(QrCodeActivity.this, DeviceBorrowListActivity.class);
                    startActivity(intent);
                }else if (num.equals("EA")){
                    Intent intent = new Intent(QrCodeActivity.this,CarCheckProgressTypeListActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(QrCodeActivity.this, ComAbRouteListActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }
    /**
     * 根據角色權限,獲取巡檢路線列表
     *
     * @param type
     */
    private String url;//請求地址
    private String result;//返回結果
    private List<RouteMessage> routeMessageList;//巡檢點信息列表
    private LocationManager locManager;
    public void getRouteList(String type) {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        showDialog();
        url = Constants.HTTP_DIMEMSION_SERVLET + "?type=" + type;

        FoxContext.getInstance().setType(type);
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                result = HttpUtils.queryStringForPost(url);

                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                String response = result;

                if (result != null) {
                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        if (!array.toString().equals("")) {
                            routeMessageList = new ArrayList<RouteMessage>();
                            for (JsonElement type : array) {
                                RouteMessage humi = gson.fromJson(type, RouteMessage.class);
                                routeMessageList.add(humi);
                            }
                            if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//
                                mHandler.sendEmptyMessage(0);
                            } else {
                                Intent intent = new Intent(getApplication(), RouteListActivity.class);
                                intent.putExtra("routeList", (Serializable) routeMessageList);
                                intent.putExtra("Dflag", "");//用於判定是否為宿舍巡檢,以方便按樓棟傳值,其他地方值為空
                                startActivity(intent);
                            }
                        } else {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = "無數據";
                            mHandler.sendMessage(message);
                        }

                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
//                        ToastUtils.showShort(MainActivityGao.this, jsonObject.get("errMessage").getAsString());
                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "無數據";
                    mHandler.sendMessage(message);
                }
                dismissDialog();
            }
        }.start();
    }

    /**
     * 獲取點位dim_id
     * @param flag
     */
    private String[] qrResult;
    private void getDimId(String flag){
        qrResult=resultString.split(",");
        showDialog();
        url = Constants.HTTP_WATER_SCAN_SERVLET + "?type=" + FoxContext.getInstance().getType()+"&dim_id="+qrResult[1]+"&flag="+flag;
        Log.e("-----","fff--"+url);
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();
                Gson gson = new Gson();
                Log.e("---fff--result----", result.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
//
//                        JsonArray array = jsonObject.get("data").getAsJsonArray();
//                        messageList = new ArrayList<AbnormalMessage>();
//
//                        for (JsonElement type : array) {
//                            AbnormalMessage humi = gson.fromJson(type, AbnormalMessage.class);
//                            messageList.add(humi);
//
//                        }
//                        Message message = new Message();
//                        message.what = MESSAGE_GET_DIM_LOCAL;
////                        message.obj = messageList;
//                        mHandler.sendMessage(message);
                        Intent intent = new Intent(getApplication(), ComAbnormalUpActivity.class);
                        intent.putExtra("dimLocal", (Serializable) routeMessageList);
                        intent.putExtra("Dflag", "");//用於判定是否為宿舍巡檢,以方便按樓棟傳值,其他地方值為空
                        intent.putExtra("result",resultString);
                        startActivity(intent);

                    } else {
                        Log.e("--------", "dd=jsonObject.get(\"errMessage\").getAsString()==" + jsonObject.get("errMessage").getAsString());
//                                ToastUtils.showShort(RouteListActivity.this, jsonObject.get("errMessage").getAsString());
//                        Message message = new Message();
//                        message.what = 2;
//                        message.obj = jsonObject.get("errMessage").getAsString();
//                        mHandler.sendMessage(message);
                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "請求不成功";
                    mHandler.sendMessage(message);
//                    ToastUtils.showShort(QrCodeActivity.this, "請求不成功");
                }

            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    if (msg.obj.equals("失敗")) {
                        ToastUtils.showShort(QrCodeActivity.this, "沒有巡檢數據!");
                    } else {
                        ToastUtils.showShort(QrCodeActivity.this, msg.obj.toString());
                    }
                    break;
                case MESSAGE_GPS:
                    ToastUtils.showShort(QrCodeActivity.this, "请开启GPS导航...");
                    break;
//                case MESSAGE_GET_DIM_LOCAL:
//
//                    break;
            }
            super.handleMessage(msg);
        }
    };

}
