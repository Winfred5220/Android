package com.example.administrator.yanfoxconn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.GTDetailActivity;
import com.example.administrator.yanfoxconn.bean.AbnormalMessage;
import com.example.administrator.yanfoxconn.bean.ExceInfo;
import com.example.administrator.yanfoxconn.bean.ExcePhoto;

import java.util.List;


/**
 * 營建 工程管理系統 異常詳情 的 適配器
 * Created by wangqian on 2018/12/22.
 */

public class GTDetailLvAdapter extends BaseAdapter {
    public GTDetailActivity mContext;
    private List<AbnormalMessage> lists;
    private List<ExcePhoto> photos;


    public GTDetailLvAdapter(GTDetailActivity context, List<AbnormalMessage> lists) {

        this.mContext = context;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        Log.e("--------","lists.size()==="+lists.size());
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        int num = position +1;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gt_detail_lv_item, null);

            holder.llGetImage = convertView.findViewById(R.id.ll_get_image);
            holder.tvContent = convertView.findViewById(R.id.tv_content);
            holder.tvExceDesp = convertView.findViewById(R.id.tv_description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

       photos = lists.get(position).getPhoto();
        holder.tvContent.setText(lists.get(position).getContent());
        holder.tvExceDesp.setText(lists.get(position).getExce_desp());

        for (int i = 0; i < photos.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            String imgStr = photos.get(i).getEXCE_FILENAME1();
            Log.e("------img-----", "======" + position);
            byte[] decode = Base64.decode(imgStr, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            imageView.setImageBitmap(bitmap);
            holder.llGetImage.addView(imageView);
        }
        return convertView;
    }

    private class ViewHolder {
        public TextView  tvContent,tvExceDesp;//名稱,異常描述
        public LinearLayout llGetImage;//圖片

    }


}
