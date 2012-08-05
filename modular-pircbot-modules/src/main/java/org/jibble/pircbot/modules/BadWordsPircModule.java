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

/**
 * Loads a list of words that are forbidden on any public chat on which the bot
 * is connected. If any of these words is said by a user connected to these
 * channels, he/she is immediately kicked with a custom reason.
 * <p>
 * This module will not kick admins.
 * 
 * <h2>Bad words file format</h2>
 * Bad words must be written in a file containing one bad word per line. If a
 * line starts with '<tt>#</tt>', the line is ignored.
 * <p>
 * Example:
 * 
 * <pre> # This is the bad words file
 * idiot
 * motherkisser
 * 
 * # Will be ignored
 * #stupid</pre>
 * 
 * @author Emmanuel Cron
 */
public class BadWordsPircModule extends AbstractPircModule {
	private static final Logger LOGGER = LoggerFactory.getLogger(BadWordsPircModule.class);

	private static final List<String> BADWORDS = new ArrayList<String>();

	private String kickReason;
	
	/**
	 * Creates a new badwords module.
	 * 
	 * @param badwordsPath path from the root of the claspath to the file
	 *        containing the bad words; each word must be on a new line
	 * @param encoding the encoding in which the file is stored
	 * @param kickReason the custom reason that will be displayed when a user is
	 *        kicked by the bot
	 */
	public BadWordsPircModule(String badwordsPath, String encoding, String kickReason) {
		this.kickReason = kickReason;
		loadBadWords(badwordsPath, encoding);
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
	
	// internal helpers

	private void loadBadWords(String badwordsPath, String encoding) {
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
