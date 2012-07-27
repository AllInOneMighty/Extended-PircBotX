package org.jibble.pircbot.modules;

import org.jibble.pircbot.ExtendedPircBot;

/**
 * A module that can be activated through a public channel.
 * 
 * @author Emmanuel Cron
 */
public interface PublicPircModule {
	/**
	 * Returns the exact word that a user has to say in a public channel chat to
	 * activate this module.
	 * 
	 * @return the trigger message of the module
	 */
	String getTriggerMessage();
	
	/**
	 * Returns a single line of text explaining what this module does.
	 * 
	 * @return the description of this module
	 */
	String getHelp();
	
	/**
	 * This method is called when this module has been activated in a public
	 * channel by a user using its trigger message.
	 * 
	 * @param bot the current bot
	 * @param channel the channel where the trigger happened
	 * @param sender the nick of the user who triggered the module
	 * @param login the login of the user who triggered the module
	 * @param hostname the hostname of the user who triggered the module
	 */
	void onTriggerMessage(ExtendedPircBot bot, String channel, String sender, String login, String hostname);
}
