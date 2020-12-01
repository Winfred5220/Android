package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * 碼頭派車單掃描信息
 * Created by song on 2017/11/24.
 */

public class CarScan implements Serializable {
    private String packing_no;//銷單號
    private String cn_board;//車牌號
    private String dri_name;//司機
    private String forwarder_name;//Forearder
    private String bookingno;//S/O號碼
    private String packingquantity11;//棧板數
    private String packingquantity12;//箱數
    private String container;//櫃號
    private String sealer;//封條號
    private float boxgw;//箱皮重
    private String loadport;//起運港
    private String destination;//目的
    private String department;//產品處
    private String factory;//裝貨廠區
    private String building;//棟號
    private String buildingNo;//碼頭號
    private String term;//運輸方式

    public String getPacking_no() {
        return packing_no;
    }

    public void setPacking_no(String packing_no) {
        this.packing_no = packing_no;
    }

    public String getCn_board() {
        return cn_board;
    }

    public void setCn_board(String cn_board) {
        this.cn_board = cn_board;
    }

    public String getDri_name() {
        return dri_name;
    }

    public void setDri_name(String dri_name) {
        this.dri_name = dri_name;
    }

    public String getForwarder_name() {
        return forwarder_name;
    }

    public void setForwarder_name(String forwarder_name) {
        this.forwarder_name = forwarder_name;
    }

    public String getBookingno() {
        return bookingno;
    }

    public void setBookingno(String bookingno) {
        this.bookingno = bookingno;
    }

    public String getPackingquantity11() {
        return packingquantity11;
    }

    public void setPackingquantity11(String packingquantity11) {
        this.packingquantity11 = packingquantity11;
    }

    public String getPackingquantity12() {
        return packingquantity12;
    }

    public void setPackingquantity12(String packingquantity12) {
        this.packingquantity12 = packingquantity12;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getSealer() {
        return sealer;
    }

    public void setSealer(String sealer) {
        this.sealer = sealer;
    }

    public float getBoxgw() {
        return boxgw;
    }

    public void setBoxgw(float boxgw) {
        this.boxgw = boxgw;
    }

    public String getLoadport() {
        return loadport;
    }

    public void setLoadport(String loadport) {
        this.loadport = loadport;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
