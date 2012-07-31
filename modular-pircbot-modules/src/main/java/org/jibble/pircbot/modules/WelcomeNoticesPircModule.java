package org.jibble.pircbot.modules;

import java.util.List;

import org.jibble.pircbot.ExtendedPircBot;

/**
 * Sends notices to any user that connects to the channel. In the notices, you
 * may use the following codes that will automatically be replaced by the bot:
 * <dl>
 * <dt>{botname}</dt>
 * <dd>Current name of the bot</dd>
 * <dt>{channel}</dt>
 * <dd>Channel that the user has just joined</dd>
 * <dt>{helptrigger}</dt>
 * <dd>Command to send to the bot to display the help. Note that you may not use
 * this code if you do not want your users to know how to display the help.</dd>
 * </dl>
 * 
 * @author Emmanuel Cron
 */
public class WelcomeNoticesPircModule extends AbstractPircModule {
	private List<String> welcomeNotices;
	
	/**
	 * Creates a new welcome module.
	 * 
	 * @param welcomeNotices the messages to send to a user that connects to a
	 *        channel where the bot is connected; special codes used in these
	 *        messages will be replaced (see class description)
	 * @param helpTrigger the command that needs to be sent to the bot to
	 *        display the help
	 */
	public WelcomeNoticesPircModule(List<String> welcomeNotices) {
		this.welcomeNotices = welcomeNotices;
	}

	@Override
	public void onJoin(ExtendedPircBot bot, String channel, String sender, String login, String hostname) {
		if (sender.equals(bot.getNick())) {
			// Don't react to own joins
			return;
		}

		if (welcomeNotices != null) {
			String helpTrigger = "!" + bot.getHelpTrigger();
			String helpPrivateTrigger = bot.getHelpTrigger();

			for (String welcomeNotice : welcomeNotices) {
				welcomeNotice = welcomeNotice.replace("{botname}", bot.getNick());
				welcomeNotice = welcomeNotice.replace("{channel}", channel);
				welcomeNotice = welcomeNotice.replace("{helptrigger}", helpTrigger);
				welcomeNotice = welcomeNotice.replace("{helpprivatetrigger}", helpPrivateTrigger);
				bot.sendNotice(sender, welcomeNotice);
			}
		}
	}
}
