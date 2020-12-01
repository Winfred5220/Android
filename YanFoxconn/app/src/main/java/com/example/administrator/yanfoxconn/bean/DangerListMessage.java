package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 隱患列表
 * Created by wang on 2019/8/22.
 */

public class DangerListMessage implements Serializable {
    private String id;//主鍵
    private String caseId;//專案Id
    private String type;//隱患類型
    private String dangerImage;//隱患圖片
    private String describe;//隱患描述
    private String address;//位置
    private String product;//責任單位
    private String supervisor;//責任主管
    private String owner;//棟主
    private String person;//陪查人
    private String checkDate;//時間
    private String signImage;//確認人簽名圖片

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDangerImage() {
        return dangerImage;
    }

    public void setDangerImage(String dangerImage) {
        this.dangerImage = dangerImage;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getSignImage() {
        return signImage;
    }

    public void setSignImage(String signImage) {
        this.signImage = signImage;
    }
}
