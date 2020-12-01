package com.example.administrator.yanfoxconn.activity;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Created by song on 17/9/5.
 */
public class LoadingDialogFragment extends DialogFragment {

    private static final String KEY_DIMABLE = "dimable";

    private static final String KEY_MSG = "msg";

    public static LoadingDialogFragment newInstance(boolean dimable, String msg) {
        LoadingDialogFragment loadingDialogFragment =  new LoadingDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_MSG, msg);
        args.putBoolean(KEY_DIMABLE, dimable);
        loadingDialogFragment.setArguments(args);

//        loadingDialogFragment.setCancelable(dimable);
        return loadingDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_dialog, null);
        TextView msgTxt = (TextView) view.findViewById(R.id.message);
        if (getArguments() != null) {
            msgTxt.setText(getArguments().getString(KEY_MSG));
        }
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        boolean dimable = getArguments().getBoolean(KEY_DIMABLE, false);
        String msg = getArguments().getString(KEY_MSG);

        Dialog dialog = new Dialog(getActivity(), R.style.dialog);
//        dialog.setCancelable(false);//點擊屏幕不消失 點返回鍵不消失
        dialog.setCanceledOnTouchOutside(dimable);//點擊屏幕不消失 點返回鍵消失
        return dialog;
    }

}
