package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 人資活動 查看人員加班時間
 * Created by song on 2018/7/26.
 */

public class EventTime implements Serializable {
    String sc_creator_id;//工號
    String sc_creator;//姓名
    String time_mm;//時間

    public String getSc_creator_id() {
        return sc_creator_id;
    }

    public void setSc_creator_id(String sc_creator_id) {
        this.sc_creator_id = sc_creator_id;
    }

    public String getSc_creator() {
        return sc_creator;
    }

    public void setSc_creator(String sc_creator) {
        this.sc_creator = sc_creator;
    }

    public String getTime_mm() {
        return time_mm;
    }

    public void setTime_mm(String time_mm) {
        this.time_mm = time_mm;
    }
}
