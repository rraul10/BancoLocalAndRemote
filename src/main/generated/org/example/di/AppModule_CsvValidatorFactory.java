package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.storages.validators.CsvValidator;

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
public final class AppModule_CsvValidatorFactory implements Factory<CsvValidator> {
  private final AppModule module;

  public AppModule_CsvValidatorFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public CsvValidator get() {
    return csvValidator(module);
  }

  public static AppModule_CsvValidatorFactory create(AppModule module) {
    return new AppModule_CsvValidatorFactory(module);
  }

  public static CsvValidator csvValidator(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.csvValidator());
  }
}
