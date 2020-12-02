package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.DNCheckListAdapter;
import com.example.administrator.yanfoxconn.bean.DNCheckMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.SwipeListView;
import com.example.administrator.yanfoxconn.widget.ThreePopupWindow;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 宿舍查房 人員列表界面
 * Created by song on 2017/8/29.
 */

public class DNCheckListActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_DIALOG = 0;
    private final int MESSAGE_UPLOAD = 1;
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_SEE_ABNORMAL = 3;
    private final int MESSAGE_SELF_ABNORMAL = 4;

    private final static int SCANNIN_GREQUEST_CODE = 1;

//    public static final int MENU_QRCODE = 0;//二維碼掃描
    public static final int MENU_ADD_ERROR = 1;//上報異常
//    public static final int MENU_QUERY_ERROR = 2;//查詢異常
//    public static final int MENU_SELF_ERROR = 3;//刪除異常

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.ib_title_right)
    ImageButton ibRight;//右側彈出菜單
    @BindView(R.id.lv_dn_list)
    SwipeListView lvDNList;//巡檢進度表
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    private ThreePopupWindow threePopupWindow;


    private List<DNCheckMessage> dnCheckMessageList;
    private DNCheckListAdapter dnListAdapter;

//    private List<AbnormalMessage> abnormalMessageList;

    private String url;//請求地址
    private String[] qrResult;//二維碼內含結果
    private String result;//接口返回結果
    private String getQrMessage;//二維碼內容

    private int qrCount = 0;//總的掃描次數
    private String nowDateTime; // 獲取當前时间

    private String flag;//查詢標誌
    private String id;//
    private int width;
    private  int height;
    private String dFlag;//判斷是否為宿舍巡檢,用不同方法更新列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dn_check_list);
        ButterKnife.bind(this);
        WindowManager wm = this.getWindowManager();

         width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        Log.e("------", "MainActivityGao==width=="+width+"==height=="+height);
//        dnCheckMessageList = (ArrayList<DNCheckMessage>) getIntent().getSerializableExtra("routeList");
//        dFlag =  getIntent().getStringExtra("Dflag");
        getQrMessage = getIntent().getStringExtra("result");
        qrResult = getQrMessage.split(",");

        tvTitle.setText(qrResult[1]+"-"+qrResult[3]);
        btnBack.setVisibility(View.VISIBLE);
        ibRight.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(this);
        ibRight.setOnClickListener(this);

        ibRight.setBackgroundResource(R.mipmap.title_more_icon);

        getDNList();

    }

    private void clickSeeOrAdd() {
        dnListAdapter.setOnClickListenerSeeOrAdd(new DNCheckListAdapter.OnClickListenerSeeOrAdd() {
            @Override
            public void OnClickListenerSee(int position) {
                Intent intent = new Intent(DNCheckListActivity.this, DNLookListActivity.class);
                intent.putExtra("flag", "R");
                intent.putExtra("qrResult",getQrMessage);
                intent.putExtra("dnCheckMessageList",(Serializable) dnCheckMessageList.get(position));

                startActivity(intent);
            }

            @Override
            public void OnClickListenerAdd(int position) {

//                        ToastUtils.showShort(DNCheckListActivity.this, "添加");
//                    Intent intent = new Intent(DNCheckListActivity.this, DNCheckActivity.class);
//                    intent.putExtra("flag", "R");
//                    intent.putExtra("scid", dnCheckMessageList.get(position).getSc_id());
//                    intent.putExtra("address", dnCheckMessageList.get(position).getDim_locale());
//                    startActivity(intent);
                Intent intent = new Intent(DNCheckListActivity.this, DNCheckActivityNew.class);
                intent.putExtra("flag", "R");
                intent.putExtra("qrResult",getQrMessage);
                intent.putExtra("dnCheckMessageList",(Serializable) dnCheckMessageList.get(position));

                startActivity(intent);

            }
        });
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                FoxContext.getInstance().setTakePic("");
                break;
            case R.id.ib_title_right:

                if (threePopupWindow == null) {
                    // 自定义的单击事件
                    OnClickLintener paramOnClickListener = new OnClickLintener();
                    threePopupWindow = new ThreePopupWindow(DNCheckListActivity.this,
                            paramOnClickListener, width/2,
                            height/3,"DN");
                    // 监听窗口的焦点事件，点击窗口外面则取消显示
                    threePopupWindow.getContentView().setOnFocusChangeListener(
                            new View.OnFocusChangeListener() {

                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        threePopupWindow.dismiss();
                                    }
                                }
                            });
                }
                //设置默认获取焦点
                threePopupWindow.setFocusable(true);
                //以某个控件的x和y的偏移量位置开始显示窗口
                threePopupWindow.showAsDropDown(ibRight, 0, 0);
                //如果窗口存在，则更新
                threePopupWindow.update();
                break;
        }
    }

    class OnClickLintener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_one:
                    Intent intent = new Intent(DNCheckListActivity.this, DNCheckActivityNew.class);
                    intent.putExtra("flag", "H");
                    intent.putExtra("qrResult",getQrMessage);
                    intent.putExtra("dnCheckMessageList",(Serializable)null);
                    intent.putExtra("hChanPin",hChanPin);
                    startActivity(intent);

                    break;
                case R.id.ll_two:
                    Intent intent1 = new Intent(DNCheckListActivity.this, DNLookListActivity.class);
                    intent1.putExtra("flag", "H");
                    intent1.putExtra("qrResult",getQrMessage);
                    intent1.putExtra("dnCheckMessageList",(Serializable)null);

                    startActivity(intent1);
                    break;
                case R.id.ll_three:
                    Intent intentR = new Intent(DNCheckListActivity.this, DNReformListActivity.class);
                    intentR.putExtra("qrResult",getQrMessage);
                    intentR.putExtra("from","self");
                    startActivity(intentR);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DIALOG://掃描后彈出
