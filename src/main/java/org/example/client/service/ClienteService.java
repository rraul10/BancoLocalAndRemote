package org.example.client.service;

import io.vavr.control.Either;
import org.example.client.service.errors.ServiceError;
import org.example.models.Cliente;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;

import java.util.List;
import java.util.UUID;

public interface ClienteService {
    Either<ServiceError, List<Cliente>> getAllClientes(Boolean fromRemote);
    Either<ServiceError, List<Usuario> > getAllUsers(Boolean fromRemote);
    Either<ServiceError, List<TarjetaCredito>> getAllTarjetas(Boolean fromRemote);
    Either<ServiceError ,Cliente> getClienteById(Long id);
    public Either<ServiceError, Usuario> getUserById(Long id);
    public Either<ServiceError, TarjetaCredito> getTarjetaById(UUID id);

    Either<ServiceError, List<Cliente>> getClienteByName(String nombre);
    Either<ServiceError, List<Usuario>> getUserByName(String nombre);
    Either<ServiceError,Cliente> createCliente(Cliente cliente);
    Either<ServiceError, Cliente> updateCliente(Long id, Cliente cliente);
    Either<ServiceError, Cliente> deleteCliente(Long id);

    void loadData();
}
