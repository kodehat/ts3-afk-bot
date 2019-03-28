package de.codehat.teamspeak.afkbot.listener;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;

public abstract class AbstractListener extends TS3EventAdapter {
  protected TS3Api api;
}
