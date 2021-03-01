package com.example.administrator.yanfoxconn.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.adapter.ProductDivisionListAdapter;
import com.example.administrator.yanfoxconn.bean.ProductDivisionMessage;
import com.example.administrator.yanfoxconn.bean.RouteMessage;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.HttpUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDivisionActivity extends BaseActivity implements View.OnClickListener{
    private final int MESSAGE_TOAST = 2;//showToast

    private String url;//請求地址
    private String result;//返回結果
    private List<RouteMessage> routeMessageList;//巡檢點信息列表

    public String[][] proName = {
            {"車間巡檢", "天台巡檢","安全出口巡檢"
              ,"成型巡檢","沖壓巡檢","組裝巡檢", "塗裝巡檢","SMT巡檢"
              ,"沖壓消殺", "成型消殺","組裝消殺","SMT消殺", "塗裝消殺"},//NME
            {"VIP巡檢"},//VIP
            {"製二安環&值星", "製二層主","PME製一(E區)E化系統-日","PME製一(E區)E化系統-週","製一(八角)巡檢",
                    "制二兼職安全員","製二棟主(企業負責人)"},//PME
            {"EBL巡檢","TV製造巡檢"},//EBL
            {"PWB巡檢"},//PWB
            {"HEC消殺巡檢"},//HEC
            {"MDI巡檢"},//MDI
            {"A區生活區", "A區廠區", "C區", "E區", "八角", "宿舍区A/E"},//總務消殺巡檢
            { "A區", "C區", "D區", "G區", "E區","A區生活區","E區生活區"},//工安巡檢
            { "電源供應器","靜電試驗機","插拔試驗機","冷熱衝擊柜","恆溫恆濕柜","推拉力試驗機","高空試驗機",
              "高溫台車","電源模擬器","衝擊試驗機","靜電放電模擬器"},//品質保證處QA
            { "X光檢測機", "研磨機", "電特性檢測設備", "溫度測定儀","電子顯微鏡"},//品質保證處SMT
            { "光譜儀", "卡尺校正儀", "多功能校準器", "三次元","臺式色差儀器","數字萬用表"},//品質保證處ME
            { "放電加工機", "線割加工機", "工安巡檢"},//模具
            { "廢料點檢", "污水及廢氣點檢", "危廢倉點檢","棧板廠點檢"}//環境安全
    };
    public String[][] proRole = {
            {"U0", "BD","BE","CH","HJ","HH","HI","HF"
             ,"CZ","DU","DV","DW","DX"},//NME
            {"BF"},//VIP
            {"BR", "CG","DY","GP","CJ","BU","CW"},//PME，原CF可以刪除
            {"CP","CS"},//EBL
            {"CV"},//PWB
            {"EB"},//HEC
            {"GO"},//MDI
            {"DA","DB","DC","DD","DE","DF"},//總務消殺巡檢
            {"DG","DH","DI","DJ","DK","DL","DM"},//工安巡檢
            {"EE","EF","EM","EP","EQ","ER","ES","ET","EY","FC","FD"},//品質保證處QA
            {"EG","EJ","EL","EW","EX"},//品質保證處SMT
            {"EH","EK","EN","EU","FA","FB"},//品質保證處ME
            {"GY","GZ","HG"},//模具
            {"HM","HO","HN","HP"}//環境安全
    };
    public int[][] proIcon = {
            {R.mipmap.icon_chejian, R.mipmap.icon_tiantai, R.mipmap.icon_anquanchu,
                    R.mipmap.icon_pmezhiyi,R.mipmap.icon_pmezhiyi,R.mipmap.icon_pmezhiyi,R.mipmap.icon_pmezhiyi,R.mipmap.icon_pmezhiyi,
                    R.mipmap.icon_nmexiaosha, R.mipmap.icon_nmexiaosha, R.mipmap.icon_nmexiaosha, R.mipmap.icon_nmexiaosha,R.mipmap.icon_nmexiaosha},//NME
            {R.mipmap.icon_vip_safe},//VIP
            {R.mipmap.icon_pme_safe, R.mipmap.icon_pmecengzhu, R.mipmap.icon_pmezhiyi,
                    R.mipmap.icon_pmezhiyi, R.mipmap.icon_anquanchu, R.mipmap.icon_jianzhi,
                    R.mipmap.icon_pme_2},//PME
            {R.mipmap.icon_ebl_safe,R.mipmap.icon_ebl_tv},//EBL
            {R.mipmap.icon_pwb},//PWB
            {R.mipmap.icon_nmexiaosha},//HEC
            {R.mipmap.icon_nmexiaosha},//MDI
            {R.mipmap.icon_a,R.mipmap.icon_b,R.mipmap.icon_c,R.mipmap.icon_e,R.mipmap.icon_d,
                    R.mipmap.icon_df},//總務消殺巡檢
            {R.mipmap.icon_a,R.mipmap.icon_b,R.mipmap.icon_c,R.mipmap.icon_e,R.mipmap.icon_d,
                    R.mipmap.icon_df,R.mipmap.icon_e},//工安巡檢
            {R.mipmap.icon_a,R.mipmap.icon_b,R.mipmap.icon_c,R.mipmap.icon_e,R.mipmap.icon_d,
                    R.mipmap.icon_df,R.mipmap.icon_e,R.mipmap.icon_e,R.mipmap.icon_e,R.mipmap.icon_e,
                    R.mipmap.icon_e},//品質保證處QA
            {R.mipmap.icon_a,R.mipmap.icon_b,R.mipmap.icon_c,R.mipmap.icon_e,R.mipmap.icon_d},//品質保證處SMT
            {R.mipmap.icon_a,R.mipmap.icon_b,R.mipmap.icon_c,R.mipmap.icon_e,R.mipmap.icon_d,
                    R.mipmap.icon_df},//品質保證處ME
            {R.mipmap.icon_a,R.mipmap.icon_b,R.mipmap.icon_c},//模具
            {R.mipmap.icon_a,R.mipmap.icon_b,R.mipmap.icon_c,R.mipmap.icon_d}//環境安全
    };

    private List<ProductDivisionMessage> mProductDivisionMessagelist;

    private List<String> proNameList;
    private List<String> proRoleList;
    private List<Integer> proIconList;

    private Bundle bundle;//接收intent
    private String type;//產品處類別

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.lv_pro_div)
    MyListView lvProDiv;//選項

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_division);
        ButterKnife.bind(this);

        bundle = this.getIntent().getExtras();
        type = bundle.getString("type");

        tvTitle.setText(bundle.getString("title"));

        btnBack.setOnClickListener(this);
        Log.e("-----------", "onCreate:type "+ type );

        initProductDivisionMessage();

        ProductDivisionListAdapter adapter = new ProductDivisionListAdapter(ProductDivisionActivity.this,mProductDivisionMessagelist);
        lvProDiv.setAdapter(adapter);

        lvProDiv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String role=mProductDivisionMessagelist.get(i).getRole();
                if (FoxContext.getInstance().getRoles().contains(role)&&role.equals("BU")){
                    Intent intent = new Intent(ProductDivisionActivity.this,OutKeepFirstActivity.class);
                    intent.putExtra("title", "PME兼職安全員");
                    intent.putExtra("type", "BU");
                    startActivity(intent);
                }else if(FoxContext.getInstance().getRoles().contains(role)&&(role.equals("CZ")||role.equals("DU")||role.equals("DV")
                        ||role.equals("DW")||role.equals("DX")||role.equals("EB")||role.equals("DY")||role.equals("GP")||role.equals("GO")
                        ||role.equals("HG")||role.equals("HM")||role.equals("HO")||role.equals("HP")||role.equals("HN"))){
                    FoxContext.getInstance().setType(role);
                    Intent intent = new Intent(ProductDivisionActivity.this, QrCodeActivity.class);
                    intent.putExtra("title", "掃描二維碼");
                    intent.putExtra("num", "cz");
                    startActivity(intent);//點位巡檢
                }else if(FoxContext.getInstance().getRoles().contains(role)&&(role.equals("EE")||role.equals("EF")
                        ||role.equals("EG")||role.equals("EH")||role.equals("EJ")||role.equals("EK")||role.equals("EL")
                        ||role.equals("EN")||role.equals("EP")||role.equals("EQ")||role.equals("ER")||role.equals("ES")
                        ||role.equals("ET")||role.equals("EU")||role.equals("EW")||role.equals("EX")||role.equals("EY")
                        ||role.equals("FA")||role.equals("FB")||role.equals("FC")||role.equals("FD")||role.equals("EM"))){
                    Intent intent = new Intent(ProductDivisionActivity.this, ZhiyinshuiMenuActivity.class);
                    intent.putExtra("title", "品質保證處");
                    intent.putExtra("type", role);
                    intent.putExtra("name", mProductDivisionMessagelist.get(i).getName());
                    startActivity(intent);//品質保證處設備巡檢
                }else if(FoxContext.getInstance().getRoles().contains(role)&&(role.equals("GY")||role.equals("GZ"))){
                    Intent intent = new Intent(ProductDivisionActivity.this, ZhiyinshuiMenuActivity.class);
                    intent.putExtra("title", "模具加工");
                    intent.putExtra("type", role);
                    intent.putExtra("name", mProductDivisionMessagelist.get(i).getName());
                    startActivity(intent);//模具加工設備巡檢
                }else if(FoxContext.getInstance().getRoles().contains(mProductDivisionMessagelist.get(i).getRole())){
                    getRouteList(mProductDivisionMessagelist.get(i).getRole());
                }else{
                    ToastUtils.showShort(ProductDivisionActivity.this,"您沒有該巡檢權限,請確認!");
                }
            }
        });
    }

    private void initProductDivisionMessage(){
        mProductDivisionMessagelist = new ArrayList<ProductDivisionMessage>();
        proNameList = new ArrayList<>();
        proRoleList = new ArrayList<>();
        proIconList = new ArrayList<>();
        if(type.equals("NME")){
            for (int i = 0; i < proName[0].length; i++) {
                proNameList.add(proName[0][i]);
                proRoleList.add(proRole[0][i]);
                proIconList.add(proIcon[0][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }
        }else if (type.equals("VIP")){
            for (int i = 0; i < proName[1].length; i++) {
                proNameList.add(proName[1][i]);
                proRoleList.add(proRole[1][i]);
                proIconList.add(proIcon[1][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }

        }else if (type.equals("PME")){
            for (int i = 0; i < proName[2].length; i++) {
                proNameList.add(proName[2][i]);
                proRoleList.add(proRole[2][i]);
                proIconList.add(proIcon[2][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }
        }else if (type.equals("EBL")){
            for (int i = 0; i < proName[3].length; i++) {
                proNameList.add(proName[3][i]);
                proRoleList.add(proRole[3][i]);
                proIconList.add(proIcon[3][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }
        }else if (type.equals("PWB")){
            for (int i = 0; i < proName[4].length; i++) {
                proNameList.add(proName[4][i]);
                proRoleList.add(proRole[4][i]);
                proIconList.add(proIcon[4][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }
        }else if (type.equals("HEC")){
            for (int i = 0; i < proName[5].length; i++) {
                proNameList.add(proName[5][i]);
                proRoleList.add(proRole[5][i]);
                proIconList.add(proIcon[5][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }
        }else if (type.equals("MDI")){
            for (int i = 0; i < proName[6].length; i++) {
                proNameList.add(proName[6][i]);
                proRoleList.add(proRole[6][i]);
                proIconList.add(proIcon[6][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }
        }else if (type.equals("ZXS")){
            for (int i = 0; i < proName[7].length; i++) {
                proNameList.add(proName[7][i]);
                proRoleList.add(proRole[7][i]);
                proIconList.add(proIcon[7][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }
        }else if (type.equals("GAN")){
            for (int i = 0; i < proName[8].length; i++) {
                proNameList.add(proName[8][i]);
                proRoleList.add(proRole[8][i]);
                proIconList.add(proIcon[8][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }
        }else if (type.equals("QAQ")){
            for (int i = 0; i < proName[9].length; i++) {
                proNameList.add(proName[9][i]);
                proRoleList.add(proRole[9][i]);
                proIconList.add(proIcon[9][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }
        }else if (type.equals("SMT")){
            for (int i = 0; i < proName[10].length; i++) {
                proNameList.add(proName[10][i]);
                proRoleList.add(proRole[10][i]);
                proIconList.add(proIcon[10][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }
        }else if (type.equals("MEM")){
            for (int i = 0; i < proName[11].length; i++) {
                proNameList.add(proName[11][i]);
                proRoleList.add(proRole[11][i]);
                proIconList.add(proIcon[11][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }
        }else if (type.equals("MUJ")){
            for (int i = 0; i < proName[12].length; i++) {
                proNameList.add(proName[12][i]);
                proRoleList.add(proRole[12][i]);
                proIconList.add(proIcon[12][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }
        }else if (type.equals("HAN")){
            for (int i = 0; i < proName[13].length; i++) {
                proNameList.add(proName[13][i]);
                proRoleList.add(proRole[13][i]);
                proIconList.add(proIcon[13][i]);
                ProductDivisionMessage productDivisionMessage = new ProductDivisionMessage(proNameList.get(i),proRoleList.get(i),proIconList.get(i));
                mProductDivisionMessagelist.add(productDivisionMessage);
            }
        }else {
            ToastUtils.showShort(ProductDivisionActivity.this,"選擇產品處出錯");
        }
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    /**
     * 根據角色權限,獲取巡檢路線列表
     * @param type
     */

    public void getRouteList(String type) {
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
                dismissDialog();
                if (result != null) {
                    Log.e("---------", "result==fff===" + response);
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    String errCode = jsonObject.get("errCode").getAsString();
                    if (errCode.equals("200")) {

                        JsonArray array = jsonObject.get("data").getAsJsonArray();
                        routeMessageList = new ArrayList<RouteMessage>();
                        for (JsonElement type : array) {
                            RouteMessage humi = gson.fromJson(type, RouteMessage.class);
                            routeMessageList.add(humi);
                        }
                        Intent intent = new Intent(getApplication(), RouteListActivity.class);
                        intent.putExtra("routeList", (Serializable) routeMessageList);
                        intent.putExtra("Dflag", "");//用於判定是否為宿舍巡檢,以方便按樓棟傳值,其他地方值為空
                        startActivity(intent);


                    } else {
                        Message message = new Message();
                        message.what = MESSAGE_TOAST;
                        message.obj = jsonObject.get("errMessage").getAsString();
                        mHandler.sendMessage(message);
                    }
                } else {
                    Message message = new Message();
                    message.what = MESSAGE_TOAST;
                    message.obj = "無數據";
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST://Toast彈出
                    if (msg.obj.equals("失敗")) {
                        ToastUtils.showShort(ProductDivisionActivity.this, "沒有巡檢數據!");
                    } else {
                        ToastUtils.showShort(ProductDivisionActivity.this, msg.obj.toString());
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
