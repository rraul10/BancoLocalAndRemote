package org.example.client.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class LocalDataBaseManager implements AutoCloseable {
    private static LocalDataBaseManager instance = null;
    private HikariDataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(LocalDataBaseManager.class);
    private String DB_URL = "jdbc:sqlite:clients.db";
    private String DB_USER = "admin"; // reemplaza con tu usuario
    private String DB_PASSWORD = "adminPassword123"; // reemplaza con tu contraseña
    private String DB_Timeout = "10000";
    private Connection connection = null;

    // Constructor sin parámetros (usa valores predeterminados)
    protected LocalDataBaseManager() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(DB_URL);
        hikariConfig.setUsername(DB_USER);
        hikariConfig.setPassword(DB_PASSWORD);
        hikariConfig.setConnectionTimeout(Long.parseLong(DB_Timeout));

        this.dataSource = new HikariDataSource(hikariConfig);
        logger.info("Hikari configurado correctamente (con valores predeterminados)");
    }

    // Constructor que recibe la configuración
    private LocalDataBaseManager(ConfigProperties config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getProperty("local.database.url", DB_URL));
        hikariConfig.setUsername(config.getProperty("database.username", DB_USER));
        hikariConfig.setPassword(config.getProperty("database.password", DB_PASSWORD));
        hikariConfig.setConnectionTimeout(Long.parseLong(config.getProperty("local.database.timeout", DB_Timeout)));

        this.dataSource = new HikariDataSource(hikariConfig);
        logger.info("Hikari configurado correctamente (con configuración)");
    }

    // Método para obtener la instancia sin configuración (singleton)
    public static LocalDataBaseManager getInstance() {
        if (instance == null) {
            instance = new LocalDataBaseManager();
        }
        return instance;
    }

    // Método para obtener la instancia con configuración (singleton)
    public static LocalDataBaseManager getInstance(ConfigProperties config) {
        if (instance == null) {
            instance = new LocalDataBaseManager(config);
        }
        return instance;
    }

    // Método para conectarse a la base de datos
    public Connection connect() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos local", e);
            throw new RuntimeException("Error al conectar a la base de datos local", e);
        }
    }

    public void initializeDatabase() {
        try (Connection conn = connect();
             BufferedReader reader = new BufferedReader(new InputStreamReader(
                     getClass().getClassLoader().getResourceAsStream("initCliente.sql"), StandardCharsets.UTF_8));
             Statement statement = conn.createStatement()) {

            StringBuilder sqlBuilder = new StringBuilder();
            String line;

            // Leer todo el contenido del archivo y almacenarlo en sqlBuilder
            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line.trim()).append("\n");
            }

            // Dividir las instrucciones SQL usando el punto y coma como delimitador
            String[] sqlStatements = sqlBuilder.toString().split(";");
            for (String sqlStatement : sqlStatements) {
                logger.debug(sqlStatement);
                sqlStatement = sqlStatement.trim();
                if (!sqlStatement.isEmpty()) {
                    statement.execute(sqlStatement); // Ejecutar cada instrucción
                }
            }

            logger.info("Base de datos inicializada correctamente.");

        } catch (IOException e) {
            logger.error("Error al leer el archivo initCliente.sql", e);
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos o ejecutar las instrucciones SQL", e);
        }
    }

    // Método para desconectar
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
