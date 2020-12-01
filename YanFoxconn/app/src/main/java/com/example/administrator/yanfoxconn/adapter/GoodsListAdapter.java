package com.example.administrator.yanfoxconn.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.GoodsGeneralActivity;
import com.example.administrator.yanfoxconn.bean.GoodsMessage;

import java.util.List;


/**
 * 普通物品列表 的 適配器
 * Created by wangqian on 2018/12/22.
 */

public class GoodsListAdapter extends BaseAdapter {

    private GoodsGeneralActivity activity;
    private List<GoodsMessage> lists;


    public GoodsListAdapter(GoodsGeneralActivity activity, List<GoodsMessage> lists) {

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
            Log.e("-------------", "lists.get(position).getOUTD01()===="+lists.get(position).getOUTD01());
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_goods_list, null);

            holder.tvNo = convertView.findViewById(R.id.tv_list_no);
            holder.tvName = convertView.findViewById(R.id.tv_list_name);
            holder.tvSpec = convertView.findViewById(R.id.tv_list_spec);
            holder.tvNum = convertView.findViewById(R.id.tv_list_num);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvNo.setText(num + "");
        holder.tvName.setText(lists.get(position).getOUTD01());
        holder.tvSpec.setText(lists.get(position).getOUTD02());
        holder.tvNum.setText(lists.get(position).getOUTD03() +"/" +lists.get(position).getOUTD04());

        return convertView;
    }

    private class ViewHolder {
        public TextView tvNo, tvName, tvSpec, tvNum;//編號,名稱,類別,數量单位

    }


}
