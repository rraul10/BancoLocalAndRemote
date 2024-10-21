package org.example.di;

import dagger.Component;
import org.example.service.ClienteService;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    ClienteService clienteService();
}
