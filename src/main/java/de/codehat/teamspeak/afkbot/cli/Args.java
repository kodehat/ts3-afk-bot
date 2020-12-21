package de.codehat.teamspeak.afkbot.cli;

import com.beust.jcommander.Parameter;

public class Args {

  @Parameter(
      names = {"-d", "--debug"},
      description = "More verbose logging",
      order = 1)
  private boolean debug = false;

  @Parameter(names = {"-h", "--help"}, description = "Shows help page", help = true, order = 2)
  private boolean help = false;

  public boolean isDebug() {
    return debug;
  }

  public boolean isHelp() {
    return help;
  }
}
