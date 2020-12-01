package com.example.administrator.yanfoxconn.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.administrator.yanfoxconn.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 欢迎页
 * Created by song on 2017/8/29.
 */

public class WelcomeActivity extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                finish();
            }
        }, 3000);

    }

}
