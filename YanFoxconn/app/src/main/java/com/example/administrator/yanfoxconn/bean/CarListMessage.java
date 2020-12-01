package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 碼頭放行列表 車輛信息
 * Created by song on 2018/5/25.
 */

public class CarListMessage implements Serializable {
    private String packing_no;//銷單號
    private String truckno;//車牌號
    private String buildingno;//碼頭號
    private String container;//櫃號
    private String status;//狀態

    public String getPacking_no() {
        return packing_no;
    }

    public void setPacking_no(String packing_no) {
        this.packing_no = packing_no;
    }

    public String getTruckno() {
        return truckno;
    }

    public void setTruckno(String truckno) {
        this.truckno = truckno;
    }

    public String getBuildingno() {
        return buildingno;
    }

    public void setBuildingno(String buildingno) {
        this.buildingno = buildingno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }
}
