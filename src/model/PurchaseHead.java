/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bhaumik
 */
public class PurchaseHead {

    @SerializedName("result")
    @Expose
    private Integer result;
    @SerializedName("Cause")
    @Expose
    private String Cause;
    @SerializedName("purchaseHeader")
    @Expose
    private List<PurchaseHeader> purchaseHeader = new ArrayList<PurchaseHeader>();

    /**
     *
     * @return The result
     */
    public Integer getResult() {
        return result;
    }

    /**
     *
     * @param result The result
     */
    public void setResult(Integer result) {
        this.result = result;
    }

    /**
     *
     * @return The Cause
     */
    public String getCause() {
        return Cause;
    }

    /**
     *
     * @param Cause The Cause
     */
    public void setCause(String Cause) {
        this.Cause = Cause;
    }

    /**
     *
     * @return The purchaseHeader
     */
    public List<PurchaseHeader> getPurchaseHeader() {
        return purchaseHeader;
    }

    /**
     *
     * @param purchaseHeader The purchaseHeader
     */
    public void setPurchaseHeader(List<PurchaseHeader> purchaseHeader) {
        this.purchaseHeader = purchaseHeader;
    }

}
