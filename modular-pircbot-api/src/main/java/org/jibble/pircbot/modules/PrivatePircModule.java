package org.jibble.pircbot.modules;

import org.jibble.pircbot.ExtendedPircBot;

public interface PrivatePircModule {
	String getPrivateTriggerMessage();
	
	void onTriggerPrivateMessage(ExtendedPircBot bot, String sender, String login, String hostname, String message);
	
	boolean isOpRequired();
}
