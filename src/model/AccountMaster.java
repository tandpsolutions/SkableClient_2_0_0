/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author bhaumik
 */
public class AccountMaster {

    @SerializedName("AC_CD")
    @Expose
    private String ACCD;
    @SerializedName("AC_ALIAS")
    @Expose
    private String ACALIAS;
    @SerializedName("FNAME")
    @Expose
    private String FNAME;
    @SerializedName("MNAME")
    @Expose
    private String MNAME;
    @SerializedName("LNAME")
    @Expose
    private String LNAME;
    @SerializedName("TIN")
    @Expose
    private String TIN;
    @SerializedName("ADD1")
    @Expose
    private String ADD1;
    @SerializedName("MOBILE1")
    @Expose
    private String MOBILE1;
    @SerializedName("BAL")
    @Expose
    private double BAL;
    @SerializedName("ref_by")
    @Expose
    private String ref_by;

    @SerializedName("EMAIL")
    @Expose
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return The ACCD
     */
    public String getACCD() {
        return ACCD;
    }

    /**
     *
     * @param ACCD The AC_CD
     */
    public void setACCD(String ACCD) {
        this.ACCD = ACCD;
    }

    /**
     *
     * @return The ACALIAS
     */
    public String getACALIAS() {
        return ACALIAS;
    }

    /**
     *
     * @param ACALIAS The AC_ALIAS
     */
    public void setACALIAS(String ACALIAS) {
        this.ACALIAS = ACALIAS;
    }

    /**
     *
     * @return The FNAME
     */
    public String getFNAME() {
        return FNAME;
    }

    /**
     *
     * @param FNAME The FNAME
     */
    public void setFNAME(String FNAME) {
        this.FNAME = FNAME;
    }

    /**
     *
     * @return The MNAME
     */
    public String getMNAME() {
        return MNAME;
    }

    /**
     *
     * @param MNAME The MNAME
     */
    public void setMNAME(String MNAME) {
        this.MNAME = MNAME;
    }

    /**
     *
     * @return The LNAME
     */
    public String getLNAME() {
        return LNAME;
    }

    /**
     *
     * @param LNAME The LNAME
     */
    public void setLNAME(String LNAME) {
        this.LNAME = LNAME;
    }

    /**
     *
     * @return The TIN
     */
    public String getTIN() {
        return TIN;
    }

    /**
     *
     * @param TIN The TIN
     */
    public void setTIN(String TIN) {
        this.TIN = TIN;
    }

    /**
     *
     * @return The ADD1
     */
    public String getADD1() {
        return ADD1;
    }

    /**
     *
     * @param ADD1 The ADD1
     */
    public void setADD1(String ADD1) {
        this.ADD1 = ADD1;
    }

    /**
     *
     * @return The MOBILE1
     */
    public String getMOBILE1() {
        return MOBILE1;
    }

    /**
     *
     * @param MOBILE1 The MOBILE1
     */
    public void setMOBILE1(String MOBILE1) {
        this.MOBILE1 = MOBILE1;
    }

    public double getBAL() {
        return BAL;
    }

    public void setBAL(double BAL) {
        this.BAL = BAL;
    }

    public String getRef_by() {
        return ref_by;
    }

    public void setRef_by(String ref_by) {
        this.ref_by = ref_by;
    }

}
