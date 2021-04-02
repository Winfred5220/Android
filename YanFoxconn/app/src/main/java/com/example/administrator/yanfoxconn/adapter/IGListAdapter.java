package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.GCHead;
import com.example.administrator.yanfoxconn.bean.IGMessage;

import java.util.List;

/**
 * Created by Song
 * on 2021/4/1
 * Description：宿舍寄存 申請列表界面
 */
public class IGListAdapter extends BaseAdapter {
    public Context mContext;
    private List<GCHead> lists;
    private List<IGMessage> igLists;
    private String from ="";

    public IGListAdapter(Context context, List<IGMessage> igLists, String from) {
        this.mContext = context;
        this.igLists = igLists;
        this.from = from;
    }

    @Override
    public int getCount() {


            return igLists.size();

    }

    @Override
    public Object getItem(int position) {

        return igLists.get(position);

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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_ig_lv, null);
            holder.tvStatue = (TextView)convertView.findViewById(R.id.tv_statue);
            holder.tvId = (TextView)convertView.findViewById(R.id.tv_id);
            holder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
            holder.tvCpc = (TextView)convertView.findViewById(R.id.tv_cpc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


            holder.tvStatue.setText(igLists.get(position).getS_STATUS());
            holder.tvCpc.setText(igLists.get(position).getS_USER_DEP());
            holder.tvName.setText(igLists.get(position).getS_USER_NAME());
            holder.tvId.setText(igLists.get(position).getS_USER_ID());

        return convertView;
        }

    private class ViewHolder{
        public TextView tvStatue,tvId,tvName,tvCpc;
    }

}
