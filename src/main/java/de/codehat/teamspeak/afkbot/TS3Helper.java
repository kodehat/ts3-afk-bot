package de.codehat.teamspeak.afkbot;

import com.github.theholywaffle.teamspeak3.api.exception.TS3Exception;
import org.tinylog.Logger;

public final class TS3Helper {

  /**
   * Same as @see TS3Helper#safeExecute(Callable), but allows you to modify the error message.
   *
   * @param callable lambda where TeamSpeak related code can be safely executed
   * @param errorMsg the error message
   * @param args arguments passed to the error message
   */
  public static void safeExecute(
      final Callable callable, final String errorMsg, final Object... args) {
    try {
      callable.call();
    } catch (TS3Exception e) {
      Logger.error(e, "Unable to execute TeamSpeak3 command!");
    }
  }

  /**
   * TeamSpeak related code can be safely execute within the {@code callable}. Means all kinds of
   * {@link TS3Exception}s are catched and logged.
   *
   * @param callable lambda where TeamSpeak related code can be safely executed
   */
  public static void safeExecute(final Callable callable) {
    safeExecute(callable, "Unable to execute TeamSpeak3 command!");
  }

//  public static <T> T safeExecute(final Supplier<T> supplier, final String errorMsg, final Object... args) {
//
//  }
//
//  public static <T> T safeExecute(final Supplier<T> supplier) {
//
//  }

  @FunctionalInterface
  public interface Callable {
    void call();
  }
}
