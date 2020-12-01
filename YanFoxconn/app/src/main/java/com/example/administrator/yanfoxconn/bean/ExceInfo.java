package com.example.administrator.yanfoxconn.bean;

import java.util.List;

/**
 * 直飲水異常信息
 * Created by wang on 2020/07/28.
 */

public class ExceInfo {
    private String exce_id;//主鍵
    private String content;//二維碼主鍵
    private String exce_time;//排序
    private String exce_desp;//點檢內容
    private String exce_result;//類型input、radio、checkbox
    private String real_id;

    public String getExce_id() {
        return exce_id;
    }

    public void setExce_id(String exce_id) {
        this.exce_id = exce_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExce_time() {
        return exce_time;
    }

    public void setExce_time(String exce_time) {
        this.exce_time = exce_time;
    }

    public String getExce_desp() {
        return exce_desp;
    }

    public void setExce_desp(String exce_desp) {
        this.exce_desp = exce_desp;
    }

    public String getExce_result() {
        return exce_result;
    }

    public void setExce_result(String exce_result) {
        this.exce_result = exce_result;
    }

    public String getReal_id() {
        return real_id;
    }

    public void setReal_id(String real_id) {
        this.real_id = real_id;
    }
}
