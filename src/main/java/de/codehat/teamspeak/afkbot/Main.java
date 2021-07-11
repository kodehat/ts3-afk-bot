package de.codehat.teamspeak.afkbot;

import com.beust.jcommander.JCommander;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.codehat.teamspeak.afkbot.bot.BotModule;
import de.codehat.teamspeak.afkbot.cli.Args;
import de.codehat.teamspeak.afkbot.config.ConfigurationModule;
import de.codehat.teamspeak.afkbot.config.TS3BotConfig;
import org.tinylog.Logger;
import org.tinylog.configuration.Configuration;

public class Main {

  private final TS3AfkBot bot;
  private final TS3Query query;
  private final TS3BotConfig botConfig;

  @Inject
  Main(final TS3AfkBot bot, final TS3Query query, final TS3BotConfig botConfig) {
    this.bot = bot;
    this.query = query;
    this.botConfig = botConfig;
  }

  /**
   * Main method.
   *
   * @param args arguments passed as command line arguments to the program
   */
  public static void main(String[] args) {
    //Logger.info(String.join(",", args));

    // Create Injector.
    final Injector injector =
        Guice.createInjector(new ConfigurationModule(), new TS3ApiModule(), new BotModule());

    // Get all command-line arguments and parse them afterwards.
    final Args readArgs = injector.getInstance(Args.class);
    final JCommander jCommander = JCommander.newBuilder().addObject(readArgs).build();
    jCommander.parse(args);

    // Print help if requested.
    if (readArgs.isHelp()) {
      jCommander.usage();
      return;
    }
    // Set debug if requested.
    setLoggerConfiguration(readArgs.isDebug());

    // Get Bot's configuration to log some values.
    final TS3BotConfig botConfig = injector.getInstance(TS3BotConfig.class);
    Logger.info(
        "Connecting to '{}:{}' as '{}' with nickname '{}'...",
        botConfig.hostname(),
        botConfig.queryPort(),
        botConfig.username(),
        botConfig.nickname());

    // Get TeamSpeak API query and connect to the server.
    final TS3Query query = injector.getInstance(TS3Query.class);
    query.connect();

    if (query.isConnected()) {
      Logger.info("Connected!");
    } else {
      Logger.warn("Query was unable to connect! Exiting...");
      return;
    }

    final TS3Api api = injector.getInstance(TS3Api.class);
    api.selectVirtualServerById(botConfig.virtualServerId());
    // Only set nickname if it's not the same as the current one.
    if (!api.whoAmI().getNickname().equals(botConfig.nickname())) {
      api.setNickname(botConfig.nickname());
    }

    // Finally - after connecting to the server - start all checks/tasks etc.
    final var main = injector.getInstance(Main.class);
    main.start();
  }

  private static void setLoggerConfiguration(final boolean isDebug) {
    if (isDebug) {
      Configuration.set("writer.level", "debug");
      Configuration.set(
          "writer.format", "{date} [{thread}] {class}.{method}()\n{level}: {message}");
      Logger.debug("Debug mode is enabled!");
    } else {
      Configuration.set("writer.level", "info");
      Configuration.set("writer.format", "{level}: {message|indent=4}");
    }
  }

  private void start() {
    bot.startRepeatingCheck();
    if (botConfig.enableMoveToggling()) {
      Logger.info("Toggle moving is enabled.");
      bot.startListeningBotCommands();
      bot.startListeningToPlayerMovement();
    }

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  Logger.info("Shutting down bot...");
                  stop();
                }));
  }

  private void stop() {
    query.exit();
  }
}
