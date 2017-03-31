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

/**
 *
 * @author bhaumik
 */
public interface GroupMasterAPI {

    @GET("GetGroupMaster")
    Call<JsonObject> GetGroupMaster();

    @FormUrlEncoded
    @POST("AddUpdateGroupMaster")
    Call<JsonObject> AddUpdateGroupMaster(@Field("GRP_CD") String grp_cd,@Field("ACC_EFF") String acc_eff,
            @Field("GRP_NAME") String grp_name, @Field("HEAD_GRP") String head_grp, @Field("user_id") String user_id, @Field("AC_YEAR") String ac_year);
}
