package com.example.administrator.yanfoxconn.bean;

/**
 * Created by Song
 * on 2020/9/29
 * Description：
 */
public class FHMessage  {

    private String dim_id;//二維碼主鍵
    private String dim_type;//類型
    private String dim_locale;//餐廳名稱
    private String dim_rate;//頻率
    private String dim_unit;//週期
    private String count;//已點檢次數

    public String getDim_id() {
        return dim_id;
    }

    public void setDim_id(String dim_id) {
        this.dim_id = dim_id;
    }

    public String getDim_type() {
        return dim_type;
    }

    public void setDim_type(String dim_type) {
        this.dim_type = dim_type;
    }

    public String getDim_locale() {
        return dim_locale;
    }

    public void setDim_locale(String dim_locale) {
        this.dim_locale = dim_locale;
    }

    public String getDim_rate() {
        return dim_rate;
    }

    public void setDim_rate(String dim_rate) {
        this.dim_rate = dim_rate;
    }

    public String getDim_unit() {
        return dim_unit;
    }

    public void setDim_unit(String dim_unit) {
        this.dim_unit = dim_unit;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
