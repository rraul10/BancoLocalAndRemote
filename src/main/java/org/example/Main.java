package org.example;


import org.example.client.service.ClienteService;
import org.example.client.service.ClienteServiceImpl;
import org.example.di.AppComponent;
import org.example.di.DaggerAppComponent;

public class Main {



    public static void main(String[] args) {

        ClienteService clienteService = DaggerAppComponent.create().clienteService();
    }
}