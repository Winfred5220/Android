package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 110接處警主表
 * Created by song on 2018/12/7.
 */

public class AQ110Message implements Serializable {
    //警情信息
    private String ID;//主鍵單號
    private String CPC;//報警方式
    private String CREAT_DATE;//創建日期
    private String BUMEN;//警情位置
    private String WJ_REMARK;//報警內容
    private String OTHER;//聯繫方式
    private String KEDUI;//指揮中心處置
    private String CK_TYPE;//狀態（是否結案）
    private String CODE;//報案人工號
    private String NAME;//姓名
    private String WJ_ADDRESS;//部門
    private String JC_TYPE;//資位
    private String INTO_DATE;//入廠日期
    private String ZZ_TYPE;//警情類別
    private String BA_DATE;//報案日期

    //單身表
    private String B_ID;//主鍵
    private String B_DESC;//描述
    private String B_REDEEM;//挽回損失
    private String B_LOGIN_CODE;//處警工號
    private String B_LOGIN_NAME;//處警人
    private String B_CREATE_DATE;//創建時間


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCPC() {
        return CPC;
    }

    public void setCPC(String CPC) {
        this.CPC = CPC;
    }

    public String getCREAT_DATE() {
        return CREAT_DATE;
    }

    public void setCREAT_DATE(String CREAT_DATE) {
        this.CREAT_DATE = CREAT_DATE;
    }

    public String getBUMEN() {
        return BUMEN;
    }

    public void setBUMEN(String BUMEN) {
        this.BUMEN = BUMEN;
    }

    public String getWJ_REMARK() {
        return WJ_REMARK;
    }

    public void setWJ_REMARK(String WJ_REMARK) {
        this.WJ_REMARK = WJ_REMARK;
    }

    public String getOTHER() {
        return OTHER;
    }

    public void setOTHER(String OTHER) {
        this.OTHER = OTHER;
    }

    public String getKEDUI() {
        return KEDUI;
    }

    public void setKEDUI(String KEDUI) {
        this.KEDUI = KEDUI;
    }

    public String getCK_TYPE() {
        return CK_TYPE;
    }

    public void setCK_TYPE(String CK_TYPE) {
        this.CK_TYPE = CK_TYPE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

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

    public String getJC_TYPE() {
        return JC_TYPE;
    }

    public void setJC_TYPE(String JC_TYPE) {
        this.JC_TYPE = JC_TYPE;
    }

    public String getINTO_DATE() {
        return INTO_DATE;
    }

    public void setINTO_DATE(String INTO_DATE) {
        this.INTO_DATE = INTO_DATE;
    }

    public String getZZ_TYPE() {
        return ZZ_TYPE;
    }

    public void setZZ_TYPE(String ZZ_TYPE) {
        this.ZZ_TYPE = ZZ_TYPE;
    }

    public String getBA_DATE() {
        return BA_DATE;
    }

    public void setBA_DATE(String BA_DATE) {
        this.BA_DATE = BA_DATE;
    }

    public String getB_ID() {
        return B_ID;
    }

    public void setB_ID(String b_ID) {
        B_ID = b_ID;
    }

    public String getB_DESC() {
        return B_DESC;
    }

    public void setB_DESC(String b_DESC) {
        B_DESC = b_DESC;
    }

    public String getB_REDEEM() {
        return B_REDEEM;
    }

    public void setB_REDEEM(String b_REDEEM) {
        B_REDEEM = b_REDEEM;
    }

    public String getB_LOGIN_CODE() {
        return B_LOGIN_CODE;
    }

    public void setB_LOGIN_CODE(String b_LOGIN_CODE) {
        B_LOGIN_CODE = b_LOGIN_CODE;
    }

    public String getB_LOGIN_NAME() {
        return B_LOGIN_NAME;
    }

    public void setB_LOGIN_NAME(String b_LOGIN_NAME) {
        B_LOGIN_NAME = b_LOGIN_NAME;
    }

    public String getB_CREATE_DATE() {
        return B_CREATE_DATE;
    }

    public void setB_CREATE_DATE(String b_CREATE_DATE) {
        B_CREATE_DATE = b_CREATE_DATE;
    }
}

