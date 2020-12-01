package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 巡檢點 信息類
 * Created by song on 2017/8/30.
 */

public class RouteMessage implements Serializable {

    public String dim_id;//巡檢二維碼id

    public String dim_locale;//巡檢點名稱

    public String count;//已巡檢次數

    public String sc_id;//掃描主鍵



    public String getSc_id() {
        return sc_id;
    }

    public void setSc_id(String sc_id) {
        this.sc_id = sc_id;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDim_locale() {
        return dim_locale;
    }

    public void setDim_locale(String dim_locale) {
        this.dim_locale = dim_locale;
    }

    public String getDim_id() {
        return dim_id;
    }

    public void setDim_id(String dim_id) {
        this.dim_id = dim_id;
    }
}
