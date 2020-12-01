package com.example.administrator.yanfoxconn.utils;



import com.example.administrator.yanfoxconn.activity.LoadingDialogFragment;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Created by song on 2017/9/5.
 */

public abstract class BaseActivity extends FragmentActivity {

    public void showDialog() {
        DialogFragment dialog =(DialogFragment) getSupportFragmentManager().findFragmentByTag("loading_dialog");
        if (dialog == null) {
            LoadingDialogFragment.newInstance(false,"加載中......").show(getSupportFragmentManager(), "loading_dialog");
        }
    }

    public void dismissDialog() {
        DialogFragment dialog = (DialogFragment) getSupportFragmentManager().findFragmentByTag("loading_dialog");
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
