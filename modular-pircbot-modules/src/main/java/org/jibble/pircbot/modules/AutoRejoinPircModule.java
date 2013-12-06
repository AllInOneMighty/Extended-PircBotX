package org.jibble.pircbot.modules;

import org.jibble.pircbot.ExtendedPircBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Makes the bot try to rejoin any channel when it is kicked for any reason.
 * 
 * @author Emmanuel Cron
 */
public class AutoRejoinPircModule extends AbstractPircModule {
  private static final Logger LOGGER = LoggerFactory.getLogger(AutoRejoinPircModule.class);

  @Override
  public void onKick(ExtendedPircBot bot, String channel, String kickerNick, String kickerLogin,
      String kickerHostname, String recipientNick, String reason) {
    if (recipientNick.equals(bot.getNick())) {
      LOGGER.info("I have been kicked from {} by {}, rejoining channel (reason: {})", new Object[] {
          channel, kickerNick, reason});
      bot.joinChannel(channel);
    }
  }
}
