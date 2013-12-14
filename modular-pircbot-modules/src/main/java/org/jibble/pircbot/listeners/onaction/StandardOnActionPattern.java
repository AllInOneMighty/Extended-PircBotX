package org.jibble.pircbot.listeners.onaction;

import java.util.regex.Pattern;

/**
 * An action pattern that matches a simple string (such as "{@code launches .*}").
 * 
 * @author Emmanuel Cron
 */
public class StandardOnActionPattern extends OnActionPattern {
  public StandardOnActionPattern(Pattern pattern) {
    setPattern(pattern);
  }

  @Override
  public String toString() {
    return getPattern().pattern();
  }
}
