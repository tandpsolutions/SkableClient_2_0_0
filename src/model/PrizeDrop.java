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
public class PrizeDrop {

    private String pur_tag_no;
    private int sr_no;
    private String voucher_no;
    private String v_date;
    private String remark;
    private double previous_rate;
    private double rate;
    private String ac_cd;

    public String getAc_cd() {
        return ac_cd;
    }

    public void setAc_cd(String ac_cd) {
        this.ac_cd = ac_cd;
    }

    public double getPrevious_rate() {
        return previous_rate;
    }

    public void setPrevious_rate(double previous_rate) {
        this.previous_rate = previous_rate;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getPur_tag_no() {
        return pur_tag_no;
    }

    public void setPur_tag_no(String pur_tag_no) {
        this.pur_tag_no = pur_tag_no;
    }

    public int getSr_no() {
        return sr_no;
    }

    public void setSr_no(int sr_no) {
        this.sr_no = sr_no;
    }

    public String getVoucher_no() {
        return voucher_no;
    }

    public void setVoucher_no(String voucher_no) {
        this.voucher_no = voucher_no;
    }

    public String getV_date() {
        return v_date;
    }

    public void setV_date(String v_date) {
        this.v_date = v_date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
