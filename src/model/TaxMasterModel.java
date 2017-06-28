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
    @SerializedName("EDIT_NO")
    @Expose
    private String EDITNO;
    @SerializedName("USER_ID")
    @Expose
    private String USERID;
    @SerializedName("TIME_STAMP")
    @Expose
    private String TIMESTAMP;
    @SerializedName("IGST")
    @Expose
    private String IGST;

    public String getIGST() {
        return IGST;
    }

    public void setIGST(String IGST) {
        this.IGST = IGST;
    }

    public String getTAXCD() {
        return TAXCD;
    }

    public void setTAXCD(String TAXCD) {
        this.TAXCD = TAXCD;
    }

    public String getTAXNAME() {
        return TAXNAME;
    }

    public void setTAXNAME(String TAXNAME) {
        this.TAXNAME = TAXNAME;
    }

    public String getTAXPER() {
        return TAXPER;
    }

    public void setTAXPER(String TAXPER) {
        this.TAXPER = TAXPER;
    }

    public String getADDTAXPER() {
        return ADDTAXPER;
    }

    public void setADDTAXPER(String ADDTAXPER) {
        this.ADDTAXPER = ADDTAXPER;
    }

    public String getTAXONSALES() {
        return TAXONSALES;
    }

    public void setTAXONSALES(String TAXONSALES) {
        this.TAXONSALES = TAXONSALES;
    }

    public String getTAXACCD() {
        return TAXACCD;
    }

    public void setTAXACCD(String TAXACCD) {
        this.TAXACCD = TAXACCD;
    }

    public String getADDTAXACCD() {
        return ADDTAXACCD;
    }

    public void setADDTAXACCD(String ADDTAXACCD) {
        this.ADDTAXACCD = ADDTAXACCD;
    }

    public String getEDITNO() {
        return EDITNO;
    }

    public void setEDITNO(String EDITNO) {
        this.EDITNO = EDITNO;
    }

    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public String getTIMESTAMP() {
        return TIMESTAMP;
    }

    public void setTIMESTAMP(String TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }
}
