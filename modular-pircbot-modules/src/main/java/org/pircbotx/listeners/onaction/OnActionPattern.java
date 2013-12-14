package org.pircbotx.listeners.onaction;

import java.util.regex.Pattern;

/**
 * A pattern used to match actions made by users on a channel to detect if the bot needs to react
 * (by sending an {@link Answer} back).
 * 
 * @author Emmanuel Cron
 */
public abstract class OnActionPattern {
  private Pattern pattern;

  protected final void setPattern(Pattern pattern) {
    this.pattern = pattern;
  }

  public final Pattern getPattern() {
    return pattern;
  }

  /**
   * Returns {@code true} if this action pattern matches the given action
   * 
   * @param action the action to match
   * @return {@code true} if it matches
   */
  public boolean matches(String action) {
    return getPattern().matcher(action).matches();
  }
}
