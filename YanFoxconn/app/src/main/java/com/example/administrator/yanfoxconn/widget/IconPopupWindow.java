package com.example.administrator.yanfoxconn.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.administrator.yanfoxconn.R;

/**
 * 巡檢使用的彈出菜單
 * Created by song on 2017/10/19.
 */

public class IconPopupWindow extends PopupWindow{
    private View mainView;
    private LinearLayout llQrCode, llAddError,llQueryError,llSelfError;

    public IconPopupWindow(Activity paramActivity,
                           View.OnClickListener paramOnClickListener, int paramInt1,
                           int paramInt2) {
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(
                R.layout.popwin_layout, null);
        //收藏布局
        llQrCode = ((LinearLayout) mainView.findViewById(R.id.ll_qr_code));
        //收藏布局
        llAddError = ((LinearLayout) mainView.findViewById(R.id.ll_add_error));
        //收藏布局
        llQueryError = ((LinearLayout) mainView.findViewById(R.id.ll_query_error));
        //收藏布局
        llSelfError = ((LinearLayout) mainView.findViewById(R.id.ll_self_error));

        //设置每个子布局的事件监听器
        if (paramOnClickListener != null) {
            llQrCode.setOnClickListener(paramOnClickListener);
            llAddError.setOnClickListener(paramOnClickListener);
            llQueryError.setOnClickListener(paramOnClickListener);
            llSelfError.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        //设置宽度
        setWidth(paramInt1);
        //设置高度
        setHeight(paramInt2);

    }
    public IconPopupWindow(Activity paramActivity,
                             View.OnClickListener paramOnClickListener, int paramInt1,
                             int paramInt2,String from) {
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(
                R.layout.popwin_layout, null);
        //收藏布局
        llQrCode = ((LinearLayout) mainView.findViewById(R.id.ll_qr_code));
        //收藏布局
        llAddError = ((LinearLayout) mainView.findViewById(R.id.ll_add_error));
        //收藏布局
        llQueryError = ((LinearLayout) mainView.findViewById(R.id.ll_query_error));
        //收藏布局
        llSelfError = ((LinearLayout) mainView.findViewById(R.id.ll_self_error));
        llQrCode.setVisibility(View.GONE);

        llSelfError.setVisibility(View.GONE);
        //设置每个子布局的事件监听器
        if (paramOnClickListener != null) {
//            llQrCode.setOnClickListener(paramOnClickListener);
            llAddError.setOnClickListener(paramOnClickListener);
            llQueryError.setOnClickListener(paramOnClickListener);
//            llSelfError.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        //设置宽度
        setWidth(paramInt1);
        //设置高度
        setHeight(paramInt2);

    }
}
