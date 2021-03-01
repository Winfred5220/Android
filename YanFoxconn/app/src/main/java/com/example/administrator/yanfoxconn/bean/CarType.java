package com.example.administrator.yanfoxconn.bean;

public class CarType {
    private String type;//類別
    private String name;//名稱

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CarType{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
