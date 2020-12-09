package com.example.administrator.yanfoxconn.bean;

public class GCBody {
    private String In_Random_Id;//
    private String T_Id;//單身編號
    private String T_Description;//
    private String T_Tempature;//
    private String T_Createor_time;//
    private String In_Status;//是否刪除 D刪
    private String now_date;//

    public String getIn_Random_Id() {
        return In_Random_Id;
    }

    public String getIn_Status() {
        return In_Status;
    }

    public String getNow_date() {
        return now_date;
    }

    public String getT_Createor_time() {
        return T_Createor_time;
    }

    public String getT_Description() {
        return T_Description;
    }

    public String getT_Id() {
        return T_Id;
    }

    public String getT_Tempature() {
        return T_Tempature;
    }

    public void setIn_Random_Id(String in_Random_Id) {
        In_Random_Id = in_Random_Id;
    }

    public void setIn_Status(String in_Status) {
        In_Status = in_Status;
    }

    public void setNow_date(String now_date) {
        this.now_date = now_date;
    }

    public void setT_Createor_time(String t_Createor_time) {
        T_Createor_time = t_Createor_time;
    }

    public void setT_Description(String t_Description) {
        T_Description = t_Description;
    }

    public void setT_Id(String t_Id) {
        T_Id = t_Id;
    }

    public void setT_Tempature(String t_Tempature) {
        T_Tempature = t_Tempature;
    }
}
