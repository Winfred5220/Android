package com.example.administrator.yanfoxconn.bean;

/**
 * Created by Song
 * on 2020/8/22
 * Description：人資監餐 類型 基本資料
 *              總務臨時工 異常類別
 */
public class DZFoodType {
    private String name2;
    private String id;
    private String name3;
    private String score;

    private String t_item;
    private String t_name;

    public String getT_item() {
        return t_item;
    }

    public String getT_name() {
        return t_name;
    }

    public void setT_item(String t_item) {
        this.t_item = t_item;
    }

    public void setT_name(String t_name) {
        this.t_name = t_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }
}
