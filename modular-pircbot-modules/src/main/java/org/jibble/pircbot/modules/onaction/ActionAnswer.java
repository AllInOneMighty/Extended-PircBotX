package org.jibble.pircbot.modules.onaction;

public class ActionAnswer extends Answer {
	public ActionAnswer(String answer) {
		super(answer);
	}

	@Override
	public boolean isAction() {
		return true;
	}
}
