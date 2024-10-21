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
 * Implementacion del repositorio de tarjetas de credito.
 * Esta clase proporciona metodos para interactuar con la base de datos y realizar
 * operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre las tarjetas de credito.
 * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
 * @since 1.0
 */

public class CreditCardRepositoryImpl implements CreditCardRepository {
    private final Logger logger = LoggerFactory.getLogger(CreditCardRepository.class);
    private final DataBaseManager dataBaseManager;

    public CreditCardRepositoryImpl(DataBaseManager dataBaseManager) {
        this.dataBaseManager = dataBaseManager;
    }

    /**
     * Obtiene todas las tarjetas de credito.
     * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
     * @since 1.0
     * @return lista de tarjetas de credito
     */

    @Override
    public List<TarjetaCredito> getAll() {
        logger.info("Obteniendo tarjetas de credito...");
        List<TarjetaCredito> tarjetas = new ArrayList<>();
        String query = "SELECT * FROM Tarjeta";


        try (Connection connection = dataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                TarjetaCredito tarjeta = TarjetaCredito.builder()
                        .id((java.util.UUID) resultSet.getObject("id"))
                        .numero(resultSet.getString("numero"))
                        .nombreTitular(resultSet.getString("nombreTitular"))
                        .clientID(Long.parseLong(resultSet.getObject("clienteID", String.class)))
                        .fechaCaducidad(resultSet.getString("fechaCaducidad"))
                        .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
                        .updatedAt(resultSet.getObject("updatedAt", LocalDateTime.class))
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
     * Obtiene una tarjeta de credito por su ID.
     * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
     * @since 1.0
     * @param id ID de la tarjeta de credito
     * @return tarjeta de credito si existe, Optional.empty() si no existe
     */

    @Override
    public Optional<TarjetaCredito> getById(UUID id) {
        logger.info("Obteniendo tarjeta por id...");
        String query = "SELECT * FROM Tarjeta WHERE id = ?";


        try (Connection connection = dataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, id.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(TarjetaCredito.builder()
                            .id(resultSet.getObject("id", UUID.class))
                            .numero(resultSet.getString("numero"))
                            .nombreTitular(resultSet.getString("nombreTitular"))
                            .clientID((resultSet.getObject("clienteID", Long.class)))
                            .fechaCaducidad(resultSet.getString("fechaCaducidad"))
                            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
                            .updatedAt(resultSet.getObject("updatedAt", LocalDateTime.class))
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
     * Crea una nueva tarjeta de credito.
     * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
     * @since 1.0
     * @param creditcard tarjeta de credito a crear
     * @return tarjeta de credito creada
     */

    @Override
    public TarjetaCredito create(TarjetaCredito creditcard) {
        logger.info("Creando tarjeta...");
        String query = "INSERT INTO Tarjeta (id, numero,nombreTitular,clienteId,fechaCaducidad, createdAt, updatedAt,isDeleted) VALUES (?,?,?,?,?,?,?,?)";
        var uuid = java.util.UUID.randomUUID();
        var timeStamp = LocalDateTime.now();

        try (Connection connection = dataBaseManager.connect();

             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {


            statement.setObject(1, creditcard.getId());
            statement.setString(2, creditcard.getNumero());
            statement.setString(3, creditcard.getNombreTitular());
            statement.setObject(4, creditcard.getClientID());
            statement.setObject(5, creditcard.getFechaCaducidad());
            statement.setObject(6, timeStamp);
            statement.setObject(7, timeStamp);
            statement.setBoolean(8, creditcard.getIsDeleted());


            statement.executeUpdate();


            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    String id = generatedKeys.getString(1);
                    creditcard.setId(UUID.fromString(id)); // Asumiendo que Persona tiene un metodo setId
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
     * Actualiza una tarjeta de credito existente.
     * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
     * @since 1.0
     * @param id ID de la tarjeta de credito a actualizar
     * @param creditcard tarjeta de credito actualizada
     * @return tarjeta de credito actualizada
     */

    @Override
    public TarjetaCredito update(UUID id, TarjetaCredito creditcard) {
        logger.info("Actualizando tarjeta...");
        String query = "UPDATE Tarjeta SET nombreTitular = ?,clienteId = ? ,fechaCaducidad = ? ,updatedAt = ? WHERE id = ?";
        LocalDateTime timeStamp = LocalDateTime.now();

        try (Connection connection = dataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {


            statement.setString(1, creditcard.getNombreTitular());
            statement.setObject(2, creditcard.getClientID());
            statement.setObject(3, creditcard.getFechaCaducidad());
            statement.setObject(4, timeStamp);


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
     * Elimina una tarjeta de credito por su ID.
     * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
     * @since 1.0
     * @param id ID de la tarjeta de credito a eliminar
     * @return true si se elimina la tarjeta, false si no se elimina
     */

    @Override
    public boolean delete(UUID id) {
        logger.info("Borrando tarjeta...");
        String query = "DELETE FROM Tarjeta WHERE id = ?";

        try (Connection connection = dataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {


            statement.setString(1, id.toString());


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

    /**
     * Encontrar la tarjeta por el id del usuario.
     * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
     * @since 1.0
     * @param id ID del usuario a encontrar
     * @return true si se se encuentra, false si no se elimina
     */

    @Override
    public List<TarjetaCredito> findAllCreditCardsByUserId(Long id) {
        logger.info("Obteniendo tarjetas de credito seguún el id del usuario" + id);
        List<TarjetaCredito> tarjetas = new ArrayList<>();
        String query = "SELECT * FROM Tarjeta where clientID = ?";


        try (Connection connection = dataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                TarjetaCredito tarjeta = TarjetaCredito.builder()
                        .id((java.util.UUID) resultSet.getObject("uuid"))
                        .numero(resultSet.getString("numero"))
                        .nombreTitular(resultSet.getString("nombreTitular"))
                        .clientID(Long.parseLong(resultSet.getObject("clienteID", String.class)))
                        .fechaCaducidad(resultSet.getString("fechaCaducidad"))
                        .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
                        .updatedAt(resultSet.getObject("updatedAt", LocalDateTime.class))
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
