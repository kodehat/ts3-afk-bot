package de.codehat.teamspeak.afkbot.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.aeonbits.owner.ConfigFactory;

public class ConfigurationModule extends AbstractModule {
  @Provides
  @Singleton
  TS3BotConfig provideTS3BotConfig() {
    return ConfigFactory.create(TS3BotConfig.class);
  }
}
