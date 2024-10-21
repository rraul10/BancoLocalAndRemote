package org.example;



import org.example.di.DaggerAppComponent;
import org.example.service.ClienteService;

public class Main {



    public static void main(String[] args) {

        ClienteService clienteService = DaggerAppComponent.create().clienteService();
    }
}