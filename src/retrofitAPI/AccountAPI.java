/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrofitAPI;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 *
 * @author bhaumik
 */
public interface AccountAPI {

    @GET("GetGroupSummary")
    Call<JsonObject> GetGroupSummary(@Query("grp_cd") String sr_cd, @Query("mode") int mode, @Query("amt") double amt);

    @GET("UpdateOLDB2_4")
    Call<JsonObject> UpdateOLDB2_4(@Query("unpaid_amt") String sr_cd, @Query("doc_ref_no") String mode);

    @GET("CashSnapshot")
    Call<JsonObject> CashSnapshot(@Query("grp_cd") String grp_cd, @Query("from_date") String from_date, @Query("to_date") String to_date);

    @GET("GetGeneralLedgerSummary")
    Call<JsonObject> GetGeneralLedgerSummary(@Query("ac_cd") String ac_cd);

    @FormUrlEncoded
    @POST("GenralLedger")
    Call<JsonObject> GenralLedger(@Field("ac_cd") String ac_cd, @Field("from_date") String from_date, @Field("to_date") String to_date);

    @FormUrlEncoded
    @POST("BankBook")
    Call<JsonObject> BankBook(@Field("ac_cd") String ac_cd, @Field("from_date") String from_date, @Field("to_date") String to_date, @Field("rec_date") boolean rec_date);

    @FormUrlEncoded
    @POST("SetReconsilationDate")
    Call<JsonObject> SetReconsilationDate(@Field("ref_no") String ref_no, @Field("rec_date") String rec_date);

    @FormUrlEncoded
    @POST("updateDenomation")
    Call<JsonObject> updateDenomation(@Field("v_date") String v_date, @Field("branch_cd") String branch_cd, @Field("note_cd") int note_cd, @Field("qty") int qty);

    @FormUrlEncoded
    @POST("Phonebook")
    Call<JsonObject> Phonebook(@Field("grp_cd") String grp_cd, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("v_type") int v_type, @Field("sales") boolean sales);

