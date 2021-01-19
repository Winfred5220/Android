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
import com.example.administrator.yanfoxconn.bean.GTMain;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiExceMsg;

import java.util.List;


/**
 * 普通物品列表 的 適配器
 * Created by wangqian on 2018/12/22.
 */

public class GTCheckListAdapter extends BaseAdapter {
    public Context mContext;
    private List<GTMain> lists;
    private String flag;//是今日施工Y還是未施工N

    public GTCheckListAdapter(Context context, List<GTMain> lists,String flag) {
        this.mContext = context;
        this.lists = lists;
        this.flag = flag;
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

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gt_search_item, null);

            holder.tvNum = convertView.findViewById(R.id.tv_num);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvBuilding = convertView.findViewById(R.id.tv_building);
            holder.tvWinVendor = convertView.findViewById(R.id.tv_win_vendor);
            holder.tvCpc = convertView.findViewById(R.id.tv_cpc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (flag.equals("N")){
            holder.tvNum.setVisibility(View.GONE);
        }
        holder.tvNum.setText(String.valueOf(lists.get(position).getCount()));
        holder.tvName.setText(num+lists.get(position).getProject_name());
        holder.tvBuilding.setText("施工位置："+lists.get(position).getBuilding());
        holder.tvWinVendor.setText("中標廠商："+lists.get(position).getWin_vendor());
        holder.tvCpc.setText("申請單位："+lists.get(position).getCpc());
Log.e("--------","tvNum=="+String.valueOf(lists.get(position).getCount())+"==tvBuilding=="+lists.get(position).getBuilding()+"==tvWinVendor=="+lists.get(position).getWin_vendor()+"==tvCpc=="+lists.get(position).getCpc());
        return convertView;
    }

    private class ViewHolder {
        public TextView tvNum, tvName,tvBuilding,tvWinVendor,tvCpc;//次數，工程名稱，施工位置，中標廠商，申請單位
    }


}
