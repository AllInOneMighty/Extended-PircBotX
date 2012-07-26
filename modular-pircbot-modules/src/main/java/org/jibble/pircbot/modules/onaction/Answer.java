package org.jibble.pircbot.modules.onaction;

public abstract class Answer {
	
	private String answer;
	
	public Answer(String answer) {
		this.answer = answer;
	}

	public String buildAnswer(String sender) {
		return answer.replaceAll("\\{sender\\}", sender);
	}

	public abstract boolean isAction();
	
	@Override
	public String toString() {
		return answer;
	}
}