package com.example.administrator.yanfoxconn.adapter;

import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.UpAbnormalActivity;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.utils.BaseActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by song on 2017/11/29.
 */

public class PhotoGridViewAdapter extends BaseAdapter {
    private ArrayList<String> listUrls;
private Constants constants;
    public PhotoGridViewAdapter(Constants constants,ArrayList<String> listUrls) {
        this.listUrls = listUrls;
        this.constants=constants;
    }

    @Override
    public int getCount() {
        if (listUrls.size() == 9) {
            return 9;
        } else {
            return listUrls.size() + 1;
        }

    }

    @Override
    public String getItem(int position) {
        return listUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
//            convertView = getLayoutInflater().inflate(R.layout.item_image, null);
            imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(imageView);
            // 重置ImageView宽高
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(columnWidth, columnWidth);
//                imageView.setLayoutParams(params);
        } else {
            imageView = (ImageView) convertView.getTag();
        }
        if (position == listUrls.size()) {
//            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_addpic_unfocused));
            if (position == 9) {
                imageView.setVisibility(View.GONE);
            }
        } else {
//            Glide.with(UpAbnormalActivity.this)
//                    .load(new File(getItem(position)))
//                    .placeholder(R.mipmap.default_error)
//                    .error(R.mipmap.default_error)
//                    .centerCrop()
//                    .crossFade()
//                    .into(imageView);
        }
        return convertView;
    }
}
