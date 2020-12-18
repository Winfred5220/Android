package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

public class GCHead implements Serializable {

    private String In_Random_Id;//單號
    private String In_Category;//人員類別
    private String In_Name;//姓名
    private String In_Sex;//性別
    private String In_Department;//部門
    private String In_Number;//工號
    private String In_Tempature;//初始體溫
    private String In_Door;//初始門崗
    private String In_Observation;//留觀地點

    public String getIn_Door() {
        return In_Door;
    }

    public String getIn_Observation() {
        return In_Observation;
    }

    public String getIn_Tempature() {
        return In_Tempature;
    }

    public void setIn_Door(String in_Door) {
        In_Door = in_Door;
    }

    public void setIn_Observation(String in_Observation) {
        In_Observation = in_Observation;
    }

    public void setIn_Tempature(String in_Tempature) {
        In_Tempature = in_Tempature;
    }


    public String getIn_Number() {
        return In_Number;
    }

    public void setIn_Number(String in_Number) {
        In_Number = in_Number;
    }

    public String getIn_Category() {
        return In_Category;
    }

    public String getIn_Department() {
        return In_Department;
    }

    public String getIn_Name() {
        return In_Name;
    }

    public String getIn_Random_Id() {
        return In_Random_Id;
    }

    public String getIn_Sex() {
        return In_Sex;
    }

    public void setIn_Category(String in_Category) {
        In_Category = in_Category;
    }

    public void setIn_Department(String in_Department) {
        In_Department = in_Department;
    }

    public void setIn_Random_Id(String in_Random_Id) {
        In_Random_Id = in_Random_Id;
    }

    public void setIn_Name(String in_Name) {
        In_Name = in_Name;
    }

    public void setIn_Sex(String in_Sex) {
        In_Sex = in_Sex;
    }

}
