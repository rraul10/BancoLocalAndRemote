package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import javax.annotation.processing.Generated;
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
public final class DaggerAppComponent {
  private DaggerAppComponent() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static AppComponent create() {
    return new Builder().build();
  }

  public static final class Builder {
    private AppModule appModule;

    private Builder() {
    }

    public Builder appModule(AppModule appModule) {
      this.appModule = Preconditions.checkNotNull(appModule);
      return this;
    }

    public AppComponent build() {
      if (appModule == null) {
        this.appModule = new AppModule();
      }
      return new AppComponentImpl(appModule);
    }
  }

  private static final class AppComponentImpl implements AppComponent {
    private final AppComponentImpl appComponentImpl = this;

    private Provider<UsersRepository> provideLocalUsersRepositoryProvider;

    private Provider<CreditCardLocalRepository> provideLocalCreditCardRepositoryProvider;

    private Provider<CreditCardRepository> provideRemoteCreditCardRepositoryProvider;

    private Provider<CacheUsuario> provideCacheUsuarioProvider;

    private Provider<CacheTarjetaImpl> provideCacheTarjetaProvider;

    private Provider<UserValidator> provideUserValidatorProvider;

    private Provider<UserRemoteRepositoryImpl> provideUserRemoteRepositoryProvider;

    private Provider<TarjetaValidator> provideTarjetaValidatorProvider;

    private Provider<StorageJsonClient> storageJsonClientProvider;

    private Provider<StorageCsvUser> storageCsvUserProvider;

    private Provider<StorageCsvCredCard> storageCsvCredCardProvider;

    private Provider<ClienteService> provideClienteServiceProvider;

    private AppComponentImpl(AppModule appModuleParam) {

      initialize(appModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final AppModule appModuleParam) {
      this.provideLocalUsersRepositoryProvider = DoubleCheck.provider(AppModule_ProvideLocalUsersRepositoryFactory.create(appModuleParam));
      this.provideLocalCreditCardRepositoryProvider = DoubleCheck.provider(AppModule_ProvideLocalCreditCardRepositoryFactory.create(appModuleParam));
      this.provideRemoteCreditCardRepositoryProvider = DoubleCheck.provider(AppModule_ProvideRemoteCreditCardRepositoryFactory.create(appModuleParam));
      this.provideCacheUsuarioProvider = DoubleCheck.provider(AppModule_ProvideCacheUsuarioFactory.create(appModuleParam));
      this.provideCacheTarjetaProvider = DoubleCheck.provider(AppModule_ProvideCacheTarjetaFactory.create(appModuleParam));
      this.provideUserValidatorProvider = DoubleCheck.provider(AppModule_ProvideUserValidatorFactory.create(appModuleParam));
      this.provideUserRemoteRepositoryProvider = DoubleCheck.provider(AppModule_ProvideUserRemoteRepositoryFactory.create(appModuleParam));
      this.provideTarjetaValidatorProvider = DoubleCheck.provider(AppModule_ProvideTarjetaValidatorFactory.create(appModuleParam));
      this.storageJsonClientProvider = DoubleCheck.provider(AppModule_StorageJsonClientFactory.create(appModuleParam));
      this.storageCsvUserProvider = DoubleCheck.provider(AppModule_StorageCsvUserFactory.create(appModuleParam));
      this.storageCsvCredCardProvider = DoubleCheck.provider(AppModule_StorageCsvCredCardFactory.create(appModuleParam));
      this.provideClienteServiceProvider = DoubleCheck.provider(AppModule_ProvideClienteServiceFactory.create(appModuleParam, provideLocalUsersRepositoryProvider, provideLocalCreditCardRepositoryProvider, provideRemoteCreditCardRepositoryProvider, provideCacheUsuarioProvider, provideCacheTarjetaProvider, provideUserValidatorProvider, provideUserRemoteRepositoryProvider, provideTarjetaValidatorProvider, storageJsonClientProvider, storageCsvUserProvider, storageCsvCredCardProvider));
    }

    @Override
    public ClienteService clienteService() {
      return provideClienteServiceProvider.get();
    }
  }
}
