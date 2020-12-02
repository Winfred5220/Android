package com.example.administrator.yanfoxconn.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.DNCheckListActivity;
import com.example.administrator.yanfoxconn.bean.DNCheckMessage;

import java.util.List;


/**
 * 总务宿舍查验列表  的 適配器
 * Created by song on 2017/8/30.
 */

public class DNCheckListAdapter extends BaseAdapter {

    private DNCheckListActivity activity;
    private List<DNCheckMessage> dnCheckList;

    private OnClickListenerSeeOrAdd onClickListenerSeeOrAdd;

    public DNCheckListAdapter(DNCheckListActivity activity, List<DNCheckMessage> routeList) {

        this.activity = activity;
        this.dnCheckList = routeList;
    }

    @Override
    public int getCount() {
        return dnCheckList.size();

    }

    @Override
    public Object getItem(int i) {
        return dnCheckList.get(i);
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
            Log.e("===", "ss");
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dn_check_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.id = (TextView) convertView.findViewById(R.id.tv_id);
            holder.bed = (TextView) convertView.findViewById(R.id.tv_bed);
            holder.tvSee = convertView.findViewById(R.id.tv_see);//查看異常
            holder.tvAdd = convertView.findViewById(R.id.tv_add);//添加異常

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(dnCheckList.get(position).getREGULAREMP_NAME()+"\n"+dnCheckList.get(position).getREGULAREMP_BANBIE());
        holder.id.setText(dnCheckList.get(position).getREGULAREMP_NO());
        holder.bed.setText(dnCheckList.get(position).getREGULAREMP_BEDNUM());

//        if (Integer.parseInt(dnCheckList.get(position).getCount()) == 0) {
//
//            holder.ivRound.setImageResource(R.drawable.route_status_null);
//            holder.tvAdd.setClickable(false);
//        } else {
//
//            holder.ivRound.setImageResource(R.drawable.route_status_round);
//        }

        holder.tvSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListenerSeeOrAdd != null) {
                    onClickListenerSeeOrAdd.OnClickListenerSee(position);
                }
            }
        });
        holder.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListenerSeeOrAdd != null) {
                    onClickListenerSeeOrAdd.OnClickListenerAdd(position);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        public TextView name,bed,id, tvAdd,tvSee;

    }

    public interface OnClickListenerSeeOrAdd {
        void OnClickListenerSee(int position);

        void OnClickListenerAdd(int position);
    }

    public void setOnClickListenerSeeOrAdd(OnClickListenerSeeOrAdd onClickListenerSeeOrAdd) {
        this.onClickListenerSeeOrAdd = onClickListenerSeeOrAdd;
    }
}
