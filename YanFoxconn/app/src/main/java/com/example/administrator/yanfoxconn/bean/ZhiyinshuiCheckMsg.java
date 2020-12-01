package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 直飲水點檢信息
 * Created by wang on 2020/07/18.
 */

public class ZhiyinshuiCheckMsg implements Serializable {
    private String id;//主鍵
    private String dim_id;//二維碼主鍵
    private String item;//排序
    private String content;//點檢內容
    private String type;//類型input、radio、checkbox
    private String flag;//input標誌 N/Y
    private String max;//input最大值
    private String min;//input最小值
    private String status;//狀態
    private String dim_type;//類別

    private List<Options> option;//選項

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDim_id() {
        return dim_id;
    }

    public void setDim_id(String dim_id) {
        this.dim_id = dim_id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDim_type() {
        return dim_type;
    }

    public void setDim_type(String dim_type) {
        this.dim_type = dim_type;
    }

    public List<Options> getOption() {
        return option;
    }

    public void setOption(List<Options> option) {
        this.option = option;
    }



}
