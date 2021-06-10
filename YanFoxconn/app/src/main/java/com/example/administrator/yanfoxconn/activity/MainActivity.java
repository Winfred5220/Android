package com.example.administrator.yanfoxconn.activity;


import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.fragment.BaobiaoFragment;
import com.example.administrator.yanfoxconn.fragment.XunjianFragment;
import com.example.administrator.yanfoxconn.utils.BaseActivity;

/**
 * 主界面
 * Created by wang on 2019/11/9.
 */

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener{

    //定义布局内的控件
    RadioGroup radioGroup;
    FrameLayout frameLayout;
    //定义四个存放碎片的数组
    Fragment[] fragment = new Fragment[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        radioGroup.check(R.id.main_rb1);
        //显示第一个碎片页面
        showFragment(0);

    }

    /**
     * 显示碎片页面的方法
     * 这里是要重点理解的地方
     * 这里要添加和移除的操作都有，而且还有进行一定的判断
     */
    //定义一个当前点击的游标值，默认是-1，说明还没有点
    int currentIndex = -1;

    private void showFragment(int i) {
        //如果点击的页面是刚才显示的页面，就什么都不做
        if (i == currentIndex) {
            return;
        }

        //处理碎片，显示、移除等等
        //这里要用碎片的事务来完成
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //如果用户打开已经打开过一个Fragment页面，再打开其他页面后，要先把原来的页面移除
        if (currentIndex != -1) {
            //移除碎片
            transaction.hide(fragment[currentIndex]);
        }
        //显示新的碎片
        if (fragment[i] == null) {
            //创建碎片
            CreateFragment(i);
            //使用事务显示碎片
            //第一个参数是碎片要显示的布局的位置的ID号
            //第二个参数是显示的碎片的对象
            transaction.add(R.id.main_fl, fragment[i]);
        } else {
            //如果碎片曾经显示过就显示出来就可以了
            transaction.show(fragment[i]);
            // transaction.addToBackStack(null);
        }

        //保存用户点击的游标值
        currentIndex = i;
        //最后提交事务，把碎片放置到位
        transaction.commit();
    }


    //初始化数据
    private void initView() {
        //实例化数据
        radioGroup = (RadioGroup) findViewById(R.id.main_rg);
        frameLayout = (FrameLayout) findViewById(R.id.main_fl);
        //给GroupButton设置监听事件
        radioGroup.setOnCheckedChangeListener(this);

    }

    /**
     * 按钮选择后触发的方法
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        //点击哪一个按钮就显示哪一个碎片
        //这里的checkedID不是0、1、2、3这种数值，而是布局里面对应的控件的ID值
        switch (checkedId) {
            case R.id.main_rb1:
                showFragment(0);
                break;
            case R.id.main_rb2:
                showFragment(1);
                break;
        }

    }

    /**
     * 创建碎片页面对象的方法
     */
    private void CreateFragment(int i) {
        //如果碎片是第一次点开,就要创建碎片
        switch (i) {
            case 0:
                fragment[i] = new XunjianFragment();
                break;
            case 1:
                fragment[i] = new BaobiaoFragment();
                break;
        }
    }

    //禁止使用返回键返回到上一页,但是可以直接退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;//不执行父类点击事件
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

}
