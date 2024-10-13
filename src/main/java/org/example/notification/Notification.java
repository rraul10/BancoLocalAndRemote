package org.example.notification;

import java.time.LocalDateTime;

public record Notification<T>(Type type, T item, String message, LocalDateTime createdAt) {
    /**
     * Creates a new notification.
     * @param type the type of the notification.
     * @param item the item associated with the notification.
     * @param message the message associated with the notification.
     * @param createdAt the date and time when the notification was created.
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public Notification(Type type, T item, String message, LocalDateTime createdAt) {
        this.type = type;
        this.item = item;
        this.message = message;
        this.createdAt = createdAt;
    }

    /**
     * Creates a new notification with the current date and time.
     * @param type the type of the notification.
     * @param item the item associated with the notification.
     * @param message the message associated with the notification.
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public Notification(Type type, T item, String message) {
        this(type, item, message, LocalDateTime.now());
    }

    /**
     * Creates a new notification with the current date and time and a null message.
     * @param type the type of the notification.
     * @param item the item associated with the notification.
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public Notification(Type type, T item) {
        this(type, item, null, LocalDateTime.now());
    }

    /**
     * Creates a new notification with the current date and time and a null message and item.
     * @param type the type of the notification.
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public Notification(Type type) {
        this(type, null, null, LocalDateTime.now());
    }

    /**
     * The type of the notification.
     */
    public enum Type {
        CREATE, UPDATE, DELETE, REFRESH
    }
}
