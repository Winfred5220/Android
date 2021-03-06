package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 營建工程管理 GT營建,GU安保部,GV工業安全,GW產品處
 * @Author song
 * @Date 2021/1/5 15:11
 */
public class GTMain implements Serializable {

    private String project_no;//工程編號
    private String project_name;//工程名稱
    private String win_vendor;//中標廠商
    private String area;//區
    private String building;//位置
    private String supplier;//廠商負責人
    private String expect_enddate;//預計完成時間
    private String progress;//進度
    private String today_work;//今日工作內容
    private String cpc;//申請單位
    private int count;//點檢次數

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCpc() {
        return cpc;
    }

    public void setCpc(String cpc) {
        this.cpc = cpc;
    }


    private List<GTMainBtn> gtMainBtns;//點檢按鈕

    public List<GTMainBtn> getGtMainBtns() {
        return gtMainBtns;
    }

    public void setGtMainBtns(List<GTMainBtn> gtMainBtns) {
        this.gtMainBtns = gtMainBtns;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getToday_work() {
        return today_work;
    }

    public void setToday_work(String today_work) {
        this.today_work = today_work;
    }

    public String getArea() {
        return area;
    }

    public String getBuilding() {
        return building;
    }

    public String getExpect_enddate() {
        return expect_enddate;
    }

    public String getProject_name() {
        return project_name;
    }

    public String getProject_no() {
        return project_no;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getWin_vendor() {
        return win_vendor;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setExpect_enddate(String expect_enddate) {
        this.expect_enddate = expect_enddate;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public void setProject_no(String project_no) {
        this.project_no = project_no;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public void setWin_vendor(String win_vendor) {
        this.win_vendor = win_vendor;
    }

}
