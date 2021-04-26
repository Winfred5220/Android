package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.CPCAdapterMessage;
import com.example.administrator.yanfoxconn.bean.CPCMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 成品倉出貨 的 適配器
 * Created by S1007989 on 2021/04/10.
 */

public class CPCBodyListAdapter extends RecyclerView.Adapter<CPCBodyListAdapter.ViewHolder> {
    private List<CPCMessage> lists;
    private static HashMap<Integer, String> isConfirmOkMap = new HashMap<>();//存放箱數值的map
    private static HashMap<Integer, CPCAdapterMessage> adapterMessageMap = new HashMap<>();

    public static Context mContext;
    public boolean isChange;
    private int pcsNum=0;//總PCS數
    private int releaseCount=0;//已放行數量

    static class ViewHolder extends RecyclerView.ViewHolder{
        //序號 部位 檢查項目
        TextView tvListNo,tvAdd,tvSub,tvNo,tvAllNum,tvLastNum,tvSurplusNum,tvXiangNum,tvJing,tvMao,tvXiang,tvZhanban;
        EditText etGetNum;// 填寫信息
        Button btnConfirm;//確認按鈕

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvListNo = itemView.findViewById(R.id.tv_list_no);
//            tvAdd = itemView.findViewById(R.id.tv_add);
//            tvSub= itemView.findViewById(R.id.tv_sub);
            tvNo= itemView.findViewById(R.id.tv_no);
            tvAllNum = itemView.findViewById(R.id.tv_all_num);
            tvLastNum = itemView.findViewById(R.id.tv_last_num);
            tvSurplusNum = itemView.findViewById(R.id.tv_surplus_num);
            etGetNum = itemView.findViewById(R.id.et_get_num);
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
            tvXiangNum = itemView.findViewById(R.id.tv_xiang_num);
            tvJing = itemView.findViewById(R.id.tv_jing);
            tvMao = itemView.findViewById(R.id.tv_mao);
            tvXiang = itemView.findViewById(R.id.tv_xiang);
            tvZhanban = itemView.findViewById(R.id.tv_zb);
        }

    }

    public CPCBodyListAdapter(Context context, List<CPCMessage> lists){
        this.lists=lists;
        this.mContext = context;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_cpc_release_list,parent,false);
        ViewHolder holder = new ViewHolder(view);



        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int num=position+1;
        CPCMessage cpcBodyMessage = lists.get(position);
        CPCAdapterMessage adapterMessage = new CPCAdapterMessage();
        adapterMessageMap.put(position,adapterMessage);

        holder.tvListNo.setText(num+"");
        holder.tvNo.setText(cpcBodyMessage.getEx_lh());
        holder.tvLastNum.setText(cpcBodyMessage.getEx_release_count());
        holder.tvXiangNum.setText(cpcBodyMessage.getEx_carton_count());
        holder.tvJing.setText(cpcBodyMessage.getEx_nw());
        holder.tvMao.setText(cpcBodyMessage.getEx_gw());
        holder.tvXiang.setText(cpcBodyMessage.getEx_start_carton()+"-"+cpcBodyMessage.getEx_end_carton());
        holder.tvZhanban.setText(cpcBodyMessage.getEx_start_pallet()+"-"+cpcBodyMessage.getEx_end_pallet());

        adapterMessageMap.get(position).setNum("0");
        adapterMessageMap.get(position).setUnit("PCS");
        isConfirmOkMap.put(position,"N");

        if (cpcBodyMessage.getEx_count()!=null && !cpcBodyMessage.getEx_count().equals("")){
            pcsNum =(int)Double.parseDouble(cpcBodyMessage.getEx_count());
            adapterMessageMap.get(position).setPcsNum(pcsNum);
            holder.tvAllNum.setText(pcsNum+"");
            holder.tvSurplusNum.setText(pcsNum+"");
        }

        if (cpcBodyMessage.getEx_release_count()!=null && !cpcBodyMessage.getEx_release_count().equals("")){
            isChange = true;
            releaseCount = Integer.parseInt(cpcBodyMessage.getEx_release_count());
            adapterMessageMap.get(position).setReleaseCount(releaseCount);
            int temp = adapterMessageMap.get(position).getPcsNum()-adapterMessageMap.get(position).getReleaseCount();
            holder.tvSurplusNum.setText(""+temp);
            //holder.etGetNum.setText(temp+"");
            //adapterMessageMap.get(position).setNum(temp);
            if (temp==0){
                holder.btnConfirm.setText("確認OK");
                holder.btnConfirm.setEnabled(false);
                holder.etGetNum.setText("0");
                adapterMessageMap.get(position).setNum("0");
                holder.etGetNum.setEnabled(false);
                //holder.tvAdd.setEnabled(false);
                //holder.tvSub.setEnabled(false);
                isConfirmOkMap.put(position,"Y");
            }
            isChange = false;
        }
        //单击接口的回调
        if(onConfirmClickListener!=null){
            holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onConfirmClickListener.onConfirmclick(position);
                }
            });
        }
        //长按
        if(onitemLongClick!=null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onitemLongClick.lonitemclick(position);
                    return true;
                }
            });
        }
        //长按彈出菜單
        if(onItemClickListener!=null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onItemLongClick(holder.itemView,position);
                    return false;
                }
            });
        }

