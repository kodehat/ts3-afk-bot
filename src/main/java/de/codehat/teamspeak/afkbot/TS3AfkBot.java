package de.codehat.teamspeak.afkbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.codehat.teamspeak.afkbot.config.TS3BotConfig;
import de.codehat.teamspeak.afkbot.listener.BotCommandListener;
import de.codehat.teamspeak.afkbot.listener.PlayerMovementListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.tinylog.Logger;

class TS3AfkBot {

  private static final Long IDLE_CHECK_DELAY = 500L;
  private final TS3BotConfig botConfig;
  private final TS3Api api;
  private final Timer timer;
  private final TimerTask idleCheckTask;
  private final BotCommandListener botCommandListener;
  private final PlayerMovementListener playerMovementListener;
  private boolean checking = false;
  private boolean botCommandListening = false;
  private boolean playerMovementListening = false;

  /** Creates a new TS3 AFK bot. */
  @Inject
  TS3AfkBot(
      final TS3BotConfig botConfig,
      final TS3Api api,
      final Timer timer,
      @Named("IdleCheckTask") final TimerTask idleCheckTask,
      final BotCommandListener botCommandListener,
      final PlayerMovementListener playerMovementListener) {
    this.botConfig = botConfig;
    this.api = api;
    this.timer = timer;
    this.idleCheckTask = idleCheckTask;
    this.botCommandListener = botCommandListener;
    this.playerMovementListener = playerMovementListener;
  }

  /** Starts task to check for idling players. */
  void startRepeatingCheck() {
    if (checking) {
      return;
    }
    checking = true;
    Logger.info(
        "Muted clients are moved after {} seconds, and not muted clients after {} seconds.",
        botConfig.moveMutedThreshold(),
        botConfig.moveListeningThreshold());
    Logger.info("Checking for idle players every {} seconds.", botConfig.checkPeriod());

    timer.scheduleAtFixedRate(
        idleCheckTask, IDLE_CHECK_DELAY, TimeUnit.SECONDS.toMillis(botConfig.checkPeriod()));
  }

  void startListeningBotCommands() {
    if (botCommandListening) {
      return;
    }
    botCommandListening = true;

    api.registerEvent(TS3EventType.TEXT_PRIVATE);
    api.addTS3Listeners(botCommandListener);
  }

  void startListeningToPlayerMovement() {
    if (playerMovementListening) {
      return;
    }
    playerMovementListening = true;

    api.registerEvent(TS3EventType.SERVER);
    api.addTS3Listeners(playerMovementListener);
  }
}
