package com.example.administrator.yanfoxconn.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.ComAbRouteListActivity;
import com.example.administrator.yanfoxconn.bean.RouteMessage;

import java.util.List;


/**
 * 巡檢進度表 的 適配器
 * Created by song on 2017/8/30.
 */

public class ComAbRouteListAdapter extends BaseAdapter {

    private ComAbRouteListActivity activity;
    private List<RouteMessage> routeMessageList;

    public ComAbRouteListAdapter(ComAbRouteListActivity activity, List<RouteMessage> routeList) {

        this.activity = activity;
        this.routeMessageList = routeList;
    }

    @Override
    public int getCount() {
        return routeMessageList.size();

    }

    @Override
    public Object getItem(int i) {
        return routeMessageList.get(i);
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
            Log.e("===", "ss");
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_com_ab_route_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.ivRound = (ImageView) convertView.findViewById(R.id.iv_round);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(routeMessageList.get(position).getDim_locale());
        holder.count.setText(routeMessageList.get(position).getCount());
        if (Integer.parseInt(routeMessageList.get(position).getCount()) == 0) {

            holder.ivRound.setImageResource(R.drawable.route_status_null);
         
        } else {

            holder.ivRound.setImageResource(R.drawable.route_status_round);
        }

        return convertView;
    }

    private class ViewHolder {
        public ImageView ivRound;
        public TextView name;
        public TextView count;
    }
}
