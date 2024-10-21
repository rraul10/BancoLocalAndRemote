package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.users.validator.UserValidator;

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
public final class AppModule_ProvideUserValidatorFactory implements Factory<UserValidator> {
  private final AppModule module;

  public AppModule_ProvideUserValidatorFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public UserValidator get() {
    return provideUserValidator(module);
  }

  public static AppModule_ProvideUserValidatorFactory create(AppModule module) {
    return new AppModule_ProvideUserValidatorFactory(module);
  }

  public static UserValidator provideUserValidator(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideUserValidator());
  }
}
