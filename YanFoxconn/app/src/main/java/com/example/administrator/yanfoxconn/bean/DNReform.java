package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Song
 * on 2020/10/20
 * Description：宿舍查驗 異常整改列表item
 */
public class DNReform implements Serializable {
    private String jc_id;
    private String jc_room;
    private String jc_bed;
    private List<DNSpinner> jc_result;
    private String jc_date;
    private String emp_name;


    public String getJc_id() {
        return jc_id;
    }

    public void setJc_id(String jc_id) {
        this.jc_id = jc_id;
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

    public String getJc_date() {
        return jc_date;
    }

    public void setJc_date(String jc_date) {
        this.jc_date = jc_date;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }
}
