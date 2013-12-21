package org.pircbotx.listeners.urlhandlers;

import java.net.URL;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * A URL handler. Handlers of this kind take an URL as input and do whatever they want with it:
 * display it in a channel or in a private chat, retrieve a document from somewhere, get information
 * on that URL and make it available to all users, ...
 *
 * @author Emmanuel Cron
 */
public interface URLHandler {
  /**
   * The minimum length of the URLs this class can handle. The returned value should include the
   * minimum length of mandatory parts, e.g. IDs.
   */
  int getUrlRequiredMinLength();

  /**
   * Returns {@code true} if the given URL can be handled by this handler. You generally want to
   * keep checks simple in this method, as you will likely need to parse the URL in
   * {@link #handle(MessageEvent, URL)} anyway.
   */
  boolean matches(URL url);

  /**
   * Handles the URL that was detected in the given event. This method will be called for each URL
   * found in that event.
   */
  void handle(MessageEvent<PircBotX> event, URL url);
}
