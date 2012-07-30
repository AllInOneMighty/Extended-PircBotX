package org.jibble.pircbot.modules;

import org.jibble.pircbot.ExtendedPircBot;


public class HelpPircModule extends AbstractPircModule implements PublicPircModule, PrivatePircModule {
	
	private String trigger;
	
	private String helpIntro;

	private String helpText;

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
		bot.sendNotice(sender, helpIntro);
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
		bot.sendMessage(sender, helpIntro);
		for (String line : bot.buildHelp(sender, true)) {
			bot.sendMessage(sender, line);
		}
	}
}
