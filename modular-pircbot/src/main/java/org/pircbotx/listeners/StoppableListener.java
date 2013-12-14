package org.pircbotx.listeners;

/**
 * A stoppable listener. Stoppable listeners have a single method {@link #stop()} that is called
 * when the bot is shut down (= disconnects and quits).
 *
 * @author Emmanuel Cron
 */
public interface StoppableListener {
  /**
   * When the bot is shut down, it requests all listeners implementing this interface to stop
   * immediately. The module should do so rapidly (&lt; 500ms) since the bot will kill any reamining
   * running thread after a finite amount of time.
   */
  public abstract void stop();
}
