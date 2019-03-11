package de.codehat.teamspeak3.afkbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.tinylog.Logger;

public class IdleCheckTask extends TimerTask {

  private final TS3Api api;
  private final Integer afkChannelId;
  private final Long moveMutedClientPeriod;
  private final Long moveNotMutedClientPeriod;

  private Integer botClientId;

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
  public IdleCheckTask(
      TS3Api api,
      Integer afkChannelId,
      Integer moveMutedClientPeriod,
      Integer moveNotMutedClientPeriod) {
    this.api = api;
    this.afkChannelId = afkChannelId;
    this.moveMutedClientPeriod = moveMutedClientPeriod.longValue();
    this.moveNotMutedClientPeriod = moveNotMutedClientPeriod.longValue();

    TS3Helper.safeExecute(() -> botClientId = api.whoAmI().getId());
  }

  @Override
  public void run() {
    Logger.debug("Let's see who's idling around!?");

    // Get a fresh piece of channels and clients.
    refreshChannels();
    refreshClients();

    clientMap.forEach(
        (client, channel) -> {
          long idleTime = TimeUnit.MILLISECONDS.toSeconds(client.getIdleTime());

          // Check if client isn't already in AFK channel and if it's not the bot itself.
          if (canBeMoved(client)
              && (isClientIdleAndNotMuted(client) || isClientIdleAndMuted(client))) {
            Logger.info(
                "Client '{}' is idling for '{}'. Moving it to AFK channel!",
                client.getNickname(),
                client.getIdleTime());

            // Move client. May throw exception if moving is not allowed or something weird happens.
            TS3Helper.safeExecute(
                () -> api.moveClient(client.getId(), afkChannelId),
                "Unable to move client '{}'!",
                client.getNickname());

            // Inform client about move.
            api.sendPrivateMessage(
                client.getId(),
                "You have been moved, because you're idling for " + idleTime + " seconds.");
            // Inform channel that client was moved.
//            api.sendChannelMessage(
//                channel.getId(),
//                String.format(
//                    "Client %s was moved, because he was idling too long.", client.getNickname()));
          }
        });
  }

  private boolean canBeMoved(Client c) {
    return c.getId() != botClientId && c.getChannelId() != afkChannelId;
  }

  private boolean isClientIdleAndMuted(final Client c) {
    long idleTime = TimeUnit.MILLISECONDS.toSeconds(c.getIdleTime());
    return idleTime > moveMutedClientPeriod && (c.isInputMuted() || c.isOutputMuted());
  }

  private boolean isClientIdleAndNotMuted(final Client c) {
    long idleTime = TimeUnit.MILLISECONDS.toSeconds(c.getIdleTime());
    return idleTime > moveNotMutedClientPeriod && !c.isInputMuted() && !c.isOutputMuted();
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
