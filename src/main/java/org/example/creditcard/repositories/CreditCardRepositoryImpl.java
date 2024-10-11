package org.example.creditcard.repositories;

import org.example.creditcard.database.DataBaseManager;
import org.example.models.TarjetaCredito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del repositorio de tarjetas de crédito.
 * Esta clase proporciona métodos para interactuar con la base de datos y realizar
 * operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre las tarjetas de crédito.
 * @author Raúl Fernández, Alvaro Herrero, Javier Ruíz, Javier Hernández, Samuel Cortés, Yahya El Hadri.
 * @since 1.0
 */

public class CreditCardRepositoryImpl implements CreditCardRepository {
    private final Logger logger = LoggerFactory.getLogger(CreditCardRepository.class);
    private final DataBaseManager dataBaseManager;

    public CreditCardRepositoryImpl(DataBaseManager dataBaseManager) {
        this.dataBaseManager = dataBaseManager;
    }

    /**
     * Obtiene todas las tarjetas de crédito.
     * @author Raúl Fernández, Alvaro Herrero, Javier Ruíz, Javier Hernández, Samuel Cortés, Yahya El Hadri.
     * @since 1.0
     * @return lista de tarjetas de crédito
     */

    @Override
    public List<TarjetaCredito> getAll() {
        logger.info("Obteniendo tarjetas de credito...");
        List<TarjetaCredito> tarjetas = new ArrayList<>();
        String query = "SELECT * FROM Tarjeta";

        // Esto es un try-with-resources, se cierra automáticamente
        try (Connection connection = dataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                TarjetaCredito tarjeta = TarjetaCredito.builder()
                        .id((java.util.UUID) resultSet.getObject("uuid"))
                        .numero(resultSet.getString("numero"))
                        .nombreTitular(resultSet.getString("nombreTitular"))
                        .clientID(Long.parseLong(resultSet.getObject("clientID", String.class)))
                        .fechaCaducidad(resultSet.getString("fechaCaducidad"))
                        .createdAt(resultSet.getObject("created_at", LocalDateTime.class))
                        .updatedAt(resultSet.getObject("updated_at", LocalDateTime.class))
                        .isDeleted(resultSet.getObject("isDeleted", Boolean.class))
                        .build();
                tarjetas.add(tarjeta);
            }
        } catch (SQLException e) {
            logger.error("Error al obtener tarjetas", e);
        }

