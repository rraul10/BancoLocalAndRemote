package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.client.storage.StorageJsonClient;

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
public final class AppModule_StorageJsonClientFactory implements Factory<StorageJsonClient> {
  private final AppModule module;

  public AppModule_StorageJsonClientFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public StorageJsonClient get() {
    return storageJsonClient(module);
  }

  public static AppModule_StorageJsonClientFactory create(AppModule module) {
    return new AppModule_StorageJsonClientFactory(module);
  }

  public static StorageJsonClient storageJsonClient(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.storageJsonClient());
  }
}
