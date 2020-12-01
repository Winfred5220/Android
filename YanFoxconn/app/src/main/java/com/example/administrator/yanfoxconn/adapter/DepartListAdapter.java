package com.example.administrator.yanfoxconn.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.IndustrialSafetyActivity;
import com.example.administrator.yanfoxconn.bean.DepartListMessage;

import java.util.List;


/**
 * 部門基本信息列表
 * Created by wang on 2019/8/6.
 */

public class DepartListAdapter extends BaseAdapter{

    private IndustrialSafetyActivity activity;
    private List<DepartListMessage> listMessages;

    private OnClickListenerUpOrSee onClickListenerUpOrSee;

    public DepartListAdapter(IndustrialSafetyActivity activity, List<DepartListMessage> listMessages){
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_caselist_item, null);
            holder.caseName = (TextView) convertView.findViewById(R.id.tv_case_name);
            holder.address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tvGo = convertView.findViewById(R.id.tv_let_go);//放行
            holder.tvAdd = convertView.findViewById(R.id.tv_up_photo);//上傳

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.caseName.setText(listMessages.get(position).getCaseName());
        holder.address.setText(listMessages.get(position).getArea()+"-"+listMessages.get(position).getTung()+"-"+listMessages.get(position).getFloor()+"-"+listMessages.get(position).getProduct()+"-"+listMessages.get(position).getTechnology());

        holder.tvGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListenerUpOrSee != null) {
                    onClickListenerUpOrSee.OnClickListenerSee(position);
                }
            }
        });
        holder.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListenerUpOrSee != null) {
                    onClickListenerUpOrSee.OnClickListenerUp(position);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        public TextView caseName,address;//專案名稱,位置
        public TextView  tvGo, tvAdd;//放行,提交
    }

    public interface OnClickListenerUpOrSee {
        void OnClickListenerSee(int position);

        void OnClickListenerUp(int position);
    }

    public void setOnClickListenerUpOrSee(OnClickListenerUpOrSee onClickListenerUpOrSee) {
        this.onClickListenerUpOrSee = onClickListenerUpOrSee;
    }
}
