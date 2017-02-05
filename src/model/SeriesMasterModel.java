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
public class SeriesMasterModel {

    private String SR_CD;
    private String SR_ALIAS;
    private String SR_NAME;
    private String BRAND_NAME;
    private String MODEL_NAME;
    private String MEMORY_NAME;
    private String COLOUR_NAME;
    private String TYPE_NAME;
    private String TAX_NAME;
    private double MOP;

    public String getTAX_NAME() {
        return TAX_NAME;
    }

    public void setTAX_NAME(String TAX_NAME) {
        this.TAX_NAME = TAX_NAME;
    }

    public String getSR_CD() {
        return SR_CD;
    }

    public void setSR_CD(String SR_CD) {
        this.SR_CD = SR_CD;
    }

    public String getSR_ALIAS() {
        return SR_ALIAS;
    }

    public void setSR_ALIAS(String SR_ALIAS) {
        this.SR_ALIAS = SR_ALIAS;
    }

    public String getSR_NAME() {
        return SR_NAME;
    }

    public void setSR_NAME(String SR_NAME) {
        this.SR_NAME = SR_NAME;
    }

    public String getBRAND_NAME() {
        return BRAND_NAME;
    }

    public void setBRAND_NAME(String BRAND_NAME) {
        this.BRAND_NAME = BRAND_NAME;
    }

    public String getMODEL_NAME() {
        return MODEL_NAME;
    }

    public void setMODEL_NAME(String MODEL_NAME) {
        this.MODEL_NAME = MODEL_NAME;
    }

    public String getMEMORY_NAME() {
        return MEMORY_NAME;
    }

    public void setMEMORY_NAME(String MEMORY_NAME) {
        this.MEMORY_NAME = MEMORY_NAME;
    }

    public String getCOLOUR_NAME() {
        return COLOUR_NAME;
    }

    public void setCOLOUR_NAME(String COLOUR_NAME) {
        this.COLOUR_NAME = COLOUR_NAME;
    }

    public String getTYPE_NAME() {
        return TYPE_NAME;
    }

    public void setTYPE_NAME(String TYPE_NAME) {
        this.TYPE_NAME = TYPE_NAME;
    }

    public double getMOP() {
        return MOP;
    }

    public void setMOP(double MOP) {
        this.MOP = MOP;
    }

}
