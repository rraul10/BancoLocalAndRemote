package org.example.notification;

public interface Notifications<T>{
    void send(Notification<T> notification);
}
