package de.codehat.teamspeak3.afkbot;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TS3ClientIgnoreListTest {

  @Test
  void testIgnoredClientsIsNotNull() {
    assertNotNull(TS3ClientIgnoreList.getInstance().getIgnoredClients());
  }

  // contains

  @Test
  void testIgnoredClientsNotContainsClientId() {
    assertFalse(TS3ClientIgnoreList.getInstance().contains(13));
  }

  @Test
  void testIgnoredClientsContainsClientId() {
    TS3ClientIgnoreList.getInstance().getIgnoredClients().clear();

    TS3ClientIgnoreList.getInstance().ignore(42);
    assertTrue(TS3ClientIgnoreList.getInstance().contains(42));
  }

  // ignore

  @Test
  void testIgnoreClientId() {

  }

}
