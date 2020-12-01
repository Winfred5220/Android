package com.example.administrator.yanfoxconn.bean;

import java.util.List;

/**
 * 跨區無紙化 掃描信息
 * Created by wang on 2019/5/10.
 */

public class CrossScanMessage {

    public String BCQ01;//單號
    public String BCQ00;//流向
    public String BCQ03;//箱門數
    public String BCQ22;//出發地
    public String BCQ23;//目的地
    public String BCQ09;//種類
    public String BCQ08;//件數
    public String BCQ06;//攜帶者工號
    public String BCQ12;//貨物名稱
    public String BCQ13;//驗放崗位
    public String BCQ14;//收貨崗位
    public String BCQ182;//封號

    public List<CrossScanList> bcrList;//列表

    public String getBCQ01() {
        return BCQ01;
    }

    public void setBCQ01(String BCQ01) {
        this.BCQ01 = BCQ01;
    }

    public String getBCQ00() {
        return BCQ00;
    }

    public void setBCQ00(String BCQ00) {
        this.BCQ00 = BCQ00;
    }

    public String getBCQ03() {
        return BCQ03;
    }

    public void setBCQ03(String BCQ03) {
        this.BCQ03 = BCQ03;
    }

    public String getBCQ22() {
        return BCQ22;
    }

    public void setBCQ22(String BCQ22) {
        this.BCQ22 = BCQ22;
    }

    public String getBCQ23() {
        return BCQ23;
    }

    public void setBCQ23(String BCQ23) {
        this.BCQ23 = BCQ23;
    }

    public String getBCQ09() {
        return BCQ09;
    }

    public void setBCQ09(String BCQ09) {
        this.BCQ09 = BCQ09;
    }

    public String getBCQ08() {
        return BCQ08;
    }

    public void setBCQ08(String BCQ08) {
        this.BCQ08 = BCQ08;
    }

    public String getBCQ06() {
        return BCQ06;
    }

    public void setBCQ06(String BCQ06) {
        this.BCQ06 = BCQ06;
    }

    public String getBCQ12() {
        return BCQ12;
    }

    public void setBCQ12(String BCQ12) {
        this.BCQ12 = BCQ12;
    }

    public String getBCQ13() {
        return BCQ13;
    }

    public void setBCQ13(String BCQ13) {
        this.BCQ13 = BCQ13;
    }

    public String getBCQ14() {
        return BCQ14;
    }

    public void setBCQ14(String BCQ14) {
        this.BCQ14 = BCQ14;
    }

    public String getBCQ182() {
        return BCQ182;
    }

    public void setBCQ182(String BCQ182) {
        this.BCQ182 = BCQ182;
    }

    public List<CrossScanList> getBcrList() {
        return bcrList;
    }

    public void setBcrList(List<CrossScanList> bcrList) {
        this.bcrList = bcrList;
    }
}
