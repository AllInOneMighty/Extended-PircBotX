package org.jibble.pircbot.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * A module that can be activated in a public channel.
 *
 * @author Emmanuel Cron
 */
public interface PublicListener extends TriggerableListener {
  /**
   * Returns the word that a user has to say in a public channel chat to activate this module. Note
   * that an exclamation mark will have to prefix this word for the bot to recognize it.
   * <p>
   * For example, if this method returns "{@code help}", a user will have to say "{@code !help}" in
   * a public channel to trigger this module.
   *
   * @return the trigger message of the module
   */
  String getTriggerMessage();

  /**
   * This method is called when this module has been activated in a public channel by a user using
   * its trigger message.
   *
   * @param bot the current bot
   * @param channel the channel where the trigger happened
   * @param sender the nick of the user who triggered the module
   * @param login the login of the user who triggered the module
   * @param hostname the hostname of the user who triggered the module
   */
  void onTriggerMessage(MessageEvent<? extends PircBotX> event);
}
