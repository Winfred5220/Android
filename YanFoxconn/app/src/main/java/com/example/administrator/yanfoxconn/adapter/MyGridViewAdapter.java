package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.administrator.yanfoxconn.R;
import java.util.List;

/**
 * 二級菜單的gridview
 * Created by song on 2017/11/17.
 */

public class MyGridViewAdapter extends BaseAdapter {

    private Context mContext;

    /**
     * 每个分组下的每个子项的 GridView 数据集合
     */
    private List<String> itemGridList;
    /**
     * 每个分组下的每个子项的 GridView 数据集合,角色權限
     */
    private List<String> itemTypeList;

    /**
     * 每个分组下的每个子项的 GridView 数据集合,圖片顯示
     */
    private List<Integer> itemImgList;

    private List<String> rolesList;
    private OnClickListener onClickListener;

    public MyGridViewAdapter(Context mContext, List<String> itemGridList, List<String> itemTypeList, List<String> rolesList, List<Integer> itemImgList) {
        this.mContext = mContext;
        this.itemGridList = itemGridList;
        this.itemTypeList = itemTypeList;
        this.rolesList = rolesList;
        this.itemImgList = itemImgList;
    }

    @Override
    public int getCount() {
        return itemGridList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemGridList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = View.inflate(mContext, R.layout.gridview_item, null);
        }
        TextView tvGridView = (TextView) convertView.findViewById(R.id.tv_gridview);
        tvGridView.setText(itemGridList.get(position));
//        tvGridView.setTag(itemTypeList);
//        tvGridView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.color_009adb));
        Drawable img = convertView.getResources().getDrawable(itemImgList.get(position));
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
        tvGridView.setCompoundDrawables(null, img, null, null);
//        rolesList.add("G");
        for (int i = 0; i < rolesList.size(); i++) {
            if (rolesList.get(i).equals(itemTypeList.get(position))) {
//                tvGridView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                tvGridView.setEnabled(true);
                tvGridView.setClickable(true);
                if (rolesList.get(i).equals("F0") || rolesList.get(i).equals("BT")) {
                    tvGridView.setTag(rolesList.get(i));
                } else if (rolesList.get(i).equals("E0")) {//出口碼頭
                    tvGridView.setTag("e");
                } else if (rolesList.get(i).equals("G0")) {
                    tvGridView.setTag("g");
                } else if (rolesList.get(i).equals("V0")) {
                    tvGridView.setTag("v");
                } else if (rolesList.get(i).equals("BG")) {//宿舍
                    tvGridView.setTag("bg");
                } else if (rolesList.get(i).equals("BK")) {//人工跨區無紙化
                    tvGridView.setTag("bk");
                } else if (rolesList.get(i).equals("X0")) {//活動列表
                    tvGridView.setTag("x");
                } else if (rolesList.get(i).equals("Y0")) {//活動簽到
                    tvGridView.setTag("y");
                } else if (rolesList.get(i).equals("CD")) {//安全部 常用表單
                    tvGridView.setTag("cd");
                } else if (rolesList.get(i).equals("GC")) {//安全部 健康追蹤
                    tvGridView.setTag("gc");
                } else if (rolesList.get(i).equals("CE")) {//安全部 物品放行
                    tvGridView.setTag("ce");
                } else if (rolesList.get(i).equals("CI")) {//HUB倉
                    tvGridView.setTag("ci");
                } else if (rolesList.get(i).equals("CK")) {//叉車巡檢
                    tvGridView.setTag("ck");
                } else if (rolesList.get(i).equals("CL")) {//移動上網設備管控
                    tvGridView.setTag("cl");
                } else if (rolesList.get(i).equals("NME")) {//NME
                    tvGridView.setTag("nme");
                } else if (rolesList.get(i).equals("VIP")) {//VIP
                    tvGridView.setTag("vip");
                } else if (rolesList.get(i).equals("PME")) {//PME
                    tvGridView.setTag("pme");
                } else if (rolesList.get(i).equals("EBL")) {//EBL
                    tvGridView.setTag("ebl");
                } else if (rolesList.get(i).equals("PWB")) {//PWB
                    tvGridView.setTag("pwb");
                } else if (rolesList.get(i).equals("HEC")) {//HEC
                    tvGridView.setTag("hec");
                } else if (rolesList.get(i).equals("MDI")) {//MDI
                    tvGridView.setTag("mdi");
                } else if (rolesList.get(i).equals("MUJ")) {//模具加工
                    tvGridView.setTag("muj");
                } else if (rolesList.get(i).equals("HAN")) {//環境安全
                    tvGridView.setTag("han");
                } else if (rolesList.get(i).equals("CQ")) {//車輛跨區無紙化
                    tvGridView.setTag("cq");
                } else if (rolesList.get(i).equals("CR")) {//廢料出廠
                    tvGridView.setTag("cr");
                } else if (rolesList.get(i).equals("CT")) {//工安巡檢
                    tvGridView.setTag("ct");
                } else if (rolesList.get(i).equals("CU")) {//值班科長
                    tvGridView.setTag("cu");
                } else if (rolesList.get(i).equals("CX")) {//三防隱患
                    tvGridView.setTag("cx");
                } else if (rolesList.get(i).equals("CY")) {//消殺點檢
                    tvGridView.setTag("cy");
                } else if (rolesList.get(i).equals("ZXS")) {//總務消殺巡檢
                    tvGridView.setTag("zxs");
                } else if (rolesList.get(i).equals("GAN")) {//工安巡檢
                    tvGridView.setTag("gan");
                } else if (rolesList.get(i).equals("DN")) {//宿舍查验
                    tvGridView.setTag("dn");
                } else if (rolesList.get(i).equals("DO")) {//人資退訓
                    tvGridView.setTag("do");
                } else if (rolesList.get(i).equals("DP")) {//人資班導
                    tvGridView.setTag("dp");
                } else if (rolesList.get(i).equals("DQ")) {//直飲水
                    tvGridView.setTag("dq");
                } else if (rolesList.get(i).equals("BS")) {//
                    tvGridView.setTag("bs");
                } else if (rolesList.get(i).equals("DZ")) {//人資監餐
                    tvGridView.setTag("dz");
                } else if (rolesList.get(i).equals("K0")) {//一大隊巡邏
                    tvGridView.setTag("ko");
                } else if (rolesList.get(i).equals("L0")) {//二大隊巡邏
                    tvGridView.setTag("lo");
                } else if (rolesList.get(i).equals("M0")) {//三大隊巡邏
                    tvGridView.setTag("mo");
                } else if (rolesList.get(i).equals("R0")) {//機動隊巡邏
                    tvGridView.setTag("ro");
                } else if (rolesList.get(i).equals("H0")) {//一大隊固定
                    tvGridView.setTag("h0");
                } else if (rolesList.get(i).equals("I0")) {//二大隊固定
                    tvGridView.setTag("i0");
                } else if (rolesList.get(i).equals("J0")) {//三大隊固定
                    tvGridView.setTag("j0");
                } else if (rolesList.get(i).equals("S0")) {//機動隊固定
                    tvGridView.setTag("s0");
                } else if (rolesList.get(i).equals("A0")) {//安保值班室
                    tvGridView.setTag("a0");
                } else if (rolesList.get(i).equals("ED")) {//人資教室點檢
                    tvGridView.setTag("ed");
                } else if (rolesList.get(i).equals("EC")) {//碼頭點檢
                    tvGridView.setTag("ec");
                } else if (rolesList.get(i).equals("HR")) {//會議室
                    tvGridView.setTag("hr");
                } else if (rolesList.get(i).equals("HQ")) {//值班室
                    tvGridView.setTag("hq");
                } else if (rolesList.get(i).equals("HS")) {//資料室
                    tvGridView.setTag("hs");
                } else if (rolesList.get(i).equals("FE")) {//洗眼器點檢
                    tvGridView.setTag("fe");
                } else if (rolesList.get(i).equals("FF")) {//危化品暫存柜點檢
                    tvGridView.setTag("ff");
                } else if (rolesList.get(i).equals("FG")) {//危化品暫存倉點檢
                    tvGridView.setTag("fg");
                } else if (rolesList.get(i).equals("QAQ")) {//品質保證處
                    tvGridView.setTag("qaq");
                } else if (rolesList.get(i).equals("SMT")) {//品質保證處
                    tvGridView.setTag("smt");
                } else if (rolesList.get(i).equals("MEM")) {//品質保證處
                    tvGridView.setTag("mem");
                } else if (rolesList.get(i).equals("EA")) {//車輛巡檢
                    tvGridView.setTag("ea");
                } else if (rolesList.get(i).equals("FQ")) {//車調車輛巡檢
                    tvGridView.setTag("fq");
                } else if (rolesList.get(i).equals("FS")) {//營建空調巡檢
                    tvGridView.setTag("fs");
                } else if (rolesList.get(i).equals("FW")) {//營建配電箱巡檢
                    tvGridView.setTag("fw");
                } else if (rolesList.get(i).equals("GD")) {//營建路燈射燈巡檢
                    tvGridView.setTag("gd");
                } else if (rolesList.get(i).equals("FH")) {//總務餐廳巡檢
                    tvGridView.setTag("fh");
                } else if (rolesList.get(i).equals("FV")) {//工業安全-吸煙區
                    tvGridView.setTag("fv");
                } else if (rolesList.get(i).equals("FX")) {//工業安全-鋰電池防火
                    tvGridView.setTag("fx");
                } else if (rolesList.get(i).equals("FZ")) {//工業安全-有限空間
                    tvGridView.setTag("fz");
                } else if (rolesList.get(i).equals("GE")) {//工業安全-室外堆場
                    tvGridView.setTag("ge");
                } else if (rolesList.get(i).equals("GB")) {//越南巡更
                    tvGridView.setTag("gb");
                } else if (rolesList.get(i).equals("GA")) {//總務臨時工
                    tvGridView.setTag("ga");
                }else if (rolesList.get(i).equals("IV")) {//總務臨時工请假 主管签核
                    tvGridView.setTag("iv");
                } else if (rolesList.get(i).equals("FT")) {//華北商務-流動攤位
                    tvGridView.setTag("ft");
                } else if (rolesList.get(i).equals("FU")) {//華北商務-門市房
                    tvGridView.setTag("fu");
                } else if (rolesList.get(i).equals("BV")) {//叉車球車巡檢
                    tvGridView.setTag("bv");
                } else if (rolesList.get(i).equals("GQ")) {//南寧直飲水巡檢
                    tvGridView.setTag("gq");
                } else if (rolesList.get(i).equals("GR")) {//南寧機電巡檢
                    tvGridView.setTag("gr");
                } else if (rolesList.get(i).equals("GS")) {//南寧警衛巡邏
                    tvGridView.setTag("gs");
                } else if (rolesList.get(i).equals("GX")) {//南寧警衛巡邏
                    tvGridView.setTag("gx");
                } else if (rolesList.get(i).equals("GCGL")) {//營建 工程管理
                    tvGridView.setTag("gcgl");
                } else if (rolesList.get(i).equals("HL")) {//總務倉庫
                    tvGridView.setTag("hl");
                } else if (rolesList.get(i).equals("HX")) {//總務餐廳及多媒體
                    tvGridView.setTag("hx");
                } else if (rolesList.get(i).equals("HT")) {//人資會議室
                    tvGridView.setTag("ht");
                } else if (rolesList.get(i).equals("HU")) {//人資資料室
                    tvGridView.setTag("hu");
                } else if (rolesList.get(i).equals("HV")) {//人資倉庫
                    tvGridView.setTag("hv");
                } else if (rolesList.get(i).equals("HW")) {//人資文康場館
                    tvGridView.setTag("hw");
                } else if (rolesList.get(i).equals("HZ")) {//華北商貿倉庫
                    tvGridView.setTag("hz");
                } else if (rolesList.get(i).equals("IA")) {//關愛中心屋室
                    tvGridView.setTag("ia");
                } else if (rolesList.get(i).equals("N0")) {//A
                    tvGridView.setTag("n0");
                } else if (rolesList.get(i).equals("P0")) {//C
                    tvGridView.setTag("p0");
                } else if (rolesList.get(i).equals("O0")) {//E
                    tvGridView.setTag("o0");
                } else if (rolesList.get(i).equals("Q0")) {//D
                    tvGridView.setTag("q0");
                } else if (rolesList.get(i).equals("IB")) {//G
                    tvGridView.setTag("ib");
                } else if (rolesList.get(i).equals("IC")) {//A-5
                    tvGridView.setTag("ic");
                } else if (rolesList.get(i).equals("ID")) {//工業安全會議室
                    tvGridView.setTag("id");
                } else if (rolesList.get(i).equals("IE")) {//工會空間點檢
                    tvGridView.setTag("ie");
                } else if (rolesList.get(i).equals("IF")) {//關愛中心其他工作室
                    tvGridView.setTag("if");
                } else if (rolesList.get(i).equals("IH")) {//110接處警
                    tvGridView.setTag("ih");
                } else if (rolesList.get(i).equals("IG")) {//宿舍 寄存
                    tvGridView.setTag("ig");
                } else if (rolesList.get(i).equals("II")) {//成品倉出貨
                    tvGridView.setTag("ii");
                } else if (rolesList.get(i).equals("LMNOP")) {//總務 商鋪物業
                    tvGridView.setTag("lmnop");
                } else if (rolesList.get(i).equals("IQ")) {//sharp 巡檢
                    tvGridView.setTag("iq");
                } else if (rolesList.get(i).equals("IR")) {//總務廣播巡檢
                    tvGridView.setTag("ir");
                } else if (rolesList.get(i).equals("IS")) {//人資活動發佈
                    tvGridView.setTag("is");
                } else if (rolesList.get(i).equals("IJ")) {//防火
                    tvGridView.setTag("ij");
                } else if (rolesList.get(i).equals("IT")) {//安保固定
                    tvGridView.setTag("it");
                } else if (rolesList.get(i).equals("IU")) {//安保巡邏
                    tvGridView.setTag("iu");
                } else if (rolesList.get(i).equals("IW")) {//運動器材
                    tvGridView.setTag("iw");
                } else {
                    tvGridView.setTag("true");
                }
                break;
            } else {
//                tvGridView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_eeeeee));
                tvGridView.setEnabled(false);
                tvGridView.setClickable(false);
                tvGridView.setTag("false");
            }
        }
        tvGridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag().equals("true")) {
                    onClickListener.OnClickListenerTrue(position);
                } else if (view.getTag().equals("F0")) {//碼頭網站跳轉
                    onClickListener.OnClickWebView(position, "F0");
                } else if (view.getTag().equals("BT")) {//跨區網站跳轉
                    onClickListener.OnClickWebView(position, "BT");
                } else if (view.getTag().equals("e")) {//條碼掃描跳轉
                    onClickListener.OnClickCarScan(position);
                } else if (view.getTag().equals("g")) {//行車日誌列表跳轉
                    onClickListener.OnClickCarLogList(position);
                } else if (view.getTag().equals("v")) {//營建維保
                    onClickListener.OnClickOutKeep(position);
                } else if (view.getTag().equals("bg")) {//宿舍巡檢
                    onClickListener.OnClickDormitory(position);
                } else if (view.getTag().equals("bk")) {//人工跨區無紙化
                    onClickListener.OnClickCrossScan(position);
                } else if (view.getTag().equals("x")) {//活動列表
                    onClickListener.OnClickEventList(position);
                } else if (view.getTag().equals("y")) {//活動簽到
                    onClickListener.OnClickEventCheckIn(position);
                } else if (view.getTag().equals("cd")) {//安全部 常用表單
                    onClickListener.OnClickComForms(position);
                } else if (view.getTag().equals("ce")) {//安全部 物品放行
                    onClickListener.OnClickGoods();
                } else if (view.getTag().equals("gc")) {//安全部 健康追蹤
                    onClickListener.OnClickGC(position);
                } else if (view.getTag().equals("ci")) {//HUB倉
                    onClickListener.OnClickHub();
                } else if (view.getTag().equals("ck")) {//叉車巡檢
                    onClickListener.OnClickForkLift();
                } else if (view.getTag().equals("cl")) {//移動上網設備
                    onClickListener.OnClickMobile();
                } else if (view.getTag().equals("vip")) {//VIP
                    onClickListener.OnClickVIP();
                } else if (view.getTag().equals("pme")) {//PME
                    onClickListener.OnClickPME();
                } else if (view.getTag().equals("nme")) {//NME
                    onClickListener.OnClickNME();
                } else if (view.getTag().equals("ebl")) {//EBL
                    onClickListener.OnClickEBL();
                } else if (view.getTag().equals("pwb")) {//PWB
                    onClickListener.OnClickPWB();
                } else if (view.getTag().equals("hec")) {//HEC
                    onClickListener.OnClickHEC();
                } else if (view.getTag().equals("mdi")) {//MDI
                    onClickListener.OnClickMDI();
                } else if (view.getTag().equals("muj")) {//模具
                    onClickListener.OnClickMUJ();
                } else if (view.getTag().equals("han")) {//環境安全
                    onClickListener.OnClickHAN();
                } else if (view.getTag().equals("cq")) {//車輛跨區無紙化
                    onClickListener.OnClickCrossCar(position);
                } else if (view.getTag().equals("cr")) {//廢料出廠
                    onClickListener.OnClickScarpLeave(position);
                } else if (view.getTag().equals("ct")) {//工安巡檢
                    onClickListener.OnClickWorkSafety(position);
                } else if (view.getTag().equals("cu")) {//值班科長
                    onClickListener.OnClickDuty(position);
                } else if (view.getTag().equals("cx")) {//三防隱患
                    onClickListener.OnClickSanFang(position);
                } else if (view.getTag().equals("cy")) {//消殺點檢
                    onClickListener.OnKill();
                } else if (view.getTag().equals("zxs")) {//總務消殺巡檢
                    onClickListener.OnDAKill(position);
                } else if (view.getTag().equals("gan")) {//工安巡檢
                    onClickListener.OnClickWorkSafety(position);
                } else if (view.getTag().equals("dn")) {//宿舍查验
                    onClickListener.OnDN();
                } else if (view.getTag().equals("do")) {//退訓放行
                    onClickListener.OnClickRetreat();
                } else if (view.getTag().equals("dp")) {//班導
                    onClickListener.OnClickBandao();
                } else if (view.getTag().equals("dq")) {//直飲水
                    onClickListener.OnClickWater();
                } else if (view.getTag().equals("bs")) {//候車亭
                    onClickListener.OnClickBS();
                } else if (view.getTag().equals("dz")) {//人資監餐
                    onClickListener.OnDZFood();
                } else if (view.getTag().equals("ko")) {//一大隊巡邏
                    onClickListener.OnClickKO();
                } else if (view.getTag().equals("lo")) {//二大隊巡邏
                    onClickListener.OnClickLO();
                } else if (view.getTag().equals("mo")) {//三大隊巡邏
                    onClickListener.OnClickMO();
                } else if (view.getTag().equals("ro")) {//機動隊巡邏
                    onClickListener.OnClickRO();
                } else if (view.getTag().equals("h0")) {//一大隊固定
                    onClickListener.OnClickH0();
                } else if (view.getTag().equals("i0")) {//二大隊固定
                    onClickListener.OnClickI0();
                } else if (view.getTag().equals("j0")) {//三大隊固定
                    onClickListener.OnClickJ0();
                } else if (view.getTag().equals("s0")) {//機動隊固定
                    onClickListener.OnClickS0();
                } else if (view.getTag().equals("a0")) {//安保值班室
                    onClickListener.OnClickA0();
                } else if (view.getTag().equals("ed")) {//人資教室點檢
                    onClickListener.OnClickED();
                } else if (view.getTag().equals("ec")) {//碼頭點檢
                    onClickListener.OnClickEC();
                } else if (view.getTag().equals("hr")) {//會議室
                    onClickListener.OnClickHR();
                } else if (view.getTag().equals("hq")) {//值班室
                    onClickListener.OnClickHQ();
                } else if (view.getTag().equals("hs")) {//資料室
                    onClickListener.OnClickHS();
                } else if (view.getTag().equals("fe")) {//洗眼器點檢
                    onClickListener.OnClickFE();
                } else if (view.getTag().equals("ff")) {//危化品暫存柜點檢
                    onClickListener.OnClickFF();
                } else if (view.getTag().equals("fg")) {//危化品暫存倉點檢
                    onClickListener.OnClickFG();
                } else if (view.getTag().equals("qaq")) {//品質保證處
                    onClickListener.OnClickQAQ();
                } else if (view.getTag().equals("smt")) {//品質保證處
                    onClickListener.OnClickSMT();
                } else if (view.getTag().equals("mem")) {//品質保證處
                    onClickListener.OnClickMEM();
                } else if (view.getTag().equals("ea")) {//車輛巡檢
                    onClickListener.OnClickEA();
                } else if (view.getTag().equals("fq")) {//車調車輛巡檢
                    onClickListener.OnClickFQ();
                } else if (view.getTag().equals("fs")) {//營建空調巡檢
                    onClickListener.OnClickFS();
                } else if (view.getTag().equals("fw")) {//營建配電箱巡檢
                    onClickListener.OnClickFW();
                } else if (view.getTag().equals("gd")) {//營建路燈射燈巡檢
                    onClickListener.OnClickGD();
                } else if (view.getTag().equals("fh")) {//總務餐廳巡檢
                    onClickListener.OnClickFH();
                } else if (view.getTag().equals("fv")) {//工業安全-吸煙區
                    onClickListener.OnClickFV();
                } else if (view.getTag().equals("fx")) {//工業安全-鋰電池防火
                    onClickListener.OnClickFX();
                } else if (view.getTag().equals("fz")) {//工業安全-有限空間
                    onClickListener.OnClickFZ();
                } else if (view.getTag().equals("ge")) {//工業安全-室外堆場
                    onClickListener.OnClickGE();
                } else if (view.getTag().equals("gb")) {//越南巡更
                    onClickListener.OnClickGB();
                } else if (view.getTag().equals("ga")) {//總務臨時工
                    onClickListener.OnClickGA();
                } else if (view.getTag().equals("iv")) {//總務臨時工请假 主管签核
                    onClickListener.OnClickIV();
                } else if (view.getTag().equals("ft")) {//華北商務-流動攤位
                    onClickListener.OnClickFT();
                } else if (view.getTag().equals("fu")) {//華北商務-門市房
                    onClickListener.OnClickFU();
                } else if (view.getTag().equals("bv")) {//叉車球車點檢
                    onClickListener.OnClickBV();
                } else if (view.getTag().equals("gq")) {//南寧直飲水點檢
                    onClickListener.OnClickGQ();
                } else if (view.getTag().equals("gr")) {//南寧機電巡檢
                    onClickListener.OnClickGR();
                } else if (view.getTag().equals("gs")) {//南寧警衛巡邏
                    onClickListener.OnClickGS();
                } else if (view.getTag().equals("gx")) {//南寧警衛巡邏
                    onClickListener.OnClickGX();
                } else if (view.getTag().equals("gcgl")) {//營建 工程管理
                    onClickListener.OnClickGCGL();
                } else if (view.getTag().equals("hl")) {//總務倉庫
                    onClickListener.OnClickHL();
                } else if (view.getTag().equals("hx")) {//總務餐廳及多媒體
                    onClickListener.OnClickHX();
                } else if (view.getTag().equals("ht")) {//人資會議室
                    onClickListener.OnClickHT();
                } else if (view.getTag().equals("hu")) {//人資資料室
                    onClickListener.OnClickHU();
                } else if (view.getTag().equals("hv")) {//人資倉庫
                    onClickListener.OnClickHV();
                } else if (view.getTag().equals("hw")) {//人資文康場館
                    onClickListener.OnClickHW();
                } else if (view.getTag().equals("hz")) {//華北商貿倉庫
                    onClickListener.OnClickHZ();
                } else if (view.getTag().equals("ia")) {//關愛中心倉庫
                    onClickListener.OnClickIA();
                } else if (view.getTag().equals("n0")) {//A
                    onClickListener.OnClickN0();
                } else if (view.getTag().equals("p0")) {//C
                    onClickListener.OnClickP0();
                } else if (view.getTag().equals("o0")) {//E
                    onClickListener.OnClickO0();
                } else if (view.getTag().equals("q0")) {//D
                    onClickListener.OnClickQ0();
                } else if (view.getTag().equals("ib")) {//G
                    onClickListener.OnClickIB();
                } else if (view.getTag().equals("ic")) {//A-5
                    onClickListener.OnClickIC();
                } else if (view.getTag().equals("id")) {//工業安全會議室
                    onClickListener.OnClickID();
                } else if (view.getTag().equals("ie")) {//工會空間點檢
                    onClickListener.OnClickIE();
                } else if (view.getTag().equals("if")) {//關愛中心其他工作室
                    onClickListener.OnClickIF();
                } else if (view.getTag().equals("ih")) {//110接處警
                    onClickListener.OnClickIH();
                } else if (view.getTag().equals("ig")) {//宿舍寄存
                    onClickListener.OnClickIG();
                } else if (view.getTag().equals("lmnop")) {//總務 商鋪物業
                    onClickListener.OnClickLMNOP();
                } else if (view.getTag().equals("ii")) {//成品倉出貨
                    onClickListener.OnClickII();
                } else  if (view.getTag().equals("iq")) {//sharp 巡檢
                    onClickListener.OnClickIQ();
                } else if (view.getTag().equals("ir")) {//總務廣播巡檢
                    onClickListener.OnClickIR();
                } else if (view.getTag().equals("is")) {//人資活動發佈
                    onClickListener.OnClickIS();
                } else if (view.getTag().equals("ij")) {//防火
                    onClickListener.OnClickIJ();
                } else if (view.getTag().equals("it")) {//安保固定
                    onClickListener.OnClickIT();
                } else if (view.getTag().equals("iu")) {//安保巡邏
                    onClickListener.OnClickIU();
                } else if (view.getTag().equals("iw")) {//運動器材
                    onClickListener.OnClickIW();
                } else {
                    onClickListener.OnClickListenerFalse(position);
                }
            }
        });
        return convertView;
    }

    public interface OnClickListener {
        void OnClickListenerFalse(int position);
        void OnClickListenerTrue(int position);
        void OnClickWebView(int position, String role);
        void OnClickCarScan(int position);
        void OnClickCarLogList(int position);
        void OnClickOutKeep(int position);
        void OnClickCrossScan(int position);//人工跨區無紙化
        void OnClickDormitory(int position);//宿舍巡檢
        void OnClickEventList(int position);//活動列表
        void OnClickEventCheckIn(int position);//活動簽到
        void OnClickComForms(int position);//常用表單
        void OnClickGC(int position);//安保部 健康追蹤
        void OnClickGoods();//物品放行
        void OnClickHub();//HUB倉
        void OnClickForkLift();//叉車巡檢
        void OnClickMobile();//移動上網設備
        void OnClickVIP();//VIP
        void OnClickPME();//PME
        void OnClickNME();//NME
        void OnClickEBL();//EBL
        void OnClickPWB();//PWB
        void OnClickHEC();//HEC
        void OnClickMDI();//MDI
        void OnClickMUJ();//模具
        void OnClickHAN();//環安
        void OnClickCrossCar(int position);//車輛跨區無紙化
        void OnClickScarpLeave(int position);//廢料出廠
        void OnClickWorkSafety(int position);//工安巡檢
        void OnClickDuty(int position);//值班科長
        void OnClickSanFang(int position);//三防隱患
        void OnKill();        //消殺點檢
        void OnDAKill(int position);//總務消殺巡檢
        void OnDN();//宿舍查验
        void OnClickRetreat();//退訓放行
        void OnClickBandao();//班導
        void OnClickWater();//直飲水
        void OnClickBS();//候車亭
        void OnDZFood();//人資監餐
        void OnClickKO();//一大隊巡邏
        void OnClickLO();//二大隊巡邏
        void OnClickMO();//三大隊巡邏
        void OnClickRO();//機動隊巡邏
        void OnClickH0();//一大隊固定
        void OnClickI0();//二大隊固定
        void OnClickJ0();//三大隊固定
        void OnClickS0();//機動隊固定
        void OnClickA0();//安保值班室
        void OnClickED();//人資教室點檢
        void OnClickEC();//碼頭點檢
        void OnClickFE();//洗眼器點檢 危化品暫存柜點檢 危化品暫存倉點檢
        void OnClickFF();//危化品暫存柜點檢
        void OnClickFG();//危化品暫存倉點檢
        void OnClickQAQ();//品質保證處
        void OnClickSMT();//品質保證處
        void OnClickMEM();//品質保證處
        void OnClickEA();//車輛巡檢
        void OnClickFQ();//車調車輛巡檢
        void OnClickFS();//營建空調巡檢
        void OnClickFW();//營建配電箱巡檢
        void OnClickGD();//營建路燈射燈巡檢
        void OnClickFH();//總務餐廳巡檢
        void OnClickFV();//工業安全-吸煙區
        void OnClickFT();//華北商務-流動攤位
        void OnClickFU();//華北商務-門市房
        void OnClickBV();//叉車球車點檢
        void OnClickFX();//工業安全-鋰電池防火
        void OnClickFZ();//工業安全-有限空間
        void OnClickGE();//工業安全-室外堆場
        void OnClickGA();//總務臨時工
        void OnClickIV();//總務臨時工请假 主管签核
        void OnClickGB();//越南巡更
        void OnClickGQ();//南寧直飲水
        void OnClickGR();//南寧機電
        void OnClickGS();//南寧警衛巡邏
        void OnClickGX();//南寧警衛巡邏
        void OnClickGCGL();//營建 工程管理
        void OnClickHR();//運籌會議室
        void OnClickHQ();//運籌值班室
        void OnClickHS();//運籌資料室
        void OnClickHL();//總務倉庫
        void OnClickHX();//總務餐廳區域及多媒體
        void OnClickHT();//人資會議室
        void OnClickHU();//人資資料室
        void OnClickHV();//人資倉庫
        void OnClickHW();//人資文康場館
        void OnClickHZ();//華北商貿倉庫
        void OnClickIA();//關愛中心倉庫
        void OnClickLMNOP();//總務 商鋪物業

        void OnClickN0();//A
        void OnClickP0();//C
        void OnClickO0();//E
        void OnClickQ0();//D
        void OnClickIB();//G
        void OnClickIC();//A-5
        void OnClickID();//工業安全會議室
        void OnClickIE();//工會空間點檢
        void OnClickIF();//關愛中心其他工作室
        void OnClickIH();//110接處警
        void OnClickIG();//宿舍寄存
        void OnClickII();//成品倉出貨
        void OnClickIQ();//sharp 巡檢
        void OnClickIR();//總務廣播巡檢
        void OnClickIS();//人資活動發佈
        void OnClickIJ();//防火
        void OnClickIT();//安保固定
        void OnClickIU();//安保巡邏
        void OnClickIW();//運動器材
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}