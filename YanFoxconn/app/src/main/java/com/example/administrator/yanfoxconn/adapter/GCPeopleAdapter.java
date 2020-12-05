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
 * on 2020/10/20
 * Description：宿舍查驗 異常整改item
 */
public class GCPeopleAdapter extends BaseAdapter {
    public Context mContext;
    private List<GCHead> lists;

    private OnClickListenerSeeOrAdd onClickListenerSeeOrAdd;
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
            holder.tvSee = (TextView)convertView.findViewById(R.id.tv_see);
            holder.tvAdd = (TextView)convertView.findViewById(R.id.tv_add);
            holder.tvId = (TextView)convertView.findViewById(R.id.tv_id);
            holder.tvDel = (TextView)convertView.findViewById(R.id.tv_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvNum.setText(""+(position+1));
        holder.tvSex.setText(lists.get(position).getIn_Sex());
        holder.tvName.setText(lists.get(position).getIn_Name());
        holder.tvId.setText(lists.get(position).getIn_Number());
//        Log.e("-----------","lists.get(position).getJc_result().get(0).getName()==="+lists.get(position).getJc_result().get(0).getName());

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
        holder.tvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListenerSeeOrAdd != null) {
                    onClickListenerSeeOrAdd.OnClickListenerDel(position);
                }
            }
        });
        return convertView;
        }

    private class ViewHolder{
        public TextView tvNum,tvId,tvName,tvSex;
        public TextView tvSee,tvAdd,tvDel;
    }
    public interface OnClickListenerSeeOrAdd {
        void OnClickListenerSee(int position);

void OnClickListenerDel(int position);

        void OnClickListenerAdd(int position);
    }

    public void setOnClickListenerSeeOrAdd(OnClickListenerSeeOrAdd onClickListenerSeeOrAdd) {
        this.onClickListenerSeeOrAdd = onClickListenerSeeOrAdd;
    }
}
