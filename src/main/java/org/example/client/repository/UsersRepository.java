package org.example.client.repository;

import org.example.models.Usuario;

import java.util.List;
import java.util.UUID;

public interface UsersRepository {
    List<Usuario> findAllUsers();
    List<Usuario> findUsersByName(String name);
    Usuario findUserById(UUID id);
    Usuario saveUser(Usuario user);
    Usuario updateUser(UUID uuid, Usuario user);
    Boolean deleteUserById(UUID id);
    Boolean deleteAllUsers();
}
