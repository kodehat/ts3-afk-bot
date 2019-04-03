package de.codehat.teamspeak.afkbot.cli;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class Args {

  @Parameter(
      names = {"-d", "--debug"},
      description = "More verbose logging",
      order = 1)
  private boolean debug = false;

  @Parameter(names = {"-h", "--help"}, description = "Shows help page", help = true, order = 2)
  private boolean help = false;
}
