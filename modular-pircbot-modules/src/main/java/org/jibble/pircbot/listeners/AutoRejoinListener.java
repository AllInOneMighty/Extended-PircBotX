package org.jibble.pircbot.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.KickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Makes the bot try to rejoin channels when it is kicked from one for any reason.
 *
 * @author Emmanuel Cron
 */
public class AutoRejoinListener extends ListenerAdapter<PircBotX> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AutoRejoinListener.class);

  @Override
  public void onKick(KickEvent<PircBotX> event) {
    if (event.getRecipient().getNick().equals(event.getBot().getNick())) {
      LOGGER.info("I have been kicked from {} by {}, rejoining channel (reason: {})", new Object[] {
          event.getChannel().getName(), event.getUser().getNick(), event.getReason()});
      event.getBot().sendIRC().joinChannel(event.getChannel().getName());
    }
  }
}
