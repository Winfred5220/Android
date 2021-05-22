package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 活動發佈表
 * Created by S1007989 on 2021/05/21.
 */

public class ActivityMsg implements Serializable {
    //活動信息
    private String act_id;//主鍵單號
    private String act_type;//活動類別 ：個人賽 團體賽
    private String act_name;//活動名稱
    private String act_rules;//活動規則
    private String act_award;//獎品設置
    private String act_time_start;//活動開始時間
    private String act_end_sign;//報名截止時間
    private String act_num_team;//隊伍上限數量
    private String act_num_person;//活動上限人數
    private String act_time_end;//活動結束時間
    private String act_num_now;//現場活動人數
    private String create_code;//組織人工號
    private String create_name;//組織人姓名
    private String create_date;//維護時間
    private String act_statue;//活動狀態

    //單身表
    private String B_ID;//主鍵
    private String B_DESC;//描述
    private String B_REDEEM;//挽回損失
    private String B_LOGIN_CODE;//處警工號
    private String B_LOGIN_NAME;//處警人
    private String B_CREATE_DATE;//創建時間

    public String getAct_id() {
        return act_id;
    }

    public void setAct_id(String act_id) {
        this.act_id = act_id;
    }

    public String getAct_type() {
        return act_type;
    }

    public void setAct_type(String act_type) {
        this.act_type = act_type;
    }

    public String getAct_name() {
        return act_name;
    }

    public void setAct_name(String act_name) {
        this.act_name = act_name;
    }

    public String getAct_rules() {
        return act_rules;
    }

    public void setAct_rules(String act_rules) {
        this.act_rules = act_rules;
    }

    public String getAct_award() {
        return act_award;
    }

    public void setAct_award(String act_award) {
        this.act_award = act_award;
    }

    public String getAct_time_start() {
        return act_time_start;
    }

    public void setAct_time_start(String act_time_start) {
        this.act_time_start = act_time_start;
    }

    public String getAct_end_sign() {
        return act_end_sign;
    }

    public void setAct_end_sign(String act_end_sign) {
        this.act_end_sign = act_end_sign;
    }

    public String getAct_num_team() {
        return act_num_team;
    }

    public void setAct_num_team(String act_num_team) {
        this.act_num_team = act_num_team;
    }

    public String getAct_num_person() {
        return act_num_person;
    }

    public void setAct_num_person(String act_num_person) {
        this.act_num_person = act_num_person;
    }

    public String getAct_time_end() {
        return act_time_end;
    }

    public void setAct_time_end(String act_time_end) {
        this.act_time_end = act_time_end;
    }

    public String getAct_num_now() {
        return act_num_now;
    }

    public void setAct_num_now(String act_num_now) {
        this.act_num_now = act_num_now;
    }

    public String getCreate_code() {
        return create_code;
    }

    public void setCreate_code(String create_code) {
        this.create_code = create_code;
    }

    public String getCreate_name() {
        return create_name;
    }

    public void setCreate_name(String create_name) {
        this.create_name = create_name;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getAct_statue() {
        return act_statue;
    }

    public void setAct_statue(String act_statue) {
        this.act_statue = act_statue;
    }
}

