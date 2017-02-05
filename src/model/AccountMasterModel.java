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
public class AccountMasterModel {

    private String AC_CD;
    private String FNAME;
    private String GROUP_NAME;
    private String GRP_CD;
    private String CST;
    private String TIN;
    private String ADD1;
    private String MOBILE1;
    private String EMAIL;
    private String CARD_NO;
    private double OPB_AMT;
    private int OPB_EFF;
    private String ref_by;
    private String ref_cd;
    
    public String getRef_cd() {
        return ref_cd;
    }

    public void setRef_cd(String ref_cd) {
        this.ref_cd = ref_cd;
    }


    public double getOPB_AMT() {
        return OPB_AMT;
    }

    public void setOPB_AMT(double OPB_AMT) {
        this.OPB_AMT = OPB_AMT;
    }

    public int getOPB_EFF() {
        return OPB_EFF;
    }

    public void setOPB_EFF(int OPB_EFF) {
        this.OPB_EFF = OPB_EFF;
    }

    public String getCARD_NO() {
        return CARD_NO;
    }

    public void setCARD_NO(String CARD_NO) {
        this.CARD_NO = CARD_NO;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getAC_CD() {
        return AC_CD;
    }

    public void setAC_CD(String AC_CD) {
        this.AC_CD = AC_CD;
    }

    public String getFNAME() {
        return FNAME;
    }

    public void setFNAME(String FNAME) {
        this.FNAME = FNAME;
    }

    public String getGROUP_NAME() {
        return GROUP_NAME;
    }

    public void setGROUP_NAME(String GROUP_NAME) {
        this.GROUP_NAME = GROUP_NAME;
    }

    public String getGRP_CD() {
        return GRP_CD;
    }

    public void setGRP_CD(String GRP_CD) {
        this.GRP_CD = GRP_CD;
    }

    public String getCST() {
        return CST;
    }

    public void setCST(String CST) {
        this.CST = CST;
    }

    public String getTIN() {
        return TIN;
    }

    public void setTIN(String TIN) {
        this.TIN = TIN;
    }

    public String getADD1() {
        return ADD1;
    }

    public void setADD1(String ADD1) {
        this.ADD1 = ADD1;
    }

    public String getMOBILE1() {
        return MOBILE1;
    }

    public void setMOBILE1(String MOBILE1) {
        this.MOBILE1 = MOBILE1;
    }

    public String getRef_by() {
        return ref_by;
    }

    public void setRef_by(String ref_by) {
        this.ref_by = ref_by;
    }

    
}
