package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.users.repository.UserRemoteRepositoryImpl;

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
public final class AppModule_ProvideUserRemoteRepositoryFactory implements Factory<UserRemoteRepositoryImpl> {
  private final AppModule module;

  public AppModule_ProvideUserRemoteRepositoryFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public UserRemoteRepositoryImpl get() {
    return provideUserRemoteRepository(module);
  }

  public static AppModule_ProvideUserRemoteRepositoryFactory create(AppModule module) {
    return new AppModule_ProvideUserRemoteRepositoryFactory(module);
  }

  public static UserRemoteRepositoryImpl provideUserRemoteRepository(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideUserRemoteRepository());
  }
}
