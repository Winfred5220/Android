package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.CrossScanActivity;
import com.example.administrator.yanfoxconn.bean.CrossScanList;

import java.util.List;


/**
 * 人工跨區的 適配器
 * Created by song on 2017/8/30.
 */

public class CrossListsAdapter extends BaseAdapter {

    private Context mContext;
    private List<CrossScanList> lists;


    public CrossListsAdapter(Context context, List<CrossScanList> lists) {

        this.mContext = context;
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
        if (convertView == null) {
            holder = new ViewHolder();
            Log.e("-------------", "lists.get(position).getBCR04()===="+lists.get(position).getBCR04());
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_cross_list, null);

            holder.tvNo = convertView.findViewById(R.id.tv_list_no);
            holder.tvName = convertView.findViewById(R.id.tv_list_name);
            holder.tvType = convertView.findViewById(R.id.tv_list_type);
            holder.tvNum = convertView.findViewById(R.id.tv_list_num);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvNo.setText(lists.get(position).getBCR02());
        holder.tvName.setText(lists.get(position).getBCR04());
        holder.tvType.setText(lists.get(position).getBCR11());
        holder.tvNum.setText(lists.get(position).getBCR10());

        return convertView;
    }

    private class ViewHolder {
        public TextView tvNo, tvName, tvType, tvNum;//編號,名稱,類別,數量

    }


}
