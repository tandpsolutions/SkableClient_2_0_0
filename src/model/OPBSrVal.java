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
public class OPBSrVal {

    private String sr_cd;
    private int sr_no;
    private String tag_no;
    private String imei;
    private String serial;
    private double p_rate;
    private String branch_cd;
    private String ref_no;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSr_cd() {
        return sr_cd;
    }

    public void setSr_cd(String sr_cd) {
        this.sr_cd = sr_cd;
    }

    public int getSr_no() {
        return sr_no;
    }

    public void setSr_no(int sr_no) {
        this.sr_no = sr_no;
    }

    public String getTag_no() {
        return tag_no;
    }

    public void setTag_no(String tag_no) {
        this.tag_no = tag_no;
    }

    public double getP_rate() {
        return p_rate;
    }

    public void setP_rate(double p_rate) {
        this.p_rate = p_rate;
    }

    public String getBranch_cd() {
        return branch_cd;
    }

    public void setBranch_cd(String branch_cd) {
        this.branch_cd = branch_cd;
    }

    public String getRef_no() {
        return ref_no;
    }

    public void setRef_no(String ref_no) {
        this.ref_no = ref_no;
    }

}
