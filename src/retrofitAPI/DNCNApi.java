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
public interface DNCNApi {

    @GET("GetDNCNHeader")
    Call<JsonObject> GetBankPaymentHeader(@Query("from_date") String from_date, @Query("to_date") String to_date, 
            @Query("v_type") String v_type,@Query("branch_cd") String branch_cd);

    @GET("GetDataFromServer")
    Call<JsonObject> getBankDetail(@Query("value") String value, @Query("param_code") String param_code);

    @FormUrlEncoded
    @POST("AddUpdateDNCNDetail")
    Call<JsonObject> addUpdateBankVoucher(@Field("detail") String param_code);

    @GET("DNCNDelete")
    Call<JsonObject> DeleteBankBill(@Query("ref_no") String ref_no, @Query("type") int type);
}
