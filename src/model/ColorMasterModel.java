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
public class ColorMasterModel {

    private String COLOUR_CD;
    private String COLOUR_NAME;
    private String USER_ID;

    public String getCOLOUR_CD() {
        return COLOUR_CD;
    }

    public void setCOLOUR_CD(String COLOUR_CD) {
        this.COLOUR_CD = COLOUR_CD;
    }

    public String getCOLOUR_NAME() {
        return COLOUR_NAME;
    }

    public void setCOLOUR_NAME(String COLOUR_NAME) {
        this.COLOUR_NAME = COLOUR_NAME;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

}
