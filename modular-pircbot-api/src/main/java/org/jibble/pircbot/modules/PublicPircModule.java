package org.jibble.pircbot.modules;

import org.jibble.pircbot.ExtendedPircBot;

public interface PublicPircModule {
	String getTriggerMessage();
	
	String getHelp();
	
	void onTriggerMessage(ExtendedPircBot bot, String channel, String sender, String login, String hostname,
			String message);
}
