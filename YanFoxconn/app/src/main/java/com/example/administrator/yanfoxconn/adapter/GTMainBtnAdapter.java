package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.GTMainActivity;
import com.example.administrator.yanfoxconn.bean.GTMainBtn;

import java.util.List;

/**
 * @Description 營建工程管理 點檢按鈕
 * @Author song
 * @Date 2021/1/5 15:32
 */
public class GTMainBtnAdapter extends BaseAdapter {
    private Context mContext;
    private List<GTMainBtn> gtMainBtns;

    public GTMainBtnAdapter(GTMainActivity activity, List<GTMainBtn> gtMainBtnList){
        this.gtMainBtns = gtMainBtnList;
        this.mContext = activity;
    }
    @Override
    public int getCount() {
        return gtMainBtns.size();
    }

    @Override
    public Object getItem(int i) {
        return gtMainBtns.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (null == convertView) {
            convertView = View.inflate(mContext, R.layout.gridview_item, null);
        }
        TextView tvGridView = (TextView) convertView.findViewById(R.id.tv_gridview);
//        tvGridView.setText(gtMainBtns.get(position).getName());
////        tvGridView.setTag(itemTypeList);
////        tvGridView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.color_009adb));
//        Drawable img = convertView.getResources().getDrawable(gtMainBtns.get(position).getFile());
//        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
//        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
//        tvGridView.setCompoundDrawables(null, img, null, null);
        return convertView;
    }
}
