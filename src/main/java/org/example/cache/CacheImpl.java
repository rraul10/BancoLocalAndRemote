package org.example.cache;

import io.vavr.control.Either;  // Suponiendo que usas Either de Vavr
import org.example.cache.errors.CacheErrors;

import java.util.HashMap;
import java.util.Map;

public class CacheImpl<K, T> implements Cache<K, T> {

    private final Map<K, T> cache;
    private final int maxCapacity;

    public CacheImpl(int maxCapacity) {
        this.cache = new HashMap<>();
        this.maxCapacity = maxCapacity;
    }

    @Override
    public Either<CacheErrors, T> get(K key) {
        if (key == null) {
            return Either.left(new CacheErrors<K>() {
                @Override
                public String getMessage() {
                    return getInvalidKeyMessage(key); // Usamos un error específico
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

        if (cache.size() >= maxCapacity) {
            return Either.left(new CacheErrors<K>() {
                @Override
                public String getMessage() {
                    return getCacheFullMessage(); // Error de caché lleno
                }
            });
        }
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
