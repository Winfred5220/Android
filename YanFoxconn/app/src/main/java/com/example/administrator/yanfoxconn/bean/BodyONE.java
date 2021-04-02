package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 宿舍寄存 物品種類和數量
 * @Author song
 * @Date 2021/4/1 15:22
 */
public class BodyONE implements Serializable {
    private String ID;//編號
    private String S_ID;//申請單號
    private String S_DEPOSIT_NAME;//物品種類
    private String S_DEPOSIT_COUNT;//物品數量
    private List<BodyTWO> body2;//儲位

    public String getS_ID() {
        return S_ID;
    }

    public List<BodyTWO> getBody2() {
        return body2;
    }

    public String getID() {
        return ID;
    }

    public String getS_DEPOSIT_COUNT() {
        return S_DEPOSIT_COUNT;
    }

    public String getS_DEPOSIT_NAME() {
        return S_DEPOSIT_NAME;
    }

    public void setS_ID(String s_ID) {
        S_ID = s_ID;
    }

    public void setBody2(List<BodyTWO> body2) {
        this.body2 = body2;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setS_DEPOSIT_COUNT(String s_DEPOSIT_COUNT) {
        S_DEPOSIT_COUNT = s_DEPOSIT_COUNT;
    }

    public void setS_DEPOSIT_NAME(String s_DEPOSIT_NAME) {
        S_DEPOSIT_NAME = s_DEPOSIT_NAME;
    }
}
