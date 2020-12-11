package com.example.administrator.yanfoxconn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.EmpMessage;
import com.example.administrator.yanfoxconn.bean.ExceInfo;

import java.util.List;


/**
 * 普通物品列表 的 適配器
 * Created by wangqian on 2018/12/22.
 */

public class CommonFormsTwoWheelDayListAdapter extends BaseAdapter {
    public Context mContext;
    private List<EmpMessage> lists;


    public CommonFormsTwoWheelDayListAdapter(Context context, List<EmpMessage> lists) {

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


    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        int num = position +1;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_two_wheel_day_list, null);

            holder.tvNum = convertView.findViewById(R.id.tv_num);
            holder.tvCode = convertView.findViewById(R.id.tv_code);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvPosition = convertView.findViewById(R.id.tv_position);
            holder.tvCount = convertView.findViewById(R.id.tv_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvNum.setText(num+"");
        holder.tvCode.setText(lists.get(position).getCODE());
        holder.tvName.setText(lists.get(position).getNAME());
        holder.tvPosition.setText(lists.get(position).getWJ_ADDRESS());
        holder.tvCount.setText(lists.get(position).getYEAR_COUNT());
        return convertView;
    }

    private class ViewHolder {
        public TextView tvNum,tvCode,tvName, tvPosition,tvCount;//工號,姓名,稽核地點，違規次數

    }


}
