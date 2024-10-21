package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.users.cache.CacheUsuario;

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
public final class AppModule_ProvideCacheUsuarioFactory implements Factory<CacheUsuario> {
  private final AppModule module;

  public AppModule_ProvideCacheUsuarioFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public CacheUsuario get() {
    return provideCacheUsuario(module);
  }

  public static AppModule_ProvideCacheUsuarioFactory create(AppModule module) {
    return new AppModule_ProvideCacheUsuarioFactory(module);
  }

  public static CacheUsuario provideCacheUsuario(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideCacheUsuario());
  }
}
