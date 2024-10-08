package org.example.notificaciones.repository;

import org.example.models.Cliente;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RepositoryClientNotificacion {
    private final List<Cliente> clientes = new ArrayList<>();

    private final Flux<List<Cliente>> clientesFlux;
    private final Flux<String> clienteNotificationFlux;
    private FluxSink<List<Cliente>> clienteFluxSink;
    private FluxSink<String> clienteNotification;

    public RepositoryClientNotificacion() {
        ConnectableFlux<List<Cliente>> connectableFunkoFlux = Flux.<List<Cliente>>create(emitter -> this.clienteFluxSink = emitter).publish();
        ConnectableFlux<String> connectableFunkoNotificationFlux = Flux.<String>create(emitter -> this.clienteNotification = emitter).publish();

        clientesFlux = connectableFunkoFlux;
        clienteNotificationFlux = connectableFunkoNotificationFlux;

        connectableFunkoFlux.connect();
        connectableFunkoNotificationFlux.connect();
    }

    public void add(Cliente cliente) {
        clientes.add(cliente);
        clienteFluxSink.next(clientes);
        clienteNotification.next("Se ha añadido un nuevo cliente: " + cliente);
    }

    public void delete(UUID id) {
        Optional<Cliente> funkoToRemove = clientes.stream().filter(f -> f.getId().equals(id)).findFirst();
        funkoToRemove.ifPresent(f -> {
            clientes.remove(f);
            clienteFluxSink.next(clientes);
            clienteNotification.next("Se ha eliminado un cliente: " + f);
        });
    }

    public Flux<List<Cliente>> getAllAsFlux() {
        return clientesFlux;
    }

    public Flux<String> getNotificationAsFlux() {
        return clienteNotificationFlux;
    }
}
