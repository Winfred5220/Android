package com.example.administrator.yanfoxconn.bean;

import java.util.List;

/**
 * 無紙化 常用表單 員工基本資料
 * Created by song on 2018/12/7.
 */

public class EmpMessage {
    private String WORKNO;//工號
    private String CHINESENAME;//姓名
    private String BU_CODE;//部門代碼
    private String ORGNAME;//部門名稱
    private String CZC03;
    private String INCUMBENCYSTATE;//在職狀態
    private String IDENTITYNO;//身份證號
    private String EMPLOYEELEVEL;//資位
    private String MANAGER;//職能
    private String JOINGROUPDATE;//入廠時間
    private String YEAR_COUNT;//年度違規次數

    private String CODE;//工號
    private String NAME;//姓名
    private String WJ_ADDRESS;//稽核地點

    private String KEDUI;//指揮中心處置

    private List<EmpFile>  file;

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getWJ_ADDRESS() {
        return WJ_ADDRESS;
    }

    public void setWJ_ADDRESS(String WJ_ADDRESS) {
        this.WJ_ADDRESS = WJ_ADDRESS;
    }

    public String getEMPLOYEELEVEL() {
        return EMPLOYEELEVEL;
    }

    public String getJOINGROUPDATE() {
        return JOINGROUPDATE;
    }

    public String getMANAGER() {
        return MANAGER;
    }

    public void setEMPLOYEELEVEL(String EMPLOYEELEVEL) {
        this.EMPLOYEELEVEL = EMPLOYEELEVEL;
    }

    public void setJOINGROUPDATE(String JOINGROUPDATE) {
        this.JOINGROUPDATE = JOINGROUPDATE;
    }

    public void setMANAGER(String MANAGER) {
        this.MANAGER = MANAGER;
    }


    public String getWORKNO() {
        return WORKNO;
    }

    public void setWORKNO(String WORKNO) {
        this.WORKNO = WORKNO;
    }

    public String getCHINESENAME() {
        return CHINESENAME;
    }

    public void setCHINESENAME(String CHINESENAME) {
        this.CHINESENAME = CHINESENAME;
    }

    public String getIDENTITYNO() {
        return IDENTITYNO;
    }

    public void setIDENTITYNO(String IDENTITYNO) {
        this.IDENTITYNO = IDENTITYNO;
    }

    public String getBU_CODE() {
        return BU_CODE;
    }

    public void setBU_CODE(String BU_CODE) {
        this.BU_CODE = BU_CODE;
    }

    public String getORGNAME() {
        return ORGNAME;
    }

    public void setORGNAME(String ORGNAME) {
        this.ORGNAME = ORGNAME;
    }

    public String getCZC03() {
        return CZC03;
    }

    public void setCZC03(String CZC03) {
        this.CZC03 = CZC03;
    }

    public String getINCUMBENCYSTATE() {
        return INCUMBENCYSTATE;
    }

    public void setINCUMBENCYSTATE(String INCUMBENCYSTATE) {
        this.INCUMBENCYSTATE = INCUMBENCYSTATE;
    }

    public String getYEAR_COUNT() {
        return YEAR_COUNT;
    }

    public void setYEAR_COUNT(String YEAR_COUNT) {
        this.YEAR_COUNT = YEAR_COUNT;
    }
    public List<EmpFile> getFile() {
        return file;
    }

    public void setFile(List<EmpFile> file) {
        this.file = file;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getKEDUI() {
        return KEDUI;
    }

    public void setKEDUI(String KEDUI) {
        this.KEDUI = KEDUI;
    }
}

