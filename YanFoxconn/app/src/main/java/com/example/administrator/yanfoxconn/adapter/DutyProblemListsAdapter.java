package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.DutyProblemList;

import java.util.List;


/**
 * 值班課長稽核問題 適配器
 * Created by wang on 2019/8/16.
 */

public class DutyProblemListsAdapter extends BaseAdapter {

    private Context mContext;
    private List<DutyProblemList> lists;


    public DutyProblemListsAdapter(Context context, List<DutyProblemList> lists) {

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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            Log.e("-------------", "lists.get(position).getBCR04()===="+lists.get(position).getNAME());
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_duty_problem_list, null);

            holder.tvTime = convertView.findViewById(R.id.tv_time);
            holder.tvPosition = convertView.findViewById(R.id.tv_position);
            holder.tvContent = convertView.findViewById(R.id.tv_content);
            holder.tvTeam = convertView.findViewById(R.id.tv_team);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvCaptain = convertView.findViewById(R.id.tv_captain_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvTime.setText(lists.get(position).getCHECK_DATE());
        holder.tvPosition.setText(lists.get(position).getPOSITION());
        holder.tvContent.setText(lists.get(position).getCONDITION());
        holder.tvTeam.setText(lists.get(position).getTEAM());
        holder.tvName.setText(lists.get(position).getPERSON());
        holder.tvCaptain.setText(lists.get(position).getCAPTAIN());
        return convertView;
    }

    private class ViewHolder {
        public TextView tvTime, tvPosition, tvContent, tvTeam, tvName, tvCaptain;//時間,位置,巡崗狀況,科隊,負責人,隊長

    }


}
