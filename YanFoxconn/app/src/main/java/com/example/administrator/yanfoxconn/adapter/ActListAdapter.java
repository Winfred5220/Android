package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.AQ110Message;
import com.example.administrator.yanfoxconn.bean.ActivityMsg;

import java.util.List;

/**
 * Created by S1007989 on 2021/05/21
 * Description：活動查詢列表
 */
public class ActListAdapter extends BaseAdapter {
    public Context mContext;
    private List<ActivityMsg> lists;

    public ActListAdapter(Context context, List<ActivityMsg> lists) {
        this.mContext = context;
        this.lists = lists;

    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_act_list, null);
            holder.tvNum = (TextView)convertView.findViewById(R.id.tv_num);
            holder.tvCpc = (TextView)convertView.findViewById(R.id.tv_cpc);
            holder.tvDate = (TextView)convertView.findViewById(R.id.tv_date);
            holder.tvPosition = (TextView)convertView.findViewById(R.id.tv_position);
//            holder.tvContent = (TextView)convertView.findViewById(R.id.tv_content);
//            holder.tvPhone = (TextView)convertView.findViewById(R.id.tv_phone);
//            holder.ll110 = (LinearLayout)convertView.findViewById(R.id.ll_110);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvNum.setText(""+(position+1));

        holder.tvCpc.setText(lists.get(position).getAct_name());
        holder.tvDate.setText(lists.get(position).getAct_type());
        holder.tvPosition.setText(lists.get(position).getCreate_name());
//        holder.tvContent.setText(lists.get(position).getAct_num_person());
//        holder.tvPhone.setText(lists.get(position).getAct_statue());
        //Log.e("------", position +"-->"+lists.get(position).getAct_num_now());
//        if (lists.get(position).getAct_statue()!=null&&lists.get(position).getAct_statue().equals("N")){
//            holder.ll110.setBackgroundResource(R.color.color_fee123);
//        }else {
//            holder.ll110.setBackgroundResource(R.color.white);
//        }

        return convertView;
        }

    private class ViewHolder{
        public TextView tvNum,tvCpc,tvDate,tvPosition,tvContent,tvPhone;
        LinearLayout  ll110;
    }

}
