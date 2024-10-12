package org.example.exceptions;

public class UserNotFoundException extends UserException {
    /**
     * Excepcion que se lanza cuando no se encuentra un usuario.
     *
     * @author Javier Hernandez, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     * @param message Mensaje de la excepcion
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}