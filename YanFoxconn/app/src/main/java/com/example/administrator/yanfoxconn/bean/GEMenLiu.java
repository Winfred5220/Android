package com.example.administrator.yanfoxconn.bean;

/**
 * 健康追蹤 門崗和留觀地點
 */
public class GEMenLiu {

    private String Id;//編號
    private String In_Door;//門崗 或 留觀地點
    private String Place_Flag;//標誌
    private String Createid;//
    private String Createor;//
    private String Createtime;//

    public String getCreateid() {
        return Createid;
    }

    public String getCreateor() {
        return Createor;
    }

    public String getCreatetime() {
        return Createtime;
    }

    public String getId() {
        return Id;
    }

    public String getIn_Door() {
        return In_Door;
    }

    public String getPlace_Flag() {
        return Place_Flag;
    }

    public void setCreateid(String createid) {
        Createid = createid;
    }

    public void setCreateor(String createor) {
        Createor = createor;
    }

    public void setCreatetime(String createtime) {
        Createtime = createtime;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setIn_Door(String in_Door) {
        In_Door = in_Door;
    }

    public void setPlace_Flag(String place_Flag) {
        Place_Flag = place_Flag;
    }

}
