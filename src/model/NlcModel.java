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
public class NlcModel {

    private String ref_no;
    private double disc_per;
    private double extra_support;
    private double activation;
    private double backend;
    private double prize_drop;
    private double cnAmount;
    private String ac_cd;
    private String remark;

    public double getBackend() {
        return backend;
    }

    public void setBackend(double backend) {
        this.backend = backend;
    }

    public String getRef_no() {
        return ref_no;
    }

    public void setRef_no(String ref_no) {
        this.ref_no = ref_no;
    }

    public double getDisc_per() {
        return disc_per;
    }

    public void setDisc_per(double disc_per) {
        this.disc_per = disc_per;
    }

    public double getExtra_support() {
        return extra_support;
    }

    public void setExtra_support(double extra_support) {
        this.extra_support = extra_support;
    }

    public double getActivation() {
        return activation;
    }

    public void setActivation(double activation) {
        this.activation = activation;
    }

    public double getPrize_drop() {
        return prize_drop;
    }

    public void setPrize_drop(double prize_drop) {
        this.prize_drop = prize_drop;
    }

    public double getCnAmount() {
        return cnAmount;
    }

    public void setCnAmount(double cnAmount) {
        this.cnAmount = cnAmount;
    }

    public String getAc_cd() {
        return ac_cd;
    }

    public void setAc_cd(String ac_cd) {
        this.ac_cd = ac_cd;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
