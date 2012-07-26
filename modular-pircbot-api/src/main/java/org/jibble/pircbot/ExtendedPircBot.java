package org.jibble.pircbot;

import java.util.List;

import org.jibble.pircbot.PircBot;

public abstract class ExtendedPircBot extends PircBot {
	private boolean quitRequested;

	public abstract List<String> buildHelp(String nick, boolean inPrivate);
	
	public void setQuitRequested(boolean quitRequested) {
		this.quitRequested = quitRequested;
	}
	
	public boolean isQuitRequested() {
		return quitRequested;
	}
}
