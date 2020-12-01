package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * Created by song on 2018/5/30.
 */

public class CarPictureMessage implements Serializable {
    private String  packing_no;//銷單號
    private String  filepath;//開箱照片
    private String  logopath;//關箱照片
    private String  shifengpath;//鉛封照片
    private String  remark8;//三角木
    private String  remark9;//派車單
    private String  remark10;//設備交接單

    public String getPacking_no() {
        return packing_no;
    }

    public void setPacking_no(String packing_no) {
        this.packing_no = packing_no;
    }

    public String getRemark8() {
        return remark8;
    }

    public void setRemark8(String remark8) {
        this.remark8 = remark8;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getLogopath() {
        return logopath;
    }

    public void setLogopath(String logopath) {
        this.logopath = logopath;
    }

    public String getShifengpath() {
        return shifengpath;
    }

    public void setShifengpath(String shifengpath) {
        this.shifengpath = shifengpath;
    }

    public String getRemark9() {
        return remark9;
    }

    public void setRemark9(String remark9) {
        this.remark9 = remark9;
    }

    public String getRemark10() {
        return remark10;
    }

    public void setRemark10(String remark10) {
        this.remark10 = remark10;
    }
}
