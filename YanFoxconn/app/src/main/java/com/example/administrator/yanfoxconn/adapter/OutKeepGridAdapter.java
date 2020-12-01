package com.example.administrator.yanfoxconn.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.OutKeepFirstActivity;
import com.example.administrator.yanfoxconn.activity.OutKeepSecondActivity;
import com.example.administrator.yanfoxconn.utils.ToastUtils;

import java.util.List;

import androidx.core.content.ContextCompat;

/**
 * 營建 維保分區的adapter
 * Created by song on 2018/3/1.
 */

public class OutKeepGridAdapter extends BaseAdapter {
    private List<String> group;
    private List<Integer> icon;
    private OutKeepFirstActivity activity;
    private List<String> type;
    private List<String> rolesList;
    private String firstType;


    public OutKeepGridAdapter(OutKeepFirstActivity activity, List<String> typeGroup, List<Integer> icon, List<String> type, List<String> rolesList, String firstType) {
        this.activity = activity;
        this.group = typeGroup;
        this.icon = icon;
        this.type = type;
        this.rolesList = rolesList;
        this.firstType = firstType;
    }

    @Override
    public int getCount() {
        return group.size();
    }

    @Override
    public Object getItem(int i) {
        return group.get(i);
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
        tvGridView.setText(group.get(position));
//        tvGridView.setTag(itemTypeList);
//        tvGridView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.color_009adb));
        Drawable img = convertView.getResources().getDrawable(icon.get(position));
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
        tvGridView.setCompoundDrawables(null, img, null, null);

        if (!firstType.equals("V0")) {
            for (int j = 0; j < rolesList.size(); j++) {
                if (rolesList.get(j).equals(type.get(position))) {

                    tvGridView.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                    tvGridView.setClickable(true);
                    Log.e("---------", type.get(position));
                    tvGridView.setTag("true");
                    break;
                } else {
                    tvGridView.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_eeeeee));
                    tvGridView.setClickable(false);
                    tvGridView.setTag("false");

                    Log.e("---------", type.get(position));
                }
            }
        }


        tvGridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstType.equals("V0")) {
                    Intent intent = new Intent(activity, OutKeepSecondActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("title", group.get(position));
                    intent.putExtra("type", type.get(position));
                    activity.startActivity(intent);
                } else if (firstType.equals("BU")&&view.getTag().equals("true")){

                    activity.getRouteList(type.get(position));
                }else if (view.getTag().equals("true")) {
                    activity.getDormitory(type.get(position), position);
                } else {
                    ToastUtils.showShort(activity, "沒有權限!");
                }


            }
        });

        return convertView;
    }


}
