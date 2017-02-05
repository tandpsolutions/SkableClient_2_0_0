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
public class BankPaymentReceiptModel {

    private String ref_no;
    private String vdate;
    private String user_id;
    private String ac_cd;
    private String bank_cd;
    private String cheque_date;
    private String cheque_no;
    private double amt;
    private String doc_ref_no;
    private String remark;
    private int type;
    private double tot_amt;
    private String branch_cd;
    private String party_bank_name;

    public String getParty_bank_name() {
        return party_bank_name;
    }

    public void setParty_bank_name(String party_bank_name) {
        this.party_bank_name = party_bank_name;
    }

    public String getBranch_cd() {
        return branch_cd;
    }

    public void setBranch_cd(String branch_cd) {
        this.branch_cd = branch_cd;
    }

    public String getBank_cd() {
        return bank_cd;
    }

    public void setBank_cd(String bank_cd) {
        this.bank_cd = bank_cd;
    }

    public String getCheque_date() {
        return cheque_date;
    }

    public void setCheque_date(String cheque_date) {
        this.cheque_date = cheque_date;
    }

    public String getCheque_no() {
        return cheque_no;
    }

    public void setCheque_no(String cheque_no) {
        this.cheque_no = cheque_no;
    }

    public String getDoc_ref_no() {
        return doc_ref_no;
    }

    public void setDoc_ref_no(String doc_ref_no) {
        this.doc_ref_no = doc_ref_no;
    }

    public double getTot_amt() {
        return tot_amt;
    }

    public void setTot_amt(double tot_amt) {
        this.tot_amt = tot_amt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

}
