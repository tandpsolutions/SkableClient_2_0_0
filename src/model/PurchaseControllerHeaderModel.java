/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author bhaumik
 */
public class PurchaseControllerHeaderModel {

    private String ref_no;
    private int V_TYPE;
    private String V_DATE;
    private int PMT_MODE;
    private String BILL_DATE;
    private String BILL_NO;
    private String AC_CD;
    private double DET_TOT;
    private double TAX_AMT;
    private double ADD_TAX_AMT;
    private double ADJST;
    private double NET_AMT;
    private String USER_ID;
    private String REMARK;
    private double FRIEGHT_CHARGES;
    private String ac_name;
    private String address;
    private int BRANCH_CD;
    private String DUE_DATE;
    private int TAX_TYPE;

    public int getTAX_TYPE() {
        return TAX_TYPE;
    }

    public void setTAX_TYPE(int TAX_TYPE) {
        this.TAX_TYPE = TAX_TYPE;
    }

    public String getDUE_DATE() {
        return DUE_DATE;
    }

    public void setDUE_DATE(String DUE_DATE) {
        this.DUE_DATE = DUE_DATE;
    }

    public int getBRANCH_CD() {
        return BRANCH_CD;
    }

    public void setBRANCH_CD(int BRANCH_CD) {
        this.BRANCH_CD = BRANCH_CD;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAc_name() {
        return ac_name;
    }

    public void setAc_name(String ac_name) {
        this.ac_name = ac_name;
    }

    public double getFRIEGHT_CHARGES() {
        return FRIEGHT_CHARGES;
    }

    public void setFRIEGHT_CHARGES(double FRIEGHT_CHARGES) {
        this.FRIEGHT_CHARGES = FRIEGHT_CHARGES;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public String getRef_no() {
        return ref_no;
    }

    public void setRef_no(String ref_no) {
        this.ref_no = ref_no;
    }

    public int getV_TYPE() {
        return V_TYPE;
    }

    public void setV_TYPE(int V_TYPE) {
        this.V_TYPE = V_TYPE;
    }

    public String getV_DATE() {
        return V_DATE;
    }

    public void setV_DATE(String V_DATE) {
        this.V_DATE = V_DATE;
    }

    public int getPMT_MODE() {
        return PMT_MODE;
    }

    public void setPMT_MODE(int PMT_MODE) {
        this.PMT_MODE = PMT_MODE;
    }

    public String getBILL_DATE() {
        return BILL_DATE;
    }

    public void setBILL_DATE(String BILL_DATE) {
        this.BILL_DATE = BILL_DATE;
    }

    public String getBILL_NO() {
        return BILL_NO;
    }

    public void setBILL_NO(String BILL_NO) {
        this.BILL_NO = BILL_NO;
    }

    public String getAC_CD() {
        return AC_CD;
    }

    public void setAC_CD(String AC_CD) {
        this.AC_CD = AC_CD;
    }

    public double getDET_TOT() {
        return DET_TOT;
    }

    public void setDET_TOT(double DET_TOT) {
        this.DET_TOT = DET_TOT;
    }

    public double getTAX_AMT() {
        return TAX_AMT;
    }

    public void setTAX_AMT(double TAX_AMT) {
        this.TAX_AMT = TAX_AMT;
    }

    public double getADD_TAX_AMT() {
        return ADD_TAX_AMT;
    }

    public void setADD_TAX_AMT(double ADD_TAX_AMT) {
        this.ADD_TAX_AMT = ADD_TAX_AMT;
    }

    public double getADJST() {
        return ADJST;
    }

    public void setADJST(double ADJST) {
        this.ADJST = ADJST;
    }

    public double getNET_AMT() {
        return NET_AMT;
    }

    public void setNET_AMT(double NET_AMT) {
        this.NET_AMT = NET_AMT;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

}
