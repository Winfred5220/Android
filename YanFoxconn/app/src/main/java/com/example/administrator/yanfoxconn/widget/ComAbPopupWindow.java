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

public class ComAbPopupWindow extends PopupWindow{
    private View mainView;
    private LinearLayout llQrCode, llN,llQueryError;
    private TextView tvOne,tvTwo;

    public ComAbPopupWindow(Activity paramActivity,
                            View.OnClickListener paramOnClickListener, int paramInt1,
                            int paramInt2) {
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(
                R.layout.popwin_com_ab_layout, null);
        //收藏布局
//        llQrCode = ((LinearLayout) mainView.findViewById(R.id.ll_qr_code));
        //收藏布局
        llN = ((LinearLayout) mainView.findViewById(R.id.ll_n));
        //收藏布局
        llQueryError = ((LinearLayout) mainView.findViewById(R.id.ll_query_error));

        //设置每个子布局的事件监听器
        if (paramOnClickListener != null) {
//            llQrCode.setOnClickListener(paramOnClickListener);
            llN.setOnClickListener(paramOnClickListener);
            llQueryError.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        //设置宽度
        setWidth(paramInt1);
        //设置高度
        setHeight(paramInt2);

    }
    public ComAbPopupWindow(Activity paramActivity,
                            View.OnClickListener paramOnClickListener, int paramInt1,
                            int paramInt2, String from) {
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(
                R.layout.popwin_com_ab_layout, null);
        //收藏布局
//        llQrCode = ((LinearLayout) mainView.findViewById(R.id.ll_qr_code));
        //收藏布局
        llN = ((LinearLayout) mainView.findViewById(R.id.ll_n));
        //收藏布局
        llQueryError = ((LinearLayout) mainView.findViewById(R.id.ll_query_error));
//        llQrCode.setVisibility(View.GONE);
        tvOne=(TextView) mainView.findViewById(R.id.tv_one);
        tvTwo=(TextView)mainView.findViewById(R.id.tv_two);
        tvOne.setText("已消殺車輛");
        tvTwo.setText("特殊車輛");
        //设置每个子布局的事件监听器
        if (paramOnClickListener != null) {
//            llQrCode.setOnClickListener(paramOnClickListener);
            llN.setOnClickListener(paramOnClickListener);
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
