package org.example.cache;

import io.vavr.control.Either;
import org.example.cache.errors.CacheErrors;
import java.util.LinkedHashMap;
import java.util.Map;



public class CacheImpl<K, T> implements Cache<K, T> {

    // LinkedHashMap con orden de acceso para implementar la caché LRU
    private final LinkedHashMap<K, T> cache;
    private final int maxCapacity;

    public CacheImpl(int maxCapacity) {
        this.maxCapacity = maxCapacity;

        this.cache = new LinkedHashMap<K, T>(maxCapacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<K, T> eldest) {
                return false;
            }
        };
    }

    @Override
    public Either<CacheErrors, T> get(K key) {
        if (key == null) {
            return Either.left(new CacheErrors<K>() {
                @Override
                public String getMessage() {
                    return getInvalidKeyMessage(key); // Error por clave inválida
                }
            });
        }

        T value = cache.get(key);
        if (value == null) {
            return Either.left(new CacheErrors<K>() {
                @Override
                public String getMessage() {
                    return getKeyNotFoundMessage(key); // Error de clave no encontrada
                }
            });
        }

        return Either.right(value);
    }

    @Override
    public Either<CacheErrors, T> put(K key, T value) {
        if (key == null) {
            return Either.left(new CacheErrors<K>() {
                @Override
                public String getMessage() {
                    return getInvalidKeyMessage(key); // Error por clave inválida
                }
            });
        }

        // Insertamos el nuevo valor, el LinkedHashMap gestionará la eliminación del más antiguo si es necesario
        cache.put(key, value);
        return Either.right(value);
    }

    @Override
    public Either<CacheErrors, T> remove(K key) {
        if (key == null) {
            return Either.left(new CacheErrors<K>() {
                @Override
                public String getMessage() {
                    return getInvalidKeyMessage(key); // Error por clave inválida
                }
            });
        }

        T value = cache.remove(key);
        if (value == null) {
            return Either.left(new CacheErrors<K>() {
                @Override
                public String getMessage() {
                    return getKeyNotFoundMessage(key); // Error de clave no encontrada
                }
            });
        }
        return Either.right(value);
    }

    @Override
    public Either<CacheErrors, Void> clear() {
        cache.clear();
        return Either.right(null); // No hay valor a devolver.
    }
}

