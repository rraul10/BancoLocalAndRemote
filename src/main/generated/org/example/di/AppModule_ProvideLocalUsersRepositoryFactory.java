package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.client.repository.user.UsersRepository;

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
public final class AppModule_ProvideLocalUsersRepositoryFactory implements Factory<UsersRepository> {
  private final AppModule module;

  public AppModule_ProvideLocalUsersRepositoryFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public UsersRepository get() {
    return provideLocalUsersRepository(module);
  }

  public static AppModule_ProvideLocalUsersRepositoryFactory create(AppModule module) {
    return new AppModule_ProvideLocalUsersRepositoryFactory(module);
  }

  public static UsersRepository provideLocalUsersRepository(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideLocalUsersRepository());
  }
}
