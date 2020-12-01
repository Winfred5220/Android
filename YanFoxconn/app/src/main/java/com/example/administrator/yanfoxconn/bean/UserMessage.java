package com.example.administrator.yanfoxconn.bean;

/**
 * 用戶信息
 * Created by song on 2017/9/21.
 */

public class UserMessage {
    private String xg_id;//工號
    private String xg_name;//姓名
    private String xg_role;//巡檢人的角色權限
    private String xg_state;//在職狀態
    private String xg_dep;//部門
    private String xg_phone;//手機號
    private String xg_tel;//工作電話
    private String need_flag;//是否需要維護巡檢人員,Y需要,N不需要
    private String need_result;//是否已維護過巡檢人員,Y已維護過,N沒有維護過



    public String getXg_id() {
        return xg_id;
    }

    public void setXg_id(String xg_id) {
        this.xg_id = xg_id;
    }

    public String getXg_name() {
        return xg_name;
    }

    public void setXg_name(String xg_name) {
        this.xg_name = xg_name;
    }

    public String getXg_role() {
        return xg_role;
    }

    public void setXg_role(String xg_role) {
        this.xg_role = xg_role;
    }

    public String getXg_state() {
        return xg_state;
    }

    public void setXg_state(String xg_state) {
        this.xg_state = xg_state;
    }

    public String getXg_dep() {
        return xg_dep;
    }

    public void setXg_dep(String xg_dep) {
        this.xg_dep = xg_dep;
    }

    public String getXg_phone() {
        return xg_phone;
    }

    public void setXg_phone(String xg_phone) {
        this.xg_phone = xg_phone;
    }

    public String getXg_tel() {
        return xg_tel;
    }

    public void setXg_tel(String xg_tel) {
        this.xg_tel = xg_tel;
    }

    public String getNeed_flag() {
        return need_flag;
    }

    public void setNeed_flag(String need_flag) {
        this.need_flag = need_flag;
    }

    public String getNeed_result() {
        return need_result;
    }

    public void setNeed_result(String need_result) {
        this.need_result = need_result;
    }
}
