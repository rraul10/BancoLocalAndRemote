package org.example;

import org.example.di.DaggerAppComponent;
import org.example.models.Cliente;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.example.service.ClienteService;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Main {

    public static void main(String[] args) {

        ClienteService clienteService = DaggerAppComponent.create().clienteService();

        CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    clienteService.loadData();
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    System.out.println("La tarea fue interrumpida.");
                    break;
                }
            }
        });

        CompletableFuture.runAsync(()->{
                    clienteService.loadTarjetasCsv(new File("data/tarjetas.csv")).forEach(i->
                            i.forEach(j->{
                                clienteService.createTarjeta(j);
                            })
                    );
                    clienteService.getAllClientes(false).forEach(i->
                            i.forEach(j->{
                                System.out.println(j);
                            })
                    );
                }

        );

    }
}
