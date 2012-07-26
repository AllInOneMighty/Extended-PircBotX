package org.jibble.pircbot.modules;

import org.jibble.pircbot.ExtendedPircBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoRejoinPircModule extends AbstractPircModule {
	private static final Logger LOGGER = LoggerFactory.getLogger(AutoRejoinPircModule.class);

	@Override
	public void onKick(ExtendedPircBot bot, String channel, String kickerNick, String kickerLogin,
			String kickerHostname,
			String recipientNick, String reason) {
		if (recipientNick.equals(bot.getNick())) {
			LOGGER.info("I have been kicked from {} by {}, rejoining channel", channel, kickerNick);
			bot.joinChannel(channel);
		}
	}
}
