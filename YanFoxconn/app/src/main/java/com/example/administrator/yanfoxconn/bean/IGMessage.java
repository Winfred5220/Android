package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 宿舍 基本信息
 * @Author song
 * @Date 2021/3/30 16:51
 */
public class IGMessage implements Serializable {
    private String REGULAREMP_NO;//工號
    private String REGULAREMP_NAME;//姓名
    private String REGULAREMP_DEPARTMENT;//部門
    private String REGULAREMP_BEDID;//床位
    private String REGULAREMP_TEL;//電話

    private String ST_NAME;//物品種類


    private String S_ID;//申請單號
    private String S_USER_ID;//工號
    private String S_USER_NAME;//姓名
    private String S_USER_DEP;//部門
    private String S_CREATE_DATE;//創建時間
    private String S_ZW_APPLYER;//總務人員
    private String S_STATUS;//狀態，申請還是已排配
    private String S_LEAVE_FLAG;//是否離職

    private String sh_code;//倉庫編碼

    private List<BodyONE> body1;//物品及數量

    /**儲位變更 獲取儲位信息**/
    private String S_DEPOSIT_NAME;//物品類型
    private String SL_CODE;//儲位編號
    private String ID;//id
    /**倉庫盤點 獲取倉庫信息**/
    private String ZY;//在用
    private String KZ;//
    private String YC;//
    private String SI_CREATE_DATE;//上次盤點日期

    public String getKZ() {
        return KZ;
    }

    public String getSI_CREATE_DATE() {
        return SI_CREATE_DATE;
    }

    public String getYC() {
        return YC;
    }

    public String getZY() {
        return ZY;
    }

    public void setKZ(String KZ) {
        this.KZ = KZ;
    }

    public void setSI_CREATE_DATE(String SI_CREATE_DATE) {
        this.SI_CREATE_DATE = SI_CREATE_DATE;
    }

    public void setZY(String ZY) {
        this.ZY = ZY;
    }

    public void setYC(String YC) {
        this.YC = YC;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSL_CODE() {
        return SL_CODE;
    }

    public void setSL_CODE(String SL_CODE) {
        this.SL_CODE = SL_CODE;
    }

    public String getS_DEPOSIT_NAME() {
        return S_DEPOSIT_NAME;
    }

    public void setS_DEPOSIT_NAME(String s_DEPOSIT_NAME) {
        S_DEPOSIT_NAME = s_DEPOSIT_NAME;
    }


    public List<BodyONE> getBody1() {
        return body1;
    }

    public void setBody1(List<BodyONE> body1) {
        this.body1 = body1;
    }

    public String getSh_code() {
        return sh_code;
    }

    public void setSh_code(String sh_code) {
        this.sh_code = sh_code;
    }

    public String getS_CREATE_DATE() {
        return S_CREATE_DATE;
    }

    public String getS_ID() {
        return S_ID;
    }

    public String getS_LEAVE_FLAG() {
        return S_LEAVE_FLAG;
    }

    public String getS_STATUS() {
        return S_STATUS;
    }

    public String getS_USER_DEP() {
        return S_USER_DEP;
    }

    public String getS_USER_NAME() {
        return S_USER_NAME;
    }

    public String getS_USER_ID() {
        return S_USER_ID;
    }

    public String getS_ZW_APPLYER() {
        return S_ZW_APPLYER;
    }

    public void setS_CREATE_DATE(String s_CREATE_DATE) {
        S_CREATE_DATE = s_CREATE_DATE;
    }

    public void setS_ID(String s_ID) {
        S_ID = s_ID;
    }

    public void setS_LEAVE_FLAG(String s_LEAVE_FLAG) {
        S_LEAVE_FLAG = s_LEAVE_FLAG;
    }

    public void setS_STATUS(String s_STATUS) {
        S_STATUS = s_STATUS;
    }

    public void setS_USER_DEP(String s_USER_DEP) {
        S_USER_DEP = s_USER_DEP;
    }

    public void setS_USER_ID(String s_USER_ID) {
        S_USER_ID = s_USER_ID;
    }

    public void setS_USER_NAME(String s_USER_NAME) {
        S_USER_NAME = s_USER_NAME;
    }

    public void setS_ZW_APPLYER(String s_ZW_APPLYER) {
        S_ZW_APPLYER = s_ZW_APPLYER;
    }


    public String getST_NAME() {
        return ST_NAME;
    }

    public void setST_NAME(String ST_NAME) {
        this.ST_NAME = ST_NAME;
    }

    public String getREGULAREMP_BEDID() {
        return REGULAREMP_BEDID;
    }

    public String getREGULAREMP_DEPARTMENT() {
        return REGULAREMP_DEPARTMENT;
    }

    public String getREGULAREMP_NAME() {
        return REGULAREMP_NAME;
    }

    public String getREGULAREMP_NO() {
        return REGULAREMP_NO;
    }

    public String getREGULAREMP_TEL() {
        return REGULAREMP_TEL;
    }

    public void setREGULAREMP_BEDID(String REGULAREMP_BEDID) {
        this.REGULAREMP_BEDID = REGULAREMP_BEDID;
    }

    public void setREGULAREMP_DEPARTMENT(String REGULAREMP_DEPARTMENT) {
        this.REGULAREMP_DEPARTMENT = REGULAREMP_DEPARTMENT;
    }

    public void setREGULAREMP_NAME(String REGULAREMP_NAME) {
        this.REGULAREMP_NAME = REGULAREMP_NAME;
    }

    public void setREGULAREMP_NO(String REGULAREMP_NO) {
        this.REGULAREMP_NO = REGULAREMP_NO;
    }

    public void setREGULAREMP_TEL(String REGULAREMP_TEL) {
        this.REGULAREMP_TEL = REGULAREMP_TEL;
    }
}
