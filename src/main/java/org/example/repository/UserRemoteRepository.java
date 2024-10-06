package org.example.repository;

import org.example.api.UserApiRest;
import org.example.models.Usuario;

import java.util.List;

public class UserRemoteRepository {

    private final UserApiRest userApiRest;

    public UserRemoteRepository(UserApiRest userApiRest) {
        this.userApiRest = userApiRest;
    }

    public List<Usuario> getAll() {
        //TODO implementar
        return null;
    }

}
