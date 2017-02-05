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
public class UserRightsModel {

    private String FORM_CD;
    private String VIEWS;
    private String ADDS;
    private String EDITS;
    private String DELETES;
    private String PRINTS;

    public String getFORM_CD() {
        return FORM_CD;
    }

    public void setFORM_CD(String FORM_CD) {
        this.FORM_CD = FORM_CD;
    }

    public String getVIEWS() {
        return VIEWS;
    }

    public void setVIEWS(String VIEWS) {
        this.VIEWS = VIEWS;
    }

    public String getADDS() {
        return ADDS;
    }

    public void setADDS(String ADDS) {
        this.ADDS = ADDS;
    }

    public String getEDITS() {
        return EDITS;
    }

    public void setEDITS(String EDITS) {
        this.EDITS = EDITS;
    }

    public String getDELETES() {
        return DELETES;
    }

    public void setDELETES(String DELETES) {
        this.DELETES = DELETES;
    }

    public String getPRINTS() {
        return PRINTS;
    }

    public void setPRINTS(String PRINTS) {
        this.PRINTS = PRINTS;
    }

}