//                    aboutAlert(result.toString());
                    break;
                case MESSAGE_UPLOAD://刷新列表
                    dnListAdapter = new DNCheckListAdapter(DNCheckListActivity.this, dnCheckMessageList);
                    lvDNList.setAdapter(dnListAdapter);
                    setListViewHeightBasedOnChildren(lvDNList);
                    clickSeeOrAdd();
                    break;
                case MESSAGE_TOAST://Toast彈出
                        ToastUtils.showShort(DNCheckListActivity.this, msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 用於解決scrollview嵌套listview導致只顯示一條數據的問題
     * @param listView
     */

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        DNCheckListAdapter listAdapter = (DNCheckListAdapter) listView.getAdapter();
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

    private String hChanPin="";//分兩個產品處,環境用外圍jc_chanpin,人員用內圍jc_chanpin
    //刷新人員列表
    private void getDNList() {
        showDialog();
        String room=null;//房間
        String building=null;//棟
        String area=null;//區
        try {
             area = URLEncoder.encode(URLEncoder.encode(qrResult[0], "UTF-8"), "UTF-8");
             building = URLEncoder.encode(URLEncoder.encode(qrResult[1].toString(), "UTF-8"), "UTF-8");
             room = URLEncoder.encode(URLEncoder.encode(qrResult[3], "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
            url = Constants.HTTP_SUSHE_BEDSERVLET + "?area=" + area+"&building="+building+"&room="+room;

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
                    hChanPin= jsonObject.get("jc_chanpin").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        dnCheckMessageList = new ArrayList<DNCheckMessage>();
                        for (JsonElement type : array) {
                            DNCheckMessage humi = gson.fromJson(type, DNCheckMessage.class);
                            dnCheckMessageList.add(humi);
                        }
                        Message message = new Message();
                        message.what = MESSAGE_UPLOAD;
                        mHandler.sendMessage(message);

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
                    message.obj ="請求不成功,請退出重試!";
             }
            }
        }.start();
    }
}
