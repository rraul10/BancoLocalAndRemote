package org.example.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.example.notification.UserNotifications;

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
public final class AppModule_ProviedUserNotificationsFactory implements Factory<UserNotifications> {
  private final AppModule module;

  public AppModule_ProviedUserNotificationsFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public UserNotifications get() {
    return proviedUserNotifications(module);
  }

  public static AppModule_ProviedUserNotificationsFactory create(AppModule module) {
    return new AppModule_ProviedUserNotificationsFactory(module);
  }

  public static UserNotifications proviedUserNotifications(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.proviedUserNotifications());
  }
}
