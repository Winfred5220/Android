package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.ForkliftMessage;

import java.util.HashMap;
import java.util.List;

/**
 * 叉車巡檢列表 的 適配器
 * Created by wangqian on 2019/3/4.
 */

public class ForkliftCheckAdapter extends BaseAdapter {

    private static HashMap<Integer, Boolean> isSelected; //存放checkBox状态的map
    private List<ForkliftMessage> lists;
    public Context mContext;
    public boolean isChange;

    public ForkliftCheckAdapter(Context context, HashMap<Integer, Boolean> isSelected, List<ForkliftMessage> lists) {
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
        int num = position + 1;
        if (convertView == null) {
            holder = new ViewHolder();
            Log.e("-------------", "lists.get(position).getId()===="+lists.get(position).getId());
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_forklift_check, null,false);
            holder.tvNum = convertView.findViewById(R.id.tv_num);
            holder.tvItem1 = convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = convertView.findViewById(R.id.tv_item2);
            holder.rgIsNormal = convertView.findViewById(R.id.rg_is_normal);
            holder.rbTure = convertView.findViewById(R.id.rb_true);
            holder.rbFalse = convertView.findViewById(R.id.rb_false);
            holder.etAbnormal = convertView.findViewById(R.id.et_abnormal);
            holder.etMessage = convertView.findViewById(R.id.et_message);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // EditText etAbnormal点击监听
        holder.etAbnormal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                holder.etAbnormal.setFocusable(true);
                holder.etAbnormal.setFocusableInTouchMode(true);
                holder.etAbnormal.requestFocus();
            }
        });

        // EditText etAbnormal监听
        holder.etAbnormal.addTextChangedListener(new MyTextWatcher(holder) {
            @Override
            public void afterTextChanged(Editable s,ViewHolder holder) {
                int position = (Integer) holder.etAbnormal.getTag();
                lists.get(position).setAbnormal(holder.etAbnormal.getText().toString());
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


        // EditText etMessage点击监听
        holder.etMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                holder.etMessage.setFocusable(true);
                holder.etMessage.setFocusableInTouchMode(true);
                holder.etMessage.requestFocus();
            }
        });

        // EditText etMessage监听
        holder.etMessage.addTextChangedListener(new MyTextWatcher(holder) {
            @Override
            public void afterTextChanged(Editable s,ViewHolder holder) {
                int position = (Integer) holder.etMessage.getTag();
                lists.get(position).setMessage(holder.etMessage.getText().toString());
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

        /******一下为RadioButton相关设置监听********/
        holder.rbTure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!isChange) {
                    if (isSelected.get(position)) {
                        holder.etAbnormal.setVisibility(View.VISIBLE);
                        isSelected.put(position, false);
                        setIsSelected(isSelected);
                    } else {
                        holder.etAbnormal.setVisibility(View.GONE);
                        holder.etAbnormal.setText("");
                        isSelected.put(position, true);
                        setIsSelected(isSelected);

                    }
                    Log.e("position=========", position + "");
                }
                }
            });

            if (lists.get(position).getFlag().equals("A")) {
                holder.rgIsNormal.setVisibility(View.GONE);
                holder.etMessage.setVisibility(View.VISIBLE);
            }else {
                holder.rgIsNormal.setVisibility(View.VISIBLE);
                holder.etMessage.setVisibility(View.GONE);
            }
            isChange = true;
            holder.tvNum.setText(""+num);
            // 根据isSelected来设置checkbox的选中状况
            holder.rbTure.setChecked(getIsSelected().get(position));
            holder.tvItem1.setText(lists.get(position).getItem1());
            holder.tvItem2.setText(lists.get(position).getItem2());
            holder.etAbnormal.setTag(position);
            holder.etMessage.setTag(position);
            isChange = false;
            return convertView;
    }


    private class ViewHolder {
        public TextView tvNum,tvItem1,tvItem2;//序號 部位 檢查項目
        public RadioButton rbTure,rbFalse;//單選
        public EditText etAbnormal,etMessage;//異常信息 填寫信息
        public RadioGroup rgIsNormal;
    }

//    /** * String 转换int */
//    public int toInt(String tostring) {
//        return Integer.parseInt(tostring);
//    }
//
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
        ForkliftCheckAdapter.isSelected = isSelected;
    }

}
