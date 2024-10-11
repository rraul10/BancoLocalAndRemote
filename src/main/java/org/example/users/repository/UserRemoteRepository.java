package org.example.users.repository;

import org.example.models.Usuario;

import java.util.List;

public interface UserRemoteRepository {
    List<Usuario> getAll();
    Usuario getById(Long id);
    Usuario createUser(Usuario usuario);
    Usuario updateUser(Long id , Usuario usuario);
    Usuario deleteById(Long id);
}
