package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.notification.TarjetaNotificacion;

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
public final class AppModule_ProviedTarjetaNotificationsFactory implements Factory<TarjetaNotificacion> {
  private final AppModule module;

  public AppModule_ProviedTarjetaNotificationsFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public TarjetaNotificacion get() {
    return proviedTarjetaNotifications(module);
  }

  public static AppModule_ProviedTarjetaNotificationsFactory create(AppModule module) {
    return new AppModule_ProviedTarjetaNotificationsFactory(module);
  }

  public static TarjetaNotificacion proviedTarjetaNotifications(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.proviedTarjetaNotifications());
  }
}
