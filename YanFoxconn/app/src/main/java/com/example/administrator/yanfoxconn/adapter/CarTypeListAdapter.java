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
import com.example.administrator.yanfoxconn.bean.CarType;
import com.example.administrator.yanfoxconn.bean.ExceInfo;

import java.util.List;


/**
 * 普通物品列表 的 適配器
 * Created by wangqian on 2018/12/22.
 */

public class CarTypeListAdapter extends BaseAdapter {
    public Context mContext;
    private List<CarType> lists;

    public CarTypeListAdapter(Context context, List<CarType> lists) {

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

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_com_ab_route_item, null);

            //holder.tvNum = convertView.findViewById(R.id.tv_num);
            holder.tvContent = convertView.findViewById(R.id.tv_name);
           // holder.tvExceDesp = convertView.findViewById(R.id.tv_exce_desp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //holder.tvNum.setText(num+"");
        holder.tvContent.setText(lists.get(position).getName());
        holder.tvContent.setTextColor(R.color.color_7edbf4);
       // holder.tvExceDesp.setText(lists.get(position).getType());

        return convertView;
    }

    private class ViewHolder {
        public TextView tvNum, tvContent,tvExceDesp,tvResult;//編號,名稱,異常描述，異常時間

    }


}
