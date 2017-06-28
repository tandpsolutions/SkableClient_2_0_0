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
public interface TaxMasterAPI {

    @GET("GetTaxMasterView")
    Call<JsonObject> getTaxMasterView();

    @GET("GetTaxMaster")
    Call<JsonObject> GetTaxMaster(@Query("tax_cd") String tax_cd);

    @FormUrlEncoded
    @POST("AddUpdateTaxMaster")
    Call<JsonObject> addUpdateTaxMaster(@Field("tax_cd") String brand_cd, @Field("tax_name") String brand_name,
            @Field("sgst") String sgst, @Field("cgst") String cgst,
            @Field("user_id") String user_id, @Field("AC_YEAR") String ac_year);
}
