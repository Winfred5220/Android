package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 行車日誌 退件
 * Created by song on 2018/1/10.
 */

public class CarLogReturnM implements Serializable {
    private String xc_id;//單頭
    private String car_code;//車牌號
    private String car_driver;//司機名稱
    private String xc_date;//日誌記錄時間
    private String xc_state;//
    private String xc_create_date;//上傳日誌時間
    private String rec_name;//退件人
    private String rec_content;//退件原因

    public String getXc_id() {
        return xc_id;
    }

    public void setXc_id(String xc_id) {
        this.xc_id = xc_id;
    }

    public String getCar_code() {
        return car_code;
    }

    public void setCar_code(String car_code) {
        this.car_code = car_code;
    }

    public String getCar_driver() {
        return car_driver;
    }

    public void setCar_driver(String car_driver) {
        this.car_driver = car_driver;
    }

    public String getXc_date() {
        return xc_date;
    }

    public void setXc_date(String xc_date) {
        this.xc_date = xc_date;
    }

    public String getXc_state() {
        return xc_state;
    }

    public void setXc_state(String xc_state) {
        this.xc_state = xc_state;
    }

    public String getXc_create_date() {
        return xc_create_date;
    }

    public void setXc_create_date(String xc_create_date) {
        this.xc_create_date = xc_create_date;
    }

    public String getRec_name() {
        return rec_name;
    }

    public void setRec_name(String rec_name) {
        this.rec_name = rec_name;
    }

    public String getRec_content() {
        return rec_content;
    }

    public void setRec_content(String rec_content) {
        this.rec_content = rec_content;
    }
}
