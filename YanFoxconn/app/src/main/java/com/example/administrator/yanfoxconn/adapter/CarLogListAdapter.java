package com.example.administrator.yanfoxconn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.CarLogListActivity;
import com.example.administrator.yanfoxconn.activity.CarLogReturnActivity;
import com.example.administrator.yanfoxconn.bean.CarLogMessage;
import com.example.administrator.yanfoxconn.bean.CarLogReturnM;
import com.example.administrator.yanfoxconn.utils.StringUtils;

import java.util.List;

/**
 * 行車日誌 列表
 * Created by song on 2017/12/15.
 */

public class CarLogListAdapter extends BaseAdapter {

    private CarLogListActivity activity;
    private List<CarLogMessage> messageList;
    private CarLogReturnActivity returnActivity;
    private List<CarLogReturnM> returnMessageList;
    private boolean isReturn;

    public CarLogListAdapter(CarLogListActivity activity, List<CarLogMessage> messageList, boolean isReturn) {
        this.activity = activity;
        this.messageList = messageList;
        this.isReturn = isReturn;
    }

    public CarLogListAdapter(CarLogReturnActivity activity, List<CarLogReturnM> messageList, boolean isReturn) {
        this.returnActivity = activity;
        this.returnMessageList = messageList;
        this.isReturn = isReturn;
    }

    @Override
    public int getCount() {
        if (isReturn) {
            return returnMessageList.size();
        } else {
            return messageList.size();
        }
    }

    @Override
    public Object getItem(int i) {

        if (isReturn) {
            return  returnMessageList.get(i);
        } else {
            return messageList.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_see_item, null);
            viewHolder.tvStart = convertView.findViewById(R.id.tv_address);
            viewHolder.tvMid = convertView.findViewById(R.id.tv_time);
            viewHolder.tvEnd = convertView.findViewById(R.id.tv_description);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (isReturn) {
            viewHolder.tvStart.setText("日誌日期:"+ StringUtils.getSubstr(returnMessageList.get(position).getXc_date()," "));
            viewHolder.tvMid.setText("");
            viewHolder.tvEnd.setText(returnMessageList.get(position).getRec_content());
        } else {
            viewHolder.tvStart.setText(messageList.get(position).getStart_add());
            viewHolder.tvMid.setText(messageList.get(position).getMid_add());
            viewHolder.tvEnd.setText(messageList.get(position).getEnd_add());
        }
        return convertView;
    }

    private class ViewHolder {
        public TextView tvStart, tvMid, tvEnd;
    }
}
