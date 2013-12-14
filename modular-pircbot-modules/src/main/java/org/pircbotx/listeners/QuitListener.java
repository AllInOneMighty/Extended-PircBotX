package org.pircbotx.listeners;

import static com.google.common.base.Preconditions.checkArgument;

import org.pircbotx.ExtendedPircBotX;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Makes the bot quit when an admin sends a custom message in private chat to the bot. The quit
 * message of the bot can be customized.
 *
 * @author Emmanuel Cron
 */
public class QuitListener extends ListenerAdapter<PircBotX> implements PrivateListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(QuitListener.class);

  private String trigger;

  private String helpMessage;

  private String quitMessage;

  /**
   * Creates a new quit listener.
   *
   * @param trigger the message that an admin needs to send to the bot to make it quit
   * @param quitMessage the message displayed to all users when the bot quits; this message can be
   *        {@code null} or empty if you do not want a quit message
   */
  public QuitListener(String trigger, String quitMessage) {
    checkArgument(!Strings.isNullOrEmpty(trigger));

    this.trigger = trigger;
    this.quitMessage = quitMessage;
  }

  @Override
  public String getPrivateTriggerMessage() {
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
  public boolean isOpRequired() {
    return true;
  }

  @Override
  public void onTriggerPrivateMessage(PrivateMessageEvent<ExtendedPircBotX> event) {
    LOGGER.info("Bot was requested to quit by {}", event.getUser().getNick());
    event.getBot().stopBotReconnect();

    if (!Strings.isNullOrEmpty(quitMessage)) {
      event.getBot().sendIRC().quitServer(quitMessage);
    } else {
      event.getBot().sendIRC().quitServer();
    }
  }
}
