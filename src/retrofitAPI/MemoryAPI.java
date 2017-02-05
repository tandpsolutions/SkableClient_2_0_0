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
public interface MemoryAPI {

    @GET("GetMemoryMaster")
    Call<JsonObject> getColorMaster();

    @GET("AddUpdateMemoryMaster")
    Call<JsonObject> addUpdateColorMaster(@Query("memory_cd") String color_cd, @Query("memory_name") String color_name, @Query("user_id") String user_id);

}
