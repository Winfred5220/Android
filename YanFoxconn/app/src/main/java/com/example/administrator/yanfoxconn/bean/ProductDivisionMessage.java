package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 產品處信息
 * Created by wang on 2019/4/19.
 */

public class ProductDivisionMessage implements Serializable {
    private String name;
    private String role;
    private int imageId;

    public ProductDivisionMessage(String name,String role,int imageId){
        this.name = name;
        this.role = role;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
