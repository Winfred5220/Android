package com.example.administrator.yanfoxconn.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.EventTimeLvActivity;
import com.example.administrator.yanfoxconn.bean.EventTime;
import com.example.administrator.yanfoxconn.utils.BaseActivity;

import java.util.List;

/**
 * Created by song on 2018/7/26.
 */

public class EventTimeAdapter extends BaseAdapter {
    private EventTimeLvActivity activity;
    private List<EventTime> eventTimeList;

    public EventTimeAdapter(EventTimeLvActivity activity,List<EventTime> list){
        this.activity = activity;
        this.eventTimeList = list;
    }

    @Override
    public int getCount() {
        return eventTimeList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventTimeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            Log.e("===", "ss");
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_event_time, null);

            holder.tvId = convertView.findViewById(R.id.tv_id);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvTime = convertView.findViewById(R.id.tv_time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvId.setText(eventTimeList.get(position).getSc_creator_id());
        holder.tvName.setText(eventTimeList.get(position).getSc_creator());
        holder.tvTime.setText(eventTimeList.get(position).getTime_mm());

        return convertView;
    }

    private class ViewHolder {
        public TextView  tvId, tvName,tvTime;//工號,姓名,計時
    }
}
