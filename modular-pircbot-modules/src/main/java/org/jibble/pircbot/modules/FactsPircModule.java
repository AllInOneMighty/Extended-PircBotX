package org.jibble.pircbot.modules;

import java.util.List;
import java.util.Random;

import org.jibble.pircbot.ExtendedPircBot;
import org.jibble.pircbot.modules.facts.FactsReader;

public class FactsPircModule extends AbstractPircModule implements PublicPircModule {
	private static final Random RANDOM = new Random();

	private String trigger;
	
	private String helpMessage;
	
	private FactsReader factsReader;

	private List<List<String>> facts;

	public FactsPircModule(String trigger, FactsReader factsReader) {
		this.trigger = trigger;
		this.factsReader = factsReader;
		loadFacts();
	}
	
	@Override
	public String getTriggerMessage() {
		return trigger;
	}
	
	public void setHelp(String helpMessage) {
		this.helpMessage = helpMessage;
	}

	@Override
	public String getHelpText() {
		return helpMessage;
	}

	@Override
	public void onTriggerMessage(ExtendedPircBot bot, String channel, String sender, String login, String hostname) {
		if (facts != null) {
			int fact = RANDOM.nextInt(facts.size());
			for (String line : facts.get(fact)) {
				bot.sendMessage(channel, line);
			}
		}
	}
	
	// internal helpers
	
	private void loadFacts() {
		List<List<String>> factsRead = factsReader.readFacts();
		if (factsRead.size() > 0) {
			facts = factsRead;
		}
	}
}
