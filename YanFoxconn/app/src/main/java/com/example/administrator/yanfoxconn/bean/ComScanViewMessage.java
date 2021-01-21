package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * Created by Song
 * on 2020/8/5
 * Description：公用點檢 點檢進度 每次點檢內容
 */
public class ComScanViewMessage implements Serializable {
    private String sc_id;
    private String dim_id;
    private String  sc_lng;
    private String   sc_lat;
    private String  dim_locale;
    private String  sc_creator;
    private String  sc_create_date;
    private String sc_date;
    private String  sc_state;
    private String  sc_creator_id;
    private String  count;
    private String cname;//營建工程管理

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getSc_id() {
        return sc_id;
    }

    public void setSc_id(String sc_id) {
        this.sc_id = sc_id;
    }

    public String getDim_id() {
        return dim_id;
    }

    public void setDim_id(String dim_id) {
        this.dim_id = dim_id;
    }

    public String getSc_lng() {
        return sc_lng;
    }

    public void setSc_lng(String sc_lng) {
        this.sc_lng = sc_lng;
    }

    public String getSc_lat() {
        return sc_lat;
    }

    public void setSc_lat(String sc_lat) {
        this.sc_lat = sc_lat;
    }

    public String getDim_locale() {
        return dim_locale;
    }

    public void setDim_locale(String dim_locale) {
        this.dim_locale = dim_locale;
    }

    public String getSc_creator() {
        return sc_creator;
    }

    public void setSc_creator(String sc_creator) {
        this.sc_creator = sc_creator;
    }

    public String getSc_create_date() {
        return sc_create_date;
    }

    public void setSc_create_date(String sc_create_date) {
        this.sc_create_date = sc_create_date;
    }

    public String getSc_date() {
        return sc_date;
    }

    public void setSc_date(String sc_date) {
        this.sc_date = sc_date;
    }

    public String getSc_state() {
        return sc_state;
    }

    public void setSc_state(String sc_state) {
        this.sc_state = sc_state;
    }

    public String getSc_creator_id() {
        return sc_creator_id;
    }

    public void setSc_creator_id(String sc_creator_id) {
        this.sc_creator_id = sc_creator_id;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
