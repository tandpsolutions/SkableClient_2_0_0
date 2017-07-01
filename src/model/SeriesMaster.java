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
public class SeriesMaster {

    @SerializedName("SR_CD")
    @Expose
    private String SRCD;
    @SerializedName("SR_NAME")
    @Expose
    private String SRNAME;

    @SerializedName("TAX_CD")
    @Expose
    private String TAXCD;

    @SerializedName("TAX_NAME")
    @Expose
    private String TAXNAME;

    @SerializedName("SR_ALIAS")
    @Expose
    private String SRALIAS;

    @SerializedName("STOCK")
    @Expose
    private String STOCK;
    @Expose
    private String GSTCD;
    @SerializedName("GST_NAME")
    @Expose
    private String GSTNAME;

    public String getGSTCD() {
        return GSTCD;
    }

    public void setGSTCD(String GSTCD) {
        this.GSTCD = GSTCD;
    }

    public String getGSTNAME() {
        return GSTNAME;
    }

    public void setGSTNAME(String GSTNAME) {
        this.GSTNAME = GSTNAME;
    }

    public String getSTOCK() {
        return STOCK;
    }

    public void setSTOCK(String STOCK) {
        this.STOCK = STOCK;
    }

    public String getSRALIAS() {
        return SRALIAS;
    }

    public void setSRALIAS(String SRALIAS) {
        this.SRALIAS = SRALIAS;
    }

    /**
     *
     * @return The SRCD
     */
    public String getSRCD() {
        return SRCD;
    }

    /**
     *
     * @param SRCD The SR_CD
     */
    public void setSRCD(String SRCD) {
        this.SRCD = SRCD;
    }

    /**
     *
     * @return The SRNAME
     */
    public String getSRNAME() {
        return SRNAME;
    }

    /**
     *
     * @param SRNAME The SR_NAME
     */
    public void setSRNAME(String SRNAME) {
        this.SRNAME = SRNAME;
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

}
