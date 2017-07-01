/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrofitAPI;

import com.google.gson.JsonObject;
import model.PurchaseHead;
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
public interface SalesAPI {

    @GET("GetSalesDetail")
    Call<PurchaseHead> getDataHeader(@Query("from_date") String from_date, @Query("to_date") String to_date, @Query("v_type") String v_type, @Query("branch_cd") String branch_cd, @Query("tax_type") String tax_type);

    @GET("SalesTrack")
    Call<PurchaseHead> SalesTrack(@Query("from_date") String from_date, @Query("to_date") String to_date, @Query("branch_cd") String branch_cd,
            @Query("imei") String imei, @Query("name") String name, @Query("mobile") String mobile);

    @GET("GetDataFromServer")
    Call<JsonObject> GetDataFromServer(@Query("VALUE") String ref_no, @Query("param_code") String param_code);

    @GET("GetDataFromServer")
    Call<JsonObject> getTagNo(@Query("ref_no") String ref_no, @Query("param_code") String param_code);

    @GET("DeleteSalesBill")
    Call<JsonObject> DeleteSalesBill(@Query("ref_no") String ref_no);

    @GET("GetDataFromServer")
    Call<JsonObject> GetDataFromServer(@Query("tag_list") String ref_no, @Query("param_code") String param_code, @Query("only_stock") boolean flag);

    @GET("GetDataFromServer")
    Call<JsonObject> getTagNoDetailSales(@Query("tag_list") String ref_no, @Query("param_code") String param_code, @Query("only_stock") boolean flag, @Query("loc") String loc);

    @GET("GetSalesBillPrint")
    Call<JsonObject> GetSalesBillPrint(@Query("ref_no") String ref_no);

    @GET("GetSalesBillTaxPrint")
    Call<JsonObject> GetSalesBillTaxPrint(@Query("ref_no") String ref_no);

    @FormUrlEncoded
    @POST("AddUpdateSalesBill")
    Call<JsonObject> addUpdateSalesBill(@Field("header") String ref_no, @Field("detail") String param_code);

    @GET("GetInsuranceBill")
    Call<JsonObject> GetInsuranceBill(@Query("ref_no") String ref_no);

    @GET("GetDataFromServer")
    Call<JsonObject> getTagNoDetailSales(@Query("tag_list") String ref_no, @Query("param_code") String param_code, @Query("only_stock") boolean flag);

}
