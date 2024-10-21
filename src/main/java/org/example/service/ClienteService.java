package org.example.service;

import io.vavr.control.Either;
import org.example.service.errors.ServiceError;
import org.example.models.Cliente;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;

import java.io.File;
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
    Either<ServiceError, Usuario> createUser(Usuario usuario);
    Either<ServiceError, TarjetaCredito> createTarjeta(TarjetaCredito tarjetaCredito);
    Either<ServiceError, Cliente> updateCliente(Long id, Cliente cliente);
    Either<ServiceError, Usuario> updateUser(Long id, Usuario usuario);
    Either<ServiceError, TarjetaCredito> updateTarjeta(UUID id, TarjetaCredito tarjetaCredito);
    Either<ServiceError, Cliente> deleteCliente(Long id);
    Either<ServiceError, Usuario> deleteUser(Long id);
    Either<ServiceError, TarjetaCredito> deleteTarjeta(UUID id);

    Either<ServiceError, List<Cliente>> loadClientesJson(File file);
    Either<ServiceError, Void> saveClientesJson(List<Cliente> clientes, File file);
    Either<ServiceError, List<Usuario>> loadUsersCsv(File file);
    Either<ServiceError, Void> saveUsersCsv(List<Usuario> usuarios, File file);
    Either<ServiceError, List<TarjetaCredito>> loadTarjetasCsv(File file);
    Either<ServiceError, Void> saveTarjetasCsv(List<TarjetaCredito> tarjetasCredito, File file);

    void loadData();
}
