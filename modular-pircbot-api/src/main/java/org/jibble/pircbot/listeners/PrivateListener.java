package org.jibble.pircbot.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.PrivateMessageEvent;

/**
 * A module that can be activated in a private chat.
 *
 * @author Emmanuel Cron
 */
public interface PrivateListener extends TriggerableListener {
  /**
   * Returns the exact word that a user has to say in a private chat to activate this module.
   *
   * @return the private trigger message of the module
   */
  String getPrivateTriggerMessage();

  /**
   * This method is called when this module has been activated in a private chat by a user using its
   * trigger message.
   *
   * @param bot the current bot
   * @param sender the nick of the user who triggered the module
   * @param login the login of the user who triggered the module
   * @param hostname the hostname of the user who triggered the module
   */
  void onTriggerPrivateMessage(PrivateMessageEvent<? extends PircBotX> event);

  /**
   * Returns whether this module can only be activated by an op or not.
   *
   * @return {@code true} if only ops can activate this module, {@code false} if anybody can do it
   */
  boolean isOpRequired();
}
