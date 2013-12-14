package org.pircbotx.listeners;

import org.pircbotx.PircBotX;

/**
 * A runnable listener. Runnable listeners are launched when the bot successfully connects to a
 * server. Once a listener is running, it is only stopped when the bot is shut down (= disconnects
 * and quits). Thus, a runnable listener may only be ran once even if the bot reconnects multiple
 * times to a server.
 *
 * @author Emmanuel Cron
 */
public interface RunnableListener extends StoppableListener, Runnable {
  /**
   * Sets the bot that launches this runnable listener.
   */
  public abstract void setBot(PircBotX bot);
}
