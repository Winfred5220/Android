package com.example.administrator.yanfoxconn.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.ForkliftMaintenanceListActivity;
import com.example.administrator.yanfoxconn.bean.ForkliftMessage;

import java.util.List;

/**
 * HUB倉簽收物品列表 的 適配器
 * Created by wangqian on 2019/4/1.
 */

public class ForkliftMaintenanceAdapter extends BaseAdapter {

    private ForkliftMaintenanceListActivity activity;
    private List<ForkliftMessage> lists;

    public ForkliftMaintenanceAdapter(ForkliftMaintenanceListActivity activity, List<ForkliftMessage> lists) {
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
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        int num = position +1;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_forklift_maintenance_list, null,false);
            holder.tvNo = convertView.findViewById(R.id.tv_num);
            holder.tvBianhao = convertView.findViewById(R.id.tv_bianhao);
            holder.tvChepai = convertView.findViewById(R.id.tv_chepai);
            holder.tvBxdate = convertView.findViewById(R.id.tv_bx_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            holder.tvNo.setText(num + "");
            holder.tvBianhao.setText(lists.get(position).getBianhao());
            holder.tvChepai.setText(lists.get(position).getChepai());
            holder.tvBxdate.setText(lists.get(position).getBx_date().substring(0,16));

            return convertView;
    }

    private class ViewHolder {
        public TextView tvNo,tvBianhao,tvChepai,tvBxdate;//編號,車架號,車牌號,報修時間
    }

}
