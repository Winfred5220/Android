package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 行車日誌 詳情
 * Created by song on 2017/12/15.
 */

public class CarLogMessage implements Serializable {
    private String xc_id;//主鍵
    private String xc_item;//項次
    private String start_time;//起始時間
    private String end_time;//結束時間
    private String xc_start;//起始里程
    private String xc_end;//結束里程
    private String start_add;//起點
    private String end_add;//終點
    private String mid_add;//途徑
    private String road_fee;//過橋費
    private String part_fee;//停車費
    private String delay_eat;//誤餐費
    private String xc_remark;//備註
    private String filename;//圖片
    private String xc_create_date;//維護日期

    public String getXc_id() {
        return xc_id;
    }

    public void setXc_id(String xc_id) {
        this.xc_id = xc_id;
    }

    public String getXc_item() {
        return xc_item;
    }

    public void setXc_item(String xc_item) {
        this.xc_item = xc_item;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getXc_start() {
        return xc_start;
    }

    public void setXc_start(String xc_start) {
        this.xc_start = xc_start;
    }

    public String getXc_end() {
        return xc_end;
    }

    public void setXc_end(String xc_end) {
        this.xc_end = xc_end;
    }

    public String getStart_add() {
        return start_add;
    }

    public void setStart_add(String start_add) {
        this.start_add = start_add;
    }

    public String getEnd_add() {
        return end_add;
    }

    public void setEnd_add(String end_add) {
        this.end_add = end_add;
    }

    public String getMid_add() {
        return mid_add;
    }

    public void setMid_add(String mid_add) {
        this.mid_add = mid_add;
    }

    public String getRoad_fee() {
        return road_fee;
    }

    public void setRoad_fee(String road_fee) {
        this.road_fee = road_fee;
    }

    public String getPart_fee() {
        return part_fee;
    }

    public void setPart_fee(String part_fee) {
        this.part_fee = part_fee;
    }

    public String getDelay_eat() {
        return delay_eat;
    }

    public void setDelay_eat(String delay_eat) {
        this.delay_eat = delay_eat;
    }

    public String getXc_remark() {
        return xc_remark;
    }

    public void setXc_remark(String xc_remark) {
        this.xc_remark = xc_remark;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getXc_create_date() {
        return xc_create_date;
    }

    public void setXc_create_date(String xc_create_date) {
        this.xc_create_date = xc_create_date;
    }
}
