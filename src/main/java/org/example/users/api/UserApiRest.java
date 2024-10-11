package org.example.users.api;

import org.example.users.api.createupdatedelete.Request;
import org.example.users.api.getAll.ResponseUserGetAll;
import org.example.users.api.getById.ResponseUserGetByid;
import org.example.users.api.createupdatedelete.Response;
import retrofit2.http.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserApiRest {

    String API_USERS_URL = "https://jsonplaceholder.typicode.com/users";


    /**
     * Devuelve la lista de todos los usuarios.
     *
     * @return lista de usuarios.
     * @since 1.0
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     */
    @GET("users")
    CompletableFuture<List<ResponseUserGetAll>> getAll();

    /**
     * Obtiene un usuario por su id.
     *
     * @param id del usuario a obtener.
     * @return el usuario encontrado.
     * @since 1.0
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     */
    @GET("users/{id}")
    CompletableFuture<ResponseUserGetByid> getById(@Path("id") int id);

    /**
     * Crea un usuario.
     *
     * @param usuario a crear.
     * @return el usuario creado.
     * @since 1.0
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     */
    @POST("users")
    CompletableFuture<Response> createUser(@Body Request usuario);

    /**
     * Actualiza un usuario.
     *
     * @param id       del usuario a actualizar.
     * @param usuario con los datos actuales.
     * @return el usuario actualizado.
     * @since 1.0
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     */
    @PUT("users/{id}")
    CompletableFuture<Response> updateUser(@Path("id") int id, @Body Request usuario);

    /**
     * Elimina un usuario.
     *
     * @param id del usuario a eliminar.
     * @return el usuario eliminado.
     * @since 1.0
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     */
    @DELETE("users/{id}")
    CompletableFuture<Response> deleteUser(@Path("id") int id);

}
