package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.AQ110Message;
import com.example.administrator.yanfoxconn.bean.GCBody;

import java.util.List;

/**
 * Created by Song
 * on 2020/12/05
 * Description：安保部處警反饋追蹤情況item
 */
public class CommonFormsCjfkzzAdapter extends BaseAdapter {
    public Context mContext;
    private List<AQ110Message> lists;

    private OnClickListenerSeeOrAdd onClickListenerSeeOrAdd;

    public CommonFormsCjfkzzAdapter(Context context, List<AQ110Message> lists) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gc_health_lv, null);
            holder.tvNum = (TextView)convertView.findViewById(R.id.tv_num);
            holder.tvDate = (TextView)convertView.findViewById(R.id.tv_date);
            holder.tvTemp = (TextView)convertView.findViewById(R.id.tv_temp);
            holder.tvDep = (TextView)convertView.findViewById(R.id.tv_dep);
            holder.tvDel = (TextView)convertView.findViewById(R.id.tv_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvNum.setText(""+(position+1));
        holder.tvDate.setText(lists.get(position).getB_CREATE_DATE().substring(0,16));
        holder.tvTemp.setText(lists.get(position).getB_REDEEM());
        holder.tvDep.setText(lists.get(position).getB_DESC());
        return convertView;
        }

    private class ViewHolder{
        public TextView tvNum,tvDate,tvTemp,tvDep;
        public TextView tvDel;
    }
    public interface OnClickListenerSeeOrAdd {

        void OnClickListenerDel(int position);

    }

    public void setOnClickListenerSeeOrAdd(OnClickListenerSeeOrAdd onClickListenerSeeOrAdd) {
        this.onClickListenerSeeOrAdd = onClickListenerSeeOrAdd;
    }
}
