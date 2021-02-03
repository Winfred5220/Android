package com.example.administrator.yanfoxconn.activity.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.WebViewActivity;


/**
 * 班牌Fragment
 * Created by wang on 2019/11/9.
 */

public class BaobiaoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baobiao, null);

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        Button btnBack = (Button) view.findViewById(R.id.btn_title_left);
        tvTitle.setText("報表");
        btnBack.setVisibility(view.GONE);


        //工業安全鋰電池
        LinearLayout btnYuyue = (LinearLayout) view.findViewById(R.id.ib_yuyue);
        btnYuyue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("role","FX");
                startActivity(intent);
            }
        });
        //查看预约
        LinearLayout btnChakan = (LinearLayout) view.findViewById(R.id.ib_chakan);
        btnChakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("role","FX");
                startActivity(intent);
            }
        });
        //考勤
        LinearLayout ibKaoqin = (LinearLayout) view.findViewById(R.id.ib_kaoqin);
        ibKaoqin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("role","FX");
                startActivity(intent);
            }
        });
        //签核
        LinearLayout ibSign = (LinearLayout) view.findViewById(R.id.ib_sign);
        ibSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("role","FX");
                startActivity(intent);
            }
        });
        //教室利用率
        LinearLayout btnUsedRatio = (LinearLayout) view.findViewById(R.id.btn_used_ratio);
        btnUsedRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("role","FX");
                startActivity(intent);
            }
        });

        //空间数据
        LinearLayout btnRoom = (LinearLayout) view.findViewById(R.id.btn_room);
        btnRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("role","FX");
                startActivity(intent);
            }
        });

        return view;
    }

}