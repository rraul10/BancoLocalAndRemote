package org.example.notification;

import java.time.LocalDateTime;

public record Notification<T>(Type type, T item, String message, LocalDateTime createdAt) {
    /**
     * Crea una nueva notificacion.
     * @param type el tipo de la notificacion.
     * @param item el elemento asociado con la notificacion.
     * @param message el mensaje asociado con la notificacion.
     * @param createdAt la fecha y hora en que se creo la notificacion.
     * @author Javier Hernandez, Yahya El Hadri, Javier Ruiz, Alvaro Herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */

    public Notification(Type type, T item, String message, LocalDateTime createdAt) {
        this.type = type;
        this.item = item;
        this.message = message;
        this.createdAt = createdAt;
    }

    /**
     * Crea una nueva notificacion con la fecha y hora actual.
     * @param type el tipo de la notificacion.
     * @param item el elemento asociado con la notificacion.
     * @param message el mensaje asociado con la notificacion.
     * @author Javier Hernandez, Yahya El Hadri, Javier Ruiz, Alvaro Herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */

    public Notification(Type type, T item, String message) {
        this(type, item, message, LocalDateTime.now());
    }

    /**
     * Crea una nueva notificacion con la fecha y hora actual y un mensaje nulo.
     * @param type el tipo de la notificacion.
     * @param item el elemento asociado con la notificacion.
     * @author Javier Hernandez, Yahya El Hadri, Javier Ruiz, Alvaro Herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */

    public Notification(Type type, T item) {
        this(type, item, null, LocalDateTime.now());
    }

    /**
     * Crea una nueva notificacion con la fecha y hora actual, y con un mensaje y elemento nulos.
     * @param type el tipo de la notificacion.
     * @author Javier Hernandez, Yahya El Hadri, Javier Ruiz, Alvaro Herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */

    public Notification(Type type) {
        this(type, null, null, LocalDateTime.now());
    }

    /**
     * El tipo de notificacion
     */
    public enum Type {
        CREATE, UPDATE, DELETE, REFRESH
    }
}
