package com.example.administrator.yanfoxconn.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.administrator.yanfoxconn.R;

/**
 * 碼頭放行使用的彈出菜單
 * Created by song on 2017/10/19.
 */

public class CarPopupWindow extends PopupWindow{
    private View mainView;
    private LinearLayout llQrCode, llAddError;

    public CarPopupWindow(Activity paramActivity,
                          View.OnClickListener paramOnClickListener, int paramInt1,
                          int paramInt2) {
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(
                R.layout.popwin_car_layout, null);
        //收藏布局
        llQrCode = ((LinearLayout) mainView.findViewById(R.id.ll_code));
        //收藏布局
        llAddError = ((LinearLayout) mainView.findViewById(R.id.ll_write));


        //设置每个子布局的事件监听器
        if (paramOnClickListener != null) {
            llQrCode.setOnClickListener(paramOnClickListener);
            llAddError.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        //设置宽度
        setWidth(paramInt1);
        //设置高度
        setHeight(paramInt2);

    }
}
