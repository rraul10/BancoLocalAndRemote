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
import java.util.UUID;

public class UserRepositoryImpl implements UsersRepository{
    private final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private final LocalDataBaseManager localDataBaseManager;

    /**
     * Constructor que recibe el gestor de la base de datos.
     * @param localDataBase gestor de la base de datos.
     */
    public UserRepositoryImpl(LocalDataBaseManager localDataBase) {this.localDataBaseManager = localDataBase;}
    /**
     * Obtiene una lista de todos los usuarios en la base de datos.
     *
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

    /**
     * Obtiene una lista de usuarios cuyo nombre coincide con el especificado.
     *
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

    /**
     * Busca un usuario por su id en la base de datos.
     *
     * @param id el id del usuario a buscar
     * @return el usuario encontrado
     */
    @Override
    public Usuario findUserById(UUID id) {
        logger.debug("Obteniendo usuario por id...");
        Usuario usuario = null;
        String query = "SELECT * FROM Cliente WHERE id = ?";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
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

    /**
     * Guarda un usuario en la base de datos.
     *
     * @param user el usuario a guardar
     * @return el usuario guardado
     */
    @Override
    public Usuario saveUser(Usuario user) {
        logger.debug("Guardando usuario...");
        String query = "INSERT INTO Cliente (id, name, username, email) VALUES (?, ?, ?, ?)";
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

    /**
     * Actualiza un usuario en la base de datos.
     *
     * @param uuid el id del usuario a actualizar
     * @param user el usuario con los datos a actualizar
     * @return el usuario actualizado
     */
    @Override
    public Usuario updateUser(UUID uuid, Usuario user) {
        logger.debug("Actualizando usuario...");
        String query = "UPDATE Cliente SET name = ?, username = ?, email = ? WHERE id = ?";
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

    /**
     * Elimina un usuario en la base de datos por su id.
     *
     * @param id el id del usuario a eliminar
     * @return {@code true} si se elimin  el usuario, {@code false} en caso de error.
     */
    @Override
    public Boolean deleteUserById(UUID id) {
        logger.debug("Eliminando usuario por id...");
        String query = "DELETE FROM Cliente WHERE id = ?";
        try (Connection connection = localDataBaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al eliminar usuario por id", e);
        }
        return true;
    }

    /**
     * Elimina todos los usuarios en la base de datos.
     *
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
