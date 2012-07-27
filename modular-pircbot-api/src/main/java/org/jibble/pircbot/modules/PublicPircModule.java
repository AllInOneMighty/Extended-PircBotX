package org.jibble.pircbot.modules;

import org.jibble.pircbot.ExtendedPircBot;

/**
 * A module that can be activated through a public channel.
 * 
 * @author Emmanuel Cron
 */
public interface PublicPircModule {
	String getTriggerMessage();
	
	String getHelp();
	
	void onTriggerMessage(ExtendedPircBot bot, String channel, String sender, String login, String hostname,
			String message);
}
