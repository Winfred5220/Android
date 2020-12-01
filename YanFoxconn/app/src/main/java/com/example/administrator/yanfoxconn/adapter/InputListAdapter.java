package com.example.administrator.yanfoxconn.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.HubInputActivity;
import com.example.administrator.yanfoxconn.bean.HubList;

import java.util.List;

/**
 * Created by wangqian on 2019/2/27.
 */

public class InputListAdapter extends BaseAdapter {

    private HubInputActivity activity;
    private List<HubList> lists;

    public InputListAdapter(HubInputActivity activity, List<HubList> lists) {

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
        final InputListAdapter.ViewHolder holder;
        int num = position +1;
        if (convertView == null) {
            holder = new InputListAdapter.ViewHolder();
            Log.e("-------------", "lists.get(position).getOUTD01()===="+lists.get(position).getAPPLYER());
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_input_list, null);

            holder.tvNo = convertView.findViewById(R.id.tv_no);
            holder.tvApply = convertView.findViewById(R.id.tv_apply);
            holder.tvEmp = convertView.findViewById(R.id.tv_emp);
            holder.tvPhone = convertView.findViewById(R.id.tv_phone);

            convertView.setTag(holder);
        } else {
            holder = (InputListAdapter.ViewHolder) convertView.getTag();
        }

        holder.tvNo.setText(num + "");
        holder.tvApply.setText(lists.get(position).getAPPLYER());
        holder.tvEmp.setText(lists.get(position).getAPPLYER_ADD());
        holder.tvPhone.setText(lists.get(position).getAPPLYER_TEL());

        return convertView;
    }

    private class ViewHolder {
        public TextView tvNo, tvApply, tvPhone, tvEmp;//編號,領取人,電話,部門

    }
}
