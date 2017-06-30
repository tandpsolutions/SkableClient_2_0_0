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
public class ModelMasterModel {

    private String MODEL_CD;
    private String MODEL_NAME;
    private String BRAND_NAME;
    private String TAX_NAME;
    private String TYPE_NAME;
    private String HSN_CODE;

    public String getHSN_CODE() {
        return HSN_CODE;
    }

    public void setHSN_CODE(String HSN_CODE) {
        this.HSN_CODE = HSN_CODE;
    }

    public String getMODEL_CD() {
        return MODEL_CD;
    }

    public void setMODEL_CD(String MODEL_CD) {
        this.MODEL_CD = MODEL_CD;
    }

    public String getMODEL_NAME() {
        return MODEL_NAME;
    }

    public void setMODEL_NAME(String MODEL_NAME) {
        this.MODEL_NAME = MODEL_NAME;
    }

    public String getBRAND_NAME() {
        return BRAND_NAME;
    }

    public void setBRAND_NAME(String BRAND_NAME) {
        this.BRAND_NAME = BRAND_NAME;
    }

    public String getTAX_NAME() {
        return TAX_NAME;
    }

    public void setTAX_NAME(String TAX_NAME) {
        this.TAX_NAME = TAX_NAME;
    }

    public String getTYPE_NAME() {
        return TYPE_NAME;
    }

    public void setTYPE_NAME(String TYPE_NAME) {
        this.TYPE_NAME = TYPE_NAME;
    }
}
