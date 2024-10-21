package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.client.database.LocalDataBaseManager;

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
public final class AppModule_ProvideLocalDataBaseManagerFactory implements Factory<LocalDataBaseManager> {
  private final AppModule module;

  public AppModule_ProvideLocalDataBaseManagerFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public LocalDataBaseManager get() {
    return provideLocalDataBaseManager(module);
  }

  public static AppModule_ProvideLocalDataBaseManagerFactory create(AppModule module) {
    return new AppModule_ProvideLocalDataBaseManagerFactory(module);
  }

  public static LocalDataBaseManager provideLocalDataBaseManager(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideLocalDataBaseManager());
  }
}
