package org.example.users.repository;

import org.example.users.api.UserApiRest;
import org.example.exceptions.UserNotFoundException;
import org.example.mappers.UserMapper;
import org.example.models.Usuario;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;

public class UserRemoteRepositoryImpl implements UserRemoteRepository{

    private final Logger logger = LoggerFactory.getLogger(UserRemoteRepositoryImpl.class);
    private final UserApiRest userApiRest;

    public UserRemoteRepositoryImpl(UserApiRest userApiRest) {
        this.userApiRest = userApiRest;
    }

    /**
     * Obtiene una lista de todos los usuarios en la API remota
     * @return una lista con todos los usuarios encontrados
     * @throws Exception si ocurre un error al obtener los usuarios
     * @version 1.0
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     */
    @Override
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

    /**
     * Obtiene un usuario por su id en la API remota
     * @param id el id del usuario a buscar
     * @return el usuario encontrado
     * @throws UserNotFoundException si el usuario no existe
     * @throws Exception si ocurre un error al obtener el usuario
     * @version 1.0
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     */
    @Override
    public Usuario updateUser(long id , Usuario usuario){
        logger.info("Actualizando al usuario " + usuario + " con id "+ id);

        var callSync  = userApiRest.updateUser(id,UserMapper.toRequest(usuario));
        try {
            var response = callSync.get();
            return UserMapper.toUserFromUpdate(response, id);
        } catch (Exception e) {
            if (e.getCause().getMessage().contains("404")) {
                logger.info("Usuario no encontrados");
                throw new UserNotFoundException("Usuario no encontrado con id: " + id);
            } else {
                e.printStackTrace();
                return null;
            }
        }
    }
    @Override
    public Usuario getById(long id) {
        logger.info("Obteniendo usuario con id: " + id);
        var call = userApiRest.getById(id);

        try {
            var response = call.get();

            if (response == null) {
                throw new UserNotFoundException("Usuario no encontrado con id: " + id);
            }

            return UserMapper.toUserFromCreate(response);
        } catch (Exception e) {
            if (e.getCause() != null && e.getCause().getMessage().contains("404")) {
                throw new UserNotFoundException("Usuario no encontrado con id: " + id);
            } else {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public List<Usuario> getByName(String name) {
        logger.info("Obteniendo usuario(s) con nombre: " + name);
        var call = userApiRest.getByName(name);

        try {
            var response = call.get();

            if (response == null || response.isEmpty()) {
                throw new UserNotFoundException("Usuario no encontrado con nombre: " + name);
            }
            return response.stream()
                    .map(UserMapper::toUserFromCreate)
                    .toList();

        } catch (Exception e) {
            if (e.getCause() != null && e.getCause().getMessage().contains("404")) {
                throw new UserNotFoundException("Usuario no encontrado con nombre: " + name);
            } else {
                e.printStackTrace();
            }
        }
        return List.of();
    }




    /**
     * Crea un usuario en la API remota
     * @param usuario el usuario a crear
     * @return el usuario creado
     * @throws Exception si ocurre un error al crear el usuario
     * @version 1.0
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     */
    @Override
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

    /**
     * Borra un usuario por su id en la API remota
     * @param id el id del usuario a borrar
     * @return el usuario borrado
     * @throws Exception si ocurre un error al borrar el usuario
     * @throws UserNotFoundException si el usuario no existe
     * @version 1.0
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     */
    @Override
    public Usuario deleteById(long id) {
        logger.info("Borrando al usuario con id: " + id);

        var callSync = userApiRest.deleteUser(id);
        try {
            var response = callSync.get();

            if (response == null) {
                logger.info("Usuario eliminado correctamente con id: " + id);
                return null;
            }

            return UserMapper.toUserFromCreate(response);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause != null && cause.getMessage().contains("404")) {
                throw new UserNotFoundException("Usuario no encontrado con id: " + id);
            } else {
                e.printStackTrace();
                return null;
            }
        }
    }



}
