package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 巡檢點 信息類
 * Created by song on 2017/8/30.
 */

public class DNCheckMessage implements Serializable {
    private String jc_id;//单号
    private String jc_area;//区
    private String jc_building;//樓
    private String jc_floor;//層
    private String jc_room;//房間
    private String jc_bed;//床
    private List<DNSpinner> jc_result;//稽查結果(下拉選項)
    private String jc_remarks;//稽查備註
    private String jc_date;//稽查日期
    private String jc_flag;//稽查類別.人員或環境
    private String emp_no;//工號
    private String emp_name;//姓名
    private String create_date;//提交

    private String REGULAREMP_BEDNUM;//床号
    private String REGULAREMP_NO;//工號
    private String REGULAREMP_NAME;//姓名
    private String REGULAREMP_CHANPIN;//產品處
    private String REGULAREMP_DEPARTMENT;//部門
    private String REGULAREMP_BANBIE;//班別

    private List<FileName> fileName;//圖片

    public List<FileName> getFileName() {
        return fileName;
    }

    public void setFileName(List<FileName> fileName) {
        this.fileName = fileName;
    }

    public String getREGULAREMP_CHANPIN() {
        return REGULAREMP_CHANPIN;
    }

    public void setREGULAREMP_CHANPIN(String REGULAREMP_CHANPIN) {
        this.REGULAREMP_CHANPIN = REGULAREMP_CHANPIN;
    }

    public String getREGULAREMP_DEPARTMENT() {
        return REGULAREMP_DEPARTMENT;
    }

    public void setREGULAREMP_DEPARTMENT(String REGULAREMP_DEPARTMENT) {
        this.REGULAREMP_DEPARTMENT = REGULAREMP_DEPARTMENT;
    }

    public String getJc_id() {
        return jc_id;
    }

    public void setJc_id(String jc_id) {
        this.jc_id = jc_id;
    }

    public String getJc_area() {
        return jc_area;
    }

    public void setJc_area(String jc_area) {
        this.jc_area = jc_area;
    }

    public String getJc_building() {
        return jc_building;
    }

    public void setJc_building(String jc_building) {
        this.jc_building = jc_building;
    }

    public String getJc_floor() {
        return jc_floor;
    }

    public void setJc_floor(String jc_floor) {
        this.jc_floor = jc_floor;
    }

    public String getJc_room() {
        return jc_room;
    }

    public void setJc_room(String jc_room) {
        this.jc_room = jc_room;
    }

    public String getJc_bed() {
        return jc_bed;
    }

    public void setJc_bed(String jc_bed) {
        this.jc_bed = jc_bed;
    }

    public List<DNSpinner> getJc_result() {
        return jc_result;
    }

    public void setJc_result(List<DNSpinner> jc_result) {
        this.jc_result = jc_result;
    }

    public String getJc_remarks() {
        return jc_remarks;
    }

    public void setJc_remarks(String jc_remarks) {
        this.jc_remarks = jc_remarks;
    }

    public String getJc_date() {
        return jc_date;
    }

    public void setJc_date(String jc_date) {
        this.jc_date = jc_date;
    }

    public String getJc_flag() {
        return jc_flag;
    }

    public void setJc_flag(String jc_flag) {
        this.jc_flag = jc_flag;
    }

    public String getEmp_no() {
        return emp_no;
    }

    public void setEmp_no(String emp_no) {
        this.emp_no = emp_no;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getREGULAREMP_BEDNUM() {
        return REGULAREMP_BEDNUM;
    }

    public void setREGULAREMP_BEDNUM(String REGULAREMP_BEDNUM) {
        this.REGULAREMP_BEDNUM = REGULAREMP_BEDNUM;
    }

    public String getREGULAREMP_NO() {
        return REGULAREMP_NO;
    }

    public void setREGULAREMP_NO(String REGULAREMP_NO) {
        this.REGULAREMP_NO = REGULAREMP_NO;
    }

    public String getREGULAREMP_NAME() {
        return REGULAREMP_NAME;
    }

    public void setREGULAREMP_NAME(String REGULAREMP_NAME) {
        this.REGULAREMP_NAME = REGULAREMP_NAME;
    }

    public String getREGULAREMP_BANBIE() {
        return REGULAREMP_BANBIE;
    }

    public void setREGULAREMP_BANBIE(String REGULAREMP_BANBIE) {
        this.REGULAREMP_BANBIE = REGULAREMP_BANBIE;
    }
}