//        //点击减按钮的事件
//        holder.tvSub.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.etGetNum.setFocusable(false);
//                if (!holder.etGetNum.getText().toString().equals("")){
//                    int i = toInt(holder.etGetNum.getText().toString());
//                    if (i>0){
//                        holder.etGetNum.setText("" + (i - 1));
//                    }else {
//                        holder.etGetNum.setText("0");
//                    }
//                } else {
//                    holder.etGetNum.setText("0");
//                }
//            }});
//        //点击加按钮的事件
//        holder.tvAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.etGetNum.setFocusable(false);
//                if (!holder.etGetNum.getText().toString().equals("")){
//                    int i = toInt(holder.etGetNum.getText().toString());
//                    if(adapterMessageMap.get(position).getPcsNum()!=0){
//                        if (i>=0 && i<adapterMessageMap.get(position).getPcsNum()-adapterMessageMap.get(position).getReleaseCount()){
//                            holder.etGetNum.setText("" + (i + 1));
//                        }
//                    }
//                } else {
//                    holder.etGetNum.setText("0");
//                }
//            }});

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
        holder.etGetNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isChange){
                    if (!s.toString().equals("")){

                        if(adapterMessageMap.get(position).getPcsNum()!=0){
                            int temp = adapterMessageMap.get(position).getPcsNum()-adapterMessageMap.get(position).getReleaseCount();
                            if (toInt(s.toString()) > temp) {
                                //Log.e("-----position#-----", ""+ position +' '+ s.toString());
                                Toast.makeText(mContext, "序號"+num+ "數量過大，不能超過最大數量", Toast.LENGTH_SHORT).show();
                                holder.etGetNum.setText(temp+"");
                                adapterMessageMap.get(position).setNum(temp+"");
                            } else {
                                //Log.e("-----position-----", ""+ position +' '+ s.toString());
                                adapterMessageMap.get(position).setNum(s.toString());
                            }
                        }

                    } else {
                        adapterMessageMap.get(position).setNum("0");
                        Toast.makeText(mContext, "序號"+num+"請輸入放行数量", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public int getItemCount() {
        Log.e("--------","lists.size()==="+lists.size());
        return lists.size();
    }
    /** * String 转换int */
    public int toInt(String tostring) {
        return Integer.parseInt(tostring);
    }
    /** * String 转换double */
    public double toDouble(String tostring) {
        return Double.parseDouble(tostring);
    }
    //接口回调，定义点击事件
    private OnConfirmClickListener onConfirmClickListener;
    public interface OnConfirmClickListener{
        void onConfirmclick(int position);
    }
    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }
    //接口回调，定义长按事件
    private OnitemLongClick onitemLongClick;
    public interface OnitemLongClick{
        void lonitemclick(int position);
    }
    public void setOnitemLongClick(OnitemLongClick onitemLongClick) {
        this.onitemLongClick = onitemLongClick;
    }
    //接口回调，定义长按彈出菜單事件
    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onItemLongClick(View view , int pos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }



    public static HashMap<Integer, String> getIsConfirmOkMap() {
        return isConfirmOkMap;
    }

    public static void setIsConfirmOkMap(HashMap<Integer, String> isConfirmOkMap) {
        CPCBodyListAdapter.isConfirmOkMap = isConfirmOkMap;
    }

    public static HashMap<Integer, CPCAdapterMessage> getAdapterMessageMap() {
        return adapterMessageMap;
    }

    public static void setAdapterMessageMap(HashMap<Integer, CPCAdapterMessage> adapterMessageMap) {
        CPCBodyListAdapter.adapterMessageMap = adapterMessageMap;
    }
}
