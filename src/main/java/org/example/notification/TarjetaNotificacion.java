package org.example.notification;

import lombok.Getter;
import org.example.creditcard.dto.TarjetaCreditoDto;
import org.example.users.dto.UsuarioDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class TarjetaNotificacion implements Notifications<TarjetaCreditoDto> {
    private final Logger logger = LoggerFactory.getLogger(UserNotifications.class);

    private final Sinks.Many<Notification<TarjetaCreditoDto>> notificationsSink = Sinks.many().replay().limit(1);
    @Getter
    private final Flux<Notification<TarjetaCreditoDto>> notifications = notificationsSink.asFlux().onBackpressureDrop();
    /**
     * Envia una notificacion de un tarjeta de credito.
     * @param notification la notificacion a enviar
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
    @Override
    public void send(Notification<TarjetaCreditoDto> notification) {
        logger.debug("Send notification: {}", notification);
        notificationsSink.tryEmitNext(notification);
    }
}
