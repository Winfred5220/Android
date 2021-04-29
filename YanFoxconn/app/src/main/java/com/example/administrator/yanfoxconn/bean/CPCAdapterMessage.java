package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 成品倉出貨adapter數據處理使用類
 */
public class CPCAdapterMessage implements Serializable {
    private String num;//放行數量
    private String unit;//放行單位
    private int zbNum;//棧板數量
    private int xNum;//箱數
    private int pcsNum;//PCS數
    private int releaseCount;//已放行數量
    private String releaseUnit;//已放行單位
    private String isConfirmOk;//是否確認ok


    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getZbNum() {
        return zbNum;
    }

    public void setZbNum(int zbNum) {
        this.zbNum = zbNum;
    }

    public int getxNum() {
        return xNum;
    }

    public void setxNum(int xNum) {
        this.xNum = xNum;
    }


    public int getReleaseCount() {
        return releaseCount;
    }

    public void setReleaseCount(int releaseCount) {
        this.releaseCount = releaseCount;
    }


    public String getReleaseUnit() {
        return releaseUnit;
    }

    public void setReleaseUnit(String releaseUnit) {
        this.releaseUnit = releaseUnit;
    }

    public String getIsConfirmOk() {
        return isConfirmOk;
    }

    public void setIsConfirmOk(String isConfirmOk) {
        this.isConfirmOk = isConfirmOk;
    }

    public int getPcsNum() {
        return pcsNum;
    }

    public void setPcsNum(int pcsNum) {
        this.pcsNum = pcsNum;
    }

}
