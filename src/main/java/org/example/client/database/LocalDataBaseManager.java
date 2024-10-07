package org.example.client.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class LocalDataBaseManager implements AutoCloseable {
    private static LocalDataBaseManager instance = null;
    private HikariDataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(LocalDataBaseManager.class);
    private String DB_URL = "jdbc:sqlite:clients.db";
    private String DB_USER = "admin"; // reemplaza con tu usuario
    private String DB_PASSWORD = "adminPassword123"; // reemplaza con tu contraseña
    private String DB_Timeout = "10000";
    private Connection connection = null;

    protected LocalDataBaseManager() {
    }

    // Cambiamos el constructor para que reciba la configuración
    private LocalDataBaseManager(ConfigProperties config) {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(config.getProperty("local.database.url", DB_URL));
        hikariConfig.setUsername(config.getProperty("database.username", DB_USER));
        hikariConfig.setPassword(config.getProperty("database.password", DB_PASSWORD));
        hikariConfig.setConnectionTimeout(Long.parseLong(config.getProperty("local.database.timeout",DB_Timeout)));

        this.dataSource = new HikariDataSource(hikariConfig);
        logger.info("Hikari configurado correctamente");
    }

    public static LocalDataBaseManager getInstance() {
        if (instance == null) {
            instance = new LocalDataBaseManager();
        }
        return instance;
    }

    public static LocalDataBaseManager getInstance(ConfigProperties config) {
        if (instance == null) {
            instance = new LocalDataBaseManager(config);
        }
        return instance;
    }

    public Connection connect() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos local", e);
            throw new RuntimeException("Error al conectar a la base de datos local", e);
        }
    }

    public void disconnect() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("Desconectado del pool de conexiones...");
        }
    }


    @Override
    public void close() throws Exception {
        disconnect();
    }
}