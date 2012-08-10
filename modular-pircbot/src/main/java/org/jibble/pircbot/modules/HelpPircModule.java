package org.jibble.pircbot.modules;

import org.apache.commons.lang3.StringUtils;
import org.jibble.pircbot.ExtendedPircBot;

/**
 * Automatically provides help that users can request by typing a trigger either
 * on a public channel (help will be sent as <tt>NOTICE</tt> messages) or in a
 * private chat.
 * <p>
 * The module automatically detects which modules the bot has and adds one line
 * of description for each of them that can be triggered. Additionally, it
 * detects if the user requesting help is an administrator, and hides any module
 * that is op-only to normal users.
 * 
 * @author Emmanuel Cron
 */
public class HelpPircModule extends AbstractPircModule implements PublicPircModule, PrivatePircModule {
	
	private String trigger;
	
	private String helpIntro;

	private String helpText;

	/**
	 * Creates a new help module.
	 * 
	 * @param trigger the trigger users have to say in a public channel
	 *        (prefixed by "<tt>!</tt>") or in a private chat (not prefixed) to
	 *        display help
	 * @param helpIntro an optional introduction sentence such as
	 *        "These are the available commands:"
	 */
	public HelpPircModule(String trigger, String helpIntro) {
		this.trigger = trigger;
		this.helpIntro = helpIntro;
	}

	@Override
	public boolean isOpRequired() {
		return false;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	@Override
	public String getHelpText() {
		return helpText;
	}
	
	@Override
	public String getTriggerMessage() {
		return trigger;
	}
	
	@Override
	public void onTriggerMessage(ExtendedPircBot bot, String channel, String sender, String login, String hostname) {
		if (StringUtils.isNotBlank(helpIntro)) {
			bot.sendNotice(sender, helpIntro);
		}
		for (String line : bot.buildHelp(sender, false)) {
			bot.sendNotice(sender, line);
		}
	}
	
	@Override
	public String getPrivateTriggerMessage() {
		return trigger;
	}
	
	@Override
	public void onTriggerPrivateMessage(ExtendedPircBot bot, String sender, String login, String hostname) {
		if (StringUtils.isNotBlank(helpIntro)) {
			bot.sendMessage(sender, helpIntro);
		}
		for (String line : bot.buildHelp(sender, true)) {
			bot.sendMessage(sender, line);
		}
	}
}
