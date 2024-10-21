package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.client.repository.creditcard.CreditCardLocalRepository;

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
public final class AppModule_ProvideLocalCreditCardRepositoryFactory implements Factory<CreditCardLocalRepository> {
  private final AppModule module;

  public AppModule_ProvideLocalCreditCardRepositoryFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public CreditCardLocalRepository get() {
    return provideLocalCreditCardRepository(module);
  }

  public static AppModule_ProvideLocalCreditCardRepositoryFactory create(AppModule module) {
    return new AppModule_ProvideLocalCreditCardRepositoryFactory(module);
  }

  public static CreditCardLocalRepository provideLocalCreditCardRepository(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideLocalCreditCardRepository());
  }
}
