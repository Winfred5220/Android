package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;
import java.util.List;

/**
 * create by song
 * on 2020/2/28
 * 消殺查驗列表項
 */
public class CzKillCheckLv implements Serializable {
    private String XS_CREATE_DATE ="";//日期
    private String XS_RECORDER="";//維護人
    private String XS_TW="";//體溫
    private String XS_REMARK="";//備註
    private List<FileName> photo;//照片

    public String getXS_CREATE_DATE() {
        return XS_CREATE_DATE;
    }

    public void setXS_CREATE_DATE(String XS_CREATE_DATE) {
        this.XS_CREATE_DATE = XS_CREATE_DATE;
    }

    public String getXS_RECORDER() {
        return XS_RECORDER;
    }

    public void setXS_RECORDER(String XS_RECORDER) {
        this.XS_RECORDER = XS_RECORDER;
    }

    public String getXS_TW() {
        return XS_TW;
    }

    public void setXS_TW(String XS_TW) {
        this.XS_TW = XS_TW;
    }

    public String getXS_REMARK() {
        return XS_REMARK;
    }

    public void setXS_REMARK(String XS_REMARK) {
        this.XS_REMARK = XS_REMARK;
    }

    public List<FileName> getPhoto() {
        return photo;
    }

    public void setPhoto(List<FileName> photo) {
        this.photo = photo;
    }
}
