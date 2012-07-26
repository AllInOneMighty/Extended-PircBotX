package org.jibble.pircbot.modules;

import org.jibble.pircbot.ExtendedPircBot;


public class JoinOnServerResponsePircModule extends AbstractPircModule {
	private int triggerCode;
	
	private String channel;
	
	public JoinOnServerResponsePircModule(int triggerCode, String channel) {
		this.triggerCode = triggerCode;
		this.channel = channel;
	}

	@Override
	public void onServerResponse(ExtendedPircBot bot, int code, String response) {
		if (code == triggerCode) {
			bot.joinChannel(channel);
		}
	}
}
