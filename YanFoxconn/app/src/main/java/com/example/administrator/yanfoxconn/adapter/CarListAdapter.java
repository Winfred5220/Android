package com.example.administrator.yanfoxconn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.CarListActivity;
import com.example.administrator.yanfoxconn.activity.CyCarListActivity;
import com.example.administrator.yanfoxconn.activity.CyDelListActivity;
import com.example.administrator.yanfoxconn.bean.CarListMessage;
import com.example.administrator.yanfoxconn.bean.CyCarMessage;

import java.util.List;

import static com.example.administrator.yanfoxconn.R.id.listMode;
import static com.example.administrator.yanfoxconn.R.id.tv_state;


/**
 * 碼頭貨物放行 車輛列表
 * 兼 物流消殺cy 車輛列表
 * Created by song on 2018/5/25.
 */

public class CarListAdapter extends BaseAdapter{

    private CarListActivity activity;
    private List<CarListMessage> listMessages;
    private String status;//狀態
    private String role = "";//角色
    private CyCarListActivity cyActivity;//物流消殺車輛界面
    private CyDelListActivity cyDelListActivity;//物流已消殺車輛界面
    private List<CyCarMessage> cyCarMessages;

    private OnClickListenerGoOrAdd onClickListenerGoOrAdd;

    public CarListAdapter(CarListActivity activity, List<CarListMessage> listMessages){
        this.activity = activity;
        this.listMessages = listMessages;
    }

    public CarListAdapter(Context activity, List<CyCarMessage> cyCarMessages, String role) {
        if (role.equals("cy")) {
            this.cyActivity = (CyCarListActivity) activity;
        } else {
            this.cyDelListActivity =(CyDelListActivity) activity;
        }
        this.cyCarMessages = cyCarMessages;
        this.role = role;
    }

    @Override
    public int getCount() {
        if (role.equals("")) {
            return listMessages.size();
        } else {
            return cyCarMessages.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (role.equals("")) {
            return listMessages.get(position);
        } else {
            return cyCarMessages.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            Log.e("===", "ss");
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_carlist_item, null);
            holder.carId = (TextView) convertView.findViewById(R.id.tv_car_id);
            holder.dock = (TextView) convertView.findViewById(R.id.tv_dock);
            holder.container = (TextView) convertView.findViewById(R.id.tv_container);
            holder.state = (TextView) convertView.findViewById(R.id.tv_state);
            holder.tvGo = convertView.findViewById(R.id.tv_let_go);//放行
            holder.tvAdd = convertView.findViewById(R.id.tv_up_photo);//上傳
holder.time = convertView.findViewById(R.id.tv_time);//時間
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (role.equals("")) {
        holder.carId.setText(listMessages.get(position).getTruckno());
        holder.dock.setText(listMessages.get(position).getBuildingno());
        holder.container.setText(listMessages.get(position).getContainer());
        status = listMessages.get(position).getStatus();

        if (status.equals("1")) {
            holder.state.setText("準備裝貨");
            int color = activity.getResources().getColor(R.color.color_009adb);
            holder.state.setTextColor(color);
        }else if (status.equals("2")){
            holder.state.setText("正在裝貨");
            int color = activity.getResources().getColor(R.color.main_color);
            holder.state.setTextColor(color);
        }else if (status.equals("3")){
            holder.state.setText("裝貨完畢");
            int color = activity.getResources().getColor(R.color.color_fee123);
            holder.state.setTextColor(color);
        }else if (status.equals("4")){
            holder.state.setText("放行單列印完畢");
            int color = activity.getResources().getColor(R.color.viewfinder_mask);
            holder.state.setTextColor(color);
        }

        holder.tvGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListenerGoOrAdd != null) {
                    onClickListenerGoOrAdd.OnClickListenerGo(position);
                }
            }
        });
        holder.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListenerGoOrAdd != null) {
                    onClickListenerGoOrAdd.OnClickListenerAdd(position);
                }
            }
        });
        } else {
            if (role.equals("two")) {
                holder.time.setVisibility(View.VISIBLE);
                holder.time.setText(cyCarMessages.get(position).getIN_DATE() + "--" + cyCarMessages.get(position).getOUT_DATE());
                if (!cyCarMessages.get(position).getFLAG().equals("")) {
                    Log.e("--------", "==cyCarMessages.get(position).getFLAG()==" + cyCarMessages.get(position).getFLAG() + "position==" + position);
//                    int color = activity.getResources().getColor(R.color.color_42D42B);
                    holder.time.setBackgroundResource(R.color.color_42D42B);
//                    holder.time.setBackgroundColor(Resources.getSystem().getColor(R.color.design_default_color_primary));
                } else {
                    holder.time.setBackgroundResource(R.color.color_eeeeee);
                }
            }
            holder.carId.setText(cyCarMessages.get(position).getAPPLY_NO());
            holder.dock.setEllipsize(TextUtils.TruncateAt.END);
            holder.dock.setSingleLine(true);
            holder.dock.setText(cyCarMessages.get(position).getCOMPANY_NAME());
            holder.container.setText(cyCarMessages.get(position).getDRIVER_NAME());
            holder.state.setText(cyCarMessages.get(position).getCAR_NUM());
        }
        return convertView;
    }

    private class ViewHolder {
        public TextView carId,dock,container,state;//車牌號,碼頭,櫃號,狀態 或 單號,公司名稱,司機姓名,車牌號
        public TextView time;//時間
        public TextView  tvGo, tvAdd;//放行,提交
    }

    public interface OnClickListenerGoOrAdd {
        void OnClickListenerGo(int position);

        void OnClickListenerAdd(int position);
    }

    public void setOnClickListenerGoOrAdd(OnClickListenerGoOrAdd onClickListenerGoOrAdd) {
        this.onClickListenerGoOrAdd = onClickListenerGoOrAdd;
    }
}
