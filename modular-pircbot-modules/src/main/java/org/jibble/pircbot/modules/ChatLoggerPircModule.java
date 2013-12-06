package org.jibble.pircbot.modules;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jibble.pircbot.modules.ChatLoggerPircModule.ChatLoggerEvent.DISCONNECT;
import static org.jibble.pircbot.modules.ChatLoggerPircModule.ChatLoggerEvent.JOIN;
import static org.jibble.pircbot.modules.ChatLoggerPircModule.ChatLoggerEvent.KICK;
import static org.jibble.pircbot.modules.ChatLoggerPircModule.ChatLoggerEvent.KICK_YOU;
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

import com.google.common.base.Strings;

/**
 * A module that logs everything that the bot sees (or almost...) into log files on the machine it
 * is running. Files are automatically renamed each day to keep one file name per day.
 * <p>
 * Chat events (= things the bot sees) all have a default log format that can be overridden if
 * needed. All supported events are described in the {@link ChatLoggerEvent} enumeration.
 * <p>
 * If you want to override a chat event log format, use
 * {@link #setEventFormat(ChatLoggerEvent, String)}. If you want to add timestamps to your log
 * files, use {@link #setTimestampFormat(String)}.
 * 
 * @author Emmanuel Cron
 */
public class ChatLoggerPircModule extends AbstractStoppablePircModule {
  private static final Logger LOGGER = LoggerFactory.getLogger(ChatLoggerPircModule.class);

  /**
   * List of chat messages that are supported by the chat logger module.
   * <p>
   * Each chat message has a default log format that may be overridden by a custom value.
   * 
   * @author Emmanuel Cron
   */
  public enum ChatLoggerEvent {
    /**
     * When the topic is sent to the bot.
     * 
     * <pre>
     * Topic is {topic}
     * </pre>
     */
    TOPIC("* Topic is '%s'"),
    /**
     * When the information about whom sent the last topic is sent to the bot.
     * 
     * <pre>
     * Set by {nick} on {date}
     * </pre>
     */
    TOPIC_SET_BY("* Set by %s on %s"),
    /**
     * When someone changes the topic of the channel.
     * 
     * <pre>
     * {nick} changes topic to {newtopic}
     * </pre>
     */
    TOPIC_CHANGED("* %s changes topic to '%s'"),
    /**
     * When someone says something on the channel.
     * 
     * <pre>
     * &lt;{prefix}{nick}&gt; {message}
     * </pre>
     */
    MESSAGE("<%s%s> %s"),
    /**
     * When someone joins the channel.
     * 
     * <pre>
     * {nick} ({login}@{host} has joined {channel}
     * </pre>
     */
    JOIN("* %s (%s@%s) has joined %s"),
    /**
     * When someone leaves the channel.
     * 
     * <pre>
     * {prefix}{nick} ({login}@{host} has left {channel}
     * </pre>
     */
    PART("* %s%s (%s@%s) has left %s"),
    /**
     * When someone is kicked from a channel.
     * 
     * <pre>
     * * %s was kicked by %s (%s)
     * </pre>
     */
    KICK("* %s was kicked by %s (%s)"),
    /**
     * When the bot is kicked from a channel.
     * 
     * <pre>
     * * You were kicked from %s by %s (%s)
     * </pre>
     */
    KICK_YOU("* You were kicked from %s by %s (%s)"),
    /**
     * When the bot is disconnected from the server.
     * 
     * <pre>
     * * Disconnected
     * </pre>
     */
    DISCONNECT("* Disconnected"),
    /**
     * When someone quits the server.
     * 
     * <pre>
     * {prefix}{nick} ({login}@{host} Quit ({quitmessage})
     * </pre>
     */
    QUIT("* %s%s (%s@%s) Quit (%s)"),
    /**
     * When the channel modes (moderated, private, ...) are changed.
     * 
     * <pre>
     * {nick} sets mode: {modes}
     * </pre>
     */
    MODE("* %s sets mode: %s"),
    /**
     * When user modes (op, voice, ...) are changed.
     * 
     * <pre>
     * {nick} sets mode: {nick} {modes}
     * </pre>
     */
    USER_MODE("* %s sets mode: %s %s");

    private String defaultFormat;

    private int requiredReplacements;

    private ChatLoggerEvent(String defaultFormat) {
      this.defaultFormat = defaultFormat;
      this.requiredReplacements = 0;

      int searchIndex = 0;
      while ((searchIndex = defaultFormat.indexOf("%s", searchIndex)) != -1) {
        requiredReplacements++;
        // The string is 2 chars long
        searchIndex += 2;
      }
    }

    /**
     * Number of replacement strings ({@code %s}) expected by this chat event.
     * 
     * @return the number of required replacement strings in the format of this chat event
     */
    public int getRequiredReplacements() {
      return requiredReplacements;
    }

