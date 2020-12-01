package com.example.administrator.yanfoxconn.bean;

import java.io.Serializable;

/**
 * Created by wangqian on 2019/2/28.
 * 車輛點檢
 */

public class CarCheckMessage implements Serializable {

    private String id;
    private String chepai;    //	6
    private String che_belong;    //	3
    private String che_company;    //
    private String che_brand;    //	4
    private String che_type;    //	5
    private String bianhao;    //	15
    private String xinghao;    //	8
    private String hezai;    //	17
    private String dunwei;    //	9
    private String fuel_type;    //	20
    private String voltage;    //	14
    private String capacity;    //	13
    private String sc_date;    //	10
    private String area;    //	1
    private String bumen;    //	2
    private String hy_startdate;    //	11
    private String hy_enddate;    //	11
    private String nianjian_date;    //	28
    private String register_date;    //	22
    private String useyears;    //	23
    private String scrap_date;    //	24
    private String consump;    //	21
    private String maintenance;    //	25
    private String driver_name;    //	18
    private String driver_years;    //	19
    private String insur_company;    //	26
    private String insur_startdate;    //	27
    private String insur_enddate;    //	27
    private String gas_date;    //	29

    private String status;//狀態
    //叉車：1,2,3,4,5,6,7,8,9,10,11,12,13,14
    //球車：1,2,4,5,6,8,17,10,14
    //車調：2,3,4,5,6,18,19,20,21,22,23,24,25,26,27,11,28,29


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChepai() {
        return chepai;
    }

    public void setChepai(String chepai) {
        this.chepai = chepai;
    }

    public String getChe_belong() {
        return che_belong;
    }

    public void setChe_belong(String che_belong) {
        this.che_belong = che_belong;
    }

    public String getChe_company() {
        return che_company;
    }

    public void setChe_company(String che_company) {
        this.che_company = che_company;
    }

    public String getChe_brand() {
        return che_brand;
    }

    public void setChe_brand(String che_brand) {
        this.che_brand = che_brand;
    }

    public String getChe_type() {
        return che_type;
    }

    public void setChe_type(String che_type) {
        this.che_type = che_type;
    }

    public String getBianhao() {
        return bianhao;
    }

    public void setBianhao(String bianhao) {
        this.bianhao = bianhao;
    }

    public String getXinghao() {
        return xinghao;
    }

    public void setXinghao(String xinghao) {
        this.xinghao = xinghao;
    }

    public String getHezai() {
        return hezai;
    }

    public void setHezai(String hezai) {
        this.hezai = hezai;
    }

    public String getDunwei() {
        return dunwei;
    }

    public void setDunwei(String dunwei) {
        this.dunwei = dunwei;
    }

    public String getFuel_type() {
        return fuel_type;
    }

    public void setFuel_type(String fuel_type) {
        this.fuel_type = fuel_type;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getSc_date() {
        return sc_date;
    }

    public void setSc_date(String sc_date) {
        this.sc_date = sc_date;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBumen() {
        return bumen;
    }

    public void setBumen(String bumen) {
        this.bumen = bumen;
    }

    public String getHy_startdate() {
        return hy_startdate;
    }

    public void setHy_startdate(String hy_startdate) {
        this.hy_startdate = hy_startdate;
    }

    public String getHy_enddate() {
        return hy_enddate;
    }

    public void setHy_enddate(String hy_enddate) {
        this.hy_enddate = hy_enddate;
    }

    public String getNianjian_date() {
        return nianjian_date;
    }

    public void setNianjian_date(String nianjian_date) {
        this.nianjian_date = nianjian_date;
    }

    public String getRegister_date() {
        return register_date;
    }

    public void setRegister_date(String register_date) {
        this.register_date = register_date;
    }

    public String getUseyears() {
        return useyears;
    }

    public void setUseyears(String useyears) {
        this.useyears = useyears;
    }

    public String getScrap_date() {
        return scrap_date;
    }

    public void setScrap_date(String scrap_date) {
        this.scrap_date = scrap_date;
    }

    public String getConsump() {
        return consump;
    }

    public void setConsump(String consump) {
        this.consump = consump;
    }

    public String getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(String maintenance) {
        this.maintenance = maintenance;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_years() {
        return driver_years;
    }

    public void setDriver_years(String driver_years) {
        this.driver_years = driver_years;
    }

    public String getInsur_company() {
        return insur_company;
    }

    public void setInsur_company(String insur_company) {
        this.insur_company = insur_company;
    }

    public String getInsur_startdate() {
        return insur_startdate;
    }

    public void setInsur_startdate(String insur_startdate) {
        this.insur_startdate = insur_startdate;
    }

    public String getInsur_enddate() {
        return insur_enddate;
    }

    public void setInsur_enddate(String insur_enddate) {
        this.insur_enddate = insur_enddate;
    }

    public String getGas_date() {
        return gas_date;
    }

    public void setGas_date(String gas_date) {
        this.gas_date = gas_date;
    }
}
