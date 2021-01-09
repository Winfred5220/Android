package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 營建工程管理 GT營建,GU安保部,GV工業安全,GW產品處
 * @Author song
 * @Date 2021/1/5 15:11
 */
public class GTMain implements Serializable {
    private String id;//點檢類別
    private String name;//點檢名稱
    private String file;//點檢圖片
    private String project_no;//工程編號
    private String project_name;//工程名稱
    private String win_vendor;//中標廠商
    private String area;//區
    private String building;//位置
    private String supplier;//廠商負責人
    private String expect_enddate;//預計完成時間
    private List<GTMain> gtMainBtns;//點檢按鈕

    public String getId() {
        return id;
    }

    public List<GTMain> getGtMainBtns() {
        return gtMainBtns;
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

    public String getFile() {
        return file;
    }

    public String getName() {
        return name;
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

    public void setId(String id) {
        this.id = id;
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

    public void setFile(String file) {
        this.file = file;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGtMainBtns(List<GTMain> gtMainBtns) {
        this.gtMainBtns = gtMainBtns;
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
