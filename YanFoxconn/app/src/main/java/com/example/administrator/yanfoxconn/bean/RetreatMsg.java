package com.example.administrator.yanfoxconn.bean;

/**
 * 人資退訓放行掃描信息&班導信息
 * Created by wang on 2020/04/27.
 */

public class RetreatMsg {

    private String code;//工號
    private String sf_code;//身份證
    private String sign_code;//人資系統主鍵
    private String name;//姓名
    private String report_date;//報到日期
    private String rl_from;//人力來源
    private String yg_type;//人力性質
    private String tx_reason;//退訓原因
    private String tx_type;//退訓類別
    private String tx_huanjie;//退訓環節
    private String tx_date;//退訓日期
    private String test_num;//分數


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSf_code() {
        return sf_code;
    }

    public void setSf_code(String sf_code) {
        this.sf_code = sf_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReport_date() {
        return report_date;
    }

    public void setReport_date(String report_date) {
        this.report_date = report_date;
    }

    public String getRl_from() {
        return rl_from;
    }

    public void setRl_from(String rl_from) {
        this.rl_from = rl_from;
    }

    public String getYg_type() {
        return yg_type;
    }

    public void setYg_type(String yg_type) {
        this.yg_type = yg_type;
    }

    public String getTx_reason() {
        return tx_reason;
    }

    public void setTx_reason(String tx_reason) {
        this.tx_reason = tx_reason;
    }

    public String getTx_type() {
        return tx_type;
    }

    public void setTx_type(String tx_type) {
        this.tx_type = tx_type;
    }

    public String getTx_huanjie() {
        return tx_huanjie;
    }

    public void setTx_huanjie(String tx_huanjie) {
        this.tx_huanjie = tx_huanjie;
    }

    public String getTx_date() {
        return tx_date;
    }

    public void setTx_date(String tx_date) {
        this.tx_date = tx_date;
    }

    public String getTest_num() {
        return test_num;
    }

    public void setTest_num(String test_num) {
        this.test_num = test_num;
    }

    public String getSign_code() {
        return sign_code;
    }

    public void setSign_code(String sign_code) {
        this.sign_code = sign_code;
    }
}
