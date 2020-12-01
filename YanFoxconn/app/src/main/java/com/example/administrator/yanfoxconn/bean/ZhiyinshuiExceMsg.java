package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 直飲水異常信息
 * Created by wang on 2020/07/28.
 */

public class ZhiyinshuiExceMsg implements Serializable {
    private String sc_id;//主鍵
    private String dim_id;//二維碼主鍵
    private String dim_locale;//排序
    private String real_id;//維修ID
    private String sc_creator;//點檢內容
    private String sc_date;//
    private String sc_creator_id;//
    private String dim_producer;
    private String count;

    private List<ExceInfo> exce_info;//選項

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

    public String getSc_date() {
        return sc_date;
    }

    public void setSc_date(String sc_date) {
        this.sc_date = sc_date;
    }

    public String getSc_creator_id() {
        return sc_creator_id;
    }

    public void setSc_creator_id(String sc_creator_id) {
        this.sc_creator_id = sc_creator_id;
    }

    public List<ExceInfo> getExce_info() {
        return exce_info;
    }

    public void setExce_info(List<ExceInfo> exce_info) {
        this.exce_info = exce_info;
    }

    public String getReal_id() {
        return real_id;
    }

    public void setReal_id(String real_id) {
        this.real_id = real_id;
    }

    public String getDim_producer() {
        return dim_producer;
    }

    public void setDim_producer(String dim_producer) {
        this.dim_producer = dim_producer;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
