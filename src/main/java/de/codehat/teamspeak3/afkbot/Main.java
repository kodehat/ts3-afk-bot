package de.codehat.teamspeak.afkbot;

import com.beust.jcommander.JCommander;
import org.tinylog.Logger;
import org.tinylog.configuration.Configuration;

public class Main {

  private static TS3AfkBot bot;

  /**
   * Main method.
   * @param args arguments passed as command line arguments to the program
   */
  public static void main(String[] args) {
    final Args readArgs = new Args();
    final JCommander jCommander = JCommander.newBuilder().addObject(readArgs).build();
    jCommander.parse(args);

    // Print help if requested.
    if (readArgs.isHelp()) {
      jCommander.usage();
      return;
    }
    // Set debug if requested.
    if (readArgs.isDebug()) {
      Configuration.set("writer.level", "debug");
      Configuration.set(
          "writer.format", "{date} [{thread}] {class}.{method}()\n{level}: {message}");
    } else {
      Configuration.set("writer.level", "info");
      Configuration.set("writer.format", "{level}: {message|indent=4}");
    }

    bot =
        new TS3AfkBot(
            readArgs.getHost(),
            readArgs.getQueryPort(),
            readArgs.getUsername(),
            readArgs.getPassword(),
            readArgs.getVirtualServerId(),
            readArgs.getNickname(),
            readArgs.getAfkChannelId());

    bot.connect();
    bot.startRepeatingCheck();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      Logger.info("Shutting down bot...");
      bot.getQuery().exit();
    }));
  }
}
