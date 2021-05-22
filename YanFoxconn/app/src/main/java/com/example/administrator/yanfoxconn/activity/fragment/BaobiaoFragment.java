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
import com.example.administrator.yanfoxconn.activity.WebViewLandScapeActivity;
import com.example.administrator.yanfoxconn.activity.WebViewTestActivity;


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

        /** 工業安全 */
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
        //工作互聯
        LinearLayout btnChakan = (LinearLayout) view.findViewById(R.id.ib_work);
        btnChakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(), WebViewTestActivity.class);
                intent.putExtra("role","WORK");
                startActivity(intent);
            }
        });
        /** 營建 */

        //設備點檢
        LinearLayout ibKaoqin = (LinearLayout) view.findViewById(R.id.ib_kaoqin);
        ibKaoqin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("role","FW");
                startActivity(intent);
            }
        });

        /** 總務 */

        //餐飲巡檢
        LinearLayout btnRoom = (LinearLayout) view.findViewById(R.id.ib_sign);
        btnRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("role","CTPP");
                startActivity(intent);
            }
        });


        return view;
    }

}
