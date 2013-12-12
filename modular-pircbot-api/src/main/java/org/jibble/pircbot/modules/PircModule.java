package org.jibble.pircbot.modules;

/**
 * A PircBot module. A module always has some associated help text.
 * 
 * @author Emmanuel Cron
 */
public interface PircModule {
  /**
   * Returns a single line of text explaining what this module does.
   * 
   * @return the description of this module or {@code null} if no description is available
   */
  String getHelpText();
}
