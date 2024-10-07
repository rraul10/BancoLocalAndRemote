package org.example.api;

import org.example.api.createupdatedelete.Request;
import org.example.api.getAll.ResponseUserGetAll;
import org.example.api.getById.ResponseUserGetByid;
import retrofit2.http.*;

import java.util.List;
import org.example.api.createupdatedelete.Response;
import java.util.concurrent.CompletableFuture;

public interface UserApiRest {

    String API_USERS_URL = "https://jsonplaceholder.typicode.com/users";


    @GET("users")
    CompletableFuture<List<ResponseUserGetAll>> getAll();

    @GET("users/{id}")
    CompletableFuture<ResponseUserGetByid> getById(@Path("id") int id);

    @POST("users")
    CompletableFuture<Response> createUser(@Body Request usuario);

    @PUT("users/{id}")
    CompletableFuture<Response> updateUser(@Path("id") int id, @Body Request usuario);

    @DELETE("users/{id}")
    CompletableFuture<Response> deleteUser(@Path("id") int id);

}
