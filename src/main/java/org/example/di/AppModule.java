package org.example.di;


import dagger.Module;
import dagger.Provides;
import org.example.client.database.LocalDataBaseManager;
import org.example.client.repository.creditcard.CreditCardLocalRepository;
import org.example.client.repository.creditcard.CreditCardLocalRepositoryImpl;
import org.example.client.repository.user.UserLocalRepositoryImpl;
import org.example.client.repository.user.UsersRepository;
import org.example.client.storage.StorageJsonClient;
import org.example.client.storage.StorageJsonClientImpl;
import org.example.config.ConfigProperties;
import org.example.creditcard.cache.CacheTarjetaImpl;
import org.example.creditcard.database.DataBaseManager;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.creditcard.repositories.CreditCardRepositoryImpl;
import org.example.creditcard.storage.StorageCsvCredCard;
import org.example.creditcard.storage.StorageCsvCredCardImpl;
import org.example.creditcard.validator.TarjetaValidator;
import org.example.creditcard.validator.TarjetaValidatorImpl;
import org.example.notification.TarjetaNotificacion;
import org.example.notification.UserNotifications;
import org.example.service.ClienteService;
import org.example.service.ClienteServiceImpl;
import org.example.storages.validators.CsvValidator;
import org.example.storages.validators.JsonValidator;
import org.example.users.api.RetrofitUser;
import org.example.users.api.UserApiRest;
import org.example.users.cache.CacheUsuario;
import org.example.users.cache.CacheUsuarioImpl;
import org.example.users.repository.UserRemoteRepositoryImpl;
import org.example.users.storage.StorageCsvUser;
import org.example.users.storage.StorageCsvUserImpl;
import org.example.users.validator.UserValidator;
import org.example.users.validator.UserValidatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Module
public class AppModule {
    private final ConfigProperties configProperties;
    private final Logger logger = LoggerFactory.getLogger(AppModule.class);

    public AppModule() {
        this.configProperties = new ConfigProperties("application.properties");
    }

    @Provides
    @Singleton
    public LocalDataBaseManager provideLocalDataBaseManager() {
        return LocalDataBaseManager.getInstance(configProperties);
    }

    @Provides
    @Singleton
    public UsersRepository provideLocalUsersRepository() {
        LocalDataBaseManager dataBaseManager = LocalDataBaseManager.getInstance(configProperties);
        return new UserLocalRepositoryImpl(dataBaseManager);
    }

    @Provides
    @Singleton
    public CreditCardLocalRepository provideLocalCreditCardRepository() {
        LocalDataBaseManager dataBaseManager = LocalDataBaseManager.getInstance(configProperties);
        return new CreditCardLocalRepositoryImpl(dataBaseManager, provideLocalUsersRepository());
    }

    @Provides
    @Singleton
    public CreditCardRepository provideRemoteCreditCardRepository() {
        DataBaseManager dataBaseManager = DataBaseManager.getInstance(configProperties);
        return new CreditCardRepositoryImpl(dataBaseManager);
    }

    @Provides
    @Singleton
    public CacheUsuario provideCacheUsuario() {
        return new CacheUsuarioImpl(Integer.parseInt(configProperties.getProperty("cache.max.size", "10")), logger);
    }

    @Provides
    @Singleton
    public CacheTarjetaImpl provideCacheTarjeta() {
        return new CacheTarjetaImpl(Integer.parseInt(configProperties.getProperty("cache.max.size", "10")), logger);
    }

    @Provides
    @Singleton
    public UserValidator provideUserValidator() {
        return new UserValidatorImpl();
    }

    @Provides
    @Singleton
    public UserRemoteRepositoryImpl provideUserRemoteRepository() {
        return new UserRemoteRepositoryImpl(
                RetrofitUser.getUser(
                        "https://jsonplaceholder.typicode.com/"
                ).create(UserApiRest.class));
    }

    @Provides
    @Singleton
    public TarjetaValidator provideTarjetaValidator() {
        return new TarjetaValidatorImpl();
    }

    @Provides
    @Singleton
    public UserNotifications proviedUserNotifications() {
        return new UserNotifications();
    }
    @Provides
    @Singleton
    public TarjetaNotificacion proviedTarjetaNotifications() {
        return new TarjetaNotificacion();
    }

    @Provides
    @Singleton
    public CsvValidator csvValidator() {
        return new CsvValidator();
    }

    @Provides
    @Singleton
    public JsonValidator jsonValidator() {
        return new JsonValidator();
    }
    @Provides
    @Singleton
    public StorageCsvCredCard storageCsvCredCard() {
        return new StorageCsvCredCardImpl();
    }

    @Provides
    @Singleton
    public StorageCsvUser storageCsvUser() {
        return new StorageCsvUserImpl(csvValidator());
    }

    @Provides
    @Singleton
    public StorageJsonClient storageJsonClient() {
        return new StorageJsonClientImpl(jsonValidator());
    }

    @Provides
    @Singleton
    public ClienteService provideClienteService(UsersRepository usersRepository,
                                                CreditCardLocalRepository creditCardLocalRepository,
                                                CreditCardRepository creditCardRepository,
                                                CacheUsuario cacheUsuario,
                                                CacheTarjetaImpl cacheTarjeta,
                                                UserValidator userValidator,
                                                UserRemoteRepositoryImpl userRemoteRepository,
                                                TarjetaValidator tarjetaValidator,
                                                StorageJsonClient storageJsonClient,
                                                StorageCsvUser storageCsvUser,
                                                StorageCsvCredCard storageCsvCredCard) {
        return new ClienteServiceImpl(
                usersRepository,
                creditCardLocalRepository,
                creditCardRepository,
                cacheUsuario,
                cacheTarjeta,
                userValidator,
                userRemoteRepository,
                tarjetaValidator,
                proviedUserNotifications(),
                proviedTarjetaNotifications(),
                storageJsonClient,
                storageCsvCredCard,
                storageCsvUser
        );
    }
}