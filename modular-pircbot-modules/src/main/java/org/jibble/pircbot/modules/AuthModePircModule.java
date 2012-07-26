package org.jibble.pircbot.modules;

import org.jibble.pircbot.ExtendedPircBot;


public class AuthModePircModule extends AbstractPircModule {
	
	private String authUsername;
	
	private String authPassword;
	
	public AuthModePircModule(String authUsername, String authPassword) {
		this.authUsername = authUsername;
		this.authPassword = authPassword;
	}

	@Override
	public void onConnect(ExtendedPircBot bot) {
		bot.sendRawLineViaQueue("auth " + authUsername + " " + authPassword);
		bot.sendRawLineViaQueue("mode " + bot.getNick() + " +x");
	}
	
}
