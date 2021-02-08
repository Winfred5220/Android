package com.example.administrator.yanfoxconn.bean;

/**
 * song 2020/12/01
 * 安保部 健康追蹤
 */
public class GEPeopleMsg {
    private String WORKNO;//工號
    private String CHINESENAME;//姓名
    private String SEX;//性別
    private String BU_CODE;//產品處
    private String CZC03;//部門
//    private String ORGNAME;//產品處
    private String PRIVATETEL;//電話

    public String getBU_CODE() {
        return BU_CODE;
    }

    public String getCZC03() {
        return CZC03;
    }

    public void setBU_CODE(String BU_CODE) {
        this.BU_CODE = BU_CODE;
    }

    public void setCZC03(String CZC03) {
        this.CZC03 = CZC03;
    }

    public String getPRIVATETEL() {
        return PRIVATETEL;
    }

    public void setPRIVATETEL(String PRIVATETEL) {
        this.PRIVATETEL = PRIVATETEL;
    }

    public String getCHINESENAME() {
        return CHINESENAME;
    }

//    public String getORGNAME() {
//        return ORGNAME;
//    }

    public String getSEX() {
        return SEX;
    }

    public String getWORKNO() {
        return WORKNO;
    }

    public void setCHINESENAME(String CHINESENAME) {
        this.CHINESENAME = CHINESENAME;
    }

//    public void setORGNAME(String ORGNAME) {
//        this.ORGNAME = ORGNAME;
//    }

    public void setSEX(String SEX) {
        this.SEX = SEX;
    }

    public void setWORKNO(String WORKNO) {
        this.WORKNO = WORKNO;
    }

    private String SF_CODE;//身份證
    private String NAME;//廠商姓名
//    private String SEX;//
    private String DANWEI;//廠商單位
    private String TEL;//廠商電話

    public String getDANWEI() {
        return DANWEI;
    }

    public String getNAME() {
        return NAME;
    }

    public String getSF_CODE() {
        return SF_CODE;
    }

    public String getTEL() {
        return TEL;
    }

    public void setDANWEI(String DANWEI) {
        this.DANWEI = DANWEI;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public void setSF_CODE(String SF_CODE) {
        this.SF_CODE = SF_CODE;
    }

    public void setTEL(String TEL) {
        this.TEL = TEL;
    }

}
