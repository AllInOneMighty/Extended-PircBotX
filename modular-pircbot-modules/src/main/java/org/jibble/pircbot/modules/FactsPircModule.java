package org.jibble.pircbot.modules;

import java.util.List;
import java.util.Random;

import org.jibble.pircbot.ExtendedPircBot;
import org.jibble.pircbot.modules.facts.FactsReader;

/**
 * Displays random facts on user request. This module loads a list of facts on
 * startup and then randomly chooses one of them and displays it on specific
 * triggers.
 * <p>
 * On instantiation, the developer needs to provide an implementation of
 * {@link FactsReader} in order to load the facts into memory. You should check
 * which readers are available in the
 * <tt>org.jibble.pircbot.modules.facts.*</tt> package before developing your
 * own.
 * <p>
 * This module can only be triggered on a public chat.
 * 
 * @author Emmanuel Cron
 */
public class FactsPircModule extends AbstractPircModule implements PublicPircModule {
	private static final Random RANDOM = new Random();

	private String trigger;
	
	private String helpMessage;
	
	private FactsReader factsReader;

	private List<List<String>> facts;

	/**
	 * Creates a new facts module.
	 * 
	 * @param trigger the word to say in a public chat to trigger the display of
	 *        a fact; note that this word must be prefixed by "<tt>!</tt>"
	 * @param factsReader a facts reader that will be used to load the facts
	 *        into memory at startup
	 */
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
