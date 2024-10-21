package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.creditcard.validator.TarjetaValidator;

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
public final class AppModule_ProvideTarjetaValidatorFactory implements Factory<TarjetaValidator> {
  private final AppModule module;

  public AppModule_ProvideTarjetaValidatorFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public TarjetaValidator get() {
    return provideTarjetaValidator(module);
  }

  public static AppModule_ProvideTarjetaValidatorFactory create(AppModule module) {
    return new AppModule_ProvideTarjetaValidatorFactory(module);
  }

  public static TarjetaValidator provideTarjetaValidator(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideTarjetaValidator());
  }
}
