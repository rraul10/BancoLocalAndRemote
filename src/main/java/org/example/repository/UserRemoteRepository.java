package org.example.repository;

import org.example.api.UserApiRest;
import org.example.exceptions.UserNotFoundException;
import org.example.mappers.UserMapper;
import org.example.models.Usuario;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;

public class UserRemoteRepository {

    private final Logger logger = LoggerFactory.getLogger(UserRemoteRepository.class);
    private final UserApiRest userApiRest;

    public UserRemoteRepository(UserApiRest userApiRest) {
        this.userApiRest = userApiRest;
    }

    public List<Usuario> getAll() {
        logger.info("Obteniendo todos los usuarios...");
        var call = userApiRest.getAll();

        try{
            var response = call.get();
            return response.stream()
                    .map(UserMapper::toUserFromCreate)
                    .toList();
        }catch(Exception e){
            e.printStackTrace();
            return List.of();
        }
    }

    public Usuario getById(int id) {
        logger.info("Obteniendo usuario con id: " + id);
        var call = userApiRest.getById(id);

        try {
            var response = call.get();
            return UserMapper.toUserFromCreate(response);
        } catch (Exception e) {
            if (e.getCause().getMessage().contains("404")) {
                throw new UserNotFoundException("Usuario no encontrado con id: " + id);
            } else {
                e.printStackTrace();
                return null;
            }
        }
    }

    public Usuario createUser(Usuario usuario) {
        var callSync = userApiRest.createUser(UserMapper.toRequest(usuario));
        try {
            var response = callSync.get();
            return UserMapper.toUserFromCreate(response);
        } catch (Exception e) {
            logger.error("Error creando usuario");
            return null;
        }
    }

    public Usuario updateUser(int id , Usuario usuario){
        logger.info("Actualizando al usuario" + usuario + " con id "+ id);

        var callSync  = userApiRest.updateUser(id,UserMapper.toRequest(usuario));
        try {
            var response = callSync.get();
            return UserMapper.toUserFromUpdate(response, id);
        } catch (Exception e) {
            if (e.getCause().getMessage().contains("404")) {
                throw new UserNotFoundException("Usuario no encontrado con id: " + id);
            } else {
                e.printStackTrace();
                return null;
            }
        }
    }

    public Usuario deleteById(int id) {
        logger.info("Borrando al usuario con id: " + id);

        var callSync = userApiRest.deleteUser(id);
        try {
            var response = callSync.get();
            return UserMapper.toUserFromCreate(response);
        } catch (Exception e) {
            if (e.getCause().getMessage().contains("404")) {
                throw new UserNotFoundException("Usuario no encontrado con id: " + id);
            } else {
                e.printStackTrace();
                return null;
            }
        }
    }



}
