package org.example.client.repository.user;

import org.example.models.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersRepository {
    List<Usuario> findAllUsers();
    List<Usuario> findUsersByName(String name);
    Optional<Usuario> findUserById(Long id);
    Optional<Usuario> saveUser(Usuario user);
    Optional<Usuario> updateUser(Long uuid, Usuario user);
    Boolean deleteUserById(Long id);
    Boolean deleteAllUsers();
}
