package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.GTMainActivity;
import com.example.administrator.yanfoxconn.bean.GTMainBtn;
import com.example.administrator.yanfoxconn.widget.ImageBtnWithText;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @Description 營建工程管理 點檢按鈕
 * @Author song
 * @Date 2021/1/5 15:32
 */
public class GTMainBtnAdapter extends BaseAdapter {
    private GTMainActivity mContext;
    private List<GTMainBtn> gtMainBtns;

    private final ImageLoader imageLoader;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.ic_launcher)          // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.ic_launcher)  // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.ic_launcher)       // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)                          // 设置下载的图片是否缓存在SD卡中
            .bitmapConfig(Bitmap.Config.RGB_565)        // 设置图片的解码类型
            .build();                                   // 创建配置过得DisplayImageOption对象;

    public GTMainBtnAdapter(GTMainActivity activity, List<GTMainBtn> gtMainBtnList){
        this.gtMainBtns = gtMainBtnList;
        this.mContext = activity;
        // 初始化imageloader
        imageLoader = ImageLoader.getInstance();
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
        final ViewHolder viewHolder;
        if ( convertView ==null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gridview_igtv_item, null);
            viewHolder.ibtnGridView = convertView.findViewById(R.id.ibtv);
            viewHolder.tvName = convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

//        viewHolder.ibtnGridView.setText(gtMainBtns.get(position).getCname());
//        tvGridView.setTag(itemTypeList);
//        tvGridView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.color_009adb));
//
//        Drawable img = new BitmapDrawable(returnBitMap(gtMainBtns.get(position).getIconurl()));
//        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
//        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
//        tvGridView.setCompoundDrawables(null, img, null, null);
        Log.e("----------------","name=="+gtMainBtns.get(position).getCname());
        viewHolder.tvName.setText(gtMainBtns.get(position).getCname());
        if (gtMainBtns.get(position).getFlag().equals("N")){
            // 显示图片
            imageLoader.displayImage(gtMainBtns.get(position).getIconurl_dis(),viewHolder.ibtnGridView,options);}else{
        // 显示图片
        imageLoader.displayImage(gtMainBtns.get(position).getIconurl(),viewHolder.ibtnGridView,options);}
        viewHolder.tvName.setText(gtMainBtns.get(position).getCname());

        return convertView;
    }
    private class ViewHolder {

        public TextView tvName;
        public ImageView ibtnGridView;
    }
}
