package com.example.administrator.yanfoxconn.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.AQ110MenuActivity;
import com.example.administrator.yanfoxconn.activity.BandaoInputActivity;
import com.example.administrator.yanfoxconn.activity.CarListActivity;
import com.example.administrator.yanfoxconn.activity.CommonformsActivity;
import com.example.administrator.yanfoxconn.activity.CrossScanMainActivity;
import com.example.administrator.yanfoxconn.activity.CyCarListActivity;
import com.example.administrator.yanfoxconn.activity.DZFoodCheckActivity;
import com.example.administrator.yanfoxconn.activity.DutySectionChiefActivity;
import com.example.administrator.yanfoxconn.activity.EventCheckInActivity;
import com.example.administrator.yanfoxconn.activity.ForkliftEntranceActivity;
import com.example.administrator.yanfoxconn.activity.GAWorkerSingInActivity;
import com.example.administrator.yanfoxconn.activity.GCMainActivity;
import com.example.administrator.yanfoxconn.activity.GoodsReleaseInputActivity;
import com.example.administrator.yanfoxconn.activity.HubAreaActivity;
import com.example.administrator.yanfoxconn.activity.MobilecontrolInputActivity;
import com.example.administrator.yanfoxconn.activity.OutKeepFirstActivity;
import com.example.administrator.yanfoxconn.activity.ProductDivisionActivity;
import com.example.administrator.yanfoxconn.activity.QrCodeActivity;
import com.example.administrator.yanfoxconn.activity.SanFangActivity;
import com.example.administrator.yanfoxconn.activity.ScrapInputActivity;
import com.example.administrator.yanfoxconn.activity.WebViewActivity;
import com.example.administrator.yanfoxconn.activity.ZhiyinshuiMenuActivity;
import com.example.administrator.yanfoxconn.activity.fragment.XunjianFragment;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.ToastUtils;

import java.util.List;

/**
 * ExpandableListView 适配器
 * Created by song on 2017/11/17.
 */

