package org.jibble.pircbot.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jibble.pircbot.ExtendedPircBot;
import org.jibble.pircbot.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BadWordsPircModule extends AbstractPircModule {
	private static final Logger LOGGER = LoggerFactory.getLogger(BadWordsPircModule.class);

	private static final List<String> BADWORDS = new ArrayList<String>();

	private String kickReason;
	
	public BadWordsPircModule(String badwordsPath, String encoding, String kickReason) {
		this.kickReason = kickReason;
		loadChuckNorris(badwordsPath, encoding);
	}
	
	@Override
	public void onMessage(ExtendedPircBot bot, String channel, String sender, String login, String hostname,
			String message) {
		if (sender.equals(bot.getNick())) {
			// Avoid self kick
			return;
		}
		
		User[] users = bot.getUsers(channel);
		for (User user : users) {
			if (user.getNick().equals(sender) && user.isOp()) {
				// Avoid kicking admins
				return;
			}
		}
		
		String lowerMessage = " " + message.toLowerCase() + " ";
		for (String badword : BADWORDS) {
			if (lowerMessage.contains(badword)) {
				bot.kick(channel, sender, kickReason);
			}
		}
	}
	
	private void loadChuckNorris(String badwordsPath, String encoding) {
		InputStream input = ClassLoader.getSystemResourceAsStream(badwordsPath);
		try {
			Reader reader = new InputStreamReader(input, encoding);
			BufferedReader bufferedReader = new BufferedReader(reader);
			
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				// trim afterwards to avoid NPE
				line = line.trim();
				if (StringUtils.isNotBlank(line) && !line.startsWith("#")) {
					// Put everything small case for easier comparison
					BADWORDS.add(" " + line.toLowerCase() + " ");
				}
			}
		} catch (IOException ioe) {
			LOGGER.error("Could not read badwords file, ignoring", ioe);
		} finally {
			try {
				input.close();
			} catch (IOException ioe) {
				LOGGER.warn("Could not close badwords input stream", ioe);
			}
		}
	}
}
