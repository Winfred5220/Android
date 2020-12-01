package com.example.administrator.yanfoxconn.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.HubList;
import java.util.List;

/**
 * HUB倉簽收物品列表 的 適配器
 * Created by wangqian on 2019/4/1.
 */

public class HubSignListAdapter extends BaseAdapter {

    private List<HubList> lists;

    public HubSignListAdapter(List<HubList> lists) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_hub_sign_list, null,false);
            holder.tvNo = convertView.findViewById(R.id.tv_list_no);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvModel = convertView.findViewById(R.id.tv_model);
            holder.tvNum = convertView.findViewById(R.id.tv_num);
            holder.tvUnit = convertView.findViewById(R.id.tv_unit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            holder.tvNo.setText(num + "");
            holder.tvName.setText(lists.get(position).getMATERIAL_NAME());
            holder.tvModel.setText(lists.get(position).getMATERIAL_SPEC());
            holder.tvNum.setText(lists.get(position).getAPPLY_COUNT());
            holder.tvUnit.setText(lists.get(position).getUNIT());

            return convertView;
    }

    private class ViewHolder {
        public TextView tvNo,tvName, tvModel, tvNum,tvUnit ;//編號,名稱,型號,數量,單位
    }

}
