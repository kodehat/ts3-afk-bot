package de.codehat.teamspeak3.afkbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import org.tinylog.Logger;

public class TS3AfkBot {

  private static final Long IDLE_CHECK_DELAY = 500L;

  private final String host;
  private final Integer queryPort;
  private final String username;
  private final String password;
  private final Integer virtualServerId;
  private final String nickname;
  private final Integer afkChannelId;
  private final Long idleCheckPeriod;
  private final Integer moveMutedClientPeriod;
  private final Integer moveNotMutedClientPeriod;

  private boolean connected = false;
  private boolean checking = false;

  private TS3Config config;
  private TS3Query query;
  private TS3Api api;

  private Timer idleCheckTask;

  /**
   * Creates a new TS3 AFK bot.
   *
   * @param host TS3 server host
   * @param queryPort server query port
   * @param username username for server query login
   * @param password password for server query login
   * @param virtualServerId id of the virtual server in TeamSpeak
   * @param nickname nickname of the bot
   * @param afkChannelId id of the AFK channel
   * @param idleCheckPeriod how often is checked for idling clients in seconds
   * @param moveMutedClientPeriod when are idle muted clients moved in seconds
   * @param moveNotMutedClientPeriod when are idle not muted clients moved in seconds
   */
  public TS3AfkBot(
      String host,
      Integer queryPort,
      String username,
      String password,
      Integer virtualServerId,
      String nickname,
      Integer afkChannelId,
      Integer idleCheckPeriod,
      Integer moveMutedClientPeriod,
      Integer moveNotMutedClientPeriod) {
    this.host = host;
    this.queryPort = queryPort;
    this.username = username;
    this.password = password;
    this.virtualServerId = virtualServerId;
    this.nickname = nickname;
    this.afkChannelId = afkChannelId;
    this.idleCheckPeriod = TimeUnit.SECONDS.toMillis(idleCheckPeriod);
    this.moveMutedClientPeriod = moveMutedClientPeriod;
    this.moveNotMutedClientPeriod = moveNotMutedClientPeriod;
  }

  private static TS3Config buildTS3Config(final String host, final Integer queryPort) {
    final TS3Config config = new TS3Config();
    config.setHost(host);
    config.setQueryPort(queryPort);
    config.setEnableCommunicationsLogging(false);
    return config;
  }

  private static TS3Query buildTS3Query(final TS3Config config) {
    return new TS3Query(config);
  }

  private static TS3Api buildTS3Api(
      final TS3Query query,
      final String nickname,
      final String username,
      final String password,
      final Integer virtualServerId) {
    final TS3Api api = query.getApi();
    api.login(username, password);
    api.selectVirtualServerById(virtualServerId);
    api.setNickname(nickname);
    return api;
  }

  /** Connects the bot to the defined server. Won't execute if bot is already connected. */
  public void connect() {
    if (connected) {
      return;
    }
    Logger.info(
        "Connecting to '{}:{}' as '{}' with nickname '{}'...", host, queryPort, username, nickname);
    connected = true;

    config = buildTS3Config(host, queryPort);
    query = buildTS3Query(config);
    query.connect();
    api = buildTS3Api(query, nickname, username, password, virtualServerId);
  }

  /** Starts task to check for idling players. */
  public void startRepeatingCheck() {
    if (checking) {
      return;
    }
    checking = true;
    Logger.info(
        "Muted clients are moved after {} seconds, and not muted clients after {} seconds.",
        moveMutedClientPeriod,
        moveNotMutedClientPeriod);
    Logger.info("Checking for idle players every {} seconds.", getIdleCheckPeriod());

    idleCheckTask = new Timer();
    idleCheckTask.scheduleAtFixedRate(
        new IdleCheckTask(api, afkChannelId, moveMutedClientPeriod, moveNotMutedClientPeriod),
        IDLE_CHECK_DELAY,
        idleCheckPeriod);
  }

  private Long getIdleCheckPeriod() {
    return TimeUnit.MILLISECONDS.toSeconds(idleCheckPeriod);
  }

  public TS3Query getQuery() {
    return query;
  }
}
