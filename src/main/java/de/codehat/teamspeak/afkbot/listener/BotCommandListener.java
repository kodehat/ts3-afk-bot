package de.codehat.teamspeak.afkbot.listener;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.google.inject.Inject;
import de.codehat.teamspeak.afkbot.TS3ClientIgnoreList;
import java.util.stream.Collectors;

public class BotCommandListener extends AbstractListener {

  private final int ownId;

  /**
   * Listens to commands that are send to the bot.
   * @param api the TeamSpeak3 api object
   */
  @Inject
  BotCommandListener(final TS3Api api) {
    this.api = api;
    ownId = api.whoAmI().getId();
  }

  @Override
  public void onTextMessage(TextMessageEvent e) {
    int senderId = e.getInvokerId();

    if (e.getTargetMode() != TextMessageTargetMode.CLIENT || senderId == ownId) {
      return;
    }

    if (e.getMessage().trim().equalsIgnoreCase("!toggle")) {
      boolean result = TS3ClientIgnoreList.getInstance().toggle(senderId);

      if (result) {
        api.sendPrivateMessage(senderId, "You are now not being moved!");
      } else {
        api.sendPrivateMessage(senderId, "You are now being moved, if AFK!");
      }
    } else if (e.getMessage().trim().equalsIgnoreCase("!list")) {
      String ignoredClients =
          TS3ClientIgnoreList.getInstance().getIgnoredClients().stream()
              .map((clientId) -> api.getClientInfo(clientId).getNickname())
              .collect(Collectors.joining(", "));
      api.sendPrivateMessage(senderId, "Ignored clients: " + ignoredClients);
    }
  }
}
