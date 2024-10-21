package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.storages.validators.JsonValidator;

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
public final class AppModule_JsonValidatorFactory implements Factory<JsonValidator> {
  private final AppModule module;

  public AppModule_JsonValidatorFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public JsonValidator get() {
    return jsonValidator(module);
  }

  public static AppModule_JsonValidatorFactory create(AppModule module) {
    return new AppModule_JsonValidatorFactory(module);
  }

  public static JsonValidator jsonValidator(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.jsonValidator());
  }
}
