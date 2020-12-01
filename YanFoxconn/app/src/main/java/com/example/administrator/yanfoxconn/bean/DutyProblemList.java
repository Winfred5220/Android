package com.example.administrator.yanfoxconn.bean;

/**
 * 值班課長稽核問題列表
 * Created by wang on 2019/8/16.
 */

public class DutyProblemList {

    private String CHECK_DATE;//稽核時間
    private String POSITION;//崗位位置
    private String CONDITION;//巡崗狀況
    private String PERSON;//責任人
    private String TEAM;//科隊
    private String CAPTAIN;//隊長
    private String NAME;//課長
    private String DUTY_DATE;//值班日期
    private String NO;//主鍵

    public String getCHECK_DATE() {
        return CHECK_DATE;
    }

    public void setCHECK_DATE(String CHECK_DATE) {
        this.CHECK_DATE = CHECK_DATE;
    }

    public String getPOSITION() {
        return POSITION;
    }

    public void setPOSITION(String POSITION) {
        this.POSITION = POSITION;
    }

    public String getCONDITION() {
        return CONDITION;
    }

    public void setCONDITION(String CONDITION) {
        this.CONDITION = CONDITION;
    }

    public String getPERSON() {
        return PERSON;
    }

    public void setPERSON(String PERSON) {
        this.PERSON = PERSON;
    }

    public String getTEAM() {
        return TEAM;
    }

    public void setTEAM(String TEAM) {
        this.TEAM = TEAM;
    }

    public String getCAPTAIN() {
        return CAPTAIN;
    }

    public void setCAPTAIN(String CAPTAIN) {
        this.CAPTAIN = CAPTAIN;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getDUTY_DATE() {
        return DUTY_DATE;
    }

    public void setDUTY_DATE(String DUTY_DATE) {
        this.DUTY_DATE = DUTY_DATE;
    }

    public String getNO() {
        return NO;
    }

    public void setNO(String NO) {
        this.NO = NO;
    }
}
