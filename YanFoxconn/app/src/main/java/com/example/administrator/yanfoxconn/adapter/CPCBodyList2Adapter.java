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
import com.example.administrator.yanfoxconn.bean.CPCMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 成品倉出貨 的 適配器
 * Created by S1007989 on 2021/04/10.
 */

public class CPCBodyList2Adapter extends RecyclerView.Adapter<CPCBodyList2Adapter.ViewHolder> {
    private List<CPCMessage> lists;
    private static HashMap<Integer, String> numMap = new HashMap<>();//存放放行數量值的map
    private static HashMap<Integer, String> unitMap = new HashMap<>();//存放放行單位值的map
    private static HashMap<Integer, String> zbNumMap = new HashMap<>();//存放棧板數量值的map
    private static HashMap<Integer, String> xNumMap = new HashMap<>();//存放箱數值的map
    private static HashMap<Integer, String> isConfirmOkMap = new HashMap<>();//存放箱數值的map
    private List<String> selectData = new ArrayList<>();
    public static Context mContext;
    public boolean isChange;
    private int zbNum=0,xNum=0;//棧板數和箱數
    private int releaseCount=0;//已放行數量
    private String releaseUnit="";//已放行單位

    static class ViewHolder extends RecyclerView.ViewHolder{
        //序號 部位 檢查項目
        TextView tvListNo,tvAdd,tvSub,tvNo,tvAllNum,tvLastNum,tvXiangNum,tvJing,tvMao,tvXiang,tvZhanban;
        EditText etGetNum;// 填寫信息
        Spinner spSelect;//下拉選擇
        Button btnconfirm;//確認按鈕

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvListNo = itemView.findViewById(R.id.tv_list_no);
            tvAdd = itemView.findViewById(R.id.tv_add);
            tvSub= itemView.findViewById(R.id.tv_sub);
            tvNo= itemView.findViewById(R.id.tv_no);
            tvAllNum = itemView.findViewById(R.id.tv_all_num);
            tvLastNum = itemView.findViewById(R.id.tv_last_num);
            etGetNum = itemView.findViewById(R.id.et_get_num);
            spSelect = itemView.findViewById(R.id.sp_unit);
            btnconfirm = itemView.findViewById(R.id.btn_confirm);
            tvXiangNum = itemView.findViewById(R.id.tv_xiang_num);
            tvJing = itemView.findViewById(R.id.tv_jing);
            tvMao = itemView.findViewById(R.id.tv_mao);
            tvXiang = itemView.findViewById(R.id.tv_xiang);
            tvZhanban = itemView.findViewById(R.id.tv_zb);
        }

    }
    //初始化數據
    private void init(){
        selectData.add("棧板");
        selectData.add("箱");
        selectData.add("PCS");
        selectData.add("KPCS");
    }
    public CPCBodyList2Adapter(Context context, List<CPCMessage> lists){
        this.lists=lists;
        this.mContext = context;
        init();
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

        holder.tvListNo.setText(num+"");
        holder.tvNo.setText(cpcBodyMessage.getEx_lh());
        holder.tvAllNum.setText(cpcBodyMessage.getEx_count());
        holder.tvLastNum.setText(cpcBodyMessage.getEx_release_count()+"/"+cpcBodyMessage.getEx_release_unit());
        holder.tvXiangNum.setText(cpcBodyMessage.getEx_carton_count());
        holder.tvJing.setText(cpcBodyMessage.getEx_nw());
        holder.tvMao.setText(cpcBodyMessage.getEx_gw());
        holder.tvXiang.setText(cpcBodyMessage.getEx_start_carton()+"-"+cpcBodyMessage.getEx_end_carton());
        holder.tvZhanban.setText(cpcBodyMessage.getEx_start_pallet()+"-"+cpcBodyMessage.getEx_end_pallet());
        isChange = true;
        holder.etGetNum.setText("1");
        isChange = false;
        unitMap.put(position,"棧板");
        numMap.put(position,"1");
        isConfirmOkMap.put(position,"N");
        if (cpcBodyMessage.getEx_end_pallet()!=null && !cpcBodyMessage.getEx_end_pallet().equals("")&&cpcBodyMessage.getEx_start_pallet()!=null && !cpcBodyMessage.getEx_start_pallet().equals("")){
            zbNum =Integer.parseInt(cpcBodyMessage.getEx_end_pallet())-Integer.parseInt(cpcBodyMessage.getEx_start_pallet())+1;
        }
        if (cpcBodyMessage.getEx_end_carton()!=null && !cpcBodyMessage.getEx_end_carton().equals("")&&cpcBodyMessage.getEx_start_carton()!=null && !cpcBodyMessage.getEx_start_carton().equals("")){
            xNum =Integer.parseInt(cpcBodyMessage.getEx_end_carton())-Integer.parseInt(cpcBodyMessage.getEx_start_carton())+1;
        }
        if (cpcBodyMessage.getEx_release_unit()!=null && !cpcBodyMessage.getEx_release_unit().equals("")){
            releaseUnit = cpcBodyMessage.getEx_release_unit();
            selectData.clear();
            selectData.add(releaseUnit);
            unitMap.put(position,releaseUnit);
        }
        if (cpcBodyMessage.getEx_release_count()!=null && !cpcBodyMessage.getEx_release_count().equals("")){
            releaseCount = Integer.parseInt(cpcBodyMessage.getEx_release_count());
            isChange = true;
            if (releaseUnit.equals("棧板")){
                holder.etGetNum.setText((zbNum-releaseCount)+"");
                numMap.put(position,(zbNum-releaseCount)+"");
                if (zbNum-releaseCount==0){
                    holder.btnconfirm.setText("確認OK");
                    holder.btnconfirm.setEnabled(false);
                    holder.etGetNum.setEnabled(false);
                    holder.tvAdd.setEnabled(false);
                    holder.tvSub.setEnabled(false);
                    holder.spSelect.setEnabled(false);
                    isConfirmOkMap.put(position,"Y");
                }
            }else if (releaseUnit.equals("箱")){
                holder.etGetNum.setText((xNum-releaseCount)+"");
                numMap.put(position,(xNum-releaseCount)+"");
                if (xNum-releaseCount==0){
                    holder.btnconfirm.setText("確認OK");
                    holder.btnconfirm.setEnabled(false);
                    holder.etGetNum.setEnabled(false);
                    holder.tvAdd.setEnabled(false);
                    holder.tvSub.setEnabled(false);
                    holder.spSelect.setEnabled(false);
                    isConfirmOkMap.put(position,"Y");
                }
            }else{

            }
            isChange = false;
        }
        //单击接口的回调
        if(onConfirmClickListener!=null){
            holder.btnconfirm.setOnClickListener(new View.OnClickListener() {
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
       //下拉列表選擇
       holder.spSelect.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, selectData));
       holder.spSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
               unitMap.put(position,selectData.get(pos));
               holder.etGetNum.setText(numMap.get(position));
           }
           @Override
           public void onNothingSelected(AdapterView<?> parent) {
           }
       });

        //点击减按钮的事件
        holder.tvSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.etGetNum.setFocusable(false);
                if (!holder.etGetNum.getText().toString().equals("")){
                    int i = toInt(holder.etGetNum.getText().toString());
                    if (i>0){
                        holder.etGetNum.setText("" + (i - 1));
                    }else {
                        holder.etGetNum.setText("0");
                    }
                } else {
                    holder.etGetNum.setText("0");
                }
            }});
        //点击加按钮的事件
        holder.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.etGetNum.setFocusable(false);
                if (!holder.etGetNum.getText().toString().equals("")){
                    int i = toInt(holder.etGetNum.getText().toString());
                    if (zbNum!=0 && unitMap.get(position).equals("棧板")){
                        if (i>=0&&i<zbNum-releaseCount){
                            holder.etGetNum.setText("" + (i + 1));
                        }
                    }else if (xNum!=0 && unitMap.get(position).equals("箱")){
                        if (i>=0&&i<xNum-releaseCount){
                            holder.etGetNum.setText("" + (i + 1));
                        }
                    }else {
                        if (i>=0&&i<toDouble(lists.get(position).getEx_count())-releaseCount){
                            holder.etGetNum.setText("" + (i + 1));
                        }
                    }
                } else {
                    holder.etGetNum.setText("0");
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
        holder.etGetNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isChange){
                    if (!s.toString().equals("")){

                        if (zbNum!=0 && unitMap.get(position).equals("棧板")) {
                            if (toInt(s.toString()) > zbNum-releaseCount) {
                                //Log.e("-----position#-----", ""+ position +' '+ s.toString());
                                Toast.makeText(mContext, "序號"+num+ "數量過大，不能超過最大棧板數", Toast.LENGTH_SHORT).show();
                                holder.etGetNum.setTextColor(Color.RED);
                                numMap.put(position,"0");
                            } else {
                                holder.etGetNum.setTextColor(Color.BLACK);
                                //Log.e("-----position-----", ""+ position +' '+ s.toString());
                                numMap.put(position,s.toString());
                            }
                        }else if (xNum!=0 && unitMap.get(position).equals("箱")){

                            if (toInt(s.toString()) > xNum-releaseCount) {
                                //Log.e("-----position#-----", ""+ position +' '+ s.toString());
                                Toast.makeText(mContext, "序號"+ num + "數量過大，不能超過最大箱數", Toast.LENGTH_SHORT).show();
                                holder.etGetNum.setTextColor(Color.RED);
                                numMap.put(position,"0");
                            } else {
                                holder.etGetNum.setTextColor(Color.BLACK);
                                //Log.e("-----position-----", ""+ position +' '+ s.toString());
                                numMap.put(position,s.toString());
                            }
                        }else{
                            if (toInt(s.toString()) > toDouble(lists.get(position).getEx_count())-releaseCount) {
                                //Log.e("-----position#-----", ""+ position +' '+ s.toString());
                                Toast.makeText(mContext, "序號"+num+ "數量過大，不能超過最大數量", Toast.LENGTH_SHORT).show();
                                holder.etGetNum.setTextColor(Color.RED);
                                numMap.put(position,"0");
                            } else {
                                holder.etGetNum.setTextColor(Color.BLACK);
                                //Log.e("-----position-----", ""+ position +' '+ s.toString());
                                numMap.put(position,s.toString());
                            }
                        }

                    } else {
                        numMap.put(position,"0");
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

    public static HashMap<Integer, String> getNumMap() {
        return numMap;
    }

    public static void setNumMap(HashMap<Integer, String> numMap) {
        CPCBodyList2Adapter.numMap = numMap;
    }

    public static HashMap<Integer, String> getUnitMap() {
        return unitMap;
    }

    public static void setUnitMap(HashMap<Integer, String> unitMap) {
        CPCBodyList2Adapter.unitMap = unitMap;
    }

    public static HashMap<Integer, String> getIsConfirmOkMap() {
        return isConfirmOkMap;
    }

    public static void setIsConfirmOkMap(HashMap<Integer, String> isConfirmOkMap) {
        CPCBodyList2Adapter.isConfirmOkMap = isConfirmOkMap;
    }
}
