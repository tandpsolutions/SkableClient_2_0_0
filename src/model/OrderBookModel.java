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
public class OrderBookModel {

    private String ref_no;
    private String vdate;
    private String user_id;
    private String ac_cd;
    private double amt;
    private String remark;
    private String model_cd;
    private String memory_cd;
    private String color_cd;
    private double CASH_AMT;
    private String BANK_CD;
    private String BANK_NAME;
    private String BANK_BRANCH;
    private String CHEQUE_NO;
    private String CHEQUE_DATE;
    private double BANK_AMT;
    private String CARD_NAME;
    private double CARD_AMT;
    private double CARD_PER;
    private double CARD_CHG;
    private String BAJAJ_NAME;
    private double BAJAJ_AMT;
    private double BAJAJ_PER;
    private double BAJAJ_CHG;
    private String SFID;
    private String card_no;
    private String tid_no;
    private String branch_cd;

    public String getBranch_cd() {
        return branch_cd;
    }

    public void setBranch_cd(String branch_cd) {
        this.branch_cd = branch_cd;
    }
    
    

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getTid_no() {
        return tid_no;
    }

    public void setTid_no(String tid_no) {
        this.tid_no = tid_no;
    }

    public double getCASH_AMT() {
        return CASH_AMT;
    }

    public void setCASH_AMT(double CASH_AMT) {
        this.CASH_AMT = CASH_AMT;
    }

    public String getBANK_CD() {
        return BANK_CD;
    }

    public void setBANK_CD(String BANK_CD) {
        this.BANK_CD = BANK_CD;
    }

    public String getBANK_NAME() {
        return BANK_NAME;
    }

    public void setBANK_NAME(String BANK_NAME) {
        this.BANK_NAME = BANK_NAME;
    }

    public String getBANK_BRANCH() {
        return BANK_BRANCH;
    }

    public void setBANK_BRANCH(String BANK_BRANCH) {
        this.BANK_BRANCH = BANK_BRANCH;
    }

    public String getCHEQUE_NO() {
        return CHEQUE_NO;
    }

    public void setCHEQUE_NO(String CHEQUE_NO) {
        this.CHEQUE_NO = CHEQUE_NO;
    }

    public String getCHEQUE_DATE() {
        return CHEQUE_DATE;
    }

    public void setCHEQUE_DATE(String CHEQUE_DATE) {
        this.CHEQUE_DATE = CHEQUE_DATE;
    }

    public double getBANK_AMT() {
        return BANK_AMT;
    }

    public void setBANK_AMT(double BANK_AMT) {
        this.BANK_AMT = BANK_AMT;
    }

    public String getCARD_NAME() {
        return CARD_NAME;
    }

    public void setCARD_NAME(String CARD_NAME) {
        this.CARD_NAME = CARD_NAME;
    }

    public double getCARD_AMT() {
        return CARD_AMT;
    }

    public void setCARD_AMT(double CARD_AMT) {
        this.CARD_AMT = CARD_AMT;
    }

    public double getCARD_PER() {
        return CARD_PER;
    }

    public void setCARD_PER(double CARD_PER) {
        this.CARD_PER = CARD_PER;
    }

    public double getCARD_CHG() {
        return CARD_CHG;
    }

    public void setCARD_CHG(double CARD_CHG) {
        this.CARD_CHG = CARD_CHG;
    }

    public String getBAJAJ_NAME() {
        return BAJAJ_NAME;
    }

    public void setBAJAJ_NAME(String BAJAJ_NAME) {
        this.BAJAJ_NAME = BAJAJ_NAME;
    }

    public double getBAJAJ_AMT() {
        return BAJAJ_AMT;
    }

    public void setBAJAJ_AMT(double BAJAJ_AMT) {
        this.BAJAJ_AMT = BAJAJ_AMT;
    }

    public double getBAJAJ_PER() {
        return BAJAJ_PER;
    }

    public void setBAJAJ_PER(double BAJAJ_PER) {
        this.BAJAJ_PER = BAJAJ_PER;
    }

    public double getBAJAJ_CHG() {
        return BAJAJ_CHG;
    }

    public void setBAJAJ_CHG(double BAJAJ_CHG) {
        this.BAJAJ_CHG = BAJAJ_CHG;
    }

    public String getSFID() {
        return SFID;
    }

    public void setSFID(String SFID) {
        this.SFID = SFID;
    }

    public String getRef_no() {
        return ref_no;
    }

    public void setRef_no(String ref_no) {
        this.ref_no = ref_no;
    }

    public String getVdate() {
        return vdate;
    }

    public void setVdate(String vdate) {
        this.vdate = vdate;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAc_cd() {
        return ac_cd;
    }

    public void setAc_cd(String ac_cd) {
        this.ac_cd = ac_cd;
    }

    public double getAmt() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getModel_cd() {
        return model_cd;
    }

    public void setModel_cd(String model_cd) {
        this.model_cd = model_cd;
    }

    public String getMemory_cd() {
        return memory_cd;
    }

    public void setMemory_cd(String memory_cd) {
        this.memory_cd = memory_cd;
    }

    public String getColor_cd() {
        return color_cd;
    }

    public void setColor_cd(String color_cd) {
        this.color_cd = color_cd;
    }
}
