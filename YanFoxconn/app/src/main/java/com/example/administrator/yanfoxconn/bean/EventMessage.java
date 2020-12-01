package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 人資活動信息
 * Created by song on 2018/7/10.
 */

public class EventMessage implements Serializable {
    private String dim_id;
    private String dim_locale;
    private String dim_type;
    private String dim_status;


    public String getDim_id() {
        return dim_id;
    }

    public void setDim_id(String dim_id) {
        this.dim_id = dim_id;
    }

    public String getDim_locale() {
        return dim_locale;
    }

    public void setDim_locale(String dim_locale) {
        this.dim_locale = dim_locale;
    }

    public String getDim_type() {
        return dim_type;
    }

    public void setDim_type(String dim_type) {
        this.dim_type = dim_type;
    }

    public String getDim_status() {
        return dim_status;
    }

    public void setDim_status(String dim_status) {
        this.dim_status = dim_status;
    }
}
