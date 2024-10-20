package org.example.di;

import dagger.Component;
import org.example.client.service.ClienteService;
import org.example.client.service.ClienteServiceImpl;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    ClienteService clienteService();
}
