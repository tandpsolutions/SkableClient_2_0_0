/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrofitAPI;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * @author bhaumik
 */
public interface InventoryAPI {

    @GET("GetStockLedger")
    Call<JsonObject> GetStockLedger(@Query("sr_cd") String sr_cd, @Query("from_date") String type_name, @Query("to_date") String user_id);

    @GET("TagTrackQty")
    Call<JsonObject> TagTrackQty(@Query("sr_cd") String sr_cd, @Query("from_date") String type_name, @Query("to_date") String user_id);

    @GET("GetTagTrack")
    Call<JsonObject> GetTagTrack(@Query("tag_no") String tag_no);

    @GET("IMEISearch")
    Call<JsonObject> IMEISearch(@Query("tag_no") String tag_no);

    @GET("GetStockSummary")
    Call<JsonObject> GetStockSummary(@Query("sr_cd") String sr_cd, @Query("type_cd") String type_cd, @Query("brnad_cd") String brnad_cd,
            @Query("isNagative") boolean isNagative, @Query("model_cd") String model_cd, @Query("is_zero") boolean is_zero,
            @Query("branch_cd") String branch_cd);

    @GET("GetStockValueStatementDateWise")
    Call<JsonObject> GetStockValueStatementDateWise(@Query("sr_cd") String sr_cd, @Query("type_cd") String type_cd, @Query("brnad_cd") String brnad_cd,
            @Query("model_cd") String model_cd, @Query("from_date") String from_date, @Query("to_date") String to_date);

    @GET("GetStockStatementDateWise")
    Call<JsonObject> GetStockStatementDateWise(@Query("sr_cd") String sr_cd, @Query("type_cd") String type_cd, @Query("brnad_cd") String brnad_cd,
            @Query("model_cd") String model_cd, @Query("from_date") String from_date, @Query("to_date") String to_date);

    @GET("GetStockSummaryDetail")
    Call<JsonObject> GetStockSummaryDetail(@Query("sr_cd") String sr_cd);

    @GET("GetStockValueStatement")
    Call<JsonObject> getStockStatementIMEI(@Query("code") String code, @Query("mode") String mode, @Query("type_cd") String type_cd,
            @Query("GD_CD") String GD_CD, @Query("before_date") String before_date,
            @Query("after_date") String after_date, @Query("equal_date") String equal_date,
            @Query("date_mode") String date_mode, @Query("before_rate") String before_rate,
            @Query("after_rate") String after_rate, @Query("rate_mode") String rate_mode, @Query("branch_cd") String branch_cd);

    @GET("GetStockValueStatementAccess")
    Call<JsonObject> GetStockValueStatementAccess(@Query("code") String code, @Query("mode") String mode, @Query("type_cd") String type_cd,
            @Query("GD_CD") String GD_CD, @Query("before_date") String before_date,
            @Query("after_date") String after_date, @Query("equal_date") String equal_date,
            @Query("date_mode") String date_mode, @Query("before_rate") String before_rate,
            @Query("after_rate") String after_rate, @Query("rate_mode") String rate_mode, @Query("branch_cd") String branch_cd);

    @GET("GetDayWiseStockSummary")
    Call<JsonObject> GetDayWiseStockSummary(@Query("code") String code, @Query("mode") String mode, @Query("type_cd") String type_cd,
            @Query("GD_CD") String GD_CD, @Query("before_date") String before_date,
            @Query("after_date") String after_date, @Query("equal_date") String equal_date,
            @Query("date_mode") String date_mode, @Query("before_rate") String before_rate,
            @Query("after_rate") String after_rate, @Query("rate_mode") String rate_mode, @Query("branch_cd") String branch_cd, @Query("sub_type_cd") String sub_type_cd);

    @GET("GetIntransitStockReport")
    Call<JsonObject> getDataHeader(@Query("from_date") String from_date, @Query("to_date") String to_date);

    @GET("StockTrnasferUpdate")
    Call<JsonObject> StockTrnasferUpdate(@Query("ref_no") String ref_no, @Query("user_id") String user_id);

    @GET("GetStockInOutReport")
    Call<JsonObject> GetStockInOutReport(@Query("from_date") String from_date, @Query("to_date") String to_date);

}
