package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.ProductDivisionMessage;

import java.util.List;

/**
 * Created by wangqian on 2019/4/19.
 */

public class ProductDivisionListAdapter extends BaseAdapter {
    private Context mContext;
    private List<ProductDivisionMessage> mProductDivisionMessagelist;

    public ProductDivisionListAdapter(Context mContext, List<ProductDivisionMessage> mProductDivisionMessagelist) {
        this.mContext = mContext;
        this.mProductDivisionMessagelist = mProductDivisionMessagelist;
    }

    @Override
    public int getCount() {
        Log.e("--------","mProductDivisionMessagelist.size()==="+mProductDivisionMessagelist.size());
        return mProductDivisionMessagelist.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductDivisionMessagelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            Log.e("-------------", "mProductDivisionMessagelist.get(position).getName()===="+mProductDivisionMessagelist.get(position).getName());
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_product_item, null,false);
        }
        TextView tvItemName = (TextView) convertView.findViewById(R.id.tv_item_name);
        ImageView ivImg = (ImageView) convertView.findViewById(R.id.iv_icon);

        tvItemName.setText(mProductDivisionMessagelist.get(position).getName());
        ivImg.setImageResource(mProductDivisionMessagelist.get(position).getImageId());

        return convertView;
    }
}
