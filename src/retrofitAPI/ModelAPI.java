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
public interface ModelAPI {

    @GET("GetModelMaster")
    Call<JsonObject> getModelMaster(@Query("model_name") String model_name);

    @FormUrlEncoded
    @POST("GetModel")
    Call<JsonObject> GetModel(@Field("model_cd") String model_cd);

    @FormUrlEncoded
    @POST("UpdateBulkHSN")
    Call<JsonObject> updateBulkHSN(@Field("detail") String detail);

    @FormUrlEncoded
    @POST("UpdateBulkGstCode")
    Call<JsonObject> updateBulkGSTCode(@Field("detail") String detail);

    @GET("getSetUpData")
    Call<JsonObject> getSetUpData();

    @FormUrlEncoded
    @POST("AppUpdateModelMaster")
    Call<JsonObject> AppUpdateModelMaster(@Field("model_cd") String model_cd, @Field("model_name") String model_name,
            @Field("brand_cd") String brand_cd, @Field("type_cd") String type_cd, @Field("tax_cd") String tax_cd, @Field("user_id") String user_id, @Field("AC_YEAR") String ac_year, @Field("GST_CD") String gst_cd);
}
