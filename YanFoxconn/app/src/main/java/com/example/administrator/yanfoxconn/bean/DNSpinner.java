package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * Created by song on 2020/4/10. 14:11
 */
public class DNSpinner implements Serializable {
    private String id;
    private String name;
    private String yc_flag;
    private String ksh_flag;
    private String ksh_yctype;
    private String photo_flag;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYc_flag() {
        return yc_flag;
    }

    public void setYc_flag(String yc_flag) {
        this.yc_flag = yc_flag;
    }

    public String getKsh_flag() {
        return ksh_flag;
    }

    public void setKsh_flag(String ksh_flag) {
        this.ksh_flag = ksh_flag;
    }

    public String getKsh_yctype() {
        return ksh_yctype;
    }

    public void setKsh_yctype(String ksh_yctype) {
        this.ksh_yctype = ksh_yctype;
    }

    public String getPhoto_flag() {
        return photo_flag;
    }

    public void setPhoto_flag(String photo_flag) {
        this.photo_flag = photo_flag;
    }
}
