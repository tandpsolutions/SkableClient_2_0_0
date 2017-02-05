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
public interface TypeAPI {

    @GET("GetTypeMsater")
    Call<JsonObject> getTypeMaster();

    @GET("AddUpdateTypeMaster")
    Call<JsonObject> addUpdateTypeMaster(@Query("type_cd") String type_cd, @Query("type_name") String type_name, @Query("user_id") String user_id);

}
