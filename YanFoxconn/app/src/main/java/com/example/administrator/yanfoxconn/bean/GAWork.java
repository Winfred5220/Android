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
