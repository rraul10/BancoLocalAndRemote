package org.example.client.repository;

import org.example.client.database.LocalDataBaseManager;
import org.example.models.Cliente;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientsRepositoryImpl implements ClientsRepository, UsersRepository, CreditCardRepository {
  private final Logger logger = LoggerFactory.getLogger(ClientsRepositoryImpl.class);
  private final LocalDataBaseManager localDataBaseManager;

  public ClientsRepositoryImpl(LocalDataBaseManager localDataBaseManager) {
    this.localDataBaseManager = localDataBaseManager;
  }
  //----------------------------------------------------------------
    @Override
    public List<Cliente> findAllClientes()  {
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

    //----------------------------------------------------------------

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

    //----------------------------------------------------------------

    @Override
    public List<Usuario> findAllUsers() {
      logger.debug("Obteniendo todos los usuarios...");
      List<Usuario> users = new ArrayList<>();
      String query = "SELECT * FROM Usuario";

      try (Connection connection = localDataBaseManager.connect();
           PreparedStatement statement = connection.prepareStatement(query);
           ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          users.add(Usuario.builder()
              .id(resultSet.getObject("id", UUID.class))
              .name(resultSet.getString("name"))
              .username(resultSet.getString("username"))
              .email(resultSet.getString("email"))
              .build());
        }
      } catch (SQLException e) {
        logger.error("Error al obtener usuarios", e);
      }
        return users;
    }

    @Override
    public List<Usuario> findUsersByName(String name) {
      logger.debug("Obteniendo usuarios por nombre...");
        List<Usuario> users = new ArrayList<>();
      String query = "SELECT * FROM Usuario WHERE name = ?";
      try (Connection connection = localDataBaseManager.connect();
           PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setString(1, name);
        try (ResultSet resultSet = statement.executeQuery()) {

          while (resultSet.next()) {
            users.add(Usuario.builder()
                .id(resultSet.getObject("id", UUID.class))
                .name(resultSet.getString("name"))
                .username(resultSet.getString("username"))
                .email(resultSet.getString("email"))
                .build());
          }
          return users;
        }
      } catch (SQLException e) {
        logger.error("Error al obtener usuarios por nombre", e);
      }
        return users;
    }

    @Override
    public Usuario findUserById(Integer id) {
        logger.debug("Obteniendo usuario por id...");
        Usuario usuario = null;
        String query = "SELECT * FROM Usuario WHERE id = ?";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
          statement.setInt(1, id);
          try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
              usuario = Usuario.builder()
                  .id(resultSet.getObject("id", UUID.class))
                  .name(resultSet.getString("name"))
                  .username(resultSet.getString("username"))
                  .email(resultSet.getString("email"))
                  .build();
            }
          }
        } catch (SQLException e) {
          logger.error("Error al obtener usuario por id", e);
        }
        return usuario;
    }

    @Override
    public Usuario saveUser(Usuario user) {
      logger.debug("Guardando usuario...");
      String query = "INSERT INTO Usuario (id, name, username, email) VALUES (?, ?, ?, ?)";
      try (Connection connection = localDataBaseManager.connect();
           PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setObject(1, user.getId());
        statement.setString(2, user.getName());
        statement.setString(3, user.getUsername());
        statement.setString(4, user.getEmail());
        statement.executeUpdate();
      } catch (SQLException e) {
        logger.error("Error al guardar usuario", e);
      }
        return user;
    }

    @Override
    public Usuario updateUser(UUID uuid, Usuario user) {
      logger.debug("Actualizando usuario...");
      String query = "UPDATE Usuario SET name = ?, username = ?, email = ? WHERE id = ?";
      try (Connection connection = localDataBaseManager.connect();
           PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setString(1, user.getName());
        statement.setString(2, user.getUsername());
        statement.setString(3, user.getEmail());
        statement.setObject(4, uuid);
        statement.executeUpdate();
      } catch (SQLException e) {
        logger.error("Error al actualizar usuario", e);
      }
        return user;
    }

    @Override
    public Boolean deleteUserById(UUID id) {
      logger.debug("Eliminando usuario por id...");
      String query = "DELETE FROM Usuario WHERE id = ?";
      try (Connection connection = localDataBaseManager.connect();
           PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setObject(1, id);
        statement.executeUpdate();
      } catch (SQLException e) {
        logger.error("Error al eliminar usuario por id", e);
      }
        return null;
    }

    @Override
    public Boolean deleteAllUsers() {
      logger.debug("Eliminando todos los usuarios...");
      String query = "DELETE FROM Usuario";
      try (Connection connection = localDataBaseManager.connect();
           PreparedStatement statement = connection.prepareStatement(query)) {
        statement.executeUpdate();
      } catch (SQLException e) {
        logger.error("Error al eliminar todos los usuarios", e);
      }
        return null;
    }

}
