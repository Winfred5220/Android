package com.example.administrator.yanfoxconn.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.EventListActivity;
import com.example.administrator.yanfoxconn.bean.EventMessage;

import java.util.List;


/**
 * 人資活動列表
 * Created by song on 2018/5/25.
 */

public class EventListAdapter extends BaseAdapter{

    private EventListActivity activity;
    private List<EventMessage> listMessages;
    private String status;//狀態

    private OnClickListenerCancelOrShow onClickListenerCancelOrShow;

    public EventListAdapter(EventListActivity activity, List<EventMessage> listMessages){
        this.activity = activity;
        this.listMessages = listMessages;
    }

    @Override
    public int getCount() {
        return listMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return listMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            Log.e("===", "ss");
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_eventlv_item, null);
            holder.tvEventName = (TextView) convertView.findViewById(R.id.tv_event_name);

            holder.tvCancel = convertView.findViewById(R.id.tv_cancle);//取消
            holder.tvShow = convertView.findViewById(R.id.tv_show);//查看
            holder.tvTime = convertView.findViewById(R.id.tv_time);//計時

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvEventName.setText(listMessages.get(position).getDim_locale());
//        status = listMessages.get(position).getStatus();

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListenerCancelOrShow != null) {
                    onClickListenerCancelOrShow.OnClickListenerCancel(position);
                }
            }
        });
        holder.tvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListenerCancelOrShow != null) {
                    onClickListenerCancelOrShow.OnClickListenerShow(position);
                }
            }
        });
        holder.tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListenerCancelOrShow!=null){
                    onClickListenerCancelOrShow.OnClickListenerTime(position);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        public TextView tvEventName;//活動名稱
        public TextView  tvCancel, tvShow,tvTime;//取消,查看,計時
    }

    public interface OnClickListenerCancelOrShow {
        void OnClickListenerCancel(int position);

        void OnClickListenerShow(int position);

        void OnClickListenerTime(int position);
    }

    public void setOnClickListenerCancelOrShow(OnClickListenerCancelOrShow onClickListenerCancelOrShow) {
        this.onClickListenerCancelOrShow = onClickListenerCancelOrShow;
    }
}
