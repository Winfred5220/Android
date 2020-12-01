package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.DangerListMessage;

import java.util.List;


/**
 * 工安巡檢隱患查看 適配器
 * Created by wang on 2019/8/22.
 */

public class IndustrialDangerListAdapter extends BaseAdapter {

    private Context mContext;
    private List<DangerListMessage> lists;


    public IndustrialDangerListAdapter(Context context, List<DangerListMessage> lists) {

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
            Log.e("-------------", "lists.get(position).getCaseId()===="+lists.get(position).getCaseId());
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_industrial_danger_list, null);

            holder.tvTime = convertView.findViewById(R.id.tv_time);
            holder.tvType = convertView.findViewById(R.id.tv_type);
            holder.tvAddress = convertView.findViewById(R.id.tv_address);
            holder.tvDescribe = convertView.findViewById(R.id.tv_describe);
            holder.tvSupervisor = convertView.findViewById(R.id.tv_supervisor);
            holder.tvOwner = convertView.findViewById(R.id.tv_owner);
            holder.tvPerson = convertView.findViewById(R.id.tv_person);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvTime.setText(lists.get(position).getCheckDate());
        holder.tvType.setText(lists.get(position).getType());
        holder.tvAddress.setText(lists.get(position).getAddress()+"-"+lists.get(position).getProduct());
        holder.tvDescribe.setText(lists.get(position).getDescribe());
        holder.tvSupervisor.setText(lists.get(position).getSupervisor());
        holder.tvOwner.setText(lists.get(position).getOwner());
        holder.tvPerson.setText(lists.get(position).getPerson());
        return convertView;
    }

    private class ViewHolder {
        public TextView tvTime, tvAddress, tvDescribe, tvSupervisor, tvOwner, tvPerson,tvType;//時間,位置,隱患描述,責任主管,棟主,陪查人,隱患類型

    }


}
