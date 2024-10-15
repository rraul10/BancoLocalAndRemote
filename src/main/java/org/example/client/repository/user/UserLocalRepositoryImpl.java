package org.example.client.repository.user;

import org.example.client.database.LocalDataBaseManager;
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

/**
 * Implementacion del repositorio de usuarios.
 * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
 * @since 1.0
 */

public class UserLocalRepositoryImpl implements UsersRepository{
    private final Logger logger = LoggerFactory.getLogger(UserLocalRepositoryImpl.class);
    private final LocalDataBaseManager localDataBaseManager;

    /**
     * Constructor que recibe el gestor de la base de datos.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param localDataBase gestor de la base de datos.
     */

    public UserLocalRepositoryImpl(LocalDataBaseManager localDataBase) {this.localDataBaseManager = localDataBase;}
    /**
     * Obtiene una lista de todos los usuarios en la base de datos.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @return una lista con todos los usuarios encontrados
     */
    @Override
    public List<Usuario> findAllUsers() {
        logger.debug("Obteniendo todos los usuarios...");
        List<Usuario> users = new ArrayList<>();
        String query = "SELECT * FROM Cliente";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(Usuario.builder()
                        .id(Long.parseLong(resultSet.getObject("id", String.class)))
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

    /**
     * Obtiene una lista de usuarios cuyo nombre coincide con el especificado.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param name el nombre del usuario a buscar
     * @return una lista con los usuarios encontrados
     */

    @Override
    public List<Usuario> findUsersByName(String name) {
        logger.debug("Obteniendo usuarios por nombre...");
        List<Usuario> users = new ArrayList<>();
        String query = "SELECT * FROM Cliente WHERE name = ?";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    users.add(Usuario.builder()
                            .id(resultSet.getObject("id", Long.class))
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

    /**
     * Busca un usuario por su id en la base de datos.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param id el id del usuario a buscar
     * @return el usuario encontrado
     */

    @Override
    public Optional<Usuario> findUserById(Long id) {
        logger.debug("Obteniendo usuario por id...");
        String query = "SELECT * FROM Cliente WHERE id = ?";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Usuario usuario = Usuario.builder()
                            .id(resultSet.getObject("id", Long.class))
                            .name(resultSet.getString("name"))
                            .username(resultSet.getString("username"))
                            .email(resultSet.getString("email"))
                            .build();
                    return Optional.of(usuario);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener usuario por id", e);
        }
        return Optional.empty();
    }


    /**
     * Guarda un usuario en la base de datos.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param user el usuario a guardar
     * @return el usuario guardado
     */

    @Override
    public Optional<Usuario> saveUser(Usuario user) {
        logger.debug("Guardando usuario...");
        String query = "INSERT INTO Cliente (id, name, username, email) VALUES (?, ?, ?, ?)";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, user.getId());
            statement.setString(2, user.getName());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getEmail());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error al guardar usuario", e);
            return Optional.empty();
        }
    }
    /**
     * Actualiza un usuario en la base de datos.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param uuid el id del usuario a actualizar
     * @param user el usuario con los datos a actualizar
     * @return el usuario actualizado
     */

    @Override
    public Optional<Usuario> updateUser(Long uuid, Usuario user) {
        logger.debug("Actualizando usuario...");
        String query = "UPDATE Cliente SET name = ?, username = ?, email = ? WHERE id = ?";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getEmail());
            statement.setObject(4, uuid);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.of(user);
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            logger.error("Error al actualizar usuario", e);
            return Optional.empty();
        }
    }

    /**
     * Elimina un usuario en la base de datos por su id.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param id el id del usuario a eliminar
     * @return {@code true} si se elimin  el usuario, {@code false} en caso de error.
     */

    @Override
    public Boolean deleteUserById(Long id) {
        logger.debug("Eliminando usuario por id...");
        String query = "DELETE FROM Cliente WHERE id = ?";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                logger.warn("No se encontro ningun usuario con el id: " + id);
                return false;
            }

            logger.info("Usuario con id " + id + " eliminado correctamente.");
            return true;
        } catch (SQLException e) {
            logger.error("Error al eliminar usuario por id", e);
            return false;
        }
    }


    /**
     * Elimina todos los usuarios en la base de datos.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @return {@code true} si se eliminaron todos los usuarios, {@code false} en caso de error.
     */

    @Override
    public Boolean deleteAllUsers() {
        logger.debug("Eliminando todos los usuarios...");
        String query = "DELETE FROM Cliente";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al eliminar todos los usuarios", e);
        }

        return true;
    }
}
