package org.pircbotx.listeners;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.listeners.urlhandlers.URLHandler;
import org.pircbotx.util.URLUtils;

import com.google.common.collect.ImmutableSet;

public class URLListener extends ListenerAdapter<PircBotX> {
  private Set<URLHandler> urlHandlers;
  private int urlsRequiredMinLength = Integer.MAX_VALUE;

  public URLListener(Set<URLHandler> urlHandlers) {
    this.urlHandlers = ImmutableSet.copyOf(urlHandlers);
    for (URLHandler urlHandler : urlHandlers) {
      if (urlsRequiredMinLength > urlHandler.getUrlRequiredMinLength()) {
        urlsRequiredMinLength = urlHandler.getUrlRequiredMinLength();
      }
    }
  }

  @Override
  public void onMessage(MessageEvent<PircBotX> event) {
    String message = event.getMessage();

    if (message.length() < urlsRequiredMinLength) {
      return;
    }

    // Find URLs...
    for (String messagePart : message.split(" ")) {
      if (messagePart.length() < urlsRequiredMinLength) {
        // Too short
        continue;
      }

      URI uri;
      try {
        uri = new URI(messagePart);
      } catch (URISyntaxException e) {
        // Not an URI, skipping
        continue;
      }

      URL url = URLUtils.toPublicHTTPURL(uri);
      if (url == null) {
        continue;
      }

      // Found an URL, now handle it
      for (URLHandler urlHandler : urlHandlers) {
        if (messagePart.length() >= urlHandler.getUrlRequiredMinLength() && urlHandler.matches(url)) {
          urlHandler.handle(event, url);
        }
      }
    }
  }
}
