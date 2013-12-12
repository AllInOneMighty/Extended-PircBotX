package org.jibble.pircbot.modules;

import static com.google.common.base.Preconditions.checkArgument;

import org.jibble.pircbot.ExtendedPircBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Makes the bot quit when an admin sends a custom message in private chat to the bot. The quit
 * message of the bot can be customized.
 * 
 * @author Emmanuel Cron
 */
public class QuitPircModule extends AbstractPircModule implements PrivatePircModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(QuitPircModule.class);

  private String trigger;

  private String helpMessage;

  private String quitMessage;

  /**
   * Creates a new quit module.
   * 
   * @param trigger the message that an admin needs to send to the bot to make it quit
   * @param quitMessage the message displayed to all users when the bot quits; this message can be
   *        {@code null} or empty if you do not want a quit message
   */
  public QuitPircModule(String trigger, String quitMessage) {
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
  public void onTriggerPrivateMessage(ExtendedPircBot bot, String sender, String login,
      String hostname) {
    LOGGER.info("Bot was requested to quit by {}", sender);
    bot.setQuitRequested(true);

    if (!Strings.isNullOrEmpty(quitMessage)) {
      bot.quitServer(quitMessage);
    } else {
      bot.quitServer();
    }
  }
}
