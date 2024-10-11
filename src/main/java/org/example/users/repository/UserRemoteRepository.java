package org.example.users.repository;

import org.example.models.Usuario;

import java.util.List;

public interface UserRemoteRepository {
    List<Usuario> getAll();
    Usuario getById(int id);
    Usuario createUser(Usuario usuario);
    Usuario updateUser(int id , Usuario usuario);
    Usuario deleteById(int id);
}
