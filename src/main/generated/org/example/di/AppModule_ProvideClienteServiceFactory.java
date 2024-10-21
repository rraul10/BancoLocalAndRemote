package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import org.example.client.repository.creditcard.CreditCardLocalRepository;
import org.example.client.repository.user.UsersRepository;
import org.example.client.storage.StorageJsonClient;
import org.example.creditcard.cache.CacheTarjetaImpl;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.creditcard.storage.StorageCsvCredCard;
import org.example.creditcard.validator.TarjetaValidator;
import org.example.service.ClienteService;
import org.example.users.cache.CacheUsuario;
import org.example.users.repository.UserRemoteRepositoryImpl;
import org.example.users.storage.StorageCsvUser;
import org.example.users.validator.UserValidator;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AppModule_ProvideClienteServiceFactory implements Factory<ClienteService> {
  private final AppModule module;

  private final Provider<UsersRepository> usersRepositoryProvider;

  private final Provider<CreditCardLocalRepository> creditCardLocalRepositoryProvider;

  private final Provider<CreditCardRepository> creditCardRepositoryProvider;

  private final Provider<CacheUsuario> cacheUsuarioProvider;

  private final Provider<CacheTarjetaImpl> cacheTarjetaProvider;

  private final Provider<UserValidator> userValidatorProvider;

  private final Provider<UserRemoteRepositoryImpl> userRemoteRepositoryProvider;

  private final Provider<TarjetaValidator> tarjetaValidatorProvider;

  private final Provider<StorageJsonClient> storageJsonClientProvider;

  private final Provider<StorageCsvUser> storageCsvUserProvider;

  private final Provider<StorageCsvCredCard> storageCsvCredCardProvider;

  public AppModule_ProvideClienteServiceFactory(AppModule module,
      Provider<UsersRepository> usersRepositoryProvider,
      Provider<CreditCardLocalRepository> creditCardLocalRepositoryProvider,
      Provider<CreditCardRepository> creditCardRepositoryProvider,
      Provider<CacheUsuario> cacheUsuarioProvider, Provider<CacheTarjetaImpl> cacheTarjetaProvider,
      Provider<UserValidator> userValidatorProvider,
      Provider<UserRemoteRepositoryImpl> userRemoteRepositoryProvider,
      Provider<TarjetaValidator> tarjetaValidatorProvider,
      Provider<StorageJsonClient> storageJsonClientProvider,
      Provider<StorageCsvUser> storageCsvUserProvider,
      Provider<StorageCsvCredCard> storageCsvCredCardProvider) {
    this.module = module;
    this.usersRepositoryProvider = usersRepositoryProvider;
    this.creditCardLocalRepositoryProvider = creditCardLocalRepositoryProvider;
    this.creditCardRepositoryProvider = creditCardRepositoryProvider;
    this.cacheUsuarioProvider = cacheUsuarioProvider;
    this.cacheTarjetaProvider = cacheTarjetaProvider;
    this.userValidatorProvider = userValidatorProvider;
    this.userRemoteRepositoryProvider = userRemoteRepositoryProvider;
    this.tarjetaValidatorProvider = tarjetaValidatorProvider;
    this.storageJsonClientProvider = storageJsonClientProvider;
    this.storageCsvUserProvider = storageCsvUserProvider;
    this.storageCsvCredCardProvider = storageCsvCredCardProvider;
  }

  @Override
  public ClienteService get() {
    return provideClienteService(module, usersRepositoryProvider.get(), creditCardLocalRepositoryProvider.get(), creditCardRepositoryProvider.get(), cacheUsuarioProvider.get(), cacheTarjetaProvider.get(), userValidatorProvider.get(), userRemoteRepositoryProvider.get(), tarjetaValidatorProvider.get(), storageJsonClientProvider.get(), storageCsvUserProvider.get(), storageCsvCredCardProvider.get());
  }

  public static AppModule_ProvideClienteServiceFactory create(AppModule module,
      Provider<UsersRepository> usersRepositoryProvider,
      Provider<CreditCardLocalRepository> creditCardLocalRepositoryProvider,
      Provider<CreditCardRepository> creditCardRepositoryProvider,
      Provider<CacheUsuario> cacheUsuarioProvider, Provider<CacheTarjetaImpl> cacheTarjetaProvider,
      Provider<UserValidator> userValidatorProvider,
      Provider<UserRemoteRepositoryImpl> userRemoteRepositoryProvider,
      Provider<TarjetaValidator> tarjetaValidatorProvider,
      Provider<StorageJsonClient> storageJsonClientProvider,
      Provider<StorageCsvUser> storageCsvUserProvider,
      Provider<StorageCsvCredCard> storageCsvCredCardProvider) {
    return new AppModule_ProvideClienteServiceFactory(module, usersRepositoryProvider, creditCardLocalRepositoryProvider, creditCardRepositoryProvider, cacheUsuarioProvider, cacheTarjetaProvider, userValidatorProvider, userRemoteRepositoryProvider, tarjetaValidatorProvider, storageJsonClientProvider, storageCsvUserProvider, storageCsvCredCardProvider);
  }

  public static ClienteService provideClienteService(AppModule instance,
      UsersRepository usersRepository, CreditCardLocalRepository creditCardLocalRepository,
      CreditCardRepository creditCardRepository, CacheUsuario cacheUsuario,
      CacheTarjetaImpl cacheTarjeta, UserValidator userValidator,
      UserRemoteRepositoryImpl userRemoteRepository, TarjetaValidator tarjetaValidator,
      StorageJsonClient storageJsonClient, StorageCsvUser storageCsvUser,
      StorageCsvCredCard storageCsvCredCard) {
    return Preconditions.checkNotNullFromProvides(instance.provideClienteService(usersRepository, creditCardLocalRepository, creditCardRepository, cacheUsuario, cacheTarjeta, userValidator, userRemoteRepository, tarjetaValidator, storageJsonClient, storageCsvUser, storageCsvCredCard));
  }
}
