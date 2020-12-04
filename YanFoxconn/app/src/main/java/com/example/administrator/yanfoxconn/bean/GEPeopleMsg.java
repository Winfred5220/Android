package com.example.administrator.yanfoxconn.bean;

/**
 * song 2020/12/01
 * 安保部 健康追蹤
 */
public class GEPeopleMsg {
    private String WORKNO;//工號
    private String CHINESENAME;//姓名
    private String SEX;//性別
    private String ORGNAME;//產品處
    private String PRIVATETEL;//電話

    public String getPRIVATETEL() {
        return PRIVATETEL;
    }

    public void setPRIVATETEL(String PRIVATETEL) {
        this.PRIVATETEL = PRIVATETEL;
    }

    public String getCHINESENAME() {
        return CHINESENAME;
    }

    public String getORGNAME() {
        return ORGNAME;
    }

    public String getSEX() {
        return SEX;
    }

    public String getWORKNO() {
        return WORKNO;
    }

    public void setCHINESENAME(String CHINESENAME) {
        this.CHINESENAME = CHINESENAME;
    }

    public void setORGNAME(String ORGNAME) {
        this.ORGNAME = ORGNAME;
    }

    public void setSEX(String SEX) {
        this.SEX = SEX;
    }

    public void setWORKNO(String WORKNO) {
        this.WORKNO = WORKNO;
    }
}
