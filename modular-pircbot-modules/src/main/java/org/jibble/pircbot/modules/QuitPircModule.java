package org.jibble.pircbot.modules;

import org.apache.commons.lang3.StringUtils;
import org.jibble.pircbot.ExtendedPircBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuitPircModule extends AbstractPircModule implements PrivatePircModule {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QuitPircModule.class);

	private String trigger;

	private String quitMessage;

	public QuitPircModule(String trigger, String quitMessage) {
		this.trigger = trigger;
		this.quitMessage = quitMessage;
	}
	
	@Override
	public String getPrivateTriggerMessage() {
		return trigger;
	}
	
	@Override
	public boolean isOpRequired() {
		return true;
	}

	@Override
	public void onTriggerPrivateMessage(ExtendedPircBot bot, String sender, String login, String hostname) {
		LOGGER.info("Bot was requested to quit by {}", sender);
		bot.setQuitRequested(true);

		if (StringUtils.isNotBlank(quitMessage)) {
			bot.quitServer(quitMessage);
		} else {
			bot.quitServer();
		}
	}
}
