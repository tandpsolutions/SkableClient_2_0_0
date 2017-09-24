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
public interface OrderBookAPI {

    @GET("GetOrderBookHeader")
    Call<JsonObject> GetOrderBookHeader(@Query("from_date") String from_date, @Query("to_date") String to_date,
            @Query("branch_cd") String branch_cd, @Query("model_cd") String model_cd, @Query("memory_cd") String memory_cd, @Query("colour_cd") String colour_cd, @Query("db_name") String db_name, @Query("db_year") String db_year);

    @GET("GetOrderBookBill")
    Call<JsonObject> getOrderBookDetail(@Query("ref_no") String value, @Query("db_name") String db_name, @Query("db_year") String ac_year);

    @FormUrlEncoded
    @POST("AddUpdateOrderBookVoucher")
    Call<JsonObject> AddUpdateOrderBookVoucher(@Field("detail") String param_code, @Field("db_name") String db_name, @Field("db_year") String db_year);

    @GET("DeleteOrderBookEntry")
    Call<JsonObject> DeleteOrderBookEntry(@Query("ref_no") String ref_no, @Query("db_name") String db_name, @Query("db_year") String db_year);

    @GET("GetDataFromServer")
    Call<JsonObject> GetDataFromServer(@Query("VALUE") String ref_no, @Query("param_code") String param_code, @Query("db_name") String db_name, @Query("db_year") String ac_year);
}
