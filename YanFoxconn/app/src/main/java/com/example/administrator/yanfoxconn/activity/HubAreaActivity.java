package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.utils.ToastUtils;

import androidx.appcompat.app.AppCompatActivity;


public class HubAreaActivity extends AppCompatActivity {
    private String[] areaData = {"請選擇區域","A","E03","E05","MIT","PCB","C","A區宿舍"};
    private String Area;
    private Spinner spArea;
    private Button btnReceive;
    private Button btnSign;
    private Button btnSelt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_area);
        //區域数据
        spArea = (Spinner) findViewById(R.id.sp_area);
        btnReceive = (Button) findViewById(R.id.btn_receive);
        btnSign = (Button) findViewById(R.id.btn_sign);
        btnSelt = (Button) findViewById(R.id.btn_selt);
        Button btnBack = (Button) findViewById(R.id.btn_title_left);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvTitle.setText("HUB倉");

        //區域下拉列表選擇
        spArea.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, areaData));
        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Area = areaData[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Area.equals("請選擇區域")){
                    ToastUtils.showShort(HubAreaActivity.this,"請選擇區域");
                }else {
                    Intent intent = new Intent(HubAreaActivity.this, HubInputActivity.class);
                    intent.putExtra("area", Area);
                    startActivity(intent);
                }
            }
        });

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Area.equals("請選擇區域")){
                    ToastUtils.showShort(HubAreaActivity.this,"請選擇區域");
                }else {
                    Intent intent = new Intent(HubAreaActivity.this, HubSignActivity.class);
                    intent.putExtra("area", Area);
                    startActivity(intent);
                }
            }
        });

        btnSelt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Area.equals("請選擇區域")){
                    ToastUtils.showShort(HubAreaActivity.this,"請選擇區域");
                }else {
                    Intent intent = new Intent(HubAreaActivity.this, HubSelectInputActivity.class);
                    intent.putExtra("area", Area);
                    startActivity(intent);
                }
            }
        });
    }
}
