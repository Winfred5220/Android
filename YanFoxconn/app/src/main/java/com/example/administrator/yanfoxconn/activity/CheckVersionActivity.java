package com.example.administrator.yanfoxconn.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.VersionMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 版本更新界面
 * Created by song on 2017/9/29.
 */

public class CheckVersionActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_VISION = 1;

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.tv_old_code)
    TextView tvOldCode;//舊版本號
    @BindView(R.id.tv_new_code)
    TextView tvNewCode;//新版本號
    @BindView(R.id.tv_function)
    TextView tvFunction;//功能介紹
    @BindView(R.id.btn_check_version)
    Button btnCheckVersion;//檢查最新版本按鈕

    private String newCode;//新版本號
    private String function;//功能介紹

    private String versionResult;//版本返回結果
    private List<VersionMessage> versionMessageList;
    private int ersionCode;//本機APP版本號
    private int getVersionCode;//獲取的最新版本號
    private Handler handlerM = new Handler();//更新版本下載
    private ProgressDialog pBar;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.copy_activity_check_version);

        ButterKnife.bind(this);

        tvTitle.setText("版本查看");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnCheckVersion.setOnClickListener(this);


        if (FoxContext.getInstance().isUpdate()) {
            tvOldCode.setText("當前已為最新版本");
            tvNewCode.setText("最新版本號為：" + getIntent().getStringExtra("id"));
            btnCheckVersion.setText("已是最新");

        } else {
            try {
                ersionCode = getApplication().getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                tvOldCode.setText("當前版本號為：" + ersionCode);
                tvNewCode.setText("最新版本號為：" + getIntent().getStringExtra("id"));
                btnCheckVersion.setText("檢查更新");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        tvFunction.setText("功能介紹：" + getIntent().getStringExtra("remark"));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_check_version:
                if (FoxContext.getInstance().isUpdate()) {

                    ToastUtils.showShort(CheckVersionActivity.this, "最新版本");
                } else {
                    isVersionUpdate();
                }
                break;
            case R.id.btn_title_left://返回
                finish();
                break;
        }
    }

    //是否需要更新
    public void isVersionUpdate() {
        Log.e("---------", "isVersionUpdate===");
        showDialog();
        final String urlVersion = Constants.HTTP_GET_VERSION_CODE;
        new Thread() {
            @Override
            public void run() {
                versionResult = HttpUtils.queryStringForPost(urlVersion);
                Log.e("---------", "fffff=url==" + urlVersion.toString());
                dismissDialog();
                Gson gson = new Gson();
                Log.e("-----i----", "isVersionUpdate");
                if (versionResult != null) {
                    Log.e("---------", "fffff===" + versionResult.toString());
                    JsonObject jsonObject = new JsonParser().parse(versionResult).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        versionMessageList = new ArrayList<VersionMessage>();
                        for (JsonElement type : array) {
                            VersionMessage humi = gson.fromJson(type, VersionMessage.class);
                            versionMessageList.add(humi);
                        }
                        getVersionCode = Integer.parseInt(versionMessageList.get(0).getId());

                        Message message = new Message();
                        message.what = MESSAGE_VISION;
                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showShort(CheckVersionActivity.this, msg.obj.toString());
                    break;
                case MESSAGE_VISION://是否更新的彈出框
                    isUpdate();
                    break;
                case 5:
                    pBar.setProgress((Integer) msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 是否更新彈出框
     */
    private void isUpdate() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

        if (ersionCode < getVersionCode) {
            //跳轉下載最新版本apk方法
            Log.e("----------------", "false");
            Dialog dialog = new AlertDialog.Builder(CheckVersionActivity.this)
                    .setTitle("软件更新")
                    .setMessage(versionMessageList.get(0).getRemark().toString())
                    // 设置内容
                    .setPositiveButton("更新",// 设置确定按钮
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    pBar = new ProgressDialog(CheckVersionActivity.this);
                                    pBar.setTitle("正在下载");
                                    pBar.setMessage("请稍候...");
                                    pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    pBar.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失

                                    downFile(Constants.HTTP_GET_APK + versionMessageList.get(0).getId() + ".apk");
                                }
                            })
                    .setNegativeButton("暂不更新",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    FoxContext.getInstance().setUpdate(false);
                                    // 点击"取消"按钮之后退出程序
                                    finish();
                                }
                            }).create();// 创建
            // 显示对话框
            dialog.show();
        } else {
            //給全局變量賦值true
            FoxContext.getInstance().setUpdate(true);

            Log.e("----------------", "true");
        }
    }

    /**
     * 下載最新版本apk
     *
     * @param url
     */
    private void downFile(final String url) {
        pBar.show();
        Log.e("----fff",url);
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);

                    Log.e("----fff",response.toString());
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                versionMessageList.get(0).getId() + ".apk");
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int count = 0;
                        int percent = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            if (length > 0) {
                            }
                            if (((int) (100 * count / length)) > percent) {
                                percent = (int) (100 * count / length);
//                                progressDialog.setProgress(percent);
                                Message message1 = new Message();
                                message1.what = 5;
                                message1.obj = percent;
                                mHandler.sendMessage(message1);
                            }
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    down();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * 下載完成
     */
    private void down() {
        handlerM.post(new Runnable() {
            public void run() {
                pBar.cancel();
                update();
            }
        });
    }

    /**
     * 進行安裝
     */
    private void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File apkFile = new File(
                Environment.getExternalStorageDirectory(),
                versionMessageList.get(0).getId() + ".apk");
        Uri apkFileUri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            apkFileUri = FileProvider.getUriForFile(mContext, "com.example.administrator.yanfoxconn.provider", apkFile);
        } else {
            apkFileUri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(apkFileUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//沒這句,最后不会提示完成、打开。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        startActivity(intent);
       // android.os.Process.killProcess(android.os.Process.myPid());//沒這句,安装好了，点打开，是不会打开新版本应用的。
    }
}
