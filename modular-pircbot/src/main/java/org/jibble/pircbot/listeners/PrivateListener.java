package org.jibble.pircbot.listeners;

import org.jibble.pircbot.ExtendedPircBotX;
import org.pircbotx.hooks.events.PrivateMessageEvent;

/**
 * A listener that can be activated in a private chat.
 *
 * @author Emmanuel Cron
 */
public interface PrivateListener extends TriggerableListener {
  /**
   * Returns the exact word that a user has to say in a private chat to trigger this listener.
   */
  String getPrivateTriggerMessage();

  /**
   * This method is called when this listener has been triggered in a private chat by a user using
   * its trigger message.
   *
   * @param event the trigger event
   */
  void onTriggerPrivateMessage(PrivateMessageEvent<ExtendedPircBotX> event);

  /**
   * Returns whether this listener can only be triggered by an OP or not.
   */
  boolean isOpRequired();
}
