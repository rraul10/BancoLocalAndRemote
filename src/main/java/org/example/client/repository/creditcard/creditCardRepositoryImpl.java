package org.example.client.repository.creditcard;

import org.example.client.database.LocalDataBaseManager;
import org.example.client.repository.user.UsersRepository;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class creditCardRepositoryImpl implements CreditCardRepository{
    private final Logger logger = LoggerFactory.getLogger(CreditCardRepository.class);
    private final LocalDataBaseManager localDataBaseManager;
    private final UsersRepository userRepository;
    public creditCardRepositoryImpl(LocalDataBaseManager localDataBaseManager, UsersRepository userRepository) {
        this.localDataBaseManager = localDataBaseManager;
        this.userRepository = userRepository;
    }
    @Override
    public List<TarjetaCredito> findAllCreditCards() {
        logger.debug("Obteniendo todas las tarjetas de credito...");
        List<TarjetaCredito> creditCards = new ArrayList<>();
        String query = "SELECT * FROM Tarjeta";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Usuario user = userRepository.findUserById(resultSet.getObject("clientID", UUID.class));
                    creditCards.add(TarjetaCredito.builder()
                            .id(resultSet.getObject("id", UUID.class))
                            .numero(resultSet.getString("numero"))
                            .clientID(resultSet.getObject("clientID", UUID.class))
                            .nombreTitular(resultSet.getString(user.getName()))
                            .fechaCaducidad(resultSet.getString("fechaCaducidad"))
                            .createdAt(resultSet.getObject("created_at", LocalDateTime.class))
                            .updatedAt(resultSet.getObject("updated_at", LocalDateTime.class))
                            .isDeleted(resultSet.getObject("isDeleted", boolean.class))
                            .build());
                }
                return creditCards;
            }
        } catch (SQLException e) {
            logger.error("Error al obtener tarjetas de credito", e);
        }
        return creditCards;
    }

    @Override
    public TarjetaCredito findCreditCardById(UUID id) {
        logger.debug("Obteniendo tarjeta de credito por id...");
        TarjetaCredito creditCard = null;
        String query = "SELECT * FROM Tarjeta WHERE id = ?";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Usuario user = userRepository.findUserById(resultSet.getObject("clientID", UUID.class));
                    creditCard = TarjetaCredito.builder()
                            .id(resultSet.getObject("id", UUID.class))
                            .numero(resultSet.getString("numero"))
                            .clientID(resultSet.getObject("clientID", UUID.class))
                            .nombreTitular(resultSet.getString(user.getName()))
                            .fechaCaducidad(resultSet.getString("fechaCaducidad"))
                            .createdAt(resultSet.getObject("created_at", LocalDateTime.class))
                            .updatedAt(resultSet.getObject("updated_at", LocalDateTime.class))
                            .isDeleted(resultSet.getObject("isDeleted", boolean.class))
                            .build();
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener tarjeta de credito por id", e);
        }
        return creditCard;
    }

    @Override
    public TarjetaCredito findCreditCardByNumber(String number) {
        logger.debug("Obteniendo tarjeta de credito por numero...");
        TarjetaCredito creditCard = null;
        String query = "SELECT * FROM Tarjeta WHERE numero = ?";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, number);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Usuario user = userRepository.findUserById(resultSet.getObject("clientID", UUID.class));
                    creditCard = TarjetaCredito.builder()
                            .id(resultSet.getObject("id", UUID.class))
                            .numero(resultSet.getString("numero"))
                            .clientID(resultSet.getObject("clientID", UUID.class))
                            .nombreTitular(resultSet.getString(user.getName()))
                            .fechaCaducidad(resultSet.getString("fechaCaducidad"))
                            .createdAt(resultSet.getObject("created_at", LocalDateTime.class))
                            .updatedAt(resultSet.getObject("updated_at", LocalDateTime.class))
                            .isDeleted(resultSet.getObject("isDeleted", boolean.class))
                            .build();
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener tarjeta de credito por numero", e);
        }
        return creditCard;
    }

    @Override
    public TarjetaCredito saveCreditCard(TarjetaCredito creditCard) {
        logger.debug("Guardando tarjeta de credito...");
        String query = "INSERT INTO Tarjeta (id, numero, clientID, fechaCaducidad) VALUES (?, ?, ?, ?)";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, creditCard.getId());
            statement.setString(2, creditCard.getNumero());
            statement.setObject(3, creditCard.getClientID());
            statement.setObject(4, creditCard.getFechaCaducidad());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al guardar tarjeta de credito", e);
        }
        return creditCard;
    }

    @Override
    public TarjetaCredito updateCreditCard(TarjetaCredito creditCard) {
        logger.debug("Actualizando tarjeta de credito...");
        String query = "UPDATE Tarjeta SET numero = ?, clientID = ?, fechaCaducidad = ? WHERE id = ?";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, creditCard.getNumero());
            statement.setObject(2, creditCard.getClientID());
            statement.setObject(3, creditCard.getFechaCaducidad());
            statement.setObject(4, creditCard.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al actualizar tarjeta de credito", e);
        }
        return creditCard;
    }

    @Override
    public Boolean deleteCreditCard(UUID id) {
        logger.debug("Eliminando tarjeta de credito...");
        String query = "DELETE FROM Tarjeta WHERE id = ?";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al eliminar tarjeta de credito", e);
        }
        return true;
    }

    @Override
    public Boolean deleteAllCreditCards() {
        logger.debug("Eliminando todas las tarjetas de credito...");
        String query = "DELETE FROM Tarjeta";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al eliminar todas las tarjetas de credito", e);
        }
        return true;
    }

    @Override
    public List<TarjetaCredito> findAllCreditCardsByUserId(UUID userId) {
        logger.debug("Obteniendo todas las tarjetas de credito por usuario...");
        Usuario user = userRepository.findUserById(userId);
        List<TarjetaCredito> creditCards = new ArrayList<>();
        String query = "SELECT * FROM Tarjeta WHERE clientID = ?";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    creditCards.add(TarjetaCredito.builder()
                            .id(resultSet.getObject("id", UUID.class))
                            .numero(resultSet.getString("numero"))
                            .clientID(resultSet.getObject("clientID", UUID.class))
                            .nombreTitular(resultSet.getString(user.getName()))
                            .fechaCaducidad(resultSet.getString("fechaCaducidad"))
                            .createdAt(resultSet.getObject("created_at", LocalDateTime.class))
                            .updatedAt(resultSet.getObject("updated_at", LocalDateTime.class))
                            .isDeleted(resultSet.getObject("isDeleted", boolean.class))
                            .build());
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener tarjetas de credito por usuario", e);
        }
        return creditCards;
    }
}
