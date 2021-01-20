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
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiMsg;

import java.util.List;


/**
 * 設備借用狀態列表 的 適配器
 * Created by wangqian on 2020/01/20.
 */

public class DeviceBorrowListAdapter extends BaseAdapter {
    public Context mContext;
    private List<ZhiyinshuiMsg> lists;


    public DeviceBorrowListAdapter(Context context, List<ZhiyinshuiMsg> lists) {

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

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_device_borrow_list, null);

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
        holder.tvCode.setText(lists.get(position).getDIM_NAME());
        holder.tvName.setText(lists.get(position).getDIM_PRODUCER());
        holder.tvPosition.setText(lists.get(position).getDIM_no());
        if (lists.get(position).getDIM_STATE().equals("0")){
            holder.tvCount.setText("可借用");
            holder.tvCount.setTextColor(convertView.getResources().getColor(R.color.color_42D42B));
        }else{
            holder.tvCount.setText("借用中");
            holder.tvCount.setTextColor(convertView.getResources().getColor(R.color.color_ff552e));
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView tvNum,tvCode,tvName, tvPosition,tvCount;//序號，名稱,品牌,型號，狀態

    }


}
