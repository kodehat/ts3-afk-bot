package de.codehat.teamspeak.afkbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdleCheckTask extends TimerTask {
  private static final Logger log = LoggerFactory.getLogger(IdleCheckTask.class.getName());

  private static final long MAX_IDLE_TIME_MUTED = 5 * 60L; // 5 minutes
  private static final long MAX_IDLE_TIME_NOT_MUTED = 10 * 60L; // 10 minutes

  private TS3Api api;
  private Integer botClientId;
  private Integer afkChannelId;

  // Key is channel id, value is the corresponding channel.
  private Map<Integer, Channel> channelMap;
  // Key is client, value is the channel where the client currently is.
  private Map<Client, Channel> clientMap;

  /**
   * Task, which checks repetitively for idling clients. If an idle client is found, it's moved into
   * the defined AFK channel.
   *
   * @param api TS3 API instance
   * @param afkChannelId id of the AFK channel
   */
  public IdleCheckTask(TS3Api api, Integer afkChannelId) {
    this.api = api;
    this.afkChannelId = afkChannelId;

    botClientId = api.whoAmI().getId();
  }

  @Override
  public void run() {
    log.info("Let's see who's idling around!?");

    // Get a fresh piece of channels and clients.
    refreshChannels();
    refreshClients();

    clientMap.forEach(
        (client, channel) -> {
          long idleTime = TimeUnit.MILLISECONDS.toSeconds(client.getIdleTime());

          // Check if client isn't already in AFK channel and if it's not the bot itself.
          if (canBeMoved(client)
              && (isClientIdleAndNotMuted(client) || isClientIdleAndMuted(client))) {
            log.info(
                "Client '{}' is idling for '{}'. Moving it to AFK channel!",
                client.getNickname(),
                client.getIdleTime());

            // Move client. May throw exception if moving is not allowed or something weird happens.
            try {
              api.moveClient(client.getId(), afkChannelId);
            } catch (TS3CommandFailedException e) {
              log.error("Unable to move client '{}'!", client.getNickname());
            }

            // Inform client about move.
            api.sendPrivateMessage(
                client.getId(),
                "You have been moved, because you're idling for " + idleTime + " seconds.");
            // Inform channel that client was moved.
            api.sendChannelMessage(
                channel.getId(),
                String.format(
                    "Client %s was moved, because he was idling too long.", client.getNickname()));
          }
        });
  }

  private boolean canBeMoved(Client c) {
    return c.getId() != botClientId && c.getChannelId() != afkChannelId;
  }

  private boolean isClientIdleAndMuted(final Client c) {
    long idleTime = TimeUnit.MILLISECONDS.toSeconds(c.getIdleTime());
    return idleTime > MAX_IDLE_TIME_MUTED && (c.isInputMuted() || c.isOutputMuted());
  }

  private boolean isClientIdleAndNotMuted(final Client c) {
    long idleTime = TimeUnit.MILLISECONDS.toSeconds(c.getIdleTime());
    return idleTime > MAX_IDLE_TIME_NOT_MUTED && !c.isInputMuted() && !c.isOutputMuted();
  }

  private void refreshChannels() {
    final List<Channel> channels = api.getChannels();
    if (channelMap == null) {
      channelMap = new HashMap<>(channels.size());
    }
    channelMap.clear();

    channels.forEach((c) -> channelMap.put(c.getId(), c));
  }

  private void refreshClients() {
    if (clientMap == null) {
      clientMap = new HashMap<>();
    }
    clientMap.clear();

    api.getClients().forEach((c) -> clientMap.put(c, channelMap.get(c.getChannelId())));
  }
}
