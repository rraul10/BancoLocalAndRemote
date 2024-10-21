package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.creditcard.repositories.CreditCardRepository;

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
public final class AppModule_ProvideRemoteCreditCardRepositoryFactory implements Factory<CreditCardRepository> {
  private final AppModule module;

  public AppModule_ProvideRemoteCreditCardRepositoryFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public CreditCardRepository get() {
    return provideRemoteCreditCardRepository(module);
  }

  public static AppModule_ProvideRemoteCreditCardRepositoryFactory create(AppModule module) {
    return new AppModule_ProvideRemoteCreditCardRepositoryFactory(module);
  }

  public static CreditCardRepository provideRemoteCreditCardRepository(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideRemoteCreditCardRepository());
  }
}
