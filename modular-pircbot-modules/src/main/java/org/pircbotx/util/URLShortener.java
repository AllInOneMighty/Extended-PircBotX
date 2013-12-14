package org.pircbotx.util;

import java.io.IOException;

/**
 * A class providing a URL shortening service; URLs given to the {@link #shortenURL(String)} method
 * are shortened using a particular service that should easily be guessable by reading the class
 * name.
 * 
 * @author Emmanuel Cron
 */
public interface URLShortener {
  /**
   * Shortens the given URL using the service provided by this class. You may want to cache these
   * results if they have a chance to be used repetitively.
   * 
   * @param url the URL to shorten
   * @return the given URL, shortened
   * 
   * @throws IOException if something goes wrong while contacting the URL shortening service
   */
  String shortenURL(String url) throws IOException;
}
