package org.example.client.repository;

import org.example.models.Cliente;

import java.util.List;
import java.util.UUID;

public interface ClientsRepository {
    List<Cliente> findAllClientes();
    Cliente findClientById(UUID id);
    List<Cliente> findClientByName(String name);
    Cliente saveClient(Cliente client);
    Cliente updateClient(UUID id, Cliente updatedClient);
    Boolean deleteCientById(UUID id);
    Boolean deleteAllClients();
}
