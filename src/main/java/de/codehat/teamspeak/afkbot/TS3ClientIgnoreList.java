package de.codehat.teamspeak.afkbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import java.util.HashSet;
import java.util.Set;

public class TS3ClientIgnoreList {

  private static final TS3ClientIgnoreList ourInstance = new TS3ClientIgnoreList();

  private final Set<Integer> ignoredClients;

  private TS3ClientIgnoreList() {
    this.ignoredClients = new HashSet<>();
  }

  public static TS3ClientIgnoreList getInstance() {
    return ourInstance;
  }

  public boolean contains(int clientId) {
    return ignoredClients.contains(clientId);
  }

  public boolean contains(Client client) {
    return contains(client.getId());
  }

  public synchronized boolean ignore(int clientId) {
    return ignoredClients.add(clientId);
  }

  public synchronized boolean ignore(Client client) {
    return ignore(client.getId());
  }

  public synchronized boolean listen(int clientId) {
    return ignoredClients.remove(clientId);
  }

  public synchronized boolean listen(Client client) {
    return listen(client.getId());
  }

  public synchronized boolean toggle(int clientId) {
    if (!contains(clientId)) {
      ignore(clientId);
      return true;
    } else {
      listen(clientId);
      return false;
    }
  }

  public synchronized boolean toggle(Client client) {
    return toggle(client.getId());
  }

  public Set<Integer> getIgnoredClients() {
    return new HashSet<>(ignoredClients);
  }
}
