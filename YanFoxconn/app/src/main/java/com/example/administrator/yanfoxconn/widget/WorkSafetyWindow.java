package com.example.administrator.yanfoxconn.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.administrator.yanfoxconn.R;

/**
 * 工安巡檢使用的彈出菜單
 * Created by wang on 2019/7/29.
 */

public class WorkSafetyWindow extends PopupWindow{
    private View mainView;
    private LinearLayout llAdd,llPeople,llText,llSee;

    public WorkSafetyWindow(Activity paramActivity,
                            View.OnClickListener paramOnClickListener, int paramInt1,
                            int paramInt2) {
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(
                R.layout.popwin_worksafety_layout, null);
        //收藏布局
        llAdd = ((LinearLayout) mainView.findViewById(R.id.ll_add));
        //收藏布局
        llPeople = ((LinearLayout) mainView.findViewById(R.id.ll_people));
        //收藏布局
        llText = ((LinearLayout) mainView.findViewById(R.id.ll_text));
        //收藏布局
        llSee = ((LinearLayout) mainView.findViewById(R.id.ll_see));

        //设置每个子布局的事件监听器
        if (paramOnClickListener != null) {
            llAdd.setOnClickListener(paramOnClickListener);
            llPeople.setOnClickListener(paramOnClickListener);
            llText.setOnClickListener(paramOnClickListener);
            llSee.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        //设置宽度
        setWidth(paramInt1);
        //设置高度
        setHeight(paramInt2);

    }
}
