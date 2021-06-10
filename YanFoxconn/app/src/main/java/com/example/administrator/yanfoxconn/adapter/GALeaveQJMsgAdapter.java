package com.example.administrator.yanfoxconn.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.GALeaveQHUpActivity;
import com.example.administrator.yanfoxconn.activity.GoodsGeneralActivity;
import com.example.administrator.yanfoxconn.bean.GAWork;
import com.example.administrator.yanfoxconn.bean.GoodsMessage;

import java.util.List;

/**
 * @Description 临时工请假列表
 * @Author song
 * @Date 6/8/21 11:17 AM
 */
public class GALeaveQJMsgAdapter extends BaseAdapter {

    private List<GAWork> gaWorks;
    private GALeaveQHUpActivity activity;
    private OnClickListenerLeftOrRight onClickListenerLeftOrRight;
    private String from="";
    public GALeaveQJMsgAdapter(GALeaveQHUpActivity activity, List<GAWork> gaWorks,String from) {
        this.activity = activity;
        this.gaWorks = gaWorks;
        this.from = from;
    }
    @Override
    public int getCount() {
        return gaWorks.size();
    }

    @Override
    public Object getItem(int position) {
        return gaWorks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_zw_qj_form, null);
            viewHolder.tvId = convertView.findViewById(R.id.tv_id);
            viewHolder.tvName = convertView.findViewById(R.id.tv_name);
            viewHolder.tvGName = convertView.findViewById(R.id.tv_gname);
            viewHolder.tvGPost = convertView.findViewById(R.id.tv_gpost);
            viewHolder.tvStartDate = convertView.findViewById(R.id.tv_start_date);
            viewHolder.tvEndDate = convertView.findViewById(R.id.tv_end_date);
            viewHolder.tvTime = convertView.findViewById(R.id.tv_time);
            viewHolder.tvClass = convertView.findViewById(R.id.tv_class);
            viewHolder.etReason = convertView.findViewById(R.id.tv_reason);
            viewHolder.tvLeft = convertView.findViewById(R.id.tv_left);
            viewHolder.tvRight = convertView.findViewById(R.id.tv_right);
            viewHolder.llTvBtn = convertView.findViewById(R.id.ll_tv_btn);
            viewHolder.tvStatue = convertView.findViewById(R.id.tv_statue);
            viewHolder.tvZGName = convertView.findViewById(R.id.tv_zg_name);
            viewHolder.tvZGDate = convertView.findViewById(R.id.tv_zg_date);
            viewHolder.tvZGReason = convertView.findViewById(R.id.tv_zg_reason);
            viewHolder.rlStatue = convertView.findViewById(R.id.rl_statue);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.tvId.setText(gaWorks.get(position).getP_empId());
        viewHolder.tvName.setText(gaWorks.get(position).getP_empName());
        viewHolder.tvGName.setText(gaWorks.get(position).getG_name());
        viewHolder.tvGPost.setText(gaWorks.get(position).getG_post());
        viewHolder.tvStartDate.setText(gaWorks.get(position).getQj_start_date());
        viewHolder.tvEndDate.setText(gaWorks.get(position).getQj_end_date());
        viewHolder.tvTime.setText(gaWorks.get(position).getQj_time());
        String classType="";
        if (gaWorks.get(0).getC_type().equals("D")){
            classType="白班";
        }else{
            classType="夜班";
        }
        if (gaWorks.get(position).getC_up_start().equals(gaWorks.get(position).getC_down_start())){
            viewHolder.tvClass.setText(classType+"\u3000\u3000"+gaWorks.get(0).getC_up_start()+"-"+gaWorks.get(0).getC_up_end());
        }else{
            viewHolder.tvClass.setText(classType+"\u3000\u3000"+gaWorks.get(0).getC_up_start()+"-"+gaWorks.get(0).getC_up_end()+"\u3000\u3000"+gaWorks.get(0).getC_down_start()+"-"+gaWorks.get(0).getC_down_end());

        }
        viewHolder.etReason.setText(gaWorks.get(position).getQj_reason());

        viewHolder.tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListenerLeftOrRight != null) {
                    onClickListenerLeftOrRight.OnClickListenerLeft(position);
                }
            }
        });
        viewHolder.tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListenerLeftOrRight != null) {
                    onClickListenerLeftOrRight.OnClickListenerRight(position);
                }
            }
        });
        if (from.equals("emp")){
            viewHolder.rlStatue.setVisibility(View.VISIBLE);
            viewHolder.llTvBtn.setVisibility(View.GONE);
            if (gaWorks.get(position).getQj_statue().equals("Y")){
                viewHolder.llTvBtn.setVisibility(View.GONE);
                viewHolder.tvStatue.setText("已签核");
                viewHolder.tvStatue.setBackgroundColor(R.color.color_42D42B);
                viewHolder.tvZGName.setText(gaWorks.get(position).getZg_name());
                viewHolder.tvZGDate.setText(gaWorks.get(position).getQh_date());
            }else if (gaWorks.get(position).getQj_statue().equals("Y")){
                if (gaWorks.get(position).getTj_statue().equals("Y")){
                    viewHolder.llTvBtn.setVisibility(View.GONE);
                    viewHolder.tvStatue.setText("已退件");
                    viewHolder.tvStatue.setBackgroundColor(R.color.color_d73d19);
                    viewHolder.tvZGName.setText(gaWorks.get(position).getZg_name());
                    viewHolder.tvZGDate.setText(gaWorks.get(position).getQh_date());
                    viewHolder.tvZGReason.setText(gaWorks.get(position).getTj_reason());
                }
            }else {
                viewHolder.tvStatue.setText("待处理");
                viewHolder.tvStatue.setBackgroundColor(R.color.color_00CCFF);
                viewHolder.tvZGDate.setVisibility(View.GONE);
                viewHolder.tvZGReason.setVisibility(View.GONE);
                viewHolder.tvZGName.setVisibility(View.GONE);
            }
        }else{

            viewHolder.rlStatue.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class ViewHolder {
        public TextView tvId,tvName, tvGName, tvGPost,tvStartDate,tvEndDate, tvTime, tvClass,etReason;
        public TextView tvLeft,tvRight;
        public LinearLayout llTvBtn;
        public TextView tvStatue,tvZGName,tvZGDate,tvZGReason;
        public RelativeLayout rlStatue;
    }


    /**
     * 重写 避免滑动过程界面混乱
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public interface OnClickListenerLeftOrRight {
        void OnClickListenerLeft(int position);
        void OnClickListenerRight(int position);
    }

    public void setOnClickListenerLeftOrRight(OnClickListenerLeftOrRight onClickListenerLeftOrRight) {
        this.onClickListenerLeftOrRight = onClickListenerLeftOrRight;
    }
}
