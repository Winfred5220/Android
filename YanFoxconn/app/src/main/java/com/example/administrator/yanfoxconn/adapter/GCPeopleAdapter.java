package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.DNReform;
import com.example.administrator.yanfoxconn.bean.GCHead;

import java.util.List;

/**
 * Created by Song
 * on 2020/12/19
 * Description：體症異常 模糊查詢列表
 */
public class GCPeopleAdapter extends BaseAdapter {
    public Context mContext;
    private List<GCHead> lists;

    public GCPeopleAdapter(Context context, List<GCHead> lists) {
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
            Log.e("===", "ss");
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gc_search_lv, null);
            holder.tvNum = (TextView)convertView.findViewById(R.id.tv_num);
            holder.tvSex = (TextView)convertView.findViewById(R.id.tv_sex);
            holder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
            holder.tvId = (TextView)convertView.findViewById(R.id.tv_id);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvNum.setText(""+(position+1));
        holder.tvSex.setText(lists.get(position).getIn_Sex());
        holder.tvName.setText(lists.get(position).getIn_Name());
        holder.tvId.setText(lists.get(position).getIn_Number());

        return convertView;
        }

    private class ViewHolder{
        public TextView tvNum,tvId,tvName,tvSex;
    }

}
