package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 圖片文件名稱
 * Created by song on 2017/9/14.
 */

public class FileName implements Serializable {
    private String exce_filename1;
    private String exce_filename2;
    private String REC_NAME;

    public String getExce_filename1() {
        return exce_filename1;
    }

    public void setExce_filename1(String exce_filename1) {
        this.exce_filename1 = exce_filename1;
    }

    public String getExce_filename2() {
        return exce_filename2;
    }

    public void setExce_filename2(String exce_filename2) {
        this.exce_filename2 = exce_filename2;
    }

    public String getREC_NAME() {
        return REC_NAME;
    }

    public void setREC_NAME(String REC_NAME) {
        this.REC_NAME = REC_NAME;
    }
}
