package org.jibble.pircbot.modules.onaction;

import java.util.ArrayList;
import java.util.List;

public class OnActionAnswers {
	private int probability;
	private List<Answer> possibleAnswers = new ArrayList<Answer>();
	
	public OnActionAnswers(int probability, List<String> possiblePrivateAnswers, List<String> possibleActionAnswers) {
		this.probability = probability;
		for (String possiblePrivateAnswer : possiblePrivateAnswers) {
			possibleAnswers.add(new PrivateAnswer(possiblePrivateAnswer));
		}
		for (String possibleActionAnswer : possibleActionAnswers) {
			possibleAnswers.add(new ActionAnswer(possibleActionAnswer));
		}
	}
	
	public int getProbability() {
		return probability;
	}
	
	public List<Answer> getPossibleAnswers() {
		return possibleAnswers;
	}
}