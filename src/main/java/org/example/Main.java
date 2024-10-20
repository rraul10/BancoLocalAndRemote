package org.example;

import org.example.di.DaggerAppComponent;
import org.example.models.TarjetaCredito;
import org.example.service.ClienteService;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Main {

    public static void main(String[] args) {

        ClienteService clienteService = DaggerAppComponent.create().clienteService();

        CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    clienteService.loadData();  // Ejecutar la función
                    Thread.sleep(30000);  // Esperar 30 segundos
                } catch (InterruptedException e) {
                    System.out.println("La tarea fue interrumpida.");
                    break;
                }
            }
        });

        CompletableFuture.runAsync(()->{
                    clienteService.getAllClientes(false);
                    clienteService.getAllUsers(true).forEach(i->
                            i.forEach(System.out::println)
                    );

                }

        );

    }
}
