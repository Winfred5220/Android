package com.example.administrator.yanfoxconn.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.GoodsGeneralActivity;
import com.example.administrator.yanfoxconn.bean.GoodsMessage;
import java.util.HashMap;
import java.util.List;

/**
 * 普通物品列表 的 適配器
 * Created by wangqian on 2018/12/22.
 */

public class GoodsListAdapter extends RecyclerView.Adapter<GoodsListAdapter.ViewHolder>{

    private GoodsGeneralActivity activity;
    private List<GoodsMessage> lists;
    private static HashMap<String,String> goodsMap = new HashMap<>();

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvNo,tvName,tvSpec,tvUnit;//序號 部位 檢查項目
        EditText etNum;//數量

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNo = itemView.findViewById(R.id.tv_list_no);
            tvName = itemView.findViewById(R.id.tv_list_name);
            tvSpec= itemView.findViewById(R.id.tv_list_spec);
            etNum=itemView.findViewById(R.id.et_list_num);
            tvUnit =  itemView.findViewById(R.id.tv_list_unit);
        }

    }

    @Override
    public String toString() {
        return "GoodsListAdapter{" +
                "goodsMap=" + goodsMap +
                '}';
    }

    public static HashMap<String, String> getGoodsMap() {
        return goodsMap;
    }

    public GoodsListAdapter(GoodsGeneralActivity activity, List<GoodsMessage> lists) {

        this.activity = activity;
        this.lists = lists;
    }

    /**
     * 重写 避免滑动过程界面混乱
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_goods_list,parent,false);
        ViewHolder holder = new ViewHolder(view);

        //點檢內容輸入框
        holder.etNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int position = holder.getAdapterPosition();
                //輸入文字
                goodsMap.put(lists.get(position).getOUTDNO(),charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvNo.setText(lists.get(position).getOUTDNO());
        holder.tvName.setText(lists.get(position).getOUTD01());
        holder.tvSpec.setText(lists.get(position).getOUTD02());
        holder.etNum.setText(lists.get(position).getOUTD03());
        holder.tvUnit.setText(lists.get(position).getOUTD04());
        goodsMap.put(lists.get(position).getOUTDNO(),lists.get(position).getOUTD03());
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        Log.e("---lists.size()---",lists.size()+"");
        return lists.size();
    }



}