    @FormUrlEncoded
    @POST("GetInsuranceRegister")
    Call<JsonObject> GetInsuranceRegister(@Field("from_date") String from_date, @Field("to_date") String to_date, @Field("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("BajajReport")
    Call<JsonObject> BajajReport(@Field("from_date") String from_date, @Field("to_date") String to_date, @Field("branch_cd") String branch_cd, @Field("ac_cd") String ac_cd);

    @FormUrlEncoded
    @POST("DailySalesStatement")
    Call<JsonObject> DailySalesStatement(@Field("mode") int mode, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("EOD")
    Call<JsonObject> EOD(@Field("mode") int mode, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("GetDenomation")
    Call<JsonObject> GetDenomation(@Field("v_date") String v_date,
            @Field("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("BranchWisePendingCollecionReport")
    Call<JsonObject> BranchWisePendingCollecionReport(@Field("branch_cd") String branch_cd, @Field("v_type") int v_type, @Field("ref_cd") String ref_cd, @Field("from_date") String from_date, @Field("to_date") String to_date);

    @FormUrlEncoded
    @POST("DailyCashSummary")
    Call<JsonObject> DailyCashStatement(@Field("from_date") String from_date, @Field("to_date") String to_date, @Field("branch_cd") int branch_cd);

    @FormUrlEncoded
    @POST("DailyBankSummary")
    Call<JsonObject> DailyBankSummary(@Field("from_date") String from_date, @Field("to_date") String to_date, @Field("branch_cd") int branch_cd);

    @FormUrlEncoded
    @POST("DailyCashStatementDetail")
    Call<JsonObject> DailyCashStatementDetail(@Field("from_date") String from_date, @Field("to_date") String to_date, @Field("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("DailyBankStatementDetail")
    Call<JsonObject> DailyBankStatementDetail(@Field("from_date") String from_date, @Field("to_date") String to_date, @Field("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("DailySalesStatementDetail")
    Call<JsonObject> DailySalesStatementDetail(@Field("mode") int mode, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("TypeWiseSales")
    Call<JsonObject> TypeWiseSales(@Field("type_cd") String type_cd, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("ac_cd") String ac_cd, @Query("sr_cd") String sr_cd, @Query("brnad_cd") String brnad_cd, @Query("model_cd") String model_cd, @Field("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("SalesReportAccount")
    Call<JsonObject> SalesReportAccount(@Field("type_cd") String type_cd, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("ac_cd") String ac_cd, @Query("sr_cd") String sr_cd, @Query("brnad_cd") String brnad_cd, @Query("model_cd") String model_cd, @Field("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("TypeWisePurchase")
    Call<JsonObject> TypeWisePurchase(@Field("type_cd") String type_cd, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("ac_cd") String ac_cd, @Query("sr_cd") String sr_cd, @Query("brnad_cd") String brnad_cd, @Query("model_cd") String model_cd, @Field("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("TypeWiseSalesDetail")
    Call<JsonObject> TypeWiseSalesDetail(@Field("type_cd") String type_cd, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("ac_cd") String ac_cd, @Query("sr_cd") String sr_cd, @Query("brnad_cd") String brnad_cd, @Query("model_cd") String model_cd, @Field("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("WithoutTagSalesReport")
    Call<JsonObject> WithoutTagSalesReport(@Field("type_cd") String type_cd, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("ac_cd") String ac_cd, @Query("sr_cd") String sr_cd, @Query("brnad_cd") String brnad_cd, @Query("model_cd") String model_cd);

    @FormUrlEncoded
    @POST("TypeWisePurchaseDetail")
    Call<JsonObject> TypeWisePurchaseDetail(@Field("type_cd") String type_cd, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("ac_cd") String ac_cd, @Query("sr_cd") String sr_cd, @Query("brnad_cd") String brnad_cd, @Query("model_cd") String model_cd, @Field("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("BuyBackRegister")
    Call<JsonObject> BuyBackRegister(@Field("type_cd") String type_cd, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("ac_cd") String ac_cd, @Query("sr_cd") String sr_cd, @Query("brnad_cd") String brnad_cd, @Query("model_cd") String model_cd);

    @FormUrlEncoded
    @POST("DCRegister")
    Call<JsonObject> DCRegister(@Field("type_cd") String type_cd, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("ac_cd") String ac_cd, @Query("sr_cd") String sr_cd, @Query("brnad_cd") String brnad_cd, @Query("model_cd") String model_cd);

    @FormUrlEncoded
    @POST("CreditNoteRegister")
    Call<JsonObject> CreditNoteRegister(@Field("type_cd") String type_cd, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("ac_cd") String ac_cd, @Query("sr_cd") String sr_cd, @Query("brnad_cd") String brnad_cd, @Query("model_cd") String model_cd);

    @FormUrlEncoded
    @POST("CreditNoteReport")
    Call<JsonObject> StockReportCreditNote(@Field("type_cd") String type_cd, @Field("model_cd") String model_cd, @Field("from_date") String from_date, @Field("to_date") String to_date, @Field("ac_cd") String ac_cd, @Field("memory_cd") String memory_cd, @Field("sales") boolean sales);

    @FormUrlEncoded
    @POST("UpdatePrize")
    Call<JsonObject> UpdatePrize(@Field("detail") String model_cd);

    @FormUrlEncoded
    @POST("UpdateNlc")
    Call<JsonObject> UpdateNlc(@Field("detail") String model_cd);

    @FormUrlEncoded
    @POST("UpdateBill")
    Call<JsonObject> UpdateBill(@Field("dr_doc_ref_no") String dr_doc_ref_no, @Field("cr_doc_ref_no") String cr_doc_ref_no,
            @Field("DR_DOC_CD") String DR_DOC_CD, @Field("CR_DOC_CD") String CR_DOC_CD,
            @Field("DR_INV_NO") String DR_INV_NO, @Field("CR_INV_NO") String CR_INV_NO,
            @Field("DR_AMT") String DR_AMT, @Field("CR_AMT") String CR_AMT,
            @Field("DR_SR_NO") String DR_SR_NO, @Field("CR_SR_NO") String CR_SR_NO, @Field("ac_cd") String ac_cd);

    @FormUrlEncoded
    @POST("ReverseBill")
    Call<JsonObject> ReverseBill(@Field("doc_ref_no") String doc_ref_no);

    @FormUrlEncoded
    @POST("SalesRegister")
    Call<JsonObject> SalesRegister(@Field("pmt_mode") int pmt_mode, @Field("mode") int mode, @Field("branch_cd") int branch_cd,
            @Field("from_date") String from_date, @Field("to_date") String to_date);

    @FormUrlEncoded
    @POST("SalesReport")
    Call<JsonObject> SalesReport(@Field("from_date") String from_date, @Field("to_date") String to_date);

    @FormUrlEncoded
    @POST("SalesReportTax")
    Call<JsonObject> SalesReportTax(@Field("from_date") String from_date, @Field("to_date") String to_date);

    @FormUrlEncoded
    @POST("SalesReturnReportTax")
    Call<JsonObject> SalesReturnReportTax(@Field("from_date") String from_date, @Field("to_date") String to_date);

    @FormUrlEncoded
    @POST("PurchaseReturnTax")
    Call<JsonObject> PurchaseReturnTax(@Field("from_date") String from_date, @Field("to_date") String to_date);

    @FormUrlEncoded
    @POST("CardWiseSalesStatement")
    Call<JsonObject> SalesRegisterCardWise(@Field("card") boolean sales);

    @FormUrlEncoded
    @POST("SalesRegisterDetail")
    Call<JsonObject> SalesRegisterDetail(@Field("pmt_mode") int pmt_mode, @Field("mode") int mode, @Field("branch_cd") int branch_cd,
            @Field("from_date") String from_date, @Field("to_date") String to_date, @Field("ac_cd") String ac_cd);

    @FormUrlEncoded
    @POST("CardWiseSalesDetailStatement")
    Call<JsonObject> CardWiseSalesDetailStatement(@Field("ac_cd") String ac_cd);

    @FormUrlEncoded
    @POST("SalesRegisterDetailAccount")
    Call<JsonObject> SalesRegisterDetailAccount(@Field("pmt_mode") int pmt_mode, @Field("mode") int mode, @Field("branch_cd") int branch_cd,
            @Field("from_date") String from_date, @Field("to_date") String to_date, @Field("ac_cd") String ac_cd);

    @FormUrlEncoded
    @POST("PurchaseRegisterDetailAccount")
    Call<JsonObject> PurchaseRegisterDetailAccount(@Field("pmt_mode") int pmt_mode, @Field("mode") int mode, @Field("branch_cd") int branch_cd,
            @Field("from_date") String from_date, @Field("to_date") String to_date, @Field("ac_cd") String ac_cd);

    @FormUrlEncoded
    @POST("SalesReturnRegisterDetailAccount")
    Call<JsonObject> SalesReturnRegisterDetailAccount(@Field("pmt_mode") int pmt_mode, @Field("branch_cd") int branch_cd,
            @Field("from_date") String from_date, @Field("to_date") String to_date, @Field("ac_cd") String ac_cd);

    @FormUrlEncoded
    @POST("PurchaseReturnRegisterAccount")
    Call<JsonObject> PurchaseReturnRegisterAccount(@Field("pmt_mode") int pmt_mode, @Field("branch_cd") int branch_cd,
            @Field("from_date") String from_date, @Field("to_date") String to_date, @Field("ac_cd") String ac_cd);

    @FormUrlEncoded
    @POST("PurchaseRegister")
    Call<JsonObject> PurchaseRegister(@Field("pmt_mode") int pmt_mode, @Field("mode") int mode, @Field("branch_cd") int branch_cd,
            @Field("from_date") String from_date, @Field("to_date") String to_date);

    @FormUrlEncoded
    @POST("SalesReturnRegister")
    Call<JsonObject> SalesReturnRegister(@Field("pmt_mode") int pmt_mode, @Field("branch_cd") int branch_cd,
            @Field("from_date") String from_date, @Field("to_date") String to_date);

    @FormUrlEncoded
    @POST("PurchaseReturnRegister")
    Call<JsonObject> PurchaseReturnRegister(@Field("pmt_mode") int pmt_mode, @Field("branch_cd") int branch_cd,
            @Field("from_date") String from_date, @Field("to_date") String to_date);

    @FormUrlEncoded
    @POST("PurchaseRegisterDetail")
    Call<JsonObject> PurchaseRegisterDetail(@Field("pmt_mode") int pmt_mode, @Field("mode") int mode, @Field("branch_cd") int branch_cd,
            @Field("from_date") String from_date, @Field("to_date") String to_date, @Field("ac_cd") String ac_cd);

    @FormUrlEncoded
    @POST("SalesReturnRegisterDetail")
    Call<JsonObject> SalesReturnRegisgerDetail(@Field("pmt_mode") int pmt_mode, @Field("branch_cd") int branch_cd,
            @Field("from_date") String from_date, @Field("to_date") String to_date, @Field("ac_cd") String ac_cd);

    @FormUrlEncoded
    @POST("PurchaseReturnRegisterDetail")
    Call<JsonObject> PurchaseReturnRegisterDetail(@Field("pmt_mode") int pmt_mode, @Field("branch_cd") int branch_cd,
            @Field("from_date") String from_date, @Field("to_date") String to_date, @Field("ac_cd") String ac_cd);

    @GET("MarginReport")
    Call<JsonObject> MarginReport(@Query("from_date") String type_name, @Query("to_date") String user_id, @Query("mode") String mode,
            @Query("sr_cd") String sr_cd, @Query("brnad_cd") String brnad_cd, @Query("model_cd") String model_cd,
            @Query("type_cd") String type_cd, @Query("v_type") int voucher_type, @Query("branch_cd") String branch_cd);

    @GET("SnapShots")
    Call<JsonObject> SnapShots(@Query("from_date") String type_name, @Query("to_date") String user_id);

    @GET("TypeWiseProftStatement")
    Call<JsonObject> TypeWiseProftStatement(@Query("from_date") String type_name, @Query("to_date") String user_id,
            @Query("type_cd") String type_cd, @Query("branch_cd") String branch_cd);

    @GET("TypeWiseBrandWiseProfitStatement")
    Call<JsonObject> TypeWiseBrandWiseProfitStatement(@Query("from_date") String type_name, @Query("to_date") String user_id,
            @Query("type_cd") String type_cd, @Query("branch_cd") String branch_cd);

    @GET("MarginReportByTag")
    Call<JsonObject> MarginReportByTag(@Query("tag_no") String tag_no);

    @GET("PurchaseRateByTag")
    Call<JsonObject> PurchaseRateByTag(@Query("tag_no") String tag_no);

    @GET("IMEWisePS")
    Call<JsonObject> IMEWisePS(@Query("from_date") String type_name, @Query("to_date") String user_id,
            @Query("model_cd") String model_cd, @Query("ac_cd") String ac_cd,
            @Query("type_cd") String type_cd, @Query("brand_cd") String brand_cd, @Query("bill_no") String bill_no);

    @GET("IMEWisePSSales")
    Call<JsonObject> IMEWisePSSales(@Query("from_date") String type_name, @Query("to_date") String user_id,
            @Query("model_cd") String model_cd, @Query("ac_cd") String ac_cd, @Query("type_cd") String type_cd,
            @Query("include") boolean include, @Query("brand_cd") String brand_cd, @Query("bill_no") String bill_no);

    @GET("GetCardDetail")
    Call<JsonObject> GetCardDetail(@Query("from_date") String type_name, @Query("to_date") String user_id, @Query("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("GetStockAdjustmentRegister")
    Call<JsonObject> GetStockAdjustmentRegister(@Field("type_cd") String type_cd, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Query("sr_cd") String sr_cd, @Query("brnad_cd") String brnad_cd, @Query("model_cd") String model_cd);

    @GET("ListBills")
    Call<JsonObject> ListBills(@Query("ac_cd") String ac_cd, @Query("sales") boolean sales);

    @GET("ListBillsAdjsted")
    Call<JsonObject> ListBillsAdjsted(@Query("ac_cd") String ac_cd);

    @GET("GetModelwiseMonthWiseSalesStatement")
    Call<JsonObject> GetModelwiseMonthWiseSalesStatement(@Query("code") String code, @Query("mode") String mode, @Query("type_cd") String type_cd,
            @Query("GD_CD") String GD_CD, @Query("before_date") String before_date,
            @Query("after_date") String after_date, @Query("equal_date") String equal_date,
            @Query("date_mode") String date_mode, @Query("before_rate") String before_rate,
            @Query("after_rate") String after_rate, @Query("rate_mode") String rate_mode, @Query("branch_cd") String branch_cd, @Query("sub_type_cd") String sub_type_cd);

}
