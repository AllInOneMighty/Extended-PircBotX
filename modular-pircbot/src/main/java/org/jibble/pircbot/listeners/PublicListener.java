package org.jibble.pircbot.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * A listener that can be activated in a public channel.
 *
 * @author Emmanuel Cron
 */
public interface PublicListener extends TriggerableListener {
  /**
   * Returns the word that a user has to say in a public channel to trigger this listener. Note that
   * an exclamation mark will have to prefix this word for the bot to recognize it.
   * <p>
   * For example, if this method returns "{@code help}", a user will have to say "{@code !help}" in
   * a public channel to trigger this listener.
   */
  String getTriggerMessage();

  /**
   * This method is called when this listener has been activated in a public channel by a user using
   * its trigger message.
   *
   * @param event the trigger event
   */
  void onTriggerMessage(MessageEvent<? extends PircBotX> event);
}
