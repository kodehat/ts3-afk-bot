package de.codehat.teamspeak3.afkbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import java.util.HashSet;
import java.util.Set;

public class TS3ClientIgnoreList {

  private static TS3ClientIgnoreList ourInstance = new TS3ClientIgnoreList();

  private Set<Integer> ignoredClients;

  public static TS3ClientIgnoreList getInstance() {
    return ourInstance;
  }

  private TS3ClientIgnoreList() {
    this.ignoredClients = new HashSet<>();
  }

  public boolean contains(int clientId) {
    return ignoredClients.contains(clientId);
  }

  public boolean contains(Client client) {
    return contains(client.getId());
  }

  public boolean ignore(int clientId) {
    return ignoredClients.add(clientId);
  }

  public boolean ignore(Client client) {
    return ignore(client.getId());
  }

  public boolean listen(int clientId) {
    return ignoredClients.remove(clientId);
  }

  public boolean listen(Client client) {
    return listen(client.getId());
  }

  public boolean toggle(int clientId) {
    if (!contains(clientId)) {
      ignore(clientId);
      return true;
    } else {
      listen(clientId);
      return false;
    }
  }

  public boolean toggle(Client client) {
    return toggle(client.getId());
  }

  public Set<Integer> getIgnoredClients() {
    return ignoredClients;
  }
}
