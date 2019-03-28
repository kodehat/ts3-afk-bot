package de.codehat.teamspeak.afkbot.bot;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.util.TimerTask;

public class BotModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TimerTask.class)
        .annotatedWith(Names.named("IdleCheckTask"))
        .to(IdleCheckTask.class);
  }
}
