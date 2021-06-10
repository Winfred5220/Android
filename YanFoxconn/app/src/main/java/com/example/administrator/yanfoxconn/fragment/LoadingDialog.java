package com.example.administrator.yanfoxconn.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.administrator.yanfoxconn.R;


/**
 * 等待弹框
 */
public class LoadingDialog extends DialogFragment {

    private String msg = "正在加载";
    private boolean onTouchOutside = true;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        //getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getDialog().setCanceledOnTouchOutside(onTouchOutside);
        View loadingView = inflater.inflate(R.layout.fragment_dialog_loading, container);
        textView= loadingView.findViewById(R.id.textView);
        textView.setText(msg);
        return loadingView;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.dialog_login);
//        dialog.setCancelable(false);//點擊屏幕不消失 點返回鍵不消失
        dialog.setCanceledOnTouchOutside(true);//點擊屏幕不消失 點返回鍵消失
        return dialog;
    }
    public LoadingDialog setMsg(String msg) {
        this.msg = msg;
        if (textView!= null) {
            textView.setText(msg);
        }
        return this;
    }

    public LoadingDialog setOnTouchOutside(boolean onTouchOutside) {
        this.onTouchOutside = onTouchOutside;
        if(getDialog() != null){
            getDialog().setCanceledOnTouchOutside(onTouchOutside);
        }
        return this;
    }





}
