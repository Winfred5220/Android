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
 * HUB倉簽收查詢物品列表 的 適配器
 * Created by wangqian on 2019/5/27.
 */

public class HubReceiveListAdapter extends BaseAdapter {

    private List<HubList> lists;

    public HubReceiveListAdapter(List<HubList> lists) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_hub_receive_list, null,false);
            holder.tvListNo = convertView.findViewById(R.id.tv_list_no);
            holder.tvNo = convertView.findViewById(R.id.tv_no);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvNum = convertView.findViewById(R.id.tv_num);
            holder.tvDate = convertView.findViewById(R.id.tv_date);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            holder.tvListNo.setText(num + "");
            holder.tvName.setText(lists.get(position).getMATERIAL_NAME());
            holder.tvNo.setText(lists.get(position).getORDER_NO());
            holder.tvNum.setText(lists.get(position).getRECEIVE_COUNT());
            holder.tvDate.setText(lists.get(position).getRECEIVE_DATE());

            return convertView;
    }

    private class ViewHolder {
        public TextView tvListNo,tvNo,tvName,tvNum,tvDate ;//編號,單號,名稱,數量,日期
    }

}
