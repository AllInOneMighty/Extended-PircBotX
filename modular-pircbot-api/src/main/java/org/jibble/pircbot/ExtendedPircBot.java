package org.jibble.pircbot;

import java.util.List;

/**
 * An extension of {@link PircBot} adding functionalities such as quit request
 * status and help support. This extension is mainly used by modules in a
 * modular bot.
 * 
 * @author Emmanuel Cron
 */
public abstract class ExtendedPircBot extends PircBot {
	private boolean quitRequested;
	
	/**
	 * Returns the trigger used to display the help. This method may return
	 * <tt>null</tt> if no help module has been added to the bot.
	 * 
	 * @return the bot help trigger or <tt>null</tt> if none could be found
	 */
	public abstract String getHelpTrigger();

	/**
	 * Builds a list of lines to be sent to the user that gives help information
	 * on this bot. The returned strings are arbitrary. You usually want to give
	 * the actions that can be triggered on the bot, such as "<tt>!news</tt>",
	 * associated to their description.
	 * 
	 * @param nick the nick of the user who requested help to be displayed
	 * @param inPrivate <tt>true</tt> if the help request was made in a private
	 *        chat, <tt>false</tt> if it was made on a public channel
	 * @return a list of strings containing the help of this bot
	 */
	public abstract List<String> buildHelp(String nick, boolean inPrivate);
	
	/**
	 * Sets whether a user has requested the bot to quit.
	 * 
	 * @param quitRequested <tt>true</tt> if a user has requested a quit action,
	 *        <tt>false</tt> otherwise
	 */
	public void setQuitRequested(boolean quitRequested) {
		this.quitRequested = quitRequested;
	}
	
	/**
	 * Returns whether a quit action was requested on the bot by a user.
	 * 
	 * @return <tt>true</tt> if quit action was requested, <tt>false</tt>
	 *         otherwise
	 */
	public boolean isQuitRequested() {
		return quitRequested;
	}
}
