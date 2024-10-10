package org.example.client.service;

import io.vavr.control.Either;
import org.example.client.service.errors.ServiceError;
import org.example.models.Usuario;

import java.util.List;

public interface ClienteService {
    Either<ServiceError,Usuario> getAllUsuarios(Boolean fromRemote);
    Either<ServiceError ,Usuario> getUsuarioById(Long id);
    Either<ServiceError, List<Usuario>> getUsuariosByName(String nombre);
    Either<ServiceError,Usuario> createUsuario(Usuario usuario);
    Either<ServiceError, Usuario> updateUsuario(Long id, Usuario usuario);
    Either<ServiceError, Usuario> deleteUsuario(Long id);
}
