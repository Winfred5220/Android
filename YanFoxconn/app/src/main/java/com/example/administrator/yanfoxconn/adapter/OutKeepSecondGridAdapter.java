package com.example.administrator.yanfoxconn.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.OutKeepSecondActivity;

import java.util.List;

import androidx.core.content.ContextCompat;

/**
 * 宿舍 棟的分類 adapter
 * Created by song on 2018/3/1.
 */

public class OutKeepSecondGridAdapter extends BaseAdapter {
    private List<String> rolesList;
    private List<String> name;
    private List<String> type;
    private List<Integer> icon;
    private OutKeepSecondActivity activity;


    private OnClickListener onClickListener;



    public OutKeepSecondGridAdapter(OutKeepSecondActivity activity, List<String>rolesList, List<String> name, List<String> type, List<Integer> icon){
        this.activity = activity;
        this.rolesList = rolesList;
        this.name = name;
        this.type = type;
        this.icon = icon;
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int i) {
        return name.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        if (null == convertView) {
            convertView = View.inflate(activity, R.layout.gridview_item, null);
        }
        TextView tvGridView = (TextView) convertView.findViewById(R.id.tv_gridview);
        tvGridView.setText(name.get(position));
//        tvGridView.setTag(itemTypeList);
//        tvGridView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.color_009adb));
        Drawable img = convertView.getResources().getDrawable(icon.get(position));
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
        tvGridView.setCompoundDrawables(null, img, null, null);

        for (int i = 0; i < rolesList.size(); i++) {
            if (rolesList.get(i).equals(type.get(position))) {
                tvGridView.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_eeeeee));
                tvGridView.setClickable(true);
                if (rolesList.get(i).equals("F0")) {
                    tvGridView.setTag("f");
                } else if (rolesList.get(i).equals("E0")) {
                    tvGridView.setTag("e");
                } else if(rolesList.get(i).equals("G0")){
                    tvGridView.setTag("g");
                }else if(rolesList.get(i).equals("V0")){
                    tvGridView.setTag("v");
                }else{
                    tvGridView.setTag("true");
                }
                break;
            } else {
                tvGridView.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_ffdedede));
                tvGridView.setClickable(false);
                tvGridView.setTag("false");
            }
        }

        tvGridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag().equals("true")) {

                    onClickListener.OnClickListenerTrue(position);
//                } else if (view.getTag().equals("f")) {//網站跳轉
//
//                    onClickListener.OnClickWebView(position);
//                }else if (view.getTag().equals("e")){//條碼掃描跳轉
//
//                    onClickListener.OnClickCarScan(position);
//                } else if(view.getTag().equals("g")){//行車日誌列表跳轉
//
//                    onClickListener.OnClickCarLogList(position);
//                }else if(view.getTag().equals("v")){//營建維保
//
//                    onClickListener.OnClickOutKeep(position);
                }else {

                    onClickListener.OnClickListenerFalse(position);
                }

            }
        });



        return convertView;
    }

    public interface OnClickListener {
        void OnClickListenerFalse(int position);

        void OnClickListenerTrue(int position);

//        void OnClickWebView(int position);
//
//        void OnClickCarScan(int position);
//
//        void OnClickCarLogList(int position);
//
//        void OnClickOutKeep(int position);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
