package org.example.cache.errors;

public interface CacheErrors<K> {
    String getMessage();

    default String getErrorMessage() {
        return "Error en el cache: " + getMessage();
    }

    default void logError() {
        System.err.println(getErrorMessage());
    }

    default String getKeyNotFoundMessage(K key) {
        return "La clave '" + key + "' no fue encontrada en el caché.";
    }

    default void logKeyNotFound(K key) {
        System.err.println(getKeyNotFoundMessage(key));
    }

    default String getStorageFailureMessage() {
        return "Error en el almacenamiento del caché. No se pudo completar la operación.";
    }

    default void logStorageFailure() {
        System.err.println(getStorageFailureMessage());
    }

    default String getInvalidKeyMessage(K key) {
        return "La clave '" + key + "' es inválida para el caché.";
    }

    default void logInvalidKey(K key) {
        System.err.println(getInvalidKeyMessage(key));
    }

    default String getOperationTimeoutMessage(String operation) {
        return "La operación '" + operation + "' excedió el tiempo máximo permitido.";
    }

    default void logOperationTimeout(String operation) {
        System.err.println(getOperationTimeoutMessage(operation));
    }

    default String getGeneralErrorMessage() {
        return "Ocurrió un error general en el caché.";
    }

    default void logGeneralError() {
        System.err.println(getGeneralErrorMessage());
    }

    default String getCacheClearedMessage() {
        return "La caché fue limpiada exitosamente.";
    }

    default void logCacheCleared() {
        System.out.println(getCacheClearedMessage());
    }
}