        return tarjetas;
    }

    /**
     * Obtiene una tarjeta de crédito por su ID.
     * @author Raúl Fernández, Alvaro Herrero, Javier Ruíz, Javier Hernández, Samuel Cortés, Yahya El Hadri.
     * @since 1.0
     * @param id ID de la tarjeta de crédito
     * @return tarjeta de crédito si existe, Optional.empty() si no existe
     */

    @Override
    public Optional<TarjetaCredito> getById(UUID id) {
        logger.info("Obteniendo tarjeta por id...");
        String query = "SELECT * FROM Tarjeta WHERE id = ?";

        // Esto es un try-with-resources, se cierra automáticamente
        try (Connection connection = dataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Sustituimos el ? por el id
            statement.setString(1, id.toString());
            // Ejecutamos la consulta como try with resources
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(TarjetaCredito.builder()
                            .id(resultSet.getObject("uuid", UUID.class))
                            .numero(resultSet.getString("numero"))
                            .nombreTitular(resultSet.getString("nombreTitular"))
                            .clientID(( resultSet.getObject("clientID", Long.class)))
                            .fechaCaducidad(resultSet.getString("fechaCaducidad"))
                            .createdAt(resultSet.getObject("created_at", LocalDateTime.class))
                            .updatedAt(resultSet.getObject("updated_at", LocalDateTime.class))
                            .isDeleted(resultSet.getObject("isDeleted", Boolean.class))
                            .build());
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener tarjeta por id", e);
        }
        return Optional.empty();
    }

    /**
     * Crea una nueva tarjeta de crédito.
     * @author Raúl Fernández, Alvaro Herrero, Javier Ruíz, Javier Hernández, Samuel Cortés, Yahya El Hadri.
     * @since 1.0
     * @param creditcard tarjeta de crédito a crear
     * @return tarjeta de crédito creada
     */

    @Override
    public TarjetaCredito create(TarjetaCredito creditcard) {
        logger.info("Creando tarjeta...");
        String query = "INSERT INTO Tarjeta (uuid, numero,nombreTitular,clienteId,fechaCaducidad, created_at, updated_at,isDeleted) VALUES (?, ?, ?, ?,?,?,?,?)";
        var uuid = java.util.UUID.randomUUID();
        var timeStamp = LocalDateTime.now();
        // Esto es un try-with-resources, se cierra automáticamente
        try (Connection connection = dataBaseManager.connect();
             // Este statement nos permite recuperar la clave generada
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Sustituimos los ? por los valores de la persona
            statement.setObject(1, uuid);
            statement.setString(2, creditcard.getNumero());
            statement.setString(3, creditcard.getNombreTitular());
            statement.setObject(4, creditcard.getClientID());
            statement.setObject(5, creditcard.getFechaCaducidad());
            statement.setObject(6, timeStamp);
            statement.setObject(7, timeStamp);
            statement.setBoolean(8, creditcard.getIsDeleted());

            // Ejecutamos la consulta
            statement.executeUpdate();

            // Recuperamos la clave generada
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    String id = generatedKeys.getString(1);
                    creditcard.setId(UUID.fromString(id)); // Asumiendo que Persona tiene un método setId
                    creditcard.setNumero(creditcard.getNumero());
                    creditcard.setNombreTitular(creditcard.getNombreTitular());
                    creditcard.setClientID(creditcard.getClientID());
                    creditcard.setFechaCaducidad(creditcard.getFechaCaducidad());
                    creditcard.setCreatedAt(timeStamp);
                    creditcard.setUpdatedAt(timeStamp);
                    creditcard.setIsDeleted(creditcard.getIsDeleted());
                    return creditcard;
                } else {
                    throw new SQLException("No se pudo obtener la clave generada.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error al crear tarjeta", e);
        }

        return creditcard;
    }

    /**
     * Actualiza una tarjeta de crédito existente.
     * @author Raúl Fernández, Alvaro Herrero, Javier Ruíz, Javier Hernández, Samuel Cortés, Yahya El Hadri.
     * @since 1.0
     * @param id ID de la tarjeta de crédito a actualizar
     * @param creditcard tarjeta de crédito actualizada
     * @return tarjeta de crédito actualizada
     */

    @Override
    public TarjetaCredito update(UUID id, TarjetaCredito creditcard) {
        logger.info("Actualizando tarjeta...");
        String query = "UPDATE Tarjeta SET nombreTitular = ?,clienteId = ? ,fechaCaducidad = ? ,updated_at = ? WHERE id = ?";
        LocalDateTime timeStamp = LocalDateTime.now();
        // Esto es un try-with-resources, se cierra automáticamente
        try (Connection connection = dataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Sustituimos los ? por los valores de la persona
            statement.setString(1, creditcard.getNombreTitular());
            statement.setObject(2, creditcard.getClientID());
            statement.setObject(3, creditcard.getFechaCaducidad());
            statement.setObject(4, timeStamp);

            // Ejecutamos la consulta
            int rows = statement.executeUpdate();
            if (rows > 0) {
                creditcard.setNombreTitular(creditcard.getNombreTitular());
                creditcard.setClientID(creditcard.getClientID());
                creditcard.setFechaCaducidad(creditcard.getFechaCaducidad());
                creditcard.setUpdatedAt(timeStamp);
                return creditcard;
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar tarjeta", e);
        }
        return null;
    }

    /**
     * Elimina una tarjeta de crédito por su ID.
     * @author Raúl Fernández, Alvaro Herrero, Javier Ruíz, Javier Hernández, Samuel Cortés, Yahya El Hadri.
     * @since 1.0
     * @param id ID de la tarjeta de crédito a eliminar
     * @return true si se elimina la tarjeta, false si no se elimina
     */

    @Override
    public boolean delete(UUID id) {
        logger.info("Borrando persona...");
        String query = "DELETE FROM Tarjeta WHERE id = ?";
        // Esto es un try-with-resources, se cierra automáticamente
        try (Connection connection = dataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Sustituimos el ? por el id
            statement.setString(1, id.toString());


            // Ejecutamos la consulta
            int rows = statement.executeUpdate();
            if (rows > 0) {
                return true;
            } else {
                logger.warn("No se ha borrado ninguna tarjeta");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error al borrar tarjeta", e);
        }

        return false;
    }

    @Override
    public List<TarjetaCredito> findAllCreditCardsByUserId(Long id) {
        logger.info("Obteniendo tarjetas de credito seguún el id del usuario" + id);
        List<TarjetaCredito> tarjetas = new ArrayList<>();
        String query = "SELECT * FROM Tarjeta where clientID = ?";

        // Esto es un try-with-resources, se cierra automáticamente
        try (Connection connection = dataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                TarjetaCredito tarjeta = TarjetaCredito.builder()
                        .id((java.util.UUID) resultSet.getObject("uuid"))
                        .numero(resultSet.getString("numero"))
                        .nombreTitular(resultSet.getString("nombreTitular"))
                        .clientID(Long.parseLong(resultSet.getObject("clientID", String.class)))
                        .fechaCaducidad(resultSet.getString("fechaCaducidad"))
                        .createdAt(resultSet.getObject("created_at", LocalDateTime.class))
                        .updatedAt(resultSet.getObject("updated_at", LocalDateTime.class))
                        .isDeleted(resultSet.getObject("isDeleted", Boolean.class))
                        .build();
                tarjetas.add(tarjeta);
            }
        } catch (SQLException e) {
            logger.error("Error al obtener tarjetas", e);
        }

        return tarjetas;
    }
}
