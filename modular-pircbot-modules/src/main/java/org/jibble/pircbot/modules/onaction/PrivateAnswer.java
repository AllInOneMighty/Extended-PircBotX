package org.jibble.pircbot.modules.onaction;

public class PrivateAnswer extends Answer {
	public PrivateAnswer(String answer) {
		super(answer);
	}
	
	@Override
	public boolean isAction() {
		return false;
	}
}
