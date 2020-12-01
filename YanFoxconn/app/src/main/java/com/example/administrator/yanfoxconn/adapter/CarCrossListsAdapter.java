package com.example.administrator.yanfoxconn.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.CrossCarActivity;
import com.example.administrator.yanfoxconn.activity.CrossScanActivity;
import com.example.administrator.yanfoxconn.bean.CrossScanList;

import java.util.List;


/**
 * 車輛跨區的 適配器
 * Created by song on 2017/8/30.
 */

public class CarCrossListsAdapter extends BaseAdapter {

    private CrossCarActivity activity;
    private List<CrossScanList> lists;

    public CarCrossListsAdapter(CrossCarActivity activity, List<CrossScanList> lists) {

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
        int num=position+1;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_car_cross_list, null);

            holder.tvNo = convertView.findViewById(R.id.tv_list_no);
            holder.tvFeng = convertView.findViewById(R.id.tv_list_feng);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvNo.setText(num+"、");
        holder.tvFeng.setText(lists.get(position).getQIANFENG());

        return convertView;
    }

    private class ViewHolder {
        public TextView tvNo, tvFeng;//編號,封號

    }


}