public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {
    private XunjianFragment mContext;

    /**
     * 每个分组的名字的集合
     */
    private List<String> groupList;

    /**
     * 所有分组的所有子项的 GridView 数据集合
     */
    private List<List<String>> itemList;

    /**
     * 所有分组的所有子项的 GridView 数据集合,
     */
    private List<List<String>> itemTypeList;

    /**
     * 所有分组的所有子项的 GridView 数据集合,
     */
    private List<List<Integer>> itemImgList;

    private List<String> rolesList;
    private GridView gridView;

    public MyExpandableListViewAdapter(XunjianFragment context, List<String> groupList,
                                       List<List<String>> itemList, List<List<String>> itemType,
                                       List<String> rolesList, List<List<Integer>> itemImgList) {
        mContext = context;
        this.groupList = groupList;
        this.itemList = itemList;
        this.itemTypeList = itemType;
        this.rolesList = rolesList;
        this.itemImgList = itemImgList;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itemList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = View.inflate(mContext.getActivity(), R.layout.expandablelist_group, null);
        }
        ImageView ivGroup = (ImageView) convertView.findViewById(R.id.iv_group);
        TextView tvGroup = (TextView) convertView.findViewById(R.id.tv_group);
        // 如果是展开状态，就显示展开的箭头，否则，显示折叠的箭头
        if (isExpanded) {
            ivGroup.setImageResource(R.mipmap.icon_ex_open);
        } else {
            ivGroup.setImageResource(R.mipmap.icon_ex_close);
        }
        // 设置分组组名
        tvGroup.setText(groupList.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = View.inflate(mContext.getActivity(), R.layout.expandablelist_item, null);
        }
        // 因为 convertView 的布局就是一个 GridView，
        // 所以可以向下转型为 GridView
        gridView = (GridView) convertView;
        // 创建 GridView 适配器
        MyGridViewAdapter gridViewAdapter = new MyGridViewAdapter(mContext.getActivity(), itemList.get(groupPosition), itemTypeList.get(groupPosition), rolesList, itemImgList.get(groupPosition));
        Log.e("----e-----", "groupPosition==" + groupPosition);
        gridView.setAdapter(gridViewAdapter);

        gridViewAdapter.setOnClickListener(new MyGridViewAdapter.OnClickListener() {
            @Override
            public void OnClickListenerFalse(int position) {
                Log.e("-------", "沒有權限");
                ToastUtils.showLong(mContext.getActivity(), "您沒有該巡檢權限,請確認!");
            }

            @Override
            public void OnClickListenerTrue(int position) {
                Log.e("-------", "有權限");

//                mContext.getActivity().getRouteList(FoxContext.getInstance().getTypes()[position]);
                mContext.getRouteList(itemTypeList.get(groupPosition).get(position));
            }

            @Override
            public void OnClickWebView(int position, String role) {
                Intent intent = new Intent(mContext.getActivity(), WebViewActivity.class);
                intent.putExtra("role",role);
                mContext.getActivity().startActivity(intent);
            }


            @Override//碼頭放行,碼頭掃描
            public void OnClickCarScan(int position) {
//                mContext.getActivity().getCarList();
                Intent intent = new Intent(mContext.getActivity(), CarListActivity.class);
                mContext.getActivity().startActivity(intent);
            }

            @Override//司機行車日誌
            public void OnClickCarLogList(int position) {
                mContext.getReturnLog();
            }

            @Override//營建維保
            public void OnClickOutKeep(int position) {
                Intent intent = new Intent(mContext.getActivity(), OutKeepFirstActivity.class);
                intent.putExtra("title", "維保巡檢分區");
                intent.putExtra("type", "V0");
                mContext.getActivity().startActivity(intent);
            }

            @Override//人工跨區無紙化作業
            public void OnClickCrossScan(int position) {
                Intent intent = new Intent(mContext.getActivity(), CrossScanMainActivity.class);
                intent.putExtra("flag","cross");
                mContext.getActivity().startActivity(intent);
            }
            @Override//車輛跨區無紙化作業
            public void OnClickCrossCar(int position) {
                Intent intent = new Intent(mContext.getActivity(), CrossScanMainActivity.class);
                intent.putExtra("flag","car");
                mContext.getActivity().startActivity(intent);
            }

            @Override//廢料出廠
            public void OnClickScarpLeave(int position) {
                Intent intent = new Intent(mContext.getActivity(), ScrapInputActivity.class);
                mContext.getActivity().startActivity(intent);
            }

            @Override//工安巡檢
            public void OnClickWorkSafety(int position) {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "工安巡檢");
                intent.putExtra("type", "GAN");
                mContext.getActivity().startActivity(intent);
            }

            @Override//值班科長
            public void OnClickDuty(int position) {
                Intent intent = new Intent(mContext.getActivity(), DutySectionChiefActivity.class);
                mContext.getActivity().startActivity(intent);
            }

            @Override//三防隱患
            public void OnClickSanFang(int position) {
                Intent intent = new Intent(mContext.getActivity(), SanFangActivity.class);
                mContext.getActivity().startActivity(intent);
            }

            @Override//消殺點檢
            public void OnKill() {
//                mContext.getActivity().getCyCarList();
                Intent intent = new Intent(mContext.getActivity(), CyCarListActivity.class);
//                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
//                intent.putExtra("title", "二維碼掃描");
//                intent.putExtra("num", "cy");
                mContext.getActivity().startActivity(intent);
            }
            @Override//總務消殺巡檢
            public void OnDAKill(int position) {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "消殺巡檢");
                intent.putExtra("type", "ZXS");
                mContext.getActivity().startActivity(intent);
            }
            @Override//宿舍查验
            public void OnDN() {
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "二維碼掃描");
                intent.putExtra("num", "dn");
                mContext.getActivity().startActivity(intent);
            }

            @Override//宿舍巡檢
            public void OnClickDormitory(int position) {
                Intent intent = new Intent(mContext.getActivity(), OutKeepFirstActivity.class);
                intent.putExtra("title", "宿舍巡檢分區");
                intent.putExtra("type", "BG");
                mContext.getActivity().startActivity(intent);
            }

            @Override//常用表單
            public void OnClickComForms(int position) {
                Intent intent = new Intent(mContext.getActivity(), CommonformsActivity.class);
                mContext.getActivity().startActivity(intent);
            }

            @Override//安保部 健康追蹤
            public void OnClickGC(int position) {
                Intent intent2 = new Intent(mContext.getActivity(), GCMainActivity.class);
                mContext.getActivity().startActivity(intent2);
            }

            @Override//物品放行
            public void OnClickGoods() {
                Intent intent = new Intent(mContext.getActivity(), GoodsReleaseInputActivity.class);
                mContext.getActivity().startActivity(intent);
            }

            @Override//HUB倉
            public void OnClickHub() {
                Intent intent = new Intent(mContext.getActivity(), HubAreaActivity.class);
                mContext.getActivity().startActivity(intent);
            }
            @Override//叉車巡檢
            public void OnClickForkLift() {
                Intent intent = new Intent(mContext.getActivity(), ForkliftEntranceActivity.class);
                mContext.getActivity().startActivity(intent);
            }

            @Override//移動上網設備
            public void OnClickMobile() {
                Intent intent = new Intent(mContext.getActivity(), MobilecontrolInputActivity.class);
                mContext.getActivity().startActivity(intent);
            }

            @Override//產品處======
            public void OnClickVIP() {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "VIP安全部");
                intent.putExtra("type", "VIP");
                mContext.getActivity().startActivity(intent);
            }

            @Override
            public void OnClickPME() {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "PME安全部");
                intent.putExtra("type", "PME");
                mContext.getActivity().startActivity(intent);
            }

            @Override
            public void OnClickNME() {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "NME安全部");
                intent.putExtra("type", "NME");
                mContext.getActivity().startActivity(intent);
            }

            @Override
            public void OnClickEBL() {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "EBL安全部");
                intent.putExtra("type", "EBL");
                mContext.getActivity().startActivity(intent);
            }
            @Override
            public void OnClickPWB() {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "PWB安全部");
                intent.putExtra("type", "PWB");
                mContext.getActivity().startActivity(intent);
            }
            @Override
            public void OnClickHEC() {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "HEC安全部");
                intent.putExtra("type", "HEC");
                mContext.getActivity().startActivity(intent);
            }
            @Override//模具
            public void OnClickMUJ() {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "模具加工");
                intent.putExtra("type", "MUJ");
                mContext.getActivity().startActivity(intent);
            }
            @Override//環安
            public void OnClickHAN() {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "環境安全");
                intent.putExtra("type", "HAN");
                mContext.getActivity().startActivity(intent);
            }
            @Override//產品處======
            public void OnClickMDI() {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "MDI安全部");
                intent.putExtra("type", "MDI");
                mContext.getActivity().startActivity(intent);
            }

