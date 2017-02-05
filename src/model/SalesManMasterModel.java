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
public class SalesManMasterModel {

    private String SM_CD;
    private String SM_NAME;
    private String USER_ID;

    public String getSMCD() {
        return SM_CD;
    }

    public void setSMCD(String SM_CD) {
        this.SM_CD = SM_CD;
    }

    public String getSMNAME() {
        return SM_NAME;
    }

    public void setSMNAME(String SM_NAME) {
        this.SM_NAME = SM_NAME;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

}
