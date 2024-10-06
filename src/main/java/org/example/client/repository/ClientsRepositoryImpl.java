package org.example.client.repository;

import org.example.models.Cliente;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientsRepositoryImpl implements ClientsRepository, UsersRepository, CreditCardRepository {
    @Override
    public List<Cliente> findAllClientes() {
        return List.of();
    }

    @Override
    public Cliente findClientById(UUID id) {
        return null;
    }

    @Override
    public List<Cliente> findClientByName(String name) {
        return List.of();
    }

    @Override
    public Cliente saveClient(Cliente client) {
        return null;
    }

    @Override
    public Cliente updateClient(UUID id, Cliente updatedClient) {
        return null;
    }

    @Override
    public Boolean deleteCientById(UUID id) {
        return null;
    }

    @Override
    public Boolean deleteAllClients() {
        return null;
    }

    @Override
    public List<TarjetaCredito> findAllCreditCards() {
        return List.of();
    }

    @Override
    public TarjetaCredito findCreditCardById(UUID id) {
        return null;
    }

    @Override
    public TarjetaCredito findCreditCardByNumber(String number) {
        return null;
    }

    @Override
    public TarjetaCredito saveCreditCard(TarjetaCredito creditCard) {
        return null;
    }

    @Override
    public TarjetaCredito updateCreditCard(TarjetaCredito creditCard) {
        return null;
    }

    @Override
    public Boolean deleteCreditCard(UUID id) {
        return null;
    }

    @Override
    public Boolean deleteAllCreditCards() {
        return null;
    }

    @Override
    public Optional<List<TarjetaCredito>> findAllCreditCardsByUserId(String userId) {
        return Optional.empty();
    }

    @Override
    public List<Usuario> findAllUsers() {
        return List.of();
    }

    @Override
    public List<Usuario> findUsersByName(String name) {
        return List.of();
    }

    @Override
    public Usuario findUserById(Integer id) {
        return null;
    }

    @Override
    public Usuario saveUser(Usuario user) {
        return null;
    }

    @Override
    public Usuario updateUser(UUID uuid, Usuario user) {
        return null;
    }

    @Override
    public Boolean deleteUserById(UUID id) {
        return null;
    }

    @Override
    public Boolean deleteAllUsers() {
        return null;
    }

}
