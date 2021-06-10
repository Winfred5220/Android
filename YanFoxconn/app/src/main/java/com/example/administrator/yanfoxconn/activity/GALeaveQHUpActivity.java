package com.example.administrator.yanfoxconn.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.CarListAdapter;
import com.example.administrator.yanfoxconn.adapter.GALeaveQJMsgAdapter;
import com.example.administrator.yanfoxconn.bean.GAWork;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description 临时工请假，主管签核界面
 * @Author song
 * @Date 6/8/21 9:09 AM
 */
public class GALeaveQHUpActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_TOAST = 2;//showToast
     private final int MESSAGE_TOAST_NULL = 0;//showToast
    private final int MESSAGE_SET_TEXT = 1;//獲取狀態后,設置控件內容
    private final int MESSAGE_SET_QJ_TEXT = 5;//獲取狀態后,設置控件內容
    private final int MESSAGE_NOT_NET = 3;//網絡問題
    private final int MESSAGE_UP = 4;//提交响应

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnAdd;
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.lv_qj_msg)
    MyListView lvQJMsg;

    private GALeaveQJMsgAdapter adapter;
    private List<GAWork> gaWorks;
    private String from;//emp员工，zg主管
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ga_leave_lv_main);
        ButterKnife.bind(this);

        tvTitle.setText("请假界面");
        btnBack.setText("返回");
        btnBack.setOnClickListener(this);

        from = getIntent().getStringExtra("from");
        if (from.equals("emp")){
            btnAdd.setVisibility(View.VISIBLE);
            btnAdd.setText("新增");
            btnAdd.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_title_right:
                Intent intent = new Intent(GALeaveQHUpActivity.this,GALeaveMainActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 主管获取需要签核的请假列表信息
     * @param type
     * @param creatorId 临时工工号
     */
    private void getQJMsg(String type,String creatorId){
        showDialog();
        final String url = Constants.HTTP_ZW_LEAVE_GET_QJ_MSG+"?creator_id="+creatorId+"&type="+type;

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
                        message.what = MESSAGE_SET_QJ_TEXT;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else{
                        Message message = new Message();
                        message.what = MESSAGE_TOAST_NULL;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            } }.start();
    }
    /**
     * 主管获取需要签核的请假列表信息
     * @param type
     * @param zgId 主管工号
     */
    private void getQHMsg(String type,String zgId){
        showDialog();
        final String url = Constants.HTTP_ZW_LEAVE_GET_QH_MSG+"?zgId="+zgId+"&type="+type;

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
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
                    getQHMsg(FoxContext.getInstance().getType(),FoxContext.getInstance().getLoginId());
                    ToastUtils.showLong(GALeaveQHUpActivity.this, msg.obj.toString());
//                    finish();
                    break;
                    case MESSAGE_TOAST_NULL://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);
                    ToastUtils.showLong(GALeaveQHUpActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    if (gaWorks != null) {

                        adapter = new GALeaveQJMsgAdapter(GALeaveQHUpActivity.this, gaWorks,from);
                        lvQJMsg.setAdapter(adapter);
                        setListViewHeightBasedOnChildren(lvQJMsg);
                        clickGoorup();
                    } else {
                        ToastUtils.showShort(GALeaveQHUpActivity.this, "沒有數據!");
                    }
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_NOT_NET://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

                    ToastUtils.showLong(GALeaveQHUpActivity.this, "网络问题请稍后重试！");
//                    finish();
                    break;
                case MESSAGE_SET_QJ_TEXT:
                    if (gaWorks != null) {

                        adapter = new GALeaveQJMsgAdapter(GALeaveQHUpActivity.this, gaWorks,from);
                        lvQJMsg.setAdapter(adapter);
                        setListViewHeightBasedOnChildren(lvQJMsg);
                        clickGoorup();
                    } else {
                        ToastUtils.showShort(GALeaveQHUpActivity.this, "沒有數據!");
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void TuJianUp(int position,String reason){
        final String url = Constants.HTTP_ZW_LEAVE_TJ_UP; //此處寫上自己的URL

        JsonObject object = new JsonObject();

        object.addProperty("qj_id", gaWorks.get(position).getQj_id());
        object.addProperty("tj_reason", reason);
        object.addProperty("tj_statue","Y");

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

    private void QianHeUp(int position){
        final String url = Constants.HTTP_ZW_LEAVE_QH_UP; //此處寫上自己的URL

        JsonObject object = new JsonObject();

        object.addProperty("qj_id", gaWorks.get(position).getQj_id());
        object.addProperty("qj_statue","Y");

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

    public void clickGoorup() {
        adapter.setOnClickListenerLeftOrRight(new GALeaveQJMsgAdapter.OnClickListenerLeftOrRight() {
            @Override
            public void OnClickListenerLeft(int position) {
                QianHeUp(position);
            }

            @Override
            public void OnClickListenerRight(int position) {

                TJReasonAlert("请选择退件原因", MESSAGE_TOAST,position);
            }
        });
    }
    /**
     * 用於解決scrollview嵌套listview導致只顯示一條數據的問題
     * @param listView
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        GALeaveQJMsgAdapter listAdapter = (GALeaveQJMsgAdapter) listView.getAdapter();
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
    protected void onStart() {
        super.onStart();
        if (from.equals("emp")){
            getQJMsg(FoxContext.getInstance().getType(),FoxContext.getInstance().getLoginId());
        }else{
        getQHMsg(FoxContext.getInstance().getType(),FoxContext.getInstance().getLoginId());
    }}

    public String[] tjReason = {"one", "two", "three", "香港", "澳门"};
    public String toastText = "";

    private void TJReasonAlert(String msg, final int type,int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息")
                .setMessage(msg)

        .setSingleChoiceItems(tjReason, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToastUtils.showShort(GALeaveQHUpActivity.this,tjReason[which]);
                TuJianUp(position,tjReason[which]);
            }
        })
       .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int id) {
               // TODO Auto-generated method stub
               if (type==MESSAGE_TOAST){
                   finish();
               }
           }})
        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }
}
