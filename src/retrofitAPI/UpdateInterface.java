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
public interface UpdateInterface {

    @GET("getBranchNameNew.php")
    Call<JsonObject> getUpdateVersion(@Query("ver") String ver);

    @GET("GetBranchMaster")
    Call<JsonObject> GetBranchMaster();

    @GET("GetCreditLimit")
    Call<JsonObject> GetCreditLimit(@Query("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("UpdateBranchMaster")
    Call<JsonObject> UpdateBranchMaster(@Field("branch") String branch);
}
