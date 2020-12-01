package com.example.administrator.yanfoxconn.bean;

/**
 * Created by wangqian on 2019/3/28.
 */

public class MobileMessage {
    public String DOCUMENT_NO;//單號
    public String AREA;//使用廠區及區域
    public String USERNO;//工號
    public String USERNAME;//姓名
    public String USERDEPM;//部門
    public String USERPS;//資位
    public String USERLC;//職位
    public String EQUIPMENT;//設備名稱
    public String ARDIT_DATE;//申請日期

    public String getDOCUMENT_NO() {
        return DOCUMENT_NO;
    }

    public String getAREA() {
        return AREA;
    }

    public String getUSERNO() {
        return USERNO;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public String getUSERDEPM() {
        return USERDEPM;
    }

    public String getUSERPS() {
        return USERPS;
    }

    public String getUSERLC() {
        return USERLC;
    }

    public String getEQUIPMENT() {
        return EQUIPMENT;
    }

    public String getARDIT_DATE() {
        return ARDIT_DATE;
    }
}
