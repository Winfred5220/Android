package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.GCPeopleAdapter;
import com.example.administrator.yanfoxconn.adapter.RouteListAdapter;
import com.example.administrator.yanfoxconn.bean.GCHead;
import com.example.administrator.yanfoxconn.bean.GEMenLiu;
import com.example.administrator.yanfoxconn.bean.GEPeopleMsg;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.example.administrator.yanfoxconn.widget.SwipeListView;
import com.example.administrator.yanfoxconn.widget.SwipeListViewThree;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * song 2020/12/3
 * 安保部 健康追蹤 模糊查詢界面
 */
public class GCSerchActivity extends BaseActivity implements View.OnClickListener
{
    private final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    private final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    private final int MESSAGE_DELETE_SUCCESS = 3;//刪除成功，刷新列表
    private final int MESSAGE_NOT_NET = 4;//顯示提醒

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_search)
    Button btnSearch;//搜索
    @BindView(R.id.et_search)
    EditText etSearch;//搜索文字s
    @BindView(R.id.lv_people)
    SwipeListViewThree lvPeople;

    private List<GCHead> gcHeads;
    private GCPeopleAdapter gcPeopleAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gc_search);
        ButterKnife.bind(this);

        btnBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this::onClick);

        tvTitle.setText("體症異常追蹤");


    }
    private void clickSeeOrAdd() {
        gcPeopleAdapter.setOnClickListenerSeeOrAdd(new GCPeopleAdapter.OnClickListenerSeeOrAdd() {
            @Override
            public void OnClickListenerSee(int position) {
                Intent intent = new Intent(GCSerchActivity.this,GCUpOrDoneActivity.class);
                intent.putExtra("people",(Serializable)gcHeads.get(position));
                intent.putExtra("from","end");
                startActivity(intent);
//                ToastUtils.showShort(GCSerchActivity.this,"結案");
            }

            @Override
            public void OnClickListenerDel(int position) {
                delPeople(gcHeads.get(position).getIn_Random_Id());
//                ToastUtils.showShort(GCSerchActivity.this,"刪除");
            }

            @Override
            public void OnClickListenerAdd(int position) {
//                ToastUtils.showShort(GCSerchActivity.this,"追蹤");

                Intent intent = new Intent(GCSerchActivity.this,GCUpOrDoneActivity.class);
                intent.putExtra("people",(Serializable)gcHeads.get(position));
                intent.putExtra("from","add");
                startActivity(intent);

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.btn_search:
search(etSearch.getText().toString());
                break;
        }
    }
    /**
     * 用於解決scrollview嵌套listview導致只顯示一條數據的問題
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        GCPeopleAdapter listAdapter = (GCPeopleAdapter) listView.getAdapter();
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


    private void search(String id){

        showDialog();
        final String url = Constants.HTTP_BODY_SELECT+"?json="+id;

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForGet(url);

                dismissDialog();
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
                        JsonArray array = jsonObject.get("result").getAsJsonArray();
                        gcHeads = new ArrayList<GCHead>();

                        for (JsonElement type : array) {
                            GCHead humi = gson.fromJson(type, GCHead.class);
                            gcHeads.add(humi);
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
                }
            } }.start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
//                    aboutAlert(msg.obj.toString(),MESSAGE_TOAST);

                    ToastUtils.showLong(GCSerchActivity.this, msg.obj.toString());
//                    finish();
                    break;
                case MESSAGE_SET_TEXT://text賦值
                    gcPeopleAdapter = new GCPeopleAdapter(GCSerchActivity.this,gcHeads);
                    lvPeople.setAdapter(gcPeopleAdapter);
                    setListViewHeightBasedOnChildren(lvPeople);
                    clickSeeOrAdd();
//                    setText();
//                    aboutAlert(msg.obj.toString(),MESSAGE_SET_TEXT);
                    break;
                case MESSAGE_DELETE_SUCCESS://提交響應

                    search(etSearch.getText().toString());
                    break;
                case MESSAGE_NOT_NET:
                    ToastUtils.showLong(GCSerchActivity.this, "網絡錯誤，請稍後重試！");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 刪除單頭
     * @param roundId 單頭號
     */
    private void delPeople(String roundId){
        showDialog();
        final String url = Constants.HTTP_HEAD_DELETE+"?In_Random_Id="+roundId;

        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                String result = HttpUtils.queryStringForGet(url);

                dismissDialog();
                Log.e("---------", "==fff===" + url);
                Gson gson = new Gson();
                if (result != null) {
                    Log.e("---------", "result==fff===" + result);

                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {
                        Log.e("--fff---------", "result==" + result);
//                        JsonArray array = jsonObject.get("result").getAsJsonArray();


                        Message message = new Message();
                        message.what = MESSAGE_DELETE_SUCCESS;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);

                    } else{
                        Log.e("-----------", "result==" + result);
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                }
            } }.start();
    }
}
