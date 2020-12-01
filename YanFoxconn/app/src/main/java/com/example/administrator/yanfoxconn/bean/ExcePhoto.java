package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * Created by Song
 * on 2020/7/31
 * Description：公共點檢獲取圖片   或   人資監餐獲取圖片
 */
public class ExcePhoto implements Serializable {
    private String file;//公共點檢獲取圖片

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    private String EXCE_ID;//掃描主鍵 sc_id
    private String EXCE_ITEM;//項次
    private String DIM_TYPE;//類別
    private String EXCE_FILENAME1;//圖片64碼
    private String exce_filename1;

    public String getExce_filename1() {
        return exce_filename1;
    }

    public void setExce_filename1(String exce_filename1) {
        this.exce_filename1 = exce_filename1;
    }

    public String getEXCE_ID() {
        return EXCE_ID;
    }

    public void setEXCE_ID(String EXCE_ID) {
        this.EXCE_ID = EXCE_ID;
    }

    public String getEXCE_ITEM() {
        return EXCE_ITEM;
    }

    public void setEXCE_ITEM(String EXCE_ITEM) {
        this.EXCE_ITEM = EXCE_ITEM;
    }

    public String getDIM_TYPE() {
        return DIM_TYPE;
    }

    public void setDIM_TYPE(String DIM_TYPE) {
        this.DIM_TYPE = DIM_TYPE;
    }

    public String getEXCE_FILENAME1() {
        return EXCE_FILENAME1;
    }

    public void setEXCE_FILENAME1(String EXCE_FILENAME1) {
        this.EXCE_FILENAME1 = EXCE_FILENAME1;
    }
}
