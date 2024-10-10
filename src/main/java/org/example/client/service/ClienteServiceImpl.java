package org.example.client.service;

import io.vavr.control.Either;
import org.example.client.service.errors.ServiceError;
import org.example.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClienteServiceImpl implements ClienteService {
    private final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);


    @Override
    public Either<ServiceError, Usuario> getAllUsuarios(Boolean fromRemote) {
        return null;
    }

    @Override
    public Either<ServiceError, Usuario> getUsuarioById(Long id) {
        return null;
    }

    @Override
    public Either<ServiceError, List<Usuario>> getUsuariosByName(String nombre) {
        return null;
    }

    @Override
    public Either<ServiceError, Usuario> createUsuario(Usuario usuario) {
        return null;
    }

    @Override
    public Either<ServiceError, Usuario> updateUsuario(Long id, Usuario usuario) {
        return null;
    }

    @Override
    public Either<ServiceError, Usuario> deleteUsuario(Long id) {
        return null;
    }
}
