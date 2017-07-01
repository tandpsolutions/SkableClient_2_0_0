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
public interface PurchaseAPI {

    @GET("GetPurchaseDetail")
    Call<PurchaseHead> getDataHeader(@Query("from_date") String from_date, @Query("to_date") String to_date, @Query("v_type") String v_type,@Query("tax_type") int tax_type);

    @GET("GetDataFromServer")
    Call<JsonObject> getBill(@Query("ref_no") String ref_no, @Query("param_code") String param_code);

    @GET("GetDataFromServer")
    Call<JsonObject> getTagNo(@Query("ref_no") String ref_no, @Query("param_code") String param_code);

    @GET("DeletePurchaseBill")
    Call<JsonObject> DeletePurchaseBill(@Query("ref_no") String ref_no);

    @GET("GetDataFromServer")
    Call<JsonObject> getTagNoDetail(@Query("tag_list") String ref_no, @Query("param_code") String param_code, @Query("only_stock") boolean flag);

    @FormUrlEncoded
    @POST("AddUpdatePurchaseBill")
    Call<JsonObject> addUpdatePurchaseBill(@Field("header") String ref_no, @Field("detail") String param_code);
}
