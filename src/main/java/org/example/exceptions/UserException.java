package org.example.exceptions;

abstract class UserException extends RuntimeException {
    /**
     * Excepcion personalizada para errores en usuarios.
     * @author Javier Hernandez, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     * @param message mensaje de error
     */
    public UserException(String message) {
        super(message);
    }
}