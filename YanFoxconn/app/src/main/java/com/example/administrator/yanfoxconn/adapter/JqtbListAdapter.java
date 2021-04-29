package com.example.administrator.yanfoxconn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 110警情通報 中心處置
 * Created by S1007989 on 2018/12/4.
 */

public class JqtbListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private List<String> olist;
    private List<String> list;
    private OnClickSetText onClickSetText;
    private List<String> getId;

    /**
     * 构造函数
     */
    public JqtbListAdapter(Context context, List<String> teamList) {
        this.mInflater = LayoutInflater.from(context);
        this.list = teamList;
        this.olist = list;
    }


    public void SearchCity(String city) {
        this.list = Search(city);
        notifyDataSetChanged();
    }

    private List<String> Search(String city) {
        //如果查询的值不是空的就走进来然后返回搜索后的值，否则返回原本的值
        if (city != null && city.length() > 0) {
            //new一个新的容器
            ArrayList<String> area = new ArrayList<String>();
            getId = new ArrayList<>();
            //循环olist集合
            for (String a : this.olist) {
                //判断a里面如果包含了搜索的值，有就添加，没有否则就不添加
                if (a.toString().indexOf(city) != -1) {
                    area.add(a);
                }
            }
            return area;
        } else {
            return  new ArrayList<>();
//                return this.olist;
        }

    }

    @Override
    public int getCount() {
        return list.size();//返回数组的长度
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 书中详细解释该方法
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //观察convertView随ListView滚动情况
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_emp_team, null);
            holder = new ViewHolder();
            /**得到各个控件的对象*/
            holder.title = (TextView) convertView.findViewById(R.id.ItemTitle);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
        }
        /**设置TextView显示的内容，即我们存放在动态数组中的数据*/
        holder.title.setText(list.get(position));
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  onClickSetText.OnClickxt(list.get(position));
            }
        });
        return convertView;
    }

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView title;
//        public TextView text;
    }
    public interface OnClickSetText {
        void OnClickxt(String tit);

    }

    public void OnClickSetText(OnClickSetText onClickSetText) {
        this.onClickSetText = onClickSetText;
    }

}
