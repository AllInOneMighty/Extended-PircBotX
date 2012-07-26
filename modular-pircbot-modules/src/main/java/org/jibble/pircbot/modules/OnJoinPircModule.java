package org.jibble.pircbot.modules;

import java.util.List;

import org.jibble.pircbot.ExtendedPircBot;

public class OnJoinPircModule extends AbstractPircModule {
	private List<String> welcomeMessages;
	
	private String helpTrigger;

	public OnJoinPircModule(List<String> welcomeMessages, String helpTrigger) {
		this.welcomeMessages = welcomeMessages;
		this.helpTrigger = helpTrigger;
	}

	@Override
	public void onJoin(ExtendedPircBot bot, String channel, String sender, String login, String hostname) {
		if (sender.equals(bot.getNick())) {
			// Don't react to own joins
			return;
		}

		if (welcomeMessages != null) {
			for (String welcomeMessage : welcomeMessages) {
				welcomeMessage = welcomeMessage.replace("{botname}", bot.getNick());
				welcomeMessage = welcomeMessage.replace("{channel}", channel);
				welcomeMessage = welcomeMessage.replace("{helptrigger}", helpTrigger);
				bot.sendNotice(sender, welcomeMessage);
			}
		}
	}
}
