package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * Created by song on 2018/4/9.
 */

public class DimemsionMenu implements Serializable{
    private String dim_flag;
    private String dim_flag_name;

    public String getDim_flag() {
        return dim_flag;
    }

    public void setDim_flag(String dim_flag) {
        this.dim_flag = dim_flag;
    }

    public String getDim_flag_name() {
        return dim_flag_name;
    }

    public void setDim_flag_name(String dim_flag_name) {
        this.dim_flag_name = dim_flag_name;
    }
}
