package org.example.usuarios.api;

import org.example.usuarios.api.createupdatedelete.Request;
import org.example.usuarios.api.getAll.ResponseUserGetAll;
import org.example.usuarios.api.getById.ResponseUserGetByid;
import org.example.usuarios.api.createupdatedelete.Response;
import retrofit2.http.*;

import java.util.List;
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
