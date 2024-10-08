package org.example.cache;

import io.vavr.control.Either; // Ejemplo usando la librería Vavr para Either
import org.example.cache.errors.CacheErrors;

public interface Cache<K, T> {

    Either<CacheErrors, T> get(K key);

    Either<CacheErrors, T> put(K key, T value);

    Either<CacheErrors, T> remove(K key);

    Either<CacheErrors, Void> clear();
}
