package com.example.administrator.yanfoxconn.bean;

/**
 * Created by wangqian on 2019/2/28.
 */

public class ForkliftMessage {

    //叉車信息
    private String area;//區域
    private String bumen;//部門
    private String chepai;//車牌號
    private String bianhao;//車架號
    private String xinghao;//車型
    private String dunwei;//噸位
    private String sc_date;//車輛生產日期
    private String voltage;//電壓
    private String capacity;//電瓶容量
    private String hy_enddate;//合約期限
    private String start_date;//開始時間
    private String end_date;//結束時間
    //巡檢
    private String id;//id
    private String flag;//標誌
    private String item1;//部位
    private String item2;//檢查項目
    private String isnormal;//正常異常
    private String abnormal;//異常問題
    private String message;//填寫信息
    //維修
    private String danhao;//單號
    private String hours;//小時數
    private String address;//位置
    private String bx_date;//報修時間
    private String bx_name;//報修人
    private String bx_tel;//聯繫方式
    private String question;//故障問題
    private String wish_date;//期望完成時間
    private String user_or;//是否影響使用

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getDanhao() {
        return danhao;
    }

    public void setDanhao(String danhao) {
        this.danhao = danhao;
    }

    public String getBx_date() {
        return bx_date;
    }

    public void setBx_date(String bx_date) {
        this.bx_date = bx_date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBx_name() {
        return bx_name;
    }

    public void setBx_name(String bx_name) {
        this.bx_name = bx_name;
    }

    public String getBx_tel() {
        return bx_tel;
    }

    public void setBx_tel(String bx_tel) {
        this.bx_tel = bx_tel;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getWish_date() {
        return wish_date;
    }

    public void setWish_date(String wish_date) {
        this.wish_date = wish_date;
    }

    public String getUser_or() {
        return user_or;
    }

    public void setUser_or(String user_or) {
        this.user_or = user_or;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getItem1() {
        return item1;
    }

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public String getItem2() {
        return item2;
    }

    public void setItem2(String item2) {
        this.item2 = item2;
    }

    public String getAbnormal() {
        return abnormal;
    }

    public void setAbnormal(String abnormal) {
        this.abnormal = abnormal;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIsnormal() {
        return isnormal;
    }

    public void setIsnormal(String isnormal) {
        this.isnormal = isnormal;
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

    public String getChepai() {
        return chepai;
    }

    public void setChepai(String chepai) {
        this.chepai = chepai;
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

    public String getDunwei() {
        return dunwei;
    }

    public void setDunwei(String dunwei) {
        this.dunwei = dunwei;
    }

    public String getSc_date() {
        return sc_date;
    }

    public void setSc_date(String sc_date) {
        this.sc_date = sc_date;
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

    public String getHy_enddate() {
        return hy_enddate;
    }

    public void setHy_enddate(String hy_enddate) {
        this.hy_enddate = hy_enddate;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }


}
