package com.example.administrator.yanfoxconn.activity;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.UserMessage;
import com.example.administrator.yanfoxconn.bean.VersionMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.PermissionUtil;
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

/**
 * 登錄界面
 * Created by song on 2017/9/20.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks{

    private final int MESSAGE_VISION = 1;
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_OTHER = 3;
    private final int MESSAGE_NETMISTAKE = 4;
    //要申请的权限
    private String[] mPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA};
    public static final int CODE = 0x001;

    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.name_edit)
    EditText editName;
    @BindView(R.id.password_edit)
    EditText editPass;
    @BindView(R.id.btn_username_clear)
    Button delUserEdit;
    @BindView(R.id.btn_pwd_clear)
    Button delPassEdit;
    @BindView(R.id.name_input_layout)
    RelativeLayout nameLayout;
    @BindView(R.id.password_input_layout)
    RelativeLayout passLayout;
    @BindView(R.id.tv_version_code)
    TextView tvCode;//版本號操作
    @BindView(R.id.check_box_login)
    CheckBox cbRemember;//記住密碼
    @BindView(R.id.tv_work)
    TextView tvWork;//工作互聯

    private String result;//登錄返回結果
    private List<UserMessage> userMessageList;
    private String versionResult;//版本返回結果
    private List<VersionMessage> versionMessageList;
    private int ersionCode;//本機APP版本號
    private int getVersionCode;//獲取的最新版本號
    private Handler handlerM = new Handler();//更新版本下載
    private ProgressDialog pBar;
    private SharedPreferences sharedPreferences;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //检查权限，防止重复获取
        mPermissions = PermissionUtil.getDeniedPermissions(this, mPermissions);
        if (mPermissions!=null) {
            /**
             * 1.上下文
             * 2.权限失败后弹出对话框的内容
             * 3.requestCode
             * 4.要申请的权限
             */
            EasyPermissions.requestPermissions(this, PermissionUtil.permissionText(mPermissions), CODE, mPermissions);
        }

        btnLogin.setOnClickListener(this);
        tvCode.setOnClickListener(this);
        tvWork.setOnClickListener(this);
        cbRemember.setOnClickListener(this);

        isVersionUpdate();
        delBottonShow();
        delete();
        getPoint();
        try {
            ersionCode = getApplication().getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            Log.e("-----code", "本機版本===" + ersionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        sharedPreferences = getSharedPreferences("rememberpassword", Context.MODE_PRIVATE);
        boolean isRemember = sharedPreferences.getBoolean("rememberpassword", false);
        if (isRemember) {
            String name = sharedPreferences.getString("name", "");
            String password = sharedPreferences.getString("password", "");
            editName.setText(name);
            editPass.setText(password);
            cbRemember.setChecked(true);
        }
    }

    //所有的权限申请成功的回调
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //do something
    }

    //权限获取失败的回调
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //存在被永久拒绝(拒绝&不再询问)的权限
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            mPermissions = PermissionUtil.getDeniedPermissions(this, mPermissions);
            PermissionUtil.PermissionDialog(this, PermissionUtil.permissionText(mPermissions) + "请在应用权限管理进行设置！");
        } else {
            EasyPermissions.requestPermissions(this, PermissionUtil.permissionText(mPermissions), CODE, mPermissions);
        }
    }

    //权限被拒绝后的显示提示对话框，点击确认的回调
    @Override
    public void onRationaleAccepted(int requestCode) {
        //会自动再次获取没有申请成功的权限
        //do something
    }

    //权限被拒绝后的显示提示对话框，点击取消的回调
    @Override
    public void onRationaleDenied(int requestCode) {
        //什么都不会做
        //do something
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将结果传入EasyPermissions中
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /*清空用户名或密码*/
    private void delete() {
        delUserEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editName.setText("");
            }
        });
        delPassEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPass.setText("");
            }
        });
    }

    /*输入框获取焦点*/
    private void getPoint() {
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editName.requestFocus();
            }
        });
        passLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPass.requestFocus();
            }
        });
    }

    /*清空按钮 显示或隐藏*/
    private void delBottonShow() {
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    delUserEdit.setAlpha(0);
                } else {
                    delUserEdit.setAlpha(1);
                }
            }
        });
        editPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    delPassEdit.setAlpha(0);
                } else {
                    delPassEdit.setAlpha(1);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                checkUserInput();

//                Intent intent = new Intent(LoginActivity.this, MyOptionActivity.class);
//                Intent intent = new Intent(LoginActivity.this, MyMapActivity.class);
//                startActivity(intent);
                break;
            case R.id.tv_version_code:
//                tvCode.setText("當前版本為："+ersionCode);
                if (versionResult==null){
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                }else{
                Intent checkIntent = new Intent(LoginActivity.this, CheckVersionActivity.class);
                checkIntent.putExtra("id", versionMessageList.get(0).getId());
                checkIntent.putExtra("remark", versionMessageList.get(0).getRemark());
                startActivity(checkIntent);
                }
                break;
            case R.id.tv_work:
                Intent intent= new Intent(LoginActivity.this, WebViewTestActivity.class);
                startActivity(intent);
                break;
        }
    }

    /*非空判断*/
    public void checkUserInput() {
        if (TextUtils.isEmpty(editName.getText().toString())||TextUtils.isEmpty(editPass.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.forbid_userId_empty,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (editName.getText().toString().contains(" ")||editPass.getText().toString().contains(" ")) {
            Toast.makeText(getApplicationContext(), R.string.forbid_userPsd_empty,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!FoxContext.getInstance().isUpdate()) {
            Toast.makeText(getApplicationContext(), R.string.version_update,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String name = editName.getText().toString();
        String password = editPass.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (cbRemember.isChecked()) {
            editor.putBoolean("rememberpassword", true);
            editor.putString("name", name);
            editor.putString("password", password);

        } else {
            editor.clear();

        }
        editor.commit();

        showDialog();
        final String url = Constants.HTTP_LOGIN + "?id=" + name + "&pwd=" + password;
        new Thread() {
            @Override
            public void run() {
                result = HttpUtils.queryStringForPost(url);
                dismissDialog();

                Gson gson = new Gson();
                Log.e("----fff-url----", url.toString());
                if (result != null) {

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        userMessageList = new ArrayList<UserMessage>();
                        for (JsonElement type : array) {
                            UserMessage humi = gson.fromJson(type, UserMessage.class);
                            userMessageList.add(humi);
                        }
                        FoxContext.getInstance().setName(userMessageList.get(0).getXg_name());
                        FoxContext.getInstance().setLoginId(userMessageList.get(0).getXg_id());
                        FoxContext.getInstance().setRoles(userMessageList.get(0).getXg_role());
                        FoxContext.getInstance().setDep(userMessageList.get(0).getXg_dep());

                        if(!userMessageList.get(0).getXg_phone().equals("") && !userMessageList.get(0).getXg_tel().equals("")){
                        FoxContext.getInstance().setContact(userMessageList.get(0).getXg_phone()+"/"+userMessageList.get(0).getXg_tel());
                        } else if (!userMessageList.get(0).getXg_phone().equals("")){
                            FoxContext.getInstance().setContact(userMessageList.get(0).getXg_phone());
                        } else if (!userMessageList.get(0).getXg_tel().equals("")){
                            FoxContext.getInstance().setContact(userMessageList.get(0).getXg_tel());
                        } else {
                            FoxContext.getInstance().setContact("請填寫聯繫方式");
                        }

                        if (userMessageList.get(0).getNeed_flag().equals("Y") && userMessageList.get(0).getNeed_result().equals("N")) {
//                            Log.e("--======", "111111111111");
                            Intent intent = new Intent(LoginActivity.this, ActualPeopleActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                            Message message = new Message();
                            message.what = MESSAGE_OTHER;

                            mHandler.sendMessage(message);
                        }
                    } else {
//                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }

                } else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }


    //是否需要更新
    public void isVersionUpdate() {
//        Log.e("---------", "isVersionUpdate===");
        showDialog();
        final String urlVersion = Constants.HTTP_GET_VERSION_CODE;
        new Thread() {
            @Override
            public void run() {
                versionResult = HttpUtils.queryStringForPost(urlVersion);
//                Log.e("---------", "fffff===" + urlVersion.toString());
                dismissDialog();
                Gson gson = new Gson();
//                Log.e("-----i----", "isVersionUpdate");
                if (versionResult != null) {
//                    Log.e("---------", "fffff===" + versionResult.toString());
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
                }else{
                    Message message = new Message();
                    message.what = MESSAGE_NETMISTAKE;
                    mHandler.sendMessage(message);
                    FoxContext.getInstance().setUpdate(true);
                }
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast
                    ToastUtils.showShort(LoginActivity.this, msg.obj.toString());
                    break;
                case MESSAGE_NETMISTAKE://Toast
                    ToastUtils.showLong(LoginActivity.this,R.string.net_mistake);
                    break;
                case MESSAGE_VISION://是否更新的彈出框
                    isUpdate();
                    break;
                case MESSAGE_OTHER://其他操作
                    if (!cbRemember.isChecked()) {
                        editName.setText("");
                        editPass.setText("");
                    }
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

        if (ersionCode < getVersionCode) {
            //跳轉下載最新版本apk方法
//            Log.e("----------------", "false=="+Constants.HTTP_GET_APK + versionMessageList.get(0).getId() + ".apk");
            Dialog dialog = new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("软件更新")
                    .setMessage(versionMessageList.get(0).getRemark().toString())
                    // 设置内容
                    .setPositiveButton("更新",// 设置确定按钮
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    pBar = new ProgressDialog(LoginActivity.this);
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
//            Log.e("----------------", "true");
        }
    }

    /**
     * 下載最新版本apk
     *
     * @param url
     */
    private void downFile(final String url) {
        pBar.show();
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//沒這句,最后不会提示完成、打开。
        Uri apkFileUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            apkFileUri = FileProvider.getUriForFile(this, "com.example.administrator.yanfoxconn.provider", apkFile);

        } else {
            apkFileUri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(apkFileUri, "application/vnd.android.package-archive");

        startActivity(intent);
        //android.os.Process.killProcess(android.os.Process.myPid());//沒這句,安装好了，点打开，是不会打开新版本应用的。
    }

}
