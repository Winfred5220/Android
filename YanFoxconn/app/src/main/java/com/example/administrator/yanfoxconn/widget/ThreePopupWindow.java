package com.example.administrator.yanfoxconn.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;

/**
 * 巡檢使用的彈出菜單
 * Created by song on 2017/10/19.
 */

public class ThreePopupWindow extends PopupWindow{
    private View mainView;
    private LinearLayout llOne, llTwo,llThree;
    private TextView tvOne,tvTwo,tvThree;

    public ThreePopupWindow(Activity paramActivity,
                            View.OnClickListener paramOnClickListener, int paramInt1,
                            int paramInt2) {
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(
                R.layout.popwin_three_layout, null);
        //收藏布局
        llOne = ((LinearLayout) mainView.findViewById(R.id.ll_one));
        //收藏布局
        llTwo = ((LinearLayout) mainView.findViewById(R.id.ll_two));
        //收藏布局
        llThree = ((LinearLayout) mainView.findViewById(R.id.ll_three));

        //设置每个子布局的事件监听器
        if (paramOnClickListener != null) {
            llOne.setOnClickListener(paramOnClickListener);
            llTwo.setOnClickListener(paramOnClickListener);
            llThree.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        //设置宽度
        setWidth(paramInt1);
        //设置高度
        setHeight(paramInt2);

    }
    public ThreePopupWindow(Activity paramActivity,
                            View.OnClickListener paramOnClickListener, int paramInt1,
                            int paramInt2, String from) {
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(
                R.layout.popwin_three_layout, null);
        //收藏布局
        llOne = ((LinearLayout) mainView.findViewById(R.id.ll_one));
        //收藏布局
        llTwo = ((LinearLayout) mainView.findViewById(R.id.ll_two));
        //收藏布局
        llThree = ((LinearLayout) mainView.findViewById(R.id.ll_three));

        tvOne=(TextView) mainView.findViewById(R.id.tv_one);
        tvTwo=(TextView)mainView.findViewById(R.id.tv_two);
        tvThree = (TextView)mainView.findViewById(R.id.tv_three);
        tvOne.setText("上報異常");
        tvTwo.setText("查看異常");
        tvThree.setText("異常整改");
        //设置每个子布局的事件监听器
        if (paramOnClickListener != null) {
            llOne.setOnClickListener(paramOnClickListener);
            llTwo.setOnClickListener(paramOnClickListener);
            llThree.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        //设置宽度
        setWidth(paramInt1);
        //设置高度
        setHeight(paramInt2);

    }
}
