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
public interface SeriesAPI {

    @GET("GetSeriesMaster")
    Call<JsonObject> getSeriesMaster(@Query("SR_NAME") String sr_name, @Query("brand_cd") String brand_cd);

    @GET("UpdateMOP")
    Call<JsonObject> updateMOP(@Query("SR_CD") String sr_cd, @Query("RATE") double rate);

    @GET("getSetUpDataSeries")
    Call<JsonObject> getSetUpData(@Query("sr_cd") String sr_cd);

    @FormUrlEncoded
    @POST("AppUpdateSeriesMaster")
    Call<JsonObject> appUpdateSeriesMaster(@Field("sr_cd") String sr_cd, @Field("sr_alias") String sr_alias, @Field("sr_name") String sr_name,
            @Field("brand_cd") String brand_cd, @Field("model_cd") String model_cd, @Field("memory_cd") String memory_cd, @Field("color_cd") String color_cd,
            @Field("user_id") String user_id, @Field("detail") String detail, @Field("opb_qty") int qty, @Field("opb_val") double val, @Field("AC_YEAR") String ac_year);

}
