package org.example.creditcard.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseManager implements AutoCloseable {
    private static DataBaseManager instance = null;
    private HikariDataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(DataBaseManager.class);
    private String DB_URL = "jdbc:postgresql://localhost:5432/credit-card";
    private String DB_USER = "admin"; // reemplaza con tu usuario
    private String DB_PASSWORD = "adminPassword123"; // reemplaza con tu contraseña
    private String DB_Timeout = "10000";
    private Connection connection = null;

    protected DataBaseManager() {
    }

    // Cambiamos el constructor para que reciba la configuración
    private DataBaseManager(ConfigProperties config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getProperty("database.url", DB_URL));
        hikariConfig.setUsername(config.getProperty("database.user", DB_USER));
        hikariConfig.setPassword(config.getProperty("database.password", DB_PASSWORD));
        hikariConfig.setConnectionTimeout(Long.parseLong(config.getProperty("database.timeout", DB_Timeout)));
        this.dataSource = new HikariDataSource(hikariConfig);
        logger.info("Hikari configurado correctamente (con valores predeterminados)...");
    }

    public static DataBaseManager getInstance() {
        if (instance == null) {
            instance = new DataBaseManager();
        }
        return instance;
    }

    public static DataBaseManager getInstance(ConfigProperties config) {
        if (instance == null) {
            instance = new DataBaseManager(config);
        }
        return instance;
    }

    public Connection connect() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos", e);
            throw new RuntimeException("Error al conectar a la base de datos", e);
        }
    }

    public void disconnect() {
        if (connection != null) {
                dataSource.close();
                connection = null;
                logger.info("Desconectado de la base de datos...");
        }
    }

    @Override
    public void close() throws Exception {
        disconnect();
    }
}