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
public class SalesReturnControllerDetailModel {

    private String TAG_NO;
    private String SR_CD;
    private String IMEI_NO;
    private String SERAIL_NO;
    private int QTY;
    private double RATE;
    private String TAX_CD;
    private double BASIC_AMT;
    private double TAX_AMT;
    private double ADD_TAX_AMT;
    private double DISC_PER;
    private double MRP;
    private double AMT;
    private String PUR_TAG_NO;
    private int isMain;
    private String SR_NAME;

    public String getSR_NAME() {
        return SR_NAME;
    }

    public void setSR_NAME(String SR_NAME) {
        this.SR_NAME = SR_NAME;
    }

    public int getIsMain() {
        return isMain;
    }

    public void setIsMain(int isMain) {
        this.isMain = isMain;
    }

    public String getTAX_CD() {
        return TAX_CD;
    }

    public void setTAX_CD(String TAX_CD) {
        this.TAX_CD = TAX_CD;
    }

    public double getBASIC_AMT() {
        return BASIC_AMT;
    }

    public void setBASIC_AMT(double BASIC_AMT) {
        this.BASIC_AMT = BASIC_AMT;
    }

    public double getTAX_AMT() {
        return TAX_AMT;
    }

    public void setTAX_AMT(double TAX_AMT) {
        this.TAX_AMT = TAX_AMT;
    }

    public double getADD_TAX_AMT() {
        return ADD_TAX_AMT;
    }

    public void setADD_TAX_AMT(double ADD_TAX_AMT) {
        this.ADD_TAX_AMT = ADD_TAX_AMT;
    }

    public double getDISC_PER() {
        return DISC_PER;
    }

    public void setDISC_PER(double DISC_PER) {
        this.DISC_PER = DISC_PER;
    }

    public double getMRP() {
        return MRP;
    }

    public void setMRP(double MRP) {
        this.MRP = MRP;
    }

    public String getTAG_NO() {
        return TAG_NO;
    }

    public void setTAG_NO(String TAG_NO) {
        this.TAG_NO = TAG_NO;
    }

    public String getSR_CD() {
        return SR_CD;
    }

    public void setSR_CD(String SR_CD) {
        this.SR_CD = SR_CD;
    }

    public String getIMEI_NO() {
        return IMEI_NO;
    }

    public void setIMEI_NO(String IMEI_NO) {
        this.IMEI_NO = IMEI_NO;
    }

    public String getSERAIL_NO() {
        return SERAIL_NO;
    }

    public void setSERAIL_NO(String SERAIL_NO) {
        this.SERAIL_NO = SERAIL_NO;
    }

    public int getQTY() {
        return QTY;
    }

    public void setQTY(int QTY) {
        this.QTY = QTY;
    }

    public double getRATE() {
        return RATE;
    }

    public void setRATE(double RATE) {
        this.RATE = RATE;
    }

    public double getAMT() {
        return AMT;
    }

    public void setAMT(double AMT) {
        this.AMT = AMT;
    }

    public String getPUR_TAG_NO() {
        return PUR_TAG_NO;
    }

    public void setPUR_TAG_NO(String PUR_TAG_NO) {
        this.PUR_TAG_NO = PUR_TAG_NO;
    }

}
