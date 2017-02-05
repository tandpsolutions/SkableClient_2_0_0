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
import retrofit2.http.POST;

/**
 *
 * @author bhaumik
 */
public interface StartUpAPI {

    @FormUrlEncoded
    @POST("ValidateLogin")
    Call<JsonObject> validateLogin(@Field("username") String username, @Field("password") String password, @Field("branch_cd") String branch_cd);

    @FormUrlEncoded
    @POST("ChangePassword")
    Call<JsonObject> ChangePassword(@Field("user_id") String user_id, @Field("password") String password);

    @FormUrlEncoded
    @POST("CreateUser")
    Call<JsonObject> CreateUser(@Field("username") String user_name, @Field("password") String password);

    @FormUrlEncoded
    @POST("GetDataFromServer")
    Call<JsonObject> getDataFromServer(@Field("param_code") String param_code);

    @FormUrlEncoded
    @POST("GetDataFromServer")
    Call<JsonObject> getDataFromServer(@Field("param_code") String param_code, @Field("value") String value);
    
    @FormUrlEncoded
    @POST("GetDataFromServer")
    Call<JsonObject> GetDataFromServer(@Field("param_code") String param_code, @Field("sr_cd") String sr_cd, @Field("ac_cd") String ac_cd);
}
