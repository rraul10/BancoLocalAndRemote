package org.example.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Cache de almacenamiento genérico
 * @param <T>
 * @param <K>
 * @since 1.0
 * @author Yahya El Hadri, Raúl Fernández, Samuel Cortés, Javier Hernández, Javier Ruíz, Alvaro Herrero.
 */

public class CacheImpl<K, T> implements Cache<K, T> {
    private static final Logger logger = LoggerFactory.getLogger(CacheImpl.class);
    private final LinkedHashMap<K, T> cache;
    private final int maxCapacity;

    /**
     * Constructor de la clase.
     * @autor Raúl Fernández, Javier Hernández, Javier Ruíz, Samuel Cortés, Yahya El Hadri, Alvaro Herrero.
     * @since 1.0
     * @param maxCapacity
     * @param logger
     * @return Tamaño de la cáche.
     */

    public CacheImpl(int maxCapacity, Logger logger) {
        this.maxCapacity = maxCapacity;
        this.cache = new LinkedHashMap<K, T>(maxCapacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, T> eldest) {
                return size() > maxCapacity;
            }
        };
    }

    /**
     * Obtiene el valor asociado a la clave especificada.
     * @author Alvaro Herrero, Javier Ruíz, Javier Hernández, Raúl Fernández, Yahya El Hadri, Samuel Cortés.
     * @since 1.0
     * @param key Clave a buscar
     * @return Valor asociado a la clave
     */

    @Override
    public T get(K key) {
        logger.debug("Obteniendo el valor de la clave: {}", key);
        return cache.get(key);
    }

    /**
     * Añade un valor a la cáche con la clave especificada.
     * @author Alvaro Herrero, Javier Ruíz, Javier Hernández, Raúl Fernández, Yahya El Hadri, Samuel Cortés.
     * @since 1.0
     * @param key Clave a asociar
     * @param value Valor a almacenar
     */

    @Override
    public void put(K key, T value) {
        logger.debug("Añadiendo a cache el valor de la clave: {}", key);
        cache.put(key, value);
    }

    /**
     * Elimina el valor asociado a la clave especificada de la cache.
     * @author Alvaro Herrero, Javier Ruíz, Javier Hernández, Raúl Fernández, Yahya El Hadri, Samuel Cortés.
     * @since 1.0
     * @param key Clave a eliminar
     */

    @Override
    public void remove(K key) {
        logger.debug("Eliminando de cache el valor de la clave: {}", key);
        cache.remove(key);
    }

    /**
     * Limpia la cache.
     * @author Alvaro Herrero, Javier Ruíz, Javier Hernández, Raúl Fernández, Yahya El Hadri, Samuel Cortés.
     * @since 1.0
     */

    @Override
    public void clear() {
        logger.debug("Limpiando la cache");
        cache.clear();
    }

    /**
     * Obtiene el número de valores almacenados en la cáche.
     * @author Raúl Fernández, Samuel Cortés, Javier Hernández, Alvaro Herrero, Javier Ruíz, Yahya El Hadri.
     * @since 1.0
     * @return Número de valores almacenados
     */

    @Override
    public int size() {
        logger.debug("Obteniendo el tamaño de la cache");
        return cache.size();
    }

    /**
     * Obtiene un conjunto de claves almacenadas en la cáche.
     * @author Alvaro Herrero, Samuel Cortés, Javier Hernández, Raúl Fernández, Yahya El Hadri, Javier Ruíz.
     * @since 1.0
     * @return Conjunto de claves almacenadas.
     */

    @Override
    public Set<K> keys() {
        logger.debug("Obteniendo las claves de la cache");
        return cache.keySet();
    }

    /**
     * Obtiene una colección de valores almacenados en la cáche.
     * @author Raúl Fernández, Samuel Cortés, Javier Hernández, Alvaro Herrero, Javier Ruíz, Yahya El Hadri.
     * @since 1.0
     * @return Colección de valores almacenados.
     */

    @Override
    public Collection<T> values() {
        logger.debug("Obteniendo los valores de la cache");
        return cache.values();
    }

    /**
     * Comprueba si la cáche contiene un valor asociado a la clave especificada.
     * @author Alvaro Herrero, Samuel Cortés, Javier Hernández, Raúl Fernández, Yahya El Hadri, Javier Ruíz.
     * @since 1.0
     * @param key Clave a buscar
     * @return True si la cache contiene el valor, false en caso contrario
     */

    @Override
    public boolean containsKey(K key) {
        logger.debug("Comprobando si existe la clave en la cache: {}", key);
        return cache.containsKey(key);
    }

    /**
     * Comprueba si la cache contiene un valor específico.
     * @author Alvaro Herrero, Samuel Cortés, Javier Hernández, Raúl Fernández, Yahya El Hadri, Javier Ruíz.
     * @since 1.0
     * @param value Valor a buscar
     * @return True si la cache contiene el valor, false en caso contrario
     */

    @Override
    public boolean containsValue(T value) {
        logger.debug("Comprobando si existe el valor en la cache: {}", value);
        return cache.containsValue(value);
    }

    /**
     * Comprueba si la cache está vacía.
     * @author Alvaro Herrero, Samuel Cortés, Javier Hernández, Raúl Fernández, Yahya El Hadri, Javier Ruíz.
     * @since 1.0
     * @return True si la cache está vacía, false en caso contrario.
     */

    @Override
    public boolean isEmpty() {
        logger.debug("Comprobando si la cache está vacía");
        return cache.isEmpty();
    }

    /**
     * Comprueba si la cache no está vacía.
     * @author Alvaro Herrero, Samuel Cortés, Javier Hernández, Raúl Fernández, Yahya El Hadri, Javier Ruíz.
     * @since 1.0
     * @return True si la cache no está vacía, false en caso contrario.
     */

    @Override
    public boolean isNotEmpty() {
        logger.debug("Comprobando si la cache no está vacía");
        return !isEmpty();
    }
}
