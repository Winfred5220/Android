package com.example.administrator.yanfoxconn.bean;


/**
 * Created by wangqian on 2018/12/21.
 */

public class GoodsMessage {
    //普通物品
    private String OUT00;//單號
    private String OUT02;//日期
    private String OUT03;//流出地
    private String OUT04;//接收單位
    private String OUT11A;//流通方式
    private String OUTLOCK;//當值警衛

    private String OUTD01;//品名
    private String OUTD02;//型號
    private String OUTD03;//數量
    private String OUTD04;//單位

    //智慧物品
    private String DOCUMENT_NO;//單號
    private String BRING_OUT;//攜出標誌
    private String OUT_FLAG;//流通方式
    private String TAKETO_PLACE;//攜出流向
    private String OUT_STDATE;//攜出開始時間
    private String OUT_ENDDATE;//攜出結束時間
    private String BRING_IN;//攜入標誌
    private String TAKEIN_PLACE;//攜入地點
    private String IN_STDATE;//攜入開始時間
    private String IN_ENDDATE;//攜入結束時間

    private String EQNAME;//品名
    private String BRAND;//品牌
    private String EQCOUNT;//數量
    private String REMARKS;//單位
    private String EQCOLOR;//顏色
    private String MAKENUMBER;//MAC地址
    private String EQTYPE;//序列號/SN
    private String WIRELESS;//無線網卡


    public String getOUTD01() {
        return OUTD01;
    }

    public void setOUTD01(String OUTD01) {
        this.OUTD01 = OUTD01;
    }

    public String getOUTD02() {
        return OUTD02;
    }

    public void setOUTD02(String OUTD02) {
        this.OUTD02 = OUTD02;
    }

    public String getOUTD03() {
        return OUTD03;
    }

    public void setOUTD03(String OUTD03) {
        this.OUTD03 = OUTD03;
    }

    public String getOUTD04() {return OUTD04;}

    public void setOUTD04(String OUTD04) {
        this.OUTD04 = OUTD04;
    }

    public String getOUT00() {
        return OUT00;
    }

    public void setOUT00(String OUT00) {
        this.OUT00 = OUT00;
    }

    public String getOUT02() {
        return OUT02;
    }

    public void setOUT02(String OUT02) {
        this.OUT02 = OUT02;
    }

    public String getOUT03() {
        return OUT03;
    }

    public void setOUT03(String OUT03) {
        this.OUT03 = OUT03;
    }

    public String getOUT04() {
        return OUT04;
    }

    public void setOUT04(String OUT04) {
        this.OUT04 = OUT04;
    }

    public String getOUT11A() {
        return OUT11A;
    }

    public void setOUT11A(String OUT11A) {
        this.OUT11A = OUT11A;
    }

    public String getOUTLOCK() {
        return OUTLOCK;
    }

    public void setOUTLOCK(String OUTLOCK) {
        this.OUTLOCK = OUTLOCK;
    }

    public String getDOCUMENT_NO() {
        return DOCUMENT_NO;
    }

    public void setDOCUMENT_NO(String DOCUMENT_NO) {
        this.DOCUMENT_NO = DOCUMENT_NO;
    }

    public String getBRING_OUT() {
        return BRING_OUT;
    }

    public void setBRING_OUT(String BRING_OUT) {
        this.BRING_OUT = BRING_OUT;
    }

    public String getOUT_FLAG() {
        return OUT_FLAG;
    }

    public void setOUT_FLAG(String OUT_FLAG) {
        this.OUT_FLAG = OUT_FLAG;
    }

    public String getTAKETO_PLACE() {
        return TAKETO_PLACE;
    }

    public void setTAKETO_PLACE(String TAKETO_PLACE) {
        this.TAKETO_PLACE = TAKETO_PLACE;
    }

    public String getOUT_STDATE() {
        return OUT_STDATE;
    }

    public void setOUT_STDATE(String OUT_STDATE) {
        this.OUT_STDATE = OUT_STDATE;
    }

    public String getOUT_ENDDATE() {
        return OUT_ENDDATE;
    }

    public void setOUT_ENDDATE(String OUT_ENDDATE) {
        this.OUT_ENDDATE = OUT_ENDDATE;
    }

    public String getBRING_IN() {
        return BRING_IN;
    }

    public void setBRING_IN(String BRING_IN) {
        this.BRING_IN = BRING_IN;
    }

    public String getTAKEIN_PLACE() {
        return TAKEIN_PLACE;
    }

    public void setTAKEIN_PLACE(String TAKEIN_PLACE) {
        this.TAKEIN_PLACE = TAKEIN_PLACE;
    }

    public String getIN_STDATE() {
        return IN_STDATE;
    }

    public void setIN_STDATE(String IN_STDATE) {
        this.IN_STDATE = IN_STDATE;
    }

    public String getIN_ENDDATE() {
        return IN_ENDDATE;
    }

    public void setIN_ENDDATE(String IN_ENDDATE) {
        this.IN_ENDDATE = IN_ENDDATE;
    }

    public String getEQNAME() {
        return EQNAME;
    }

    public void setEQNAME(String EQNAME) {
        this.EQNAME = EQNAME;
    }

    public String getBRAND() {
        return BRAND;
    }

    public void setBRAND(String BRAND) {
        this.BRAND = BRAND;
    }

    public String getEQCOUNT() {
        return EQCOUNT;
    }

    public void setEQCOUNT(String EQCOUNT) {
        this.EQCOUNT = EQCOUNT;
    }

    public String getREMARKS() {
        return REMARKS;
    }

    public void setREMARKS(String REMARKS) {
        this.REMARKS = REMARKS;
    }

    public String getEQCOLOR() {
        return EQCOLOR;
    }

    public void setEQCOLOR(String EQCOLOR) {
        this.EQCOLOR = EQCOLOR;
    }

    public String getMAKENUMBER() {
        return MAKENUMBER;
    }

    public void setMAKENUMBER(String MAKENUMBER) {
        this.MAKENUMBER = MAKENUMBER;
    }

    public String getEQTYPE() {
        return EQTYPE;
    }

    public void setEQTYPE(String EQTYPE) {
        this.EQTYPE = EQTYPE;
    }

    public String getWIRELESS() {
        return WIRELESS;
    }

    public void setWIRELESS(String WIRELESS) {
        this.WIRELESS = WIRELESS;
    }
}