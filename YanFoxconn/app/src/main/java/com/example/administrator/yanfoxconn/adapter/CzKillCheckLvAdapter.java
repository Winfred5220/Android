package com.example.administrator.yanfoxconn.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.CyKillCheckActivity;
import com.example.administrator.yanfoxconn.bean.CzKillCheckLv;

import java.util.List;

/**
 * create by song
 * on 2020/2/28
 */
public class CzKillCheckLvAdapter extends BaseAdapter {
    private List<CzKillCheckLv> checkList;

    public CzKillCheckLvAdapter(CyKillCheckActivity activity, List<CzKillCheckLv> checkList) {
        this.checkList = checkList;

    }

    private OnClickListener onClickListener;

    @Override
    public int getCount() {
        Log.e("--------------", "checkList.size()==" + checkList.size());
        return checkList.size();
    }

    @Override
    public Object getItem(int i) {
        return checkList.get(i);
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
//            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lv_kill_item, null);
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_kill_lv_item, null);
            viewHolder.tvDate = convertView.findViewById(R.id.tv_create_date);
            viewHolder.tvPeople = convertView.findViewById(R.id.tv_people);
            viewHolder.tvTemp = convertView.findViewById(R.id.tv_temperature);
            viewHolder.tvRemark = convertView.findViewById(R.id.tv_remark);
            viewHolder.tvTempName = convertView.findViewById(R.id.tv_temp_name);
            viewHolder.tvRemarkName = convertView.findViewById(R.id.tv_remark_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvDate.setText(checkList.get(position).getXS_CREATE_DATE());
        viewHolder.tvPeople.setText(checkList.get(position).getXS_RECORDER());
        Log.e("---------------","checkList.get(position).getXS_TW()=="+checkList.get(position).getXS_TW());
        if (checkList.get(position).getXS_TW().toString().equals("")) {
            viewHolder.tvTempName.setVisibility(View.GONE);
            viewHolder.tvTemp.setVisibility(View.GONE);
        } else {
            viewHolder.tvTemp.setText(checkList.get(position).getXS_TW());
        }
        if (checkList.get(position).getXS_REMARK().equals("")) {
            viewHolder.tvRemarkName.setVisibility(View.GONE);
            viewHolder.tvRemark.setVisibility(View.GONE);
        } else {
            viewHolder.tvRemark.setText(checkList.get(position).getXS_REMARK());
        }



        return convertView;
    }

    private class ViewHolder {
        public TextView tvDate, tvPeople, tvTemp, tvRemark;
        public TextView tvTempName,tvRemarkName;

    }

    public interface OnClickListener {
        void OnCheckPhotos(int position);
    }

}
