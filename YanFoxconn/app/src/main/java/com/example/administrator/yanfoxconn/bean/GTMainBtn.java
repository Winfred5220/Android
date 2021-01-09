package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 營建工程管理 GT營建,GU安保部,GV工業安全,GW產品處
 * @Author song
 * @Date 2021/1/5 15:11
 */
public class GTMainBtn implements Serializable {
    private String ctype;//點檢類別
    private String cname;//點檢名稱
    private String iconurl;//點檢圖片地址 可點擊
    private String iconurl_dis;//點檢圖片地址 不可點擊
    private String flag;//點檢類型

    public String getCname() {
        return cname;
    }

    public String getCtype() {
        return ctype;
    }

    public String getFlag() {
        return flag;
    }

    public String getIconurl() {
        return iconurl;
    }

    public String getIconurl_dis() {
        return iconurl_dis;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public void setIconurl_dis(String iconurl_dis) {
        this.iconurl_dis = iconurl_dis;
    }

}
