package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.users.storage.StorageCsvUser;

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
public final class AppModule_StorageCsvUserFactory implements Factory<StorageCsvUser> {
  private final AppModule module;

  public AppModule_StorageCsvUserFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public StorageCsvUser get() {
    return storageCsvUser(module);
  }

  public static AppModule_StorageCsvUserFactory create(AppModule module) {
    return new AppModule_StorageCsvUserFactory(module);
  }

  public static StorageCsvUser storageCsvUser(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.storageCsvUser());
  }
}