    /**
     * The default log format of this chat event.
     * 
     * @return the default log format
     */
    public String getDefaultFormat() {
      return defaultFormat;
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

  /**
   * Creates a new chat logger module.
   * 
   * @param logsPath the folder where to store the chat log files
   * @param charset the charset to use when writing in the log files
   */
  public ChatLoggerPircModule(String logsPath, String charset) {
    checkArgument(!Strings.isNullOrEmpty(logsPath), "No chat logs path specified");

    this.logsPath = logsPath;
    this.charset = checkNotNull(charset);
  }

  /**
   * Sets the format to use for one of the {@link ChatLoggerEvent}s supported by this module. The
   * given format must contain at least the number of required string replacements of the event
   * (specified by {@link ChatLoggerEvent#getRequiredReplacements()}).
   * <p>
   * The exact data used to replace these string replacements depends on the event logged. They have
   * been hugely inspired by the way the <a href="http://www.mirc.com/">mIRC</a> IRC client displays
   * them.
   * <p>
   * Each event provides a default format that should fit for most situations. However, you can use
   * this method to override this format. You may also use it to disable a particular event by
   * specifying an empty or <tt>null</tt> format.
   * <p>
   * You may get all default formats by calling {@link ChatLoggerEvent#getDefaultFormat()} on each
   * chat event.
   * 
   * @param event the event for which set a new format
   * @param format the format; it may be empty or <tt>null</tt> if you wish to disable logging for
   *        this event
   */
  public void setEventFormat(ChatLoggerEvent event, String format) {
    checkNotNull(event);

    if (StringUtils.isBlank(format)) {
      eventFormats.put(event, null);
      return;
    }

    int countMatches = StringUtils.countMatches(format, "%s");
    if (countMatches < event.getRequiredReplacements()) {
      LOGGER.warn("Number of replacements strings is fewer than the expected count,"
          + " some data may not be logged; event: {}, expected: {}, format: '{}'", new Object[] {
          event.name(), event.getRequiredReplacements(), format});
    } else if (countMatches > event.getRequiredReplacements()) {
      // Fail safe

      LOGGER.error("WILL USE DEFAULT LOG FORMAT - Number of replacements strings exceeds"
          + " expected count; event: {}, expected: {}, format: '{}'", new Object[] {event.name(),
          event.getRequiredReplacements(), format});
      // Not inserting anything in map to use default format
      return;
    }
    eventFormats.put(event, format);
  }

  /**
   * Sets the timestamp format to use in the chat logs. If the given pattern is empty (or i you
   * never call this method), no timestamp will be used in the logs.
   * <p>
   * Format must be compatible with a {@link SimpleDateFormat}.
   * 
   * @param pattern the date/time pattern to use for each message in the chat logs
   * 
   * @throws IllegalArgumentException if the pattern is invalid
   */
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
        LOGGER.error("Could not close chat log file writer, will be done when Java system exists",
            ioe);
      }
    }
  }

  @Override
  public void onTopic(ExtendedPircBot bot, String channel, String topic, String setBy, long date,
      boolean changed) {
    if (changed) {
      log(channel, TOPIC_CHANGED, setBy, topic);
    } else {
      String formattedDate =
          new SimpleDateFormat("EEE MMM dd HH:mm:ss", Locale.ENGLISH).format(new Date(date));
      log(channel, TOPIC, topic);
      log(channel, TOPIC_SET_BY, setBy, formattedDate);
    }
  }

  @Override
  public void onMessage(ExtendedPircBot bot, String channel, String sender, String login,
      String hostname, String message) {
    log(channel, MESSAGE, getUserPrefix(bot, channel, sender), sender, message);
  }

  @Override
  public void onJoin(ExtendedPircBot bot, String channel, String sender, String login,
      String hostname) {
    log(channel, JOIN, sender, login, hostname, channel);
  }

  @Override
  public void onPart(ExtendedPircBot bot, String channel, String sender, String login,
      String hostname) {
    log(channel, PART, getUserPrefix(bot, channel, sender), sender, login, hostname, channel);
  }

  @Override
  public void onDisconnect(ExtendedPircBot bot) {
    for (String channel : bot.getChannels()) {
      log(channel, DISCONNECT);
    }
  }

  @Override
  public void onQuit(ExtendedPircBot bot, String sourceNick, String sourceLogin,
      String sourceHostname, String reason) {
    for (String channel : bot.getChannels()) {
      log(channel, QUIT, getUserPrefix(bot, channel, sourceNick), sourceNick, sourceLogin,
          sourceHostname, reason);
    }
  }

  @Override
  public void onMode(ExtendedPircBot bot, String channel, String sourceNick, String sourceLogin,
      String sourceHostname, String mode) {
    log(channel, MODE, sourceNick, mode);
  }

  @Override
  public void onUserMode(ExtendedPircBot bot, String targetNick, String sourceNick,
      String sourceLogin, String sourceHostname, String mode) {
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

  @Override
  public void onKick(ExtendedPircBot bot, String channel, String kickerNick, String kickerLogin,
      String kickerHostname, String recipientNick, String reason) {
    if (bot.getNick().equals(recipientNick)) {
      log(channel, KICK_YOU, channel, kickerNick, reason);
    } else {
      log(channel, KICK, recipientNick, kickerNick, reason);
    }
  }

  // internal helpers

  private synchronized void log(String channel, ChatLoggerEvent event, Object... args) {
    if (!checkedFormats) {
      for (ChatLoggerEvent checkEvent : ChatLoggerEvent.values()) {
        if (eventFormats.containsKey(checkEvent) && eventFormats.get(checkEvent) == null) {
          LOGGER
              .info("Format of event {} has been forced to nothing; it will not be logged", event);
        }
      }
      checkedFormats = true;
    }

    // Get custom format (may be blank) or default if not set
    String format =
        eventFormats.containsKey(event) ? eventFormats.get(event) : event.getDefaultFormat();

    // Cannot be blank here, check is done in setEventFormat()
    if (format == null) {
      // Means a key was found but it was set to nothing (= wishing not to
      // log these events)
      return;
    }

    String message = String.format(format, args);
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
