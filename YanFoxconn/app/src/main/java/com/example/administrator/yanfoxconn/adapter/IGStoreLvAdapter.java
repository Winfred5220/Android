package com.example.administrator.yanfoxconn.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.DZFoodAbListActivty;
import com.example.administrator.yanfoxconn.activity.IGMainActivity;
import com.example.administrator.yanfoxconn.activity.TimeDatePickerDialog;
import com.example.administrator.yanfoxconn.bean.BodyONE;
import com.example.administrator.yanfoxconn.bean.BodyTWO;
import com.example.administrator.yanfoxconn.bean.DZFoodAbList;
import com.example.administrator.yanfoxconn.bean.SelectModel;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiCheckMsg;
import com.example.administrator.yanfoxconn.intent.PhotoPickerIntent;
import com.example.administrator.yanfoxconn.intent.PhotoPreviewIntent;
import com.example.administrator.yanfoxconn.widget.MyGridView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.core.app.ActivityCompat.startActivityForResult;

/**
 * Created by Song
 * on 2021/4/1
 * Description：宿舍寄存 物品和儲位信息
 */
public class IGStoreLvAdapter extends RecyclerView.Adapter<IGStoreLvAdapter.ViewHolder>{
    private IGMainActivity igMainActivity;
    private List<BodyONE> abList;
    private List<String> deposit;
    private String from;
    private int count=0;
    public IGStoreLvAdapter(IGMainActivity activity, List<BodyONE> messageList, String from){
        this.igMainActivity = activity;
        this.abList = messageList;
        this.from = from;
    }
    public IGStoreLvAdapter(IGMainActivity activity, List<BodyONE> messageList, String from,List<String> deposit){
        this.igMainActivity = activity;
        this.abList = messageList;
        this.from = from;
        this.deposit = deposit;
    }




    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return abList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_ig_store_lv,parent,false);
        ViewHolder holder = new ViewHolder(view);


        return holder;
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
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvType.setText(abList.get(position).getS_DEPOSIT_NAME());
        holder.tvNum.setText(abList.get(position).getS_DEPOSIT_COUNT());

        Log.e("-----getS_DEPOSIT_COUNT-----",abList.get(position).getS_DEPOSIT_NAME()+"");
        if (from.equals("deposit")){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < Integer.parseInt(abList.get(position).getS_DEPOSIT_COUNT()); i++) {

                Log.e("----------",abList.get(position).getS_DEPOSIT_COUNT()+"");
                Log.e("----------",count+"");
                Log.e("----------",deposit.get(count));
                TextView textView = new TextView(igMainActivity);
                textView.setTextSize(12);
                //textView.setBackgroundResource(R.drawable.label_style); //设置背景
                textView.setText(deposit.get(count));
                textView.setLayoutParams(layoutParams);
                count++;
                holder.llList.addView(textView);
            }
        }else {
            if (abList.get(position).getBody2().size() == 0) {

            } else {
                List<BodyTWO> bodyTWOS = new ArrayList<>();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                for (int i = 0; i < abList.get(position).getBody2().size(); i++) {
                    TextView textView = new TextView(igMainActivity);
                    textView.setTextSize(12);
                    //textView.setBackgroundResource(R.drawable.label_style); //设置背景
                    textView.setText(abList.get(position).getBody2().get(i) + "");
                    textView.setLayoutParams(layoutParams);

                    holder.llList.addView(textView);
                }
            }
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvType,tvNum;
        public LinearLayout llList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
          tvType= itemView.findViewById(R.id.tv_type);
            tvNum = itemView.findViewById(R.id.tv_num);
            llList = itemView.findViewById(R.id.ll_store);

        }

    }
}
