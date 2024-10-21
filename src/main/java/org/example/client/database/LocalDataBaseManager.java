package org.example.client.database;

import org.example.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestor de base de datos local que utiliza JDBC para gestionar conexiones a una base de datos SQLite.
 * @author Raul Fernandez, Javier Ruiz, Alvaro Herrero, Javier Hernandez, Yahya El Hadri, Samuel Cortes.
 * @version 1.0
 */
public class LocalDataBaseManager implements AutoCloseable {
    private static LocalDataBaseManager instance = null;
    private final Logger logger = LoggerFactory.getLogger(LocalDataBaseManager.class);
    private String DB_URL = "jdbc:sqlite:clients.db";
    private String DB_USER = "admin";
    private String DB_PASSWORD = "adminPassword123";
    private String DB_Timeout = "10000";


    /**
     * Constructor sin parámetros que utiliza valores predeterminados para configurar la conexión a la base de datos.
     */
    protected LocalDataBaseManager() {
        logger.info("Configuración de base de datos local establecida.");
        initializeDatabase();
    }

    /**
     * Constructor que recibe un objeto ConfigProperties.
     * @param config objeto ConfigProperties que contiene la configuración de la base de datos
     */
    private LocalDataBaseManager(ConfigProperties config) {
        this.DB_URL = config.getProperty("local.database.url", DB_URL);
        this.DB_USER = config.getProperty("database.username", DB_USER);
        this.DB_PASSWORD = config.getProperty("database.password", DB_PASSWORD);
        this.DB_Timeout = config.getProperty("local.database.timeout", DB_Timeout);
        initializeDatabase();


    }

    public static LocalDataBaseManager getInstance(ConfigProperties config) {
        if (instance == null) {
            instance = new LocalDataBaseManager();
        }
        return instance;
    }

    public static LocalDataBaseManager getInstanceMemory(ConfigProperties config) {
        if (instance == null) {
            instance = new LocalDataBaseManager(config);
        }
        return instance;
    }

    /**
     * Método que establece una conexión a la base de datos y devuelve un objeto Connection.
     * @return objeto Connection que representa la conexión a la base de datos
     * @throws SQLException si hay errores al establecer la conexión
     */
    public Connection connect() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(DB_URL);
            logger.info("Conexión exitosa a la base de datos local.");
            return connection;
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos local", e);
            throw new RuntimeException("Error al conectar a la base de datos local", e);
        }
    }

    /**
     * Método que inicializa la base de datos ejecutando un script SQL contenido en un archivo llamado "initCliente.sql".
     * @throws IOException si hay errores al leer el archivo "initCliente.sql"
     * @throws SQLException si hay errores al conectar a la base de datos o ejecutar las instrucciones SQL
     */
    public void initializeDatabase() {
        try (Connection conn = connect();
             BufferedReader reader = new BufferedReader(new InputStreamReader(
                     getClass().getClassLoader().getResourceAsStream("initCliente.sql"), StandardCharsets.UTF_8));
             Statement statement = conn.createStatement()) {

            StringBuilder sqlBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line.trim()).append("\n");
            }

            String[] sqlStatements = sqlBuilder.toString().split(";");
            for (String sqlStatement : sqlStatements) {
                sqlStatement = sqlStatement.trim();
                if (!sqlStatement.isEmpty()) {
                    logger.info("Ejecutando: " + sqlStatement);
                    statement.execute(sqlStatement);
                }
            }

            logger.info("Base de datos inicializada correctamente.");

        } catch (IOException e) {
            logger.error("Error al leer el archivo initCliente.sql", e);
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos o ejecutar las instrucciones SQL", e);
        }
    }

    /**
     * Método que cierra la conexión a la base de datos.
     * @throws Exception si hay errores al cerrar la conexión
     */
    @Override
    public void close() throws Exception {
        logger.info("Gestor de base de datos cerrado.");
    }
}