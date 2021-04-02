package com.example.administrator.yanfoxconn.bean;

/**
 * @Description 宿舍寄存 儲位信息
 * @Author song
 * @Date 2021/4/1 15:25
 */
public class BodyTWO {
    private String ID;//編號
    private String S_BID;//類別編號
    private String SH_CODE;//倉庫
    private String SL_CODE;//儲位
    private String S_STATUS;//狀態

    public String getID() {
        return ID;
    }

    public String getS_STATUS() {
        return S_STATUS;
    }

    public String getS_BID() {
        return S_BID;
    }

    public String getSH_CODE() {
        return SH_CODE;
    }

    public String getSL_CODE() {
        return SL_CODE;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setS_STATUS(String s_STATUS) {
        S_STATUS = s_STATUS;
    }

    public void setS_BID(String s_BID) {
        S_BID = s_BID;
    }

    public void setSH_CODE(String SH_CODE) {
        this.SH_CODE = SH_CODE;
    }

    public void setSL_CODE(String SL_CODE) {
        this.SL_CODE = SL_CODE;
    }

}
