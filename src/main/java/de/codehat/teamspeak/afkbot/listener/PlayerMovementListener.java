package de.codehat.teamspeak.afkbot.listener;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.google.inject.Inject;
import de.codehat.teamspeak.afkbot.TS3ClientIgnoreList;
import org.tinylog.Logger;

public class PlayerMovementListener extends AbstractListener {
  @Inject
  PlayerMovementListener(final TS3Api api) {
    this.api = api;
  }

  @Override
  public void onClientLeave(ClientLeaveEvent e) {
    final int clientId = e.getClientId();
    Logger.debug("Removing client with ID '{}' from ignore list, because he left the server.", clientId);
    TS3ClientIgnoreList.getInstance().listen(e.getClientId());
  }
}
