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
import com.example.administrator.yanfoxconn.activity.DNLookListActivity;
import com.example.administrator.yanfoxconn.bean.AbnormalMessage;
import com.example.administrator.yanfoxconn.bean.DNCheckMessage;

import java.util.List;

/**
 * 異常列表
 * Created by song on 2017/9/14.
 */

public class DNLookListAdapter extends BaseAdapter {
    private DNLookListActivity dnLookListActivity;
//    private AbnormalSelfListActivity abnormalSelfListActivity;
    private List<DNCheckMessage> messageList;
    private String flag;//H環境R人員



    public DNLookListAdapter(DNLookListActivity dnLookListActivity, List<DNCheckMessage> messageList, String flag) {
        this.dnLookListActivity = dnLookListActivity;
        this.messageList = messageList;
        this.flag = flag;
//        for (int i = 0;i<messageList.size();i++){
//            Log.e("--------","messageList==="+messageList.get(i).getExce_desp());
//        }
    }
//    public DNLookListAdapterH(DNLookListActivity dnLookListActivity, List<AbnormalMessage> messageList) {
//        this.dnLookListActivity = dnLookListActivity;
//        this.messageList = messageList;
//        for (int i = 0;i<messageList.size();i++){
//            Log.e("--------","messageList==="+messageList.get(i).getExce_desp());
//        }
//    }


    @Override
    public int getCount() {
        return messageList.size();

    }

    @Override
    public Object getItem(int i) {
        return messageList.get(i);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dn_look_item, null);
            viewHolder.tvAddress = convertView.findViewById(R.id.tv_address);
            viewHolder.tvTime = convertView.findViewById(R.id.tv_time);
            viewHolder.tvDesp = convertView.findViewById(R.id.tv_description);
            viewHolder.tvMan = convertView.findViewById(R.id.tv_man);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.tvAddress.setText(messageList.get(position).getJc_area()+"-"+messageList.get(position).getJc_building()+"-"+messageList.get(position).getJc_room());
        viewHolder.tvTime.setText(messageList.get(position).getCreate_date());
        viewHolder.tvDesp.setText(messageList.get(position).getJc_remarks());
        if (flag.equals("R")) {
            viewHolder.tvMan.setVisibility(View.VISIBLE);
            viewHolder.tvMan.setText(messageList.get(position).getJc_bed() + "床-" + messageList.get(position).getEmp_name());

        }
        return convertView;
    }

    private class ViewHolder {
        public TextView tvAddress,tvMan, tvTime, tvDesp;
    }
}