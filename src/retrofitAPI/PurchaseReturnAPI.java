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
public interface PurchaseReturnAPI {

    @GET("GetPurchaseReturnHeader")
    Call<PurchaseHead> getDataHeader(@Query("from_date") String from_date, @Query("to_date") String to_date, @Query("v_type") String v_type);

    @GET("GetDataFromServer")
    Call<JsonObject> GetDataFromServer(@Query("VALUE") String ref_no, @Query("param_code") String param_code);

    @GET("GetDataFromServer")
    Call<JsonObject> getTagNoDetailSales(@Query("tag_list") String ref_no, @Query("param_code") String param_code, @Query("only_stock") boolean flag);

    @FormUrlEncoded
    @POST("AddUpdatePurchaseReturnBill")
    Call<JsonObject> addUpdatePurchaseReturnBill(@Field("header") String ref_no, @Field("detail") String param_code);

    @FormUrlEncoded
    @POST("DeletePurchaseReturn")
    Call<JsonObject> DeletePurchaseReturn(@Field("ref_no") String param_code);

    @GET("GetPurchaseReturnPrint")
    Call<JsonObject> GetPurchaseReturnPrint(@Query("ref_no") String ref_no);

    @GET("GetPurchaseReturnTaxPrint")
    Call<JsonObject> GetPurchaseReturnTaxPrint(@Query("ref_no") String ref_no);

}
