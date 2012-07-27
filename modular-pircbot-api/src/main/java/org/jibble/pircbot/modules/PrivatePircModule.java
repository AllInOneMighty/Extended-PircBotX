package org.jibble.pircbot.modules;

import org.jibble.pircbot.ExtendedPircBot;

/**
 * A module that can be activated in a private chat.
 * 
 * @author Emmanuel Cron
 */
public interface PrivatePircModule {
	/**
	 * Returns the exact word that a user has to say in a private chat to
	 * activate this module.
	 * 
	 * @return the trigger message of the module
	 */
	String getPrivateTriggerMessage();
	
	/**
	 * This method is called when this module has been activated in a private
	 * chat by a user using its trigger message.
	 * 
	 * @param bot the current bot
	 * @param sender the nick of the user who triggered the module
	 * @param login the login of the user who triggered the module
	 * @param hostname the hostname of the user who triggered the module
	 */
	void onTriggerPrivateMessage(ExtendedPircBot bot, String sender, String login, String hostname);
	
	/**
	 * Returns whether this module can only be activated by an op or not.
	 * 
	 * @return <tt>true</tt> if only ops can activate this module,
	 *         <tt>false</tt> if anybody can do it
	 */
	boolean isOpRequired();
}
