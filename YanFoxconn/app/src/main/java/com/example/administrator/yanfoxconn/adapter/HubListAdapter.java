package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.HubGoodsActivity;
import com.example.administrator.yanfoxconn.bean.HubList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.administrator.yanfoxconn.activity.PhotoPickerActivity.TAG;

/**
 * 普通物品列表 的 適配器
 * Created by wangqian on 2018/12/22.
 */

public class HubListAdapter extends BaseAdapter {

    private static HashMap<Integer, Boolean> isSelected; //存放checkBox状态的map
    private List<HubList> lists;
    public Context mContext;
    public boolean isChange;

    public HubListAdapter(Context context, HashMap<Integer, Boolean> isSelected,  List<HubList> lists) {
        this.isSelected = isSelected;
        this.lists = lists;
        this.mContext = context;
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < lists.size(); i++) {
            isSelected.put(i, true);
        }
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
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        int num = position +1;

        if (convertView == null) {
            holder = new ViewHolder();
            Log.e("-------------", "lists.get(position).getMATERIAL_NAME()===="+lists.get(position).getMATERIAL_NAME());
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_hub_list, null,false);
            holder.ckChose = convertView.findViewById(R.id.ck_chose);
            holder.tvAdd = convertView.findViewById(R.id.tv_add);
            holder.tvSub = convertView.findViewById(R.id.tv_sub);
            holder.tvNo = convertView.findViewById(R.id.tv_list_no);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvModel = convertView.findViewById(R.id.tv_model);
            holder.tvAllNum = convertView.findViewById(R.id.tv_all_num);
            holder.etGetNum = convertView.findViewById(R.id.et_get_num);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //点击减按钮的事件
        holder.tvSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.etGetNum.setFocusable(false);
                if (!holder.etGetNum.getText().toString().equals("")){
                    int i = toInt(holder.etGetNum.getText().toString());
                    if (i>0){
                        holder.etGetNum.setText("" + (i - 1));
                        lists.get(position).setAPPLY_COUNT("" + (i - 1));
                    }else {
                        holder.etGetNum.setText("0");
                        lists.get(position).setAPPLY_COUNT("0");
                    }} else {
                    holder.etGetNum.setText("0");
                    lists.get(position).setAPPLY_COUNT("0");
                }
            }});
        //点击加按钮的事件
        holder.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.etGetNum.setFocusable(false);
                if (!holder.etGetNum.getText().toString().equals("")){
                    int i = toInt(holder.etGetNum.getText().toString());
                    if (i>=0&&i<toInt(lists.get(position).getSY())){
                        holder.etGetNum.setText("" + (i + 1));
                        lists.get(position).setAPPLY_COUNT("" + (i + 1));
                    }} else {
                    holder.etGetNum.setText("0");
                    lists.get(position).setAPPLY_COUNT("0");
                }
            }});
        // EditText点击监听
        holder.etGetNum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                holder.etGetNum.setFocusable(true);
                holder.etGetNum.setFocusableInTouchMode(true);
                holder.etGetNum.requestFocus();
            }
        });
        // EditText监听
        holder.etGetNum.addTextChangedListener(new MyTextWatcher(holder) {
            @Override
            public void afterTextChanged(Editable s,ViewHolder holder) {
            if (!isChange){
                int position = (Integer) holder.etGetNum.getTag();
                if (!s.toString().equals("")){
                    if (toInt(holder.etGetNum.getText().toString())>toInt(lists.get(position).getSY())) {
                        Log.e("-----position#-----", ""+ position +' '+ s.toString());
                        Toast.makeText(mContext, position + 1 +"數量過大，不能超過剩餘數量", Toast.LENGTH_SHORT).show();
                        holder.etGetNum.setTextColor(Color.RED);
                    } else {
                        holder.etGetNum.setTextColor(Color.BLACK);
                        lists.get(position).setAPPLY_COUNT(holder.etGetNum.getText().toString());
                        Log.e("-----position-----", ""+ position +' '+ s.toString());
                    }
                } else {
                    Toast.makeText(mContext, position + 1 +"請輸入", Toast.LENGTH_SHORT).show();
                }
            }
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
        });
        /******一下为CheckBox相关设置监听********/
        holder.ckChose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isSelected.get(position)) {
                    isSelected.put(position, false);
                    setIsSelected(isSelected);
                } else {
                    isSelected.put(position, true);
                    setIsSelected(isSelected);
                }
                Log.e("position=========", position + "");
            }
        });
        // 根据isSelected来设置checkbox的选中状况
        holder.ckChose.setChecked(getIsSelected().get(position));

            isChange = true;
            holder.tvNo.setText(num + "");
            holder.tvName.setText(lists.get(position).getMATERIAL_NAME());
            holder.tvModel.setText(lists.get(position).getMATERIAL_SPEC());
            holder.tvAllNum.setText(lists.get(position).getSY());
            holder.etGetNum.setText(lists.get(position).getSY());
            holder.etGetNum.setTag(position);
            isChange = false;

            return convertView;
    }

    /** * String 转换int */
    public int toInt(String tostring) {
        return Integer.parseInt(tostring);
    }

    private class ViewHolder {
        public TextView tvAdd,tvSub,tvNo,tvName, tvModel, tvAllNum ;//編號,名稱,型號,總數量
        public CheckBox ckChose;//複選框
        public EditText etGetNum;//取走數量
    }


    private abstract class MyTextWatcher implements TextWatcher{
        private ViewHolder mHolder;

        public MyTextWatcher(ViewHolder holder) {
            this.mHolder=holder;
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
        }
        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            afterTextChanged(s, mHolder);
        }
        public abstract void afterTextChanged(Editable s,ViewHolder holder);
    }



    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        HubListAdapter.isSelected = isSelected;
    }


}
