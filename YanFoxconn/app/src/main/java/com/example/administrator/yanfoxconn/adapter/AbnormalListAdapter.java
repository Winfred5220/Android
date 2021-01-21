package com.example.administrator.yanfoxconn.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.AbnormalListActivity;
import com.example.administrator.yanfoxconn.activity.AbnormalSelfListActivity;
import com.example.administrator.yanfoxconn.activity.ComAbAbnormalListActivity;
import com.example.administrator.yanfoxconn.activity.ComAbDListActivity;
import com.example.administrator.yanfoxconn.activity.ComAbRouteItemListActivity;
import com.example.administrator.yanfoxconn.bean.AbnormalMessage;
import com.example.administrator.yanfoxconn.bean.ComScanViewMessage;
import com.example.administrator.yanfoxconn.bean.RouteMessage;

import java.util.List;

/**
 * 異常列表
 * Created by song on 2017/9/14.
 */

public class AbnormalListAdapter extends BaseAdapter {
    private AbnormalListActivity abnormalActivity;
    private AbnormalSelfListActivity abnormalSelfListActivity;
    private ComAbAbnormalListActivity comAbAbnormalListActivity;
    private ComAbDListActivity comAbDListActivity;
    private ComAbRouteItemListActivity comAbRouteItemListActivity;
    private List<AbnormalMessage> messageList;
    private List<RouteMessage> routeMessages=null;
    private List<ComScanViewMessage> comScanViewMessages=null;
    private String type = "";


    public  AbnormalListAdapter(AbnormalListActivity abnormalActivity, List<AbnormalMessage> messageList) {
        this.abnormalActivity = abnormalActivity;
        this.messageList = messageList;
        for (int i = 0;i<messageList.size();i++){
            Log.e("--------","messageList==="+messageList.get(i).getExce_desp());
        }
    }
    public  AbnormalListAdapter(AbnormalSelfListActivity abnormalSelfListActivity, List<AbnormalMessage> messageList) {
        this.abnormalSelfListActivity = abnormalSelfListActivity;
        this.messageList = messageList;
        for (int i = 0;i<messageList.size();i++){
            Log.e("--------","messageList==="+messageList.get(i).getExce_desp());
        }
    }
    public  AbnormalListAdapter(ComAbAbnormalListActivity comAbAbnormalListActivity, List<AbnormalMessage> messageList,String type) {
        this.comAbAbnormalListActivity = comAbAbnormalListActivity;
        this.messageList = messageList;
        this.type = type;
        for (int i = 0;i<messageList.size();i++){
            Log.e("--------","messageList==="+messageList.get(i).getExce_desp());
        }
    }
    public  AbnormalListAdapter(ComAbDListActivity comAbDListActivity, List<RouteMessage> routeMessages, String type) {
        this.comAbDListActivity = comAbDListActivity;
        this.routeMessages = routeMessages;
        this.type = type;
        for (int i = 0;i<routeMessages.size();i++){
            Log.e("--------","messageList==="+routeMessages.get(i).getDim_id());
        }
    }
    public  AbnormalListAdapter(ComAbRouteItemListActivity comAbRouteItemListActivity, List<ComScanViewMessage> comScanViewMessages, String type) {
        this.comAbRouteItemListActivity = comAbRouteItemListActivity;
        this.comScanViewMessages = comScanViewMessages;
        this.type = type;

    }

    @Override
    public int getCount() {
        if (type.equals("Item")||type.equals("GCGL")){
           return comScanViewMessages.size();
        }else
        if (routeMessages!=null){
            return routeMessages.size();
        }else{
        return messageList.size();}

    }

    @Override
    public Object getItem(int i) {
        if (type.equals("Item")){
            return comScanViewMessages.get(i);
        }else if(type.equals("GCGL")){
            return comScanViewMessages.get(i);
        }else
        if (routeMessages!=null){
            return routeMessages.get(i);
        }else{
            return messageList.get(i);}
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_see_item, null);
            viewHolder.tvAddress = convertView.findViewById(R.id.tv_address);
            viewHolder.tvTime = convertView.findViewById(R.id.tv_time);
            viewHolder.tvDesp = convertView.findViewById(R.id.tv_description);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        if (type.equals("Item")){
            viewHolder.tvAddress.setText(comScanViewMessages.get(position).getSc_creator());
            viewHolder.tvTime.setText(comScanViewMessages.get(position).getSc_create_date());
            viewHolder.tvDesp.setText("異常項數:"+comScanViewMessages.get(position).getCount());
        }else if(type.equals("GCGL")){

            viewHolder.tvAddress.setText(comScanViewMessages.get(position).getSc_creator());
            viewHolder.tvTime.setText(comScanViewMessages.get(position).getSc_create_date());
            viewHolder.tvDesp.setText(comScanViewMessages.get(position).getCname()+"  異常項數:"+comScanViewMessages.get(position).getCount());
        }else if(type!=""){
            if (routeMessages!=null){
                viewHolder.tvAddress.setText(routeMessages.get(position).getDim_locale());
                viewHolder.tvTime.setVisibility(View.GONE);
                        viewHolder.tvDesp.setVisibility(View.GONE);
            }else{
            viewHolder.tvAddress.setText(messageList.get(position).getContent());
            viewHolder.tvTime.setText(messageList.get(position).getExce_time());
            viewHolder.tvDesp.setText(messageList.get(position).getExce_desp());}
        } else{
                viewHolder.tvAddress.setText(messageList.get(position).getExce_add());
                viewHolder.tvTime.setText(messageList.get(position).getExce_time());
                viewHolder.tvDesp.setText(messageList.get(position).getExce_desp());}

        return convertView;
    }

    private class ViewHolder {
        public TextView tvAddress, tvTime, tvDesp;
    }
}