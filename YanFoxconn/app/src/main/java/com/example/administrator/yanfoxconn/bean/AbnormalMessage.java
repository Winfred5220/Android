package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 異常信息
 * Created by song on 2017/9/14.
 */

public class AbnormalMessage implements Serializable {
    private String sc_id="";
    private String exce_id="";
    private String exce_add="";
    private String exce_time="";
    private String exce_desp="";
    private String exce_create_date="";
    private String exce_creator="";
    private List<FileName> file;

    private String content="";//異常點檢項

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSc_id() {
        return sc_id;
    }

    public void setSc_id(String sc_id) {
        this.sc_id = sc_id;
    }

    public String getExce_id() {
        return exce_id;
    }

    public void setExce_id(String exce_id) {
        this.exce_id = exce_id;
    }

    public String getExce_add() {
        return exce_add;
    }

    public void setExce_add(String exce_add) {
        this.exce_add = exce_add;
    }

    public String getExce_time() {
        return exce_time;
    }

    public void setExce_time(String exce_time) {
        this.exce_time = exce_time;
    }

    public String getExce_desp() {
        return exce_desp;
    }

    public void setExce_desp(String exce_desp) {
        this.exce_desp = exce_desp;
    }

    public String getExce_create_date() {
        return exce_create_date;
    }

    public void setExce_create_date(String exce_create_date) {
        this.exce_create_date = exce_create_date;
    }

    public String getExce_creator() {
        return exce_creator;
    }

    public void setExce_creator(String exce_creator) {
        this.exce_creator = exce_creator;
    }

    public List<FileName> getFile() {
        return file;
    }

    public void setFile(List<FileName> file) {
        this.file = file;
    }
}
