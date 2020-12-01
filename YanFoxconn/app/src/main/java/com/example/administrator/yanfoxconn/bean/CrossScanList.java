package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 跨區無紙化 掃描信息 list
 * Created by wang on 2019/5/10.
 */

public class CrossScanList implements Serializable {
    private String BCR01;//單號
    private String BCR02;//項次
    private String BCR04;//品名
    private String BCR11;//單位
    private String BCR10;//數量
    private String QIANFENG;//封號

    public String getBCR01() {
        return BCR01;
    }

    public void setBCR01(String BCR01) {
        this.BCR01 = BCR01;
    }

    public String getQIANFENG() {
        return QIANFENG;
    }

    public void setQIANFENG(String QIANFENG) {
        this.QIANFENG = QIANFENG;
    }

    public String getBCR02() {
        return BCR02;
    }

    public void setBCR02(String BCR02) {
        this.BCR02 = BCR02;
    }

    public String getBCR04() {
        return BCR04;
    }

    public void setBCR04(String BCR04) {
        this.BCR04 = BCR04;
    }

    public String getBCR11() {
        return BCR11;
    }

    public void setBCR11(String BCR11) {
        this.BCR11 = BCR11;
    }

    public String getBCR10() {
        return BCR10;
    }

    public void setBCR10(String BCR10) {
        this.BCR10 = BCR10;
    }
}
