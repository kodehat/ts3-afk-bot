module de.codehat.teamspeak.afkbot {
 requires com.google.guice;
 requires jcommander;
 requires org.tinylog.api;
 requires owner;
 requires com.github.theholywaffle.teamspeak3;
 opens de.codehat.teamspeak.afkbot.cli;
}