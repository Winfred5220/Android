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
    private String c_id;//b班别编号
    private String c_up_start;//职能
    private String c_up_end;//职能
    private String c_down_start;//职能
    private String c_down_end;//职能
    private String zg_no;//主管编号
    private String zg_name;//主管编号
    private String qj_id;//请假主键
    private String qj_start_date;//请假开始时间
    private String qj_end_date;//请假结束时间
    private String qj_time;//请假时长
    private String qj_reason;//请假原因
    private String qj_statue;//请假状态
    private String qh_date;//签核时间
    private String tj_statue;//退件状态
    private String tj_reason;//退件原因
    private String tj_date;//退件日期

    public String getZg_name() {
        return zg_name;
    }

    public void setZg_name(String zg_name) {
        this.zg_name = zg_name;
    }

    public String getQh_date() {
        return qh_date;
    }

    public String getQj_statue() {
        return qj_statue;
    }

    public String getTj_date() {
        return tj_date;
    }

    public String getTj_reason() {
        return tj_reason;
    }

    public String getTj_statue() {
        return tj_statue;
    }

    public void setQh_date(String qh_date) {
        this.qh_date = qh_date;
    }

    public void setQj_statue(String qj_statue) {
        this.qj_statue = qj_statue;
    }

    public void setTj_date(String tj_date) {
        this.tj_date = tj_date;
    }

    public void setTj_reason(String tj_reason) {
        this.tj_reason = tj_reason;
    }

    public void setTj_statue(String tj_statue) {
        this.tj_statue = tj_statue;
    }

    public String getQj_id() {
        return qj_id;
    }

    public void setQj_id(String qj_id) {
        this.qj_id = qj_id;
    }

    public String getQj_reason() {
        return qj_reason;
    }

    public void setQj_reason(String qj_reason) {
        this.qj_reason = qj_reason;
    }

    public String getQj_time() {
        return qj_time;
    }

    public void setQj_time(String qj_time) {
        this.qj_time = qj_time;
    }

    public String getQj_end_date() {
        return qj_end_date;
    }

    public String getQj_start_date() {
        return qj_start_date;
    }

    public void setQj_end_date(String qj_end_date) {
        this.qj_end_date = qj_end_date;
    }

    public void setQj_start_date(String qj_start_date) {
        this.qj_start_date = qj_start_date;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getZg_no() {
        return zg_no;
    }

    public void setZg_no(String zg_no) {
        this.zg_no = zg_no;
    }

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
