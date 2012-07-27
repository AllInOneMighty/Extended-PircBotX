package org.jibble.pircbot.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.jibble.pircbot.ExtendedPircBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChuckNorrisPircModule extends AbstractPircModule implements PublicPircModule {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChuckNorrisPircModule.class);

	private static final Random RANDOM = new Random();

	private String trigger;
	
	private String helpMessage;

	private List<List<String>> fortunes;

	public ChuckNorrisPircModule(String trigger, String fortunesPath, String encoding) {
		this.trigger = trigger;
		loadFortunes(fortunesPath, encoding);
	}
	
	@Override
	public String getTriggerMessage() {
		return trigger;
	}
	
	public void setHelp(String helpMessage) {
		this.helpMessage = helpMessage;
	}

	@Override
	public String getHelp() {
		return helpMessage;
	}

	@Override
	public void onTriggerMessage(ExtendedPircBot bot, String channel, String sender, String login, String hostname) {
		if (fortunes != null) {
			int fortune = RANDOM.nextInt(fortunes.size());
			for (String line : fortunes.get(fortune)) {
				bot.sendMessage(channel, line);
			}
		}
	}
	
	private void loadFortunes(String fortunesPath, String encoding) {
		InputStream input = ClassLoader.getSystemResourceAsStream(fortunesPath);
		
		List<List<String>> tmpFortunes = new ArrayList<List<String>>();
		List<String> fortune = new ArrayList<String>();
		try {
			Reader reader = new InputStreamReader(input, encoding);
			BufferedReader bufferedReader = new BufferedReader(reader);

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				
				// Ignore blank lines
				if (StringUtils.isBlank(line)) {
					continue;
				}
				
				if ("%".equals(line) && fortune.size() > 0) {
					tmpFortunes.add(fortune);
					// Don't clear() here
					fortune = new ArrayList<String>();
				} else {
					fortune.add(line);
				}
			}
		} catch (IOException ioe) {
			LOGGER.error("Error while reading Chuck Norris file, will use what we have already read", ioe);
		} finally {
			try {
				input.close();
			} catch (IOException ioe) {
				LOGGER.error("Could not close Chuck Norris input stream reader", ioe);
			}
		}
		
		// Add last fortune
		if (fortune.size() > 0) {
			tmpFortunes.add(fortune);
		}
		
		// Update fortunes
		if (tmpFortunes.size() > 0) {
			fortunes = tmpFortunes;
		}
	}
}
