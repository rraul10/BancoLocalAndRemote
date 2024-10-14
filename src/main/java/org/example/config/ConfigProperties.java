package org.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase que carga y gestiona propiedades de configuracion desde un archivo.
 * Esta clase permite cargar un archivo de propiedades y acceder a sus valores.
 * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
 * @since 1.0
 */

public class ConfigProperties {
    private final Properties properties = new Properties();

    /**
     * Constructor que carga el archivo de propiedades especificado.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param fileName nombre del archivo de propiedades a cargar
     */

    public ConfigProperties(String fileName) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                System.out.println("Sorry, unable to find " + fileName);
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Obtiene el valor de una propiedad especificada.
     * Si la propiedad no existe, devuelve el valor por defecto proporcionado.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param key clave de la propiedad a obtener
     * @param defaultValue valor por defecto a devolver si la propiedad no existe
     * @return valor de la propiedad o el valor por defecto
     */

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
