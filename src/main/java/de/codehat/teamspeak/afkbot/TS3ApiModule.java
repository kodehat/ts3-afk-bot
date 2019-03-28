package de.codehat.teamspeak.afkbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.codehat.teamspeak.afkbot.config.TS3BotConfig;

public class TS3ApiModule extends AbstractModule {
  @Provides
  @Singleton
  TS3Config providesTS3Config(TS3BotConfig botConfig) {
    final TS3Config config = new TS3Config();
    config.setHost(botConfig.hostname());
    config.setQueryPort(botConfig.queryPort());
    config.setEnableCommunicationsLogging(false);
    config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());
    return config;
  }

  @Provides
  @Singleton
  TS3Query providesTS3Query(TS3Config config) {
    return new TS3Query(config);
  }

  @Provides
  @Singleton
  TS3Api providesTS3Api(TS3Query query, TS3BotConfig botConfig) {
    final TS3Api api = query.getApi();
    api.login(botConfig.username(), botConfig.password());
    api.selectVirtualServerById(botConfig.virtualServerId());
    api.setNickname(botConfig.nickname());
    return api;
  }
}
