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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestor de base de datos local que utiliza la biblioteca HikariCP para gestionar conexiones a una base de datos SQLite.
 * @author Raul Fernandez, Javier Ruiz, Alvaro Herrero, Javier Hernandez, Yahya El Hadri, Samuel Cortes.
 * @version 1.0
 */

public class LocalDataBaseManager implements AutoCloseable {
    private static LocalDataBaseManager instance = null;
    private HikariDataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(LocalDataBaseManager.class);
    private String DB_URL = "jdbc:sqlite:clients.db";
    private String DB_USER = "admin"; // reemplaza con tu usuario
    private String DB_PASSWORD = "adminPassword123"; // reemplaza con tu contraseña
    private String DB_Timeout = "10000";
    private Connection connection = null;

    /**
     * Constructor sin parametros que utiliza valores predeterminados para configurar la conexion a la base de datos.
     * @author Raul Fernandez, Javier Ruiz, Alvaro Herrero, Javier Hernandez, Yahya El Hadri, Samuel Cortes.
     */

    protected LocalDataBaseManager() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(DB_URL);
        hikariConfig.setUsername(DB_USER);
        hikariConfig.setPassword(DB_PASSWORD);
        hikariConfig.setConnectionTimeout(Long.parseLong(DB_Timeout));

        this.dataSource = new HikariDataSource(hikariConfig);
        logger.info("Hikari configurado correctamente (con valores predeterminados)");
    }

    /**
     * Constructor que recibe un objeto ConfigProperties y un booleano memory que indica si la base de datos debe ser creada en memoria o no.
     * @author Raul Fernandez, Javier Ruíz, Alvaro Herrero, Javier Hernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param config objeto ConfigProperties que contiene la configuracion de la base de datos
     * @param memory booleano que indica si la base de datos debe ser creada en memoria o no
     */

    private LocalDataBaseManager(ConfigProperties config, Boolean memory) {
        HikariConfig hikariConfig = new HikariConfig();
        if (memory) {
            logger.info("-----------------------Base en memoria-------------------------");
            hikariConfig.setJdbcUrl("jdbc:sqlite:memory:myDb?cache=shared");
        } else {
            hikariConfig.setJdbcUrl(config.getProperty("local.database.url", DB_URL));

        }
        hikariConfig.setUsername(config.getProperty("database.username", DB_USER));
        hikariConfig.setPassword(config.getProperty("database.password", DB_PASSWORD));
        hikariConfig.setConnectionTimeout(Long.parseLong(config.getProperty("local.database.timeout", DB_Timeout)));

        this.dataSource = new HikariDataSource(hikariConfig);
        logger.info("Hikari configurado correctamente (con configuración)");
    }

    /**
     * Metodo que devuelve la instancia estatica de la clase LocalDataBaseManager.
     * @author Raul Fernandez, Javier Ruiz, Alvaro Herrero, Javier Hernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @return instancia estatica de la clase LocalDataBaseManager
     */

    public static LocalDataBaseManager getInstance() {
        if (instance == null) {
            instance = new LocalDataBaseManager();
        }
        return instance;
    }

    /**
     * Metodo que devuelve la instancia estatica de la clase LocalDataBaseManager configurada con el objeto ConfigProperties proporcionado.
     * @author Raul Fernandez, Javier Ruiz, Alvaro Herrero, Javier Hernandez, Yahya El Hadri, Samuel Cortas.
     * @since 1.0
     * @param config objeto ConfigProperties que contiene la configuracian de la base de datos
     * @return instancia estatica de la clase LocalDataBaseManager
     */

    public static LocalDataBaseManager getInstance(ConfigProperties config) {
        if (instance == null) {
            instance = new LocalDataBaseManager(config, false);
        }
        return instance;
    }

    /**
     * Metodo que devuelve la instancia estatica de la clase LocalDataBaseManager configurada para crear la base de datos en memoria.
     * @author Raul Fernandez, Javier Ruiz, Alvaro Herrero, Javier Hernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param config objeto ConfigProperties que contiene la configuracian de la base de datos
     * @return instancia estatica de la clase LocalDataBaseManager
     */

    public static LocalDataBaseManager getInstanceMemory(ConfigProperties config) {
        if (instance == null) {
            instance = new LocalDataBaseManager(config, true);
        }
        return instance;
    }

    /**
    * Metodo que establece una conexion a la base de datos y devuelve un objeto Connection.
    * @author Raul Fernandez, Javier Ruiz, Alvaro Herrero, Javier Hernandez, Yahya El Hadri, Samuel Cortas.
    * @since 1.0
    * @return objeto Connection que representa la conexion a la base de datos
    * @throws SQLException si hay errores al establecer
    */

    public Connection connect() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos local", e);
            throw new RuntimeException("Error al conectar a la base de datos local", e);
        }
    }

    /**
     * Metodo que inicializa la base de datos ejecutando un script SQL contenido en un archivo llamado "initCliente.sql".
     * @author Raul Fernandez, Javier Ruiz, Alvaro Herrero, Javier Hernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @throws IOException si hay errores al leer el archivo "initCliente.sql"
     * @throws SQLException sí hay errores al conectar a la base de datos o ejecutar las instrucciones SQL
     */

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
                    statement.execute(sqlStatement); // Ejecutar cada instruccion
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
     * Metodo que desconecta del pool de conexiones.
     * @author Raul Fernandez, Javier Ruíz, Alvaro Herrero, Javier Hernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     */

    public void disconnect() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("Desconectado del pool de conexiones...");
        }
    }

    /**
     * Metodo que cierra la conexion a la base de datos y desconecta del pool de conexiones.
     * @author Raul Fernández, Javier Ruiz, Alvaro Herrero, Javier Hernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @throws Exception si hay errores al desconectar del pool de conexiones
     */

    @Override
    public void close() throws Exception {
        disconnect();
    }
}
