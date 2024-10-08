package org.example.client.repository.user;

import org.example.models.Usuario;

import java.util.List;
import java.util.UUID;

public interface UsersRepository {
    List<Usuario> findAllUsers();
    List<Usuario> findUsersByName(String name);
    Usuario findUserById(Long id);
    Usuario saveUser(Usuario user);
    Usuario updateUser(Long uuid, Usuario user);
    Boolean deleteUserById(Long id);
    Boolean deleteAllUsers();
}
