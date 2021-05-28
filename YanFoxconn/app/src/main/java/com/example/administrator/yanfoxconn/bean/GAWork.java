package com.example.administrator.yanfoxconn.bean;

/**
 * 2020/11/10
 * GA总务临时工 职能基本资料
 */
public class GAWork {
    private String g_id;
    private String g_name;//课组名称
    private String g_post;//职位
    private String g_duty;//职能
    private String p_empId;//职能
    private String p_empName;//职能
    private String c_type;//职能
    private String c_up_start;//职能
    private String c_up_end;//职能
    private String c_down_start;//职能
    private String c_down_end;//职能

    public String getC_down_end() {
        return c_down_end;
    }

    public String getC_type() {
        return c_type;
    }

    public String getP_empName() {
        return p_empName;
    }

    public String getC_up_start() {
        return c_up_start;
    }

    public String getP_empId() {
        return p_empId;
    }

    public String getC_down_start() {
        return c_down_start;
    }

    public String getC_up_end() {
        return c_up_end;
    }

    public void setC_down_end(String c_down_end) {
        this.c_down_end = c_down_end;
    }

    public void setC_down_start(String c_down_start) {
        this.c_down_start = c_down_start;
    }

    public void setC_type(String c_type) {
        this.c_type = c_type;
    }

    public void setC_up_end(String c_up_end) {
        this.c_up_end = c_up_end;
    }

    public void setC_up_start(String c_up_start) {
        this.c_up_start = c_up_start;
    }

    public void setP_empId(String p_empId) {
        this.p_empId = p_empId;
    }

    public void setP_empName(String p_empName) {
        this.p_empName = p_empName;
    }

    public String getG_id() {
        return g_id;
    }

    public String getG_duty() {
        return g_duty;
    }

    public String getG_name() {
        return g_name;
    }

    public String getG_post() {
        return g_post;
    }

    public void setG_duty(String g_duty) {
        this.g_duty = g_duty;
    }

    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public void setG_post(String g_post) {
        this.g_post = g_post;
    }

}
