package org.example.creditcard.database;

import org.example.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que gestiona la conexion a una base de datos utilizando JDBC.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Statement;

/**
 * Clase que gestiona la conexión a la base de datos.
 * @author Raul Fernandez, Samuel Cortes, Javier Hernandez, Alvaro Herrero, Javier Ruiz, Yahya El Hadri.
 * @since 1.0
 */

public class DataBaseManager implements AutoCloseable {

    /**
     * Instancia única de la clase.
     */
    private static DataBaseManager instance = null;

    /**
     * Logger para registrar mensajes.
     */

    private final Logger logger = LoggerFactory.getLogger(DataBaseManager.class);

    /**
     * URL de la base de datos.
     */

    private String DB_URL;

    /**
     * Usuario de la base de datos.
     */

    private String DB_USER;

    /**
     * Contraseña de la base de datos.
     */

    private String DB_PASSWORD;

    /**
     * Tiempo de espera para la conexión a la base de datos.
     */

    private long DB_TIMEOUT;

    /**
     * Indica si la base de datos ha sido inicializada.
     */

    private Boolean init = false;

    /**
     * Constructor protegido para evitar instanciación directa.
     */

    protected DataBaseManager() {
        initializeDatabaseProperties();
    }

    /**
     * Constructor que inicializa la base de datos con las propiedades proporcionadas.
     * @author Raul Fernandez, Samuel Cortes, Javier Hernandez, Alvaro Herrero, Javier Ruiz, Yahya El Hadri.
     * @return Instancia de la clase DataBaseManager
     * @param config Propiedades de configuración.
     */

    private DataBaseManager(ConfigProperties config) {
        this.DB_URL = config.getProperty("database.url", "jdbc:postgresql://" + System.getenv("POSTGRES_HOST") + ":" + System.getenv("POSTGRES_PORT") + "/" + System.getenv("POSTGRES_DATABASE"));
        this.DB_USER = config.getProperty("database.user", "admin");
        this.DB_PASSWORD = config.getProperty("database.password", "adminPassword123");
        this.DB_TIMEOUT = Long.parseLong(config.getProperty("database.timeout", "10000"));
    }

    /**
     * Inicializa las propiedades de la base de datos con valores por defecto.
     * @author Raul Fernandez, Samuel Cortes, Javier Hernandez, Alvaro Herrero, Javier Ruiz, Yahya El Hadri.
     */

    private void initializeDatabaseProperties() {
        this.DB_URL = "jdbc:postgresql://" + System.getenv("POSTGRES_HOST") + ":" + System.getenv("POSTGRES_PORT") + "/" + System.getenv("POSTGRES_DATABASE");
        this.DB_USER = "admin";
        this.DB_PASSWORD = "adminPassword123";
        this.DB_TIMEOUT = 10000;
    }

    /**
     * Obtiene la instancia única de la clase.
     * @author Raul Fernandez, Samuel Cortes, Javier Hernandez, Alvaro Herrero, Javier Ruiz, Yahya El Hadri.
     * @return La instancia única de la clase.
     */

    public static DataBaseManager getInstance() {
        if (instance == null) {
            instance = new DataBaseManager();
        }
        return instance;
    }

    /**
     * Obtiene la instancia única de la clase con las propiedades proporcionadas.
     * @author Raul Fernandez, Samuel Cortes, Javier Hernandez, Alvaro Herrero, Javier Ruiz, Yahya El Hadri.
     * @param config Propiedades de configuración.
     * @return La instancia única de la clase.
     */

    public static DataBaseManager getInstance(ConfigProperties config) {
        if (instance == null) {
            instance = new DataBaseManager(config);
        }
        return instance;
    }

    /**
     * Establece una conexión a la base de datos.
     * @author Raul Fernandez, Samuel Cortes, Javier Hernandez, Alvaro Herrero, Javier Ruiz, Yahya El Hadri.
     * @return La conexión a la base de datos.
     * @throws SQLException Si ocurre un error al conectar.
     */

    public Connection connect() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("Conexi贸n exitosa a la base de datos.");
            if (!init){
                initializeDatabase(connection);
                init = true;
            }

            return connection;
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos", e);
            throw new RuntimeException("Error al conectar a la base de datos", e);
        }
    }

    /**
     * Cierra una conexión a la base de datos.
     * @author Raul Fernandez, Samuel Cortes, Javier Hernandez, Alvaro Herrero, Javier Ruiz, Yahya El Hadri.
     * @param connection La conexión a cerrar.
     */

    public void disconnect(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Conexi贸n cerrada correctamente.");
            } catch (SQLException e) {
                logger.error("Error al cerrar la conexi贸n", e);
            }
        }
    }

    /**
     * Inicializa la base de datos ejecutando un script SQL.
     * @author Raul Fernandez, Samuel Cortes, Javier Hernandez, Alvaro Herrero, Javier Ruiz, Yahya El Hadri.
     * @param connection La conexión a la base de datos.
     */

    private void initializeDatabase(Connection connection) {
        String scriptPath = "database/init.sql";
        try (BufferedReader reader = new BufferedReader(new FileReader(scriptPath, StandardCharsets.UTF_8));
             Statement statement = connection.createStatement()) {

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
                    statement.execute(sqlStatement);  // Ejecutar cada instrucci贸n SQL
                }
            }

            logger.info("Base de datos inicializada correctamente.");

        } catch (IOException e) {
            logger.error("Error al leer el archivo init.sql", e);
        } catch (SQLException e) {
            logger.error("Error al ejecutar las instrucciones SQL", e);
        }
    }

    /**
     * Cierra la base de datos y el logger.
     * @author Raul Fernandez, Samuel Cortes, Javier Hernandez, Alvaro Herrero, Javier Ruiz, Yahya El Hadri.
     */

    @Override
    public void close() throws Exception {
    }
}