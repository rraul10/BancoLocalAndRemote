package org.example.client.service;

import io.vavr.control.Either;
import org.example.client.service.errors.ServiceError;
import org.example.models.Cliente;

import java.util.List;

public interface ClienteService {
    Either<ServiceError, List<Cliente>> getAllClientes(Boolean fromRemote);
    Either<ServiceError ,Cliente> getClienteById(Long id);
    Either<ServiceError, List<Cliente>> getClienteByName(String nombre);
    Either<ServiceError,Cliente> createCliente(Cliente cliente);
    Either<ServiceError, Cliente> updateCliente(Long id, Cliente cliente);
    Either<ServiceError, Cliente> deleteCliente(Long id);
}
