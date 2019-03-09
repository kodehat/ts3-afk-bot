package de.codehat.teamspeak.afkbot;

import com.beust.jcommander.JCommander;

public class Main {

  public static void main(String[] args) {
    Args readArgs = new Args();
    JCommander.newBuilder()
        .addObject(readArgs)
        .build()
        .parse(args);

  }
}
