package de.codehat.teamspeak.afkbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import org.tinylog.Logger;

public class TS3AfkBot {

  private static final long IDLE_CHECK = 5000L;

  private String host;
  private Integer queryPort;
  private String username;
  private String password;
  private Integer virtualServerId;
  private String nickname;
  private Integer afkChannelId;

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
   */
  public TS3AfkBot(
      String host,
      Integer queryPort,
      String username,
      String password,
      Integer virtualServerId,
      String nickname,
      Integer afkChannelId) {
    this.host = host;
    this.queryPort = queryPort;
    this.username = username;
    this.password = password;
    this.virtualServerId = virtualServerId;
    this.nickname = nickname;
    this.afkChannelId = afkChannelId;
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

  /**
   * Connects the bot to the defined server.
   * Won't execute if bot is already connected.
   */
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

  /**
   * Starts task to check for idling players.
   */
  public void startRepeatingCheck() {
    if (checking) {
      return;
    }
    checking = true;
    Logger.info(
        "Checking for idle player every {} seconds.", TimeUnit.MILLISECONDS.toSeconds(IDLE_CHECK));

    idleCheckTask = new Timer();
    idleCheckTask.scheduleAtFixedRate(new IdleCheckTask(api, afkChannelId), 500L, IDLE_CHECK);
  }

  public TS3Query getQuery() {
    return query;
  }
}
