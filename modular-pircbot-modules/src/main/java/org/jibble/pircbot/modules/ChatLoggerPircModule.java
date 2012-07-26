package org.jibble.pircbot.modules;

import static org.jibble.pircbot.modules.ChatLoggerPircModule.ChatLoggerEvent.JOIN;
import static org.jibble.pircbot.modules.ChatLoggerPircModule.ChatLoggerEvent.MESSAGE;
import static org.jibble.pircbot.modules.ChatLoggerPircModule.ChatLoggerEvent.MODE;
import static org.jibble.pircbot.modules.ChatLoggerPircModule.ChatLoggerEvent.PART;
import static org.jibble.pircbot.modules.ChatLoggerPircModule.ChatLoggerEvent.QUIT;
import static org.jibble.pircbot.modules.ChatLoggerPircModule.ChatLoggerEvent.TOPIC;
import static org.jibble.pircbot.modules.ChatLoggerPircModule.ChatLoggerEvent.TOPIC_CHANGED;
import static org.jibble.pircbot.modules.ChatLoggerPircModule.ChatLoggerEvent.TOPIC_SET_BY;
import static org.jibble.pircbot.modules.ChatLoggerPircModule.ChatLoggerEvent.USER_MODE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jibble.pircbot.ExtendedPircBot;
import org.jibble.pircbot.User;
import org.joda.time.DateMidnight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatLoggerPircModule extends AbstractStoppablePircModule {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatLoggerPircModule.class);

	public enum ChatLoggerEvent {
		TOPIC(1),
		TOPIC_SET_BY(2),
		TOPIC_CHANGED(2),
		MESSAGE(3),
		JOIN(4),
		PART(5),
		QUIT(5),
		MODE(2),
		USER_MODE(3);
		
		private int requiredReplacements;
		
		private ChatLoggerEvent(int replacementsCount) {
			this.requiredReplacements = replacementsCount;
		}
		
		public int getRequiredReplacements() {
			return requiredReplacements;
		}
	}

	private Map<ChatLoggerEvent, String> eventFormats = new HashMap<ChatLoggerEvent, String>();

	private String logsPath;

	private DateMidnight logFileDate;
	
	private File logFile;
	
	private BufferedWriter logFileWriter;
	
	private String charset;
	
	private FastDateFormat timestampFormat;
	
	private boolean checkedFormats;

	public ChatLoggerPircModule(String logsPath, String charset) {
		this.logsPath = logsPath;
		this.charset = charset;
	}
	
	public void setEventFormat(ChatLoggerEvent event, String format) {
		int countMatches = StringUtils.countMatches(format, "%s");
		if (countMatches < event.getRequiredReplacements()) {
			LOGGER.warn("Number of replacements strings is fewer than the expected count, some data may not be logged; event: "
					+ event.name() + ", expected: " + event.getRequiredReplacements() + ", format: '" + format + "'");
		} else if (countMatches > event.getRequiredReplacements()) {
			LOGGER.error("EVENT WILL NOT BE LOGGED - Number of replacements strings exceeds expected count; event: "
					+ event.name() + ", expected: " + event.getRequiredReplacements() + ", format: '" + format + "'");
			// Avoiding later exceptions when calling format()
			return;
		}
		eventFormats.put(event, format);
	}

	public void setTimestampFormat(String pattern) {
		if (StringUtils.isBlank(pattern)) {
			timestampFormat = null;
		} else {
			timestampFormat = FastDateFormat.getInstance(pattern);
		}
	}

	@Override
	public void stop() {
		if (logFileWriter != null) {
			try {
				logFileWriter.close();
			} catch (IOException ioe) {
				LOGGER.error("Could not close chat log file writer, will be done when Java system exists", ioe);
			}
		}
	}
	
	@Override
	public void onTopic(ExtendedPircBot bot, String channel, String topic, String setBy, long date, boolean changed) {
		if (changed) {
			log(channel, TOPIC_CHANGED, setBy, topic);
		} else {
			String formattedDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss", Locale.ENGLISH).format(new Date(date));
			log(channel, TOPIC, topic);
			log(channel, TOPIC_SET_BY, setBy, formattedDate);
		}
	}

	@Override
	public void onMessage(ExtendedPircBot bot, String channel, String sender, String login, String hostname,
			String message) {
		log(channel, MESSAGE, getUserPrefix(bot, channel, sender), sender, message);
	}
	
	@Override
	public void onJoin(ExtendedPircBot bot, String channel, String sender, String login, String hostname) {
		log(channel, JOIN, sender, login, hostname, channel);
	}
	
	@Override
	public void onPart(ExtendedPircBot bot, String channel, String sender, String login, String hostname) {
		log(channel, PART, getUserPrefix(bot, channel, sender), sender, login, hostname, channel);
	}
	
	@Override
	public void
			onQuit(ExtendedPircBot bot, String sourceNick, String sourceLogin, String sourceHostname, String reason) {
		for (String channel : bot.getChannels()) {
			log(channel, QUIT, getUserPrefix(bot, channel, sourceNick), sourceNick, sourceLogin, sourceHostname, reason);
		}
	}
	
	@Override
	public void onMode(ExtendedPircBot bot, String channel, String sourceNick, String sourceLogin,
			String sourceHostname,
			String mode) {
		log(channel, MODE, sourceNick, mode);
	}
	
	@Override
	public void onUserMode(ExtendedPircBot bot, String targetNick, String sourceNick, String sourceLogin,
			String sourceHostname, String mode) {
		for (String channel : bot.getChannels()) {
			User[] users = bot.getUsers(channel);
			for (User user : users) {
				if (user.getNick().equals(targetNick)) {
					log(channel, USER_MODE, sourceNick, mode, targetNick);
					break;
				}
			}
		}
	}
	
	// internal helpers

	private synchronized void log(String channel, ChatLoggerEvent event, Object... args) {
		if (!checkedFormats) {
			for (ChatLoggerEvent IEvent : ChatLoggerEvent.values()) {
				if (StringUtils.isBlank(eventFormats.get(IEvent))) {
					LOGGER.warn("No format defined for event " + event + ", they will not be logged");
				}
			}
			checkedFormats = true;
		}

		if (!eventFormats.containsKey(event)) {
			return;
		}

		String message = String.format(eventFormats.get(event), args);
		if (timestampFormat != null) {
			message = "[" + timestampFormat.format(System.currentTimeMillis()) + "] " + message;
		}

		// Create new file when date changes or if none exist
		if (logFileDate == null || new DateMidnight().isAfter(logFileDate)) {
			// First close old writer if it exists
			if (logFileWriter != null) {
				try {
					logFileWriter.close();
				} catch (IOException ioe) {
					LOGGER.warn("Could not close writer to previous chat log file", ioe);
				}
			}

			logFileDate = new DateMidnight();
			String logFileDateStr = new SimpleDateFormat("yyyy_MM_dd").format(logFileDate.toDate());
			logFile = new File(logsPath, channel.toLowerCase() + "-" + logFileDateStr + ".log");
			
			try {
				OutputStream outputStream = new FileOutputStream(logFile, true);
				Writer writer = new OutputStreamWriter(outputStream, charset);
				logFileWriter = new BufferedWriter(writer);
			} catch (IOException ioe) {
				LOGGER.error("LOGGING WILL BE DISABLED: Could not create writer to chat log file", ioe);
			}
		}
		
		// Now log it, baby!
		try {
			if (logFile.length() > 0L) {
				logFileWriter.append("\n");
			}
			logFileWriter.append(message);
			logFileWriter.flush();
		} catch (IOException ioe) {
			LOGGER.error("Could not write message to chat log file: " + message, ioe);
		}
	}
	
	private String getUserPrefix(ExtendedPircBot bot, String channel, String sender) {
		User[] users = bot.getUsers(channel);
		for (User user : users) {
			if (user.getNick().equals(sender)) {
				return user.getPrefix();
			}
		}
		return "";
	}
}