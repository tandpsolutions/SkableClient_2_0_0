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
public interface UserAPI {

    @GET("GetUserMaster")
    Call<JsonObject> getUserMaster();

    @GET("GetUserGrpMaster")
    Call<JsonObject> GetUserGrpMaster();

    @GET("GetMenuMaster")
    Call<JsonObject> GetMenuMaster();

    @GET("GetFormFromMenu")
    Call<JsonObject> GetFormFromMenu(@Query("menu_cd") String menu_cd);

    @GET("GetUserRights")
    Call<JsonObject> GetUserRights(@Query("form_cd") String form_cd, @Query("user_grp_cd") String user_grp_cd);

    @GET("ApplyRights")
    Call<JsonObject> ApplyUserRights(@Query("form_cd") String form_cd, @Query("user_grp_cd") String user_grp_cd, @Query("VIEWS") int views, @Query("ADDS") int adds, @Query("EDITS") int edits, @Query("DELETES") int delete, @Query("PRINTS") int prints);

    @GET("AddUpdateUserGrpMst")
    Call<JsonObject> addUpdateUserMsater(@Query("user_id") String brand_cd, @Query("user_name") String brand_name, @Query("user_grp_cd") String user_id, @Query("branch_cd") String branch_cd);

    @GET("AddUpdateUserGroupMaster")
    Call<JsonObject> AddUpdateUserGroupMaster(@Query("USER_GRP") String brand_name, @Query("USER_GRP_CD") String user_id);

}
