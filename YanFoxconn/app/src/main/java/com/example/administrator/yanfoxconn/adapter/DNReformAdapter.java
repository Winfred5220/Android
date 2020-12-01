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
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiExceMsg;

import java.util.List;

/**
 * Created by Song
 * on 2020/10/20
 * Description：宿舍查驗 異常整改item
 */
public class DNReformAdapter extends BaseAdapter {
    public Context mContext;
    private List<DNReform> lists;

    private OnClickListenerSeeOrAdd onClickListenerSeeOrAdd;
    public DNReformAdapter(Context context, List<DNReform> lists) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dn_reform_lv, null);
            holder.tvNo = (TextView)convertView.findViewById(R.id.tv_no);
            holder.tvRoom = (TextView)convertView.findViewById(R.id.tv_room);
            holder.tvBed = (TextView)convertView.findViewById(R.id.tv_bed);
            holder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
            holder.tvDate = (TextView)convertView.findViewById(R.id.tv_date);
            holder.tvResult = (TextView)convertView.findViewById(R.id.tv_result);
            holder.tvSee = (TextView)convertView.findViewById(R.id.tv_see);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvNo.setText(""+(position+1));
        holder.tvRoom.setText(lists.get(position).getJc_room());
        holder.tvBed.setText(lists.get(position).getJc_bed());
        holder.tvName.setText(lists.get(position).getEmp_name());
        holder.tvDate.setText(lists.get(position).getJc_date());
        holder.tvResult.setText(lists.get(position).getJc_result().get(0).getName());
//        Log.e("-----------","lists.get(position).getJc_result().get(0).getName()==="+lists.get(position).getJc_result().get(0).getName());
       holder.tvSee.setText("整改");
        holder.tvSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListenerSeeOrAdd != null) {
                    onClickListenerSeeOrAdd.OnClickListenerSee(position);
                }
            }
        });
        return convertView;
        }

    private class ViewHolder{
        public TextView tvNo,tvRoom,tvBed,tvName,tvDate,tvResult;
        public TextView tvSee;
    }
    public interface OnClickListenerSeeOrAdd {
        void OnClickListenerSee(int position);


    }

    public void setOnClickListenerSeeOrAdd(OnClickListenerSeeOrAdd onClickListenerSeeOrAdd) {
        this.onClickListenerSeeOrAdd = onClickListenerSeeOrAdd;
    }
}
