package org.jibble.pircbot.listeners;

import org.pircbotx.PircBotX;



/**
 * A runnable module. Runnable modules are launched when the bot successfully connects to a server.
 * Once a module is running, it is only stopped when the bot is shut down (= disconnects and quits).
 * Thus, a runnable module may only be ran once even if the bot reconnects multiple times to a
 * server.
 *
 * @author Emmanuel Cron
 */
public interface RunnableListener extends StoppableListener, Runnable {
  /**
   * Sets the bot that launches this runnable module.
   *
   * @param bot the bot that runs this module
   */
  public abstract void setBot(PircBotX bot);
}
