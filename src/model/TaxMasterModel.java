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
public class TaxMasterModel {

    @SerializedName("TAX_CD")
    @Expose
    private String TAXCD;
    @SerializedName("TAX_NAME")
    @Expose
    private String TAXNAME;
    @SerializedName("TAX_PER")
    @Expose
    private String TAXPER;
    @SerializedName("ADD_TAX_PER")
    @Expose
    private String ADDTAXPER;
    @SerializedName("TAX_ON_SALES")
    @Expose
    private String TAXONSALES;
    @SerializedName("TAX_AC_CD")
    @Expose
    private String TAXACCD;
    @SerializedName("ADD_TAX_AC_CD")
    @Expose
    private String ADDTAXACCD;

    /**
     *
     * @return The TAXCD
     */
    public String getTAXCD() {
        return TAXCD;
    }

    /**
     *
     * @param TAXCD The TAX_CD
     */
    public void setTAXCD(String TAXCD) {
        this.TAXCD = TAXCD;
    }

    /**
     *
     * @return The TAXNAME
     */
    public String getTAXNAME() {
        return TAXNAME;
    }

    /**
     *
     * @param TAXNAME The TAX_NAME
     */
    public void setTAXNAME(String TAXNAME) {
        this.TAXNAME = TAXNAME;
    }

    /**
     *
     * @return The TAXPER
     */
    public String getTAXPER() {
        return TAXPER;
    }

    /**
     *
     * @param TAXPER The TAX_PER
     */
    public void setTAXPER(String TAXPER) {
        this.TAXPER = TAXPER;
    }

    /**
     *
     * @return The ADDTAXPER
     */
    public String getADDTAXPER() {
        return ADDTAXPER;
    }

    /**
     *
     * @param ADDTAXPER The ADD_TAX_PER
     */
    public void setADDTAXPER(String ADDTAXPER) {
        this.ADDTAXPER = ADDTAXPER;
    }

    /**
     *
     * @return The TAXONSALES
     */
    public String getTAXONSALES() {
        return TAXONSALES;
    }

    /**
     *
     * @param TAXONSALES The TAX_ON_SALES
     */
    public void setTAXONSALES(String TAXONSALES) {
        this.TAXONSALES = TAXONSALES;
    }

    /**
     *
     * @return The TAXACCD
     */
    public String getTAXACCD() {
        return TAXACCD;
    }

    /**
     *
     * @param TAXACCD The TAX_AC_CD
     */
    public void setTAXACCD(String TAXACCD) {
        this.TAXACCD = TAXACCD;
    }

    /**
     *
     * @return The ADDTAXACCD
     */
    public String getADDTAXACCD() {
        return ADDTAXACCD;
    }

    /**
     *
     * @param ADDTAXACCD The ADD_TAX_AC_CD
     */
    public void setADDTAXACCD(String ADDTAXACCD) {
        this.ADDTAXACCD = ADDTAXACCD;
    }
}
