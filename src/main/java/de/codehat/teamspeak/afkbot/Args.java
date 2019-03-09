package de.codehat.teamspeak.afkbot;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class Args {

  @Parameter
  private String command;

  @Parameter(names = { "-h", "--hostname" }, description = "Hostname to connect to")
  private String hostname = "127.0.0.1";

  @Parameter(names = { "-u", "--username" }, description = "Username for ServerQuery login")
  private String username;

  @Parameter(names = { "-p", "--password" }, description = "Password for ServerQuery login")
  private String password;

  @Parameter(names = { "-i", "--id" }, description = "Id of the virtual server")
  private Integer virtualServerId = 1;

  @Parameter(names = { "-n", "--nickname"}, description = "Nickname for the bot")
  private String nickname = "TS3 AFK Bot";

  @Parameter(names = { "-d", "--debug" }, description = "More verbose logging")
  private boolean debug = false;

  @Parameter(names = "--help", help = true)
  private boolean help;
}
