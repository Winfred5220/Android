package com.example.administrator.yanfoxconn.adapter;

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
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.ComAbDetailActivity;
import com.example.administrator.yanfoxconn.activity.DZFoodPhotoCheckActivity;
import com.example.administrator.yanfoxconn.bean.ExcePhoto;

import java.util.List;

/**
 * Created by Song
 * on 2020/8/20
 * Description：查看菜品图片 adapter
 */
public class DZFoodPhotoAdapter extends BaseAdapter {
    private DZFoodPhotoCheckActivity activity;
    private List<ExcePhoto> photoList;

    public DZFoodPhotoAdapter (DZFoodPhotoCheckActivity activity, List<ExcePhoto> photoList){
        this.activity = activity;
        this.photoList = photoList;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public Object getItem(int position) {
        return photoList.get(position);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dz_photo_list, null);
            viewHolder.ivPhoto = convertView.findViewById(R.id.iv_food_photo);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        String imgStr = photoList.get(position).getEXCE_FILENAME1();
        Log.e("------imgStr-----", "======" + imgStr);
        byte[] decode = Base64.decode(imgStr, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        viewHolder.ivPhoto.setImageBitmap(bitmap);

        return convertView;
    }
    private class ViewHolder {
        public ImageView ivPhoto;
    }
}
