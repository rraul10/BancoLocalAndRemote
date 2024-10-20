package org.example.users.repository;

import org.example.models.Usuario;

import java.util.List;

public interface UserRemoteRepository {
    List<Usuario> getAll();
    Usuario updateUser(long id, Usuario usuario);
    Usuario getById(long id);
    List<Usuario> getByName(String name);
    Usuario createUser(Usuario usuario);
    Usuario deleteById(long id);
}
