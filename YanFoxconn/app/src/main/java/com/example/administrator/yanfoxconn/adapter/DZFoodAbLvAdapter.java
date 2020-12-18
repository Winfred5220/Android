package com.example.administrator.yanfoxconn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.DZFoodAbListActivty;
import com.example.administrator.yanfoxconn.bean.DZFoodAbList;

import java.util.List;

/**
 * Created by Song
 * on 2020/8/20
 * Description：人資監餐 異常列表item
 */
public class DZFoodAbLvAdapter extends BaseAdapter {
    private DZFoodAbListActivty dzFoodAbListActivty;
    private List<DZFoodAbList> abList;
    private String from;
    public DZFoodAbLvAdapter (DZFoodAbListActivty activity, List<DZFoodAbList> messageList, String from){
        this.dzFoodAbListActivty = activity;
        this.abList = messageList;
        this.from = from;
    }
    @Override
    public int getCount() {
        return abList.size();
    }

    @Override
    public Object getItem(int position) {
        return abList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dz_food_ab, null);
            viewHolder.tvId= convertView.findViewById(R.id.tv_id);
            viewHolder.tvType1 = convertView.findViewById(R.id.tv_type1);
            viewHolder.tvType2 = convertView.findViewById(R.id.tv_type2);
            viewHolder.tvType3 = convertView.findViewById(R.id.tv_type3);
            viewHolder.tvScore = convertView.findViewById(R.id.tv_score);
            viewHolder.tvDesp = convertView.findViewById(R.id.tv_desp);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        if (from.equals("GA")){
            viewHolder.tvDesp.setVisibility(View.VISIBLE);
            viewHolder.tvDesp.setText(abList.get(position).getExce_result()+":"+"\n"+abList.get(position).getExce_desp());
            viewHolder.tvType1.setVisibility(View.GONE);
            viewHolder.tvType2.setVisibility(View.GONE);
            viewHolder.tvType3.setVisibility(View.GONE);
            viewHolder.tvScore.setVisibility(View.GONE);
        }
        viewHolder.tvId.setText((position+1)+"");
        viewHolder.tvType1.setText(abList.get(position).getName1());
        viewHolder.tvType2.setText(abList.get(position).getName2());
        viewHolder.tvType3.setText(abList.get(position).getName3());
        viewHolder.tvScore.setText(abList.get(position).getScore());

        return convertView;
    }

    private class ViewHolder {
        public TextView tvId,tvType1,tvType2,tvType3,tvScore,tvDesp;
    }
}
