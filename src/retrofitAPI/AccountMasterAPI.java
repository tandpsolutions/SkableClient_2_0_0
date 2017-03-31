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
public interface AccountMasterAPI {

    @GET("GetAccountMaster")
    Call<JsonObject> getSeriesMaster(@Query("AC_NAME") String ac_name);

    @FormUrlEncoded
    @POST("AddUpdateAccountMaster")
    Call<JsonObject> AddUpdateAccountMaster(@Field("ac_model") String ac_model, @Field("AC_YEAR") String ac_year);

}
