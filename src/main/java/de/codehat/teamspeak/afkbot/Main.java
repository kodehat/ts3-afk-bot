package de.codehat.teamspeak.afkbot;

import com.beust.jcommander.JCommander;
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

  @Inject
  Main(final TS3AfkBot bot, final TS3Query query) {
    this.bot = bot;
    this.query = query;
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

    // Finally - after connecting to the server - start all checks/tasks etc.
    final Main main = injector.getInstance(Main.class);
    main.start();
  }

  private static void setLoggerConfiguration(final boolean isDebug) {
    if (isDebug) {
      Configuration.set("writer.level", "debug");
      Configuration.set(
          "writer.format", "{date} [{thread}] {class}.{method}()\n{level}: {message}");
    } else {
      Configuration.set("writer.level", "info");
      Configuration.set("writer.format", "{level}: {message|indent=4}");
    }
  }

  private void start() {
    bot.startRepeatingCheck();
    bot.startListeningBotCommands();
    bot.startListeningToPlayerMovement();

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
