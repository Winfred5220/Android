package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * Created by wangqian on 2018/12/21.
 */

    public class HubList implements Serializable {
    private String MATERIAL_CODE;//料號
    private String MATERIAL_NAME;//品名
    private String MATERIAL_SPEC;//型號
    private String SY;//剩餘數量
    private String APPLY_COUNT;//取走數量
    private String ORDER_NO;//訂單號
    private String APPLYER;//領取人
    private String APPLYER_TEL;//電話
    private String APPLYER_ADD;//部門
    private String ID;//單號
    private String UNIT;//單位
    private String RECEIVE_COUNT;//簽收數量
    private String RECEIVE_DATE;//簽收日期RECEIVE_NAME
    private String RECEIVE_NAME;//簽名圖片名

    public String getRECEIVE_COUNT() {
        return RECEIVE_COUNT;
    }

    public void setRECEIVE_COUNT(String RECEIVE_COUNT) {
        this.RECEIVE_COUNT = RECEIVE_COUNT;
    }

    public String getRECEIVE_DATE() {
        return RECEIVE_DATE;
    }

    public void setRECEIVE_DATE(String RECEIVE_DATE) {
        this.RECEIVE_DATE = RECEIVE_DATE;
    }

    public String getRECEIVE_NAME() {
        return RECEIVE_NAME;
    }

    public void setRECEIVE_NAME(String RECEIVE_NAME) {
        this.RECEIVE_NAME = RECEIVE_NAME;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUNIT() {
        return UNIT;
    }

    public void setUNIT(String UNIT) {
        this.UNIT = UNIT;
    }

    public String getAPPLYER() {
        return APPLYER;
    }

    public void setAPPLYER(String APPLYER) {
        this.APPLYER = APPLYER;
    }

    public String getAPPLYER_TEL() {
        return APPLYER_TEL;
    }

    public void setAPPLYER_TEL(String APPLYER_TEL) {
        this.APPLYER_TEL = APPLYER_TEL;
    }

    public String getAPPLYER_ADD() {
        return APPLYER_ADD;
    }

    public void setAPPLYER_ADD(String APPLYER_ADD) {
        this.APPLYER_ADD = APPLYER_ADD;
    }

    public void setMATERIAL_NAME(String MATERIAL_NAME) {
        this.MATERIAL_NAME = MATERIAL_NAME;
    }

    public void setMATERIAL_SPEC(String MATERIAL_SPEC) {
        this.MATERIAL_SPEC = MATERIAL_SPEC;
    }

    public void setSY(String SY) {
        this.SY = SY;
    }

    public void setORDER_NO(String ORDER_NO) {
        this.ORDER_NO = ORDER_NO;
    }

    public void setMATERIAL_CODE(String MATERIAL_CODE) {
        this.MATERIAL_CODE = MATERIAL_CODE;
    }

    public String getORDER_NO() {
        return ORDER_NO;
    }

    public String getMATERIAL_CODE() {
        return MATERIAL_CODE;
    }

    public String getMATERIAL_NAME() {
        return MATERIAL_NAME;
    }

    public String getMATERIAL_SPEC() {
        return MATERIAL_SPEC;
    }

    public String getSY() {
        return SY;
    }

    public void setAPPLY_COUNT(String APPLY_COUNT) {
        this.APPLY_COUNT = APPLY_COUNT;
    }

    public String getAPPLY_COUNT() {
        return APPLY_COUNT;
    }

    }


