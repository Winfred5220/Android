package com.example.administrator.yanfoxconn.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.GoodsIntelligenceActivity;
import com.example.administrator.yanfoxconn.bean.GoodsMessage;

import java.util.List;


/**
 * 智慧物品列表 的 適配器
 * Created by wangqian on 2019/6/28.
 */

public class IntelligenceGoodsListAdapter extends BaseAdapter {

    private GoodsIntelligenceActivity activity;
    private List<GoodsMessage> lists;


    public IntelligenceGoodsListAdapter(GoodsIntelligenceActivity activity, List<GoodsMessage> lists) {

        this.activity = activity;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        Log.e("--------","lists.size()==="+lists.size());
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        int num = position +1;
        if (convertView == null) {
            holder = new ViewHolder();
            Log.e("-------------", "lists.get(position).getEQNAME()===="+lists.get(position).getEQNAME());
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_intelligence_goods_list, null);

            holder.tvNo = convertView.findViewById(R.id.tv_list_no);
            holder.tvName = convertView.findViewById(R.id.tv_list_name);
            holder.tvMac = convertView.findViewById(R.id.tv_list_mac);
            holder.tvSn = convertView.findViewById(R.id.tv_list_sn);
            holder.tvXian = convertView.findViewById(R.id.tv_list_xian);
            holder.tvPin = convertView.findViewById(R.id.tv_list_pin);
            holder.tvCol = convertView.findViewById(R.id.tv_list_col);
            holder.tvNum = convertView.findViewById(R.id.tv_list_num);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvNo.setText(num + "");
        holder.tvName.setText(lists.get(position).getEQNAME());
        holder.tvMac.setText(lists.get(position).getEQTYPE());
        holder.tvSn.setText(lists.get(position).getMAKENUMBER());
        if(lists.get(position).getWIRELESS().equals("No")){
            holder.tvXian.setText("無");
        }else{ holder.tvXian.setText(lists.get(position).getWIRELESS());}
        holder.tvPin.setText(lists.get(position).getBRAND());
        holder.tvCol.setText(lists.get(position).getEQCOLOR());
        holder.tvNum.setText(lists.get(position).getEQCOUNT() +"/" +lists.get(position).getREMARKS());

        return convertView;
    }

    private class ViewHolder {
        public TextView tvNo, tvName, tvMac, tvSn,tvXian, tvPin, tvCol, tvNum;//編號,名稱,MAC,SN,顯卡,品牌,顏色,數量单位

    }


}
