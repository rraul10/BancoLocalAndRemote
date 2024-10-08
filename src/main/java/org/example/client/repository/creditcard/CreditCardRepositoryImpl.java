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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CreditCardRepositoryImpl implements CreditCardRepository{
    private final Logger logger = LoggerFactory.getLogger(CreditCardRepository.class);
    private final LocalDataBaseManager localDataBaseManager;
    private final UsersRepository userRepository;
    /**
     * Constructor de la clase. Inicializa la clase con la base de datos local y el repositorio de usuarios.
     * @param localDataBaseManager La base de datos local.
     * @param userRepository El repositorio de usuarios.
     */
    public CreditCardRepositoryImpl(LocalDataBaseManager localDataBaseManager, UsersRepository userRepository) {
        this.localDataBaseManager = localDataBaseManager;
        this.userRepository = userRepository;
    }
    /**
     * Obtiene todas las tarjetas de credito.
     * @return La lista de tarjetas de credito encontradas.
     */
    @Override
    public List<TarjetaCredito> findAllCreditCards() {
        logger.debug("Obteniendo todas las tarjetas de credito...");
        List<TarjetaCredito> creditCards = new ArrayList<>();
        String query = "SELECT * FROM Tarjeta";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Optional<Usuario> user = userRepository.findUserById(Long.parseLong(resultSet.getObject("clientID", String.class)));
                    creditCards.add(TarjetaCredito.builder()
                            .id(UUID.fromString(resultSet.getString("id")))
                            .numero(resultSet.getString("numero"))
                            .clientID(Long.parseLong(resultSet.getObject("clientID", String.class)))
                            .nombreTitular(user.get().getName())
                            .fechaCaducidad(resultSet.getString("fechaCaducidad"))
                            .createdAt(resultSet.getObject("created_at", LocalDateTime.class))
                            .updatedAt(resultSet.getObject("updated_at", LocalDateTime.class))
                            .isDeleted(resultSet.getInt("isDeleted") == 1)
                            .build());
                }
                return creditCards;
            }
        } catch (SQLException e) {
            logger.error("Error al obtener tarjetas de credito", e);
        }
        return creditCards;
    }

    /**
     * Obtiene una tarjeta de credito por su id.
     * @param id El id de la tarjeta de credito.
     * @return La tarjeta de credito encontrada o null si no existe.
     */
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
                    Optional<Usuario> user = userRepository.findUserById(resultSet.getObject("clientID", Long.class));
                    creditCard = TarjetaCredito.builder()
                            .id(resultSet.getObject("id", UUID.class))
                            .numero(resultSet.getString("numero"))
                            .clientID(Long.parseLong(resultSet.getObject("clientID", String.class)))
                            .nombreTitular(resultSet.getString(user.get().getName()))
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

    /**
     * Obtiene una tarjeta de credito por su numero.
     * @param number El n mero de la tarjeta de credito.
     * @return La tarjeta de credito encontrada o null si no existe.
     */
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
                    Optional<Usuario> user = userRepository.findUserById(resultSet.getObject("clientID", Long.class));
                    creditCard = TarjetaCredito.builder()
                            .id(resultSet.getObject("id", UUID.class))
                            .numero(resultSet.getString("numero"))
                            .clientID(Long.parseLong(resultSet.getObject("clientID", String.class)))
                            .nombreTitular(resultSet.getString(user.get().getName()))
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

    /**
     * Guarda una tarjeta de credito en la base de datos.
     * @param creditCard La tarjeta de credito a guardar.
     * @return La tarjeta de credito guardada.
     */
    @Override
    public TarjetaCredito saveCreditCard(TarjetaCredito creditCard) {
        logger.debug("Guardando tarjeta de credito...");
        String query = "INSERT INTO Tarjeta (id, numero, clientID, fechaCaducidad, created_at, updated_at, isDeleted) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, creditCard.getId());
            statement.setString(2, creditCard.getNumero());
            statement.setObject(3, creditCard.getClientID());
            statement.setObject(4, creditCard.getFechaCaducidad());
            statement.setObject(5, creditCard.getCreatedAt());
            statement.setObject(6, creditCard.getUpdatedAt());
            statement.setObject(7, creditCard.getIsDeleted());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al guardar tarjeta de credito", e);
        }
        return creditCard;
    }

    /**
     * Actualiza una tarjeta de credito existente, solo permite actualizar la fecha de caducidad.
     * @param creditCard La tarjeta de credito a actualizar.
     * @return La tarjeta de credito actualizada.
     */
    @Override
    public TarjetaCredito updateCreditCard(TarjetaCredito creditCard) {
        logger.debug("Actualizando tarjeta de credito...");
        String query = "UPDATE Tarjeta SET fechaCaducidad = ?, updated_at = ? WHERE id = ?";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, creditCard.getFechaCaducidad());
            statement.setObject(2, LocalDateTime.now());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al actualizar tarjeta de credito", e);
        }
        return creditCard;
    }

    /**
     * Elimina una tarjeta de credito por su id.
     * @param id El id de la tarjeta de credito a eliminar.
     * @return true si se elimino la tarjeta, false en caso contrario.
     */
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

    /**
     * Elimina todas las tarjetas de credito.
     * @return true si se eliminararon todas las tarjetas, false en caso contrario.
     */
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

    /**
     * Obtiene todas las tarjetas de credito de un usuario.
     * @param userId El id del usuario.
     * @return Una lista de tarjetas de credito.
     */
    @Override
    public List<TarjetaCredito> findAllCreditCardsByUserId(Long userId) {
        logger.debug("Obteniendo todas las tarjetas de credito por usuario...");
        Optional<Usuario> user = userRepository.findUserById(userId);
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
                            .clientID(Long.parseLong(resultSet.getObject("clientID", String.class)))
                            .nombreTitular(resultSet.getString(user.get().getName()))
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
