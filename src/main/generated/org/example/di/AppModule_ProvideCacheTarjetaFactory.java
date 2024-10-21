package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.creditcard.cache.CacheTarjetaImpl;

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
public final class AppModule_ProvideCacheTarjetaFactory implements Factory<CacheTarjetaImpl> {
  private final AppModule module;

  public AppModule_ProvideCacheTarjetaFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public CacheTarjetaImpl get() {
    return provideCacheTarjeta(module);
  }

  public static AppModule_ProvideCacheTarjetaFactory create(AppModule module) {
    return new AppModule_ProvideCacheTarjetaFactory(module);
  }

  public static CacheTarjetaImpl provideCacheTarjeta(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideCacheTarjeta());
  }
}
