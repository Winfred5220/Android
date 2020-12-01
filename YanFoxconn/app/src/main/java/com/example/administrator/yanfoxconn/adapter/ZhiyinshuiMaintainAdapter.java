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
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiExceMsg;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiMsg;

import java.util.List;


/**
 * 普通物品列表 的 適配器
 * Created by wangqian on 2018/12/22.
 */

public class ZhiyinshuiMaintainAdapter extends BaseAdapter {
    public Context mContext;
    private List<ZhiyinshuiMsg> lists;

    public ZhiyinshuiMaintainAdapter(Context context, List<ZhiyinshuiMsg> lists) {
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

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_zhiyinshui_exce_view, null);

            holder.tvNum = convertView.findViewById(R.id.tv_num);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvPosition = convertView.findViewById(R.id.tv_position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvNum.setText(num+"");
        holder.tvName.setText(lists.get(position).getDIM_PRODUCER());
        holder.tvPosition.setText(lists.get(position).getDIM_AREA()+"-"+lists.get(position).getDIM_BUILD()+"-"+lists.get(position).getDIM_LAYER()+"-"+lists.get(position).getDIM_POSITION());


        return convertView;
    }

    private class ViewHolder {
        public TextView tvNum, tvName,tvPosition;//編號,名稱,異常描述，異常時間

    }


}
