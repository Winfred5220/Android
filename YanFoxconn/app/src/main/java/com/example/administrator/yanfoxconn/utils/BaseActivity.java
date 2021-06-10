package com.example.administrator.yanfoxconn.utils;


import androidx.fragment.app.FragmentActivity;

import com.example.administrator.yanfoxconn.fragment.LoadingDialog;

/**
 * Created by song on 2017/9/5.
 */

public abstract class BaseActivity extends FragmentActivity {
    private LoadingDialog loadingDialog;

    public void showDialog() {
        if(loadingDialog == null){
            loadingDialog = new LoadingDialog();
        }else{
            loadingDialog.dismiss();
        }
        loadingDialog.setMsg("加載中...")
                .setOnTouchOutside(false)
                .show(getSupportFragmentManager(),"loading");
        //fragment的话就把getSupportFragmentManager参数换成getChildFragmentManager
//        DialogFragment dialog =(DialogFragment) getSupportFragmentManager().findFragmentByTag("loading_dialog");
//        if (dialog == null) {
//            LoadingDialogFragment.newInstance(false,"加載中......").show(getSupportFragmentManager(), "loading_dialog");
//        }
    }

    public void dismissDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
//        DialogFragment dialog = (DialogFragment) getSupportFragmentManager().findFragmentByTag("loading_dialog");
//        if (dialog != null) {
//            dialog.dismiss();
//        }
    }

    //显示等待框
    public void showLoading(String msg,boolean touch){
        if(loadingDialog == null){
            loadingDialog = new LoadingDialog();
        }else{
            loadingDialog.dismiss();
        }
        loadingDialog.setMsg(msg)
                .setOnTouchOutside(touch)
                .show(getSupportFragmentManager(),"loading");
        //fragment的话就把getSupportFragmentManager参数换成getChildFragmentManager
    }

    //动态修改等待框中的文字
    public void setLoadingMsg(String msg){
        if(loadingDialog == null){
            return;
        }
        loadingDialog.setMsg(msg);
    }

    //隐藏等待框
    public void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }



}
