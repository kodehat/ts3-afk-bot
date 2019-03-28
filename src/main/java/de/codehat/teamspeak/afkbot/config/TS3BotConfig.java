package de.codehat.teamspeak.afkbot.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources("file:${user.dir}/application.properties")
public interface TS3BotConfig extends Config {
  @DefaultValue("localhost")
  @Key("query.hostname")
  String hostname();

  @DefaultValue("10011")
  @Key("query.port")
  int queryPort();

  @Key("query.username")
  String username();

  @Key("query.password")
  String password();

  @DefaultValue("AFK Bot")
  @Key("query.nickname")
  String nickname();

  @DefaultValue("1")
  @Key("query.virtual.server.id")
  int virtualServerId();

  @Key("query.afk.channel.id")
  int afkChannelId();

  @DefaultValue("5")
  @Key("query.check.period")
  int checkPeriod();

  @DefaultValue(5 * 60 + "")
  @Key("query.threshold.move.muted")
  int moveMutedThreshold();

  @DefaultValue(10 * 60 + "")
  @Key("query.threshold.move.listening")
  int moveListeningThreshold();
}
