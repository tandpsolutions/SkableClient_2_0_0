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
public interface SupportAPI {

    @FormUrlEncoded
    @POST("ValidateData")
    Call<JsonObject> validateData(@Field("table") String table, @Field("column1") String column1, @Field("column2") String column2, @Field("value") String value);

    @FormUrlEncoded
    @POST("ValidateDataEdit")
    Call<JsonObject> ValidateDataEdit(@Field("table") String table, @Field("column1") String column1, @Field("column2") String column2, @Field("value") String value, @Field("column3") String column3, @Field("value1") String value1);

    @GET("GetNotes")
    Call<JsonObject> getNotes(@Query("from_date") String from_date, @Query("to_date") String to_date, @Query("ac_cd") String ac_cd);

    @FormUrlEncoded
    @POST("AddNote")
    Call<JsonObject> addNote(@Field("rec_no") String rec_no, @Field("from_date") String from_date, @Field("to_date") String to_date,
            @Field("ac_cd") String ac_cd, @Field("descr") String descr);
}
