package de.codehat.teamspeak.afkbot;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class Args {

  @Parameter(
      names = {"-h", "--host"},
      description = "Host to connect to",
      required = true,
      order = 0)
  private String host;

  @Parameter(
      names = {"-q", "--port"},
      description = "Query port of the host",
      required = true,
      order = 1
  )
  private Integer queryPort;

  @Parameter(
      names = {"-u", "--username"},
      description = "Username for ServerQuery login",
      required = true,
      order = 2)
  private String username;

  @Parameter(
      names = {"-p", "--password"},
      description = "Password for ServerQuery login",
      required = true,
      order = 3)
  private String password;

  @Parameter(
      names = {"-i", "--id"},
      description = "Id of the virtual server",
      order = 4)
  private Integer virtualServerId = 1;

  @Parameter(
      names = {"-n", "--nickname"},
      description = "Nickname for the bot",
      order = 5)
  private String nickname = "TS3 AFK Bot";

  @Parameter(
      names = {"-a", "--afk-channel"},
      description = "ID of the channel where idle clients are moved to",
      required = true,
      order = 6
  )
  private Integer afkChannelId;

  @Parameter(
      names = {"-d", "--debug"},
      description = "More verbose logging",
      order = 7)
  private boolean debug = false;

  @Parameter(names = "--help", description = "Shows help page", help = true, order = 8)
  private boolean help;
}
