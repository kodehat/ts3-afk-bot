package de.codehat.teamspeak.afkbot;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger log = LoggerFactory.getLogger(Main.class.getName());

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
      // TODO: Check if debug is enabled and modify logger.
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
      log.info("Shutting down bot...");
      bot.getQuery().exit();
    }));
  }
}
