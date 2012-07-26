package org.jibble.pircbot.modules;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

import org.jibble.pircbot.ExtendedPircBot;
import org.jibble.pircbot.modules.onaction.Answer;
import org.jibble.pircbot.modules.onaction.DynamicOnActionPattern;
import org.jibble.pircbot.modules.onaction.OnActionAnswers;
import org.jibble.pircbot.modules.onaction.OnActionPattern;
import org.jibble.pircbot.modules.onaction.OnActionPatternFactory;
import org.jibble.pircbot.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnActionPircModule extends AbstractPircModule {
	private static final Logger LOGGER = LoggerFactory.getLogger(OnActionPircModule.class);

	private static final Random RANDOM = new Random();
	
	private static final Map<OnActionPattern, OnActionAnswers> TRIGGER_ACTIONS =
			new HashMap<OnActionPattern, OnActionAnswers>();

	public OnActionPircModule(String actionsPath, String encoding) {
		loadActions(actionsPath, encoding);
	}

	@Override
	public void onConnect(ExtendedPircBot bot) {
		// We now know the name of the bot, update dynamic patterns
		updateDynamicOnActionPatterns(bot.getNick());
	}

	@Override
	public void onNickChange(ExtendedPircBot bot, String oldNick, String login, String hostname, String newNick) {
		if (newNick.equals(bot.getNick())) {
			// Bot just changed nick, updating all dynamic patterns
			updateDynamicOnActionPatterns(bot.getNick());
		}
	}

	@Override
	public void
			onAction(ExtendedPircBot bot, String sender, String login, String hostname, String target, String action) {
		// Check if action can trigger a reply
		for (Entry<OnActionPattern, OnActionAnswers> entry : TRIGGER_ACTIONS.entrySet()) {
			if (entry.getKey().matches(action)) {
				// Found one!
				OnActionAnswers onActionAnswers = entry.getValue();
				
				// Should we display it this time?
				if (RANDOM.nextInt(100) < onActionAnswers.getProbability()) {
					List<Answer> possibleAnswers = onActionAnswers.getPossibleAnswers();
					Answer answer = possibleAnswers.get(RANDOM.nextInt(possibleAnswers.size()));
					
					String line = answer.buildAnswer(sender);
					
					if (answer.isAction()) {
						bot.sendCTCPCommand(target, "ACTION " + line);
					} else {
						bot.sendRawLine("PRIVMSG " + target + " :" + line);
					}
				}
			}
		}
	}
	
	// internal helpers
	
	private void updateDynamicOnActionPatterns(String botname) {
		for (OnActionPattern onActionPattern : TRIGGER_ACTIONS.keySet()) {
			if (onActionPattern instanceof DynamicOnActionPattern) {
				((DynamicOnActionPattern) onActionPattern).updatePattern(botname);
			}
		}
	}

	private void loadActions(String actionsPath, String encoding) {
		InputStream input = ClassLoader.getSystemResourceAsStream(actionsPath);
		Properties properties = new Properties();
		try {
			Reader reader = new InputStreamReader(input, encoding);
			properties.load(reader);
		} catch (IOException ioe) {
			LOGGER.error("Could not load actions file, they will not be available", ioe);
			return;
		} finally {
			try {
				input.close();
			} catch (IOException ioe) {
				LOGGER.warn("Could not close actions input stream", ioe);
			}
		}
		
		List<String> processedProperties = new ArrayList<String>();

		for (String rawProperty : properties.stringPropertyNames()) {
			String property = rawProperty.substring(0, rawProperty.indexOf('.'));
			if (!processedProperties.contains(property)) {
				processedProperties.add(property);

				List<String> triggerMessages = PropertiesUtils.getStringList(property + ".triggermessages", properties);
				int probability = PropertiesUtils.getInt(properties.getProperty(property + ".probability"));
				List<String> possiblePrivateAnswers =
						PropertiesUtils.getStringList(property + ".possibleanswers.private", properties);
				List<String> possibleActionAnswers =
						PropertiesUtils.getStringList(property + ".possibleanswers.action", properties);
				
				OnActionAnswers onActionAnswers =
						new OnActionAnswers(probability, possiblePrivateAnswers, possibleActionAnswers);
				
				for (String triggerMessage : triggerMessages) {
					OnActionPattern pattern = OnActionPatternFactory.build(triggerMessage);
					TRIGGER_ACTIONS.put(pattern, onActionAnswers);
				}
			}
		}
	}
}
