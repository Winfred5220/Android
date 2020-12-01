package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.constant.FoxContext;
import com.example.administrator.yanfoxconn.utils.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForkliftEntranceActivity extends BaseActivity {

    private String roles;//當前用戶權限

    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.btn_xunjian)
    Button btnXunjian;//巡檢
    @BindView(R.id.btn_weixiu)
    Button btnWeixiu;//維修
    @BindView(R.id.btn_baoxiu)
    Button btnBaoxiu;//報修

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forklift_entrance);
        ButterKnife.bind(this);

        roles = FoxContext.getInstance().getRoles();

        tvTitle.setText("叉車巡檢");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnXunjian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(roles.contains("CN")){
                Intent intent=new Intent(ForkliftEntranceActivity.this,QrCodeActivity.class);
                intent.putExtra("title", "二維碼掃描");
                intent.putExtra("num", "forklift_check");
                startActivity(intent);
                }else{
                    Toast.makeText(ForkliftEntranceActivity.this,"您沒有巡檢權限",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnBaoxiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(roles.contains("CM")){
                Intent intent=new Intent(ForkliftEntranceActivity.this,QrCodeActivity.class);
                intent.putExtra("title", "二維碼掃描");
                intent.putExtra("num", "forklift_repair");
                startActivity(intent);
                }else{
                    Toast.makeText(ForkliftEntranceActivity.this,"您沒有報修權限",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnWeixiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(roles.contains("CO")){
                Intent intent=new Intent(ForkliftEntranceActivity.this,ForkliftMaintenanceListActivity.class);
                startActivity(intent);
                }else{
                    Toast.makeText(ForkliftEntranceActivity.this,"您沒有維修權限",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
