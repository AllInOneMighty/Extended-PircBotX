package org.jibble.pircbot.modules.onaction;

import java.util.regex.Pattern;

/**
 * An action pattern that matches a string containing the name of the bot (such as "
 * {@code tickles <botname>}"). Patterns simply need to use "<tt>{botname}</tt>" in their regexp to
 * match with the name currently used by the bot.
 * 
 * @author Emmanuel Cron
 */
public class DynamicOnActionPattern extends OnActionPattern {
  private String dynamicRegex;

  public DynamicOnActionPattern(String dynamicRegex) {
    this.dynamicRegex = dynamicRegex;
    updatePattern("");
  }

  public void updatePattern(String botname) {
    String regex = dynamicRegex.replaceAll("\\{botname\\}", botname);
    setPattern(Pattern.compile(regex));
  }

  @Override
  public String toString() {
    return getPattern().pattern() + " (regex: " + dynamicRegex + ")";
  }
}
