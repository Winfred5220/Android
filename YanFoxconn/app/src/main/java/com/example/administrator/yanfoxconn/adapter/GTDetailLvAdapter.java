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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.GTDetailActivity;
import com.example.administrator.yanfoxconn.bean.AbnormalMessage;
import com.example.administrator.yanfoxconn.bean.ExceInfo;
import com.example.administrator.yanfoxconn.bean.ExcePhoto;

import java.util.ArrayList;
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
            holder.tv1 = convertView.findViewById(R.id.tv_1);
            holder.tvResult = convertView.findViewById(R.id.tv_result);
            holder.tvExceDesp = convertView.findViewById(R.id.tv_description);
            holder.rl =convertView.findViewById(R.id.rl);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (lists.get(position).getExce_id().equals("")){
//顯示輸入框的內容
//            holder.tv1.setVisibility(View.GONE);
//            holder.tvResult.setVisibility(View.GONE);
//            holder.tvContent.setText(lists.get(position).getContent());
//            holder.tvExceDesp.setText(lists.get(position).getExce_result());
holder.rl.setVisibility(View.GONE);
        }else{
            holder.tvContent.setText(lists.get(position).getContent());
            holder.tvResult.setText(lists.get(position).getExce_result());
            holder.tvExceDesp.setText(lists.get(position).getExce_desp());
            holder.llGetImage.setVisibility(View.VISIBLE);

            holder.rl.setVisibility(View.VISIBLE);
            photos = new ArrayList<>();
            photos = lists.get(position).getPhoto();
      for (int i = 0; i < photos.size(); i++) {
                ImageView imageView = new ImageView(mContext);
                String imgStr = photos.get(i).getEXCE_FILENAME1();
                Log.e("------img-----", "======" + position);
                byte[] decode = Base64.decode(imgStr, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                imageView.setImageBitmap(bitmap);
                holder.llGetImage.addView(imageView);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        public TextView  tvContent,tv1,tvResult,tvExceDesp;//名稱,異常項，異常描述
        public LinearLayout llGetImage;//圖片
//        public ListView lvPhoto;//
        public RelativeLayout rl;
    }

//    private class GTDEtailLvAdatper extends BaseAdapter
//    {
//        private List<ExcePhoto> photos;
//
//        public void GTDEtailLvAdatper( List<ExcePhoto> photos){
//            this.photos = photos;
//        }
//        @Override
//        public int getCount() {
//            return 0;
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            //            for (int i = 0; i < photos.size(); i++) {
////                ImageView imageView = new ImageView(mContext);
////                String imgStr = photos.get(i).getEXCE_FILENAME1();
////                Log.e("------img-----", "======" + position);
////                byte[] decode = Base64.decode(imgStr, Base64.DEFAULT);
////                Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
////                imageView.setImageBitmap(bitmap);
////                holder.llGetImage.addView(imageView);
////            }
//            return null;
//        }
//    }

}
