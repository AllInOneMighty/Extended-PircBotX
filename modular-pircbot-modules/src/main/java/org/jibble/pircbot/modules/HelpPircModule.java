package org.jibble.pircbot.modules;

import org.jibble.pircbot.ExtendedPircBot;


public class HelpPircModule extends AbstractPircModule implements PublicPircModule, PrivatePircModule {
	
	private String trigger;
	
	private String helpMessage;

	public HelpPircModule(String trigger) {
		this.trigger = trigger;
	}

	@Override
	public String getTriggerMessage() {
		return trigger;
	}
	
	@Override
	public boolean isOpRequired() {
		return false;
	}

	public void setHelp(String helpMessage) {
		this.helpMessage = helpMessage;
	}

	@Override
	public String getHelp() {
		return helpMessage;
	}
	
	@Override
	public void onTriggerMessage(ExtendedPircBot bot, String channel, String sender, String login, String hostname,
			String message) {
		for (String line : bot.buildHelp(sender, false)) {
			bot.sendNotice(sender, line);
		}
	}
	
	@Override
	public String getPrivateTriggerMessage() {
		return trigger;
	}
	
	@Override
	public void onTriggerPrivateMessage(ExtendedPircBot bot, String sender, String login, String hostname,
			String message) {
		for (String line : bot.buildHelp(sender, true)) {
			bot.sendMessage(sender, line);
		}
	}
}
