package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;
//物流消殺 車輛信息
public class CyCarMessage implements Serializable {
    private String APPLY_NO;//單號
    private String FEN_APPLY_NO;//單號加分號
    private String COMPANY_NAME;//公司名稱
    private String CAR_NUM;//車牌號
    private String DRIVER_NAME;//司機姓名
    private String DRIVER_CARD;//司機身份證
    private String IN_DATE;//入場
    private String OUT_DATE;//出廠
    private String FLAG;//標誌

    public String getFLAG() {
        return FLAG;
    }

    public void setFLAG(String FLAG) {
        this.FLAG = FLAG;
    }

    public String getIN_DATE() {
        return IN_DATE;
    }

    public void setIN_DATE(String IN_DATE) {
        this.IN_DATE = IN_DATE;
    }

    public String getOUT_DATE() {
        return OUT_DATE;
    }

    public void setOUT_DATE(String OUT_DATE) {
        this.OUT_DATE = OUT_DATE;
    }

    public String getAPPLY_NO() {
        return APPLY_NO;
    }

    public void setAPPLY_NO(String APPLY_NO) {
        this.APPLY_NO = APPLY_NO;
    }

    public String getFEN_APPLY_NO() {
        return FEN_APPLY_NO;
    }

    public void setFEN_APPLY_NO(String FEN_APPLY_NO) {
        this.FEN_APPLY_NO = FEN_APPLY_NO;
    }

    public String getCOMPANY_NAME() {
        return COMPANY_NAME;
    }

    public void setCOMPANY_NAME(String COMPANY_NAME) {
        this.COMPANY_NAME = COMPANY_NAME;
    }

    public String getCAR_NUM() {
        return CAR_NUM;
    }

    public void setCAR_NUM(String CAR_NUM) {
        this.CAR_NUM = CAR_NUM;
    }

    public String getDRIVER_NAME() {
        return DRIVER_NAME;
    }

    public void setDRIVER_NAME(String DRIVER_NAME) {
        this.DRIVER_NAME = DRIVER_NAME;
    }

    public String getDRIVER_CARD() {
        return DRIVER_CARD;
    }

    public void setDRIVER_CARD(String DRIVER_CARD) {
        this.DRIVER_CARD = DRIVER_CARD;
    }
}