//            @Override//總務餐飲 註釋；；；2021-1-24 BP餐飲巡檢已取消，總務朱富文
//            public void OnClickFood(int position) {
//                mContext.getDormitory("BP");
//            }

            @Override//活動列表
            public void OnClickEventList(int position) {
                mContext.getEventList("X0");

            }

            @Override//活動簽到
            public void OnClickEventCheckIn(int position) {
                Intent intent = new Intent(mContext.getActivity(), EventCheckInActivity.class);
                FoxContext.getInstance().setType("X0");
                mContext.getActivity().startActivity(intent);
            }

            @Override//退訓放行
            public void OnClickRetreat() {

                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "二維碼掃描");
                intent.putExtra("num", "do");
                mContext.getActivity().startActivity(intent);
            }

            @Override//班導
            public void OnClickBandao() {
                Intent intent = new Intent(mContext.getActivity(), BandaoInputActivity.class);
                mContext.getActivity().startActivity(intent);
            }

            @Override//直飲水
            public void OnClickWater() {
                Intent intent = new Intent(mContext.getActivity(), ZhiyinshuiMenuActivity.class);
                intent.putExtra("type","DQ");
                intent.putExtra("title", "總務");
                mContext.getActivity().startActivity(intent);
            }

            @Override//候車亭
            public void OnClickBS() {
                FoxContext.getInstance().setType("BS");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }

            @Override//人資監餐
            public void OnDZFood() {
                Log.e("------------","OnDZFood");
                Intent intent = new Intent(mContext.getActivity(), DZFoodCheckActivity.class);
                intent.putExtra("type","DZ");
                mContext.getActivity().startActivity(intent);
            }

            @Override//一大隊巡邏崗巡檢
            public void OnClickKO() {
                FoxContext.getInstance().setType("K0");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//二大隊巡邏點檢項巡檢
            public void OnClickLO() {
                FoxContext.getInstance().setType("L0");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//三大隊巡邏點檢項巡檢
            public void OnClickMO() {
                FoxContext.getInstance().setType("M0");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//機動隊巡邏點檢項巡檢
            public void OnClickRO() {
                FoxContext.getInstance().setType("R0");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }

            @Override//一大隊固定崗點檢項巡檢
            public void OnClickH0() {
                FoxContext.getInstance().setType("H0");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//二大隊固定點檢項巡檢
            public void OnClickI0() {
                FoxContext.getInstance().setType("I0");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//三大隊固定點檢項巡檢
            public void OnClickJ0() {
                FoxContext.getInstance().setType("J0");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//機動隊固定點檢項巡檢
            public void OnClickS0() {
                FoxContext.getInstance().setType("S0");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//安保值班室點檢項巡檢
            public void OnClickA0() {
                FoxContext.getInstance().setType("A0");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//人資教室點檢
            public void OnClickED() {
                FoxContext.getInstance().setType("ED");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//碼頭點檢
            public void OnClickEC() {
                FoxContext.getInstance().setType("EC");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//會議室
            public void OnClickHR() {
                FoxContext.getInstance().setType("HR");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//值班室
            public void OnClickHQ() {
                FoxContext.getInstance().setType("HQ");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//資料室
            public void OnClickHS() {
                FoxContext.getInstance().setType("HS");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//洗眼器點檢
            public void OnClickFE() {
                FoxContext.getInstance().setType("FE");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//危化品暫存柜點檢
            public void OnClickFF() {
                FoxContext.getInstance().setType("FF");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//危化品暫存倉點檢
            public void OnClickFG() {
                FoxContext.getInstance().setType("FG");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//品質保證處
            public void OnClickQAQ() {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "設備點檢");
                intent.putExtra("type", "QAQ");
                mContext.getActivity().startActivity(intent);
            }
            @Override//品質保證處
            public void OnClickSMT() {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "設備點檢");
                intent.putExtra("type", "SMT");
                mContext.getActivity().startActivity(intent);
            }
            @Override//品質保證處
            public void OnClickMEM() {
                Intent intent = new Intent(mContext.getActivity(), ProductDivisionActivity.class);
                intent.putExtra("title", "設備點檢");
                intent.putExtra("type", "MEM");
                mContext.getActivity().startActivity(intent);
            }
            @Override//車輛巡檢
            public void OnClickEA() {
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "EA");
                mContext.getActivity().startActivity(intent);
            }
            @Override//車調車輛點檢
            public void OnClickFQ() {
                FoxContext.getInstance().setType("FQ");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//營建空調點檢
            public void OnClickFS() {
                Intent intent = new Intent(mContext.getActivity(), ZhiyinshuiMenuActivity.class);
                intent.putExtra("title", "空調點檢");
                intent.putExtra("type", "FS");
                intent.putExtra("name", "營建空調點檢");
                mContext.getActivity().startActivity(intent);
            }
            @Override//營建配電箱點檢
            public void OnClickFW() {
                Intent intent = new Intent(mContext.getActivity(), ZhiyinshuiMenuActivity.class);
                intent.putExtra("title", "配電箱點檢");
                intent.putExtra("type", "FW");
                intent.putExtra("name", "配電箱點檢");
                mContext.getActivity().startActivity(intent);
            }
            @Override//營建路燈射燈點檢
            public void OnClickGD() {
                Intent intent = new Intent(mContext.getActivity(), ZhiyinshuiMenuActivity.class);
                intent.putExtra("title", "路燈射燈點檢");
                intent.putExtra("type", "GD");
                intent.putExtra("name", "路燈射燈點檢");
                mContext.getActivity().startActivity(intent);
            }
            @Override//總務餐廳巡檢
            public void OnClickFH() {
                FoxContext.getInstance().setType("FH");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "fh");
                mContext.getActivity().startActivity(intent);
            }

            @Override
            public void OnClickFT() {//華北商務-流動攤位
                FoxContext.getInstance().setType("FT");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }

            @Override
            public void OnClickFV() {//工業安全-吸煙區
                FoxContext.getInstance().setType("FV");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }

            @Override
            public void OnClickFU() {//華北商務-門市房
                FoxContext.getInstance().setType("FU");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override
            public void OnClickBV() {//叉車球車司機點檢
                FoxContext.getInstance().setType("BV");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }

            @Override
            public void OnClickFX() {//工業安全-鋰電池防火
                FoxContext.getInstance().setType("FX");
                Intent intent2 = new Intent(mContext.getActivity(), GCMainActivity.class);
                intent2.putExtra("type", "FX");
                mContext.getActivity().startActivity(intent2);
            }

            @Override
            public void OnClickFZ() {//工業安全-有限空間
                FoxContext.getInstance().setType("FZ");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }

            @Override
            public void OnClickGE() {//工業安全-室外堆場
                FoxContext.getInstance().setType("GE");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }

            @Override
            public void OnClickGA() {//總務臨時工簽到
                FoxContext.getInstance().setType("GA");
                Intent intent = new Intent(mContext.getActivity(), GAWorkerSingInActivity.class);
                intent.putExtra("type","GA");
                mContext.getActivity().startActivity(intent);
            }

            @Override
            public void OnClickGB() {
                FoxContext.getInstance().setType("GB");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);

            }

            @Override//南寧直飲水點檢
            public void OnClickGQ() {
                Intent intent = new Intent(mContext.getActivity(), ZhiyinshuiMenuActivity.class);
                intent.putExtra("title", "南寧廠區");
                intent.putExtra("type", "GQ");
                intent.putExtra("name", "總務直飲水");
                mContext.getActivity().startActivity(intent);
            }
            @Override
            public void OnClickGR() {//南寧機電點檢
                FoxContext.getInstance().setType("GR");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override
            public void OnClickGS() {//南寧警衛巡邏
                FoxContext.getInstance().setType("GS");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override
            public void OnClickGX() {//南寧警衛巡邏
                FoxContext.getInstance().setType("GX");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "borrow");
                mContext.getActivity().startActivity(intent);
            }

            @Override
            public void OnClickGCGL() {
                FoxContext.getInstance().setType("GCGL");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//總務倉庫
            public void OnClickHL() {
                FoxContext.getInstance().setType("HL");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//總務餐廳區域及多媒體
            public void OnClickHX() {
                FoxContext.getInstance().setType("HX");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//人資會議室
            public void OnClickHT() {
                FoxContext.getInstance().setType("HT");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//人資資料室
            public void OnClickHU() {
                FoxContext.getInstance().setType("HU");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//人資倉庫
            public void OnClickHV() {
                FoxContext.getInstance().setType("HV");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//人資文康場館
            public void OnClickHW() {
                FoxContext.getInstance().setType("HW");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//華北商貿倉庫
            public void OnClickHZ() {
                FoxContext.getInstance().setType("HZ");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//關愛中心倉庫
            public void OnClickIA() {
                FoxContext.getInstance().setType("IA");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }

            @Override//A
            public void OnClickN0() {
                FoxContext.getInstance().setType("N0");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//C
            public void OnClickP0() {
                FoxContext.getInstance().setType("P0");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//E
            public void OnClickO0() {
                FoxContext.getInstance().setType("O0");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//D
            public void OnClickQ0() {
                FoxContext.getInstance().setType("Q0");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//G
            public void OnClickIB() {
                FoxContext.getInstance().setType("IB");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//A-5
            public void OnClickIC() {
                FoxContext.getInstance().setType("IC");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//工業安全會議室
            public void OnClickID() {
                FoxContext.getInstance().setType("ID");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//工會空間點檢
            public void OnClickIE() {
                FoxContext.getInstance().setType("IE");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//關愛中心其他工作室
            public void OnClickIF() {
                FoxContext.getInstance().setType("IF");
                Intent intent = new Intent(mContext.getActivity(), QrCodeActivity.class);
                intent.putExtra("title", "掃描二維碼");
                intent.putExtra("num", "cz");
                mContext.getActivity().startActivity(intent);
            }
            @Override//110接處警
            public void OnClickIH() {
                Intent intent = new Intent(mContext.getActivity(), AQ110MenuActivity.class);
                mContext.getActivity().startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
