package org.jibble.pircbot.modules;

/**
 * A stoppable module. Stoppable modules have a single method {@link #stop()} that is called when
 * when the bot is shut down (= disconnects and quits).
 * 
 * @author Emmanuel Cron
 */
public abstract class AbstractStoppablePircModule extends AbstractPircModule {
  /**
   * When the bot is shut down, it requests all modules implementing this interface to stop
   * immediately. The module should do so rapidly (&lt; 500ms) since the bot will kill any running
   * thread after a finite amount of time.
   */
  public abstract void stop();
}
