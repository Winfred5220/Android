package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 被巡檢單位列表
 * Created by wang on 2019/8/6.
 */

public class DepartListMessage implements Serializable {
    private String id;//主鍵
    private String area;//區域
    private String tung;//樓棟
    private String floor;//樓層
    private String product;//產品處
    private String technology;//工藝
    private String checkDate;//檢查日期
    private String supervisor;//主管
    private String inspector;//巡查人
    private String caseName;//專案名稱




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTung() {
        return tung;
    }

    public void setTung(String tung) {
        this.tung = tung;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getInspector() {
        return inspector;
    }

    public void setInspector(String inspector) {
        this.inspector = inspector;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }
}
