module de.codehat.teamspeak.afkbot {
 requires com.google.guice;
 requires com.beust.jcommander;
 requires tinylog.api;
 requires owner;
 requires static lombok;
 requires teamspeak3.api;
 opens de.codehat.teamspeak.afkbot.cli;
}