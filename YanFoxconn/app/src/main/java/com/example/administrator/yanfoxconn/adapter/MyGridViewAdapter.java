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
                } else if (rolesList.get(i).equals("BP")) {//餐飲
                    tvGridView.setTag("bp");
                } else if (rolesList.get(i).equals("BK")) {//人工跨區無紙化
                    tvGridView.setTag("bk");
                } else if (rolesList.get(i).equals("X0")) {//活動列表
                    tvGridView.setTag("x");
                } else if (rolesList.get(i).equals("Y0")) {//活動簽到
                    tvGridView.setTag("y");
                } else if (rolesList.get(i).equals("CD")) {//安全部 常用表單
                    tvGridView.setTag("cd");
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
                } else if (rolesList.get(i).equals("BS")) {//直飲水
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
                } else if (rolesList.get(i).equals("ED")) {//人資教室點檢
                    tvGridView.setTag("ed");
                } else if (rolesList.get(i).equals("EC")) {//碼頭點檢
                    tvGridView.setTag("ec");
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
                } else if (rolesList.get(i).equals("GB")) {//越南巡更
                    tvGridView.setTag("gb");
                } else if (rolesList.get(i).equals("GA")) {//總務臨時工
                    tvGridView.setTag("ga");
                } else if (rolesList.get(i).equals("FT")) {//華北商務-流動攤位
                    tvGridView.setTag("ft");
                } else if (rolesList.get(i).equals("FU")) {//華北商務-門市房
                    tvGridView.setTag("fu");
                } else if (rolesList.get(i).equals("BV")) {//叉車球車巡檢
                    tvGridView.setTag("bv");
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
                } else if (view.getTag().equals("bp")) {//餐飲巡檢
                    onClickListener.OnClickFood(position);
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
                } else if (view.getTag().equals("ed")) {//人資教室點檢
                    onClickListener.OnClickED();
                } else if (view.getTag().equals("ec")) {//碼頭點檢
                    onClickListener.OnClickEC();
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
                } else if (view.getTag().equals("gb")) {//越南巡更
                    onClickListener.OnClickGB();
                } else if (view.getTag().equals("ga")) {//總務臨時工
                    onClickListener.OnClickGA();
                } else if (view.getTag().equals("ft")) {//華北商務-流動攤位
                    onClickListener.OnClickFT();
                } else if (view.getTag().equals("fu")) {//華北商務-門市房
                    onClickListener.OnClickFU();
                } else if (view.getTag().equals("bv")) {//叉車球車點檢
                    onClickListener.OnClickBV();
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
        void OnClickFood(int position);//總務餐飲
        void OnClickEventList(int position);//活動列表
        void OnClickEventCheckIn(int position);//活動簽到
        void OnClickComForms(int position);//常用表單
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
        void OnClickH0();//一大隊巡邏
        void OnClickI0();//二大隊巡邏
        void OnClickJ0();//三大隊巡邏
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
        void OnClickGA();//總務臨時工
        void OnClickGB();//越南巡更
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}