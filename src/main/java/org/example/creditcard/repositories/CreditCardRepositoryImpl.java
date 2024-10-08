package org.example.creditcard.repositories;

import org.example.creditcard.database.DataBaseManager;
import org.example.models.TarjetaCredito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CreditCardRepositoryImpl implements CreditCardRepository {
    private final Logger logger = LoggerFactory.getLogger(CreditCardRepository.class);
    private final DataBaseManager dataBaseManager;

    public CreditCardRepositoryImpl(DataBaseManager dataBaseManager) {
        this.dataBaseManager = dataBaseManager;
    }



    @Override
    public List<TarjetaCredito> getAll() {
        logger.info("Obteniendo tarjetas de credito...");
        List<TarjetaCredito> tarjetas = new ArrayList<>();
        String query = "SELECT * FROM credit-card";

        // Esto es un try-with-resources, se cierra automáticamente
        try (Connection connection = dataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                TarjetaCredito tarjeta = TarjetaCredito.builder()
                        .id((java.util.UUID) resultSet.getObject("uuid"))
                        .numero(resultSet.getString("numero"))
                        .nombreTitular(resultSet.getString("nombreTitular"))
                        .clientID((java.util.UUID) resultSet.getObject("clientID"))
                        .fechaCaducidad(resultSet.getString("fechaCaducidad"))
                        .createdAt(resultSet.getObject("created_at", LocalDateTime.class))
                        .updatedAt(resultSet.getObject("updated_at", LocalDateTime.class))
                        .isDeleted(resultSet.getObject("isDeleted", boolean.class))
                        .build();
                tarjetas.add(tarjeta);
            }
        } catch (SQLException e) {
            logger.error("Error al obtener tarjetas", e);
        }

        return tarjetas;
    }

    @Override
    public Optional<TarjetaCredito> getById(UUID id) {
        logger.info("Obteniendo tarjeta por id...");
        String query = "SELECT * FROM credit-card WHERE id = ?";

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
                            .clientID(resultSet.getObject("clientID", UUID.class))
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

    @Override
    public TarjetaCredito create(TarjetaCredito creditcard) {
        logger.info("Creando tarjeta...");
        String query = "INSERT INTO credit-card (uuid, numero,nombreTitular,clienteId,fechaCaducidad, created_at, updated_at,isDeleted) VALUES (?, ?, ?, ?,?,?,?,?)";
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

    @Override
    public TarjetaCredito update(UUID id, TarjetaCredito creditcard) {
        logger.info("Actualizando tarjeta...");
        String query = "UPDATE credit-card SET nombreTitular = ?,clienteId = ? ,fechaCaducidad = ? ,updated_at = ? WHERE id = ?";
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

    @Override
    public boolean delete(UUID id) {
        logger.info("Borrando persona...");
        String query = "DELETE FROM credit-card WHERE id = ?";
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
}
