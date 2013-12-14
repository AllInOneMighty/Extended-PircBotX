package org.pircbotx.listeners;

/**
 * A PircBotX listener that can be triggered. This kind of listeners always has some associated help
 * text.
 *
 * @author Emmanuel Cron
 */
public interface TriggerableListener {
  /**
   * Returns a single line of text explaining what this module does.
   *
   * @return the description or {@code null} if none is available
   */
  String getHelpText();
}
